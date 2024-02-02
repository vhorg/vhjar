package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.compound.ItemStackList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.vault.time.TickStopwatch;
import iskallia.vault.core.vault.time.TickTimer;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.world.entity.player.Player;

public class DeathObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("death", Objective.class).with(Version.v1_0, DeathObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Void> TIMER_DEATH = FieldKey.of("timer_death", Void.class).with(Version.v1_0, Adapters.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<Integer> KILL_ALL_STACK = FieldKey.of("kill_all_stack", Integer.class)
      .with(Version.v1_1, Adapters.INT_SEGMENTED_3, DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> KILL_ALL = FieldKey.of("kill_all", Void.class).with(Version.v1_1, Adapters.ofVoid(), DISK.all()).register(FIELDS);

   protected DeathObjective() {
   }

   public static DeathObjective create(boolean timerDeath) {
      return (DeathObjective)new DeathObjective().setIf(TIMER_DEATH, () -> timerDeath).set(KILL_ALL_STACK, Integer.valueOf(0));
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      listener.getPlayer().ifPresent(player -> {
         if (listener instanceof Runner) {
            TickClock clock = vault.get(Vault.CLOCK);
            int timeLeft = 0;
            if (clock instanceof TickTimer) {
               timeLeft = clock.get(TickTimer.DISPLAY_TIME);
            } else if (clock instanceof TickStopwatch) {
               timeLeft = clock.get(TickStopwatch.LIMIT) - clock.get(TickTimer.LOGICAL_TIME);
            }

            if (this.has(KILL_ALL) || this.has(TIMER_DEATH) && timeLeft < 0) {
               player.kill();
            }

            if (player.isDeadOrDying()) {
               if (this.get(KILL_ALL_STACK) > 0) {
                  this.set(KILL_ALL);
               }

               vault.ifPresent(Vault.STATS, collector -> {
                  StatCollector stats = collector.get(listener.get(Listener.ID));
                  stats.set(StatCollector.COMPLETION, Completion.FAILED);
                  stats.set(StatCollector.REWARD, ItemStackList.createLegacy());
               });
               vault.get(Vault.LISTENERS).remove(world, vault, listener);
               player.getInventory().clearContent();
            }
         }
      });
      super.tickListener(world, vault, listener);
   }

   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      return objective == this;
   }
}
