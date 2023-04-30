package iskallia.vault.init;

import iskallia.vault.skill.ability.component.AbilityLabelBindingRegistry;
import iskallia.vault.skill.ability.component.AbilityLabelFormatters;
import iskallia.vault.skill.ability.component.IAbilityLabelBinding;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.skill.ability.effect.DashDamageAbility;
import iskallia.vault.skill.ability.effect.DashWarpAbility;
import iskallia.vault.skill.ability.effect.EmpowerAbility;
import iskallia.vault.skill.ability.effect.EmpowerIceArmourAbility;
import iskallia.vault.skill.ability.effect.ExecuteAbility;
import iskallia.vault.skill.ability.effect.FarmerAbility;
import iskallia.vault.skill.ability.effect.FarmerAnimalAbility;
import iskallia.vault.skill.ability.effect.FarmerCactusAbility;
import iskallia.vault.skill.ability.effect.FarmerMelonAbility;
import iskallia.vault.skill.ability.effect.GhostWalkAbility;
import iskallia.vault.skill.ability.effect.GhostWalkSpiritAbility;
import iskallia.vault.skill.ability.effect.HealAbility;
import iskallia.vault.skill.ability.effect.HealEffectAbility;
import iskallia.vault.skill.ability.effect.HealGroupAbility;
import iskallia.vault.skill.ability.effect.JavelinAbility;
import iskallia.vault.skill.ability.effect.JavelinPiercingAbility;
import iskallia.vault.skill.ability.effect.JavelinScatterAbility;
import iskallia.vault.skill.ability.effect.JavelinSightAbility;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;
import iskallia.vault.skill.ability.effect.ManaShieldRetributionAbility;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import iskallia.vault.skill.ability.effect.MegaJumpBreakDownAbility;
import iskallia.vault.skill.ability.effect.MegaJumpBreakUpAbility;
import iskallia.vault.skill.ability.effect.NovaAbility;
import iskallia.vault.skill.ability.effect.NovaDotAbility;
import iskallia.vault.skill.ability.effect.NovaSpeedAbility;
import iskallia.vault.skill.ability.effect.RampageAbility;
import iskallia.vault.skill.ability.effect.RampageChainAbility;
import iskallia.vault.skill.ability.effect.RampageLeechAbility;
import iskallia.vault.skill.ability.effect.ShellAbility;
import iskallia.vault.skill.ability.effect.ShellPorcupineAbility;
import iskallia.vault.skill.ability.effect.ShellQuillAbility;
import iskallia.vault.skill.ability.effect.SmiteAbility;
import iskallia.vault.skill.ability.effect.SmiteArchonAbility;
import iskallia.vault.skill.ability.effect.SmiteThunderstormAbility;
import iskallia.vault.skill.ability.effect.StonefallAbility;
import iskallia.vault.skill.ability.effect.StonefallColdAbility;
import iskallia.vault.skill.ability.effect.StonefallSnowAbility;
import iskallia.vault.skill.ability.effect.SummonEternalAbility;
import iskallia.vault.skill.ability.effect.TauntAbility;
import iskallia.vault.skill.ability.effect.TauntCharmAbility;
import iskallia.vault.skill.ability.effect.TauntRepelAbility;
import iskallia.vault.skill.ability.effect.TotemAbility;
import iskallia.vault.skill.ability.effect.TotemManaRegenAbility;
import iskallia.vault.skill.ability.effect.TotemMobDamageAbility;
import iskallia.vault.skill.ability.effect.TotemPlayerDamageAbility;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import iskallia.vault.skill.ability.effect.VeinMinerDurabilityAbility;
import iskallia.vault.skill.ability.effect.VeinMinerFortuneAbility;
import iskallia.vault.skill.ability.effect.VeinMinerVoidAbility;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.IInstantManaAbility;
import iskallia.vault.skill.ability.effect.spi.core.IPerSecondManaAbility;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

