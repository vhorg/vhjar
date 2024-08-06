package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CoinStacksScavengerTask extends ScavengeTask {
   public final double probability;
   public final ResourceLocation icon;
   public final WeightedList<CoinStacksScavengerTask.Entry> entries;

   public CoinStacksScavengerTask(double probability, ResourceLocation icon, WeightedList<CoinStacksScavengerTask.Entry> entries) {
      this.probability = probability;
      this.icon = icon;
      this.entries = entries;
   }

   @Override
   public Optional<ScavengerGoal> generateGoal(int count, RandomSource random) {
      return this.entries.getRandom(random).map(entry -> new ScavengerGoal((int)Math.ceil(count * entry.multiplier)).put(entry.item, this.icon, entry.color));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, Objective objective) {
      CommonEvents.COIN_STACK_LOOT_GENERATION.post().register(objective, data -> {
         if (data.getPlayer().level == world) {
            if (!(data.getRandom().nextDouble() >= this.probability)) {
               this.entries.getRandom(data.getRandom()).ifPresent(entry -> {
                  data.getLoot().add(this.createStack(vault, entry.item));
                  CommonEvents.ITEM_SCAVENGE_TASK.invoke(vault, world, data.getPos(), data.getLoot());
               });
            }
         }
      });
   }

   public static class Entry {
      public final ItemStack item;
      public final double multiplier;
      public final int color;

      public Entry(ItemStack item, double multiplier, int color) {
         this.item = item;
         this.multiplier = multiplier;
         this.color = color;
      }
   }
}
