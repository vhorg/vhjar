package iskallia.vault.discoverylogic.goal.base;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.ListenerLeaveEvent;
import iskallia.vault.core.vault.DiscoveryGoalsManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;

public abstract class FlaggedVaultDiscoveryGoal<G extends FlaggedVaultDiscoveryGoal<G>> extends InVaultDiscoveryGoal<G> {
   public static int FLAG_ONGOING = 0;
   public static int FLAG_FAILED = 1;
   public static int FLAG_SUCCEED = 2;
   protected List<Predicate<ListenerLeaveEvent.Data>> predicates = new ArrayList<>();
   protected List<BiConsumer<DiscoveryGoalsManager, G>> serverInitializers = new ArrayList<>();

   public FlaggedVaultDiscoveryGoal() {
      super(FLAG_SUCCEED);
   }

   public G withPredicate(Predicate<ListenerLeaveEvent.Data> predicate) {
      this.predicates.add(predicate);
      return this.getSelf();
   }

   public G withServerInitializer(BiConsumer<DiscoveryGoalsManager, G> initializer) {
      this.serverInitializers.add(initializer);
      return this.getSelf();
   }

   @Override
   public void initServer(DiscoveryGoalsManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.LISTENER_LEAVE.register(manager, data -> {
         Listener listener = data.getListener();
         ServerPlayer player = listener.getPlayer().orElse(null);
         if (player != null) {
            if (this.predicates.stream().allMatch(p -> p.test(data))) {
               if (this.getCurrentProgress(player) == FLAG_ONGOING) {
                  this.setProgress(player, FLAG_SUCCEED);
               }
            }
         }
      });
      this.serverInitializers.forEach(reg -> reg.accept(manager, this.getSelf()));
   }

   public void markFailed(ServerPlayer player) {
      this.setProgress(player, FLAG_FAILED);
   }
}
