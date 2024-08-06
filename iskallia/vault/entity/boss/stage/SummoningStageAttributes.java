package iskallia.vault.entity.boss.stage;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.MobSpawningUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class SummoningStageAttributes {
   WeightedList<MobSpawningUtils.EntitySpawnData> entityTypes;
   int maxMobsAliveBeforeNextSpawn;
   int radius;
   int minMobCount;
   int maxMobCount;
   int minGroupCount;
   int maxGroupCount;

   public SummoningStageAttributes(
      WeightedList<MobSpawningUtils.EntitySpawnData> entityTypes,
      int maxMobsAliveBeforeNextSpawn,
      int radius,
      int minMobCount,
      int maxMobCount,
      int minGroupCount,
      int maxGroupCount
   ) {
      this.entityTypes = entityTypes;
      this.maxMobsAliveBeforeNextSpawn = maxMobsAliveBeforeNextSpawn;
      this.radius = radius;
      this.minMobCount = minMobCount;
      this.maxMobCount = maxMobCount;
      this.minGroupCount = minGroupCount;
      this.maxGroupCount = maxGroupCount;
   }

   public static SummoningStageAttributes from(CompoundTag tag) {
      return new SummoningStageAttributes(
         deserializeEntityTypes(tag.getList("EntityTypes", 10)),
         tag.getInt("MaxMobsAliveBeforeNextSpawn"),
         tag.getInt("Radius"),
         tag.getInt("MinMobCount"),
         tag.getInt("MaxMobCount"),
         tag.getInt("MinGroupCount"),
         tag.getInt("MaxGroupCount")
      );
   }

   private static WeightedList<MobSpawningUtils.EntitySpawnData> deserializeEntityTypes(ListTag tag) {
      WeightedList<MobSpawningUtils.EntitySpawnData> weightedList = new WeightedList<>();

      for (Tag element : tag) {
         CompoundTag compoundTag = (CompoundTag)element;
         MobSpawningUtils.EntitySpawnData.from(compoundTag).ifPresent(entitySpawnData -> weightedList.put(entitySpawnData, compoundTag.getDouble("Weight")));
      }

      return weightedList;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.put("EntityTypes", this.serializeEntityTypes());
      tag.putInt("MaxMobsAliveBeforeNextSpawn", this.maxMobsAliveBeforeNextSpawn);
      tag.putInt("Radius", this.radius);
      tag.putInt("MinMobCount", this.minMobCount);
      tag.putInt("MaxMobCount", this.maxMobCount);
      tag.putInt("MinGroupCount", this.minGroupCount);
      tag.putInt("MaxGroupCount", this.maxGroupCount);
      return tag;
   }

   private ListTag serializeEntityTypes() {
      ListTag tag = new ListTag();
      this.entityTypes.forEach((entityType, weight) -> {
         CompoundTag compoundTag = new CompoundTag();
         entityType.serializeTo(compoundTag);
         compoundTag.putDouble("Weight", weight);
         tag.add(compoundTag);
      });
      return tag;
   }
}
