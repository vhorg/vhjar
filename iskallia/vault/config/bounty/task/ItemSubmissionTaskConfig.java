package iskallia.vault.config.bounty.task;

import iskallia.vault.bounty.task.properties.ItemSubmissionProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.RangeEntry;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemSubmissionTaskConfig extends TaskConfig<TaskEntry<ResourceLocation>, ItemSubmissionProperties> {
   @Override
   public String getName() {
      return super.getName() + "item_submission";
   }

   public ItemSubmissionProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<ResourceLocation> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<ResourceLocation> entry = taskEntry.getRandom();
      ItemSubmissionProperties itemSubmissionProperties = new ItemSubmissionProperties(
         entry.getValue(), new ArrayList<>(entry.getValidDimensions()), entry.isVaultOnly(), entry.getRandomAmount()
      );
      itemSubmissionProperties.setRewardPool(entry.getRewardPool());
      return itemSubmissionProperties;
   }

   @Override
   protected TaskEntry<ResourceLocation> generateConfigEntry() {
      return new TaskEntry<>(new GenericEntry<>(ForgeRegistries.ITEMS.getKey(ModItems.VAULT_APPLE), new RangeEntry(10, 100)).floorToNearestTen().vaultOnly(), 3)
         .addEntry(new GenericEntry<>(ForgeRegistries.ITEMS.getKey(ModItems.VAULT_DIAMOND), new RangeEntry(10, 100)).floorToNearestTen().vaultOnly(), 3)
         .addEntry(new GenericEntry<>(ForgeRegistries.ITEMS.getKey(ModItems.CHROMATIC_IRON_NUGGET), new RangeEntry(10, 100)).floorToNearestTen().vaultOnly(), 3);
   }
}
