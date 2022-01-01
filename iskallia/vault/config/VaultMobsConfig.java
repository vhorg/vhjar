package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.data.GlobalDifficultyData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.VaultSpawner;
import iskallia.vault.world.vault.modifier.LevelModifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

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
   private Map<String, List<VaultMobsConfig.Mob.AttributeOverride>> ATTRIBUTE_OVERRIDES = new LinkedHashMap<>();
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
      List<VaultMobsConfig.Mob.AttributeOverride> attributes = new ArrayList<>();
      attributes.add(new VaultMobsConfig.Mob.AttributeOverride(ModAttributes.CRIT_CHANCE, 0.0, 0.5, "set", 0.8, 0.05));
      attributes.add(new VaultMobsConfig.Mob.AttributeOverride(ModAttributes.CRIT_MULTIPLIER, 0.0, 0.1, "set", 0.8, 0.1));
      this.ATTRIBUTE_OVERRIDES.put(EntityType.field_200725_aD.getRegistryName().toString(), attributes);
      this.LEVEL_OVERRIDES
         .add(
            new VaultMobsConfig.Level(0)
               .mobAdd(Items.field_151041_m, 1)
               .mobAdd(Items.field_151052_q, 2)
               .bossAdd(Items.field_151052_q, 1)
               .bossAdd(Items.field_151010_B, 2)
               .raffleAdd(Items.field_151048_u, 1)
               .mob(EntityType.field_200725_aD, 1)
               .boss(ModEntities.ROBOT, 1)
               .raffle(ModEntities.ARENA_BOSS, 1)
               .mobMisc(3, 1, new VaultSpawner.Config().withStartMaxMobs(5).withMinDistance(10.0).withMaxDistance(24.0).withDespawnDistance(26.0))
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
         this.MOB_MISC = new VaultMobsConfig.MobMisc(0, 0, new VaultSpawner.Config());
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

      public VaultMobsConfig.Level mobMisc(int level, int trials, VaultSpawner.Config spawner) {
         this.MOB_MISC = new VaultMobsConfig.MobMisc(level, trials, spawner);
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

      public ItemStack getForMob(EquipmentSlotType slot) {
         if (!this.MOB_LOOT.isEmpty() && this.MOB_LOOT.containsKey(slot.func_188450_d())) {
            String itemStr = this.MOB_LOOT.get(slot.func_188450_d()).getRandom(new Random());
            if (itemStr.contains("{")) {
               int part = itemStr.indexOf(123);
               String itemName = itemStr.substring(0, part);
               String nbt = itemStr.substring(part);
               Item item = Registry.field_212630_s.func_241873_b(new ResourceLocation(itemName)).orElse(Items.field_190931_a);
               ItemStack itemStack = new ItemStack(item);

               try {
                  itemStack.func_77982_d(JsonToNBT.func_180713_a(nbt));
                  return itemStack;
               } catch (CommandSyntaxException var9) {
                  return ItemStack.field_190927_a;
               }
            } else {
               Item item = Registry.field_212630_s.func_241873_b(new ResourceLocation(itemStr)).orElse(Items.field_190931_a);
               return new ItemStack(item);
            }
         } else {
            return ItemStack.field_190927_a;
         }
      }

      public ItemStack getForBoss(EquipmentSlotType slot) {
         if (!this.BOSS_LOOT.isEmpty() && this.BOSS_LOOT.containsKey(slot.func_188450_d())) {
            String itemStr = this.BOSS_LOOT.get(slot.func_188450_d()).getRandom(new Random());
            if (itemStr.contains("{")) {
               int part = itemStr.indexOf(123);
               String itemName = itemStr.substring(0, part);
               String nbt = itemStr.substring(part);
               Item item = Registry.field_212630_s.func_241873_b(new ResourceLocation(itemName)).orElse(Items.field_190931_a);
               ItemStack itemStack = new ItemStack(item);

               try {
                  itemStack.func_77982_d(JsonToNBT.func_180713_a(nbt));
                  return itemStack;
               } catch (CommandSyntaxException var9) {
                  return ItemStack.field_190927_a;
               }
            } else {
               Item item = Registry.field_212630_s.func_241873_b(new ResourceLocation(itemStr)).orElse(Items.field_190931_a);
               return new ItemStack(item);
            }
         } else {
            return ItemStack.field_190927_a;
         }
      }

      public ItemStack getForRaffle(EquipmentSlotType slot) {
         if (!this.RAFFLE_BOSS_LOOT.isEmpty() && this.RAFFLE_BOSS_LOOT.containsKey(slot.func_188450_d())) {
            String itemStr = this.RAFFLE_BOSS_LOOT.get(slot.func_188450_d()).getRandom(new Random());
            if (itemStr.contains("{")) {
               int part = itemStr.indexOf(123);
               String itemName = itemStr.substring(0, part);
               String nbt = itemStr.substring(part);
               Item item = Registry.field_212630_s.func_241873_b(new ResourceLocation(itemName)).orElse(Items.field_190931_a);
               ItemStack itemStack = new ItemStack(item);

               try {
                  itemStack.func_77982_d(JsonToNBT.func_180713_a(nbt));
                  return itemStack;
               } catch (CommandSyntaxException var9) {
                  return ItemStack.field_190927_a;
               }
            } else {
               Item item = Registry.field_212630_s.func_241873_b(new ResourceLocation(itemStr)).orElse(Items.field_190931_a);
               return new ItemStack(item);
            }
         } else {
            return ItemStack.field_190927_a;
         }
      }

      public Optional<VaultMobsConfig.Mob> getMob(LivingEntity entity) {
         return this.MOB_POOL
            .stream()
            .map(entry -> entry.value)
            .filter(mob -> mob.NAME.equals(entity.func_200600_R().getRegistryName().toString()))
            .findFirst();
      }
   }

   public static class Mob {
      @Expose
      private String NAME;

      public Mob(EntityType<?> type) {
         this.NAME = type.getRegistryName().toString();
      }

      public EntityType<?> getType() {
         return Registry.field_212629_r.func_241873_b(new ResourceLocation(this.NAME)).orElse(EntityType.field_200791_e);
      }

      public static LivingEntity scale(LivingEntity entity, VaultRaid vault, GlobalDifficultyData.Difficulty vaultDifficulty) {
         int level = vault.getProperties().getValue(VaultRaid.LEVEL);
         UUID host = vault.getProperties().getValue(VaultRaid.HOST);
         MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
         if (srv != null) {
            level += NetcodeUtils.<Integer>runIfPresent(
                  srv, host, sPlayer -> ModConfigs.PLAYER_SCALING.getMobLevelAdjustment(sPlayer.func_200200_C_().getString())
               )
               .orElse(0);
         }

         for (LevelModifier modifier : vault.getActiveModifiersFor(PlayerFilter.any(), LevelModifier.class)) {
            level += modifier.getLevelAddend();
         }

         int mobLevel = Math.max(level, 0);
         List<VaultMobsConfig.Mob.AttributeOverride> attributes = ModConfigs.VAULT_MOBS
            .ATTRIBUTE_OVERRIDES
            .get(entity.func_200600_R().getRegistryName().toString());
         if (attributes != null) {
            for (VaultMobsConfig.Mob.AttributeOverride override : attributes) {
               if (!(entity.field_70170_p.field_73012_v.nextDouble() >= override.ROLL_CHANCE)) {
                  Registry.field_239692_aP_.func_241873_b(new ResourceLocation(override.NAME)).ifPresent(attribute -> {
                     ModifiableAttributeInstance instance = entity.func_110148_a(attribute);
                     if (instance != null) {
                        double multiplier = 1.0;
                        if (attribute == Attributes.field_233818_a_ || attribute == Attributes.field_233823_f_) {
                           multiplier = vaultDifficulty.getMultiplier();
                        }

                        instance.func_111128_a(override.getValue(instance.func_111125_b(), mobLevel, entity.field_70170_p.func_201674_k()) * multiplier);
                     }
                  });
               }
            }
         }

         entity.func_70606_j(1.0F);
         entity.func_70691_i(1000000.0F);
         return entity;
      }

      public LivingEntity create(World world) {
         return (LivingEntity)this.getType().func_200721_a(world);
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

         public AttributeOverride(Attribute attribute, double min, double max, String operator, double rollChance, double scalePerLevel) {
            this.NAME = attribute.getRegistryName().toString();
            this.MIN = min;
            this.MAX = max;
            this.OPERATOR = operator;
            this.ROLL_CHANCE = rollChance;
            this.SCALE_PER_LEVEL = scalePerLevel;
         }

         public double getValue(double baseValue, int level, Random random) {
            double value = this.getStartValue(baseValue, random);

            for (int i = 0; i < level; i++) {
               value += this.getStartValue(baseValue, random) * this.SCALE_PER_LEVEL;
            }

            return value;
         }

         public double getStartValue(double baseValue, Random random) {
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
      public VaultSpawner.Config SPAWNER;

      public MobMisc(int level, int trials, VaultSpawner.Config spawner) {
         this.ENCH_LEVEL = level;
         this.ENCH_TRIALS = trials;
         this.SPAWNER = spawner;
      }
   }
}
