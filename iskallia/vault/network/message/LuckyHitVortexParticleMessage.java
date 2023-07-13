package iskallia.vault.network.message;

import iskallia.vault.init.ModParticles;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class LuckyHitVortexParticleMessage {
   private final Vec3 centerPos;
   private final float radius;

   public LuckyHitVortexParticleMessage(Vec3 centerPos, float radius) {
      this.centerPos = centerPos;
      this.radius = radius;
   }

   public static void encode(LuckyHitVortexParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.centerPos.x);
      buffer.writeDouble(message.centerPos.y);
      buffer.writeDouble(message.centerPos.z);
      buffer.writeFloat(message.radius);
   }

   public static LuckyHitVortexParticleMessage decode(FriendlyByteBuf buffer) {
      return new LuckyHitVortexParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readFloat());
   }

   public static void handle(LuckyHitVortexParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.centerPos, message.radius);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos, float radius) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();
         ParticleEngine pm = Minecraft.getInstance().particleEngine;

         for (int i = 0; i < 65; i++) {
            float rotation = random.nextFloat() * 360.0F;
            Vec3 offset = new Vec3(radius * Math.cos(rotation), 0.25, radius * Math.sin(rotation));
            float f = -0.5F + random.nextFloat() + (float)offset.x();
            float f1 = -0.5F + random.nextFloat() + (float)offset.y();
            float f2 = -0.5F + random.nextFloat() + (float)offset.z();
            Particle particle = pm.createParticle((ParticleOptions)ModParticles.LUCKY_HIT_VORTEX.get(), pos.x, pos.y, pos.z, f, f1, f2);
            if (particle != null) {
               particle.setColor(0.5F, 0.0F, 1.0F);
            }
         }
      }
   }
}
