package iskallia.vault.world.vault.logic.task;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.world.server.ServerWorld;

@FunctionalInterface
public interface IVaultTask {
   void execute(VaultRaid var1, VaultPlayer var2, ServerWorld var3);

   default void executeForAllPlayers(VaultRaid vault, ServerWorld world) {
      vault.getPlayers().forEach(vPlayer -> this.execute(vault, vPlayer, world));
   }

   default IVaultTask then(IVaultTask other) {
      return (vault, player, world) -> {
         this.execute(vault, player, world);
         other.execute(vault, player, world);
      };
   }
}
