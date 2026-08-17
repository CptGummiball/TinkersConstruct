package slimeknights.tconstruct.tools.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;

/** This item is mainly to return the proper model for a slimeskull */
public class SlimeskullItem extends ModifiableArmorItem {
  /** Model ID for our slimeskull. You may want your own for a custom slimeskull */
  public static final ResourceLocation MODEL_LOCATION = TConstruct.getResource("slimeskull");

  private final ResourceLocation name;

  public SlimeskullItem(ModifiableArmorMaterial material, ResourceLocation name, Properties properties) {
    super(material, ArmorItem.Type.HELMET, properties);
    this.name = name;
  }

  public SlimeskullItem(ModifiableArmorMaterial material, Properties properties) {
    this(material, material.getId(), properties);
  }

  // phase 5: Forge getArmorTexture and initializeClient skull armor model dispatch return with the client armor system
}
