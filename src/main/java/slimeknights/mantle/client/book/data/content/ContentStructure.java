package slimeknights.mantle.client.book.data.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.ArrowButton.ArrowType;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.ArrowElement;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.StructureElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a multiblock from a saved structure file, with controls to turn it and step through it
 * layer by layer.
 *
 * <p>The file is a vanilla structure NBT, so the smeltery and foundry previews are the same data a
 * structure block would place. Only the palette and block list are read: entities and block
 * entities have nothing to show at this size.
 */
public class ContentStructure extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("structure");

  @Nullable
  public String title = null;
  /** Location of the structure NBT, written as a full resource id */
  @Nullable
  public String data = null;
  public TextData[] text = new TextData[0];

  private transient List<StructureElement.StructureBlock> blocks = List.of();
  private transient BlockPos size = BlockPos.ZERO;

  @Nonnull
  @Override
  public String getTitle() {
    return this.title == null ? "" : this.title;
  }

  @Override
  public void load() {
    if (this.data == null || this.parent == null || this.parent.source == null) {
      return;
    }
    ResourceLocation location = this.parent.source.getResourceLocation(this.data);
    if (location == null) {
      return;
    }
    Resource resource = this.parent.source.getResource(location);
    if (resource == null) {
      Mantle.logger.error("Missing structure file {} for book page", location);
      return;
    }
    try (InputStream stream = resource.open()) {
      readStructure(NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap()));
    } catch (Exception e) {
      Mantle.logger.error("Failed to read structure file {}", location, e);
    }
  }

  /** Decodes the palette and block list of a vanilla structure file */
  private void readStructure(CompoundTag tag) {
    HolderGetter<Block> blockGetter = blockGetter();
    if (blockGetter == null) {
      return;
    }
    ListTag sizeTag = tag.getList("size", Tag.TAG_INT);
    if (sizeTag.size() == 3) {
      this.size = new BlockPos(sizeTag.getInt(0), sizeTag.getInt(1), sizeTag.getInt(2));
    }
    ListTag paletteTag = tag.getList("palette", Tag.TAG_COMPOUND);
    List<BlockState> palette = new ArrayList<>(paletteTag.size());
    for (int i = 0; i < paletteTag.size(); i++) {
      palette.add(NbtUtils.readBlockState(blockGetter, paletteTag.getCompound(i)));
    }
    ListTag blocksTag = tag.getList("blocks", Tag.TAG_COMPOUND);
    List<StructureElement.StructureBlock> blocks = new ArrayList<>(blocksTag.size());
    for (int i = 0; i < blocksTag.size(); i++) {
      CompoundTag entry = blocksTag.getCompound(i);
      int state = entry.getInt("state");
      if (state < 0 || state >= palette.size()) {
        continue;
      }
      BlockState blockState = palette.get(state);
      if (blockState.isAir()) {
        continue;
      }
      ListTag pos = entry.getList("pos", Tag.TAG_INT);
      if (pos.size() != 3) {
        continue;
      }
      blocks.add(new StructureElement.StructureBlock(new BlockPos(pos.getInt(0), pos.getInt(1), pos.getInt(2)), blockState));
    }
    this.blocks = blocks;
  }

  /** Block registry lookup from the connected client */
  @Nullable
  private static HolderGetter<Block> blockGetter() {
    net.minecraft.core.RegistryAccess access = SafeClientAccess.getRegistryAccess();
    if (access != null) {
      return access.lookupOrThrow(Registries.BLOCK);
    }
    return net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup();
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = 0;
    if (this.title != null && !this.title.isEmpty()) {
      this.addTitle(list, this.title);
      y = getTitleHeight();
    }
    if (this.blocks.isEmpty()) {
      list.add(new TextElement(0, y, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - y, this.text));
      return;
    }

    int viewHeight = BookScreen.PAGE_HEIGHT - y - 24;
    StructureElement structure = new StructureElement(0, y, BookScreen.PAGE_WIDTH, viewHeight, this.blocks, this.size);
    list.add(structure);

    int controlY = y + viewHeight + 4;
    int color = book.appearance.structureButtonColor;
    int hover = book.appearance.structureButtonColorHovered;
    list.add(new ArrowElement(4, controlY, ArrowType.LEFT, color, hover, button -> structure.rotate(-1)));
    list.add(new ArrowElement(BookScreen.PAGE_WIDTH - ArrowType.RIGHT.w - 4, controlY, ArrowType.RIGHT, color, hover, button -> structure.rotate(1)));
    list.add(new ArrowElement(BookScreen.PAGE_WIDTH / 2 - ArrowType.UP.w - 4, controlY - 4, ArrowType.UP, color, hover, button -> structure.changeLayer(1)));
    list.add(new ArrowElement(BookScreen.PAGE_WIDTH / 2 + 4, controlY - 4, ArrowType.DOWN, color, hover, button -> structure.changeLayer(-1)));

    if (this.text.length > 0) {
      list.add(new TextElement(0, controlY + 12, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - controlY - 12, this.text));
    }
  }

  @Override
  public HtmlSerializable toHTML(BookData book) {
    HtmlGroup group = HtmlGroup.indent();
    if (this.title != null && !this.title.isEmpty()) {
      group.add(makeTitleHTML());
    }
    group.add(TextData.toHtml(this.text, book));
    return group;
  }
}
