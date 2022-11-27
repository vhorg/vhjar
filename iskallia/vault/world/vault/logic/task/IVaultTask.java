package iskallia.vault.world.vault.logic.task;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.server.level.ServerLevel;

@FunctionalInterface
public interface IVaultTask {
   void execute(VaultRaid var1, VaultPlayer var2, ServerLevel var3);

   default void executeForAllPlayers(VaultRaid vault, ServerLevel world) {
      vault.getPlayers().forEach(vPlayer -> this.execute(vault, vPlayer, world));
   }

   default IVaultTask then(IVaultTask other) {
      return (vault, player, world) -> {
         this.execute(vault, player, world);
         other.execute(vault, player, world);
      };
   }
}
