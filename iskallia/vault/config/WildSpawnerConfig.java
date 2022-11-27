package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class WildSpawnerConfig extends Config {
   @Expose
   public List<WildSpawnerConfig.SpawnerGroup> spawnerGroups;

   @Override
   public String getName() {
      return "wild_spawner";
   }

   @Override
   protected void reset() {
      this.spawnerGroups = new ArrayList<>();
      CompoundTag powered = new CompoundTag();
      powered.putBoolean("powered", true);
      WeightedList<WildSpawnerConfig.SpawnerEntity> entities = new WeightedList<>();
      entities.add(new WildSpawnerConfig.SpawnerEntity(EntityType.SKELETON.getRegistryName(), null), 2);
      entities.add(new WildSpawnerConfig.SpawnerEntity(EntityType.CREEPER.getRegistryName(), powered), 1);
      this.spawnerGroups.add(new WildSpawnerConfig.SpawnerGroup(0, 10, entities));
      entities = new WeightedList<>();
      entities.add(new WildSpawnerConfig.SpawnerEntity(EntityType.BLAZE.getRegistryName(), null), 2);
      entities.add(new WildSpawnerConfig.SpawnerEntity(EntityType.WITHER_SKELETON.getRegistryName(), null), 1);
      this.spawnerGroups.add(new WildSpawnerConfig.SpawnerGroup(5, 20, entities));
   }

   public static class SpawnerEntity {
      @Expose
      public ResourceLocation type;
      @Nullable
      @Expose
      public CompoundTag nbt;

      public SpawnerEntity(ResourceLocation type, CompoundTag nbt) {
         this.type = type;
         this.nbt = nbt;
      }
   }

   public static class SpawnerGroup {
      @Expose
      public int minLevel;
      @Expose
      public int blockCheckRadius;
      @Expose
      public WeightedList<WildSpawnerConfig.SpawnerEntity> entities;

      public SpawnerGroup(int minLevel, int blockCheckRadius, WeightedList<WildSpawnerConfig.SpawnerEntity> entities) {
         this.minLevel = minLevel;
         this.blockCheckRadius = blockCheckRadius;
         this.entities = entities;
      }
   }
}
