package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.NaturalSpawner;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class VaultMobsConfig extends Config {
   @Expose
   private Map<EntityPredicate, List<VaultMobsConfig.Mob.AttributeOverride>> ATTRIBUTE_OVERRIDES = new LinkedHashMap<>();
   @Expose
   private LevelEntryList<VaultMobsConfig.LevelOverride> LEVEL_OVERRIDES = new LevelEntryList<>();

   public VaultMobsConfig.LevelOverride getForLevel(int level) {
      return this.LEVEL_OVERRIDES.getForLevel(level).orElse(VaultMobsConfig.LevelOverride.EMPTY);
   }

   @Override
   public String getName() {
      return "vault_mobs";
   }

   public static LivingEntity scale(UUID ownerId, LivingEntity entity, int vaultLevel) {
      List<VaultMobsConfig.Mob.AttributeOverride> attributes = null;

      for (Entry<EntityPredicate, List<VaultMobsConfig.Mob.AttributeOverride>> entry : ModConfigs.VAULT_MOBS.ATTRIBUTE_OVERRIDES.entrySet()) {
         if (entry.getKey().test(entity)) {
            attributes = entry.getValue();
         }
      }

      if (attributes == null) {
         return entity;
      } else {
         VaultDifficulty difficulty = WorldSettings.get(entity.level).getPlayerDifficulty(ownerId);

         for (VaultMobsConfig.Mob.AttributeOverride property : attributes) {
            VaultMobsConfig.Mob.AttributeOverrideOverride override = property.LEVELS == null ? null : property.LEVELS.getForLevel(vaultLevel).orElse(null);
            double rollChance = override == null ? property.ROLL_CHANCE : override.ROLL_CHANCE;
            if (!(entity.level.random.nextDouble() >= rollChance)) {
               Registry.ATTRIBUTE.getOptional(new ResourceLocation(property.NAME)).ifPresent(attribute -> {
                  AttributeInstance instance = entity.getAttribute(attribute);
                  if (instance != null) {
                     double baseValue = property.getValue(instance.getBaseValue(), vaultLevel, entity.level.getRandom());
                     if (instance.getAttribute() == Attributes.MAX_HEALTH) {
                        baseValue *= difficulty.getHeathMultiplier();
                     } else if (instance.getAttribute() == Attributes.ATTACK_DAMAGE) {
                        baseValue *= difficulty.getDamageMultiplier();
                     }

                     instance.setBaseValue(baseValue);
                  }
               });
            }
         }

         entity.setHealth(1.0F);
         entity.heal(1000000.0F);
         AttributeInstance inst = entity.getAttribute(Attributes.MAX_HEALTH);
         if (inst != null) {
            for (AttributeModifier modifier : inst.getModifiers()) {
               if (modifier.getName().equals("Leader zombie bonus")) {
                  inst.removeModifier(modifier);
                  break;
               }
            }
         }

         return entity;
      }
   }

   @Override
   protected void reset() {
      List<VaultMobsConfig.Mob.AttributeOverride> attributes = new ArrayList<>();
      attributes.add(new VaultMobsConfig.Mob.AttributeOverride(ModAttributes.CRIT_CHANCE, 0.0, 0.5, "set", 0.8, 0.05));
      attributes.add(new VaultMobsConfig.Mob.AttributeOverride(ModAttributes.CRIT_MULTIPLIER, 0.0, 0.1, "set", 0.8, 0.1));
      this.LEVEL_OVERRIDES.add(new VaultMobsConfig.LevelOverride(0));
   }

   public static class LevelOverride implements LevelEntryList.ILevelEntry {
      public static final VaultMobsConfig.LevelOverride EMPTY = new VaultMobsConfig.LevelOverride(0);
      @Expose
      public int MIN_LEVEL;
      @Expose
      public WeightedList<VaultMobsConfig.Mob> MOB_POOL;
      @Expose
      public WeightedList<VaultMobsConfig.Mob> BOSS_POOL;
      @Expose
      public WeightedList<VaultMobsConfig.Mob> RAFFLE_BOSS_POOL;
      @Expose
      public NaturalSpawner.Config SPAWNER;

      public LevelOverride(int minLevel) {
         this.MIN_LEVEL = minLevel;
         this.MOB_POOL = new WeightedList<>();
         this.BOSS_POOL = new WeightedList<>();
         this.RAFFLE_BOSS_POOL = new WeightedList<>();
         this.SPAWNER = new NaturalSpawner.Config(0, 8, 12, 16);
      }

      public VaultMobsConfig.LevelOverride mob(EntityType<? extends LivingEntity> type, int weight) {
         this.MOB_POOL.add(new VaultMobsConfig.Mob(type), weight);
         return this;
      }

      public VaultMobsConfig.LevelOverride mob(EntityType<? extends LivingEntity> type, int weight, Consumer<VaultMobsConfig.Mob> action) {
         VaultMobsConfig.Mob mob = new VaultMobsConfig.Mob(type);
         action.accept(mob);
         this.MOB_POOL.add(mob, weight);
         return this;
      }

      public VaultMobsConfig.LevelOverride boss(EntityType<? extends LivingEntity> type, int weight) {
         this.BOSS_POOL.add(new VaultMobsConfig.Mob(type), weight);
         return this;
      }

      public VaultMobsConfig.LevelOverride boss(EntityType<? extends LivingEntity> type, int weight, Consumer<VaultMobsConfig.Mob> action) {
         VaultMobsConfig.Mob mob = new VaultMobsConfig.Mob(type);
         action.accept(mob);
         this.BOSS_POOL.add(mob, weight);
         return this;
      }

      public VaultMobsConfig.LevelOverride raffle(EntityType<? extends LivingEntity> type, int weight) {
         this.RAFFLE_BOSS_POOL.add(new VaultMobsConfig.Mob(type), weight);
         return this;
      }

      public VaultMobsConfig.LevelOverride raffle(EntityType<? extends LivingEntity> type, int weight, Consumer<VaultMobsConfig.Mob> action) {
         VaultMobsConfig.Mob mob = new VaultMobsConfig.Mob(type);
         action.accept(mob);
         this.RAFFLE_BOSS_POOL.add(mob, weight);
         return this;
      }

      @Override
      public int getLevel() {
         return this.MIN_LEVEL;
      }
   }

   public static class Mob {
      @Expose
      private String NAME;

      public Mob(EntityType<?> type) {
         this.NAME = type.getRegistryName().toString();
      }

      public EntityType<?> getType() {
         return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(this.NAME)).orElse(EntityType.BAT);
      }

      public LivingEntity create(Level world) {
         return (LivingEntity)this.getType().create(world);
      }

      public static class AttributeOverride {
         @Expose
         public String NAME;
         @Expose
         public double MIN;
         @Expose
         public double MAX;
         @Expose
         public String OPERATOR;
         @Expose
         public double ROLL_CHANCE;
         @Expose
         public double SCALE_PER_LEVEL;
         @Expose
         public int SCALE_MAX_LEVEL = -1;
         @Expose
         public LevelEntryList<VaultMobsConfig.Mob.AttributeOverrideOverride> LEVELS;

         public AttributeOverride(Attribute attribute, double min, double max, String operator, double rollChance, double scalePerLevel) {
            this.NAME = attribute.getRegistryName().toString();
            this.MIN = min;
            this.MAX = max;
            this.OPERATOR = operator;
            this.ROLL_CHANCE = rollChance;
            this.SCALE_PER_LEVEL = scalePerLevel;
         }

         public double getValue(double baseValue, int level, Random random) {
            VaultMobsConfig.Mob.AttributeOverrideOverride override = this.LEVELS == null ? null : this.LEVELS.getForLevel(level).orElse(null);
            int scaleMaxLevel = override == null ? this.SCALE_MAX_LEVEL : override.SCALE_MAX_LEVEL;
            double scalePerLevel = override == null ? this.SCALE_PER_LEVEL : override.SCALE_PER_LEVEL;
            double value = this.getStartValue(baseValue, level, random);
            if (scaleMaxLevel >= 0) {
               level = Math.min(level, scaleMaxLevel);
            }

            for (int i = 0; i < level; i++) {
               value += this.getStartValue(baseValue, level, random) * scalePerLevel;
            }

            return value;
         }

         public double getStartValue(double baseValue, int level, Random random) {
            VaultMobsConfig.Mob.AttributeOverrideOverride override = this.LEVELS == null ? null : this.LEVELS.getForLevel(level).orElse(null);
            double min = override == null ? this.MIN : override.MIN;
            double max = override == null ? this.MAX : override.MAX;
            String operator = override == null ? this.OPERATOR : override.OPERATOR;
            double value = Math.min(min, max) + random.nextFloat() * Math.abs(max - min);
            if (operator.equalsIgnoreCase("multiply")) {
               return baseValue * value;
            } else if (operator.equalsIgnoreCase("add")) {
               return baseValue + value;
            } else {
               return operator.equalsIgnoreCase("set") ? value : baseValue;
            }
         }
      }

      public static class AttributeOverrideOverride implements LevelEntryList.ILevelEntry {
         @Expose
         public int MIN_LEVEL;
         @Expose
         public double MIN;
         @Expose
         public double MAX;
         @Expose
         public String OPERATOR;
         @Expose
         public double ROLL_CHANCE;
         @Expose
         public double SCALE_PER_LEVEL;
         @Expose
         public int SCALE_MAX_LEVEL = -1;

         @Override
         public int getLevel() {
            return this.MIN_LEVEL;
         }
      }
   }
}
