package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import net.minecraft.server.level.ServerPlayer;

public class VaultLeechGoal extends InVaultDiscoveryGoal<VaultLeechGoal> {
   public VaultLeechGoal(float targetProgress) {
      super(targetProgress);
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_LEECH.register(manager, event -> {
         if (event.getLeecher() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               this.progress(player, event.getAmountLeeched());
            }
         }
      });
   }
}
