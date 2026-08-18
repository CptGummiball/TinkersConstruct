package slimeknights.tconstruct.tools.logic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.living.LivingEvent.LivingJumpEvent;
import slimeknights.mantle.event.entity.living.LivingFallEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability.PersistentDataComponent;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.shared.TinkerAttributes;

/** Logic to run the double jump attribute */
public class DoubleJumpHandler {
  private static final ResourceLocation JUMPS = TConstruct.getResource("jumps");

  private DoubleJumpHandler() {}

  /** Registers event listeners, replacing the Forge {@code @EventBusSubscriber} annotation scan */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(LivingJumpEvent.class, DoubleJumpHandler::onJump);
    MinecraftForge.EVENT_BUS.addListener(LivingFallEvent.class, DoubleJumpHandler::onLand);
  }

  /** Event handler to reset the number of times we have jumped in mid-air */
  static void onJump(LivingJumpEvent event) {
    LivingEntity living = event.getEntity();
    // PORT 1.21: ForgeMod.ENTITY_GRAVITY became the vanilla gravity attribute
    if (living.onGround() || (living.verticalCollision && !living.verticalCollisionBelow && living.getAttributeValue(Attributes.GRAVITY) < 0)) {
      // PORT: the Forge capability's ifPresent shape maps to the CCA component key's nullable getter
      PersistentDataComponent data = PersistentDataCapability.CAPABILITY.getNullable(living);
      if (data != null) {
        data.getData().remove(JUMPS);
      }
    }
  }

  /** Event handler to reset the number of times we have jumped in mid air */
  static void onLand(LivingFallEvent event) {
    // PORT: the Forge capability's ifPresent shape maps to the CCA component key's nullable getter
    PersistentDataComponent data = PersistentDataCapability.CAPABILITY.getNullable(event.getEntity());
    if (data != null) {
      data.getData().remove(JUMPS);
    }
  }

  /**
   * Causes the player to jump an extra time, if possible
   * @param entity  Entity instance who wishes to jump again
   * @return  True if the entity jumpped, false if not
   */
  public static boolean extraJump(Player entity) {
    // validate preconditions, no using when swimming, elytra, or on the ground
    if (!entity.onGround() && !entity.onClimbable() && !entity.isInWaterOrBubble()) {
      // determine max jumps
      int extraJumps = Mth.floor(entity.getAttributeValue(TinkerAttributes.JUMP_COUNT)) - 1;
      if (extraJumps > 0) {
        // check that we can take more jumps
        ModDataNBT data = PersistentDataCapability.getOrWarn(entity);
        int jumps = data.getInt(JUMPS);
        if (jumps < extraJumps) {
          // actually jump, this method is nice enough to work in air
          entity.jumpFromGround();
          RandomSource random = entity.getCommandSenderWorld().getRandom();
          for (int i = 0; i < 4; i++) {
            entity.getCommandSenderWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, entity.getX() - 0.25f + random.nextFloat() * 0.5f, entity.getY(), entity.getZ() - 0.25f + random.nextFloat() * 0.5f, 0, 0, 0);
          }
          entity.playSound(Sounds.EXTRA_JUMP.getSound(), 0.5f, 0.5f);
          data.putInt(JUMPS, jumps + 1);
          return true;
        }
      }
    }
    return false;
  }
}
