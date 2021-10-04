package iskallia.vault.client.util;

import iskallia.vault.network.message.EffectMessage;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleHelper {
   public static void spawnParticle(EffectMessage pkt) {
      EffectMessage.Type type = pkt.getEffectType();
      switch (type) {
         case COLORED_FIREWORK:
            spawnColoredFirework(pkt.getPos(), pkt.getData().readInt());
      }
   }

   private static void spawnColoredFirework(Vector3d pos, int color) {
      ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
      SimpleAnimatedParticle fwParticle = (SimpleAnimatedParticle)mgr.func_199280_a(
         ParticleTypes.field_197629_v, pos.func_82615_a(), pos.func_82617_b(), pos.func_82616_c(), 0.0, 0.0, 0.0
      );
      Color c = new Color(color);
      fwParticle.func_70538_b(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F);
      fwParticle.field_187149_H = 0.0F;
   }
}
