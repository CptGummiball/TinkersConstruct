package slimeknights.mantle.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen shim for Forge's {@code ConditionalAdvancement}: condition-guarded advancement
 * alternatives under one file, unwrapped at load by the advancement manager mixin. Writes
 * the shape the shipped data carries:
 *
 * <pre>{"advancements": [{"conditions": [...], "advancement": {...}}]}</pre>
 */
public class ConditionalAdvancement {

  private ConditionalAdvancement() {}

  public static class Builder {
    private final List<ICondition[]> conditions = new ArrayList<>();
    private final List<Advancement.Builder> advancements = new ArrayList<>();
    private List<ICondition> pending = new ArrayList<>();

    /** Adds a condition guarding the next advancement */
    public Builder addCondition(ICondition condition) {
      pending.add(condition);
      return this;
    }

    /** Adds an advancement guarded by the conditions added since the last one */
    public Builder addAdvancement(Advancement.Builder advancement) {
      if (pending.isEmpty()) {
        throw new IllegalStateException("Cannot add an advancement without conditions");
      }
      conditions.add(pending.toArray(new ICondition[0]));
      pending = new ArrayList<>();
      advancements.add(advancement);
      return this;
    }

    /** Writes the wrapper to JSON; the id names the file and never appears inside */
    public JsonObject write(DynamicOps<JsonElement> ops, ResourceLocation id) {
      if (!pending.isEmpty()) {
        throw new IllegalStateException("Conditions added without an advancement");
      }
      JsonArray alternatives = new JsonArray();
      for (int i = 0; i < advancements.size(); i++) {
        JsonObject entry = new JsonObject();
        entry.add("conditions", ConditionHelper.serialize(conditions.get(i)));
        entry.add("advancement", Advancement.CODEC.encodeStart(ops, advancements.get(i).build(id).value()).getOrThrow());
        alternatives.add(entry);
      }
      JsonObject json = new JsonObject();
      json.add("advancements", alternatives);
      return json;
    }
  }
}
