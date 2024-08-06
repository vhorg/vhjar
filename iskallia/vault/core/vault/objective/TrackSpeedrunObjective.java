package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TrackSpeedrunObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("track_speedrun", Objective.class).with(Version.v1_0, TrackSpeedrunObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Void> INITIALIZED = FieldKey.of("initialized", Void.class).with(Version.v1_0, Adapters.ofVoid(), DISK.all()).register(FIELDS);

   public static TrackSpeedrunObjective create() {
      return new TrackSpeedrunObjective();
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.LISTENER_LEAVE.register(this, event -> {
         if (event.getListener() instanceof Runner runner) {
            if (vault.get(Vault.STATS).get(runner).getCompletion() == Completion.COMPLETED) {
               PlayerVaultStatsData data = PlayerVaultStatsData.get(world);
               runner.getPlayer().ifPresent(player -> data.updateFastestVaultTime(player, vault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME)));
            }
         }
      });
      vault.get(Vault.LISTENERS).get(Listeners.LOGIC).set(ClassicListenersLogic.ADDED_BONUS_TIME);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      PlayerVaultStatsData data = PlayerVaultStatsData.get(world);
      PlayerVaultStatsData.PlayerRecordEntry entry = data.getFastestVaultTime();
      if (!this.has(INITIALIZED)) {
         vault.get(Vault.CLOCK).set(TickClock.DISPLAY_TIME, Integer.valueOf(entry.getTickCount()));
         this.set(INITIALIZED);
      }

      vault.get(Vault.OBJECTIVES).forEach(KillBossObjective.class, objective -> {
         objective.set(KillBossObjective.BOSS_TYPE, ModEntities.ARENA_BOSS.getRegistryName());
         objective.set(KillBossObjective.BOSS_NAME, entry.getPlayerName());
         return false;
      });
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      return objective == this;
   }
}
