package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.NaturalSpawner;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.logic.VaultSpawner;
import net.minecraft.server.level.ServerPlayer;

public class MobTrapEffect extends VaultChestEffect {
   @Expose
   private final int attempts;
   @Expose
   private final VaultSpawner.Config appliedConfig;

   public MobTrapEffect(String name, int attempts, VaultSpawner.Config appliedConfig) {
      super(name);
      this.attempts = attempts;
      this.appliedConfig = appliedConfig;
   }

   public int getAttempts() {
      return this.attempts;
   }

   public VaultSpawner.Config getAppliedConfig() {
      return this.appliedConfig;
   }

   @Override
   public void apply(VirtualWorld world, Vault vault, ServerPlayer player) {
      vault.ifPresent(Vault.LISTENERS, listeners -> {
         Listener listener = listeners.get(player.getUUID());
         if (listener != null) {
            listener.ifPresent(Runner.SPAWNER, spawner -> {
               spawner.modify(NaturalSpawner.EXTRA_MAX_MOBS, ix -> ix + this.attempts);

               for (int i = 0; i < this.attempts; i++) {
                  spawner.tickServer(world, vault, listener);
               }

               spawner.modify(NaturalSpawner.EXTRA_MAX_MOBS, ix -> ix - this.attempts);
            });
         }
      });
   }
}
