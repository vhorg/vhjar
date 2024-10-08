package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.EventPriority;

public class OreScavengerTask extends ScavengeTask {
   public final double probability;
   public final ResourceLocation icon;
   public final WeightedList<OreScavengerTask.Entry> entries;

   public OreScavengerTask(double probability, ResourceLocation icon, WeightedList<OreScavengerTask.Entry> entries) {
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
      CommonEvents.PLAYER_MINE.register(objective, EventPriority.LOW, data -> {
         if (data.getPlayer().level == world) {
            if (data.getState().getBlock() instanceof VaultOreBlock) {
               if ((Boolean)data.getState().getValue(VaultOreBlock.GENERATED)) {
                  ChunkRandom random = ChunkRandom.any();
                  BlockPos pos = data.getPos();
                  random.setBlockSeed(vault.get(Vault.SEED), pos.getX(), pos.getY(), pos.getZ(), 110307L);
                  if (!(random.nextDouble() >= this.probability)) {
                     this.entries.getRandom(world.getRandom()).ifPresent(entry -> {
                        List<ItemStack> items = new ArrayList<>();
                        items.add(this.createStack(vault, entry.item));
                        CommonEvents.ITEM_SCAVENGE_TASK.invoke(vault, world, data.getPos(), items);
                        items.forEach(item -> Block.popResource(world, pos, item));
                     });
                  }
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
