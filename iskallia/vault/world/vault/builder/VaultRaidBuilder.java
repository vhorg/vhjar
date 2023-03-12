package iskallia.vault.world.vault.builder;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.event.VaultEvent;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.task.VaultTask;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public abstract class VaultRaidBuilder {
   public abstract VaultRaid.Builder initializeBuilder(ServerLevel var1, ServerPlayer var2, CrystalData var3);

   protected int getVaultLevelForObjective(ServerLevel world, ServerPlayer player) {
      return player == null ? 0 : PlayerVaultStatsData.get(world).getVaultStats(player.getUUID()).getVaultLevel();
   }

   protected VaultRaid.Builder getDefaultBuilder(CrystalData crystal, ServerLevel world, ServerPlayer player) {
      VaultObjective vObjective = null;
      return this.getDefaultBuilder(crystal, this.getVaultLevelForObjective(world, player), vObjective);
   }

   protected VaultRaid.Builder getDefaultBuilder(CrystalData crystal, int vaultLevel, @Nullable VaultObjective objective) {
      return VaultRaid.builder(null, vaultLevel, objective)
         .setInitializer(this.getDefaultInitializer())
         .addEvents(this.getDefaultEvents())
         .set(VaultRaid.CRYSTAL_DATA, crystal)
         .set(VaultRaid.IDENTIFIER, UUID.randomUUID());
   }

   protected VaultTask getDefaultInitializer() {
      return VaultRaid.TP_TO_START
         .then(VaultRaid.INIT_SANDS_EVENT)
         .then(VaultRaid.INIT_COW_VAULT)
         .then(VaultRaid.INIT_GLOBAL_MODIFIERS)
         .then(VaultRaid.ENTER_DISPLAY)
         .then(VaultRaid.INIT_RELIC_TIME);
   }

   protected Collection<VaultEvent<?>> getDefaultEvents() {
      return Arrays.asList(
         VaultRaid.SCALE_MOB,
         VaultRaid.SCALE_MOB_JOIN,
         VaultRaid.BLOCK_NATURAL_SPAWNING,
         VaultRaid.PREVENT_ITEM_PICKUP,
         VaultRaid.APPLY_SCALE_MODIFIER,
         VaultRaid.APPLY_FRENZY_MODIFIERS,
         VaultRaid.APPLY_MOB_ATTRIBUTE_MODIFIERS,
         VaultRaid.APPLY_INFLUENCE_MODIFIERS
      );
   }
}
