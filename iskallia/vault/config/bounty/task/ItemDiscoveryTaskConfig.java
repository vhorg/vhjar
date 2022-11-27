package iskallia.vault.config.bounty.task;

import iskallia.vault.bounty.task.properties.ItemDiscoveryProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.RangeEntry;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDiscoveryTaskConfig extends TaskConfig<TaskEntry<ResourceLocation>, ItemDiscoveryProperties> {
   @Override
   public String getName() {
      return super.getName() + "item_discovery";
   }

   public ItemDiscoveryProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<ResourceLocation> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<ResourceLocation> entry = taskEntry.getRandom();
      return new ItemDiscoveryProperties(entry.getValue(), new ArrayList<>(entry.getValidDimensions()), entry.isVaultOnly(), entry.getRandomAmount());
   }

   @Override
   protected TaskEntry<ResourceLocation> generateConfigEntry() {
      return new TaskEntry<>(new GenericEntry<>(ForgeRegistries.ITEMS.getKey(Items.APPLE), new RangeEntry(10, 100)).floorToNearestTen().vaultOnly(), 3)
         .addEntry(new GenericEntry<>(ForgeRegistries.ITEMS.getKey(Items.STICK), new RangeEntry(10, 100)).floorToNearestTen().vaultOnly(), 3)
         .addEntry(new GenericEntry<>(ForgeRegistries.ITEMS.getKey(Items.DIAMOND), new RangeEntry(10, 100)).floorToNearestTen().vaultOnly(), 3);
   }
}
