package iskallia.vault.config.bounty.task;

import iskallia.vault.bounty.task.properties.MiningProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.RangeEntry;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class MiningTaskConfig extends TaskConfig<TaskEntry<ResourceLocation>, MiningProperties> {
   @Override
   public String getName() {
      return super.getName() + "mining";
   }

   public MiningProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<ResourceLocation> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<ResourceLocation> entry = taskEntry.getRandom();
      MiningProperties miningProperties = new MiningProperties(
         entry.getValue(), new ArrayList<>(entry.getValidDimensions()), entry.isVaultOnly(), entry.getRandomAmount()
      );
      miningProperties.setRewardPool(entry.getRewardPool());
      return miningProperties;
   }

   @Override
   protected TaskEntry<ResourceLocation> generateConfigEntry() {
      return new TaskEntry<>(new GenericEntry<>(ForgeRegistries.BLOCKS.getKey(ModBlocks.LARIMAR_ORE), new RangeEntry(10, 30)).vaultOnly(), 3)
         .addEntry(new GenericEntry<>(ForgeRegistries.BLOCKS.getKey(ModBlocks.ISKALLIUM_ORE), new RangeEntry(10, 30)).vaultOnly(), 3)
         .addEntry(new GenericEntry<>(ForgeRegistries.BLOCKS.getKey(ModBlocks.GORGINITE_ORE), new RangeEntry(10, 30)).vaultOnly(), 3);
   }
}
