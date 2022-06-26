package iskallia.vault.util.calc;

import com.google.common.collect.Lists;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.ItemUnidentifiedArtifact;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.network.message.PlayerStatisticsMessage;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.behaviour.VaultBehaviour;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.player.VaultSpectator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class PlayerStatisticsCollector {
   private static final Supplier<List<Attribute>> displayedAttributes = () -> Lists.newArrayList(
      new Attribute[]{
         Attributes.field_233818_a_,
         Attributes.field_233823_f_,
         Attributes.field_233825_h_,
         Attributes.field_233826_i_,
         Attributes.field_233827_j_,
         Attributes.field_233820_c_,
         Attributes.field_233828_k_,
         (Attribute)ForgeMod.REACH_DISTANCE.get(),
         Attributes.field_233821_d_
      }
   );

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)event.player;
         if (sPlayer.field_70173_aa % 20 == 0) {
            TalentTree talents = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
            List<PlayerStatisticsCollector.AttributeSnapshot> snapshots = new ArrayList<>();
            List<Attribute> collectingAttributes = displayedAttributes.get();

            for (Attribute attribute : collectingAttributes) {
               double value = sPlayer.func_110148_a(attribute).func_111126_e();
               if (attribute == Attributes.field_233821_d_) {
                  value *= 10.0;
               }

               if (attribute == Attributes.field_233820_c_) {
                  value *= 100.0;
               }

               snapshots.add(new PlayerStatisticsCollector.AttributeSnapshot(attribute.func_233754_c_(), value, attribute == Attributes.field_233820_c_));
            }

            float parry = ParryHelper.getPlayerParryChanceUnlimited(sPlayer) * 100.0F;
            snapshots.add(
               collectingAttributes.indexOf(Attributes.field_233820_c_),
               new PlayerStatisticsCollector.AttributeSnapshot("stat.the_vault.parry", parry, true)
                  .setLimit((double)(AttributeLimitHelper.getParryLimit(sPlayer) * 100.0F))
            );
            float resistance = ResistanceHelper.getPlayerResistancePercentUnlimited(sPlayer) * 100.0F;
            snapshots.add(
               collectingAttributes.indexOf(Attributes.field_233820_c_),
               new PlayerStatisticsCollector.AttributeSnapshot("stat.the_vault.resistance", resistance, true)
                  .setLimit((double)(AttributeLimitHelper.getResistanceLimit(sPlayer) * 100.0F))
            );
            if (talents.hasLearnedNode(ModConfigs.TALENTS.COMMANDER)) {
               float summonEternalCooldown = CooldownHelper.getCooldownMultiplierUnlimited(sPlayer, ModConfigs.ABILITIES.SUMMON_ETERNAL) * 100.0F;
               snapshots.add(
                  collectingAttributes.indexOf(Attributes.field_233820_c_),
                  new PlayerStatisticsCollector.AttributeSnapshot(
                        "stat.the_vault.cooldown_summoneternal", "stat.the_vault.cooldown", summonEternalCooldown, true
                     )
                     .setLimit((double)(AttributeLimitHelper.getCooldownReductionLimit(sPlayer) * 100.0F))
               );
            }

            float cooldown = CooldownHelper.getCooldownMultiplierUnlimited(sPlayer, null) * 100.0F;
            snapshots.add(
               collectingAttributes.indexOf(Attributes.field_233820_c_),
               new PlayerStatisticsCollector.AttributeSnapshot("stat.the_vault.cooldown", cooldown, true)
                  .setLimit((double)(AttributeLimitHelper.getCooldownReductionLimit(sPlayer) * 100.0F))
            );
            snapshots.add(
               new PlayerStatisticsCollector.AttributeSnapshot("stat.the_vault.chest_rarity", ChestRarityHelper.getIncreasedChestRarity(sPlayer) * 100.0F, true)
            );
            snapshots.add(
               new PlayerStatisticsCollector.AttributeSnapshot("stat.the_vault.thorns_chance", ThornsHelper.getPlayerThornsChance(sPlayer) * 100.0F, true)
            );
            snapshots.add(
               new PlayerStatisticsCollector.AttributeSnapshot("stat.the_vault.thorns_damage", ThornsHelper.getPlayerThornsDamage(sPlayer) * 100.0F, true)
            );
            snapshots.add(
               new PlayerStatisticsCollector.AttributeSnapshot(
                  "stat.the_vault.fatal_strike_chance", FatalStrikeHelper.getPlayerFatalStrikeChance(sPlayer) * 100.0F, true
               )
            );
            snapshots.add(
               new PlayerStatisticsCollector.AttributeSnapshot(
                  "stat.the_vault.fatal_strike_damage", FatalStrikeHelper.getPlayerFatalStrikeDamage(sPlayer) * 100.0F, true
               )
            );
            CompoundNBT vaultStats = new CompoundNBT();
            PlayerVaultStatsData vaultStatsData = PlayerVaultStatsData.get(sPlayer.func_71121_q());
            PlayerStatsData.Stats vaultPlayerStats = PlayerStatsData.get(sPlayer.func_71121_q()).get(sPlayer);
            PlayerFavourData favourData = PlayerFavourData.get(sPlayer.func_71121_q());
            UUID playerUUID = sPlayer.func_110124_au();
            PlayerVaultStats stats = vaultStatsData.getVaultStats(playerUUID);
            PlayerStatisticsCollector.VaultRunsSnapshot vaultRunsSnapshot = PlayerStatisticsCollector.VaultRunsSnapshot.ofPlayer(sPlayer);
            vaultStats.func_218657_a("fastestVault", vaultStatsData.getFastestVaultTime().serialize());
            vaultStats.func_74768_a("powerLevel", stats.getTotalSpentSkillPoints() + stats.getUnspentSkillPts());
            vaultStats.func_74768_a("knowledgeLevel", stats.getTotalSpentKnowledgePoints() + stats.getUnspentKnowledgePts());
            vaultStats.func_74768_a("crystalsCrafted", vaultPlayerStats.getCrystals().size());
            vaultStats.func_74768_a("vaultArtifacts", vaultRunsSnapshot.artifacts);
            vaultStats.func_74768_a("vaultTotal", vaultRunsSnapshot.vaultRuns);
            vaultStats.func_74768_a("vaultDeaths", vaultRunsSnapshot.deaths);
            vaultStats.func_74768_a("vaultBails", vaultRunsSnapshot.bails);
            vaultStats.func_74768_a("vaultBossKills", vaultRunsSnapshot.bossKills);
            vaultStats.func_74768_a("vaultRaids", vaultRunsSnapshot.raidsCompleted);
            CompoundNBT favourStats = new CompoundNBT();

            for (PlayerFavourData.VaultGodType type : PlayerFavourData.VaultGodType.values()) {
               favourStats.func_218657_a(type.name(), IntNBT.func_229692_a_(favourData.getFavour(playerUUID, type)));
            }

            CompoundNBT serialized = new CompoundNBT();
            ListNBT snapshotList = new ListNBT();
            snapshots.forEach(snapshot -> snapshotList.add(snapshot.serialize()));
            serialized.func_218657_a("attributes", snapshotList);
            serialized.func_218657_a("vaultStats", vaultStats);
            serialized.func_218657_a("favourStats", favourStats);
            PlayerStatisticsMessage pkt = new PlayerStatisticsMessage(serialized);
            ModNetwork.CHANNEL.sendTo(pkt, sPlayer.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
         }
      }
   }

   public static int getFinishedRaids(MinecraftServer srv, UUID playerId) {
      if (!ModConfigs.RAID_EVENT_CONFIG.isEnabled()) {
         return -1;
      } else {
         PlayerStatsData.Stats stats = PlayerStatsData.get(srv).get(playerId);
         if (stats.hasFinishedRaidReward()) {
            return -1;
         } else {
            int completedRaids = 0;

            for (VaultRaid recordedRaid : stats.getVaults()) {
               for (VaultObjective objective : recordedRaid.getAllObjectives()) {
                  if (objective instanceof RaidChallengeObjective) {
                     completedRaids += ((RaidChallengeObjective)objective).getCompletedRaids();
                  }
               }
            }

            return completedRaids;
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

      public CompoundNBT serialize() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("key", this.getAttributeName());
         nbt.func_74778_a("parent", this.getParentAttributeName());
         nbt.func_74780_a("value", this.getValue());
         nbt.func_74757_a("isPercentage", this.isPercentage());
         nbt.func_74780_a("limit", this.getLimit());
         return nbt;
      }

      public static PlayerStatisticsCollector.AttributeSnapshot deserialize(CompoundNBT nbt) {
         return new PlayerStatisticsCollector.AttributeSnapshot(
               nbt.func_74779_i("key"), nbt.func_74779_i("parent"), nbt.func_74769_h("value"), nbt.func_74767_n("isPercentage")
            )
            .setLimit(nbt.func_74769_h("limit"));
      }
   }

   public static class VaultRunsSnapshot {
      public int vaultRuns;
      public int deaths;
      public int bails;
      public int bossKills;
      public int artifacts;
      public int raidsCompleted;

      public static PlayerStatisticsCollector.VaultRunsSnapshot ofPlayer(ServerPlayerEntity sPlayer) {
         PlayerStatsData.Stats vaultPlayerStats = PlayerStatsData.get(sPlayer.func_71121_q()).get(sPlayer);
         PlayerStatisticsCollector.VaultRunsSnapshot snapshot = new PlayerStatisticsCollector.VaultRunsSnapshot();
         snapshot.vaultRuns = vaultPlayerStats.getVaults().size();

         for (VaultRaid recordedRaid : vaultPlayerStats.getVaults()) {
            boolean completedAll = true;

            for (VaultObjective objective : recordedRaid.getAllObjectives()) {
               for (VaultObjective.Crate crate : objective.getCrates()) {
                  for (ItemStack stack : crate.getContents()) {
                     if (stack.func_77973_b() instanceof ItemUnidentifiedArtifact) {
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
               boolean isOldClassic = false;
               if (recordedRaid.getPlayers().size() == 1) {
                  VaultPlayer player = recordedRaid.getPlayers().get(0);
                  VaultBehaviour behaviour = player.getBehaviours().get(0);
                  ResourceLocation id = behaviour.getTask().getId();
                  if (VaultRaid.RUNNER_TO_SPECTATOR.getId().equals(id)) {
                     isOldClassic = true;
                  }
               }

               if (!isOldClassic && vaultType != CrystalData.Type.TROVE && vaultType != CrystalData.Type.RAFFLE) {
                  boolean done = true;
                  boolean areAllSpectators = true;

                  for (VaultPlayer vPlayer : recordedRaid.getPlayers()) {
                     if (!vPlayer.hasExited()) {
                        done = false;
                     }

                     if (vPlayer instanceof VaultRunner) {
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
               } else {
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
               }
            }
         }

         return snapshot;
      }
   }
}
