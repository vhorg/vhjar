package iskallia.vault.init;

import iskallia.vault.skill.ability.component.AbilityLabelBindingRegistry;
import iskallia.vault.skill.ability.component.AbilityLabelFormatters;
import iskallia.vault.skill.ability.component.IAbilityLabelBinding;
import iskallia.vault.skill.ability.config.DashConfig;
import iskallia.vault.skill.ability.config.ExecuteConfig;
import iskallia.vault.skill.ability.config.FarmerConfig;
import iskallia.vault.skill.ability.config.GhostWalkConfig;
import iskallia.vault.skill.ability.config.HealConfig;
import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.skill.ability.config.ManaShieldConfig;
import iskallia.vault.skill.ability.config.MegaJumpConfig;
import iskallia.vault.skill.ability.config.NovaConfig;
import iskallia.vault.skill.ability.config.RampageConfig;
import iskallia.vault.skill.ability.config.StonefallConfig;
import iskallia.vault.skill.ability.config.SummonEternalConfig;
import iskallia.vault.skill.ability.config.TankConfig;
import iskallia.vault.skill.ability.config.TauntConfig;
import iskallia.vault.skill.ability.config.VeinMinerConfig;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.config.spi.IInstantManaConfig;
import iskallia.vault.skill.ability.config.spi.IPerSecondManaConfig;
import iskallia.vault.skill.ability.config.sub.DashDamageConfig;
import iskallia.vault.skill.ability.config.sub.FarmerAnimalConfig;
import iskallia.vault.skill.ability.config.sub.FarmerCactusConfig;
import iskallia.vault.skill.ability.config.sub.FarmerMelonConfig;
import iskallia.vault.skill.ability.config.sub.HealEffectConfig;
import iskallia.vault.skill.ability.config.sub.HealGroupConfig;
import iskallia.vault.skill.ability.config.sub.HunterObjectiveConfig;
import iskallia.vault.skill.ability.config.sub.MegaJumpBreakDownConfig;
import iskallia.vault.skill.ability.config.sub.MegaJumpBreakUpConfig;
import iskallia.vault.skill.ability.config.sub.NovaDotConfig;
import iskallia.vault.skill.ability.config.sub.NovaSpeedConfig;
import iskallia.vault.skill.ability.config.sub.RampageChainConfig;
import iskallia.vault.skill.ability.config.sub.RampageLeechConfig;
import iskallia.vault.skill.ability.config.sub.TankProjectileConfig;
import iskallia.vault.skill.ability.config.sub.TankReflectConfig;
import iskallia.vault.skill.ability.config.sub.TauntRepelConfig;
import iskallia.vault.skill.ability.config.sub.VeinMinerDurabilityConfig;
import iskallia.vault.skill.ability.config.sub.VeinMinerFortuneConfig;
import iskallia.vault.skill.ability.config.sub.VeinMinerVoidConfig;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

