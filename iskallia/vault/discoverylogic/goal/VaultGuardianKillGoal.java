package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.data.LazySet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;

public class VaultGuardianKillGoal extends InVaultDiscoveryGoal<VaultGuardianKillGoal> {
   public static final Set<EntityType<?>> VALID_GUARDIANS = new LazySet<>(() -> List.of(ModEntities.ARBALIST_GUARDIAN, ModEntities.BRUISER_GUARDIAN));

   public VaultGuardianKillGoal(float targetProgress) {
      super(targetProgress);
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_DEATH.register(manager, event -> {
         if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.level == world) {
               if (VALID_GUARDIANS.contains(event.getEntity().getType())) {
                  if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.TURTLE_HELMET) {
                     this.progress(player, 1.0F);
                  }
               }
            }
         }
      });
   }
}
