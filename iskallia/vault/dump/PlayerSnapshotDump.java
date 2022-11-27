package iskallia.vault.dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.util.calc.CooldownHelper;
import iskallia.vault.util.calc.FatalStrikeHelper;
import iskallia.vault.util.calc.PlayerStatisticsCollector;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.util.calc.ThornsHelper;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.ServerVaults;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PlayerSnapshotDump {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   public static String createAndSerializeSnapshot(ServerPlayer sPlayer) {
      return GSON.toJson(createSnapshot(sPlayer));
   }

   public static PlayerSnapshotDump.PlayerSnapshot createSnapshot(ServerPlayer sPlayer) {
      PlayerSnapshotDump.PlayerSnapshot snapshot = new PlayerSnapshotDump.PlayerSnapshot(sPlayer);
      ServerLevel sWorld = sPlayer.getLevel();
      snapshot.inVault = ServerVaults.isInVault(sPlayer);
      PlayerVaultStats stats = PlayerVaultStatsData.get(sWorld).getVaultStats(sPlayer);
      snapshot.vaultLevel = stats.getVaultLevel();
      if (snapshot.vaultLevel >= ModConfigs.LEVELS_META.getMaxLevel()) {
         snapshot.levelPercent = 1.0F;
      } else {
         snapshot.levelPercent = (float)stats.getExp() / stats.getExpNeededToNextLevel();
      }

      AttributeMap mgr = sPlayer.getAttributes();

      for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {
         if (mgr.hasAttribute(attribute)) {
            ResourceLocation attrId = attribute.getRegistryName();
            snapshot.attributes.put(attrId == null ? attribute.getDescriptionId() : attrId.toString(), mgr.getValue(attribute));
         }
      }

      snapshot.blockChance = BlockChanceHelper.getBlockChance(sPlayer);
      snapshot.resistance = ResistanceHelper.getResistance(sPlayer);
      snapshot.cooldownReduction = CooldownHelper.getCooldownMultiplier(sPlayer);
      snapshot.fatalStrikeChance = FatalStrikeHelper.getFatalStrikeChance(sPlayer);
      snapshot.fatalStrikeDamage = FatalStrikeHelper.getFatalStrikeDamage(sPlayer);
      snapshot.thornsChance = ThornsHelper.getThornsChance(sPlayer);
      snapshot.thornsDamage = ThornsHelper.getThornsDamage(sPlayer);
      Arrays.stream(EquipmentSlot.values()).forEach(slotType -> {
         ItemStack stack = sPlayer.getItemBySlot(slotType);
         if (!stack.isEmpty()) {
            snapshot.equipment.put(slotType.name(), new PlayerSnapshotDump.SerializableItemStack(stack));
         }
      });
      AbilityTree abilities = PlayerAbilitiesData.get(sWorld).getAbilities(sPlayer);
      abilities.getLearnedNodes().forEach(node -> {
         if (node.getSpecialization() != null) {
            snapshot.abilities.put(node.getGroup().getParentName() + ": " + node.getSpecializationName(), node.getLevel());
         } else {
            snapshot.abilities.put(node.getGroup().getParentName(), node.getLevel());
         }
      });
      TalentTree talents = PlayerTalentsData.get(sWorld).getTalents(sPlayer);
      talents.getLearnedNodes().forEach(node -> snapshot.talents.put(node.getGroup().getParentName(), node.getLevel()));
      ResearchTree researches = PlayerResearchesData.get(sWorld).getResearches(sPlayer);
      snapshot.researches.addAll(researches.getResearchesDone());
      PlayerStatisticsCollector.VaultRunsSnapshot vaultRunsSnapshot = PlayerStatisticsCollector.VaultRunsSnapshot.ofPlayer(sPlayer);
      snapshot.vaultRuns = vaultRunsSnapshot.vaultRuns;
      snapshot.vaultWins = vaultRunsSnapshot.bossKills;
      snapshot.vaultDeaths = vaultRunsSnapshot.deaths;
      snapshot.artifactCount = vaultRunsSnapshot.artifacts;
      snapshot.powerLevel = stats.getTotalSpentSkillPoints() + stats.getUnspentSkillPoints();
      PlayerFavourData favourData = PlayerFavourData.get(sWorld);

      for (PlayerFavourData.VaultGodType type : PlayerFavourData.VaultGodType.values()) {
         snapshot.favors.put(type.getName(), favourData.getFavour(sPlayer.getUUID(), type));
      }

      EternalsData.EternalGroup group = EternalsData.get(sWorld).getEternals(sPlayer);

      for (EternalData eternal : group.getEternals()) {
         String auraName = null;
         if (eternal.getAbilityName() != null) {
            EternalAuraConfig.AuraConfig cfg = ModConfigs.ETERNAL_AURAS.getByName(eternal.getAbilityName());
            if (cfg != null) {
               auraName = cfg.getDisplayName();
            }
         }

         PlayerSnapshotDump.EternalInformation eternalSnapshot = new PlayerSnapshotDump.EternalInformation(
            eternal.getName(), eternal.getLevel(), eternal.isAncient(), auraName
         );
         eternal.getEquipment().forEach((slot, stack) -> {
            if (!stack.isEmpty()) {
               eternalSnapshot.equipment.put(slot.name(), new PlayerSnapshotDump.SerializableItemStack(stack));
            }
         });
         snapshot.eternals.add(eternalSnapshot);
      }

      return snapshot;
   }

   public static class EternalInformation {
      private final String name;
      private final int level;
      private final boolean isAncient;
      private final String auraName;
      private Map<String, PlayerSnapshotDump.SerializableItemStack> equipment = new LinkedHashMap<>();

      public EternalInformation(String name, int level, boolean isAncient, String auraName) {
         this.name = name;
         this.level = level;
         this.isAncient = isAncient;
         this.auraName = auraName;
      }
   }

   public static class PlayerSnapshot {
      protected final UUID playerUUID;
      protected final String playerNickname;
      protected final long timestamp;
      protected int vaultRuns;
      protected int vaultWins;
      protected int vaultDeaths;
      protected int artifactCount;
      protected boolean inVault = false;
      protected int powerLevel = 0;
      protected int vaultLevel = 0;
      protected float levelPercent = 0.0F;
      protected Map<String, Double> attributes = new LinkedHashMap<>();
      protected Map<String, Integer> favors = new LinkedHashMap<>();
      protected float blockChance;
      protected float resistance;
      protected float cooldownReduction;
      protected float fatalStrikeChance;
      protected float fatalStrikeDamage;
      protected float thornsChance;
      protected float thornsDamage;
      protected Map<String, PlayerSnapshotDump.SerializableItemStack> equipment = new LinkedHashMap<>();
      protected Map<String, Integer> abilities = new LinkedHashMap<>();
      protected Map<String, Integer> talents = new LinkedHashMap<>();
      protected Set<String> researches = new LinkedHashSet<>();
      protected Set<PlayerSnapshotDump.EternalInformation> eternals = new LinkedHashSet<>();

      public PlayerSnapshot(ServerPlayer playerEntity) {
         this.playerUUID = playerEntity.getUUID();
         this.playerNickname = playerEntity.getName().getString();
         this.timestamp = Instant.now().getEpochSecond();
      }
   }

   public static class SerializableItemStack {
      private final String itemKey;
      private final int count;
      private final String nbt;
      private final String gearData;

      private SerializableItemStack(ItemStack stack) {
         this.itemKey = stack.getItem().getRegistryName().toString();
         this.count = stack.getCount();
         if (stack.hasTag()) {
            this.nbt = stack.getTag().toString();
         } else {
            this.nbt = null;
         }

         this.gearData = VaultGearItem.serializeGearData(stack).toString();
      }
   }
}
