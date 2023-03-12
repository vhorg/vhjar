package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TilePredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ChestScavengerTask extends ScavengeTask {
   public final String target;
   public final double probability;
   public final ResourceLocation icon;
   public final WeightedList<ChestScavengerTask.Entry> entries;

   public ChestScavengerTask(String target, double probability, ResourceLocation icon, WeightedList<ChestScavengerTask.Entry> entries) {
      this.target = target;
      this.probability = probability;
      this.icon = icon;
      this.entries = entries;
   }

   @Override
   public Optional<ScavengerGoal> generateGoal(int count, RandomSource random) {
      return this.entries.getRandom(random).map(entry -> new ScavengerGoal(entry.item, (int)Math.ceil(count * entry.multiplier), this.icon, entry.color));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ScavengerObjective objective) {
      CommonEvents.CHEST_LOOT_GENERATION.post().register(objective, data -> {
         if (data.getPlayer().level == world) {
            if (!(data.getRandom().nextDouble() >= this.probability)) {
               TilePredicate predicate = TilePredicate.of(this.target);
               PartialTile tile = PartialTile.of(data.getState(), data.getTileEntity().serializeNBT());
               if (predicate.test(tile)) {
                  this.entries.getRandom(data.getRandom()).ifPresent(entry -> data.getLoot().add(this.createStack(vault, entry.item)));
               }
            }
         }
      });
   }

   public static class Entry {
      public final Item item;
      public final double multiplier;
      public final int color;

      public Entry(Item item, double multiplier, int color) {
         this.item = item;
         this.multiplier = multiplier;
         this.color = color;
      }
   }
}
