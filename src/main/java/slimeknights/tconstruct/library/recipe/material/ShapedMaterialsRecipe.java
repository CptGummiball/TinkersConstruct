package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.LoadableCodec;
import slimeknights.mantle.data.loadable.common.ItemStackLoadable;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Shaped recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient} and
 * {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the materials of the result.
 */
public class ShapedMaterialsRecipe extends ShapedRecipe implements MaterialsCraftingTableRecipe {
  /** List of tool parts to search for in the final recipe */
  @Getter
  private final List<Ingredient> parts;
  /**
   * If true, a part may show up multiple times in the inputs, and all copies should match.
   * If false, only the first instance of a part is checked for each input, allowing a tool with the same part multiple times.
   */
  private final boolean checkRepeats;
  /** List of additional materials to add beyond the parts */
  @Getter
  private final List<MaterialVariantId> extraMaterials;
  public ShapedMaterialsRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, List<Ingredient> parts, List<MaterialVariantId> extraMaterials) {
    super(group, category, pattern, result, showNotification);
    this.parts = parts;
    // repeats are detected by instance: 1.21 gave Ingredient an equals comparing the vanilla
    // value array, under which all Fabric custom ingredients (empty array) alias each other
    Set<Ingredient> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
    distinct.addAll(parts);
    this.checkRepeats = distinct.size() == parts.size();
    this.extraMaterials = extraMaterials;
  }

  @Override
  public int getPartCount() {
    return parts.size();
  }

  /**
   * Finds materials for each of the parts
   * @return Array of all matched materials. Array will have no null entries, though the array may be null if no match was found.
   */
  @Nullable
  static MaterialVariantId[] findMaterials(CraftingInput inventory, List<Ingredient> parts, int partCount, boolean checkRepeats) {
    // want one material for each
    MaterialVariantId[] materials = new MaterialVariantId[partCount];
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        for (int p = 0; p < partCount; p++) {
          MaterialVariantId current = materials[p];
          // if we have not found the material yet, or repeats are considered the same material, test the ingredient
          if ((current == null || checkRepeats) && parts.get(p).test(stack)) {
            MaterialVariantId matched;
            if (stack.getItem() instanceof IMaterialItem materialItem) {
              matched = materialItem.getMaterial(stack);
            } else {
              matched = MaterialRecipeCache.findRecipe(stack).getMaterial().getVariant();
            }
            // first occurrence? thats our material
            if (current == null) {
              materials[p] = matched;
              break;
            } else if (!current.matchesVariant(matched)) {
              // if same material but different variants, just discard the variant
              if (current.getId().equals(matched.getId())) {
                materials[p] = current.getId();
                break;
              } else {
                // if different materials, no match
                return null;
              }
            }
          }
        }
      }
    }
    // ensure we found all materials needed
    for (int p = 0; p < partCount; p++) {
      if (materials[p] == null) {
        return null;
      }
    }
    return materials;
  }

  @Override
  public boolean matches(CraftingInput inventory, Level level) {
    if (!super.matches(inventory, level)) {
      return false;
    }
    // ensure all part materials matched and we found all parts
    return findMaterials(inventory, parts, parts.size(), checkRepeats) != null;
  }

  /** Common logic to this and {@link ShapedMaterialsRecipe} */
  public static void setMaterial(ItemStack stack, MaterialVariantId material, List<MaterialVariantId> extraMaterials) {
    if (extraMaterials.isEmpty() && stack.getItem() instanceof IMaterialItem materialItem) {
      materialItem.setMaterial(stack, material);
    } else {
      MaterialNBT.Builder builder = MaterialNBT.builder();
      builder.add(material);
      for (MaterialVariantId extraMaterial : extraMaterials) {
        builder.add(extraMaterial);
      }
      ToolStack.from(stack).setMaterials(builder.build());
    }
  }

  /** Sets the material for the given stack */
  @Override
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    setMaterial(stack, material, extraMaterials);
  }

  /** Assembles the item with material information */
  static ItemStack assemble(ItemStack stack, CraftingInput inventory, List<Ingredient> parts, int partCount, boolean checkRepeats, List<MaterialVariantId> extraMaterials) {
    MaterialVariantId[] materials = findMaterials(inventory, parts, partCount, checkRepeats);
    if (materials != null) {
      // if the result is a tool part, and we only have the one material, set its material
      if (materials.length == 1 && extraMaterials.isEmpty() && stack.getItem() instanceof IMaterialItem materialItem) {
        return materialItem.setMaterial(stack, materials[0]);
      }
      MaterialNBT.Builder builder = MaterialNBT.builder();
      // add each material
      for (MaterialVariantId material : materials) {
        builder.add(material);
      }
      // add extra materials
      builder.add(extraMaterials);
      ToolStack.from(stack).setMaterials(builder.build());
    }
    return stack;
  }

  @Override
  public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
    return assemble(super.assemble(inventory, registries), inventory, parts, parts.size(), checkRepeats, extraMaterials);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapedMaterialsRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapedMaterialsRecipe> {
    static final Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = MaterialVariantId.LOADABLE.list(0);
    /** Codec bridges keeping the 1.20 JSON shape: the result uses the "item" key, extra materials are id strings */
    static final Codec<List<MaterialVariantId>> EXTRA_MATERIALS_CODEC = new LoadableCodec<>(EXTRA_MATERIALS);
    static final Codec<ItemStack> RESULT_CODEC = new LoadableCodec<>(ItemStackLoadable.REQUIRED_STACK);

    private static final MapCodec<ShapedMaterialsRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.getGroup()),
      CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category()),
      ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
      RESULT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
      Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(recipe -> recipe.showNotification()),
      Codec.STRING.fieldOf("parts").forGetter(Serializer::getPartsString),
      EXTRA_MATERIALS_CODEC.optionalFieldOf("extra_materials", List.of()).forGetter(recipe -> recipe.extraMaterials)
    ).apply(instance, Serializer::fromJson));

    /** Gets the original symbol map; present on JSON parsed patterns, absent after network syncing */
    private static Map<Character,Ingredient> patternKey(ShapedRecipePattern pattern) {
      return pattern.data.orElseThrow(() -> new IllegalStateException("Pattern is missing its original key data")).key();
    }

    /** Resolves the parts pattern against the key, sharing ingredient instances with the pattern */
    private static ShapedMaterialsRecipe fromJson(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, String partPattern, List<MaterialVariantId> extraMaterials) {
      // specific to shaped part recipe, map from a pattern string to the ingredients for each character
      // saves memory by not having separate copies of each, plus simplifies the JSON
      Map<Character,Ingredient> key = patternKey(pattern);
      List<Ingredient> parts = new ArrayList<>(partPattern.length());
      for (int i = 0; i < partPattern.length(); i++) {
        char sym = partPattern.charAt(i);
        Ingredient ingredient = key.get(sym);
        if (ingredient == null) {
          throw new JsonSyntaxException("Parts references symbol '" + sym + "' but it's not defined in the key");
        }
        parts.add(ingredient);
      }
      return new ShapedMaterialsRecipe(group, category, pattern, result, showNotification, List.copyOf(parts), extraMaterials);
    }

    /** Serializes the parts list back into its pattern string for datagen */
    private static String getPartsString(ShapedMaterialsRecipe recipe) {
      Map<Character,Ingredient> key = patternKey(recipe.pattern);
      StringBuilder builder = new StringBuilder(recipe.parts.size());
      for (Ingredient part : recipe.parts) {
        char symbol = 0;
        boolean found = false;
        for (Map.Entry<Character,Ingredient> entry : key.entrySet()) {
          // instance comparison as the parts were resolved from the key map
          if (entry.getValue() == part) {
            symbol = entry.getKey();
            found = true;
            break;
          }
        }
        if (!found) {
          throw new IllegalStateException("Part ingredient is missing from the pattern key");
        }
        builder.append(symbol);
      }
      return builder.toString();
    }

    /** Index by instance: 1.21's Ingredient.equals compares the vanilla value array, which aliases all Fabric custom ingredients (and EMPTY), so List.indexOf would mismatch */
    private static int identityIndexOf(List<Ingredient> list, Ingredient ingredient) {
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i) == ingredient) {
          return i;
        }
      }
      return -1;
    }

    @Override
    public MapCodec<ShapedMaterialsRecipe> codec() {
      return CODEC;
    }

    @Override
    public ShapedMaterialsRecipe fromNetworkSafe(RegistryFriendlyByteBuf buffer) {
      // shaped syncing
      int width = buffer.readVarInt();
      int height = buffer.readVarInt();
      String group = buffer.readUtf();
      CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
      // skipping ingredients for now
      ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
      boolean showNotification = buffer.readBoolean();
      // fetch remaining non-ingredient elements
      List<MaterialVariantId> extraMaterials = EXTRA_MATERIALS.decode(buffer);

      // start syncing ingredients back over, they are distinct so we will need to rematch them
      int size = buffer.readVarInt();
      List<Ingredient> distinct = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        distinct.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
      }

      // form inputs and parts lists
      NonNullList<Ingredient> inputs = NonNullList.withSize(width * height, Ingredient.EMPTY);
      for (int i = 0; i < inputs.size(); i++) {
        inputs.set(i, LogicHelper.getOrDefault(distinct, buffer.readByte(), Ingredient.EMPTY));
      }
      size = buffer.readVarInt();
      // read in parts
      List<Ingredient> parts = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        parts.add(i, LogicHelper.getOrDefault(distinct, buffer.readByte(), Ingredient.EMPTY));
      }
      // no key data on the network; only the JSON path needs it to resolve parts
      return new ShapedMaterialsRecipe(group, category, new ShapedRecipePattern(width, height, inputs, Optional.empty()), result, showNotification, List.copyOf(parts), extraMaterials);
    }

    @Override
    public void toNetworkSafe(RegistryFriendlyByteBuf buffer, ShapedMaterialsRecipe recipe) {
      // standard shaped recipe stuff
      buffer.writeVarInt(recipe.getWidth());
      buffer.writeVarInt(recipe.getHeight());
      buffer.writeUtf(recipe.getGroup());
      buffer.writeEnum(recipe.category());
      // skipping ingredients for now
      ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
      buffer.writeBoolean(recipe.showNotification());
      // sync remaining non-ingredient elements
      EXTRA_MATERIALS.encode(buffer, recipe.extraMaterials);

      // save memory and ensure instance matching by syncing only unique ingredients (by instance comparison)
      List<Ingredient> inputs = recipe.getIngredients();
      List<Ingredient> distinct = new ArrayList<>();
      for (Ingredient ingredient : inputs) {
        if (identityIndexOf(distinct, ingredient) == -1) {
          distinct.add(ingredient);
        }
      }
      buffer.writeVarInt(distinct.size());
      for (Ingredient ingredient : distinct) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
      }
      // to sync inputs, we just sync the index within the distinct list
      for (Ingredient ingredient : inputs) {
        buffer.writeByte(identityIndexOf(distinct, ingredient));
      }
      // parts size is not determine from ingredients size, so sync it directly
      buffer.writeVarInt(recipe.parts.size());
      for (Ingredient ingredient : recipe.parts) {
        buffer.writeByte(identityIndexOf(distinct, ingredient));
      }
    }
  }
}
