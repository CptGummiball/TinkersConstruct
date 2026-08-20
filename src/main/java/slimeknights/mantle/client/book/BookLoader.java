package slimeknights.mantle.client.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.ContentBlank;
import slimeknights.mantle.client.book.data.content.ContentCrafting;
import slimeknights.mantle.client.book.data.content.ContentImage;
import slimeknights.mantle.client.book.data.content.ContentImageText;
import slimeknights.mantle.client.book.data.content.ContentIndex;
import slimeknights.mantle.client.book.data.content.ContentListing;
import slimeknights.mantle.client.book.data.content.ContentPadding.ContentLeftPadding;
import slimeknights.mantle.client.book.data.content.ContentPadding.ContentRightPadding;
import slimeknights.mantle.client.book.data.content.ContentPageIconList;
import slimeknights.mantle.client.book.data.content.ContentShowcase;
import slimeknights.mantle.client.book.data.content.ContentStructure;
import slimeknights.mantle.client.book.data.content.ContentText;
import slimeknights.mantle.client.book.data.content.ContentTextImage;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.mantle.data.gson.ConditionSerializer;
import slimeknights.mantle.recipe.condition.ICondition;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry of books and of the page types they may use.
 *
 * <p>Books register at startup and load lazily the first time one is opened, because a book's
 * content depends on things that are not available at startup: the language, the recipe manager,
 * and the datapack-driven material and modifier registries.
 */
public final class BookLoader {
  private BookLoader() {}

  /** Registered books by id */
  private static final Map<ResourceLocation,BookData> BOOKS = new HashMap<>();
  /** Registered page content types by id */
  private static final Map<ResourceLocation,Class<? extends PageContent>> PAGE_TYPES = new HashMap<>();
  /** Adapters contributed by callers before the Gson instance is first used */
  private static final List<TypeAdapterEntry> EXTRA_ADAPTERS = new ArrayList<>();

  /**
   * Reads a component out of book JSON.
   *
   * <p>1.21 removed {@code Component.Serializer} as a Gson adapter, so the equivalent lives here
   * and is registered by default rather than by each mod that ships a book.
   */
  public static final Object COMPONENT_ADAPTER = new ComponentAdapter();

  /** Gson used for every book file. Declared after the adapters it installs, since static
   *  initialisers run in source order and a null adapter is rejected outright */
  public static Gson GSON = buildGson();


  static {
    registerBuiltinPageTypes();
  }

  /* Registration */

  /**
   * Registers a book.
   * @param id                  Book id, also the folder its files live in
   * @param appendIndex         Adds the section index page automatically
   * @param appendContentTable  Adds the padding pass automatically
   */
  public static BookData registerBook(ResourceLocation id, boolean appendIndex, boolean appendContentTable) {
    BookData book = new BookData();
    book.id = id;
    if (appendIndex) {
      book.addTransformer(BookTransformer.indexTranformer());
    }
    if (appendContentTable) {
      book.addTransformer(BookTransformer.paddingTransformer());
    }
    BOOKS.put(id, book);
    return book;
  }

  /** Registers a book that already exists, used by books that subclass {@link BookData} */
  public static BookData registerBook(ResourceLocation id, BookData book) {
    book.id = id;
    BOOKS.put(id, book);
    return book;
  }

  /** Book registered under the given id, or null */
  @Nullable
  public static BookData getBook(ResourceLocation id) {
    return BOOKS.get(id);
  }

  /** Registers a page content type under the id its JSON names */
  public static void registerPageType(ResourceLocation id, Class<? extends PageContent> clazz) {
    Class<? extends PageContent> existing = PAGE_TYPES.put(id, clazz);
    if (existing != null && existing != clazz) {
      Mantle.logger.warn("Book page type {} replaced {} with {}", id, existing.getSimpleName(), clazz.getSimpleName());
    }
  }

  /** Content class for a page type id, or null if nothing registered it */
  @Nullable
  public static Class<? extends PageContent> getPageType(ResourceLocation id) {
    return PAGE_TYPES.get(id);
  }

  /** Adds a Gson adapter used when reading book files */
  public static void registerGsonTypeAdapter(Type type, Object adapter) {
    EXTRA_ADAPTERS.add(new TypeAdapterEntry(type, adapter));
    GSON = buildGson();
  }

