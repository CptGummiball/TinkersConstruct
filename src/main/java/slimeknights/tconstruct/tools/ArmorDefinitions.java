package slimeknights.tconstruct.tools;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

import static slimeknights.tconstruct.TConstruct.getResource;

public class ArmorDefinitions {
   /** Balanced armor set */
  public static final ModifiableArmorMaterial TRAVELERS = ModifiableArmorMaterial.create(getResource("travelers"), net.minecraft.core.Holder.direct(Sounds.EQUIP_TRAVELERS.getSound()));
  public static final ToolDefinition TRAVELERS_SHIELD = ToolDefinition.create(slimeknights.tconstruct.TConstruct.getResource("travelers_shield"));

  /** High defense armor set */
  public static final ModifiableArmorMaterial PLATE = ModifiableArmorMaterial.create(getResource("plate"), net.minecraft.core.Holder.direct(Sounds.EQUIP_PLATE.getSound()));
  public static final ToolDefinition PLATE_SHIELD = ToolDefinition.create(slimeknights.tconstruct.TConstruct.getResource("plate_shield"));

  /** High modifiers armor set */
  public static final ModifiableArmorMaterial SLIMESUIT = ModifiableArmorMaterial.create(getResource("slime"), net.minecraft.core.Holder.direct(Sounds.EQUIP_SLIME.getSound()));
  public static final ToolDefinition SLIME_WINGS = ToolDefinition.create(slimeknights.tconstruct.TConstruct.getResource("slime_wings"));
}
