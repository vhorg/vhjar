package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

public class VaultBlockBreakGoal extends InVaultDiscoveryGoal<VaultBlockBreakGoal> {
   protected Block blockId;

   public VaultBlockBreakGoal(Block blockId, float targetProgress) {
      super(targetProgress);
      this.blockId = blockId;
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.PLAYER_MINE.register(manager, event -> {
         if (event.getPlayer() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               if (event.getState().getBlock() == this.blockId) {
                  this.progress(player, 1.0F);
               }
            }
         }
      });
   }
}
