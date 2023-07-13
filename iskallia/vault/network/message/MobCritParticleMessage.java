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

public class MobCritParticleMessage {
   private final Vec3 pos;

   public MobCritParticleMessage(Vec3 pos) {
      this.pos = pos;
   }

   public static void encode(MobCritParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.pos.x);
      buffer.writeDouble(message.pos.y);
      buffer.writeDouble(message.pos.z);
   }

   public static MobCritParticleMessage decode(FriendlyByteBuf buffer) {
      return new MobCritParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
   }

   public static void handle(MobCritParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.pos);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();
         float radius = 1.0F;
         int num = 7;
         ParticleEngine mgr = Minecraft.getInstance().particleEngine;

         for (int i = 0; i < num; i++) {
            float rotation = (float)i / num * 360.0F + (random.nextFloat() * 5.0F - 2.5F);
            Vec3 offset = new Vec3(radius / 3.0F * Math.cos(rotation), 0.0, radius / 3.0F * Math.sin(rotation));
            Particle p = mgr.createParticle(
               (ParticleOptions)ModParticles.LUCKY_HIT.get(),
               pos.x() + offset.x,
               pos.y() + random.nextDouble() * 0.15F,
               pos.z() + offset.z,
               offset.x,
               random.nextDouble() * 0.5,
               offset.z
            );
            if (p != null) {
               p.setColor(0.75F, 0.1F, 0.1F);
            }
         }
      }
   }
}
