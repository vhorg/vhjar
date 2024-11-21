package iskallia.vault.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.JsonOps;
import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.entity.boss.MobSpawningUtils;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.attack.PersistentMeleeAttackGoal;
import iskallia.vault.entity.boss.goal.BloodOrbGoal;
import iskallia.vault.entity.boss.goal.CobwebRangedAttackGoal;
import iskallia.vault.entity.boss.goal.EvokerFangsGoal;
import iskallia.vault.entity.boss.goal.FireballRangedAttackGoal;
import iskallia.vault.entity.boss.goal.GolemHandRangedAttackGoal;
import iskallia.vault.entity.boss.goal.HealGoal;
import iskallia.vault.entity.boss.goal.PlaceBlockAroundGoal;
import iskallia.vault.entity.boss.goal.PotionAuraGoal;
import iskallia.vault.entity.boss.goal.ShulkerAttackGoal;
import iskallia.vault.entity.boss.goal.SnowballRangedAttackGoal;
import iskallia.vault.entity.boss.goal.SummonAtTargetGoal;
import iskallia.vault.entity.boss.goal.SummonGoal;
import iskallia.vault.entity.boss.goal.ThrowPotionGoal;
import iskallia.vault.entity.boss.trait.ApplyPotionOnHitEffect;
import iskallia.vault.entity.boss.trait.AttributeModifierTrait;
import iskallia.vault.entity.boss.trait.LifeLeechOnHitEffect;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class VaultBossConfig extends Config {
   @Expose
   @SerializedName("Bosses")
   private Map<ResourceLocation, VaultBossConfig.BossDefinition> bosses;
   @Expose
   @SerializedName("Traits")
   private Map<String, VaultBossConfig.TraitDefinition> traits;

   @Override
   public String getName() {
      return "vault_boss";
   }

   @Override
   protected void reset() {
      this.traits = new LinkedHashMap<>();
      this.traits
         .put(
            "fireball_ranged_attack",
            new VaultBossConfig.TraitDefinition(
               "fireball_ranged_attack", new FireballRangedAttackGoal(null).setAttributes(1.0, 20, 60, 25.0F, 0.5, true).serializeNBT()
            )
         );
      this.traits
         .put(
            "snowball_ranged_attack",
            new VaultBossConfig.TraitDefinition(
               "snowball_ranged_attack", new SnowballRangedAttackGoal(null).setAttributes(1.0, 10, 20, 25.0F, 12.0F, 0.5F, false).serializeNBT()
            )
         );
      this.traits
         .put(
            "golem_hand_ranged_attack",
            new VaultBossConfig.TraitDefinition(
               "golem_hand_ranged_attack", new GolemHandRangedAttackGoal(null).setAttributes(1.0, 20, 60, 25.0F, 12.0F, 0.5F, false).serializeNBT()
            )
         );
      this.traits
         .put(
            "cobweb_ranged_attack",
            new VaultBossConfig.TraitDefinition(
               "cobweb_ranged_attack", new CobwebRangedAttackGoal(null).setAttributes(1.0, 20, 60, 25.0F, 12.0F, true).serializeNBT()
            )
         );
      WeightedList<VaultBossBaseEntity.AttackData> meleeAttacks = new WeightedList<VaultBossBaseEntity.AttackData>()
         .add(new VaultBossBaseEntity.AttackData("punch", 1.0), 1);
      this.traits
         .put(
            "golem_melee_attack",
            new VaultBossConfig.TraitDefinition("melee_attack", new PersistentMeleeAttackGoal(null).setAttributes(meleeAttacks).serializeNBT())
         );
      meleeAttacks = new WeightedList<VaultBossBaseEntity.AttackData>().add(new VaultBossBaseEntity.AttackData("double_punch", 3.0), 1);
      this.traits
         .put(
            "golem_heavy_melee_attack",
            new VaultBossConfig.TraitDefinition("melee_attack", new PersistentMeleeAttackGoal(null).setAttributes(meleeAttacks).serializeNBT())
         );
      meleeAttacks = new WeightedList<VaultBossBaseEntity.AttackData>()
         .add(new VaultBossBaseEntity.AttackData("slash", 1.0), 1)
         .add(new VaultBossBaseEntity.AttackData("jab", 1.0), 1);
      this.traits
         .put(
            "boogieman_melee_attack",
            new VaultBossConfig.TraitDefinition("melee_attack", new PersistentMeleeAttackGoal(null).setAttributes(meleeAttacks).serializeNBT())
         );
      meleeAttacks = new WeightedList<VaultBossBaseEntity.AttackData>().add(new VaultBossBaseEntity.AttackData("aoeclose", 1.0), 1);
      this.traits
         .put(
            "sweep_attack", new VaultBossConfig.TraitDefinition("melee_attack", new PersistentMeleeAttackGoal(null).setAttributes(meleeAttacks).serializeNBT())
         );
      WeightedList<MobSpawningUtils.EntitySpawnData> entityTypes = new WeightedList<>();
      entityTypes.add(new MobSpawningUtils.EntitySpawnData(EntityType.ZOMBIE, null), 1);
      entityTypes.add(new MobSpawningUtils.EntitySpawnData(EntityType.SKELETON, null), 1);
      entityTypes.add(new MobSpawningUtils.EntitySpawnData(EntityType.CREEPER, null), 1);
      this.traits
         .put(
            "summon_vanilla",
            new VaultBossConfig.TraitDefinition("summon", new SummonGoal(null).setAttributes(10, 1, 4, 100, 1, 15, entityTypes).serializeNBT())
         );
      this.traits
         .put(
            "summon_vanilla_at_target",
            new VaultBossConfig.TraitDefinition(
               "summon_at_target", new SummonAtTargetGoal(null).setAttributes(10, 1, 4, 100, 5, 15, entityTypes).serializeNBT()
            )
         );
      entityTypes.clear();
      ListTag handItemsTag = new ListTag();
      handItemsTag.add(new ItemStack(Items.NETHERITE_SWORD).save(new CompoundTag()));
      CompoundTag mobTag = new CompoundTag();
      mobTag.put("HandItems", handItemsTag);
      entityTypes.add(new MobSpawningUtils.EntitySpawnData(ModEntities.FIGHTER, mobTag), 1);
      entityTypes.add(new MobSpawningUtils.EntitySpawnData(ModEntities.DUNGEON_SKELETON, null), 1);
      entityTypes.add(new MobSpawningUtils.EntitySpawnData(ModEntities.DUNGEON_VINDICATOR, null), 1);
      this.traits
         .put(
            "summon_vault", new VaultBossConfig.TraitDefinition("summon", new SummonGoal(null).setAttributes(10, 1, 4, 100, 1, 15, entityTypes).serializeNBT())
         );
      this.traits
         .put("shulker_bullet", new VaultBossConfig.TraitDefinition("shulker_bullet", new ShulkerAttackGoal(null).setAttributes(10, 40).serializeNBT()));
      this.traits.put("heal", new VaultBossConfig.TraitDefinition("heal", new HealGoal(null).setAttributes(40, 0.1F).serializeNBT()));
      this.traits.put("blood_orb", new VaultBossConfig.TraitDefinition("blood_orb", new BloodOrbGoal(null).setAttributes(40, 100, 20, 4).serializeNBT()));
      this.traits
         .put(
            "evoker_fangs",
            new VaultBossConfig.TraitDefinition("evoker_fangs", new EvokerFangsGoal(null).setAttributes(100, 200, 1.5F, 3, 5, 0.7F).serializeNBT())
         );
      this.traits
         .put(
            "higher_health",
            new VaultBossConfig.TraitDefinition(
               "attribute_modifier",
               new AttributeModifierTrait().setAttributes(Attributes.MAX_HEALTH.getRegistryName().toString(), 1.5, "multiply").serializeNBT()
            )
         );
      this.traits
         .put(
            "higher_speed",
            new VaultBossConfig.TraitDefinition(
               "attribute_modifier",
               new AttributeModifierTrait().setAttributes(Attributes.MOVEMENT_SPEED.getRegistryName().toString(), 0.3F, "add").serializeNBT()
            )
         );
      this.traits
         .put("life_leech_on_hit", new VaultBossConfig.TraitDefinition("life_leech_on_hit", new LifeLeechOnHitEffect().setAttributes(0.5F).serializeNBT()));
      this.traits
         .put(
            "wither_on_hit",
            new VaultBossConfig.TraitDefinition(
               "apply_potion_on_hit", new ApplyPotionOnHitEffect().setAttributes(MobEffects.WITHER, 100, 0, 0.2F).serializeNBT()
            )
         );
      this.traits
         .put(
            "levitate_on_hit",
            new VaultBossConfig.TraitDefinition(
               "apply_potion_on_hit", new ApplyPotionOnHitEffect().setAttributes(MobEffects.LEVITATION, 100, 0, 0.5F).serializeNBT()
            )
         );
      this.traits
         .put(
            "steal_mana_on_hit",
            new VaultBossConfig.TraitDefinition(
               "apply_potion_on_hit", new ApplyPotionOnHitEffect().setAttributes(ModEffects.MANA_STEAL, 20, 0, 0.5F).serializeNBT()
            )
         );
      this.traits
         .put(
            "throw_lingering_poison",
            new VaultBossConfig.TraitDefinition(
               "throw_potion", new ThrowPotionGoal(null).setAttributes(1.0, 100, 200, 10.0F, MobEffects.POISON, 100, 0, true, true).serializeNBT()
            )
         );
      this.traits
         .put(
            "levitation_aura",
            new VaultBossConfig.TraitDefinition(
               PotionAuraGoal.TYPE, new PotionAuraGoal(null).setAttributes(MobEffects.LEVITATION, 40, 0, 5, false).serializeNBT()
            )
         );
      this.traits
         .put(
            "bleed_aura",
            new VaultBossConfig.TraitDefinition(PotionAuraGoal.TYPE, new PotionAuraGoal(null).setAttributes(ModEffects.BLEED, 40, 0, 10, true).serializeNBT())
         );
      this.traits.put("leap_at_target", new VaultBossConfig.TraitDefinition("leap_at_target", new CompoundTag()));
      this.traits.put("spider_attack", new VaultBossConfig.TraitDefinition("spider_attack", new CompoundTag()));
      this.traits
         .put(
            "place_cobwebs_around",
            new VaultBossConfig.TraitDefinition(
               "place_block_around", new PlaceBlockAroundGoal(null).setAttributes(5, 1, 20, 60, PartialBlockState.of(Blocks.COBWEB)).serializeNBT()
            )
         );
      this.bosses = new LinkedHashMap<>();
      this.bosses
         .put(
            ModEntities.GOLEM_BOSS.getRegistryName(),
            new VaultBossConfig.BossDefinition(
               List.of("golem_melee_attack", "summon_vault", "life_leech_on_hit", "levitate_on_hit"), Map.of("light_ranged", "golem_hand_ranged_attack")
            )
         );
      this.bosses
         .put(
            ModEntities.BLACK_WIDOW_BOSS.getRegistryName(),
            new VaultBossConfig.BossDefinition(List.of("place_cobwebs_around", "spider_attack", "leap_at_target"), Map.of("light_ranged", "evoker_fangs"))
         );
      this.bosses
         .put(
            ModEntities.BOOGIEMAN_BOSS.getRegistryName(),
            new VaultBossConfig.BossDefinition(
               List.of("boogieman_melee_attack", "snowball_ranged_attack", "summon_vanilla", "higher_health", "higher_speed"),
               Map.of("light_ranged", "blood_orb")
            )
         );
   }

   @Override
   public <T extends Config> T readConfig() {
      VaultBossConfig config = super.readConfig();

      for (Entry<String, VaultBossConfig.TraitDefinition> trait : config.traits.entrySet()) {
         trait.getValue().id = trait.getKey();
      }

      return (T)config;
   }

   public ResourceLocation getRandomBossId() {
      int index = rand.nextInt(this.bosses.size());
      Iterator<ResourceLocation> it = this.bosses.keySet().iterator();

      for (int i = 0; i < index; i++) {
         it.next();
      }

      return it.next();
   }

   public String getRandomModifier() {
      Set<String> modifiers = this.bosses.values().iterator().next().modifierTraitMap.keySet();
      int index = rand.nextInt(modifiers.size());
      Iterator<String> it = modifiers.iterator();

      for (int i = 0; it.hasNext(); i++) {
         String modifier = it.next();
         if (i == index) {
            return modifier;
         }
      }

      return modifiers.iterator().next();
   }

   public List<ItemStack> getRandomLootItems(int vaultLevel, RandomSource random) {
      LegacyLootTablesConfig.Level levelLootTables = ModConfigs.LOOT_TABLES.getForLevel(vaultLevel);
      if (levelLootTables == null) {
         return Collections.emptyList();
      } else {
         String lootTableId = levelLootTables.OFFERING_LOOT;
         LootTable table = VaultRegistry.LOOT_TABLE.getKey(lootTableId).get(Version.latest());
         List<ItemStack> items = new ArrayList<>();
         if (table != null) {
            for (LootTable.Entry entry : table.getEntries()) {
               this.generateEntry(items, entry, random);
            }
         }

         return items;
      }
   }

   protected void generateEntry(List<ItemStack> items, LootTable.Entry entry, RandomSource random) {
      int roll = entry.getRoll().get(random);

      for (int i = 0; i < roll; i++) {
         entry.getPool().getRandomFlat(Version.latest(), random).map(e -> e.getStack(random)).ifPresent(items::addAll);
      }
   }

   public List<VaultBossConfig.TraitDefinition> getBossBaseTraits(ResourceLocation bossRegistryName) {
      VaultBossConfig.BossDefinition bossDefinition = this.bosses.get(bossRegistryName);
      if (bossDefinition == null) {
         return List.of();
      } else {
         List<VaultBossConfig.TraitDefinition> bossTraits = new ArrayList<>();

         for (String baseTraitId : bossDefinition.baseTraits) {
            VaultBossConfig.TraitDefinition baseTraitDefinition = this.traits.get(baseTraitId);
            if (baseTraitDefinition != null) {
               bossTraits.add(baseTraitDefinition);
            }
         }

         return bossTraits;
      }
   }

   public Map<String, VaultBossConfig.TraitDefinition> getBossModifierTraits(ResourceLocation bossRegistryName, Set<String> modifiers) {
      VaultBossConfig.BossDefinition bossDefinition = this.bosses.get(bossRegistryName);
      if (bossDefinition == null) {
         return Map.of();
      } else {
         Map<String, VaultBossConfig.TraitDefinition> modifierTraits = new HashMap<>();

         for (String modifier : modifiers) {
            String modifierTraitId = bossDefinition.modifierTraitMap.get(modifier);
            VaultBossConfig.TraitDefinition modifierTraitDefinition = this.traits.get(modifierTraitId);
            if (modifierTraitDefinition != null) {
               modifierTraits.put(modifier, modifierTraitDefinition);
            }
         }

         return modifierTraits;
      }
   }

   @Override
   protected boolean isValid() {
      boolean valid = super.isValid();

      for (VaultBossConfig.BossDefinition boss : this.bosses.values()) {
         for (String baseTrait : boss.baseTraits) {
            if (!this.traits.containsKey(baseTrait)) {
               VaultMod.LOGGER.error("Base trait {} for boss is not defined in traits", baseTrait);
               valid = false;
            }
         }

         for (String modifierTrait : boss.modifierTraitMap.values()) {
            if (!this.traits.containsKey(modifierTrait)) {
               VaultMod.LOGGER.error("Modifier trait {} for boss is not defined in traits", modifierTrait);
               valid = false;
            }
         }
      }

      return valid;
   }

   public Iterable<String> getAllModifiers() {
      Iterator<VaultBossConfig.BossDefinition> it = this.bosses.values().iterator();
      return (Iterable<String>)(it.hasNext() ? it.next().modifierTraitMap.keySet() : Collections.emptyList());
   }

   private static class BossDefinition {
      @Expose
      @SerializedName("BaseTraits")
      List<String> baseTraits;
      @Expose
      @SerializedName("ModifierTraits")
      Map<String, String> modifierTraitMap;

      public BossDefinition(List<String> baseTraits, Map<String, String> modifierTraitMap) {
         this.baseTraits = baseTraits;
         this.modifierTraitMap = modifierTraitMap;
      }
   }

   private static class CompoundTagJsonObjectSerializer implements JsonSerializer<CompoundTag>, JsonDeserializer<CompoundTag> {
      private static final VaultBossConfig.CompoundTagJsonObjectSerializer INSTANCE = new VaultBossConfig.CompoundTagJsonObjectSerializer();

      public JsonElement serialize(CompoundTag src, Type typeOfSrc, JsonSerializationContext context) {
         return (JsonElement)NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, src);
      }

      public CompoundTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         return JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json) instanceof CompoundTag compoundTag ? compoundTag : new CompoundTag();
      }
   }

   public static class TraitDefinition {
      private String id;
      @Expose
      @SerializedName("Type")
      private String type;
      @Expose
      @SerializedName("Attributes")
      @JsonAdapter(VaultBossConfig.CompoundTagJsonObjectSerializer.class)
      private CompoundTag attributes;

      public TraitDefinition(String type, CompoundTag attributes) {
         this.type = type;
         this.attributes = attributes;
      }

      public String id() {
         return this.id;
      }

      public String type() {
         return this.type;
      }

      public CompoundTag attributesNbt() {
         return this.attributes;
      }
   }
}
