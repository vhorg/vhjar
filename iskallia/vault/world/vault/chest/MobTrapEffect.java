package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.VaultSpawner;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.world.server.ServerWorld;

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
   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      player.getProperties().getBase(VaultRaid.SPAWNER).ifPresent(spawner -> {
         VaultSpawner.Config oldConfig = spawner.getConfig();
         spawner.configure(this.getAppliedConfig());

         for (int i = 0; i < this.getAttempts(); i++) {
            spawner.execute(vault, player, world);
         }

         spawner.configure(oldConfig);
      });
   }
}
