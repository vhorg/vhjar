package iskallia.vault.world.vault.builder;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayerType;
import java.util.Optional;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class FinalBossBuilder extends VaultRaidBuilder {
   private static final FinalBossBuilder INSTANCE = new FinalBossBuilder();

   private FinalBossBuilder() {
   }

   public static FinalBossBuilder getInstance() {
      return INSTANCE;
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerWorld world, ServerPlayerEntity player, CrystalData crystal) {
      VaultRaid.Builder builder = this.getDefaultBuilder(crystal, world, player);
      VaultRaid vault = VaultRaidData.get(world).getActiveFor(player);
      if (vault == null) {
         return null;
      } else {
         vault.getPlayers()
            .stream()
            .map(p -> p.getServerPlayer(world.func_73046_m()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(sPlayer -> builder.addPlayer(VaultPlayerType.RUNNER, sPlayer));
         builder.set(VaultRaid.HOST, player.func_110124_au());
         builder.setGenerator(VaultRaid.FINAL_BOSS);
         return builder;
      }
   }
}
