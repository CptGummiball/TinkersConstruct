package slimeknights.tconstruct.library.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModelDispatcher;
import slimeknights.tconstruct.library.tools.item.armor.ArmorModelItem;

/**
 * Draws a Tinkers armor piece from the layers its model names.
 *
 * <p>Forge reached the model through {@code IClientItemExtensions#getGenericArmorModel}, a hook on
 * the item itself. Fabric registers a renderer per item instead, which suits the layered models
 * better: the renderer is handed the {@link MultiBufferSource}, where Forge's hook only ever saw a
 * single {@link VertexConsumer} and the buffer had to be scraped off a render event — see
 * {@link AbstractArmorModel#buffer}.
 *
 * <p>Registering here also replaces vanilla's own armor layer for these items: Fabric's armor
 * renderer runs instead of the texture lookup, so nothing draws twice.
 */
public class TinkerArmorRenderer extends ArmorModelDispatcher implements ArmorRenderer {
  private final ResourceLocation name;

  public TinkerArmorRenderer(ResourceLocation name) {
    this.name = name;
  }

  @Override
  protected ResourceLocation getName() {
    return name;
  }

  @Override
  public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
    Model model = getGenericArmorModel(entity, stack, slot, contextModel);
    // Forge's armor layer passed the overlay down; vanilla's computes it exactly this way
    int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0);
    MultiBufferSource previous = AbstractArmorModel.buffer;
    AbstractArmorModel.buffer = vertexConsumers;
    try {
      model.renderToBuffer(matrices, DISCARD, light, overlay, -1);
    } finally {
      AbstractArmorModel.buffer = previous;
    }
  }

  /**
   * Stand-in for the one consumer {@code Model#renderToBuffer} demands.
   *
   * <p>A layered armor model cannot use it: each layer draws with its own texture and therefore its
   * own buffer, taken from {@link AbstractArmorModel#buffer}. Passing something inert says that
   * outright, and makes a subclass that forgets and writes here draw nothing rather than draw into
   * whichever render type happened to be handy.
   */
  private static final VertexConsumer DISCARD = new VertexConsumer() {
    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
      return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
      return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
      return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
      return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
      return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
      return this;
    }
  };

  /**
   * Registers a renderer for every armor item that names an armor model.
   *
   * <p>Walks the item registry rather than a hardcoded list, so an addon's armor works by
   * implementing {@link ArmorModelItem} — as much as Forge's item hook asked of it.
   */
  public static void init() {
    for (Item item : BuiltInRegistries.ITEM) {
      if (item instanceof ArmorModelItem armor) {
        ArmorRenderer.register(new TinkerArmorRenderer(armor.getArmorModelName()), item);
      }
    }
  }
}
