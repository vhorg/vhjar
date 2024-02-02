package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class SummoningStageAttributes {
   WeightedList<SummoningStageAttributes.EntitySpawnData> entityTypes;
   int maxMobsAliveBeforeNextSpawn;
   int radius;
   int minMobCount;
   int maxMobCount;
   int minGroupCount;
   int maxGroupCount;

   public SummoningStageAttributes(
      WeightedList<SummoningStageAttributes.EntitySpawnData> entityTypes,
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

   private static WeightedList<SummoningStageAttributes.EntitySpawnData> deserializeEntityTypes(ListTag tag) {
      WeightedList<SummoningStageAttributes.EntitySpawnData> weightedList = new WeightedList<>();

      for (Tag element : tag) {
         CompoundTag compoundTag = (CompoundTag)element;
         SummoningStageAttributes.EntitySpawnData.from(compoundTag)
            .ifPresent(entitySpawnData -> weightedList.put(entitySpawnData, compoundTag.getDouble("Weight")));
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

   public record EntitySpawnData(EntityType<?> entityType, @Nullable CompoundTag entityNbt) {
      public static Optional<SummoningStageAttributes.EntitySpawnData> from(CompoundTag tag) {
         EntityType<?> entityType = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("EntityType")));
         CompoundTag entityNbt = tag.contains("EntityNbt") ? tag.getCompound("EntityNbt") : null;
         return entityType == null ? Optional.empty() : Optional.of(new SummoningStageAttributes.EntitySpawnData(entityType, entityNbt));
      }

      public CompoundTag serializeTo(CompoundTag tag) {
         tag.putString("EntityType", this.entityType.getRegistryName().toString());
         if (this.entityNbt != null) {
            tag.put("EntityNbt", this.entityNbt);
         }

         return tag;
      }
   }
}
