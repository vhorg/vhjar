package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.attribute.BooleanAttribute;
import iskallia.vault.attribute.DoubleAttribute;
import iskallia.vault.attribute.EffectAttribute;
import iskallia.vault.attribute.EffectCloudAttribute;
import iskallia.vault.attribute.EffectTalentAttribute;
import iskallia.vault.attribute.EnumAttribute;
import iskallia.vault.attribute.FloatAttribute;
import iskallia.vault.attribute.IntegerAttribute;
import iskallia.vault.attribute.LongAttribute;
import iskallia.vault.attribute.StringAttribute;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.block.PuzzleRuneBlock;
import iskallia.vault.entity.EffectCloudEntity;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.world.data.PlayerFavourData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModAttributes {
   public static Map<ResourceLocation, VAttribute<?, ?>> REGISTRY = new HashMap<>();
   public static Attribute CRIT_CHANCE;
   public static Attribute CRIT_MULTIPLIER;
   public static Attribute TP_CHANCE;
   public static Attribute TP_INDIRECT_CHANCE;
   public static Attribute TP_RANGE;
   public static Attribute POTION_RESISTANCE;
   public static Attribute SIZE_SCALE;
   public static Attribute BREAK_ARMOR_CHANCE;
   public static VAttribute<Double, DoubleAttribute> ADD_ARMOR;
   public static VAttribute<Double, DoubleAttribute> ADD_ARMOR_2;
   public static VAttribute<Double, DoubleAttribute> ADD_ARMOR_TOUGHNESS;
   public static VAttribute<Double, DoubleAttribute> ADD_ARMOR_TOUGHNESS_2;
   public static VAttribute<Double, DoubleAttribute> ADD_KNOCKBACK_RESISTANCE;
   public static VAttribute<Double, DoubleAttribute> ADD_KNOCKBACK_RESISTANCE_2;
   public static VAttribute<Double, DoubleAttribute> ADD_ATTACK_DAMAGE;
   public static VAttribute<Double, DoubleAttribute> ADD_ATTACK_DAMAGE_2;
   public static VAttribute<Double, DoubleAttribute> ADD_ATTACK_SPEED;
   public static VAttribute<Double, DoubleAttribute> ADD_ATTACK_SPEED_2;
   public static VAttribute<Integer, IntegerAttribute> ADD_DURABILITY;
   public static VAttribute<Integer, IntegerAttribute> ADD_DURABILITY_2;
   public static VAttribute<Double, DoubleAttribute> ADD_REACH;
   public static VAttribute<Double, DoubleAttribute> ADD_REACH_2;
   public static VAttribute<Float, FloatAttribute> ADD_COOLDOWN_REDUCTION;
   public static VAttribute<Float, FloatAttribute> ADD_COOLDOWN_REDUCTION_2;
   public static VAttribute<Integer, IntegerAttribute> ADD_MIN_VAULT_LEVEL;
   public static VAttribute<List<EffectCloudEntity.Config>, EffectCloudAttribute> ADD_REGEN_CLOUD;
   public static VAttribute<List<EffectCloudEntity.Config>, EffectCloudAttribute> ADD_WEAKENING_CLOUD;
   public static VAttribute<List<EffectCloudEntity.Config>, EffectCloudAttribute> ADD_WITHER_CLOUD;
   public static VAttribute<List<EffectAttribute.Instance>, EffectAttribute> ADD_POISON_IMMUNITY;
   public static VAttribute<List<EffectAttribute.Instance>, EffectAttribute> ADD_WITHER_IMMUNITY;
   public static VAttribute<List<EffectAttribute.Instance>, EffectAttribute> ADD_HUNGER_IMMUNITY;
   public static VAttribute<List<EffectAttribute.Instance>, EffectAttribute> ADD_MINING_FATIGUE_IMMUNITY;
   public static VAttribute<List<EffectAttribute.Instance>, EffectAttribute> ADD_SLOWNESS_IMMUNITY;
   public static VAttribute<List<EffectAttribute.Instance>, EffectAttribute> ADD_WEAKNESS_IMMUNITY;
   public static VAttribute<Float, FloatAttribute> ADD_FEATHER_FEET;
   public static VAttribute<Boolean, BooleanAttribute> ADD_SOULBOUND;
   public static VAttribute<Boolean, BooleanAttribute> ADD_REFORGED;
   public static VAttribute<Integer, IntegerAttribute> ADD_PLATING;
   public static VAttribute<Float, FloatAttribute> ADD_EXTRA_LEECH_RATIO;
   public static VAttribute<Float, FloatAttribute> ADD_EXTRA_RESISTANCE;
   public static VAttribute<Float, FloatAttribute> ADD_EXTRA_PARRY_CHANCE;
   public static VAttribute<Float, FloatAttribute> ADD_EXTRA_HEALTH;
   public static VAttribute<Float, FloatAttribute> FATAL_STRIKE_CHANCE;
   public static VAttribute<Float, FloatAttribute> FATAL_STRIKE_DAMAGE;
   public static VAttribute<Float, FloatAttribute> THORNS_CHANCE;
   public static VAttribute<Float, FloatAttribute> THORNS_DAMAGE;
   public static VAttribute<Double, DoubleAttribute> ARMOR;
   public static VAttribute<Double, DoubleAttribute> ARMOR_TOUGHNESS;
   public static VAttribute<Double, DoubleAttribute> KNOCKBACK_RESISTANCE;
   public static VAttribute<Double, DoubleAttribute> ATTACK_DAMAGE;
   public static VAttribute<Double, DoubleAttribute> ATTACK_SPEED;
   public static VAttribute<Integer, IntegerAttribute> DURABILITY;
   public static VAttribute<Double, DoubleAttribute> REACH;
   public static VAttribute<String, StringAttribute> GEAR_CRAFTED_BY;
   public static VAttribute<Long, LongAttribute> GEAR_RANDOM_SEED;
   public static VAttribute<Integer, IntegerAttribute> GEAR_TIER;
   public static VAttribute<Integer, IntegerAttribute> GEAR_MODEL;
   public static VAttribute<Integer, IntegerAttribute> GEAR_SPECIAL_MODEL;
   public static VAttribute<Integer, IntegerAttribute> GEAR_COLOR;
   public static VAttribute<String, StringAttribute> GUARANTEED_MODIFIER;
   public static VAttribute<VaultGear.Rarity, EnumAttribute<VaultGear.Rarity>> GEAR_RARITY;
   public static VAttribute<VaultGear.State, EnumAttribute<VaultGear.State>> GEAR_STATE;
   public static VAttribute<String, StringAttribute> GEAR_NAME;
   public static VAttribute<PlayerFavourData.VaultGodType, EnumAttribute<PlayerFavourData.VaultGodType>> IDOL_TYPE;
   public static VAttribute<Boolean, BooleanAttribute> IDOL_AUGMENTED;
   public static VAttribute<Float, FloatAttribute> GEAR_LEVEL;
   public static VAttribute<Float, FloatAttribute> GEAR_LEVEL_CHANCE;
   public static VAttribute<Integer, IntegerAttribute> GEAR_MAX_LEVEL;
   public static VAttribute<Integer, IntegerAttribute> GEAR_MODIFIERS_TO_ROLL;
   public static VAttribute<Integer, IntegerAttribute> MAX_REPAIRS;
   public static VAttribute<Integer, IntegerAttribute> CURRENT_REPAIRS;
   public static VAttribute<Float, FloatAttribute> EXTRA_LEECH_RATIO;
   public static VAttribute<Float, FloatAttribute> EXTRA_RESISTANCE;
   public static VAttribute<Float, FloatAttribute> EXTRA_PARRY_CHANCE;
   public static VAttribute<Float, FloatAttribute> EXTRA_HEALTH;
   public static VAttribute<List<EffectTalent>, EffectTalentAttribute> EXTRA_EFFECTS;
   public static VAttribute<Integer, IntegerAttribute> MIN_VAULT_LEVEL;
   public static VAttribute<String, StringAttribute> GEAR_ROLL_TYPE;
   public static VAttribute<String, StringAttribute> GEAR_ROLL_POOL;
   public static VAttribute<Boolean, BooleanAttribute> SOULBOUND;
   public static VAttribute<Boolean, BooleanAttribute> REFORGED;
   public static VAttribute<List<EffectAttribute.Instance>, EffectAttribute> EFFECT_IMMUNITY;
   public static VAttribute<Float, FloatAttribute> FEATHER_FEET;
   public static VAttribute<List<EffectCloudEntity.Config>, EffectCloudAttribute> EFFECT_CLOUD;
   public static VAttribute<Float, FloatAttribute> COOLDOWN_REDUCTION;
   public static VAttribute<PuzzleRuneBlock.Color, EnumAttribute<PuzzleRuneBlock.Color>> PUZZLE_COLOR;

   public static void register(Register<Attribute> event) {
      CRIT_CHANCE = register(event.getRegistry(), "generic.crit_chance", new RangedAttribute("attribute.name.generic.crit_chance", 0.0, 0.0, 1.0))
         .func_233753_a_(true);
      CRIT_MULTIPLIER = register(
            event.getRegistry(), "generic.crit_multiplier", new RangedAttribute("attribute.name.generic.crit_multiplier", 0.0, 0.0, 1024.0)
         )
         .func_233753_a_(true);
      TP_CHANCE = register(event.getRegistry(), "generic.tp_chance", new RangedAttribute("attribute.name.generic.tp_chance", 0.0, 0.0, 1.0))
         .func_233753_a_(true);
      TP_INDIRECT_CHANCE = register(
            event.getRegistry(), "generic.indirect_tp_chance", new RangedAttribute("attribute.name.generic.indirect_tp_chance", 0.0, 0.0, 1.0)
         )
         .func_233753_a_(true);
      TP_RANGE = register(event.getRegistry(), "generic.tp_range", new RangedAttribute("attribute.name.generic.tp_range", 32.0, 0.0, 1024.0))
         .func_233753_a_(true);
      POTION_RESISTANCE = register(
            event.getRegistry(), "generic.potion_resistance", new RangedAttribute("attribute.name.generic.potion_resistance", 0.0, 0.0, 1.0)
         )
         .func_233753_a_(true);
      SIZE_SCALE = register(event.getRegistry(), "generic.size_scale", new RangedAttribute("attribute.name.generic.size_scale", 1.0, 0.0, 512.0))
         .func_233753_a_(true);
      BREAK_ARMOR_CHANCE = register(
            event.getRegistry(), "generic.break_armor_chance", new RangedAttribute("attribute.name.generic.break_armor_chance", 0.0, 0.0, 512.0)
         )
         .func_233753_a_(true);
      ADD_ARMOR = register(new ResourceLocation("minecraft", "add_armor"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_ARMOR_2 = register(
         new ResourceLocation("minecraft", "add_armor_2"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_ARMOR_TOUGHNESS = register(
         new ResourceLocation("minecraft", "add_armor_toughness"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_ARMOR_TOUGHNESS_2 = register(
         new ResourceLocation("minecraft", "add_armor_toughness_2"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_KNOCKBACK_RESISTANCE = register(
         new ResourceLocation("minecraft", "add_knockback_resistance"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_KNOCKBACK_RESISTANCE_2 = register(
         new ResourceLocation("minecraft", "add_knockback_resistance_2"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_ATTACK_DAMAGE = register(
         new ResourceLocation("minecraft", "add_attack_damage"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_ATTACK_DAMAGE_2 = register(
         new ResourceLocation("minecraft", "add_attack_damage_2"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_ATTACK_SPEED = register(
         new ResourceLocation("minecraft", "add_attack_speed"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_ATTACK_SPEED_2 = register(
         new ResourceLocation("minecraft", "add_attack_speed_2"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_DURABILITY = register(Vault.id("add_durability"), () -> new IntegerAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_DURABILITY_2 = register(Vault.id("add_durability_2"), () -> new IntegerAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_REACH = register(new ResourceLocation("forge", "add_reach"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_REACH_2 = register(new ResourceLocation("forge", "add_reach_2"), () -> new DoubleAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_COOLDOWN_REDUCTION = register(Vault.id("add_cooldown_reduction"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_COOLDOWN_REDUCTION_2 = register(
         Vault.id("add_cooldown_reduction_2"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value)
      );
      ADD_MIN_VAULT_LEVEL = register(Vault.id("add_min_vault_level"), () -> new IntegerAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_POISON_IMMUNITY = register(Vault.id("add_poison_immunity"), () -> new EffectAttribute((stack, parent, value) -> {
         List<EffectAttribute.Instance> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_WITHER_IMMUNITY = register(Vault.id("add_wither_immunity"), () -> new EffectAttribute((stack, parent, value) -> {
         List<EffectAttribute.Instance> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_HUNGER_IMMUNITY = register(Vault.id("add_hunger_immunity"), () -> new EffectAttribute((stack, parent, value) -> {
         List<EffectAttribute.Instance> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_MINING_FATIGUE_IMMUNITY = register(Vault.id("add_mining_fatigue_immunity"), () -> new EffectAttribute((stack, parent, value) -> {
         List<EffectAttribute.Instance> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_SLOWNESS_IMMUNITY = register(Vault.id("add_slowness_immunity"), () -> new EffectAttribute((stack, parent, value) -> {
         List<EffectAttribute.Instance> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_WEAKNESS_IMMUNITY = register(Vault.id("add_weakness_immunity"), () -> new EffectAttribute((stack, parent, value) -> {
         List<EffectAttribute.Instance> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_REGEN_CLOUD = register(Vault.id("add_regen_cloud"), () -> new EffectCloudAttribute((stack, parent, value) -> {
         List<EffectCloudEntity.Config> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_WEAKENING_CLOUD = register(Vault.id("add_weakening_cloud"), () -> new EffectCloudAttribute((stack, parent, value) -> {
         List<EffectCloudEntity.Config> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_WITHER_CLOUD = register(Vault.id("add_wither_cloud"), () -> new EffectCloudAttribute((stack, parent, value) -> {
         List<EffectCloudEntity.Config> list = new ArrayList<>(parent.getBaseValue());
         list.addAll(value);
         return list;
      }));
      ADD_FEATHER_FEET = register(Vault.id("add_feather_feet"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_SOULBOUND = register(Vault.id("add_soulbound"), () -> new BooleanAttribute((stack, parent, value) -> parent.getBaseValue() | value));
      ADD_REFORGED = register(Vault.id("add_reforged"), () -> new BooleanAttribute((stack, parent, value) -> parent.getBaseValue() | value));
      ADD_PLATING = register(Vault.id("add_plating"), () -> new IntegerAttribute((stack, parent, value) -> parent.getBaseValue() * 50 + value));
      ADD_EXTRA_LEECH_RATIO = register(Vault.id("add_extra_leech_ratio"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_EXTRA_RESISTANCE = register(Vault.id("add_extra_resistance"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_EXTRA_PARRY_CHANCE = register(Vault.id("add_extra_parry_chance"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ADD_EXTRA_HEALTH = register(Vault.id("add_extra_health"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      FATAL_STRIKE_CHANCE = register(Vault.id("fatal_strike_chance"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      FATAL_STRIKE_DAMAGE = register(Vault.id("fatal_strike_damage"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      THORNS_CHANCE = register(Vault.id("thorns_chance"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      THORNS_DAMAGE = register(Vault.id("thorns_damage"), () -> new FloatAttribute((stack, parent, value) -> parent.getBaseValue() + value));
      ARMOR = register(new ResourceLocation("minecraft", "armor"), DoubleAttribute::new, ADD_ARMOR, ADD_ARMOR_2);
      ARMOR_TOUGHNESS = register(new ResourceLocation("minecraft", "armor_toughness"), DoubleAttribute::new, ADD_ARMOR_TOUGHNESS, ADD_ARMOR_TOUGHNESS_2);
      KNOCKBACK_RESISTANCE = register(
         new ResourceLocation("minecraft", "knockback_resistance"), DoubleAttribute::new, ADD_KNOCKBACK_RESISTANCE, ADD_KNOCKBACK_RESISTANCE_2
      );
      ATTACK_DAMAGE = register(new ResourceLocation("minecraft", "attack_damage"), DoubleAttribute::new, ADD_ATTACK_DAMAGE, ADD_ATTACK_DAMAGE_2);
      ATTACK_SPEED = register(new ResourceLocation("minecraft", "attack_speed"), DoubleAttribute::new, ADD_ATTACK_SPEED, ADD_ATTACK_SPEED_2);
      DURABILITY = register(Vault.id("durability"), IntegerAttribute::new, ADD_DURABILITY, ADD_DURABILITY_2, ADD_PLATING);
      REACH = register(new ResourceLocation("forge", "reach"), DoubleAttribute::new, ADD_REACH, ADD_REACH_2);
      GEAR_CRAFTED_BY = register(Vault.id("gear_crafted_by"), StringAttribute::new);
      GEAR_RANDOM_SEED = register(Vault.id("gear_random_seed"), LongAttribute::new);
      GEAR_TIER = register(Vault.id("gear_tier"), IntegerAttribute::new);
      GEAR_MODEL = register(Vault.id("gear_model"), IntegerAttribute::new);
      GEAR_SPECIAL_MODEL = register(Vault.id("gear_special_model"), IntegerAttribute::new);
      GEAR_COLOR = register(Vault.id("gear_color"), IntegerAttribute::new);
      GUARANTEED_MODIFIER = register(Vault.id("guaranteed_modifier"), StringAttribute::new);
      GEAR_RARITY = register(Vault.id("gear_rarity"), () -> new EnumAttribute<>(VaultGear.Rarity.class));
      GEAR_STATE = register(Vault.id("gear_state"), () -> new EnumAttribute<>(VaultGear.State.class));
      IDOL_TYPE = register(Vault.id("idol_type"), () -> new EnumAttribute<>(PlayerFavourData.VaultGodType.class));
      IDOL_AUGMENTED = register(Vault.id("idol_augmented"), BooleanAttribute::new);
      GEAR_NAME = register(Vault.id("gear_name"), StringAttribute::new);
      GEAR_LEVEL = register(Vault.id("gear_level"), FloatAttribute::new);
      GEAR_LEVEL_CHANCE = register(Vault.id("gear_level_chance"), FloatAttribute::new);
      GEAR_MAX_LEVEL = register(Vault.id("gear_max_level"), IntegerAttribute::new);
      GEAR_MODIFIERS_TO_ROLL = register(Vault.id("gear_modifiers_to_roll"), IntegerAttribute::new);
      MAX_REPAIRS = register(Vault.id("max_repairs"), IntegerAttribute::new);
      CURRENT_REPAIRS = register(Vault.id("current_repairs"), IntegerAttribute::new);
      EXTRA_LEECH_RATIO = register(Vault.id("extra_leech_ratio"), FloatAttribute::new);
      EXTRA_RESISTANCE = register(Vault.id("extra_resistance"), FloatAttribute::new);
      EXTRA_PARRY_CHANCE = register(Vault.id("extra_parry_chance"), FloatAttribute::new);
      EXTRA_HEALTH = register(Vault.id("extra_health"), FloatAttribute::new);
      EXTRA_EFFECTS = register(Vault.id("extra_effects"), EffectTalentAttribute::new);
      MIN_VAULT_LEVEL = register(Vault.id("min_vault_level"), IntegerAttribute::new, ADD_MIN_VAULT_LEVEL);
      GEAR_ROLL_TYPE = register(Vault.id("gear_roll_type"), StringAttribute::new);
      GEAR_ROLL_POOL = register(Vault.id("gear_roll_pool"), StringAttribute::new);
      SOULBOUND = register(Vault.id("soulbound"), BooleanAttribute::new, ADD_SOULBOUND);
      REFORGED = register(Vault.id("reforged"), BooleanAttribute::new, ADD_REFORGED);
      EFFECT_IMMUNITY = register(
         Vault.id("effect_immunity"),
         EffectAttribute::new,
         ADD_POISON_IMMUNITY,
         ADD_WITHER_IMMUNITY,
         ADD_HUNGER_IMMUNITY,
         ADD_MINING_FATIGUE_IMMUNITY,
         ADD_SLOWNESS_IMMUNITY,
         ADD_WEAKNESS_IMMUNITY
      );
      FEATHER_FEET = register(Vault.id("feather_feet"), FloatAttribute::new, ADD_FEATHER_FEET);
      EFFECT_CLOUD = register(Vault.id("effect_cloud"), EffectCloudAttribute::new, ADD_REGEN_CLOUD, ADD_WEAKENING_CLOUD, ADD_WITHER_CLOUD);
      COOLDOWN_REDUCTION = register(Vault.id("cooldown_reduction"), FloatAttribute::new, ADD_COOLDOWN_REDUCTION, ADD_COOLDOWN_REDUCTION_2);
      PUZZLE_COLOR = register(Vault.id("key_color"), () -> new EnumAttribute<>(PuzzleRuneBlock.Color.class));
   }

   private static Attribute register(IForgeRegistry<Attribute> registry, String name, Attribute attribute) {
      registry.register(attribute.setRegistryName(Vault.id(name)));
      return attribute;
   }

   private static <T, I extends VAttribute.Instance<T>> VAttribute<T, I> register(ResourceLocation id, Supplier<I> instance, VAttribute<T, I>... modifiers) {
      VAttribute<T, I> attribute = new VAttribute<>(id, instance, modifiers);
      REGISTRY.put(id, attribute);
      return attribute;
   }
}
