package iskallia.vault.world.vault.builder;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayerType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class ClassicVaultBuilder extends VaultRaidBuilder {
   private static final ClassicVaultBuilder INSTANCE = new ClassicVaultBuilder();

   private ClassicVaultBuilder() {
   }

   public static ClassicVaultBuilder getInstance() {
      return INSTANCE;
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerLevel world, ServerPlayer player, CrystalData crystal) {
      VaultRaid.Builder builder = this.getDefaultBuilder(crystal, world, player);
      builder.addPlayer(VaultPlayerType.RUNNER, player);
      builder.set(VaultRaid.HOST, player.getUUID());
      return builder;
   }
}
