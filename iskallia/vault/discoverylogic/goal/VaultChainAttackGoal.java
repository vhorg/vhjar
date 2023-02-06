package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

public class VaultChainAttackGoal extends InVaultDiscoveryGoal {
   protected int targetCount;

   public VaultChainAttackGoal(int targetProgress, int targetCount) {
      super(targetProgress);
      this.targetCount = targetCount;
   }

   @Override
   public void onGoalAchieved(ServerPlayer player) {
      DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
      ResourceLocation modelId = ModDynamicModels.Swords.CHAINSWORD.getId();
      if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
         discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
      }
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
