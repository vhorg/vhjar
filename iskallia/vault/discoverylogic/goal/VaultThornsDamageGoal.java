package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import net.minecraft.server.level.ServerPlayer;

public class VaultThornsDamageGoal extends InVaultDiscoveryGoal<VaultThornsDamageGoal> {
   public VaultThornsDamageGoal(float targetProgress) {
      super(targetProgress);
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_DAMAGE.register(manager, event -> {
         if (event.getSource() instanceof ThornsReflectDamageSource damageSource) {
            if (damageSource.getEntity() instanceof ServerPlayer player) {
               if (player.getLevel() == world) {
                  this.progress(player, event.getAmount());
               }
            }
         }
      });
   }
}
