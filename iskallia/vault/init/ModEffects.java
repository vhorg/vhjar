package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.effect.BasicEffect;
import iskallia.vault.effect.BleedEffect;
import iskallia.vault.effect.ChilledEffect;
import iskallia.vault.effect.FreezeEffect;
import iskallia.vault.effect.GlacialShatterEffect;
import iskallia.vault.effect.ImmortalityEffect;
import iskallia.vault.effect.ManaStealEffect;
import iskallia.vault.effect.NoAiEffect;
import iskallia.vault.effect.PoisonOverrideEffect;
import iskallia.vault.effect.PylonEffect;
import iskallia.vault.effect.ThresholdEffect;
import iskallia.vault.effect.TimerAccelerationEffect;
import iskallia.vault.effect.VulnerableEffect;
import iskallia.vault.effect.WeaknessEffect;
import iskallia.vault.item.bottle.CleanseBottleEffect;
import iskallia.vault.skill.ability.effect.EmpowerAbility;
import iskallia.vault.skill.ability.effect.EmpowerIceArmourAbility;
import iskallia.vault.skill.ability.effect.EmpowerSlownessAuraAbility;
import iskallia.vault.skill.ability.effect.GhostWalkAbility;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;
import iskallia.vault.skill.ability.effect.NovaDotAbility;
import iskallia.vault.skill.ability.effect.NovaSpeedAbility;
import iskallia.vault.skill.ability.effect.RampageAbility;
import iskallia.vault.skill.ability.effect.RampageChainAbility;
import iskallia.vault.skill.ability.effect.RampageLeechAbility;
import iskallia.vault.skill.ability.effect.ShellAbility;
import iskallia.vault.skill.ability.effect.ShellPorcupineAbility;
import iskallia.vault.skill.ability.effect.ShellQuillAbility;
import iskallia.vault.skill.ability.effect.StonefallAbility;
import iskallia.vault.skill.ability.effect.StonefallColdAbility;
import iskallia.vault.skill.ability.effect.StonefallSnowAbility;
import iskallia.vault.skill.ability.effect.TauntAbility;
import iskallia.vault.skill.ability.effect.TauntCharmAbility;
import iskallia.vault.skill.ability.effect.TauntRepelAbility;
import iskallia.vault.skill.ability.effect.TotemAbility;
import iskallia.vault.skill.ability.effect.TotemManaRegenAbility;
import iskallia.vault.skill.ability.effect.TotemMobDamageAbility;
import iskallia.vault.skill.ability.effect.TotemPlayerDamageAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractBonkAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.expertise.type.AngelExpertise;
import iskallia.vault.util.PlayerRageHelper;
import java.awt.Color;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModEffects {
   public static final MobEffect GHOST_WALK = new GhostWalkAbility.GhostWalkEffect(MobEffectCategory.BENEFICIAL, Color.GRAY.getRGB(), VaultMod.id("ghost_walk"));
   public static final MobEffect GHOST_WALK_SPIRIT_WALK = new GhostWalkAbility.GhostWalkEffect(
      MobEffectCategory.BENEFICIAL, Color.GREEN.getRGB(), VaultMod.id("ghost_walk_spirit_walk")
   );
   public static final MobEffect TAUNT = new TauntAbility.TauntEffect(Color.RED.getRGB(), VaultMod.id("taunt"));
   public static final MobEffect TARGET_OVERRIDE = new TauntAbility.TargetOverrideEffect(Color.RED.getRGB(), VaultMod.id("target_override"));
   public static final MobEffect TAUNT_REPEL_PLAYER = new TauntRepelAbility.TauntRepelPlayerEffect(Color.DARK_GRAY.getRGB(), VaultMod.id("taunt_repel_player"));
   public static final MobEffect TAUNT_REPEL_MOB = new TauntRepelAbility.TauntRepelMobEffect(Color.DARK_GRAY.getRGB(), VaultMod.id("taunt_repel_mob"));
   public static final MobEffect TAUNT_CHARM = new TauntCharmAbility.TauntCharmMobEffect(Color.PINK.getRGB(), VaultMod.id("taunt_charm"));
   public static final MobEffect STONEFALL = new StonefallAbility.StonefallEffect(Color.GRAY.getRGB(), VaultMod.id("stonefall"));
   public static final MobEffect STONEFALL_COLD = new StonefallColdAbility.StonefallColdEffect(Color.CYAN.getRGB(), VaultMod.id("stonefall_cold"));
   public static final MobEffect STONEFALL_SHOCKWAVE = new StonefallSnowAbility.StonefallShockwaveEffect(
      Color.CYAN.getRGB(), VaultMod.id("stonefall_shockwave")
   );
   public static final MobEffect FREEZE = new FreezeEffect(VaultMod.id("freeze"));
   public static final NovaSpeedAbility.HypothermiaEffect HYPOTHERMIA = new NovaSpeedAbility.HypothermiaEffect(5926017, VaultMod.id("hypothermia"));
   public static final MobEffect NOVA_DOT = new NovaDotAbility.NovaDotEffect(Color.GREEN.getRGB(), VaultMod.id("nova_dot"));
   public static final TotemAbility.TotemPlayerHealthEffect TOTEM_PLAYER_HEALTH = new TotemAbility.TotemPlayerHealthEffect(
      Color.GREEN.getRGB(), VaultMod.id("totem_player_health")
   );
   public static final TotemManaRegenAbility.TotemManaRegenEffect TOTEM_MANA_REGEN = new TotemManaRegenAbility.TotemManaRegenEffect(
      Color.CYAN.getRGB(), VaultMod.id("totem_mana_regen")
   );
   public static final TotemPlayerDamageAbility.TotemPlayerDamageEffect TOTEM_PLAYER_DAMAGE = new TotemPlayerDamageAbility.TotemPlayerDamageEffect(
      Color.RED.getRGB(), VaultMod.id("totem_player_damage")
   );
   public static final TotemMobDamageAbility.TotemMobDamageEffect TOTEM_MOB_DAMAGE = new TotemMobDamageAbility.TotemMobDamageEffect(
      Color.MAGENTA.getRGB(), VaultMod.id("totem_mob_damage")
   );
   public static final ToggleAbilityEffect RAMPAGE = new RampageAbility.RampageEffect(Color.RED.getRGB(), VaultMod.id("rampage"));
   public static final ToggleAbilityEffect RAMPAGE_LEECH = new RampageLeechAbility.RampageLeechEffect(Color.RED.getRGB(), VaultMod.id("rampage_leech"));
   public static final ToggleAbilityEffect RAMPAGE_CHAIN = new RampageChainAbility.RampageChainEffect(Color.RED.getRGB(), VaultMod.id("rampage_chain"));
   public static final ToggleAbilityEffect EMPOWER = new EmpowerAbility.EmpowerEffect(Color.WHITE.getRGB(), VaultMod.id("empower"));
   public static final MobEffect EMPOWER_COOP = new EmpowerAbility.EmpowerCoopEffect(Color.WHITE.getRGB(), VaultMod.id("empower_coop"));
   public static final ToggleAbilityEffect EMPOWER_ICE_ARMOUR = new EmpowerIceArmourAbility.EmpowerIceArmourEffect(
      Color.WHITE.getRGB(), VaultMod.id("empower_ice_armour")
   );
   public static final ToggleAbilityEffect EMPOWER_SLOWNESS_AURA = new EmpowerSlownessAuraAbility.EmpowerSlownessAuraEffect(
      Color.WHITE.getRGB(), VaultMod.id("empower_slowness_aura")
   );
   public static final ToggleAbilityEffect SHELL = new ShellAbility.ShellEffect(Color.WHITE.getRGB(), VaultMod.id("shell"));
   public static final ToggleAbilityEffect SHELL_PORCUPINE = new ShellPorcupineAbility.ShellPorcupineEffect(
      Color.WHITE.getRGB(), VaultMod.id("shell_porcupine")
   );
   public static final ToggleAbilityEffect SHELL_QUILL = new ShellQuillAbility.ShellQuillEffect(Color.WHITE.getRGB(), VaultMod.id("shell_quill"));
   public static final MobEffect MANA_SHIELD = new ManaShieldAbility.ManaShieldEffect(Color.CYAN.getRGB(), VaultMod.id("mana_shield"));
   public static final MobEffect MANA_SHIELD_RETRIBUTION = new ManaShieldAbility.ManaShieldEffect(Color.RED.getRGB(), VaultMod.id("mana_shield_retribution"));
   public static final ToggleAbilityEffect ANGEL = new AngelExpertise.AngelEffect(Color.WHITE.getRGB(), VaultMod.id("angel"));
   public static final ToggleAbilityEffect SMITE = new AbstractSmiteAbility.SmiteEffect(Color.RED.getRGB(), VaultMod.id("smite"));
   public static final ToggleAbilityEffect SMITE_ARCHON = new AbstractSmiteAbility.SmiteEffect(Color.RED.getRGB(), VaultMod.id("smite_archon"));
   public static final ToggleAbilityEffect SMITE_THUNDERSTORM = new AbstractSmiteAbility.SmiteEffect(Color.RED.getRGB(), VaultMod.id("smite_thunderstorm"));
   public static final MobEffect VULNERABLE = new VulnerableEffect(MobEffectCategory.HARMFUL, Color.RED.getRGB(), VaultMod.id("vulnerable"));
   public static final MobEffect CHILLED = new ChilledEffect(MobEffectCategory.HARMFUL, Color.BLUE.getRGB(), VaultMod.id("chilled"))
      .addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, Operation.MULTIPLY_TOTAL);
   public static final PylonEffect PYLON = new PylonEffect(MobEffectCategory.BENEFICIAL, Color.GREEN.getRGB(), VaultMod.id("pylon"));
   public static final PylonEffect PYLON_OVERFLOW = new PylonEffect(MobEffectCategory.BENEFICIAL, Color.GREEN.getRGB(), VaultMod.id("pylon_overflow"));
   public static final MobEffect BATTLE_CRY = new AbstractBonkAbility.BattleCryEffect(
      MobEffectCategory.BENEFICIAL, Color.RED.getRGB(), VaultMod.id("battle_cry")
   );
   public static final MobEffect BATTLE_CRY_SPECTRAL_STRIKE = new AbstractBonkAbility.BattleCryEffect(
      MobEffectCategory.BENEFICIAL, Color.CYAN.getRGB(), VaultMod.id("battle_cry_spectral_strike")
   );
   public static final MobEffect BATTLE_CRY_LUCKY_STRIKE = new AbstractBonkAbility.BattleCryEffect(
      MobEffectCategory.BENEFICIAL, Color.GREEN.getRGB(), VaultMod.id("battle_cry_lucky_strike")
   );
   public static final MobEffect IMMORTALITY = new ImmortalityEffect(VaultMod.id("immortality"), MobEffectCategory.BENEFICIAL, -16769217);
   public static final MobEffect PURIFYING_AURA = new CleanseBottleEffect.PurifyingAuraEffect(VaultMod.id("purifying_aura"), -4675);
   public static final MobEffect MANA_STEAL = new ManaStealEffect(VaultMod.id("mana_steal"), MobEffectCategory.HARMFUL, -16777046);
   public static final MobEffect SHIELD_BASH_RETRIBUTION = new ManaShieldAbility.ManaShieldEffect(Color.CYAN.getRGB(), VaultMod.id("shield_bash_retribution"));
   public static final ThresholdEffect SORCERY = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("sorcery"));
   public static final ThresholdEffect STONESKIN = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("stoneskin"));
   public static final ThresholdEffect WITCHERY = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("witchery"));
   public static final ThresholdEffect METHODICAL = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("methodical"));
   public static final ThresholdEffect DEPLETED = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("depleted"));
   public static final ThresholdEffect LAST_STAND = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("last_stand"));
   public static final ThresholdEffect BERSERKING = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("berserking"));
   public static final ThresholdEffect FRENZY = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("frenzy"));
   public static final ThresholdEffect BLOOD_THIRST = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("blood_thirst"));
   public static final ThresholdEffect ARCANA = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("arcana"));
   public static final ThresholdEffect BLAZING = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("blazing"));
   public static final ThresholdEffect LUCKY_MOMENTUM = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("lucky_momentum"));
   public static final ThresholdEffect TREASURE_SEEKER = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("treasure_seeker"));
   public static final ThresholdEffect BOUNTIFUL_HARVEST = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("bountiful_harvest"));
   public static final ThresholdEffect PRIME_AMPLIFICATION = new ThresholdEffect(Color.RED.getRGB(), VaultMod.id("prime_amplification"));
   public static final PlayerRageHelper.RageEffect RAGE = new PlayerRageHelper.RageEffect(Color.RED.getRGB(), VaultMod.id("rage"));
   public static final MobEffect RESISTANCE = new BasicEffect(MobEffectCategory.BENEFICIAL, Color.YELLOW.getRGB(), VaultMod.id("resistance"));
   public static final MobEffect REACH = new BasicEffect(MobEffectCategory.BENEFICIAL, Color.BLUE.getRGB(), VaultMod.id("reach"))
      .addAttributeModifier((Attribute)ForgeMod.REACH_DISTANCE.get(), "c55181ae-a259-11ed-a8fc-0242ac120002", 2.0, Operation.ADDITION);
   private static final String CORRUPTION_HEALTH_MODIFIER_ID_STRING = "9a7103d4-c604-4544-88a9-86775a26af22";
   public static final UUID CORRUPTION_HEALTH_MODIFIER_ID = UUID.fromString("9a7103d4-c604-4544-88a9-86775a26af22");
   public static final MobEffect CORRUPTION = new BasicEffect(MobEffectCategory.HARMFUL, 3484199, VaultMod.id("corruption"))
      .addAttributeModifier(Attributes.MAX_HEALTH, "9a7103d4-c604-4544-88a9-86775a26af22", -2.0, Operation.ADDITION);
   public static final MobEffect TIMER_ACCELERATION = new TimerAccelerationEffect(MobEffectCategory.HARMFUL, -16448251, VaultMod.id("time_acceleration"));
   public static final MobEffect GLACIAL_SHATTER = new GlacialShatterEffect(MobEffectCategory.HARMFUL, -16711681, VaultMod.id("glacial_shatter"));
   public static final MobEffect POISON_OVERRIDE = new PoisonOverrideEffect();
   public static final MobEffect NO_AI = new NoAiEffect(VaultMod.id("noai"), -2039584);
   public static final MobEffect BLEED = new BleedEffect(VaultMod.id("bleed"));
   public static final Set<MobEffect> PREVENT_DURATION_FLASH = Set.of(
      TOTEM_PLAYER_HEALTH,
      TOTEM_MANA_REGEN,
      TOTEM_PLAYER_DAMAGE,
      SORCERY,
      STONESKIN,
      WITCHERY,
      METHODICAL,
      DEPLETED,
      LAST_STAND,
      BERSERKING,
      FRENZY,
      BLOOD_THIRST,
      ARCANA,
      BLAZING,
      LUCKY_MOMENTUM,
      TREASURE_SEEKER,
      BOUNTIFUL_HARVEST,
      PRIME_AMPLIFICATION,
      BLEED,
      SHIELD_BASH_RETRIBUTION
   );
   public static final Set<MobEffect> SYNC_TO_CLIENT_ON_MOB = Set.of(
      TAUNT_CHARM,
      TAUNT_REPEL_MOB,
      GLACIAL_SHATTER,
      CHILLED,
      VULNERABLE,
      IMMORTALITY,
      MobEffects.DAMAGE_RESISTANCE,
      MobEffects.MOVEMENT_SPEED,
      MobEffects.DAMAGE_BOOST
   );

   public static void register(Register<MobEffect> event) {
      event.getRegistry()
         .registerAll(
            new MobEffect[]{
               GHOST_WALK,
               GHOST_WALK_SPIRIT_WALK,
               RAMPAGE,
               RAMPAGE_LEECH,
               RAMPAGE_CHAIN,
               EMPOWER,
               EMPOWER_COOP,
               EMPOWER_ICE_ARMOUR,
               EMPOWER_SLOWNESS_AURA,
               SHELL,
               SHELL_PORCUPINE,
               SHELL_QUILL,
               MANA_SHIELD,
               MANA_SHIELD_RETRIBUTION,
               ANGEL,
               RAGE,
               TIMER_ACCELERATION,
               POISON_OVERRIDE,
               NO_AI,
               RESISTANCE,
               REACH,
               CORRUPTION,
               TAUNT,
               TAUNT_REPEL_MOB,
               TAUNT_REPEL_PLAYER,
               TAUNT_CHARM,
               TARGET_OVERRIDE,
               STONEFALL,
               STONEFALL_COLD,
               STONEFALL_SHOCKWAVE,
               FREEZE,
               NOVA_DOT,
               HYPOTHERMIA,
               TOTEM_PLAYER_HEALTH,
               TOTEM_MANA_REGEN,
               TOTEM_PLAYER_DAMAGE,
               TOTEM_MOB_DAMAGE,
               GLACIAL_SHATTER,
               SMITE,
               SMITE_ARCHON,
               SMITE_THUNDERSTORM,
               SORCERY,
               STONESKIN,
               WITCHERY,
               METHODICAL,
               DEPLETED,
               BERSERKING,
               FRENZY,
               BLOOD_THIRST,
               ARCANA,
               BLAZING,
               LUCKY_MOMENTUM,
               TREASURE_SEEKER,
               BOUNTIFUL_HARVEST,
               PRIME_AMPLIFICATION,
               LAST_STAND,
               VULNERABLE,
               CHILLED,
               BATTLE_CRY,
               BATTLE_CRY_SPECTRAL_STRIKE,
               BATTLE_CRY_LUCKY_STRIKE,
               PYLON,
               PYLON_OVERFLOW,
               IMMORTALITY,
               PURIFYING_AURA,
               BLEED,
               MANA_STEAL,
               SHIELD_BASH_RETRIBUTION
            }
         );
      MobEffects.POISON = POISON_OVERRIDE;
      event.getRegistry()
         .register(
            new WeaknessEffect(new ResourceLocation("weakness"), MobEffectCategory.HARMFUL, 4738376, -0.05)
               .addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0, Operation.MULTIPLY_TOTAL)
         );
   }
}
