package slimeknights.mantle.event.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

/**
 * Mirror of Forge's {@code PlayerInteractEvent} family. The Fabric event bridge posts these
 * from the matching Fabric interaction callbacks and translates a cancel plus
 * {@link #getCancellationResult()} back into the callback's return value.
 */
public class PlayerInteractEvent extends PlayerEvent {
  private final InteractionHand hand;
  private final BlockPos pos;
  private InteractionResult cancellationResult = InteractionResult.PASS;

  protected PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos) {
    super(player);
    this.hand = hand;
    this.pos = pos;
  }

  public InteractionHand getHand() {
    return hand;
  }

  public ItemStack getItemStack() {
    return getEntity().getItemInHand(hand);
  }

  public BlockPos getPos() {
    return pos;
  }

  public Level getLevel() {
    return getEntity().level();
  }

  public InteractionResult getCancellationResult() {
    return cancellationResult;
  }

  public void setCancellationResult(InteractionResult result) {
    this.cancellationResult = result;
  }

  @Override
  public boolean isCancelable() {
    return true;
  }

  /** Right click on a block; use flags mirror Forge's tri-state (always DEFAULT from the bridge) */
  public static class RightClickBlock extends PlayerInteractEvent {
    private final BlockHitResult hitVec;
    private Result useBlock = Result.DEFAULT;
    private Result useItem = Result.DEFAULT;

    public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
      super(player, hand, pos);
      this.hitVec = hitVec;
    }

    public BlockHitResult getHitVec() {
      return hitVec;
    }

    public Result getUseBlock() {
      return useBlock;
    }

    public Result getUseItem() {
      return useItem;
    }

    public void setUseBlock(Result useBlock) {
      this.useBlock = useBlock;
    }

    public void setUseItem(Result useItem) {
      this.useItem = useItem;
    }
  }

  /** Right click on an entity */
  public static class EntityInteract extends PlayerInteractEvent {
    private final Entity target;

    public EntityInteract(Player player, InteractionHand hand, Entity target) {
      super(player, hand, target.blockPosition());
      this.target = target;
    }

    public Entity getTarget() {
      return target;
    }
  }

  /** Left click on a block */
  public static class LeftClickBlock extends PlayerInteractEvent {
    @Nullable
    private final net.minecraft.core.Direction face;
    private final Action action;

    public LeftClickBlock(Player player, InteractionHand hand, BlockPos pos, @Nullable net.minecraft.core.Direction face, Action action) {
      super(player, hand, pos);
      this.face = face;
      this.action = action;
    }

    @Nullable
    public net.minecraft.core.Direction getFace() {
      return face;
    }

    public Action getAction() {
      return action;
    }

    /** Mirror of Forge's click phases; the Fabric attack callback only observes START */
    public enum Action {
      START,
      ABORT,
      STOP,
      CLIENT_HOLD
    }
  }

  /** Left click on air; on the server this only fires when a client packet reports it */
  public static class LeftClickEmpty extends PlayerInteractEvent {
    public LeftClickEmpty(Player player) {
      super(player, InteractionHand.MAIN_HAND, player.blockPosition());
    }
  }
}
