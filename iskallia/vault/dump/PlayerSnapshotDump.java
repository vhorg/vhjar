package iskallia.vault.dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.AbilityPowerHelper;
import iskallia.vault.util.calc.AttributeLimitHelper;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.util.calc.CooldownHelper;
import iskallia.vault.util.calc.GodAffinityHelper;
import iskallia.vault.util.calc.LuckyHitHelper;
import iskallia.vault.util.calc.PlayerStatisticsCollector;
import iskallia.vault.util.calc.SoulChanceHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerReputationData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.SkillAltarData;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

public class PlayerSnapshotDump {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   public static String createAndSerializeSnapshot(ServerPlayer sPlayer) {
      return GSON.toJson(createSnapshot(sPlayer));
   }

   public static PlayerSnapshotDump.PlayerSnapshot createSnapshot(ServerPlayer player) {
      PlayerSnapshotDump.PlayerSnapshot snapshot = new PlayerSnapshotDump.PlayerSnapshot(player);
      ServerLevel world = player.getLevel();
      PlayerVaultStats stats = PlayerVaultStatsData.get(world).getVaultStats(player);
      snapshot.vaultLevel = stats.getVaultLevel();
      if (snapshot.vaultLevel >= ModConfigs.LEVELS_META.getMaxLevel()) {
         snapshot.levelPercent = 1.0F;
      } else {
         snapshot.levelPercent = (float)stats.getExp() / stats.getExpNeededToNextLevel();
      }

      AttributeMap mgr = player.getAttributes();

      for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {
         if (mgr.hasAttribute(attribute)) {
            ResourceLocation attrId = attribute.getRegistryName();
            snapshot.vanillaAttributes.put(attrId == null ? attribute.getDescriptionId() : attrId.toString(), mgr.getValue(attribute));
         }
      }

      AttributeSnapshot attributeSnapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      snapshot.putGearAttribute(ModGearAttributes.ABILITY_POWER, () -> AbilityPowerHelper.getAbilityPower(player));
      snapshot.putGearAttribute(ModGearAttributes.HEALING_EFFECTIVENESS, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.COOLDOWN_REDUCTION, () -> CooldownHelper.getCooldownMultiplier(player));
      snapshot.putGearAttribute(ModGearAttributes.RESISTANCE, () -> AttributeLimitHelper.getResistanceLimit(player));
      snapshot.putGearAttribute(ModGearAttributes.BLOCK, () -> BlockChanceHelper.getBlockChance(player));
      snapshot.putGearAttribute(ModGearAttributes.CRITICAL_HIT_TAKEN_REDUCTION, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.DURABILITY_WEAR_REDUCTION, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.THORNS_DAMAGE_FLAT, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.ON_HIT_CHAIN, VaultGearAttributeTypeMerger.intSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.ON_HIT_STUN, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.SHOCKING_HIT_CHANCE, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.SWEEPING_HIT_CHANCE, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.ITEM_QUANTITY, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.ITEM_RARITY, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.TRAP_DISARMING, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.COPIOUSLY, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.MINING_SPEED, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.SOUL_CHANCE, () -> SoulChanceHelper.getSoulChance(player));
      snapshot.putGearAttribute(ModGearAttributes.LUCKY_HIT_CHANCE, () -> LuckyHitHelper.getLuckyHitChance(player));
      snapshot.putGearAttribute(ModGearAttributes.DAMAGE_INCREASE, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.DAMAGE_ILLAGERS, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.DAMAGE_SPIDERS, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.DAMAGE_UNDEAD, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.DAMAGE_NETHER, VaultGearAttributeTypeMerger.floatSum(), attributeSnapshot);
      snapshot.putGearAttribute(ModGearAttributes.VELARA_AFFINITY, () -> GodAffinityHelper.getAffinityPercent(player, VaultGod.VELARA));
      snapshot.putGearAttribute(ModGearAttributes.VELARA_AFFINITY, () -> GodAffinityHelper.getAffinityPercent(player, VaultGod.TENOS));
      snapshot.putGearAttribute(ModGearAttributes.VELARA_AFFINITY, () -> GodAffinityHelper.getAffinityPercent(player, VaultGod.WENDARR));
      snapshot.putGearAttribute(ModGearAttributes.VELARA_AFFINITY, () -> GodAffinityHelper.getAffinityPercent(player, VaultGod.IDONA));
      Arrays.stream(EquipmentSlot.values()).forEach(slotType -> {
         ItemStack stack = player.getItemBySlot(slotType);
         if (!stack.isEmpty()) {
            snapshot.equipment.put(slotType.name(), new PlayerSnapshotDump.SerializableItemStack(stack));
         }
      });
      CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(handler -> {
         for (String slot : Arrays.asList("charm", "blue_trinket", "red_trinket", "belt")) {
            handler.getStacksHandler(slot).ifPresent(stackHandler -> {
               for (int i = 0; i < stackHandler.getSlots(); i++) {
                  ItemStack stack = stackHandler.getStacks().getStackInSlot(i);
                  if (!stack.isEmpty()) {
                     snapshot.equipment.put(slot + (i == 0 ? "" : "_" + i), new PlayerSnapshotDump.SerializableItemStack(stack));
                  }
               }
            });
         }
      });
      PlayerAbilitiesData.get(world).getAbilities(player).iterate(Skill.class, skill -> {
         if (skill.isUnlocked() && skill instanceof TieredSkill tiered && skill.getId() != null) {
            snapshot.abilities.put(tiered.getId(), tiered.getUnmodifiedTier());
         }
      });
      PlayerTalentsData.get(world).getTalents(player).iterate(Skill.class, skill -> {
         if (skill.isUnlocked() && skill instanceof TieredSkill tiered && skill.getId() != null) {
            snapshot.talents.put(skill.getId(), tiered.getUnmodifiedTier());
         }
      });
      PlayerExpertisesData.get(world).getExpertises(player).iterate(Skill.class, skill -> {
         if (skill.isUnlocked() && skill instanceof TieredSkill tiered && skill.getId() != null) {
            snapshot.expertises.put(skill.getId(), tiered.getUnmodifiedTier());
         }
      });
      ResearchTree researches = PlayerResearchesData.get(world).getResearches(player);
      snapshot.researches.addAll(researches.getResearchesDone());
      SkillAltarData.get(world).getSkillTemplates(player.getUUID()).forEach((index, template) -> {
         JsonObject object = new JsonObject();
         JsonObject icon = new JsonObject();
         icon.addProperty("key", template.getIcon().key());
         icon.addProperty("isTalent", template.getIcon().isTalent());
         object.add("icon", icon);
         JsonObject abilities = new JsonObject();
         template.getAbilities().iterate(Skill.class, skill -> {
            if (skill.isUnlocked() && skill instanceof TieredSkill tiered && skill.getId() != null) {
               abilities.addProperty(skill.getId(), tiered.getUnmodifiedTier());
            }
         });
         JsonObject talents = new JsonObject();
         template.getTalents().iterate(Skill.class, skill -> {
            if (skill.isUnlocked() && skill instanceof TieredSkill tiered && skill.getId() != null) {
               talents.addProperty(skill.getId(), tiered.getUnmodifiedTier());
            }
         });
         object.add("abilities", abilities);
         object.add("talents", talents);
         snapshot.skillTemplates.add(object);
      });
      PlayerStatisticsCollector.VaultRunsSnapshot vaultRunsSnapshot = PlayerStatisticsCollector.VaultRunsSnapshot.ofPlayer(player);
      snapshot.completed = vaultRunsSnapshot.completed;
      snapshot.survived = vaultRunsSnapshot.survived;
      snapshot.failed = vaultRunsSnapshot.failed;

      for (VaultGod type : VaultGod.values()) {
         snapshot.reputation.put(type.getName(), PlayerReputationData.getReputation(player.getUUID(), type));
      }

      return snapshot;
   }

