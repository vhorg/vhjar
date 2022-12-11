package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

public class CustomEntitySpawnerConfig extends Config {
   @Expose
   public Map<String, List<CustomEntitySpawnerConfig.SpawnerGroup>> spawnerGroups;

   @Override
   public String getName() {
      return "custom_entity_spawner";
   }

   @Override
   protected void reset() {
      this.spawnerGroups = new HashMap<>();
      CompoundTag fuse = new CompoundTag();
      fuse.putShort("Fuse", (short)80);
      CompoundTag tntState = new CompoundTag();
      tntState.put("BlockState", NbtUtils.writeBlockState(Blocks.TNT.defaultBlockState()));
      WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities = new WeightedList<>();
      entities.add(new CustomEntitySpawnerConfig.SpawnerEntity(EntityType.TNT.getRegistryName(), fuse, true), 2);
      entities.add(new CustomEntitySpawnerConfig.SpawnerEntity(EntityType.FALLING_BLOCK.getRegistryName(), tntState), 1);
      this.spawnerGroups.put("group1", List.of(new CustomEntitySpawnerConfig.SpawnerGroup(0, 10, entities)));
      entities = new WeightedList<>();
      entities.add(new CustomEntitySpawnerConfig.SpawnerEntity(EntityType.SHULKER_BULLET.getRegistryName(), null), 2);
      entities.add(new CustomEntitySpawnerConfig.SpawnerEntity(EntityType.FIREWORK_ROCKET.getRegistryName(), null, true), 1);
      WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities2 = new WeightedList<>();
      entities2.add(new CustomEntitySpawnerConfig.SpawnerEntity(EntityType.SHULKER_BULLET.getRegistryName(), null), 2);
      entities2.add(new CustomEntitySpawnerConfig.SpawnerEntity(EntityType.FIREWORK_ROCKET.getRegistryName(), null, true), 1);
      this.spawnerGroups
         .put("group2", List.of(new CustomEntitySpawnerConfig.SpawnerGroup(0, 20, entities), new CustomEntitySpawnerConfig.SpawnerGroup(5, 20, entities2)));
   }

   public static class SpawnerEntity {
      @Expose
      public ResourceLocation type;
      @Nullable
      @Expose
      public CompoundTag nbt;
      @Expose
      public boolean randomMotion;

      public SpawnerEntity(ResourceLocation type, @Nullable CompoundTag nbt, boolean randomMotion) {
         this.type = type;
         this.nbt = nbt;
         this.randomMotion = randomMotion;
      }

      public SpawnerEntity(ResourceLocation type, @Nullable CompoundTag nbt) {
         this(type, nbt, false);
      }
   }

   public static class SpawnerGroup {
      @Expose
      public int minLevel;
      @Expose
      public int blockCheckRadius;
      @Expose
      public WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities;

      public SpawnerGroup(int minLevel, int blockCheckRadius, WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities) {
         this.minLevel = minLevel;
         this.blockCheckRadius = blockCheckRadius;
         this.entities = entities;
      }
   }
}
