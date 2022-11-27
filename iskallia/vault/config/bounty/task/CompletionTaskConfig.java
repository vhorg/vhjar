package iskallia.vault.config.bounty.task;

import iskallia.vault.VaultMod;
import iskallia.vault.bounty.task.properties.CompletionProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.RangeEntry;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;

public class CompletionTaskConfig extends TaskConfig<TaskEntry<ResourceLocation>, CompletionProperties> {
   @Override
   public String getName() {
      return super.getName() + "completion";
   }

   public CompletionProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<ResourceLocation> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<ResourceLocation> entry = taskEntry.getRandom();
      return new CompletionProperties(entry.getValue(), new ArrayList<>(entry.getValidDimensions()), entry.isVaultOnly(), entry.getAmount().getRandom());
   }

   @Override
   protected TaskEntry<ResourceLocation> generateConfigEntry() {
      return new TaskEntry<>(new GenericEntry<>(VaultMod.id("vault"), new RangeEntry(1, 3)).vaultOnly(), 3)
         .addEntry(new GenericEntry<>(VaultMod.id("cake"), new RangeEntry(1, 3)).vaultOnly(), 3)
         .addEntry(new GenericEntry<>(VaultMod.id("scavenger"), new RangeEntry(1, 3)).vaultOnly(), 3);
   }
}
