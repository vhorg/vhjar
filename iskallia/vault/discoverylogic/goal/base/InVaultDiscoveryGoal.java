package iskallia.vault.discoverylogic.goal.base;

import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;

public abstract class InVaultDiscoveryGoal<G extends InVaultDiscoveryGoal<G>> extends DiscoveryGoal<G> {
   public InVaultDiscoveryGoal(float targetProgress) {
      super(targetProgress);
   }

   public abstract void initServer(DiscoveryGoalsManager var1, VirtualWorld var2, Vault var3);
}
