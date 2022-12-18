package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.PhoenixModifierSnapshotData;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerInventoryRestoreModifier extends VaultModifier<PlayerInventoryRestoreModifier.Properties> {
   private static final String RESTORE_FLAG = "the_vault_restore_inventory";

   public PlayerInventoryRestoreModifier(ResourceLocation id, PlayerInventoryRestoreModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      Player player = event.player;
      if (player.isAlive() && player.level instanceof ServerLevel) {
         if (player.getTags().contains("the_vault_restore_inventory")) {
            ServerLevel world = (ServerLevel)event.player.level;
            PhoenixModifierSnapshotData data = PhoenixModifierSnapshotData.get(world);
            if (data.hasSnapshot(player)) {
               data.restoreSnapshot(player);
            }

            player.removeTag("the_vault_restore_inventory");
         }
      }
   }

   @Override
   public void onListenerAdd(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      listener.getPlayer().ifPresent(player -> {
         PhoenixModifierSnapshotData snapshotData = PhoenixModifierSnapshotData.get(player.getLevel());
         if (snapshotData.hasSnapshot(player)) {
            snapshotData.removeSnapshot(player);
         }

         snapshotData.createSnapshot(player);
      });
      vault.getOptional(Vault.STATS)
         .map(stats -> stats.get(listener))
         .ifPresent(stats -> stats.modify(StatCollector.EXP_MULTIPLIER, m -> m * this.properties().experienceMultiplierOnSuccess()));
   }

   @Override
   public void onListenerRemove(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      listener.getPlayer().ifPresent(player -> {
         PhoenixModifierSnapshotData snapshotData = PhoenixModifierSnapshotData.get(player.getLevel());
         if (snapshotData.hasSnapshot(player.getUUID())) {
            if (player.isDeadOrDying()) {
               player.getTags().add("the_vault_restore_inventory");
            } else {
               snapshotData.removeSnapshot(player.getUUID());
            }
         }
      });
      vault.ifPresent(Vault.STATS, stats -> {
         StatCollector statCollector = stats.get(listener);
         if (statCollector != null && statCollector.getCompletion() == Completion.FAILED) {
            statCollector.modify(StatCollector.EXP_MULTIPLIER, m -> m * this.properties.experienceMultiplierOnDeath());
         }
      });
   }

   @Override
   public void onVaultAdd(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.ifPresent(Vault.OBJECTIVES, objectives -> objectives.forEach(DeathObjective.class, deathObjective -> {
         deathObjective.modify(DeathObjective.KILL_ALL_STACK, i -> i + 1);
         return false;
      }));
   }

   @Override
   public void onVaultRemove(VirtualWorld world, Vault vault, ModifierContext context) {
      vault.ifPresent(Vault.OBJECTIVES, objectives -> objectives.forEach(DeathObjective.class, deathObjective -> {
         deathObjective.modify(DeathObjective.KILL_ALL_STACK, i -> i - 1);
         return false;
      }));
   }

   public static class Properties {
      @Expose
      private final boolean preventsArtifact;
      @Expose
      private final float experienceMultiplierOnDeath;
      @Expose
      private final float experienceMultiplierOnSuccess;

      public Properties(boolean preventsArtifact, float experienceMultiplierOnDeath, float experienceMultiplierOnSuccess) {
         this.preventsArtifact = preventsArtifact;
         this.experienceMultiplierOnDeath = experienceMultiplierOnDeath;
         this.experienceMultiplierOnSuccess = experienceMultiplierOnSuccess;
      }

      public boolean preventsArtifact() {
         return this.preventsArtifact;
      }

      public float experienceMultiplierOnDeath() {
         return this.experienceMultiplierOnDeath;
      }

      public float experienceMultiplierOnSuccess() {
         return this.experienceMultiplierOnSuccess;
      }
   }
}
