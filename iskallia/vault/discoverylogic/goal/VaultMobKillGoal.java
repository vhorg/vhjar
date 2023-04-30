package iskallia.vault.discoverylogic.goal;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class VaultMobKillGoal extends InVaultDiscoveryGoal<VaultMobKillGoal> {
   protected List<Predicate<LivingDeathEvent>> predicates = new LinkedList<>();

   public VaultMobKillGoal(int targetProgress) {
      super(targetProgress);
   }

   public VaultMobKillGoal withPredicate(Predicate<LivingDeathEvent> predicate) {
      this.predicates.add(predicate);
      return this;
   }

   public VaultMobKillGoal withKillerPredicate(Predicate<ServerPlayer> predicate) {
      this.predicates.add(e -> e.getSource().getEntity() instanceof ServerPlayer player ? predicate.test(player) : false);
      return this;
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_DEATH.register(manager, event -> {
         if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.getLevel() == world) {
               if (this.predicates.stream().allMatch(p -> p.test(event))) {
                  this.progress(player, 1.0F);
               }
            }
         }
      });
   }
}
