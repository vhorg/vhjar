package iskallia.vault.core.vault;

import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.DiscoveryGoalsState;
import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import iskallia.vault.init.ModModelDiscoveryGoals;
import iskallia.vault.world.data.DiscoveryGoalStatesData;

public class DiscoveryGoalsManager extends DataObject<DiscoveryGoalsManager> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      ModModelDiscoveryGoals.REGISTRY.forEach((id, discoveryGoal) -> {
         if (discoveryGoal instanceof InVaultDiscoveryGoal<?> goal) {
            goal.initServer(this, world, vault);
         }
      });
      CommonEvents.LISTENER_LEAVE.register(this, data -> data.getListener().getPlayer().ifPresent(serverPlayer -> {
         DiscoveryGoalStatesData worldData = DiscoveryGoalStatesData.get(serverPlayer.getLevel());
         DiscoveryGoalsState state = worldData.getState(serverPlayer);
         state.resetGoalIf(goalId -> {
            DiscoveryGoal<?> goal = ModModelDiscoveryGoals.REGISTRY.get(goalId);
            return goal instanceof InVaultDiscoveryGoal;
         });
      }));
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }
}
