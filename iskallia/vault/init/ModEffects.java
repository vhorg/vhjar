package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.effect.BasicEffect;
import iskallia.vault.effect.PoisonOverrideEffect;
import iskallia.vault.effect.TimerAccelerationEffect;
import iskallia.vault.skill.ability.effect.ExecuteAbility;
import iskallia.vault.skill.ability.effect.GhostWalkAbility;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;
import iskallia.vault.skill.ability.effect.RampageAbility;
import iskallia.vault.skill.ability.effect.StonefallAbility;
import iskallia.vault.skill.ability.effect.TankAbility;
import iskallia.vault.skill.ability.effect.TauntAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.ability.effect.sub.RampageChainAbility;
import iskallia.vault.skill.ability.effect.sub.RampageLeechAbility;
import iskallia.vault.skill.ability.effect.sub.StonefallColdAbility;
import iskallia.vault.skill.ability.effect.sub.TankProjectileAbility;
import iskallia.vault.skill.ability.effect.sub.TankReflectAbility;
import iskallia.vault.skill.ability.effect.sub.TauntRepelAbility;
import iskallia.vault.util.PlayerRageHelper;
import java.awt.Color;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModEffects {
   public static final MobEffect GHOST_WALK = new GhostWalkAbility.GhostWalkEffect(MobEffectCategory.BENEFICIAL, Color.GRAY.getRGB(), VaultMod.id("ghost_walk"));
   public static final MobEffect TANK_RESISTANCE = new TankAbility.TankResistanceEffect(Color.WHITE.getRGB(), VaultMod.id("tank_resistance"));
   public static final MobEffect EXECUTE = new ExecuteAbility.ExecuteEffect(MobEffectCategory.BENEFICIAL, Color.YELLOW.getRGB(), VaultMod.id("execute"));
   public static final MobEffect TAUNT = new TauntAbility.TauntEffect(Color.RED.getRGB(), VaultMod.id("taunt"));
   public static final MobEffect TARGET_OVERRIDE = new TauntAbility.TargetOverrideEffect(Color.RED.getRGB(), VaultMod.id("target_override"));
   public static final MobEffect TAUNT_REPEL_PLAYER = new TauntRepelAbility.TauntRepelPlayerEffect(Color.DARK_GRAY.getRGB(), VaultMod.id("taunt_repel_player"));
   public static final MobEffect TAUNT_REPEL_MOB = new TauntRepelAbility.TauntRepelMobEffect(Color.DARK_GRAY.getRGB(), VaultMod.id("taunt_repel_mob"));
   public static final MobEffect STONEFALL = new StonefallAbility.StonefallEffect(Color.GRAY.getRGB(), VaultMod.id("stonefall"));
   public static final MobEffect STONEFALL_COLD = new StonefallColdAbility.StonefallColdEffect(Color.CYAN.getRGB(), VaultMod.id("stonefall_cold"));
   public static final ToggleAbilityEffect RAMPAGE = new RampageAbility.RampageEffect(Color.RED.getRGB(), VaultMod.id("rampage"));
   public static final ToggleAbilityEffect RAMPAGE_LEECH = new RampageLeechAbility.RampageLeechEffect(Color.RED.getRGB(), VaultMod.id("rampage_leech"));
   public static final ToggleAbilityEffect RAMPAGE_CHAIN = new RampageChainAbility.RampageChainEffect(Color.RED.getRGB(), VaultMod.id("rampage_chain"));
   public static final ToggleAbilityEffect TANK = new TankAbility.TankEffect(Color.WHITE.getRGB(), VaultMod.id("tank"));
   public static final ToggleAbilityEffect TANK_PROJECTILE = new TankProjectileAbility.TankProjectileEffect(
      Color.WHITE.getRGB(), VaultMod.id("tank_projectile")
   );
   public static final ToggleAbilityEffect TANK_REFLECT = new TankReflectAbility.TankReflectEffect(Color.WHITE.getRGB(), VaultMod.id("tank_reflect"));
   public static final ToggleAbilityEffect MANA_SHIELD = new ManaShieldAbility.ManaShieldEffect(Color.CYAN.getRGB(), VaultMod.id("mana_shield"));
   public static final PlayerRageHelper.RageEffect RAGE = new PlayerRageHelper.RageEffect(Color.RED.getRGB(), VaultMod.id("rage"));
   public static final MobEffect RESISTANCE = new BasicEffect(MobEffectCategory.BENEFICIAL, Color.YELLOW.getRGB(), VaultMod.id("resistance"));
   public static final MobEffect TIMER_ACCELERATION = new TimerAccelerationEffect(MobEffectCategory.HARMFUL, -16448251, VaultMod.id("time_acceleration"));
   public static final MobEffect POISON_OVERRIDE = new PoisonOverrideEffect();

   public static void register(Register<MobEffect> event) {
      event.getRegistry()
         .registerAll(
            new MobEffect[]{
               GHOST_WALK,
               RAMPAGE,
               RAMPAGE_LEECH,
               RAMPAGE_CHAIN,
               TANK,
               TANK_RESISTANCE,
               TANK_PROJECTILE,
               TANK_REFLECT,
               EXECUTE,
               MANA_SHIELD,
               RAGE,
               TIMER_ACCELERATION,
               POISON_OVERRIDE,
               RESISTANCE,
               TAUNT,
               TAUNT_REPEL_MOB,
               TAUNT_REPEL_PLAYER,
               TARGET_OVERRIDE,
               STONEFALL,
               STONEFALL_COLD
            }
         );
      MobEffects.POISON = POISON_OVERRIDE;
   }
}
