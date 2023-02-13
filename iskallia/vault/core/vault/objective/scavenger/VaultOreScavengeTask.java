package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.core.event.CommonEvents;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultOreScavengeTask extends ScavengeTask {
   public final String target;
   public final double probability;
   public final ResourceLocation icon;
   public final WeightedList<VaultOreScavengeTask.Entry> entries;

   public VaultOreScavengeTask(String target, double probability, ResourceLocation icon, WeightedList<VaultOreScavengeTask.Entry> entries) {
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
      CommonEvents.PLAYER_MINE.register(objective, data -> {
         if (data.getPlayer().level == world) {
            if (!(world.getRandom().nextDouble() >= this.probability)) {
               if (this.validTarget(data.getState())) {
                  this.entries.getRandom(world.getRandom()).ifPresent(entry -> {
                     BlockPos pos = data.getPos();
                     Block.popResource(world, pos, this.createStack(vault, entry.item));
                  });
               }
            }
         }
      });
   }

   private boolean validTarget(BlockState state) {
      Optional<Boolean> generated = state.getOptionalValue(VaultOreBlock.GENERATED);
      if (generated.isEmpty() || !generated.get()) {
         return false;
      } else if (this.target.startsWith("#")) {
         ResourceLocation targetLocation = new ResourceLocation(this.target.substring(1));
         return state.getTags().anyMatch(blockTagKey -> targetLocation.equals(blockTagKey.location()));
      } else {
         return state.getBlock() == ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.target));
      }
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
