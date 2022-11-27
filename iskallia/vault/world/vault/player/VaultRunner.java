package iskallia.vault.world.vault.player;

import iskallia.vault.VaultMod;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.SummonAndKillBossObjective;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class VaultRunner extends VaultPlayer {
   public static final ResourceLocation ID = VaultMod.id("runner");

   public VaultRunner() {
   }

   public VaultRunner(UUID playerId) {
      this(ID, playerId);
   }

   public VaultRunner(ResourceLocation id, UUID playerId) {
      super(id, playerId);
   }

   @Override
   public void tickTimer(VaultRaid vault, ServerLevel world, VaultTimer timer) {
      timer.tick();
      this.runIfPresent(world.getServer(), player -> {
         this.addedExtensions.clear();
         this.appliedExtensions.clear();
      });
   }

   @Override
   public void tickObjectiveUpdates(VaultRaid vault, ServerLevel world) {
      this.runIfPresent(world.getServer(), player -> {
         boolean earlyKill = false;
         if (vault.hasActiveObjective(this, SummonAndKillBossObjective.class)) {
            boolean isRaffle = vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false);
            if (isRaffle) {
               PlayerVaultStatsData.PlayerRecordEntry fastestVault = PlayerVaultStatsData.get(world).getFastestVaultTime();
               earlyKill = this.timer.getRunTime() < fastestVault.getTickCount();
            }
         }

         boolean showTimer = this.getProperties().getBaseOrDefault(VaultRaid.SHOW_TIMER, true);
         this.sendIfPresent(world.getServer(), VaultOverlayMessage.forVault(!showTimer ? 0 : this.timer.getTimeLeft(), earlyKill, showTimer));
      });
   }
}
