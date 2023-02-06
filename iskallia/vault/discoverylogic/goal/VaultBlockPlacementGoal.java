package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

public class VaultBlockPlacementGoal extends InVaultDiscoveryGoal<VaultBlockPlacementGoal> {
   protected Block block;

   public VaultBlockPlacementGoal(Block block, float targetProgress) {
      super(targetProgress);
      this.block = block;
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_PLACE.register(manager, event -> {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               if (event.getPlacedBlock().getBlock() == this.block) {
                  this.progress(player, 1.0F);
               }
            }
         }
      });
   }
}