  /** The page types Mantle itself provides */
  private static void registerBuiltinPageTypes() {
    registerPageType(ContentBlank.ID, ContentBlank.class);
    registerPageType(ContentText.ID, ContentText.class);
    registerPageType(ContentImage.ID, ContentImage.class);
    registerPageType(ContentImageText.ID, ContentImageText.class);
    registerPageType(ContentTextImage.ID, ContentTextImage.class);
    registerPageType(ContentShowcase.ID, ContentShowcase.class);
    registerPageType(ContentCrafting.ID, ContentCrafting.class);
    registerPageType(ContentIndex.ID, ContentIndex.class);
    registerPageType(ContentListing.ID, ContentListing.class);
    registerPageType(ContentStructure.ID, ContentStructure.class);
    registerPageType(ContentPageIconList.ID, ContentPageIconList.class);
    registerPageType(ContentLeftPadding.ID, ContentLeftPadding.class);
    registerPageType(ContentRightPadding.ID, ContentRightPadding.class);
  }

  /* Reloading */

  /**
   * Hooks up the two events that invalidate a loaded book.
   *
   * <p>A book is built from both sides of the game: its pages and language come from resource
   * packs, but its content — which tools, modifiers and materials exist — comes from the server's
   * datapacks. Either changing has to drop the cached book, or the reader is shown a book
   * describing a game that is no longer running.
   */
  public static void init() {
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
      @Override
      public ResourceLocation getFabricId() {
        return Mantle.getResource("books");
      }

      @Override
      public void onResourceManagerReload(ResourceManager manager) {
        resetBooks();
      }
    });
    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> resetBooks());
    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> resetBooks());
  }

  /** Drops every book's content so the next open rebuilds it */
  public static void resetBooks() {
    BOOKS.values().forEach(BookData::reset);
  }

  /* Gson */

  private static Gson buildGson() {
    GsonBuilder builder = new GsonBuilder()
      .setLenient()
      .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
      .registerTypeAdapter(ItemStackData.class, new ItemStackData.Deserializer())
      .registerTypeAdapter(ICondition.class, ConditionSerializer.INSTANCE)
      .registerTypeAdapter(Component.class, COMPONENT_ADAPTER)
      .registerTypeAdapter(int.class, ColorAdapter.INSTANCE)
      .registerTypeAdapter(Integer.class, ColorAdapter.INSTANCE);
    for (TypeAdapterEntry entry : EXTRA_ADAPTERS) {
      builder.registerTypeAdapter(entry.type(), entry.adapter());
    }
    return builder.create();
  }

  /** Adapter pairing a type with the object handling it */
  private record TypeAdapterEntry(Type type, Object adapter) {}

  /** Reads and writes resource locations as plain strings */
  private static class ResourceLocationAdapter implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
    @Override
    public ResourceLocation deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
      ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
      if (location == null) {
        throw new JsonParseException("Invalid resource location " + element);
      }
      return location;
    }

    @Override
    public JsonElement serialize(ResourceLocation location, Type type, JsonSerializationContext context) {
      return new JsonPrimitive(location.toString());
    }
  }

  /**
   * Reads integers, accepting the unquoted hex literals the shipped appearance files use.
   *
   * <p>Gson in lenient mode hands {@code 0xE5C682} over as a string rather than a number, and the
   * default integer adapter throws on it. Books have been written that way since 1.12, so the
   * adapter meets the data where it is.
   */
  private static class ColorAdapter implements JsonDeserializer<Integer>, JsonSerializer<Integer> {
    static final ColorAdapter INSTANCE = new ColorAdapter();

    @Override
    public Integer deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
      JsonPrimitive primitive = element.getAsJsonPrimitive();
      if (primitive.isNumber()) {
        return primitive.getAsInt();
      }
      String text = primitive.getAsString().trim();
      try {
        if (text.startsWith("0x") || text.startsWith("0X")) {
          return (int)Long.parseLong(text.substring(2), 16);
        }
        if (text.startsWith("#")) {
          return (int)Long.parseLong(text.substring(1), 16);
        }
        return (int)Long.parseLong(text);
      } catch (NumberFormatException e) {
        throw new JsonParseException("Invalid integer " + text, e);
      }
    }

    @Override
    public JsonElement serialize(Integer value, Type type, JsonSerializationContext context) {
      return new JsonPrimitive(value);
    }
  }

  /** Component adapter replacing the one 1.21 removed */
  private static class ComponentAdapter implements JsonDeserializer<Component>, JsonSerializer<Component> {
    @Override
    public Component deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
      return Component.Serializer.fromJson(element, registries());
    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext context) {
      return new JsonPrimitive(Component.Serializer.toJson(component, registries()));
    }

    /** Registries the client is connected with; components may name datapack registry entries */
    private static RegistryAccess registries() {
      RegistryAccess access = SafeClientAccess.getRegistryAccess();
      return access == null ? RegistryAccess.EMPTY : access;
    }
  }
}
