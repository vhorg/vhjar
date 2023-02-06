package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

public class VaultChainAttackGoal extends InVaultDiscoveryGoal<VaultChainAttackGoal> {
   protected int targetCount;

   public VaultChainAttackGoal(int targetProgress, int targetCount) {
      super(targetProgress);
      this.targetCount = targetCount;
   }

   public int getTargetCount() {
      return this.targetCount;
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_CHAIN_ATTACKED.register(manager, event -> {
         if (event.getAttacker() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               List<Mob> attackedMobs = event.getAttackedMobs();
               if (attackedMobs.size() >= this.targetCount) {
                  this.progress(player, 1.0F);
               }
            }
         }
      });
   }
}
