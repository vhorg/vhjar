package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CoinStacksScavengeTask extends ScavengeTask {
   public final double probability;
   public final double multiplier;
   public final ResourceLocation icon;
   public final WeightedList<CoinStacksScavengeTask.Entry> entries;

   public CoinStacksScavengeTask(double probability, double multiplier, ResourceLocation icon, WeightedList<CoinStacksScavengeTask.Entry> entries) {
      this.probability = probability;
      this.multiplier = multiplier;
      this.icon = icon;
      this.entries = entries;
   }

   @Override
   public Optional<ScavengerGoal> generateGoal(int count, RandomSource random) {
      return this.entries.getRandom(random).map(entry -> new ScavengerGoal(entry.item, (int)Math.ceil(count * this.multiplier), this.icon, entry.color));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ScavengerObjective objective) {
      CommonEvents.COIN_STACK_LOOT_GENERATION.post().register(objective, data -> {
         if (data.getPlayer().level == world) {
            if (!(world.getRandom().nextDouble() >= this.probability)) {
               this.entries.getRandom(world.getRandom()).ifPresent(entry -> data.getLoot().add(this.createStack(vault, entry.item)));
            }
         }
      });
   }

   public static class Entry {
      public final Item item;
      public final int color;

      public Entry(Item item, int color) {
         this.item = item;
         this.color = color;
      }
   }
}
