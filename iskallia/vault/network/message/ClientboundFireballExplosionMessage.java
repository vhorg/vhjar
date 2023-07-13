package iskallia.vault.network.message;

import iskallia.vault.client.particles.FireballParticle;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.MathUtilities;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundFireballExplosionMessage(double x, double y, double z, double r, int count) {
   private static final Random RANDOM = new Random();

   public static void encode(ClientboundFireballExplosionMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeDouble(pkt.r);
      buffer.writeInt(pkt.count);
   }

   public static ClientboundFireballExplosionMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      double r = buffer.readDouble();
      int count = buffer.readInt();
      return new ClientboundFireballExplosionMessage(x, y, z, r, count);
   }

   public static void handle(ClientboundFireballExplosionMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.r, pkt.count);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, double r, int count) {
      ParticleEngine pm = Minecraft.getInstance().particleEngine;

      for (int i = 0; i < count; i++) {
         Vec3 position = MathUtilities.getRandomPointOnSphere(x, y, z, r - 0.5, RANDOM);
         Particle particle = pm.createParticle((ParticleOptions)ModParticles.FIREBALL_CLOUD.get(), position.x, position.y, position.z, 0.0, 0.0, 0.0);
         if (particle instanceof FireballParticle) {
            FireballParticle fireballParticle = (FireballParticle)particle;
            float colorOffset = RANDOM.nextFloat() * 0.2F;
            if (RANDOM.nextBoolean()) {
               fireballParticle.setStartColor(0.48359376F + colorOffset, 0.06953125F + colorOffset, 0.0703125F);
            } else {
               fireballParticle.setStartColor(0.75F + colorOffset, 0.35F + colorOffset, 0.0F);
            }

            float col = Mth.nextFloat(RANDOM, 0.01F, 0.15F);
            fireballParticle.setEndColor(col, col, col);
         }
      }
   }
}
