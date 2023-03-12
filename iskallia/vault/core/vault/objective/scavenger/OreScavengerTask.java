package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
      return this.entries.getRandom(random).map(entry -> new ScavengerGoal(entry.item, (int)Math.ceil(count * entry.multiplier), this.icon, entry.color));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ScavengerObjective objective) {
      CommonEvents.PLAYER_MINE.register(objective, data -> {
         if (data.getPlayer().level == world) {
            if (data.getState().getBlock() instanceof VaultOreBlock) {
               if ((Boolean)data.getState().getValue(VaultOreBlock.GENERATED)) {
                  ChunkRandom random = ChunkRandom.any();
                  BlockPos pos = data.getPos();
                  random.setBlockSeed(vault.get(Vault.SEED), pos.getX(), pos.getY(), pos.getZ(), 110307);
                  if (!(random.nextDouble() >= this.probability)) {
                     this.entries.getRandom(world.getRandom()).ifPresent(entry -> Block.popResource(world, pos, this.createStack(vault, entry.item)));
                  }
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
