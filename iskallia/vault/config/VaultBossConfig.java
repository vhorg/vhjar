package iskallia.vault.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.serialization.JsonOps;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.entity.boss.BerserkStageAttributes;
import iskallia.vault.entity.boss.CatalystStageAttributes;
import iskallia.vault.entity.boss.MeleeAttacks;
import iskallia.vault.entity.boss.MeleeStageAttributes;
import iskallia.vault.entity.boss.SparkStageAttributes;
import iskallia.vault.entity.boss.SummoningStageAttributes;
import iskallia.vault.init.ModEntities;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultBossConfig extends Config {
   @Expose
   private Map<String, VaultBossConfig.BossDefinition> bossDefinitions = new HashMap<>();

   public List<VaultBossConfig.StageConfig> getBossStageConfigs(String bossDefinitionName) {
      return this.bossDefinitions.get(bossDefinitionName).stages;
   }

   @Override
   public String getName() {
      return "vault_boss";
   }

   @Override
   protected void reset() {
      WeightedList<SummoningStageAttributes.EntitySpawnData> entityTypes = new WeightedList<>();
      entityTypes.add(new SummoningStageAttributes.EntitySpawnData(EntityType.ZOMBIE, null), 1);
      entityTypes.add(new SummoningStageAttributes.EntitySpawnData(EntityType.SKELETON, null), 1);
      entityTypes.add(new SummoningStageAttributes.EntitySpawnData(EntityType.CREEPER, null), 1);
      ListTag handItemsTag = new ListTag();
      handItemsTag.add(new ItemStack(Items.NETHERITE_SWORD).save(new CompoundTag()));
      CompoundTag mobTag = new CompoundTag();
      mobTag.put("HandItems", handItemsTag);
      entityTypes.add(new SummoningStageAttributes.EntitySpawnData(ModEntities.FIGHTER, mobTag), 1);
      entityTypes.add(new SummoningStageAttributes.EntitySpawnData(ModEntities.DUNGEON_SKELETON, null), 1);
      entityTypes.add(new SummoningStageAttributes.EntitySpawnData(ModEntities.DUNGEON_VINDICATOR, null), 1);
      WeightedList<MeleeAttacks.AttackData> meleeAttacks = new WeightedList<>();
      meleeAttacks.add(new MeleeAttacks.AttackData("hammersmash", 1.5), 1);
      meleeAttacks.add(new MeleeAttacks.AttackData("uppercut", 1.0), 2);
      meleeAttacks.add(new MeleeAttacks.AttackData("groundslam", 0.9), 4);
      WeightedList<MeleeAttacks.AttackData> rageAttacks = new WeightedList<>();
      rageAttacks.add(new MeleeAttacks.AttackData("throw", 1.0), 1);
      WeightedList<CatalystStageAttributes.EffectAttributes> effects = new WeightedList<>();
      effects.put(new CatalystStageAttributes.EffectAttributes(MobEffects.HARM, 1, 3), 1);
      effects.put(new CatalystStageAttributes.EffectAttributes(MobEffects.DIG_SLOWDOWN, 200, 2), 1);
      effects.put(new CatalystStageAttributes.EffectAttributes(MobEffects.SLOW_FALLING, 400, 2), 1);
      effects.put(new CatalystStageAttributes.EffectAttributes(MobEffects.MOVEMENT_SLOWDOWN, 200, 2), 1);
      this.bossDefinitions
         .put(
            "artifact_boss",
            new VaultBossConfig.BossDefinition(
               "artifact_boss",
               List.of(
                  new VaultBossConfig.StageConfig(
                     "catalyst",
                     "catalyst",
                     new CatalystStageAttributes(
                           List.of(
                              new CatalystStageAttributes.CatalystWave(1, 1),
                              new CatalystStageAttributes.CatalystWave(2, 4),
                              new CatalystStageAttributes.CatalystWave(3, 6),
                              new CatalystStageAttributes.CatalystWave(5, 10)
                           ),
                           5.5F,
                           0.3F,
                           0.4F,
                           22.5F,
                           1.5F,
                           effects,
                           Map.of(
                              VaultChestType.WOODEN,
                              new ResourceLocation("the_vault:wooden_chest_lvl0_v2"),
                              VaultChestType.LIVING,
                              new ResourceLocation("the_vault:living_chest_lvl0_v2"),
                              VaultChestType.GILDED,
                              new ResourceLocation("the_vault:gilded_chest_lvl0_v2"),
                              VaultChestType.ORNATE,
                              new ResourceLocation("the_vault:ornate_chest_lvl0_v2")
                           )
                        )
                        .serialize()
                  ),
                  new VaultBossConfig.StageConfig(
                     "berserk", "berserk", new BerserkStageAttributes(200, 10, 0.33, 4.5, 3.3, meleeAttacks, rageAttacks, 80, 4).serialize()
                  ),
                  new VaultBossConfig.StageConfig("generic1", "melee", new MeleeStageAttributes(200, 15, 0.1, meleeAttacks, WeightedList.empty()).serialize()),
                  new VaultBossConfig.StageConfig("summoning", "summoning", new SummoningStageAttributes(entityTypes, 20, 10, 3, 6, 10, 15).serialize()),
                  new VaultBossConfig.StageConfig("generic2", "melee", new MeleeStageAttributes(200, 11, 0.1, meleeAttacks, WeightedList.empty()).serialize()),
                  new VaultBossConfig.StageConfig("spark", "spark", new SparkStageAttributes(40, 5, 10, 200, 2, 300, 100, 2.5).serialize()),
                  new VaultBossConfig.StageConfig("generic3", "melee", new MeleeStageAttributes(200, 12, 0.2, meleeAttacks, WeightedList.empty()).serialize())
               )
            )
         );
   }

   public static final class BossDefinition {
      @Expose
      private final String name;
      @Expose
      private final List<VaultBossConfig.StageConfig> stages;

      public BossDefinition(String name, List<VaultBossConfig.StageConfig> stages) {
         this.name = name;
         this.stages = stages;
      }

      public String name() {
         return this.name;
      }

      public List<VaultBossConfig.StageConfig> stages() {
         return this.stages;
      }
   }

   private static class CompoundTagJsonObjectSerializer implements JsonSerializer<CompoundTag>, JsonDeserializer<CompoundTag> {
      public JsonElement serialize(CompoundTag src, Type typeOfSrc, JsonSerializationContext context) {
         return (JsonElement)NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, src);
      }

      public CompoundTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         return JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json) instanceof CompoundTag compoundTag ? compoundTag : new CompoundTag();
      }
   }

   public static final class StageConfig {
      @Expose
      private final String stageName;
      @Expose
      private final String stageType;
      @Expose
      @JsonAdapter(VaultBossConfig.CompoundTagJsonObjectSerializer.class)
      private final CompoundTag attributes;

      public StageConfig(String stageName, String stageType, CompoundTag attributes) {
         this.stageName = stageName;
         this.stageType = stageType;
         this.attributes = attributes;
      }

      public String stageName() {
         return this.stageName;
      }

      public String stageType() {
         return this.stageType;
      }

      public CompoundTag attributes() {
         return this.attributes;
      }
   }
}
