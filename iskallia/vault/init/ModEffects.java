package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.effect.BasicEffect;
import iskallia.vault.effect.ExecuteEffect;
import iskallia.vault.effect.GhostWalkEffect;
import iskallia.vault.effect.ImmunityEffect;
import iskallia.vault.effect.RampageEffect;
import iskallia.vault.effect.TankEffect;
import iskallia.vault.effect.TimerAccelerationEffect;
import java.awt.Color;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModEffects {
   public static final Effect GHOST_WALK = new GhostWalkEffect(EffectType.BENEFICIAL, Color.GRAY.getRGB(), Vault.id("ghost_walk"));
   public static final Effect RAMPAGE = new RampageEffect(EffectType.BENEFICIAL, Color.RED.getRGB(), Vault.id("rampage"));
   public static final Effect TANK = new TankEffect(EffectType.BENEFICIAL, Color.WHITE.getRGB(), Vault.id("tank"));
   public static final Effect EXECUTE = new ExecuteEffect(EffectType.BENEFICIAL, Color.YELLOW.getRGB(), Vault.id("execute"));
   public static final Effect PARRY = new BasicEffect(EffectType.BENEFICIAL, Color.WHITE.getRGB(), Vault.id("parry"));
   public static final Effect RESISTANCE = new BasicEffect(EffectType.BENEFICIAL, Color.YELLOW.getRGB(), Vault.id("resistance"));
   public static final Effect VAULT_POWERUP = new BasicEffect(EffectType.BENEFICIAL, Color.BLACK.getRGB(), Vault.id("vault_powerup"));
   public static final Effect IMMUNITY = new ImmunityEffect(EffectType.BENEFICIAL, Color.WHITE.getRGB(), Vault.id("immunity"));
   public static final Effect TIMER_ACCELERATION = new TimerAccelerationEffect(EffectType.HARMFUL, -16448251, Vault.id("time_acceleration"));

   public static void register(Register<Effect> event) {
      event.getRegistry().registerAll(new Effect[]{GHOST_WALK, RAMPAGE, TANK, EXECUTE, TIMER_ACCELERATION, PARRY, RESISTANCE, VAULT_POWERUP, IMMUNITY});
   }
}