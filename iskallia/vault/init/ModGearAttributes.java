package iskallia.vault.init;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import iskallia.vault.VaultMod;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.custom.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.EffectCloudAttribute;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.AttackSpeedDecimalReader;
import iskallia.vault.gear.reader.EffectImmunityModifierReader;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
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
      "armor", VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Armor", 4766456)
   );
   public static final VaultGearAttribute<Integer> ARMOR_TOUGHNESS = attr(
      "armor_toughness",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Armor Toughness", 13302672)
   );
   public static final VaultGearAttribute<Double> ATTACK_DAMAGE = attr(
      "attack_damage",
      VaultGearAttributeType.doubleType(),
      ModGearAttributeGenerators.doubleRange(),
      ModGearAttributeReaders.addedDecimalReader("Attack Damage", 13116966)
   );
   public static final VaultGearAttribute<Double> ATTACK_SPEED = attr(
      "attack_speed", VaultGearAttributeType.doubleType(), ModGearAttributeGenerators.doubleRange(), new AttackSpeedDecimalReader("Attack Speed", 16767592)
   );
   public static final VaultGearAttribute<Double> ATTACK_SPEED_PERCENT = attr(
      "attack_speed_percent",
      VaultGearAttributeType.doubleType(),
      ModGearAttributeGenerators.doubleRange(),
      ModGearAttributeReaders.percentageReader("Attack Speed", 16767592)
   );
   public static final VaultGearAttribute<Double> REACH = attr(
      "reach", VaultGearAttributeType.doubleType(), ModGearAttributeGenerators.doubleRange(), ModGearAttributeReaders.addedDecimalReader("Reach", 8706047)
   );
   public static final VaultGearAttribute<Float> KNOCKBACK_RESISTANCE = attr(
      "knockback_resistance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Knockback Resist", 16756751)
   );
   public static final VaultGearAttribute<Float> HEALTH = attr(
      "health",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.addedRoundedDecimalReader("Health", 2293541)
   );
   public static final VaultGearAttribute<Float> MANA_REGEN_ADDITIVE_PERCENTILE = attr(
      "mana_regen", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Mana Regen", 65535)
   );
   public static final VaultGearAttribute<Integer> MANA_ADDITIVE = attr(
      "mana_additive", VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Mana", 65535)
   );
   public static final VaultGearAttribute<Float> MANA_ADDITIVE_PERCENTILE = attr(
      "mana_additive_percentile",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Mana", 65535)
   );
   public static final VaultGearAttribute<Integer> DURABILITY = attr(
      "durability", VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Durability", 14668030)
   );
   public static final VaultGearAttribute<Float> COOLDOWN_REDUCTION = attr(
      "cooldown_reduction",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Cooldown Reduction", 63668)
   );
   public static final VaultGearAttribute<Float> LEECH = attr(
      "leech", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Leech", 16716820)
   );
   public static final VaultGearAttribute<Float> RESISTANCE = attr(
      "resistance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Resistance", 16702720)
   );
   public static final VaultGearAttribute<Float> BLOCK = attr(
      "block", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Block Chance", 16109454)
   );
   public static final VaultGearAttribute<Boolean> SOULBOUND = attr(
      "soulbound", VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.booleanReader("Soulbound", 9856253)
   );
   public static final VaultGearAttribute<EffectGearAttribute> EFFECT = attr(
      "effect", EffectGearAttribute.type(), EffectGearAttribute.generator(), EffectGearAttribute.reader()
   );
   public static final VaultGearAttribute<MobEffect> EFFECT_IMMUNITY = attr(
      "effect_immunity",
      VaultGearAttributeType.registryType(ForgeRegistries.MOB_EFFECTS),
      ModGearAttributeGenerators.registry(ForgeRegistries.MOB_EFFECTS),
      new EffectImmunityModifierReader()
   );
   public static final VaultGearAttribute<EffectAvoidanceGearAttribute> EFFECT_AVOIDANCE = attr(
      "effect_avoidance", EffectAvoidanceGearAttribute.type(), EffectAvoidanceGearAttribute.generator(), EffectAvoidanceGearAttribute.reader()
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
      ModGearAttributeReaders.booleanReader("Fire Immunity", 10801083)
   );
   public static final VaultGearAttribute<Float> CRITICAL_HIT_TAKEN_REDUCTION = attr(
      "critical_hit_mitigation",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Crit Hit Resistance", 7441919)
   );
   public static final VaultGearAttribute<Float> VANILLA_CRITICAL_HIT_CHANCE = attr(
      "critical_hit_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Critical Hit Chance", 10029568)
   );
   public static final VaultGearAttribute<Float> DURABILITY_WEAR_REDUCTION = attr(
      "durability_wear_reduction",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Increased Durability", 576805)
   );
   public static final VaultGearAttribute<Float> FATAL_STRIKE_CHANCE = attr(
      "fatal_strike_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Fatal Strike Chance", 16523264)
   );
   public static final VaultGearAttribute<Float> FATAL_STRIKE_DAMAGE = attr(
      "fatal_strike_damage",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Fatal Strike Damage", 12520704)
   );
   public static final VaultGearAttribute<Float> THORNS_CHANCE = attr(
      "thorns_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Thorns Chance", 7195648)
   );
   public static final VaultGearAttribute<Float> THORNS_DAMAGE = attr(
      "thorns_damage",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Thorns Damage", 3646976)
   );
   public static final VaultGearAttribute<Integer> ON_HIT_CHAIN = attr(
      "on_hit_chain",
      VaultGearAttributeType.intType(),
      ModGearAttributeGenerators.intRange(),
      ModGearAttributeReaders.addedIntReader("Chaining Attack", 6119096)
   );
   public static final VaultGearAttribute<Integer> ON_HIT_AOE = attr(
      "on_hit_aoe", VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Cleave Range", 12085504)
   );
   public static final VaultGearAttribute<Float> ON_HIT_STUN = attr(
      "on_hit_stun",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Stun Attack Chance", 1681124)
   );
   public static final VaultGearAttribute<Float> SWEEPING_HIT_CHANCE = attr(
      "sweeping_hit_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Sweeping Hit Chance", 14727777)
   );
   public static final VaultGearAttribute<Float> ITEM_QUANTITY = attr(
      "item_quantity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Item Quantity", 15239698)
   );
   public static final VaultGearAttribute<Float> ITEM_RARITY = attr(
      "item_rarity",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Item Rarity", 15054873)
   );
   public static final VaultGearAttribute<Float> TRAP_DISARMING = attr(
      "trap_disarming",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Trap Disarm Chance", 7071001)
   );
   public static final VaultGearAttribute<Float> SOUL_CHANCE = attr(
      "soul_chance",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Soul Chance", 4718847)
   );
   public static final VaultGearAttribute<Float> DAMAGE_INCREASE = attr(
      "damage_increase",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Increased Damage", 16739072)
   );
   public static final VaultGearAttribute<Float> DAMAGE_ILLAGERS = attr(
      "damage_illagers",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Spiteful", 40882)
   );
   public static final VaultGearAttribute<Float> DAMAGE_SPIDERS = attr(
      "damage_spiders",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Baneful", 8281694)
   );
   public static final VaultGearAttribute<Float> DAMAGE_UNDEAD = attr(
      "damage_undead", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Holy", 16382128)
   );
   public static final VaultGearAttribute<Float> RESISTANCE_CAP = attr(
      "resistance_cap",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Resistance Cap", 16702720)
   );
   public static final VaultGearAttribute<Float> COOLDOWN_REDUCTION_CAP = attr(
      "cooldown_reduction_cap",
      VaultGearAttributeType.floatType(),
      ModGearAttributeGenerators.floatRange(),
      ModGearAttributeReaders.percentageReader("Cooldown Reduction Cap", 63668)
   );
   public static final VaultGearAttribute<Float> BLOCK_CAP = attr(
      "block_cap", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Block Cap", 16109454)
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
      registry.register(KNOCKBACK_RESISTANCE);
      registry.register(HEALTH);
      registry.register(MANA_REGEN_ADDITIVE_PERCENTILE);
      registry.register(MANA_ADDITIVE);
      registry.register(MANA_ADDITIVE_PERCENTILE);
      registry.register(DURABILITY);
      registry.register(COOLDOWN_REDUCTION);
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
      registry.register(THORNS_CHANCE);
      registry.register(THORNS_DAMAGE);
      registry.register(ON_HIT_CHAIN);
      registry.register(ON_HIT_AOE);
      registry.register(ON_HIT_STUN);
      registry.register(SWEEPING_HIT_CHANCE);
      registry.register(ITEM_QUANTITY);
      registry.register(ITEM_RARITY);
      registry.register(TRAP_DISARMING);
      registry.register(SOUL_CHANCE);
      registry.register(DAMAGE_INCREASE);
      registry.register(DAMAGE_ILLAGERS);
      registry.register(DAMAGE_SPIDERS);
      registry.register(DAMAGE_UNDEAD);
      registry.register(RESISTANCE_CAP);
      registry.register(COOLDOWN_REDUCTION_CAP);
      registry.register(BLOCK_CAP);
      registry.register(CRAFTED_BY);
      registry.register(GEAR_MODEL);
      registry.register(GEAR_COLOR);
      registry.register(GEAR_NAME);
      registry.register(GEAR_ROLL_TYPE_POOL);
      registry.register(GEAR_ROLL_TYPE);
      registry.register(IS_LOOT);
      registry.register(PREFIXES);
      registry.register(SUFFIXES);
      registry.register(ETCHING);
      registry.register(CRAFTING_POTENTIAL);
      registry.register(TRINKET_EFFECT);
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
      VANILLA_ATTRIBUTES.put(Attributes.KNOCKBACK_RESISTANCE, Operation.ADDITION, KNOCKBACK_RESISTANCE);
      VANILLA_ATTRIBUTES.put(Attributes.MAX_HEALTH, Operation.ADDITION, HEALTH);
      VANILLA_ATTRIBUTES.put(ModAttributes.MANA_REGEN, Operation.MULTIPLY_BASE, MANA_REGEN_ADDITIVE_PERCENTILE);
      VANILLA_ATTRIBUTES.put(ModAttributes.MANA_MAX, Operation.ADDITION, MANA_ADDITIVE);
      VANILLA_ATTRIBUTES.put(ModAttributes.MANA_MAX, Operation.MULTIPLY_BASE, MANA_ADDITIVE_PERCENTILE);
   }

   private static <T> VaultGearAttribute<T> attr(
      String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader
   ) {
      return new VaultGearAttribute<>(VaultMod.id(name), type, generator, reader);
   }
}