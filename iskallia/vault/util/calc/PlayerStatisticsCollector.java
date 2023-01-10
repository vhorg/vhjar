package iskallia.vault.util.calc;

import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.ItemUnidentifiedArtifact;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.network.message.PlayerStatisticsMessage;
import iskallia.vault.world.data.PlayerInfluences;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.player.VaultSpectator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber
public class PlayerStatisticsCollector {
   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player instanceof ServerPlayer sPlayer) {
         if (sPlayer.tickCount % 20 == 0) {
            CompoundTag reputationStats = new CompoundTag();

            for (VaultGod type : VaultGod.values()) {
               reputationStats.putInt(type.getName(), PlayerInfluences.getReputation(sPlayer.getUUID(), type));
            }

            CompoundTag serialized = new CompoundTag();
            serialized.put("reputation", reputationStats);
            PlayerInfluences.getFavour(sPlayer.getUUID()).ifPresent(god -> serialized.putString("favour", god.getName()));
            PlayerStatisticsMessage pkt = new PlayerStatisticsMessage(serialized);
            ModNetwork.CHANNEL.sendTo(pkt, sPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      }
   }

   public static class AttributeSnapshot {
      private final String unlocAttributeName;
      private final String parentAttributeName;
      private final double value;
      private final boolean isPercentage;
      private double limit = -1.0;

      public AttributeSnapshot(String unlocAttributeName, double value, boolean isPercentage) {
         this(unlocAttributeName, null, value, isPercentage);
      }

      public AttributeSnapshot(String unlocAttributeName, String parentAttributeName, double value, boolean isPercentage) {
         this.unlocAttributeName = unlocAttributeName;
         this.parentAttributeName = parentAttributeName;
         this.value = value;
         this.isPercentage = isPercentage;
      }

      private PlayerStatisticsCollector.AttributeSnapshot setLimit(double limit) {
         this.limit = limit;
         return this;
      }

      public String getAttributeName() {
         return this.unlocAttributeName;
      }

      public String getParentAttributeName() {
         return this.parentAttributeName != null ? this.parentAttributeName : this.getAttributeName();
      }

      public double getValue() {
         return this.value;
      }

      public boolean isPercentage() {
         return this.isPercentage;
      }

      public boolean hasLimit() {
         return this.limit != -1.0;
      }

      public double getLimit() {
         return this.limit;
      }

      public boolean hasHitLimit() {
         return this.hasLimit() && this.getValue() > this.getLimit();
      }

      public CompoundTag serialize() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("key", this.getAttributeName());
         nbt.putString("parent", this.getParentAttributeName());
         nbt.putDouble("value", this.getValue());
         nbt.putBoolean("isPercentage", this.isPercentage());
         nbt.putDouble("limit", this.getLimit());
         return nbt;
      }

      public static PlayerStatisticsCollector.AttributeSnapshot deserialize(CompoundTag nbt) {
         return new PlayerStatisticsCollector.AttributeSnapshot(
               nbt.getString("key"), nbt.getString("parent"), nbt.getDouble("value"), nbt.getBoolean("isPercentage")
            )
            .setLimit(nbt.getDouble("limit"));
      }
   }

   public static class VaultRunsSnapshot {
      public int vaultRuns;
      public int deaths;
      public int bails;
      public int bossKills;
      public int artifacts;
      public int raidsCompleted;

      public static PlayerStatisticsCollector.VaultRunsSnapshot ofPlayer(ServerPlayer sPlayer) {
         PlayerStatsData.Stats vaultPlayerStats = PlayerStatsData.get().get(sPlayer);
         PlayerStatisticsCollector.VaultRunsSnapshot snapshot = new PlayerStatisticsCollector.VaultRunsSnapshot();
         snapshot.vaultRuns = vaultPlayerStats.getVaults().size();

         for (VaultRaid recordedRaid : vaultPlayerStats.getVaults()) {
            boolean completedAll = true;

            for (VaultObjective objective : recordedRaid.getAllObjectives()) {
               for (VaultObjective.Crate crate : objective.getCrates()) {
                  for (ItemStack stack : crate.getContents()) {
                     if (stack.getItem() instanceof ItemUnidentifiedArtifact) {
                        snapshot.artifacts++;
                     }
                  }
               }

               if (objective instanceof RaidChallengeObjective) {
                  snapshot.raidsCompleted = snapshot.raidsCompleted + ((RaidChallengeObjective)objective).getCompletedRaids();
               }

               if (!objective.isCompleted()) {
                  completedAll = false;
                  break;
               }
            }

            if (completedAll) {
               snapshot.bossKills++;
            } else {
               CrystalData data = recordedRaid.getProperties().getBaseOrDefault(VaultRaid.CRYSTAL_DATA, CrystalData.EMPTY);
               CrystalData.Type vaultType = data.getType();
               if (vaultType != CrystalData.Type.COOP) {
                  for (VaultPlayer vPlayer : recordedRaid.getPlayers()) {
                     if (vPlayer.hasExited()) {
                        if (vPlayer instanceof VaultSpectator) {
                           snapshot.deaths++;
                        } else {
                           snapshot.bails++;
                        }
                        break;
                     }
                  }
               } else {
                  boolean done = true;
                  boolean areAllSpectators = true;

                  for (VaultPlayer vPlayerx : recordedRaid.getPlayers()) {
                     if (!vPlayerx.hasExited()) {
                        done = false;
                     }

                     if (vPlayerx instanceof VaultRunner) {
                        areAllSpectators = false;
                     }
                  }

                  if (done) {
                     if (areAllSpectators) {
                        snapshot.bails++;
                     } else {
                        snapshot.deaths++;
                     }
                  }
               }
            }
         }

         String uuidStr = sPlayer.getUUID().toString();
         if (uuidStr.equalsIgnoreCase("d974cbae-e62b-4e34-a1b8-0175a2d41d9a")) {
            snapshot.artifacts += 2;
         }

         if (uuidStr.equalsIgnoreCase("0f5e0db0-13a0-4125-a97a-e9f8e872d521")) {
            snapshot.artifacts += 2;
         }

         if (uuidStr.equalsIgnoreCase("5f820c39-5883-4392-b174-3125ac05e38c")) {
            snapshot.artifacts++;
         }

         if (uuidStr.equalsIgnoreCase("7ed3587b-e656-4689-90d6-08e11daaf907")) {
            snapshot.artifacts += 2;
         }

         return snapshot;
      }
   }
}