public class ModAbilityLabelBindings {
   public static void register() {
      AbilityLabelBindingRegistry.clear();
      register(DashAbility.class, Map.of("distance", ability -> AbilityLabelFormatters.integer(ability.getExtraDistance())));
      register(
         DashDamageAbility.class,
         Map.of(
            "distance",
            ability -> AbilityLabelFormatters.integer(ability.getExtraDistance()),
            "damage",
            ability -> AbilityLabelFormatters.percentRounded(ability.getAttackDamagePercentPerDash())
         )
      );
      register(DashWarpAbility.class, Map.of("force", ability -> AbilityLabelFormatters.decimal(ability.getProjectileLaunchForce())));
      register(
         ExecuteAbility.class,
         Map.of(
            "damage",
            ability -> AbilityLabelFormatters.percentRounded(ability.getDamageHealthPercentage()),
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getEffectDurationTicks())
         )
      );
      register(
         FarmerAbility.class,
         Map.of(
            "delay",
            ability -> AbilityLabelFormatters.ticks(ability.getTickDelay()),
            "rangeHorizontal",
            ability -> AbilityLabelFormatters.integer(ability.getHorizontalRange()),
            "rangeVertical",
            ability -> AbilityLabelFormatters.integer(ability.getVerticalRange())
         )
      );
      register(
         FarmerCactusAbility.class,
         Map.of(
            "delay",
            ability -> AbilityLabelFormatters.ticks(ability.getTickDelay()),
            "rangeHorizontal",
            ability -> AbilityLabelFormatters.integer(ability.getHorizontalRange()),
            "rangeVertical",
            ability -> AbilityLabelFormatters.integer(ability.getVerticalRange())
         )
      );
      register(
         FarmerMelonAbility.class,
         Map.of(
            "delay",
            ability -> AbilityLabelFormatters.ticks(ability.getTickDelay()),
            "rangeHorizontal",
            ability -> AbilityLabelFormatters.integer(ability.getHorizontalRange()),
            "rangeVertical",
            ability -> AbilityLabelFormatters.integer(ability.getVerticalRange())
         )
      );
      register(
         FarmerAnimalAbility.class,
         Map.of(
            "delay",
            ability -> AbilityLabelFormatters.ticks(ability.getTickDelay()),
            "rangeHorizontal",
            ability -> AbilityLabelFormatters.integer(ability.getHorizontalRange()),
            "rangeVertical",
            ability -> AbilityLabelFormatters.integer(ability.getVerticalRange()),
            "chance",
            ability -> AbilityLabelFormatters.percentRounded(ability.getAdultChance())
         )
      );
      register(GhostWalkAbility.class, Map.of("duration", ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks())));
      register(GhostWalkSpiritAbility.class, Map.of("duration", ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks())));
      register(HealAbility.class, Map.of("heal", ability -> AbilityLabelFormatters.decimal(ability.getFlatLifeHealed())));
      register(HealEffectAbility.class);
      register(
         HealGroupAbility.class,
         Map.of(
            "heal",
            ability -> AbilityLabelFormatters.decimal(ability.getFlatLifeHealed()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius())
         )
      );
      register(
         HunterAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getSearchRadius()),
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks())
         )
      );
      register(
         ManaShieldAbility.class,
         Map.of(
            "absorb",
            ability -> AbilityLabelFormatters.percentRounded(ability.getPercentageDamageAbsorbed()),
            "manaPerDamage",
            ability -> AbilityLabelFormatters.decimal(ability.getManaPerDamageScalar())
         )
      );
      register(
         ManaShieldRetributionAbility.class,
         Map.of(
            "absorb",
            ability -> AbilityLabelFormatters.percentRounded(ability.getPercentageDamageAbsorbed()),
            "manaPerDamage",
            ability -> AbilityLabelFormatters.decimal(ability.getManaPerDamageScalar()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getDamageRadius()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getPercentageDamageDealt())
         )
      );
      register(MegaJumpAbility.class, Map.of("rangeVertical", ability -> AbilityLabelFormatters.integer(ability.getHeight())));
      register(MegaJumpBreakUpAbility.class, Map.of("rangeVertical", ability -> AbilityLabelFormatters.integer(ability.getHeight())));
      register(
         MegaJumpBreakDownAbility.class,
         Map.of(
            "rangeVertical",
            ability -> AbilityLabelFormatters.integer(ability.getHeight()),
            "radius",
            ability -> AbilityLabelFormatters.integer(ability.getRadius())
         )
      );
      register(
         NovaAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getUnmodifiedRadius()),
            "damage",
            ability -> AbilityLabelFormatters.percentRounded(ability.getPercentAttackDamageDealt()),
            "knockback",
            ability -> AbilityLabelFormatters.percentRounded(ability.getKnockbackStrengthMultiplier())
         )
      );
      register(
         NovaDotAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getUnmodifiedRadius()),
            "damage",
            ability -> AbilityLabelFormatters.percentRounded(ability.getPercentAttackDamageDealt()),
            "duration",
            ability -> AbilityLabelFormatters.seconds(ability.getDurationSeconds())
         )
      );
      register(
         NovaSpeedAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getUnmodifiedRadius()),
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "slowness",
            ability -> AbilityLabelFormatters.integer(ability.getAmplifier())
         )
      );
      register(RampageAbility.class, Map.of("damage", ability -> AbilityLabelFormatters.percentRounded(ability.getUnmodifiedDamageIncrease())));
      register(RampageChainAbility.class, Map.of("chains", ability -> AbilityLabelFormatters.integer(ability.getAdditionalChainCount())));
      register(RampageLeechAbility.class, Map.of("leech", ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getLeechPercent())));
      register(
         StonefallAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "knockback",
            ability -> AbilityLabelFormatters.decimal(ability.getKnockbackMultiplier()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
            "damageReduction",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getDamageReduction())
         )
      );
      register(
         StonefallSnowAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
            "damageReduction",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getDamageReduction()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getDamageMultiplier())
         )
      );
      register(
         StonefallColdAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
            "damageReduction",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getDamageReduction()),
            "freezeDuration",
            ability -> AbilityLabelFormatters.ticks(ability.getFreezeTicks()),
            "amplifier",
            ability -> AbilityLabelFormatters.integer(ability.getAmplifier())
         )
      );
      register(
         SummonEternalAbility.class,
         Map.of(
            "eternals",
            ability -> AbilityLabelFormatters.integer(ability.getNumberOfEternals()),
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDespawnTime()),
            "chance",
            ability -> AbilityLabelFormatters.percentRounded(ability.getAncientChance())
         )
      );
      register(
         EmpowerAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getBuffRadius()),
            "speed",
            ability -> AbilityLabelFormatters.percentRounded(ability.getSpeedPercentAdded())
         )
      );
      register(
         EmpowerIceArmourAbility.class,
         Map.of(
            "slowness",
            ability -> AbilityLabelFormatters.integer(ability.getSlownessAmplifier()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius())
         )
      );
      register(
         TauntAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getUnmodifiedRadius()),
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "damageIncrease",
            ability -> AbilityLabelFormatters.percentRounded(ability.getDamageModifier())
         )
      );
      register(
         TauntRepelAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getUnmodifiedRadius()),
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "distance",
            ability -> AbilityLabelFormatters.decimal(ability.getRepelForce())
         )
      );
      register(
         TauntCharmAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getUnmodifiedRadius()),
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks()),
            "maxTargets",
            ability -> AbilityLabelFormatters.integer(ability.getMaxCharmedMobs()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getPercentPlayerDamage())
         )
      );
      register(VeinMinerAbility.class, Map.of("blocks", ability -> AbilityLabelFormatters.decimal((float)ability.getUnmodifiedBlockLimit())));
      register(
         VeinMinerDurabilityAbility.class,
         Map.of(
            "blocks",
            ability -> AbilityLabelFormatters.decimal((float)ability.getUnmodifiedBlockLimit()),
            "unbreaking",
            ability -> AbilityLabelFormatters.integer(ability.getAdditionalUnbreakingLevel())
         )
      );
      register(
         VeinMinerFortuneAbility.class,
         Map.of(
            "blocks",
            ability -> AbilityLabelFormatters.decimal((float)ability.getUnmodifiedBlockLimit()),
            "fortune",
            ability -> AbilityLabelFormatters.integer(ability.getAdditionalFortuneLevel())
         )
      );
      register(VeinMinerVoidAbility.class, Map.of("blocks", ability -> AbilityLabelFormatters.decimal((float)ability.getUnmodifiedBlockLimit())));
      register(
         TotemAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getTotemDurationTicks()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getTotemEffectRadius()),
            "heal",
            ability -> AbilityLabelFormatters.decimal(ability.getTotemHealthPerSecond())
         )
      );
      register(
         TotemMobDamageAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getTotemDurationTicks()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getTotemEffectRadius()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getTotemPercentDamagePerInterval()),
            "delay",
            ability -> AbilityLabelFormatters.ticks(ability.getTotemDamageIntervalTicks())
         )
      );
      register(
         TotemManaRegenAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getTotemDurationTicks()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getTotemEffectRadius()),
            "manaRegen",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getTotemManaRegenPercent())
         )
      );
      register(
         TotemPlayerDamageAbility.class,
         Map.of(
            "duration",
            ability -> AbilityLabelFormatters.ticks(ability.getTotemDurationTicks()),
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getTotemEffectRadius()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getTotemPlayerDamagePercent())
         )
      );
      register(
         JavelinAbility.class,
         Map.of(
            "damage",
            config -> AbilityLabelFormatters.percentTwoDecimalPlaces(config.getPercentAttackDamageDealt()),
            "knockback",
            config -> AbilityLabelFormatters.decimal(config.getKnockback()),
            "throwPower",
            config -> AbilityLabelFormatters.decimal(config.getThrowPower())
         )
      );
      register(
         JavelinPiercingAbility.class,
         Map.of(
            "damage",
            config -> AbilityLabelFormatters.percentTwoDecimalPlaces(config.getPercentAttackDamageDealt()),
            "piercing",
            config -> AbilityLabelFormatters.integer(config.getPiercing()),
            "throwPower",
            config -> AbilityLabelFormatters.decimal(config.getThrowPower())
         )
      );
      register(
         JavelinScatterAbility.class,
         Map.of(
            "damage",
            config -> AbilityLabelFormatters.percentTwoDecimalPlaces(config.getPercentAttackDamageDealt()),
            "numberOfJavelins",
            config -> AbilityLabelFormatters.integer(config.getNumberOfJavelins()),
            "numberOfBounces",
            config -> AbilityLabelFormatters.integer(config.getNumberOfBounces()),
            "throwPower",
            config -> AbilityLabelFormatters.decimal(config.getThrowPower())
         )
      );
      register(
         JavelinSightAbility.class,
         Map.of(
            "damage",
            config -> AbilityLabelFormatters.percentTwoDecimalPlaces(config.getPercentAttackDamageDealt()),
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getRadius()),
            "duration",
            config -> AbilityLabelFormatters.integer(config.getEffectDuration()),
            "throwPower",
            config -> AbilityLabelFormatters.decimal(config.getThrowPower())
         )
      );
      register(
         SmiteAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
            "damageInterval",
            ability -> AbilityLabelFormatters.ticks(ability.getIntervalTicks()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getPlayerDamagePercent()),
            "additionalManaPerBolt",
            ability -> AbilityLabelFormatters.decimal(ability.getAdditionalManaPerBolt())
         )
      );
      register(
         SmiteArchonAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
            "damageInterval",
            ability -> AbilityLabelFormatters.ticks(ability.getIntervalTicks()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getPlayerDamagePercent()),
            "additionalManaPerBolt",
            ability -> AbilityLabelFormatters.decimal(ability.getAdditionalManaPerBolt())
         )
      );
      register(
         SmiteThunderstormAbility.class,
         Map.of(
            "radius",
            ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
            "damageInterval",
            ability -> AbilityLabelFormatters.ticks(ability.getIntervalTicks()),
            "damage",
            ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getPlayerDamagePercent()),
            "additionalManaPerBolt",
            ability -> AbilityLabelFormatters.decimal(ability.getAdditionalManaPerBolt())
         )
      );
      register(
         ShellAbility.class,
         Map.of(
            "stunChance",
            ability -> AbilityLabelFormatters.percentRounded(ability.getStunChance()),
            "stunDuration",
            ability -> AbilityLabelFormatters.seconds(ability.getStunDurationSeconds()),
            "stunAmplifier",
            ability -> AbilityLabelFormatters.integer(ability.getStunAmplifier()),
            "additionalManaPerHit",
            ability -> AbilityLabelFormatters.decimal(ability.getAdditionalManaPerHit())
         )
      );
      register(
         ShellPorcupineAbility.class,
         Map.of(
            "durabilityWearReduction",
            ability -> AbilityLabelFormatters.percentRounded(ability.getAdditionalDurabilityWearReduction()),
            "damage",
            ability -> AbilityLabelFormatters.percentRounded(ability.getAdditionalThornsDamagePercent()),
            "additionalManaPerHit",
            ability -> AbilityLabelFormatters.decimal(ability.getAdditionalManaPerHit())
         )
      );
      register(
         ShellQuillAbility.class,
         Map.of(
            "durabilityWearReduction",
            ability -> AbilityLabelFormatters.percentRounded(ability.getAdditionalDurabilityWearReduction()),
            "damage",
            ability -> AbilityLabelFormatters.percentRounded(ability.getAdditionalThornsDamagePercent()),
            "additionalManaPerHit",
            ability -> AbilityLabelFormatters.decimal(ability.getAdditionalManaPerHit()),
            "quillCount",
            ability -> AbilityLabelFormatters.integer(ability.getQuillCount())
         )
      );
   }

   public static <C extends Skill> void register(Class<C> ability) {
      register(ability, Collections.emptyMap());
   }

   public static <C extends Skill> void register(Class<C> ability, Map<String, IAbilityLabelBinding<C>> bindings) {
      if (AbilityLabelBindingRegistry.containsKey(ability)) {
         throw new IllegalStateException("Ability label bindings already registered for ability class " + ability);
      } else {
         if (LearnableSkill.class.isAssignableFrom(ability)) {
            AbilityLabelBindingRegistry.register(ability, "level", config -> String.valueOf(((LearnableSkill)config).getUnlockLevel()));
         }

         if (Ability.class.isAssignableFrom(ability)) {
            AbilityLabelBindingRegistry.register(ability, "cooldown", config -> ((Ability)config).getCooldownTicks() / 20.0F + "s");
         }

         if (IInstantManaAbility.class.isAssignableFrom(ability)) {
            AbilityLabelBindingRegistry.register(ability, "manaCost", config -> String.valueOf(Math.round(((IInstantManaAbility)config).getManaCost())));
         } else if (IPerSecondManaAbility.class.isAssignableFrom(ability)) {
            AbilityLabelBindingRegistry.register(ability, "manaCost", config -> "%.1f".formatted(((IPerSecondManaAbility)config).getManaCostPerSecond()));
         }

         for (Entry<String, IAbilityLabelBinding<C>> entry : bindings.entrySet()) {
            AbilityLabelBindingRegistry.register(ability, entry.getKey(), entry.getValue());
         }
      }
   }
}
