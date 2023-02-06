package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import net.minecraft.server.level.ServerPlayer;

public class VaultMobStunGoal extends InVaultDiscoveryGoal<VaultMobStunGoal> {
   public VaultMobStunGoal(int targetProgress) {
      super(targetProgress);
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_STUNNED.register(manager, event -> {
         if (event.getSource() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               this.progress(player, 1.0F);
            }
         }
      });
   }
}
