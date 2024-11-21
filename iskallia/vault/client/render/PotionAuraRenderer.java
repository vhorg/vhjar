package iskallia.vault.client.render;

import iskallia.vault.init.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PotionAuraRenderer {
   public static final PotionAuraRenderer INSTANCE = new PotionAuraRenderer();

   private PotionAuraRenderer() {
   }

   public void render(Entity entity, MobEffect mobEffect, int range) {
      this.render(entity, mobEffect, range, false);
   }

   public void render(Entity entity, MobEffect mobEffect, int range, boolean invertEffectRange) {
      if (mobEffect != null) {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         if (!minecraft.isPaused() && player != null && isWithinRenderableDistance(entity, range, player, invertEffectRange)) {
            int color = mobEffect.getColor();
            float red = (color >>> 16 & 0xFF) / 255.0F;
            float green = (color >>> 8 & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            if (invertEffectRange) {
               for (int i = 0; i < 10; i++) {
                  int randomRange = player.level.random.nextInt(range, 64);
                  createParticle(entity, randomRange, red, green, blue);
               }
            } else {
               createParticle(entity, range, red, green, blue);
            }
         }
      }
   }

   private static boolean isWithinRenderableDistance(Entity entity, int range, LocalPlayer player, boolean invertEffectRange) {
      return invertEffectRange ? player.distanceTo(entity) < 64.0F : player.distanceTo(entity) < Math.max(range * 2, range + 5);
   }

   private static void createParticle(Entity entity, int range, float red, float green, float blue) {
      Vec3 offset = new Vec3(range, 0.0, 0.0).yRot(entity.level.getRandom().nextFloat((float) (Math.PI * 2)));
      Vec3 pos = entity.position().add(offset);
      ParticleEngine pm = Minecraft.getInstance().particleEngine;
      Particle particle = pm.createParticle((ParticleOptions)ModParticles.NOVA_CLOUD.get(), pos.x, pos.y + 0.2F, pos.z, 0.0, 0.0, 0.0);
      if (particle != null) {
         particle.setColor(red, green, blue);
      }

      for (int i = 0; i < 3; i++) {
         offset = new Vec3(range, 0.1F, 0.0).yRot((float)Math.toRadians(entity.tickCount % 90 * 4.0F + 120 * i));
         pos = entity.position().add(offset);
         particle = pm.createParticle((ParticleOptions)ModParticles.CHAINING.get(), pos.x, pos.y + 0.2F, pos.z, 0.0, 0.0, 0.0);
         if (particle != null) {
            particle.setColor(Mth.clamp(red * 1.25F, 0.0F, 1.0F), Mth.clamp(green * 1.25F, 0.0F, 1.0F), Mth.clamp(blue * 1.25F, 0.0F, 1.0F));
         }
      }
   }
}
