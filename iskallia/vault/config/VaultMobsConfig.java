package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.WeightedList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class VaultMobsConfig extends Config {
   public static final Item[] LEATHER_ARMOR = new Item[]{Items.field_151024_Q, Items.field_151027_R, Items.field_151026_S, Items.field_151021_T};
   public static final Item[] GOLDEN_ARMOR = new Item[]{Items.field_151169_ag, Items.field_151171_ah, Items.field_151149_ai, Items.field_151151_aj};
   public static final Item[] CHAINMAIL_ARMOR = new Item[]{Items.field_151020_U, Items.field_151023_V, Items.field_151022_W, Items.field_151029_X};
   public static final Item[] IRON_ARMOR = new Item[]{Items.field_151028_Y, Items.field_151030_Z, Items.field_151165_aa, Items.field_151167_ab};
   public static final Item[] DIAMOND_ARMOR = new Item[]{Items.field_151161_ac, Items.field_151163_ad, Items.field_151173_ae, Items.field_151175_af};
   public static final Item[] NETHERITE_ARMOR = new Item[]{Items.field_234763_ls_, Items.field_234764_lt_, Items.field_234765_lu_, Items.field_234766_lv_};
   public static final Item[] WOODEN_WEAPONS = new Item[]{
      Items.field_151041_m, Items.field_151053_p, Items.field_151039_o, Items.field_151038_n, Items.field_151017_I
   };
   public static final Item[] STONE_WEAPONS = new Item[]{
      Items.field_151052_q, Items.field_151049_t, Items.field_151050_s, Items.field_151051_r, Items.field_151018_J
   };
   public static final Item[] GOLDEN_WEAPONS = new Item[]{
      Items.field_151010_B, Items.field_151006_E, Items.field_151005_D, Items.field_151011_C, Items.field_151013_M
   };
   public static final Item[] IRON_WEAPONS = new Item[]{
      Items.field_151040_l, Items.field_151036_c, Items.field_151035_b, Items.field_151037_a, Items.field_151019_K
   };
   public static final Item[] DIAMOND_WEAPONS = new Item[]{
      Items.field_151048_u, Items.field_151056_x, Items.field_151046_w, Items.field_151047_v, Items.field_151012_L
   };
   public static final Item[] NETHERITE_WEAPONS = new Item[]{
      Items.field_234754_kI_, Items.field_234757_kL_, Items.field_234756_kK_, Items.field_234755_kJ_, Items.field_234758_kU_
   };
   @Expose
   private List<VaultMobsConfig.Level> LEVEL_OVERRIDES = new ArrayList<>();

   public VaultMobsConfig.Level getForLevel(int level) {
      for (int i = 0; i < this.LEVEL_OVERRIDES.size(); i++) {
         if (level < this.LEVEL_OVERRIDES.get(i).MIN_LEVEL) {
            if (i != 0) {
               return this.LEVEL_OVERRIDES.get(i - 1);
            }
            break;
         }

         if (i == this.LEVEL_OVERRIDES.size() - 1) {
            return this.LEVEL_OVERRIDES.get(i);
         }
      }

      return VaultMobsConfig.Level.EMPTY;
   }

   @Override
   public String getName() {
      return "vault_mobs";
   }

   @Override
   protected void reset() {
      this.LEVEL_OVERRIDES
         .add(
            new VaultMobsConfig.Level(5)
               .mobAdd(Items.field_151041_m, 1)
               .mobAdd(Items.field_151052_q, 2)
               .bossAdd(Items.field_151052_q, 1)
               .bossAdd(Items.field_151010_B, 2)
               .raffleAdd(Items.field_151048_u, 1)
               .mob(
                  EntityType.field_200725_aD,
                  1,
                  mob -> mob.attribute(ModAttributes.CRIT_CHANCE, 1.0)
                     .attribute(ModAttributes.CRIT_MULTIPLIER, 5.0)
                     .attribute(Attributes.field_233818_a_, 20.0)
               )
               .boss(ModEntities.ROBOT, 1, mob -> mob.attribute(ModAttributes.TP_CHANCE, 0.5).attribute(ModAttributes.TP_RANGE, 32.0))
               .mobMisc(3, 1, 3)
               .bossMisc(3, 1)
               .raffleMisc(3, 1)
         );
   }

   public static class BossMisc {
      @Expose
      public int ENCH_LEVEL;
      @Expose
      public int ENCH_TRIALS;

      public BossMisc(int level, int trials) {
         this.ENCH_LEVEL = level;
         this.ENCH_TRIALS = trials;
      }
   }

   public static class Level {
      public static final VaultMobsConfig.Level EMPTY = new VaultMobsConfig.Level(0);
      @Expose
      public int MIN_LEVEL;
      @Expose
      public Map<String, WeightedList<String>> MOB_LOOT;
      @Expose
      public Map<String, WeightedList<String>> BOSS_LOOT;
      @Expose
      public Map<String, WeightedList<String>> RAFFLE_BOSS_LOOT;
      @Expose
      public WeightedList<VaultMobsConfig.Mob> MOB_POOL;
      @Expose
      public WeightedList<VaultMobsConfig.Mob> BOSS_POOL;
      @Expose
      public WeightedList<VaultMobsConfig.Mob> RAFFLE_BOSS_POOL;
      @Expose
      public VaultMobsConfig.MobMisc MOB_MISC;
      @Expose
      public VaultMobsConfig.BossMisc BOSS_MISC;
      @Expose
      public VaultMobsConfig.BossMisc RAFFLE_BOSS_MISC;

      public Level(int minLevel) {
         this.MIN_LEVEL = minLevel;
         this.MOB_LOOT = new LinkedHashMap<>();
         this.BOSS_LOOT = new LinkedHashMap<>();
         this.RAFFLE_BOSS_LOOT = new LinkedHashMap<>();
         this.MOB_POOL = new WeightedList<>();
         this.BOSS_POOL = new WeightedList<>();
         this.RAFFLE_BOSS_POOL = new WeightedList<>();
         this.MOB_MISC = new VaultMobsConfig.MobMisc(0, 0, 0);
         this.BOSS_MISC = new VaultMobsConfig.BossMisc(0, 0);
         this.RAFFLE_BOSS_MISC = new VaultMobsConfig.BossMisc(0, 0);
      }

      public VaultMobsConfig.Level mobAdd(Item item, int weight) {
         if (item instanceof ArmorItem) {
            this.MOB_LOOT
               .computeIfAbsent(((ArmorItem)item).func_185083_B_().func_188450_d(), s -> new WeightedList<>())
               .add(item.getRegistryName().toString(), weight);
         } else {
            this.MOB_LOOT.computeIfAbsent(EquipmentSlotType.MAINHAND.func_188450_d(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
            this.MOB_LOOT.computeIfAbsent(EquipmentSlotType.OFFHAND.func_188450_d(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
         }

         return this;
      }

      public VaultMobsConfig.Level bossAdd(Item item, int weight) {
         if (item instanceof ArmorItem) {
            this.BOSS_LOOT
               .computeIfAbsent(((ArmorItem)item).func_185083_B_().func_188450_d(), s -> new WeightedList<>())
               .add(item.getRegistryName().toString(), weight);
         } else {
            this.BOSS_LOOT
               .computeIfAbsent(EquipmentSlotType.MAINHAND.func_188450_d(), s -> new WeightedList<>())
               .add(item.getRegistryName().toString(), weight);
            this.BOSS_LOOT.computeIfAbsent(EquipmentSlotType.OFFHAND.func_188450_d(), s -> new WeightedList<>()).add(item.getRegistryName().toString(), weight);
         }

         return this;
      }

      public VaultMobsConfig.Level raffleAdd(Item item, int weight) {
         if (item instanceof ArmorItem) {
            this.RAFFLE_BOSS_LOOT
               .computeIfAbsent(((ArmorItem)item).func_185083_B_().func_188450_d(), s -> new WeightedList<>())
               .add(item.getRegistryName().toString(), weight);
         } else {
            this.RAFFLE_BOSS_LOOT
               .computeIfAbsent(EquipmentSlotType.MAINHAND.func_188450_d(), s -> new WeightedList<>())
               .add(item.getRegistryName().toString(), weight);
            this.RAFFLE_BOSS_LOOT
               .computeIfAbsent(EquipmentSlotType.OFFHAND.func_188450_d(), s -> new WeightedList<>())
               .add(item.getRegistryName().toString(), weight);
         }

         return this;
      }

      public VaultMobsConfig.Level mobMisc(int level, int trials, int maxMobs) {
         this.MOB_MISC = new VaultMobsConfig.MobMisc(level, trials, maxMobs);
         return this;
      }

      public VaultMobsConfig.Level bossMisc(int level, int trials) {
         this.BOSS_MISC = new VaultMobsConfig.BossMisc(level, trials);
         return this;
      }

      public VaultMobsConfig.Level raffleMisc(int level, int trials) {
         this.RAFFLE_BOSS_MISC = new VaultMobsConfig.BossMisc(level, trials);
         return this;
      }

      public VaultMobsConfig.Level mob(EntityType<? extends LivingEntity> type, int weight) {
         this.MOB_POOL.add(new VaultMobsConfig.Mob(type), weight);
         return this;
      }

      public VaultMobsConfig.Level mob(EntityType<? extends LivingEntity> type, int weight, Consumer<VaultMobsConfig.Mob> action) {
         VaultMobsConfig.Mob mob = new VaultMobsConfig.Mob(type);
         action.accept(mob);
         this.MOB_POOL.add(mob, weight);
         return this;
      }

      public VaultMobsConfig.Level boss(EntityType<? extends LivingEntity> type, int weight) {
         this.BOSS_POOL.add(new VaultMobsConfig.Mob(type), weight);
         return this;
      }

      public VaultMobsConfig.Level boss(EntityType<? extends LivingEntity> type, int weight, Consumer<VaultMobsConfig.Mob> action) {
         VaultMobsConfig.Mob mob = new VaultMobsConfig.Mob(type);
         action.accept(mob);
         this.BOSS_POOL.add(mob, weight);
         return this;
      }

      public VaultMobsConfig.Level raffle(EntityType<? extends LivingEntity> type, int weight) {
         this.RAFFLE_BOSS_POOL.add(new VaultMobsConfig.Mob(type), weight);
         return this;
      }

      public VaultMobsConfig.Level raffle(EntityType<? extends LivingEntity> type, int weight, Consumer<VaultMobsConfig.Mob> action) {
         VaultMobsConfig.Mob mob = new VaultMobsConfig.Mob(type);
         action.accept(mob);
         this.RAFFLE_BOSS_POOL.add(mob, weight);
         return this;
      }

      public Item getForMob(EquipmentSlotType slot) {
         if (!this.MOB_LOOT.isEmpty() && this.MOB_LOOT.containsKey(slot.func_188450_d())) {
            String item = this.MOB_LOOT.get(slot.func_188450_d()).getRandom(new Random());
            return Registry.field_212630_s.func_241873_b(new ResourceLocation(item)).orElse(Items.field_190931_a);
         } else {
            return Items.field_190931_a;
         }
      }

      public Item getForBoss(EquipmentSlotType slot) {
         if (!this.BOSS_LOOT.isEmpty() && this.BOSS_LOOT.containsKey(slot.func_188450_d())) {
            String item = this.BOSS_LOOT.get(slot.func_188450_d()).getRandom(new Random());
            return Registry.field_212630_s.func_241873_b(new ResourceLocation(item)).orElse(Items.field_190931_a);
         } else {
            return Items.field_190931_a;
         }
      }

      public Item getForRaffle(EquipmentSlotType slot) {
         if (!this.RAFFLE_BOSS_LOOT.isEmpty() && this.RAFFLE_BOSS_LOOT.containsKey(slot.func_188450_d())) {
            String item = this.RAFFLE_BOSS_LOOT.get(slot.func_188450_d()).getRandom(new Random());
            return Registry.field_212630_s.func_241873_b(new ResourceLocation(item)).orElse(Items.field_190931_a);
         } else {
            return Items.field_190931_a;
         }
      }
   }

   public static class Mob {
      @Expose
      private String NAME;
      @Expose
      private List<VaultMobsConfig.Mob.AttributeOverride> ATTRIBUTES;

      public Mob(EntityType<?> type) {
         this.NAME = type.getRegistryName().toString();
         this.ATTRIBUTES = new ArrayList<>();
      }

      public VaultMobsConfig.Mob attribute(Attribute attribute, double defaultValue) {
         this.ATTRIBUTES.add(new VaultMobsConfig.Mob.AttributeOverride(attribute, defaultValue));
         return this;
      }

      public EntityType<?> getType() {
         return Registry.field_212629_r.func_241873_b(new ResourceLocation(this.NAME)).orElse(EntityType.field_200791_e);
      }

      public LivingEntity create(World world) {
         LivingEntity entity = (LivingEntity)this.getType().func_200721_a(world);

         for (VaultMobsConfig.Mob.AttributeOverride override : this.ATTRIBUTES) {
            if (!(world.field_73012_v.nextDouble() >= override.ROLL_CHANCE)) {
               Attribute attribute = (Attribute)Registry.field_239692_aP_.func_241873_b(new ResourceLocation(override.NAME)).orElse(null);
               if (attribute != null) {
                  ModifiableAttributeInstance instance = entity.func_110148_a(attribute);
                  if (instance != null) {
                     instance.func_111128_a(override.getValue(instance.func_111125_b(), world.func_201674_k()));
                  }
               }
            }
         }

         entity.func_70691_i(1000000.0F);
         return entity;
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

         public AttributeOverride(Attribute attribute, double defaultValue) {
            this.NAME = attribute.getRegistryName().toString();
            this.MIN = defaultValue;
            this.MAX = defaultValue;
            this.OPERATOR = "set";
            this.ROLL_CHANCE = 1.0;
         }

         public double getValue(double baseValue, Random random) {
            double value = Math.min(this.MIN, this.MAX) + random.nextFloat() * Math.abs(this.MAX - this.MIN);
            if (this.OPERATOR.equalsIgnoreCase("multiply")) {
               return baseValue * value;
            } else if (this.OPERATOR.equalsIgnoreCase("add")) {
               return baseValue + value;
            } else {
               return this.OPERATOR.equalsIgnoreCase("set") ? value : baseValue;
            }
         }
      }
   }

   public static class MobMisc {
      @Expose
      public int ENCH_LEVEL;
      @Expose
      public int ENCH_TRIALS;
      @Expose
      public int MAX_MOBS;

      public MobMisc(int level, int trials, int maxMobs) {
         this.ENCH_LEVEL = level;
         this.ENCH_TRIALS = trials;
         this.MAX_MOBS = maxMobs;
      }
   }
}
