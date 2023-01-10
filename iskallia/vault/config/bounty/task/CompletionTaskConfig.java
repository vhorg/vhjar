package iskallia.vault.config.bounty.task;

import iskallia.vault.bounty.task.properties.CompletionProperties;
import iskallia.vault.config.bounty.task.entry.GenericEntry;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.RangeEntry;
import java.util.ArrayList;

public class CompletionTaskConfig extends TaskConfig<TaskEntry<String>, CompletionProperties> {
   @Override
   public String getName() {
      return super.getName() + "completion";
   }

   public CompletionProperties getGeneratedTaskProperties(int vaultLevel) {
      TaskEntry<String> taskEntry = this.getEntry(vaultLevel);
      GenericEntry<String> entry = taskEntry.getRandom();
      CompletionProperties completionProperties = new CompletionProperties(
         entry.getValue(), new ArrayList<>(entry.getValidDimensions()), entry.isVaultOnly(), entry.getAmount().getRandom()
      );
      completionProperties.setRewardPool(entry.getRewardPool());
      return completionProperties;
   }

   @Override
   protected TaskEntry<String> generateConfigEntry() {
      return new TaskEntry<>(new GenericEntry<>("vault", new RangeEntry(1, 3)).vaultOnly(), 3)
         .addEntry(new GenericEntry<>("cake", new RangeEntry(1, 3)).vaultOnly(), 3)
         .addEntry(new GenericEntry<>("scavenger", new RangeEntry(1, 3)).vaultOnly(), 3);
   }
}
