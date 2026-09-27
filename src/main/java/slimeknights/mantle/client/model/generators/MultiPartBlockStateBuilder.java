package slimeknights.mantle.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builder for multipart-format blockstate files, port of Forge's builder of the same name.
 * Parts serialize in insertion order; conditions within a part sort by property name.
 */
public class MultiPartBlockStateBuilder implements IGeneratedBlockState {
  private final List<PartBuilder> parts = new ArrayList<>();
  private final Block owner;

  MultiPartBlockStateBuilder(Block owner) {
    this.owner = owner;
  }

  /**
   * Creates a builder for models to assign to a part, which when completed via
   * {@link ConfiguredModel.Builder#addModel()} assigns the resultant set of models to the part.
   */
  public ConfiguredModel.Builder<PartBuilder> part() {
    return ConfiguredModel.builder(this);
  }

  void addPart(PartBuilder part) {
    this.parts.add(part);
  }

  @Override
  public JsonObject toJson() {
    JsonArray variants = new JsonArray();
    for (PartBuilder part : parts) {
      variants.add(part.toJson());
    }
    JsonObject main = new JsonObject();
    main.add("multipart", variants);
    return main;
  }

  public class PartBuilder {
    public BlockStateProvider.ConfiguredModelList models;
    public final Multimap<Property<?>, Comparable<?>> conditions = LinkedHashMultimap.create();

    PartBuilder(BlockStateProvider.ConfiguredModelList models) {
      this.models = models;
    }

    /** Set a condition for this part, which limits when the part is applied */
    @SafeVarargs
    public final <T extends Comparable<T>> PartBuilder condition(Property<T> prop, T... values) {
      Preconditions.checkNotNull(prop, "Property must not be null");
      Preconditions.checkNotNull(values, "Value list must not be null");
      Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
      Preconditions.checkArgument(!conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
      Preconditions.checkArgument(owner.getStateDefinition().getProperties().contains(prop), "Property %s is not valid for the block %s", prop, owner);
      this.conditions.putAll(prop, List.of(values));
      return this;
    }

    public MultiPartBlockStateBuilder end() {
      return MultiPartBlockStateBuilder.this;
    }

    JsonObject toJson() {
      JsonObject out = new JsonObject();
      if (!conditions.isEmpty()) {
        JsonObject when = new JsonObject();
        // sorted by property name for a stable output
        conditions.asMap().entrySet().stream()
                  .sorted(Comparator.comparing(e -> e.getKey().getName()))
                  .forEach(e -> when.addProperty(e.getKey().getName(),
                                                 e.getValue().stream().map(val -> valueName(e.getKey(), val)).collect(Collectors.joining("|"))));
        out.add("when", when);
      }
      out.add("apply", models.toJSON());
      return out;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String valueName(Property<?> prop, Comparable<?> value) {
      return ((Property)prop).getName((Comparable)value);
    }

    public boolean canApplyTo(Block b) {
      return b.getStateDefinition().getProperties().containsAll(conditions.keySet());
    }
  }
}
