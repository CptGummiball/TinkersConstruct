package slimeknights.tconstruct.library.tools.item.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

/** Armor model that applies multiple texture layers in order */
public class MultilayerArmorItem extends ModifiableArmorItem implements ArmorModelItem {
  private final ResourceLocation name;
  public MultilayerArmorItem(ModifiableArmorMaterial material, ArmorItem.Type slot, Properties properties) {
    this(material, slot, properties, material.getId());
  }

  public MultilayerArmorItem(ModifiableArmorMaterial material, ArmorItem.Type slot, Properties properties, ResourceLocation name) {
    super(material, slot, properties);
    this.name = name;
  }

  public MultilayerArmorItem(DummyArmorMaterial material, ArmorItem.Type slot, Properties properties, ToolDefinition toolDefinition) {
    this(material, slot, properties, toolDefinition, material.getId());
  }

  public MultilayerArmorItem(DummyArmorMaterial material, ArmorItem.Type slot, Properties properties, ToolDefinition toolDefinition, ResourceLocation name) {
    super(material.getHolder(), slot, properties, toolDefinition);
    this.name = name;
  }

  @Override
  public ResourceLocation getArmorModelName() {
    return name;
  }
}
