package iskallia.vault.dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.Vault;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.calc.CooldownHelper;
import iskallia.vault.util.calc.FatalStrikeHelper;
import iskallia.vault.util.calc.ParryHelper;
import iskallia.vault.util.calc.PlayerStatisticsCollector;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.util.calc.ThornsHelper;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class PlayerSnapshotDump {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   public static String createAndSerializeSnapshot(ServerPlayerEntity sPlayer) {
      return GSON.toJson(createSnapshot(sPlayer));
   }

   public static PlayerSnapshotDump.PlayerSnapshot createSnapshot(ServerPlayerEntity sPlayer) {
      PlayerSnapshotDump.PlayerSnapshot snapshot = new PlayerSnapshotDump.PlayerSnapshot(sPlayer);
      ServerWorld sWorld = sPlayer.func_71121_q();
      snapshot.inVault = sWorld.func_234923_W_() == Vault.VAULT_KEY;
      PlayerVaultStats stats = PlayerVaultStatsData.get(sWorld).getVaultStats(sPlayer);
      snapshot.vaultLevel = stats.getVaultLevel();
      snapshot.levelPercent = (float)stats.getExp() / stats.getTnl();
      AttributeModifierManager mgr = sPlayer.func_233645_dx_();

      for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {
         if (mgr.func_233790_b_(attribute)) {
            ResourceLocation attrId = attribute.getRegistryName();
            snapshot.attributes.put(attrId == null ? attribute.func_233754_c_() : attrId.toString(), mgr.func_233795_c_(attribute));
         }
      }

      snapshot.parry = ParryHelper.getPlayerParryChance(sPlayer);
      snapshot.resistance = ResistanceHelper.getPlayerResistancePercent(sPlayer);
      snapshot.cooldownReduction = CooldownHelper.getCooldownMultiplier(sPlayer, null);
      snapshot.fatalStrikeChance = FatalStrikeHelper.getPlayerFatalStrikeChance(sPlayer);
      snapshot.fatalStrikeDamage = FatalStrikeHelper.getPlayerFatalStrikeDamage(sPlayer);
      snapshot.thornsChance = ThornsHelper.getThornsChance(sPlayer);
      snapshot.thornsDamage = ThornsHelper.getThornsDamage(sPlayer);
      Arrays.stream(EquipmentSlotType.values()).forEach(slotType -> {
         ItemStack stack = sPlayer.func_184582_a(slotType);
         if (!stack.func_190926_b()) {
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
      snapshot.powerLevel = stats.getTotalSpentSkillPoints() + stats.getUnspentSkillPts();
      PlayerFavourData favourData = PlayerFavourData.get(sWorld);

      for (PlayerFavourData.VaultGodType type : PlayerFavourData.VaultGodType.values()) {
         snapshot.favors.put(type.getName(), favourData.getFavour(sPlayer.func_110124_au(), type));
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
            if (!stack.func_190926_b()) {
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
      protected float parry;
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

      public PlayerSnapshot(ServerPlayerEntity playerEntity) {
         this.playerUUID = playerEntity.func_110124_au();
         this.playerNickname = playerEntity.func_200200_C_().getString();
         this.timestamp = Instant.now().getEpochSecond();
      }
   }

   public static class SerializableItemStack {
      private final String itemKey;
      private final int count;
      private final String nbt;

      private SerializableItemStack(ItemStack stack) {
         this.itemKey = stack.func_77973_b().getRegistryName().toString();
         this.count = stack.func_190916_E();
         if (stack.func_77942_o()) {
            this.nbt = stack.func_77978_p().toString();
         } else {
            this.nbt = null;
         }
      }
   }
}
