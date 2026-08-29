package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.event.MinecraftForge;
import org.joml.Matrix4f;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.data.FloatMultiplier;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Orientation2D;
import slimeknights.tconstruct.library.utils.Orientation2D.Orientation1D;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.armor.MinimapModule;
import slimeknights.tconstruct.tools.modules.armor.SleevesModule;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Modifier event hooks that run client side.
 *
 * <p>Fabric port: the tooltip, disconnect and HUD hooks have direct Fabric callbacks; the hand
 * renderer and the field of view have none, so {@code ItemInHandRendererMixin} and
 * {@code AbstractClientPlayerFovMixin} call into the two methods below.
 */
public class ModifierClientEvents {
  /** Registers the hooks that Fabric offers a callback for */
  public static void init() {
    ItemTooltipCallback.EVENT.register((stack, context, flag, tooltip) -> onTooltip(stack, tooltip));
    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> playerLoggedOut());
    MinecraftForge.EVENT_BUS.addListener(ToolEquipmentChangeEvent.class, ModifierClientEvents::equipmentChange);
    HudRenderCallback.EVENT.register(ModifierClientEvents::renderHotbar);
  }

  static void onTooltip(ItemStack stack, List<Component> tooltip) {
    // suppress durability from advanced, we display our own
    if (stack.getItem() instanceof IModifiableDisplay) {
      tooltip.removeIf(text -> {

        if (text.getContents() instanceof TranslatableContents translatable) {
          return translatable.getKey().equals("item.durability");
        }
        return false;
      });
    }
  }

  /**
   * Determines whether to render the given hand based on modifiers.
   *
   * @return true if the hand should not be rendered normally
   */
  public static boolean renderHand(InteractionHand hand, PoseStack matrices, MultiBufferSource buffer, int packedLight, float equipProgress, float swingProgress) {
    Player player = Minecraft.getInstance().player;
    if (player == null) {
      return false;
    }
    // when firing your melee weapon with ballista, don't render it in the other hand; makes it look like you duplicated your weapon
    ItemStack held = player.getItemInHand(hand);
    ItemStack opposite = player.getItemInHand(Util.getOpposite(hand));
    if (!held.isEmpty() && !opposite.isEmpty() && opposite.is(TinkerTags.Items.BALLISTAS) && ModifierUtil.getPersistentInt(opposite, ModifiableBowItem.KEY_BALLISTA, 0) == ModifiableBowItem.FLAG_BALLISTA_HELD) {
      return true;
    }

    // the remainder of this listener renders the hand when it wouldn't normally, so skip if invisible
    if (player.isInvisible()) {
      return false;
    }

    boolean showHand;
    if (held.isEmpty()) {
      // if empty, we show the empty offhand when chestplate modifiers use it
      showHand = hand == InteractionHand.OFF_HAND && !player.isInvisible() && player.getMainHandItem().getItem() != Items.FILLED_MAP && ArmorLevelModule.getLevel(player, TinkerDataKeys.SHOW_EMPTY_OFFHAND) > 0;
    } else {
      // if filled, some items prefer to render your arm with them, like gloves
      showHand = held.is(TinkerTags.Items.SHOW_HAND);
    }
    if (showHand) {
      matrices.pushPose();
      HumanoidArm side = player.getMainArm();
      if (hand == InteractionHand.OFF_HAND) {
        side = side.getOpposite();
      }
      Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderPlayerArm(matrices, buffer, packedLight, equipProgress, swingProgress, side);
      matrices.popPose();
      // an empty hand has nothing else to draw, a filled one still needs its item
      return held.isEmpty();
    }
    return false;
  }

  /**
   * Handles the zoom modifier zooming.
   *
   * @param base      Field of view modifier before any of ours
   * @param current   Modifier as it stands, in case another mod already changed it
   * @return  Modifier to use
   */
  public static float handleZoom(Player player, float base, float current) {
    float[] result = {current};
    java.util.Optional.ofNullable(TinkerDataCapability.getData(player)).ifPresent(data -> {
      float newFov = result[0];

      // scaled effects only apply if we have FOV scaling, nothing to do if 0
      float effectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
      if (effectScale > 0) {
        FloatMultiplier scaledZoom = data.get(TinkerDataKeys.SCALED_FOV_MODIFIER);
        if (scaledZoom != null) {
          // much easier when 1, save some effort
          if (effectScale == 1) {
            newFov *= scaledZoom.getValue();
          } else {
            // unlerp the fov before multiplitying to make sure we apply the proper amount
            // we could use the original FOV, but someone else may have modified it
            float original = base;
            newFov *= Mth.lerp(effectScale, 1.0F, scaledZoom.getValue() * original) / original;
          }
        }
      }

      // non-scaled effects are much easier to deal with
      FloatMultiplier constZoom = data.get(TinkerDataKeys.FOV_MODIFIER);
      if (constZoom != null) {
        newFov *= constZoom.getValue();
      }
      result[0] = newFov;
    });
    return result[0];
  }


  /* Renders the next shield strap item above the offhand item */

  /** Cache of the current item to render */
  private static final int SLOT_BACKGROUND_SIZE = 22;
  /** Size of the border around the map */
  private static final int MAP_PADDING = 7;
  /** Total map size */
  private static final int MAP_SIZE = 2 * MAP_PADDING + 128;

  @Nonnull
  private static ItemStack nextOffhand = ItemStack.EMPTY;
  @Nonnull
  private static ItemStack currentSleeve = ItemStack.EMPTY;

  /** Items to render for the item frame modifier */
  private static final List<ItemStack> itemFrames = new ArrayList<>();

  static void playerLoggedOut() {
    nextOffhand = ItemStack.EMPTY;
    itemFrames.clear();
  }

  /** Update the slot in the first shield slot */
  static void equipmentChange(ToolEquipmentChangeEvent event) {
    if (event.getEntity() != Minecraft.getInstance().player) {
      return;
    }
    EquipmentChangeContext context = event.getContext();
    if (Config.CLIENT.renderShieldSlotItem.get()) {
      if (context.getChangedSlot() == EquipmentSlot.LEGS) {
        IToolStackView tool = context.getToolInSlot(EquipmentSlot.LEGS);
        if (tool != null) {
          ModifierEntry entry = tool.getModifiers().getEntry(TinkerModifiers.shieldStrap.getId());
          if (entry != ModifierEntry.EMPTY) {
            nextOffhand = entry.getHook(ToolInventoryCapability.HOOK).getStack(tool, entry, 0);
            return;
          }
        }
        nextOffhand = ItemStack.EMPTY;
      }
    }
    if (Config.CLIENT.renderSleevesItem.get()) {
      if (context.getChangedSlot() == EquipmentSlot.CHEST) {
        IToolStackView tool = context.getToolInSlot(EquipmentSlot.CHEST);
        if (tool != null) {
          ModifierEntry entry = tool.getModifiers().getEntry(TinkerModifiers.sleeves.getId());
          if (entry != ModifierEntry.EMPTY) {
            currentSleeve = entry.getHook(ToolInventoryCapability.HOOK).getStack(tool, entry, tool.getPersistentData().getInt(SleevesModule.SELECTED_SLOT));
            return;
          }
        }
        currentSleeve = ItemStack.EMPTY;
      }
    }

    if (Config.CLIENT.renderItemFrame.get()) {
      if (context.getChangedSlot() == EquipmentSlot.HEAD) {
        itemFrames.clear();
        IToolStackView tool = context.getToolInSlot(EquipmentSlot.HEAD);
        if (tool != null) {
          ModifierEntry entry = tool.getModifier(TinkerModifiers.itemFrame.getId());
          if (entry.intEffectiveLevel() > 0) {
            entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(tool, entry, itemFrames);
          }
        }
      }
    }
  }

  /** Gets the offset to apply for potion effects on the player */
  private static int getEffectOffset(Player player) {
    boolean hasBeneficial = false;
    for (MobEffectInstance instance : player.getActiveEffects()) {
      // Forge let a mod hide its effect from the GUI through a client extension; vanilla only has
      // the flag on the instance, so that is the whole condition here
      if (instance.showIcon()) {
        if (instance.getEffect().value().isBeneficial()) {
          hasBeneficial = true;
        } else {
          // negative effects means offset two rows
          return 52;
        }
      }
    }
    // if we found a positive effect, only need one row. Otherwise none
    return hasBeneficial ? 26 : 0;
  }

  /** Render the item in the first shield slot */
  public static void renderHotbar(GuiGraphics graphics, DeltaTracker deltaTracker) {
    Minecraft mc = Minecraft.getInstance();
    Player player = mc.player;
    // Forge hooked the hotbar overlay specifically, drawing straight after it; Fabric's callback runs
    // after the whole HUD, which puts these above the rest of it rather than only above the hotbar
    if (mc.options.hideGui || player == null || player != mc.getCameraEntity()) {
      return;
    }
    boolean renderShield = Config.CLIENT.renderShieldSlotItem.get() && !nextOffhand.isEmpty();
    boolean renderSleeves = Config.CLIENT.renderSleevesItem.get() && !currentSleeve.isEmpty();
    boolean renderItemFrame = Config.CLIENT.renderItemFrame.get() && !itemFrames.isEmpty();
    // fetch map stack instance
    float mapScale = Config.CLIENT.mapScale.get().floatValue();
    ItemStack map = ItemStack.EMPTY;
    if (mapScale > 0) {
      TinkerDataCapability.Holder data = TinkerDataCapability.getData(player);
      if (data != null) {
        map = data.get(MinimapModule.MAP, ItemStack.EMPTY);
      }
    }
    if (!renderItemFrame && !renderShield && !renderSleeves && map.isEmpty()) {
      return;
    }
    MultiPlayerGameMode playerController = mc.gameMode;
    if (playerController != null && playerController.getPlayerMode() != GameType.SPECTATOR) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();

      int scaledWidth = mc.getWindow().getGuiScaledWidth();
      int scaledHeight = mc.getWindow().getGuiScaledHeight();

      // want just above the normal offhand item
      boolean emptyOffhand = player.getOffhandItem().isEmpty();
      boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
      if (renderShield) {
        int x = scaledWidth / 2 + (rightHanded ? -117 : 101);
        int y = scaledHeight - 38;
        graphics.blit(Icons.ICONS, x - 3, y - 3, emptyOffhand ? 211 : 189, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
        mc.gui.renderSlot(graphics, x, y, deltaTracker, player, nextOffhand, 11);
      }
      // want to the side above the normal offhand item
      if (renderSleeves) {
        int x = scaledWidth / 2 + (rightHanded ? -136 : 120);
        int y = scaledHeight - 19;
        graphics.blit(Icons.ICONS, x - 3, y - 3, emptyOffhand ? 211 : rightHanded ? 145 : 123, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
        mc.gui.renderSlot(graphics, x, y, deltaTracker, player, currentSleeve, 11);
      }

      // TODO: cannot remember why this was needed before. Reconfirm if bug still exists.
      // skip the non-hotbar renderers when the pause screen is open, as they can sometimes show up above elements they shouldn't
      // if (mc.screen != null && mc.screen.isPauseScreen()) {
      //   return;
      // }

      // render map
      Orientation2D mapLocation = null;
      int mapOffset = 0;
      if (!map.isEmpty() && mc.level != null) {
        MapItemSavedData data = MapItem.getSavedData(map, mc.level);
        // 1.21 moved the map id onto a component and gave it its own type
        MapId index = map.get(DataComponents.MAP_ID);

        // determine placement of the map
        mapLocation = Config.CLIENT.mapLocation.get();
        Orientation1D xOrientation = mapLocation.getX();
        Orientation1D yOrientation = mapLocation.getY();
        mapOffset = (int) (MAP_SIZE * mapScale);
        int xStart = xOrientation.align(scaledWidth - mapOffset) + Config.CLIENT.mapXOffset.get();
        int yStart = yOrientation.align(scaledHeight - mapOffset) + Config.CLIENT.mapYOffset.get();

        // if top right, compute potion offset
        if (mapLocation == Orientation2D.TOP_RIGHT) {
          int effectOffset = getEffectOffset(player);
          yStart += effectOffset;
          mapOffset += effectOffset;
        }

        // setup renderer
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        float padding = MAP_PADDING * mapScale;
        poseStack.translate(xStart + padding, yStart + padding, 0);
        poseStack.scale(mapScale, mapScale, -1);

        // draw background
        int light = 0xF000F0;
        MultiBufferSource buffer = graphics.bufferSource();
        VertexConsumer consumer = buffer.getBuffer(data == null ? ItemInHandRenderer.MAP_BACKGROUND : ItemInHandRenderer.MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix = poseStack.last().pose();
        // 1.21 renamed the whole builder chain and made endVertex implicit
        consumer.addVertex(matrix,  -7, 135, 0).setColor(255, 255, 255, 255).setUv(0, 1).setLight(light);
        consumer.addVertex(matrix, 135, 135, 0).setColor(255, 255, 255, 255).setUv(1, 1).setLight(light);
        consumer.addVertex(matrix, 135,  -7, 0).setColor(255, 255, 255, 255).setUv(1, 0).setLight(light);
        consumer.addVertex(matrix,  -7,  -7, 0).setColor(255, 255, 255, 255).setUv(0, 0).setLight(light);

        // draw map if present
        if (data != null && index != null) {
          Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, buffer, index, data, false, light);
        }
        poseStack.popPose();
      }

      if (renderItemFrame) {
        // determine how many items need to be rendered
        int columns = Config.CLIENT.itemsPerRow.get();
        int count = itemFrames.size();
        // need to split items over multiple lines potentially
        int rows = count / columns;
        int inLastRow = count % columns;
        // if we have an exact number, means we should have full in last row
        if (inLastRow == 0) {
          inLastRow = columns;
        } else {
          // we have an incomplete row that was not counted
          rows++;
        }
        // determine placement of the items
        Orientation2D location = Config.CLIENT.itemFrameLocation.get();
        Orientation1D xOrientation = location.getX();
        Orientation1D yOrientation = location.getY();
        int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * columns) + Config.CLIENT.itemFrameXOffset.get();
        int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * rows) + Config.CLIENT.itemFrameYOffset.get();
        // if the map and item frame are at the same spot, offset item frame below
        if (location == mapLocation) {
          switch (yOrientation) {
            case START -> yStart += mapOffset;
            // add in an extra half set of the slots as we don't want to center it since the map took center
            case MIDDLE -> yStart += (mapOffset + SLOT_BACKGROUND_SIZE * rows) / 2;
            case END -> yStart -= mapOffset;
          }
        }
        // handle potions as well, though its already been handled in the map offset if present
        else if (location == Orientation2D.TOP_RIGHT) {
          yStart += getEffectOffset(player);
        }

        // draw backgrounds
        int lastRow = rows - 1;
        for (int r = 0; r < lastRow; r++) {
          for (int c = 0; c < columns; c++) {
            graphics.blit(Icons.ICONS, xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
          }
        }
        // last row will be aligned in the direction of x orientation (center, left, or right)
        int lastRowOffset = xOrientation.align((columns - inLastRow) * 2) * SLOT_BACKGROUND_SIZE / 2;
        for (int c = 0; c < inLastRow; c++) {
          graphics.blit(Icons.ICONS, xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
        }

        // draw items
        int i = 0;
        xStart += 3; yStart += 3; // offset from item start instead of frame start
        for (int r = 0; r < lastRow; r++) {
          for (int c = 0; c < columns; c++) {
            mc.gui.renderSlot(graphics, xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, deltaTracker, player, itemFrames.get(i), i);
            i++;
          }
        }
        // align last row
        for (int c = 0; c < inLastRow; c++) {
          mc.gui.renderSlot(graphics, xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, deltaTracker, player, itemFrames.get(i), i);
          i++;
        }
      }
    }
  }
}
