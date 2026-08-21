package slimeknights.mantle.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import slimeknights.mantle.client.model.generators.ConfiguredModel.Builder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builder for variant-format blockstate files, port of Forge's builder of the same name.
 * Models are assigned to partial states; on serialization each partial state becomes a
 * variant key of its set properties sorted by property name.
 */
public class VariantBlockStateBuilder implements IGeneratedBlockState {
  private final Block owner;
  private final Map<PartialBlockstate, BlockStateProvider.ConfiguredModelList> models = new LinkedHashMap<>();
  private final Set<BlockState> coveredStates = new java.util.HashSet<>();

  VariantBlockStateBuilder(Block owner) {
    this.owner = owner;
  }

  public Map<PartialBlockstate, BlockStateProvider.ConfiguredModelList> getModels() {
    return models;
  }

  public Block getOwner() {
    return owner;
  }

  @Override
  public JsonObject toJson() {
    // ensure that all states are covered by asserting how many are covered
    java.util.List<BlockState> missingStates = owner.getStateDefinition().getPossibleStates().stream()
      .filter(state -> !coveredStates.contains(state))
      .toList();
    Preconditions.checkState(missingStates.isEmpty(), "Blockstate for block %s does not cover all states. Missing: %s", owner, missingStates);
    JsonObject variants = new JsonObject();
    getModels().entrySet().stream()
               .sorted(Map.Entry.comparingByKey(PartialBlockstate.comparingByProperties()))
               .forEach(entry -> variants.add(entry.getKey().toString(), entry.getValue().toJSON()));
    JsonObject main = new JsonObject();
    main.add("variants", variants);
    return main;
  }

  /**
   * Assigns the given models to the given partial state, which must not have been
   * assigned models before.
   */
  public VariantBlockStateBuilder addModels(PartialBlockstate state, ConfiguredModel... models) {
    Preconditions.checkNotNull(state, "state must not be null");
    Preconditions.checkArgument(models.length > 0, "Cannot set models to empty array");
    Preconditions.checkArgument(state.getOwner() == owner, "Cannot set models for a different block. Found: %s, Current: %s", state.getOwner(), owner);
    if (!this.models.containsKey(state)) {
      this.models.put(state, new BlockStateProvider.ConfiguredModelList(models));
      for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
        if (state.test(fullState)) {
          coveredStates.add(fullState);
        }
      }
    } else {
      this.models.compute(state, ($, cml) -> cml.append(models));
    }
    return this;
  }

  /** Assigns the given models to the given partial state */
  public VariantBlockStateBuilder setModels(PartialBlockstate state, ConfiguredModel... model) {
    Preconditions.checkArgument(!models.containsKey(state), "Cannot set models for a state that has already been configured: %s", state);
    addModels(state, model);
    return this;
  }

  /** Creates a new unconfigured partial state */
  public PartialBlockstate partialState() {
    return new PartialBlockstate(owner, this);
  }

  public VariantBlockStateBuilder forAllStates(Function<BlockState, ConfiguredModel[]> mapper) {
    return forAllStatesExcept(mapper);
  }

  public VariantBlockStateBuilder forAllStatesExcept(Function<BlockState, ConfiguredModel[]> mapper, Property<?>... ignored) {
    Set<PartialBlockstate> seen = new java.util.HashSet<>();
    for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
      Map<Property<?>, Comparable<?>> propertyValues = new LinkedHashMap<>(fullState.getValues());
      for (Property<?> p : ignored) {
        propertyValues.remove(p);
      }
      PartialBlockstate partialState = new PartialBlockstate(owner, propertyValues, this);
      if (seen.add(partialState)) {
        setModels(partialState, mapper.apply(fullState));
      }
    }
    return this;
  }

  /** A subset of a block's property values, identifying one variant */
  public static class PartialBlockstate implements java.util.function.Predicate<BlockState> {
    private final Block owner;
    private final SortedMap<Property<?>, Comparable<?>> setStates;
    @javax.annotation.Nullable
    private final VariantBlockStateBuilder outerBuilder;

    PartialBlockstate(Block owner, @javax.annotation.Nullable VariantBlockStateBuilder outerBuilder) {
      this(owner, Map.of(), outerBuilder);
    }

    PartialBlockstate(Block owner, Map<Property<?>, Comparable<?>> setStates, @javax.annotation.Nullable VariantBlockStateBuilder outerBuilder) {
      this.owner = owner;
      this.outerBuilder = outerBuilder;
      for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
        Property<?> prop = entry.getKey();
        Comparable<?> value = entry.getValue();
        Preconditions.checkArgument(owner.getStateDefinition().getProperties().contains(prop), "Property %s not found on block %s", entry, this.owner);
        Preconditions.checkArgument(((Property)prop).getPossibleValues().contains(value), "%s is not a valid value for %s", value, prop);
      }
      this.setStates = new TreeMap<>(Comparator.comparing(Property::getName));
      this.setStates.putAll(setStates);
    }

    public <T extends Comparable<T>> PartialBlockstate with(Property<T> prop, T value) {
      Preconditions.checkArgument(!setStates.containsKey(prop), "Property %s has already been set", prop);
      Map<Property<?>, Comparable<?>> newState = new LinkedHashMap<>(setStates);
      newState.put(prop, value);
      return new PartialBlockstate(owner, newState, outerBuilder);
    }

    private void checkValidOwner() {
      Preconditions.checkNotNull(outerBuilder, "Partial blockstate must have a valid owner to perform this action");
    }

    /** Creates a builder for models to assign to this state */
    public ConfiguredModel.Builder<VariantBlockStateBuilder> modelForState() {
      checkValidOwner();
      return ConfiguredModel.builder(outerBuilder, this);
    }

    /** Assigns the given models to this state */
    public VariantBlockStateBuilder setModels(ConfiguredModel... models) {
      checkValidOwner();
      outerBuilder.setModels(this, models);
      return outerBuilder;
    }

    /** Adds the given models to this state, allowing existing entries to remain */
    public PartialBlockstate addModels(ConfiguredModel... models) {
      checkValidOwner();
      outerBuilder.addModels(this, models);
      return this;
    }

    /** Complete this state without adding any new models, and return a new partial state via the parent builder */
    public PartialBlockstate partialState() {
      checkValidOwner();
      return outerBuilder.partialState();
    }

    public Block getOwner() {
      return owner;
    }

    public SortedMap<Property<?>, Comparable<?>> getSetStates() {
      return setStates;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PartialBlockstate that = (PartialBlockstate)o;
      return owner.equals(that.owner) && setStates.equals(that.setStates);
    }

    @Override
    public int hashCode() {
      return java.util.Objects.hash(owner, setStates);
    }

    @Override
    public boolean test(BlockState blockState) {
      if (blockState.getBlock() != getOwner()) {
        return false;
      }
      for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
        if (blockState.getValue(entry.getKey()) != entry.getValue()) {
          return false;
        }
      }
      return true;
    }

    @Override
    public String toString() {
      StringBuilder ret = new StringBuilder();
      for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
        if (ret.length() > 0) {
          ret.append(',');
        }
        ret.append(entry.getKey().getName())
           .append('=')
           .append(((Property)entry.getKey()).getName((Comparable)entry.getValue()));
      }
      return ret.toString();
    }

    public static Comparator<PartialBlockstate> comparingByProperties() {
      // compare partial states by their string representation for a stable sort
      return Comparator.comparing(PartialBlockstate::toString);
    }
  }
}
