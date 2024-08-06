package iskallia.vault.antique.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class AntiqueRewardItemList extends AntiqueReward {
   private WeightedList<ItemStack> results = new WeightedList<>();
   private AntiqueRewardItemList.Range count = new AntiqueRewardItemList.Range(1, 1);

   public void setCount(AntiqueRewardItemList.Range count) {
      this.count = count;
   }

   public void addResult(ItemStack stack, double weight) {
      this.results.add(stack, weight);
   }

   @Override
   public List<ItemStack> generateReward(RandomSource random, ServerPlayer player, int level) {
      List<ItemStack> out = new ArrayList<>();
      this.results.getRandom(random).ifPresent(stack -> {
         ItemStack result = stack.copy();
         if (result.getItem() instanceof VaultLevelItem levelItem) {
            levelItem.initializeVaultLoot(level, result, null, null);
         }

         result = DataTransferItem.doConvertStack(result, random);
         DataInitializationItem.doInitialize(result, random);
         result.setCount(this.count.get(random));
         out.add(result);
      });
      return out;
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      this.results = new WeightedList<>();
      JsonArray results = json.getAsJsonArray("results");

      for (int i = 0; i < results.size(); i++) {
         JsonObject result = results.get(i).getAsJsonObject();
         ItemStack stack = (ItemStack)ctx.deserialize(result.get("item"), ItemStack.class);
         double weight = result.get("weight").getAsDouble();
         this.results.add(stack, weight);
      }

      this.count = AntiqueRewardItemList.Range.deserialize(json.getAsJsonObject("count"));
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      JsonArray results = new JsonArray();
      this.results.forEach((stack, weight) -> {
         JsonObject result = new JsonObject();
         result.add("item", ctx.serialize(stack));
         result.addProperty("weight", weight);
         results.add(result);
      });
      json.add("results", results);
      json.add("count", this.count.serialize());
      return json;
   }

   public static class Range {
      private final int min;
      private final int max;

      public Range(int min, int max) {
         this.min = min;
         this.max = max;
      }

      public int get(RandomSource random) {
         return random.nextInt(this.max - this.min + 1) + this.min;
      }

      public static AntiqueRewardItemList.Range deserialize(JsonObject json) {
         return new AntiqueRewardItemList.Range(json.get("min").getAsInt(), json.get("max").getAsInt());
      }

      public JsonObject serialize() {
         JsonObject json = new JsonObject();
         json.addProperty("min", this.min);
         json.addProperty("max", this.max);
         return json;
      }
   }
}
