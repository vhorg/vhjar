package iskallia.vault.core.vault.objective.scavenger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.data.item.PartialStack;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class ScavengeTask {
   public abstract Optional<ScavengerGoal> generateGoal(int var1, RandomSource var2);

   public abstract void initServer(VirtualWorld var1, Vault var2, Objective var3);

   public ItemStack createStack(Vault vault, ItemStack stack) {
      stack = stack.copy();
      stack.getOrCreateTag().putString("VaultId", vault.get(Vault.ID).toString());
      return stack;
   }

   public static class Adapter implements JsonSerializer<ScavengeTask>, JsonDeserializer<ScavengeTask> {
      public static final ScavengeTask.Adapter INSTANCE = new ScavengeTask.Adapter();

      public ScavengeTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         String var5 = object.get("type").getAsString();
         switch (var5) {
            case "chest":
               WeightedList<ChestScavengerTask.Entry> entries = new WeightedList<>();
               JsonObject obj = object.get("entries").getAsJsonObject();

               for (String key : obj.keySet()) {
                  PartialStack.parse(key, true)
                     .flatMap(partialStack -> partialStack.generate(1))
                     .ifPresentOrElse(
                        itemStack -> {
                           JsonObject value = obj.get(key).getAsJsonObject();
                           entries.put(
                              new ChestScavengerTask.Entry(itemStack, value.get("multiplier").getAsDouble(), value.get("color").getAsInt()),
                              value.get("weight").getAsInt()
                           );
                        },
                        () -> VaultMod.LOGGER.error("Invalid item: " + key)
                     );
               }

               return new ChestScavengerTask(
                  Adapters.TILE_PREDICATE.readJson(object.get("target")).orElse(TilePredicate.FALSE),
                  object.get("probability").getAsDouble(),
                  new ResourceLocation(object.get("icon").getAsString()),
                  entries
               );
            case "coin_stacks":
               WeightedList<CoinStacksScavengerTask.Entry> entries = new WeightedList<>();
               JsonObject obj = object.get("entries").getAsJsonObject();

               for (String key : obj.keySet()) {
                  JsonObject value = obj.get(key).getAsJsonObject();
                  PartialStack.parse(key, true)
                     .flatMap(partialStack -> partialStack.generate(1))
                     .ifPresentOrElse(
                        itemStack -> entries.put(
                           new CoinStacksScavengerTask.Entry(itemStack, value.get("multiplier").getAsDouble(), value.get("color").getAsInt()),
                           value.get("weight").getAsInt()
                        ),
                        () -> VaultMod.LOGGER.error("Invalid item: " + key)
                     );
               }

               return new CoinStacksScavengerTask(object.get("probability").getAsDouble(), new ResourceLocation(object.get("icon").getAsString()), entries);
            case "ore":
               WeightedList<OreScavengerTask.Entry> entries = new WeightedList<>();
               JsonObject obj = object.get("entries").getAsJsonObject();

               for (String key : obj.keySet()) {
                  JsonObject value = obj.get(key).getAsJsonObject();
                  PartialStack.parse(key, true)
                     .flatMap(partialStack -> partialStack.generate(1))
                     .ifPresentOrElse(
                        itemStack -> entries.put(
                           new OreScavengerTask.Entry(itemStack, value.get("multiplier").getAsDouble(), value.get("color").getAsInt()),
                           value.get("weight").getAsInt()
                        ),
                        () -> VaultMod.LOGGER.error("Invalid item: " + key)
                     );
               }

               return new OreScavengerTask(object.get("probability").getAsDouble(), new ResourceLocation(object.get("icon").getAsString()), entries);
            case "mob":
               List<MobScavengerTask.Entry> entries = new ArrayList<>();
               JsonObject obj = object.get("entries").getAsJsonObject();

               for (String key : obj.keySet()) {
                  JsonObject entry = obj.get(key).getAsJsonObject();
                  JsonArray list = entry.get("mobs").getAsJsonArray();
                  Set<ResourceLocation> group = new HashSet<>();

                  for (JsonElement element : list) {
                     group.add(new ResourceLocation(element.getAsString()));
                  }

                  PartialStack.parse(key, true)
                     .flatMap(partialStack -> partialStack.generate(1))
                     .ifPresentOrElse(
                        itemStack -> entries.add(new MobScavengerTask.Entry(itemStack, entry.get("multiplier").getAsDouble(), group)),
                        () -> VaultMod.LOGGER.error("Invalid item: " + key)
                     );
               }

               return new MobScavengerTask(
                  object.get("probability").getAsDouble(),
                  new ResourceLocation(object.get("icon").getAsString()),
                  object.get("color").getAsInt(),
                  entries.toArray(MobScavengerTask.Entry[]::new)
               );
            case "compound":
               List<ScavengeTask> children = new ArrayList<>();

               for (JsonElement element : object.get("children").getAsJsonArray()) {
                  children.add((ScavengeTask)context.deserialize(element, ScavengeTask.class));
               }

               return new CompoundScavengerTask(children);
            default:
               return null;
         }
      }

      public JsonElement serialize(ScavengeTask value, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject object = new JsonObject();
         if (value instanceof ChestScavengerTask chest) {
            object.addProperty("type", "chest");
            Adapters.TILE_PREDICATE.writeJson(chest.target).ifPresent(element -> object.add("target", element));
            object.addProperty("probability", chest.probability);
            object.addProperty("icon", chest.icon.toString());
            JsonObject entries = new JsonObject();
            chest.entries.forEach((entry, weight) -> {
               JsonObject objx = new JsonObject();
               objx.addProperty("weight", weight);
               objx.addProperty("multiplier", entry.multiplier);
               objx.addProperty("color", entry.color);
               entries.add(PartialStack.of(entry.item).toString(), objx);
            });
            object.add("entries", entries);
         } else if (value instanceof CoinStacksScavengerTask coin) {
            object.addProperty("type", "coin_stacks");
            object.addProperty("probability", coin.probability);
            object.addProperty("icon", coin.icon.toString());
            JsonObject entries = new JsonObject();
            coin.entries.forEach((entry, weight) -> {
               JsonObject objx = new JsonObject();
               objx.addProperty("weight", weight);
               objx.addProperty("multiplier", entry.multiplier);
               objx.addProperty("color", entry.color);
               entries.add(PartialStack.of(entry.item).toString(), objx);
            });
            object.add("entries", entries);
         } else if (value instanceof OreScavengerTask ore) {
            object.addProperty("type", "ore");
            object.addProperty("probability", ore.probability);
            object.addProperty("icon", ore.icon.toString());
            JsonObject entries = new JsonObject();
            ore.entries.forEach((entry, weight) -> {
               JsonObject objx = new JsonObject();
               objx.addProperty("weight", weight);
               objx.addProperty("multiplier", entry.multiplier);
               objx.addProperty("color", entry.color);
               entries.add(PartialStack.of(entry.item).toString(), objx);
            });
            object.add("entries", entries);
         } else if (value instanceof MobScavengerTask mob) {
            object.addProperty("type", "mob");
            object.addProperty("probability", mob.probability);
            object.addProperty("icon", mob.icon.toString());
            object.addProperty("color", mob.color);
            JsonObject entries = new JsonObject();

            for (MobScavengerTask.Entry entry : mob.entries) {
               JsonObject obj = new JsonObject();
               obj.addProperty("multiplier", entry.multiplier);
               JsonArray group = new JsonArray();
               entry.group.forEach(id -> group.add(id.toString()));
               obj.add("mobs", group);
               entries.add(PartialStack.of(entry.item).toString(), obj);
            }

            object.add("entries", entries);
         } else if (value instanceof CompoundScavengerTask compound) {
            object.addProperty("type", "compound");
            JsonArray children = new JsonArray();

            for (ScavengeTask child : compound.children) {
               children.add(context.serialize(child));
            }

            object.add("children", children);
         }

         return object;
      }
   }
}
