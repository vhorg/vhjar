package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.discoverylogic.goal.base.FlaggedVaultDiscoveryGoal;

public class VaultCompletionGoal extends FlaggedVaultDiscoveryGoal<VaultCompletionGoal> {
   public VaultCompletionGoal() {
      this.withPredicate(data -> {
         Listener listener = data.getListener();
         StatCollector stats = data.getVault().get(Vault.STATS).get(listener);
         Completion completion = stats.getCompletion();
         return completion == Completion.COMPLETED;
      });
   }
}
