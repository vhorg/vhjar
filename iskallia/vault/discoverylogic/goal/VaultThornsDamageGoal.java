package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class VaultThornsDamageGoal extends InVaultDiscoveryGoal {
   public VaultThornsDamageGoal(float targetProgress) {
      super(targetProgress);
   }

   @Override
   public void onGoalAchieved(ServerPlayer player) {
      DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
      ResourceLocation modelId = ModDynamicModels.Shields.NOU.getId();
      if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
         discoversData.discoverModelAndBroadcast(ModItems.SHIELD, modelId, player);
      }
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
