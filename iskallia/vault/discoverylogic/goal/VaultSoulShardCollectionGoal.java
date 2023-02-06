package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import iskallia.vault.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class VaultSoulShardCollectionGoal extends InVaultDiscoveryGoal<VaultSoulShardCollectionGoal> {
   public VaultSoulShardCollectionGoal(int targetProgress) {
      super(targetProgress);
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_DROPS
         .register(
            manager,
            event -> {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (player.getLevel() == world) {
                     int shardCount = event.getDrops()
                        .stream()
                        .<ItemStack>map(ItemEntity::getItem)
                        .filter(stack -> stack.getItem() == ModItems.SOUL_SHARD)
                        .reduce(0, (sum, itemStack) -> sum + itemStack.getCount(), Integer::sum);
                     if (shardCount > 0) {
                        this.progress(player, shardCount);
                     }
                  }
               }
            }
         );
   }
}
