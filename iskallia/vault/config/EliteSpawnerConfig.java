package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.data.WeightedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EliteSpawnerConfig extends Config {
   @Expose
   public int blockCheckRadius;
   @Expose
   public Map<String, EliteSpawnerConfig.SpawnerGroup> spawnerGroups;

   @Override
   public String getName() {
      return "elite_spawner";
   }

   @Override
   protected void reset() {
      this.blockCheckRadius = 50;
      this.spawnerGroups = new HashMap<>();
      this.addSpawnerGroup(ModEntities.ELITE_DROWNED, 4, 9, EntityType.DROWNED, EntityType.GUARDIAN, EntityType.SKELETON);
      this.addSpawnerGroup(ModEntities.ELITE_ENDERMAN, 5, 10, EntityType.ENDERMAN, EntityType.PUFFERFISH, EntityType.SHULKER);
      this.addSpawnerGroup(ModEntities.ELITE_HUSK, 4, 11, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.HOGLIN);
      ListTag handItemsTag = new ListTag();
      handItemsTag.add(new ItemStack(Items.NETHERITE_SWORD).save(new CompoundTag()));
      CompoundTag mobTag = new CompoundTag();
      mobTag.put("HandItems", handItemsTag);
      this.addSpawnerGroup(
         new EliteSpawnerConfig.SpawnerEntity(ModEntities.ELITE_SKELETON.getRegistryName(), mobTag),
         6,
         9,
         new EliteSpawnerConfig.SpawnerEntity(EntityType.SKELETON.getRegistryName(), mobTag),
         new EliteSpawnerConfig.SpawnerEntity(EntityType.STRAY.getRegistryName(), mobTag),
         new EliteSpawnerConfig.SpawnerEntity(EntityType.PILLAGER.getRegistryName(), mobTag)
      );
      this.addSpawnerGroup(ModEntities.ELITE_SPIDER, 7, 10, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SILVERFISH);
      this.addSpawnerGroup(ModEntities.ELITE_STRAY, 5, 8, EntityType.STRAY, EntityType.CREEPER, EntityType.SKELETON);
      this.addSpawnerGroup(ModEntities.ELITE_WITCH, 4, 9, EntityType.WITCH, EntityType.ZOMBIE, EntityType.SKELETON);
      this.addSpawnerGroup(ModEntities.ELITE_ZOMBIE, 3, 6, EntityType.ZOMBIE, EntityType.VINDICATOR, EntityType.SKELETON);
   }

   private void addSpawnerGroup(EntityType<?> eliteEntityType, int min, int max, EntityType<?>... minionTypes) {
      WeightedList<EliteSpawnerConfig.SpawnerEntity> minions = new WeightedList<>();
      Arrays.stream(minionTypes).forEach(mt -> minions.add(new WeightedList.Entry<>(new EliteSpawnerConfig.SpawnerEntity(mt.getRegistryName(), null), 1)));
      this.spawnerGroups
         .put(
            eliteEntityType.getRegistryName().toString(),
            new EliteSpawnerConfig.SpawnerGroup(new EliteSpawnerConfig.SpawnerEntity(eliteEntityType.getRegistryName(), null), min, max, minions)
         );
   }

   private void addSpawnerGroup(EliteSpawnerConfig.SpawnerEntity elite, int min, int max, EliteSpawnerConfig.SpawnerEntity... minions) {
      WeightedList<EliteSpawnerConfig.SpawnerEntity> minionsWeigheted = new WeightedList<>();
      Arrays.stream(minions).forEach(minion -> minionsWeigheted.add(new WeightedList.Entry<>(minion, 1)));
      this.spawnerGroups.put(elite.entityName.toString(), new EliteSpawnerConfig.SpawnerGroup(elite, min, max, minionsWeigheted));
   }

   public static class SpawnerEntity {
      @Expose
      public ResourceLocation entityName;
      @Expose
      public CompoundTag entityNbt;

      public SpawnerEntity(ResourceLocation entityName, @Nullable CompoundTag entityNbt) {
         this.entityName = entityName;
         this.entityNbt = entityNbt;
      }
   }

   public static class SpawnerGroup {
      @Expose
      public EliteSpawnerConfig.SpawnerEntity elite;
      @Expose
      public int min;
      @Expose
      public int max;
      @Expose
      public WeightedList<EliteSpawnerConfig.SpawnerEntity> minions;

      public SpawnerGroup(EliteSpawnerConfig.SpawnerEntity elite, int min, int max, WeightedList<EliteSpawnerConfig.SpawnerEntity> minions) {
         this.elite = elite;
         this.min = min;
         this.max = max;
         this.minions = minions;
      }
   }
}
