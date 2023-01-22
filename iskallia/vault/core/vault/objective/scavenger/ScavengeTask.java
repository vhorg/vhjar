package iskallia.vault.core.vault.objective.scavenger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ScavengeTask {
   public abstract Optional<ScavengerGoal> generateGoal(int var1, RandomSource var2);

   public abstract void initServer(VirtualWorld var1, Vault var2, ScavengerObjective var3);

   public ItemStack createStack(Vault vault, Item item) {
      ItemStack stack = new ItemStack(item, 1);
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
               WeightedList<ChestScavengeTask.Entry> entries = new WeightedList<>();
               JsonObject obj = object.get("entries").getAsJsonObject();

               for (String key : obj.keySet()) {
                  JsonObject value = obj.get(key).getAsJsonObject();
                  entries.put(
                     new ChestScavengeTask.Entry(
                        (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(key)), value.get("multiplier").getAsDouble(), value.get("color").getAsInt()
                     ),
                     value.get("weight").getAsInt()
                  );
               }

               return new ChestScavengeTask(
                  object.get("target").getAsString(), object.get("probability").getAsDouble(), new ResourceLocation(object.get("icon").getAsString()), entries
               );
            case "coin_stacks":
               WeightedList<CoinStacksScavengeTask.Entry> entries = new WeightedList<>();
               JsonObject obj = object.get("entries").getAsJsonObject();

               for (String key : obj.keySet()) {
                  JsonObject value = obj.get(key).getAsJsonObject();
                  entries.put(
                     new CoinStacksScavengeTask.Entry(
                        (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(key)), value.get("multiplier").getAsDouble(), value.get("color").getAsInt()
                     ),
                     value.get("weight").getAsInt()
                  );
               }

               return new CoinStacksScavengeTask(object.get("probability").getAsDouble(), new ResourceLocation(object.get("icon").getAsString()), entries);
            case "mob":
               List<MobScavengeTask.Entry> entries = new ArrayList<>();
               JsonObject obj = object.get("entries").getAsJsonObject();

               for (String key : obj.keySet()) {
                  JsonObject entry = obj.get(key).getAsJsonObject();
                  JsonArray list = entry.get("mobs").getAsJsonArray();
                  Set<ResourceLocation> group = new HashSet<>();

                  for (JsonElement element : list) {
                     group.add(new ResourceLocation(element.getAsString()));
                  }

                  entries.add(
                     new MobScavengeTask.Entry((Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(key)), entry.get("multiplier").getAsDouble(), group)
                  );
               }

               return new MobScavengeTask(
                  object.get("probability").getAsDouble(),
                  new ResourceLocation(object.get("icon").getAsString()),
                  object.get("color").getAsInt(),
                  entries.toArray(MobScavengeTask.Entry[]::new)
               );
            default:
               return null;
         }
      }

      public JsonElement serialize(ScavengeTask value, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject object = new JsonObject();
         if (value instanceof ChestScavengeTask chest) {
            object.addProperty("type", "chest");
            object.addProperty("target", chest.target);
            object.addProperty("probability", chest.probability);
            object.addProperty("icon", chest.icon.toString());
            JsonObject entries = new JsonObject();
            chest.entries.forEach((entryx, weight) -> {
               JsonObject obj = new JsonObject();
               obj.addProperty("weight", weight);
               obj.addProperty("color", entryx.color);
               entries.add(entryx.item.getRegistryName().toString(), obj);
            });
            object.add("entries", entries);
         } else if (value instanceof CoinStacksScavengeTask coin) {
            object.addProperty("type", "coin_stacks");
            object.addProperty("probability", coin.probability);
            object.addProperty("icon", coin.icon.toString());
            JsonObject entries = new JsonObject();
            coin.entries.forEach((entryx, weight) -> {
               JsonObject obj = new JsonObject();
               obj.addProperty("weight", weight);
               obj.addProperty("color", entryx.color);
               entries.add(entryx.item.getRegistryName().toString(), obj);
            });
            object.add("entries", entries);
         } else if (value instanceof MobScavengeTask mob) {
            object.addProperty("type", "mob");
            object.addProperty("probability", mob.probability);
            object.addProperty("icon", mob.icon.toString());
            object.addProperty("color", mob.color);
            JsonObject entries = new JsonObject();

            for (MobScavengeTask.Entry entry : mob.entries) {
               JsonArray group = new JsonArray();
               entry.group.forEach(id -> group.add(id.toString()));
               entries.add(entry.item.getRegistryName().toString(), group);
            }

            object.add("entries", entries);
         }

         return object;
      }
   }
}
