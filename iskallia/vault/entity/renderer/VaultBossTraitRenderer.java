package iskallia.vault.entity.renderer;

import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.goal.PotionAuraGoal;
import iskallia.vault.init.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VaultBossTraitRenderer {
   private VaultBossTraitRenderer() {
   }

   public static void renderTraits(VaultBossEntity boss) {
      boss.getTraits().forEach(trait -> {
         if (trait instanceof PotionAuraGoal potionAuraGoal) {
            renderPotionAura(boss, potionAuraGoal);
         }
      });
   }

   private static void renderPotionAura(VaultBossEntity boss, PotionAuraGoal trait) {
      int range = trait.getRange();
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (!minecraft.isPaused() && player != null && !(player.distanceTo(boss) > Math.max(range * 2, range + 5))) {
         int color = trait.getMobEffect().getColor();
         float red = (color >>> 16 & 0xFF) / 255.0F;
         float green = (color >>> 8 & 0xFF) / 255.0F;
         float blue = (color & 0xFF) / 255.0F;
         Vec3 offset = new Vec3(range, 0.0, 0.0).yRot(boss.level.getRandom().nextFloat((float) (Math.PI * 2)));
         Vec3 pos = boss.position().add(offset);
         ParticleEngine pm = Minecraft.getInstance().particleEngine;
         Particle particle = pm.createParticle((ParticleOptions)ModParticles.NOVA_CLOUD.get(), pos.x, pos.y + 0.2F, pos.z, 0.0, 0.0, 0.0);
         if (particle != null) {
            particle.setColor(red, green, blue);
         }

         for (int i = 0; i < 3; i++) {
            offset = new Vec3(range, 0.1F, 0.0).yRot((float)Math.toRadians(boss.tickCount % 90 * 4.0F + 120 * i));
            pos = boss.position().add(offset);
            particle = pm.createParticle((ParticleOptions)ModParticles.CHAINING.get(), pos.x, pos.y + 0.2F, pos.z, 0.0, 0.0, 0.0);
            if (particle != null) {
               particle.setColor(Mth.clamp(red * 1.25F, 0.0F, 1.0F), Mth.clamp(green * 1.25F, 0.0F, 1.0F), Mth.clamp(blue * 1.25F, 0.0F, 1.0F));
            }
         }
      }
   }
}
