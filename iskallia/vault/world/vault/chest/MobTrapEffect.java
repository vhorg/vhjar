package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Random;
import net.minecraft.server.level.ServerPlayer;

public class MobTrapEffect extends VaultChestEffect {
   @Expose
   private final int attempts;

   public MobTrapEffect(String name, int attempts) {
      super(name);
      this.attempts = attempts;
   }

   public int getAttempts() {
      return this.attempts;
   }

   @Override
   public void apply(VirtualWorld world, Vault vault, ServerPlayer player) {
      vault.ifPresent(Vault.LISTENERS, listeners -> {
         Listener listener = listeners.get(player.getUUID());
         if (listener != null) {
            listener.ifPresent(Runner.SPAWNER, spawner -> {
               int spawned = 0;

               for (int i = 0; i < this.attempts * 200 && spawned < this.attempts; i++) {
                  if (spawner.attemptSpawn(world, vault, player, new Random()) != null) {
                     spawned++;
                  }
               }
            });
         }
      });
   }
}
