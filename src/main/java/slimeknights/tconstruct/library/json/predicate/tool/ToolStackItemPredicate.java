package slimeknights.tconstruct.library.json.predicate.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.LoadableCodec;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Matcher for Tinker tools inside vanilla item predicates.
 *
 * <p>1.21 rework: Forge let mods register whole custom {@code ItemPredicate} types; vanilla's
 * replacement is the {@link ItemSubPredicate} registry, so this is now a sub predicate under
 * {@code tconstruct:tool_stack} and the old factories hand back a wrapped vanilla predicate,
 * keeping every datagen call site unchanged. This also un-parks the last lombok-on-records
 * casualty: the class is a plain record now.
 */
public record ToolStackItemPredicate(IJsonPredicate<IToolStackView> predicate) implements ItemSubPredicate {
  public static final ResourceLocation ID = TConstruct.getResource("tool_stack");
  public static final Codec<ToolStackItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    new LoadableCodec<>(ToolStackPredicate.LOADER).fieldOf("predicate").forGetter(ToolStackItemPredicate::predicate)
  ).apply(instance, ToolStackItemPredicate::new));
  public static final ItemSubPredicate.Type<ToolStackItemPredicate> TYPE = new ItemSubPredicate.Type<>(CODEC);

  /** Registers the sub predicate type; called once from the bootstrap */
  public static void register() {
    Registry.register(BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE, ID, TYPE);
  }

  @Override
  public boolean matches(ItemStack stack) {
    // tag check is important to prevent accidently modifying the NBT of non-tools
    return stack.is(Items.MODIFIABLE) && predicate.matches(ToolStack.from(stack));
  }

  /** Creates a vanilla item predicate matching the given tool predicate */
  public static ItemPredicate ofTool(IJsonPredicate<IToolStackView> predicate) {
    return ItemPredicate.Builder.item().withSubPredicate(TYPE, new ToolStackItemPredicate(predicate)).build();
  }

  /** Creates a vanilla item predicate matching the given tool context predicate */
  public static ItemPredicate ofContext(IJsonPredicate<IToolContext> predicate) {
    return ofTool(ToolStackPredicate.context(predicate));
  }
}
