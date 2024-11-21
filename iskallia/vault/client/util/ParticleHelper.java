package iskallia.vault.client.util;

import iskallia.vault.network.message.EffectMessage;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleHelper {
   public static void spawnParticle(EffectMessage pkt) {
      EffectMessage.Type type = pkt.getEffectType();
      switch (type) {
         case COLORED_FIREWORK:
            spawnColoredFirework(pkt.getPos(), pkt.getData().readInt());
            break;
         case BLOCK_BREAK_EFFECT:
            spawnBlockBreakEffect(pkt.getPos(), pkt.getData().readInt());
      }
   }

   private static void spawnBlockBreakEffect(Vec3 pos, int id) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         BlockState state = Block.stateById(id);
         level.addDestroyBlockEffect(new BlockPos(pos), state);
      }
   }

   private static void spawnColoredFirework(Vec3 pos, int color) {
      ParticleEngine mgr = Minecraft.getInstance().particleEngine;
      SimpleAnimatedParticle fwParticle = (SimpleAnimatedParticle)mgr.createParticle(ParticleTypes.FIREWORK, pos.x(), pos.y(), pos.z(), 0.0, 0.0, 0.0);
      if (fwParticle != null) {
         Color c = new Color(color);
         fwParticle.setColor(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F);
      }
   }
}
