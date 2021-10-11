package iskallia.vault.world.vault.builder;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayerType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class TroveVaultBuilder extends VaultRaidBuilder {
   private static final TroveVaultBuilder INSTANCE = new TroveVaultBuilder();

   private TroveVaultBuilder() {
   }

   public static TroveVaultBuilder getInstance() {
      return INSTANCE;
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerWorld world, ServerPlayerEntity player, CrystalData crystal) {
      VaultRaid.Builder builder = this.getDefaultBuilder(crystal, world, player);
      builder.addPlayer(VaultPlayerType.RUNNER, player);
      builder.set(VaultRaid.HOST, player.func_110124_au());
      return builder;
   }

   @Override
   protected VaultRaid.Builder getDefaultBuilder(CrystalData crystal, ServerWorld world, ServerPlayerEntity player) {
      return super.getDefaultBuilder(crystal, 0, VaultRaid.VAULT_TROVE.get());
   }
}