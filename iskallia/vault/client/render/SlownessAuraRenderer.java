package iskallia.vault.client.render;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.particles.EntityLockedParticle;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.EmpowerSlownessAuraAbility;
import iskallia.vault.skill.base.Skill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class SlownessAuraRenderer {
   @SubscribeEvent
   public static void onTick(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof Player player && player.level.isClientSide && player.hasEffect(ModEffects.EMPOWER_SLOWNESS_AURA)) {
         if (player != Minecraft.getInstance().player) {
            return;
         }

         float range = 1.0F;

         for (EmpowerSlownessAuraAbility ability : ClientAbilityData.getTree().getAll(EmpowerSlownessAuraAbility.class, Skill::isUnlocked)) {
            range = ability.getRadius(player);
         }

         int color = 5926017;
         float red = (color >>> 16 & 0xFF) / 255.0F;
         float green = (color >>> 8 & 0xFF) / 255.0F;
         float blue = (color & 0xFF) / 255.0F;
         ParticleEngine pm = Minecraft.getInstance().particleEngine;

         for (int i = 0; i < 3; i++) {
            float rotation = (float)Math.toRadians(player.tickCount % 90 * 4.0F + 120 * i);
            new Vec3(range * Math.cos(rotation), 0.0, range * Math.sin(rotation));
            Particle particle = pm.createParticle(
               (ParticleOptions)ModParticles.ENTITY_LOCKED.get(), 0.0, 0.0, 0.0, range, (float)Math.toRadians(player.tickCount % 90 * 4.0F + 120 * i), 0.0
            );
            if (particle instanceof EntityLockedParticle entityLockedParticle) {
               particle.setColor(Mth.clamp(red, 0.0F, 1.0F), Mth.clamp(green, 0.0F, 1.0F), Mth.clamp(blue, 0.0F, 1.0F));
               entityLockedParticle.setEntity(player);
               entityLockedParticle.setPosO();
            }
         }
      }
   }
}
