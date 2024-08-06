package iskallia.vault.antique.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import iskallia.vault.core.random.RandomSource;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class AntiqueRewardCombined extends AntiqueReward {
   private final List<AntiqueReward> rewards = new ArrayList<>();

   @Override
   public List<ItemStack> generateReward(RandomSource random, ServerPlayer player, int level) {
      List<ItemStack> stacks = new ArrayList<>();

      for (AntiqueReward reward : this.rewards) {
         stacks.addAll(reward.generateReward(random, player, level));
      }

      return stacks;
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      JsonArray rewards = json.getAsJsonArray("rewards");

      for (int i = 0; i < rewards.size(); i++) {
         this.rewards.add((AntiqueReward)ctx.deserialize(rewards.get(i), AntiqueReward.class));
      }
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject object = new JsonObject();
      JsonArray rewards = new JsonArray();

      for (AntiqueReward reward : this.rewards) {
         rewards.add(ctx.serialize(reward));
      }

      object.add("rewards", rewards);
      return object;
   }
}
