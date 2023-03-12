package iskallia.vault.skill.ability.effect;

import iskallia.vault.easteregg.GrasshopperNinja;
import iskallia.vault.gear.attribute.ability.special.DashVelocityModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.DashConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;
import iskallia.vault.util.MathUtilities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class DashAbility<C extends DashConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Dash";
   }

   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      Vec3 lookVector = player.getLookAngle();
      float magnitude = (10 + config.getExtraDistance()) * 0.15F;
      double extraPitch = 10.0;

      for (ConfiguredModification<FloatValueConfig, DashVelocityModification> mod : SpecialAbilityModification.getModifications(
         player, DashVelocityModification.class
      )) {
         magnitude = mod.modification().adjustVelocity(mod.config(), magnitude);
      }

      Vec3 dashVector = new Vec3(lookVector.x(), lookVector.y(), lookVector.z());
      float initialYaw = (float)MathUtilities.extractYaw(dashVector);
      dashVector = MathUtilities.rotateYaw(dashVector, initialYaw);
      double dashPitch = Math.toDegrees(MathUtilities.extractPitch(dashVector));
      if (dashPitch + extraPitch > 90.0) {
         dashVector = new Vec3(0.0, 1.0, 0.0);
         dashPitch = 90.0;
      } else {
         dashVector = MathUtilities.rotateRoll(dashVector, (float)Math.toRadians(-extraPitch));
         dashVector = MathUtilities.rotateYaw(dashVector, -initialYaw);
         dashVector = dashVector.normalize();
      }

      double coeff = 1.6 - MathUtilities.map(Math.abs(dashPitch), 0.0, 90.0, 0.6, 1.0);
      dashVector = dashVector.scale(magnitude * coeff);
      player.push(dashVector.x(), dashVector.y(), dashVector.z());
      player.hurtMarked = true;
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(C config, ServerPlayer player) {
      ((ServerLevel)player.level).sendParticles(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0);
   }

   protected void doSound(C config, ServerPlayer player) {
      if (GrasshopperNinja.isGrasshopperShape(player)) {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.GRASSHOPPER_BRRR, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(ModSounds.GRASSHOPPER_BRRR, SoundSource.PLAYERS, 0.2F, 1.0F);
         GrasshopperNinja.achieve(player);
      } else {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.DASH_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(ModSounds.DASH_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
      }
   }
}
