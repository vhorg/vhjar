package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import net.minecraft.server.level.ServerPlayer;

public class VaultAttackBlockComboGoal extends InVaultDiscoveryGoal<VaultAttackBlockComboGoal> {
   public VaultAttackBlockComboGoal(float targetProgress) {
      super(targetProgress);
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_DAMAGE_BLOCK.blockSucceeded().register(manager, event -> {
         if (event.getAttacked() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               this.progress(player, 1.0F);
            }
         }
      });
      CommonEvents.ENTITY_DAMAGE_BLOCK.blockFailed().register(manager, event -> {
         if (event.getAttacked() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               this.resetProgress(player);
            }
         }
      });
   }
}
