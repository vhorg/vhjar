package iskallia.vault.init;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.ability.AbilityCooldownFlatAttribute;
import iskallia.vault.gear.attribute.ability.AbilityCooldownPercentAttribute;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.attribute.ability.AbilityManaCostFlatAttribute;
import iskallia.vault.gear.attribute.ability.AbilityManaCostPercentAttribute;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.config.EffectImmunityGenerator;
import iskallia.vault.gear.attribute.custom.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.EffectCloudAttribute;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.charm.CharmEffect;
import iskallia.vault.gear.charm.CharmEffectRegistry;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.AttackSpeedDecimalReader;
import iskallia.vault.gear.reader.EffectImmunityModifierReader;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.item.tool.ToolMaterial;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ModGearAttributes {
   private static final Table<Attribute, Operation, VaultGearAttribute<?>> VANILLA_ATTRIBUTES = HashBasedTable.create();
   public static final VaultGearAttribute<Integer> ARMOR = attr(
      "armor",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Armor", 4766456),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Integer> ARMOR_TOUGHNESS = attr(
      "armor_toughness",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Armor Toughness", 13302672),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Double> ATTACK_DAMAGE = attr(
      "attack_damage",
      VaultGearAttributeType.doubleType(),
      ModGearAttributeGenerators.doubleRange(),
      ModGearAttributeReaders.addedDecimalReader("Attack Damage", 13116966),
      VaultGearAttributeComparator.doubleComparator()
   );
   public static final VaultGearAttribute<Double> ATTACK_SPEED = attr(
      "attack_speed",
      VaultGearAttributeType.doubleType(),
      ModGearAttributeGenerators.doubleRange(),
      new AttackSpeedDecimalReader("Attack Speed", 16767592),
      VaultGearAttributeComparator.doubleComparator()
   );
   public static final VaultGearAttribute<Double> ATTACK_SPEED_PERCENT = attr(
      "attack_speed_percent",
      VaultGearAttributeType.doubleType(),
      ModGearAttributeGenerators.doubleRange(),
      ModGearAttributeReaders.percentageReader("Attack Speed", 16767592),
      VaultGearAttributeComparator.doubleComparator()
   );
   public static final VaultGearAttribute<Double> REACH = attr(
      "reach",
      VaultGearAttributeType.doubleType(),
      ModGearAttributeGenerators.doubleRange(),
      ModGearAttributeReaders.addedDecimalReader("Reach", 8706047),
      VaultGearAttributeComparator.doubleComparator()
   );
   public static final VaultGearAttribute<Double> ATTACK_RANGE = attr(
      "attack_range",
      VaultGearAttributeType.doubleType(),
      ModGearAttributeGenerators.doubleRange(),
      ModGearAttributeReaders.addedDecimalReader("Attack Range", 8706047),
      VaultGearAttributeComparator.doubleComparator()
   );
   public static final VaultGearAttribute<Float> KNOCKBACK_RESISTANCE = attr(
      "knockback_resistance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Knockback Resist", 16756751),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> HEALTH = attr(
      "health",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedRoundedDecimalReader("Health", 2293541),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> MANA_REGEN_ADDITIVE_PERCENTILE = attr(
      "mana_regen",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Mana Regen", 65535),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Integer> MANA_ADDITIVE = attr(
      "mana_additive",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Mana", 65535),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Float> MANA_ADDITIVE_PERCENTILE = attr(
      "mana_additive_percentile",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Mana", 65535),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> HEALING_EFFECTIVENESS = attr(
      "healing_effectiveness",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Healing Efficiency", 9371426),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Integer> DURABILITY = attr(
      "durability",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Durability", 14668030),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Float> ABILITY_POWER = attr(
      "ability_power",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedDecimalReader("Ability Power", 16711883),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> ABILITY_POWER_PERCENTILE = attr(
      "ability_power_percentile",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedDecimalReader("Ability Power Percentile", 16711883),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> ABILITY_POWER_PERCENT = attr(
      "ability_power_percent",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.increasedReader("Ability Power", 15622139),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> MOVEMENT_SPEED = attr(
      "movement_speed",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Movement Speed", 16769128),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> COOLDOWN_REDUCTION = attr(
      "cooldown_reduction",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Cooldown Reduction", 63668),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> COOLDOWN_REDUCTION_PERCENTILE = attr(
      "cooldown_reduction_percentile",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Cooldown Reduction Percentile", 63668),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> LEECH = attr(
      "leech",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Leech", 16716820),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> RESISTANCE = attr(
      "resistance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Resistance", 16702720),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> BLOCK = attr(
      "block",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Block Chance", 16109454),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Boolean> SOULBOUND = attr(
      "soulbound",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Soulbound", 9856253),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<EffectGearAttribute> EFFECT = attr(
      "effect", EffectGearAttribute.type(), EffectGearAttribute.generator(), EffectGearAttribute.reader()
   );
   public static final VaultGearAttribute<MobEffect> EFFECT_IMMUNITY = attr(
      "effect_immunity", VaultGearAttributeType.registryType(ForgeRegistries.MOB_EFFECTS), new EffectImmunityGenerator(), new EffectImmunityModifierReader()
   );
   public static final VaultGearAttribute<EffectAvoidanceGearAttribute> EFFECT_AVOIDANCE = attr(
      "effect_avoidance",
      EffectAvoidanceGearAttribute.type(),
      EffectAvoidanceGearAttribute.generator(),
      EffectAvoidanceGearAttribute.reader(),
      EffectAvoidanceGearAttribute.comparator()
   );
   public static final VaultGearAttribute<EffectCloudAttribute> EFFECT_CLOUD = attr(
      "effect_cloud", EffectCloudAttribute.type(), EffectCloudAttribute.generator(), EffectCloudAttribute.reader(false)
   );
   public static final VaultGearAttribute<EffectCloudAttribute> EFFECT_CLOUD_WHEN_HIT = attr(
      "effect_cloud_when_hit", EffectCloudAttribute.type(), EffectCloudAttribute.generator(), EffectCloudAttribute.reader(true)
   );
   public static final VaultGearAttribute<Boolean> IS_FIRE_IMMUNE = attr(
      "fire_immunity",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Fire Immunity", 10801083),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Float> CRITICAL_HIT_TAKEN_REDUCTION = attr(
      "critical_hit_mitigation",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Crit Hit Resistance", 7441919),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> VANILLA_CRITICAL_HIT_CHANCE = attr(
      "critical_hit_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Critical Hit Chance", 10029568),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DURABILITY_WEAR_REDUCTION = attr(
      "durability_wear_reduction",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Increased Durability", 576805),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> FATAL_STRIKE_CHANCE = attr(
      "fatal_strike_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Fatal Strike Chance", 16523264),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> FATAL_STRIKE_DAMAGE = attr(
      "fatal_strike_damage",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Fatal Strike Damage", 12520704),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> LUCKY_HIT_CHANCE = attr(
      "lucky_hit_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Lucky Hit Chance", 7206307),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> LUCKY_HIT_CHANCE_PERCENTILE = attr(
      "lucky_hit_chance_percentile",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Lucky Hit Chance Percent Of Base", 7206307),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> THORNS_CHANCE = attr(
      "thorns_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Thorns Chance", 16761035),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> THORNS_DAMAGE = attr(
      "thorns_damage",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Thorns Damage", 15507136),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> THORNS_DAMAGE_FLAT = attr(
      "thorns_damage_flat",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedDecimalReader("Thorns Damage", 15507136),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Integer> ON_HIT_CHAIN = attr(
      "on_hit_chain",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Chaining Attack", 6119096),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Integer> ON_HIT_AOE = attr(
      "on_hit_aoe",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Cleave Range", 12085504),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Float> ON_HIT_STUN = attr(
      "on_hit_stun",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Stun Attack Chance", 1681124),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> SWEEPING_HIT_CHANCE = attr(
      "sweeping_hit_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Sweeping Hit Chance", 14727777),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> SHOCKING_HIT_CHANCE = attr(
      "shocking_hit_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Shocking Hit Chance", 13819995),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> ITEM_QUANTITY = attr(
      "item_quantity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Item Quantity", 15239698),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> ITEM_RARITY = attr(
      "item_rarity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Item Rarity", 15400704),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> TRAP_DISARMING = attr(
      "trap_disarming",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Trap Disarm Chance", 8471551),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> SOUL_CHANCE = attr(
      "soul_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Soul Chance", 4718847),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> SOUL_CHANCE_PERCENTILE = attr(
      "soul_chance_percentile",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Soul Chance Percentile", 4718847),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> AREA_OF_EFFECT = attr(
      "area_of_effect",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.increasedReader("Area of Effect", 15319925),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_INCREASE = attr(
      "damage_increase",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.increasedReader("Attack Damage", 16739072),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_ILLAGERS = attr(
      "damage_illagers",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Illager Damage", 40882),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_SPIDERS = attr(
      "damage_spiders",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Arthropods Damage", 14338221),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_UNDEAD = attr(
      "damage_undead",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Undead Damage", 16382128),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_NETHER = attr(
      "damage_nether",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Nether Damage", 16749406),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_CHAMPION = attr(
      "damage_champion",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.increasedReader("Champion Damage", 6213132),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_DUNGEON = attr(
      "damage_dungeon",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Dungeon Damage", 823502),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_TANK = attr(
      "damage_tank",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Tank Damage", 13513996),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_HORDE = attr(
      "damage_horde",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Horde Damage", 13094412),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_ASSASSIN = attr(
      "damage_assassin",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Assassin Damage", 11881191),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> DAMAGE_DWELLER = attr(
      "damage_dweller",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Dweller Damage", 15895195),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> VELARA_AFFINITY = attr(
      "velara_affinity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Velara Affinity", VaultGod.VELARA.getColor()),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> TENOS_AFFINITY = attr(
      "tenos_affinity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Tenos Affinity", VaultGod.TENOS.getColor()),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> WENDARR_AFFINITY = attr(
      "wendarr_affinity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Wendarr Affinity", VaultGod.WENDARR.getColor()),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> IDONA_AFFINITY = attr(
      "idona_affinity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Idona Affinity", VaultGod.IDONA.getColor()),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> RESISTANCE_CAP = attr(
      "resistance_cap",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Resistance Cap", 16702720),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> COOLDOWN_REDUCTION_CAP = attr(
      "cooldown_reduction_cap",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Cooldown Reduction Cap", 63668),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> BLOCK_CAP = attr(
      "block_cap",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Block Cap", 16109454),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<ToolMaterial> TOOL_MATERIAL = attr(
      "tool_material", VaultGearAttributeType.enumType(ToolMaterial.class), ModGearAttributeGenerators.noneGenerator(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Integer> TOOL_CAPACITY = attr(
      "tool_capacity",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.none(),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Integer> JEWEL_SIZE = attr(
      "jewel_size",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Size", 14540253),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Float> MINING_SPEED = attr(
      "mining_speed",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedDecimalReader("Mining Speed", 4766456),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> COPIOUSLY = attr(
      "copiously",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Copiously", 16205696),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> IMMORTALITY = attr(
      "immortality",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Vanilla Immortality", 11505091),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Boolean> SMELTING = attr(
      "smelting",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Smelting", 16729344),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> PULVERIZING = attr(
      "pulverizing",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Pulverizing", 7582579),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> HYDROVOID = attr(
      "hydrovoid",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Hydrovoid", 2177962),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Integer> HAMMER_SIZE = attr(
      "hammer_size",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Hammer Size", 2479269),
      VaultGearAttributeComparator.intComparator()
   );
   public static final VaultGearAttribute<Boolean> WOODEN_AFFINITY = attr(
      "wooden_affinity",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Wooden Affinity", 11819275),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> ORNATE_AFFINITY = attr(
      "ornate_affinity",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Ornate Affinity", 15476005),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> GILDED_AFFINITY = attr(
      "gilded_affinity",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Gilded Affinity", 13411090),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> LIVING_AFFINITY = attr(
      "living_affinity",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Living Affinity", 7536449),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> COIN_AFFINITY = attr(
      "coin_affinity",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Coin Affinity", 16776960),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> PICKING = attr(
      "picking",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Picking", 15395562),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> AXING = attr(
      "axing",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Axing", 12889209),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> SHOVELLING = attr(
      "shovelling",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Shovelling", 14870175),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> HAMMERING = attr(
      "hammering",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Hammering", 2479269),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Boolean> REAPING = attr(
      "reaping",
      VaultGearAttributeType.booleanType(),
      ModGearAttributeGenerators.booleanFlag(),
      ModGearAttributeReaders.booleanReader("Reaping", 3902016),
      VaultGearAttributeComparator.booleanComparator()
   );
   public static final VaultGearAttribute<Float> RANGE = attr(
      "range",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedDecimalReader("Range", 16364415),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<Float> VELOCITY = attr(
      "velocity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedRoundedDecimalReader("Velocity", 14608287, 100.0F),
      VaultGearAttributeComparator.floatComparator()
   );
   public static final VaultGearAttribute<AbilityLevelAttribute> ABILITY_LEVEL = attr(
      "added_ability_level", AbilityLevelAttribute.type(), AbilityLevelAttribute.generator(), AbilityLevelAttribute.reader()
   );
   public static final VaultGearAttribute<AbilityCooldownFlatAttribute> ABILITY_COOLDOWN_FLAT = attr(
      "ability_cooldown_flat", AbilityCooldownFlatAttribute.type(), AbilityCooldownFlatAttribute.generator(), AbilityCooldownFlatAttribute.reader()
   );
   public static final VaultGearAttribute<AbilityCooldownPercentAttribute> ABILITY_COOLDOWN_PERCENT = attr(
      "ability_cooldown_percent", AbilityCooldownPercentAttribute.type(), AbilityCooldownPercentAttribute.generator(), AbilityCooldownPercentAttribute.reader()
   );
   public static final VaultGearAttribute<AbilityManaCostFlatAttribute> ABILITY_MANACOST_FLAT = attr(
      "ability_mana_cost_flat", AbilityManaCostFlatAttribute.type(), AbilityManaCostFlatAttribute.generator(), AbilityManaCostFlatAttribute.reader()
   );
   public static final VaultGearAttribute<AbilityManaCostPercentAttribute> ABILITY_MANACOST_PERCENT = attr(
      "ability_mana_cost_percent",
      AbilityManaCostPercentAttribute.type(),
      AbilityManaCostPercentAttribute.generator(),
      AbilityManaCostPercentAttribute.reader()
   );
   public static final VaultGearAttribute<SpecialAbilityGearAttribute<?, ?>> ABILITY_SPECIAL_MODIFICATION = new VaultGearAttribute<>(
      VaultMod.id("ability_special_modification"),
      SpecialAbilityGearAttribute.type(),
      (ConfigurableAttributeGenerator<SpecialAbilityGearAttribute<?, ?>, ?>)SpecialAbilityGearAttribute.generator(),
      SpecialAbilityGearAttribute.reader()
   );
   public static final VaultGearAttribute<String> CRAFTED_BY = attr(
      "crafted_by", VaultGearAttributeType.stringType(), ModGearAttributeGenerators.stringConstant(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<ResourceLocation> GEAR_MODEL = attr(
      "gear_model", VaultGearAttributeType.identifierType(), ModGearAttributeGenerators.noneGenerator(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Integer> GEAR_COLOR = attr(
      "gear_color", VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<String> GEAR_NAME = attr(
      "gear_name", VaultGearAttributeType.stringType(), ModGearAttributeGenerators.stringConstant(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<String> GEAR_ROLL_TYPE = attr(
      "gear_roll_type", VaultGearAttributeType.stringType(), ModGearAttributeGenerators.stringConstant(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<String> GEAR_ROLL_TYPE_POOL = attr(
      "gear_roll_type_pool", VaultGearAttributeType.stringType(), ModGearAttributeGenerators.stringConstant(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Boolean> IS_LOOT = attr(
      "is_loot", VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Boolean> IS_ABYSSAL = attr(
      "is_abyssal", VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Boolean> IS_CORRUPTED = attr(
      "is_corrupted", VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Integer> PREFIXES = attr(
      "prefixes", VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Integer> SUFFIXES = attr(
      "suffixes", VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<EtchingSet<?>> ETCHING = attr(
      "etching", VaultGearAttributeType.registryType(EtchingRegistry.getRegistry()), ModGearAttributeGenerators.noneGenerator(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<Integer> CRAFTING_POTENTIAL = attr(
      "crafting_potential", VaultGearAttributeType.intType(), ModGearAttributeGenerators.noneGenerator(), ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<TrinketEffect<?>> TRINKET_EFFECT = attr(
      "trinket",
      VaultGearAttributeType.registryType(TrinketEffectRegistry.getRegistry()),
      ModGearAttributeGenerators.noneGenerator(),
      ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<CharmEffect<?>> CHARM_EFFECT = attr(
      "charm",
      VaultGearAttributeType.registryType(CharmEffectRegistry.getRegistry()),
      ModGearAttributeGenerators.noneGenerator(),
      ModGearAttributeReaders.none()
   );
   public static final VaultGearAttribute<VaultGearState> STATE = attr(
      "state", VaultGearAttributeType.enumType(VaultGearState.class), ModGearAttributeGenerators.noneGenerator(), ModGearAttributeReaders.none()
   );

   @Nullable
   public static VaultGearAttribute<?> getGearAttribute(Attribute vanillaAttribute, Operation operation) {
      return (VaultGearAttribute<?>)VANILLA_ATTRIBUTES.get(vanillaAttribute, operation);
   }

   public static void init(Register<VaultGearAttribute<?>> event) {
      IForgeRegistry<VaultGearAttribute<?>> registry = event.getRegistry();
      registry.register(ARMOR);
      registry.register(ARMOR_TOUGHNESS);
      registry.register(ATTACK_DAMAGE);
      registry.register(ATTACK_SPEED);
      registry.register(ATTACK_SPEED_PERCENT);
      registry.register(REACH);
      registry.register(ATTACK_RANGE);
      registry.register(KNOCKBACK_RESISTANCE);
      registry.register(HEALTH);
      registry.register(MANA_REGEN_ADDITIVE_PERCENTILE);
      registry.register(MANA_ADDITIVE);
      registry.register(MANA_ADDITIVE_PERCENTILE);
      registry.register(HEALING_EFFECTIVENESS);
      registry.register(MOVEMENT_SPEED);
      registry.register(ABILITY_POWER);
      registry.register(ABILITY_POWER_PERCENTILE);
      registry.register(ABILITY_POWER_PERCENT);
      registry.register(DURABILITY);
      registry.register(COOLDOWN_REDUCTION);
      registry.register(COOLDOWN_REDUCTION_PERCENTILE);
      registry.register(LEECH);
      registry.register(RESISTANCE);
      registry.register(BLOCK);
      registry.register(SOULBOUND);
      registry.register(EFFECT);
      registry.register(EFFECT_IMMUNITY);
      registry.register(EFFECT_AVOIDANCE);
      registry.register(EFFECT_CLOUD);
      registry.register(EFFECT_CLOUD_WHEN_HIT);
      registry.register(IS_FIRE_IMMUNE);
      registry.register(CRITICAL_HIT_TAKEN_REDUCTION);
      registry.register(VANILLA_CRITICAL_HIT_CHANCE);
      registry.register(DURABILITY_WEAR_REDUCTION);
      registry.register(FATAL_STRIKE_CHANCE);
      registry.register(FATAL_STRIKE_DAMAGE);
      registry.register(LUCKY_HIT_CHANCE);
      registry.register(LUCKY_HIT_CHANCE_PERCENTILE);
      registry.register(THORNS_CHANCE);
      registry.register(THORNS_DAMAGE);
      registry.register(THORNS_DAMAGE_FLAT);
      registry.register(ON_HIT_CHAIN);
      registry.register(ON_HIT_AOE);
      registry.register(ON_HIT_STUN);
      registry.register(SWEEPING_HIT_CHANCE);
      registry.register(SHOCKING_HIT_CHANCE);
      registry.register(ITEM_QUANTITY);
      registry.register(ITEM_RARITY);
      registry.register(TRAP_DISARMING);
      registry.register(SOUL_CHANCE);
      registry.register(SOUL_CHANCE_PERCENTILE);
      registry.register(AREA_OF_EFFECT);
      registry.register(DAMAGE_INCREASE);
      registry.register(DAMAGE_ILLAGERS);
      registry.register(DAMAGE_SPIDERS);
      registry.register(DAMAGE_UNDEAD);
      registry.register(DAMAGE_NETHER);
      registry.register(DAMAGE_CHAMPION);
      registry.register(DAMAGE_DUNGEON);
      registry.register(DAMAGE_TANK);
      registry.register(DAMAGE_HORDE);
      registry.register(DAMAGE_ASSASSIN);
      registry.register(DAMAGE_DWELLER);
      registry.register(VELARA_AFFINITY);
      registry.register(TENOS_AFFINITY);
      registry.register(WENDARR_AFFINITY);
      registry.register(IDONA_AFFINITY);
      registry.register(RESISTANCE_CAP);
      registry.register(COOLDOWN_REDUCTION_CAP);
      registry.register(BLOCK_CAP);
      registry.register(TOOL_MATERIAL);
      registry.register(TOOL_CAPACITY);
      registry.register(JEWEL_SIZE);
      registry.register(MINING_SPEED);
      registry.register(COPIOUSLY);
      registry.register(IMMORTALITY);
      registry.register(PULVERIZING);
      registry.register(HYDROVOID);
      registry.register(HAMMER_SIZE);
      registry.register(SMELTING);
      registry.register(WOODEN_AFFINITY);
      registry.register(ORNATE_AFFINITY);
      registry.register(GILDED_AFFINITY);
      registry.register(LIVING_AFFINITY);
      registry.register(COIN_AFFINITY);
      registry.register(PICKING);
      registry.register(AXING);
      registry.register(SHOVELLING);
      registry.register(HAMMERING);
      registry.register(REAPING);
      registry.register(RANGE);
      registry.register(VELOCITY);
      registry.register(ABILITY_LEVEL);
      registry.register(ABILITY_COOLDOWN_FLAT);
      registry.register(ABILITY_COOLDOWN_PERCENT);
      registry.register(ABILITY_MANACOST_FLAT);
      registry.register(ABILITY_MANACOST_PERCENT);
      registry.register(ABILITY_SPECIAL_MODIFICATION);
      registry.register(CRAFTED_BY);
      registry.register(GEAR_MODEL);
      registry.register(GEAR_COLOR);
      registry.register(GEAR_NAME);
      registry.register(GEAR_ROLL_TYPE_POOL);
      registry.register(GEAR_ROLL_TYPE);
      registry.register(IS_LOOT);
      registry.register(IS_ABYSSAL);
      registry.register(IS_CORRUPTED);
      registry.register(PREFIXES);
      registry.register(SUFFIXES);
      registry.register(ETCHING);
      registry.register(CRAFTING_POTENTIAL);
      registry.register(TRINKET_EFFECT);
      registry.register(CHARM_EFFECT);
      registry.register(STATE);
   }

   public static void registerVanillaAssociations() {
      VANILLA_ATTRIBUTES.clear();
      VANILLA_ATTRIBUTES.put(Attributes.ARMOR, Operation.ADDITION, ARMOR);
      VANILLA_ATTRIBUTES.put(Attributes.ARMOR_TOUGHNESS, Operation.ADDITION, ARMOR_TOUGHNESS);
      VANILLA_ATTRIBUTES.put(Attributes.ATTACK_DAMAGE, Operation.ADDITION, ATTACK_DAMAGE);
      VANILLA_ATTRIBUTES.put(Attributes.ATTACK_SPEED, Operation.ADDITION, ATTACK_SPEED);
      VANILLA_ATTRIBUTES.put(Attributes.ATTACK_SPEED, Operation.MULTIPLY_BASE, ATTACK_SPEED_PERCENT);
      VANILLA_ATTRIBUTES.put((Attribute)ForgeMod.REACH_DISTANCE.get(), Operation.ADDITION, REACH);
      VANILLA_ATTRIBUTES.put((Attribute)ForgeMod.ATTACK_RANGE.get(), Operation.ADDITION, ATTACK_RANGE);
      VANILLA_ATTRIBUTES.put(Attributes.KNOCKBACK_RESISTANCE, Operation.ADDITION, KNOCKBACK_RESISTANCE);
      VANILLA_ATTRIBUTES.put(Attributes.MAX_HEALTH, Operation.ADDITION, HEALTH);
      VANILLA_ATTRIBUTES.put(ModAttributes.MANA_REGEN, Operation.MULTIPLY_BASE, MANA_REGEN_ADDITIVE_PERCENTILE);
      VANILLA_ATTRIBUTES.put(ModAttributes.MANA_MAX, Operation.ADDITION, MANA_ADDITIVE);
      VANILLA_ATTRIBUTES.put(ModAttributes.MANA_MAX, Operation.MULTIPLY_BASE, MANA_ADDITIVE_PERCENTILE);
      VANILLA_ATTRIBUTES.put(Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, MOVEMENT_SPEED);
   }

   private static <T> VaultGearAttribute<T> attr(
      String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader
   ) {
      return attr(name, type, generator, reader, null);
   }

   private static <T> VaultGearAttribute<T> attr(
      String name,
      VaultGearAttributeType<T> type,
      ConfigurableAttributeGenerator<T, ?> generator,
      VaultGearModifierReader<T> reader,
      @Nullable VaultGearAttributeComparator<T> comparator
   ) {
      return new VaultGearAttribute<>(VaultMod.id(name), type, generator, reader, comparator);
   }
}
