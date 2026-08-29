package slimeknights.tconstruct.library.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.ArmorTexture;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.TextureType;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;

import javax.annotation.Nullable;

/** Common shared logic for material armor models */
public abstract class AbstractArmorModel extends Model {
  /** Base model instance for rendering */
  @Nullable
  protected HumanoidModel<?> base;
  /** If true, applies the enchantment glint to extra layers */
  protected boolean hasGlint = false;
  /** If true, uses the legs texture */
  protected TextureType textureType = TextureType.ARMOR;

  protected boolean hasWings = false;

  protected AbstractArmorModel() {
    super(RenderType::entityCutoutNoCull);
  }

  /** Sets up the model given the passed arguments */
  protected void setup(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> base) {
    this.base = base;
    this.hasGlint = stack.hasFoil();
    this.textureType = TextureType.fromSlot(slot);
    if (slot == EquipmentSlot.CHEST) {
      this.hasWings = ModifierUtil.checkVolatileFlag(stack, ModifiableArmorItem.ELYTRA);
      if (hasWings) {
        ElytraModel<LivingEntity> wings = getWings();
        wings.setupAnim(living, 0, 0, 0, 0, 0);
        copyProperties(base, wings);
      }
    } else {
      hasWings = false;
    }
  }

  /**
   * Renders a model with its own tint composed over the caller's.
   *
   * <p>1.21 collapsed the four float channels into one packed ARGB int, so what used to be four
   * multiplications is {@link FastColor.ARGB32#multiply}.
   */
  public static void renderColored(Model model, PoseStack matrices, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, int ownColor, int color) {
    model.renderToBuffer(matrices, buffer, packedLightIn, packedOverlayIn, ownColor == -1 ? color : FastColor.ARGB32.multiply(ownColor, color));
  }

  /** Renders the wings layer */
  protected void renderWings(PoseStack matrices, int packedLightIn, int packedOverlayIn, ArmorTexture texture, int color, boolean hasGlint) {
    matrices.pushPose();
    matrices.translate(0.0D, 0.0D, 0.125D);
    assert buffer != null;
    texture.renderTexture(getWings(), matrices, buffer, packedLightIn, packedOverlayIn, color, hasGlint);
    matrices.popPose();
  }


  /* Helpers */

  /**
   * Buffer source the layers draw into.
   *
   * <p>Forge handed the model only a single {@code VertexConsumer}, so this was scraped off
   * {@code RenderLivingEvent} and parked in a static — a layered armor model needs a buffer per
   * texture, not one. Fabric's {@link net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer}
   * passes the source in, so {@link TinkerArmorRenderer} sets this around the one call that reads
   * it. Still a static because {@code Model#renderToBuffer}, the method that reads it, is vanilla's
   * signature and cannot grow a parameter.
   */
  @Nullable
  public static MultiBufferSource buffer;

  /** Wings model to render */
  @Nullable
  private static ElytraModel<LivingEntity> wingsModel;

  /** Gets or creates the elytra model */
  private ElytraModel<LivingEntity> getWings() {
    if (wingsModel == null) {
      wingsModel = new ElytraModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELYTRA));
    }
    return wingsModel;
  }

  /** Handles the unchecked cast to copy entity model properties */
  @SuppressWarnings("unchecked")
  public static <T extends LivingEntity> void copyProperties(EntityModel<T> base, EntityModel<?> other) {
    base.copyPropertiesTo((EntityModel<T>)other);
  }
}
