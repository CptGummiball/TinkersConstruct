package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.world.level.block.grower.TreeGrower;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Optional;

/**
 * Tree growers for the slime saplings.
 *
 * <p>1.21 made {@code TreeGrower} a data-holding class instead of a subclassing hook, so this
 * is now a factory; the ender 85/15 tall-tree split maps onto the secondary-tree constructor.
 */
public class SlimeTree {
  private SlimeTree() {}

  /** Creates the tree grower for the given foliage type */
  public static TreeGrower grower(FoliageType foliageType) {
    String name = "tconstruct:" + foliageType.getSerializedName() + "_slime";
    return switch (foliageType) {
      case EARTH -> new TreeGrower(name, Optional.empty(), Optional.of(TinkerStructures.earthSlimeTree), Optional.empty());
      case SKY -> new TreeGrower(name, Optional.empty(), Optional.of(TinkerStructures.skySlimeTree), Optional.empty());
      // 15% chance of the short variant as the "secondary" tree
      case ENDER -> new TreeGrower(name, 0.15f, Optional.empty(), Optional.empty(), Optional.of(TinkerStructures.enderSlimeTreeTall), Optional.of(TinkerStructures.enderSlimeTree), Optional.empty(), Optional.empty());
      case BLOOD -> new TreeGrower(name, Optional.empty(), Optional.of(TinkerStructures.bloodSlimeFungus), Optional.empty());
      case ICHOR -> new TreeGrower(name, Optional.empty(), Optional.of(TinkerStructures.ichorSlimeFungus), Optional.empty());
    };
  }
}
