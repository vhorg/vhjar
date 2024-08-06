package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ChestScavengerTask extends ScavengeTask {
   public final TilePredicate target;
   public final double probability;
   public final ResourceLocation icon;
   public final WeightedList<ChestScavengerTask.Entry> entries;

   public ChestScavengerTask(TilePredicate target, double probability, ResourceLocation icon, WeightedList<ChestScavengerTask.Entry> entries) {
      this.target = target;
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
      CommonEvents.CHEST_LOOT_GENERATION.post().register(objective, data -> {
         if (data.getPlayer().level == world) {
            if (!(data.getRandom().nextDouble() >= this.probability)) {
               PartialTile tile = PartialTile.of(PartialBlockState.of(data.getState()), PartialCompoundNbt.of(data.getTileEntity()));
               if (this.target.test(tile)) {
                  this.entries.getRandom(data.getRandom()).ifPresent(entry -> {
                     List<ItemStack> items = new ArrayList<>();
                     items.add(this.createStack(vault, entry.item));
                     CommonEvents.ITEM_SCAVENGE_TASK.invoke(vault, world, data.getPos(), items);
                     data.getLoot().addAll(items);
                  });
               }
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