public class ModAbilityLabelBindings {
   public static void register() {
      AbilityLabelBindingRegistry.clear();
      register(DashConfig.class, Map.of("distance", config -> AbilityLabelFormatters.integer(config.getExtraDistance())));
      register(
         DashDamageConfig.class,
         Map.of(
            "distance",
            config -> AbilityLabelFormatters.integer(config.getExtraDistance()),
            "damage",
            config -> AbilityLabelFormatters.percentRounded(config.getAttackDamagePercentPerDash())
         )
      );
      register(
         ExecuteConfig.class,
         Map.of(
            "damage",
            config -> AbilityLabelFormatters.percentRounded(config.getDamageHealthPercentage()),
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getEffectDurationTicks())
         )
      );
      register(
         FarmerConfig.class,
         Map.of(
            "delay",
            config -> AbilityLabelFormatters.ticks(config.getTickDelay()),
            "rangeHorizontal",
            config -> AbilityLabelFormatters.integer(config.getHorizontalRange()),
            "rangeVertical",
            config -> AbilityLabelFormatters.integer(config.getVerticalRange())
         )
      );
      register(
         FarmerCactusConfig.class,
         Map.of(
            "delay",
            config -> AbilityLabelFormatters.ticks(config.getTickDelay()),
            "rangeHorizontal",
            config -> AbilityLabelFormatters.integer(config.getHorizontalRange()),
            "rangeVertical",
            config -> AbilityLabelFormatters.integer(config.getVerticalRange())
         )
      );
      register(
         FarmerMelonConfig.class,
         Map.of(
            "delay",
            config -> AbilityLabelFormatters.ticks(config.getTickDelay()),
            "rangeHorizontal",
            config -> AbilityLabelFormatters.integer(config.getHorizontalRange()),
            "rangeVertical",
            config -> AbilityLabelFormatters.integer(config.getVerticalRange())
         )
      );
      register(
         FarmerAnimalConfig.class,
         Map.of(
            "delay",
            config -> AbilityLabelFormatters.ticks(config.getTickDelay()),
            "rangeHorizontal",
            config -> AbilityLabelFormatters.integer(config.getHorizontalRange()),
            "rangeVertical",
            config -> AbilityLabelFormatters.integer(config.getVerticalRange()),
            "chance",
            config -> AbilityLabelFormatters.percentRounded(config.getAdultChance())
         )
      );
      register(GhostWalkConfig.class, Map.of("duration", config -> AbilityLabelFormatters.ticks(config.getDurationTicks())));
      register(HealConfig.class, Map.of("heal", config -> AbilityLabelFormatters.decimal(config.getFlatLifeHealed())));
      register(HealEffectConfig.class);
      register(
         HealGroupConfig.class,
         Map.of(
            "heal",
            config -> AbilityLabelFormatters.decimal(config.getFlatLifeHealed()),
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getRadius())
         )
      );
      register(
         HunterConfig.class,
         Map.of(
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getSearchRadius()),
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getDurationTicks())
         )
      );
      register(
         HunterObjectiveConfig.class,
         Map.of(
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getSearchRadius()),
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getDurationTicks())
         )
      );
      register(ManaShieldConfig.class, Map.of("absorb", config -> AbilityLabelFormatters.percentRounded(config.getPercentageDamageAbsorbed())));
      register(MegaJumpConfig.class, Map.of("rangeVertical", config -> AbilityLabelFormatters.integer(config.getHeight())));
      register(MegaJumpBreakUpConfig.class, Map.of("rangeVertical", config -> AbilityLabelFormatters.integer(config.getHeight())));
      register(MegaJumpBreakDownConfig.class, Map.of("rangeVertical", config -> AbilityLabelFormatters.integer(config.getHeight())));
      register(
         NovaConfig.class,
         Map.of(
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getRadius()),
            "damage",
            config -> AbilityLabelFormatters.percentRounded(config.getPercentAttackDamageDealt()),
            "knockback",
            config -> AbilityLabelFormatters.percentRounded(config.getKnockbackStrengthMultiplier())
         )
      );
      register(
         NovaDotConfig.class,
         Map.of(
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getRadius()),
            "damage",
            config -> AbilityLabelFormatters.percentRounded(config.getPercentAttackDamageDealt()),
            "duration",
            config -> AbilityLabelFormatters.seconds(config.getDurationSeconds())
         )
      );
      register(
         NovaSpeedConfig.class,
         Map.of(
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getRadius()),
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getDurationTicks()),
            "slowness",
            config -> AbilityLabelFormatters.integer(config.getAmplifier())
         )
      );
      register(RampageConfig.class, Map.of("damage", config -> AbilityLabelFormatters.percentRounded(config.getDamageIncrease())));
      register(RampageChainConfig.class, Map.of("chains", config -> AbilityLabelFormatters.integer(config.getAdditionalChainCount())));
      register(RampageLeechConfig.class, Map.of("leech", config -> AbilityLabelFormatters.percentTwoDecimalPlaces(config.getLeechPercent())));
      register(
         SummonEternalConfig.class,
         Map.of(
            "eternals",
            config -> AbilityLabelFormatters.integer(config.getNumberOfEternals()),
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getDespawnTime()),
            "chance",
            config -> AbilityLabelFormatters.percentRounded(config.getAncientChance())
         )
      );
      register(
         TankConfig.class,
         Map.of(
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getDurationTicksPerHit()),
            "resistance",
            config -> AbilityLabelFormatters.percentRounded(config.getResistancePercentAddedPerHit())
         )
      );
      register(
         TankProjectileConfig.class,
         Map.of(
            "resistance",
            config -> AbilityLabelFormatters.percentRounded(config.getKnockbackResistance()),
            "projectileDamageTaken",
            config -> AbilityLabelFormatters.percentRounded(config.getPercentageReducedProjectileDamage())
         )
      );
      register(
         TankReflectConfig.class,
         Map.of(
            "chance",
            config -> AbilityLabelFormatters.percentRounded(config.getAdditionalThornsChance()),
            "damage",
            config -> AbilityLabelFormatters.percentRounded(config.getThornsDamageMultiplier())
         )
      );
      register(
         TauntConfig.class,
         Map.of(
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getRadius()),
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getDurationTicks())
         )
      );
      register(
         TauntRepelConfig.class,
         Map.of(
            "radius",
            config -> AbilityLabelFormatters.decimal(config.getRadius()),
            "duration",
            config -> AbilityLabelFormatters.ticks(config.getDurationTicks()),
            "distance",
            config -> AbilityLabelFormatters.decimal(config.getRepelForce())
         )
      );
      register(VeinMinerConfig.class, Map.of("blocks", config -> AbilityLabelFormatters.decimal((float)config.getBlockLimit())));
      register(
         VeinMinerDurabilityConfig.class,
         Map.of(
            "blocks",
            config -> AbilityLabelFormatters.decimal((float)config.getBlockLimit()),
            "unbreaking",
            config -> AbilityLabelFormatters.integer(config.getAdditionalUnbreakingLevel())
         )
      );
      register(
         VeinMinerFortuneConfig.class,
         Map.of(
            "blocks",
            config -> AbilityLabelFormatters.decimal((float)config.getBlockLimit()),
            "fortune",
            config -> AbilityLabelFormatters.integer(config.getAdditionalFortuneLevel())
         )
      );
      register(VeinMinerVoidConfig.class, Map.of("blocks", config -> AbilityLabelFormatters.decimal((float)config.getBlockLimit())));
      register(StonefallConfig.class, Map.of("duration", config -> AbilityLabelFormatters.ticks(config.getDurationTicks())));
   }

   public static <C extends AbstractAbilityConfig> void register(Class<C> configClass) {
      register(configClass, Collections.emptyMap());
   }

   public static <C extends AbstractAbilityConfig> void register(Class<C> configClass, Map<String, IAbilityLabelBinding<C>> bindings) {
      if (AbilityLabelBindingRegistry.containsKey(configClass)) {
         throw new IllegalStateException("Ability label bindings already registered for config class " + configClass);
      } else {
         AbilityLabelBindingRegistry.register(configClass, "level", config -> String.valueOf(config.getLevelRequirement()));
         AbilityLabelBindingRegistry.register(configClass, "cooldown", config -> config.getCooldownTicks() / 20.0F + "s");
         if (IInstantManaConfig.class.isAssignableFrom(configClass)) {
            AbilityLabelBindingRegistry.register(configClass, "manaCost", config -> String.valueOf(Math.round(((IInstantManaConfig)config).getManaCost())));
         } else if (IPerSecondManaConfig.class.isAssignableFrom(configClass)) {
            AbilityLabelBindingRegistry.register(
               configClass, "manaCost", config -> String.valueOf(Math.round(((IPerSecondManaConfig)config).getManaCostPerSecond()))
            );
         }

         for (Entry<String, IAbilityLabelBinding<C>> entry : bindings.entrySet()) {
            AbilityLabelBindingRegistry.register(configClass, entry.getKey(), entry.getValue());
         }
      }
   }
}