   public static class PlayerSnapshot {
      protected final UUID playerUUID;
      protected final String playerNickname;
      protected final long timestamp;
      protected int completed;
      protected int survived;
      protected int failed;
      protected int vaultLevel = 0;
      protected float levelPercent = 0.0F;
      protected Map<String, Double> vanillaAttributes = new LinkedHashMap<>();
      protected Map<String, JsonElement> gearAttributes = new LinkedHashMap<>();
      protected Map<String, Integer> reputation = new LinkedHashMap<>();
      protected Map<String, PlayerSnapshotDump.SerializableItemStack> equipment = new LinkedHashMap<>();
      protected Map<String, Integer> abilities = new LinkedHashMap<>();
      protected Map<String, Integer> talents = new LinkedHashMap<>();
      protected Map<String, Integer> expertises = new LinkedHashMap<>();
      protected Set<String> researches = new LinkedHashSet<>();
      protected List<JsonObject> skillTemplates = new ArrayList<>();

      public PlayerSnapshot(ServerPlayer playerEntity) {
         this.playerUUID = playerEntity.getUUID();
         this.playerNickname = playerEntity.getName().getString();
         this.timestamp = Instant.now().getEpochSecond();
      }

      public <V extends Comparable<V>> void putGearAttribute(VaultGearAttribute<V> attribute, Supplier<V> valueSupplier) {
         this.gearAttributes.put(attribute.getRegistryName().toString(), attribute.getType().serialize(valueSupplier.get()));
      }

      public <V extends Comparable<V>> void putGearAttribute(
         VaultGearAttribute<V> attribute, VaultGearAttributeTypeMerger<V, V> merger, AttributeSnapshot snapshot
      ) {
         this.putGearAttribute(attribute, () -> snapshot.getAttributeValue(attribute, merger));
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
