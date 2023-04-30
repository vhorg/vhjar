package iskallia.vault.network.message;

import iskallia.vault.client.particles.PylonConsumeParticle;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class PylonConsumeParticleMessage {
   private final Vec3 playerPos;
   private final int entityID;
   private final int color;

   public PylonConsumeParticleMessage(Vec3 playerPos, int entityID, int color) {
      this.playerPos = playerPos;
      this.entityID = entityID;
      this.color = color;
   }

   public static void encode(PylonConsumeParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
      buffer.writeInt(message.entityID);
      buffer.writeInt(message.color);
   }

   public static PylonConsumeParticleMessage decode(FriendlyByteBuf buffer) {
      return new PylonConsumeParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readInt(), buffer.readInt());
   }

   public static void handle(PylonConsumeParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.playerPos, message.entityID, message.color);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos, int entityID, int color) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();
         ParticleEngine pe = Minecraft.getInstance().particleEngine;
         if (level.getEntity(entityID) instanceof LivingEntity livingEntity) {
            for (int i = 0; i < 80; i++) {
               float rotation = random.nextFloat() * 360.0F;
               float radius = 1.0F + random.nextFloat();
               Vec3 offset = new Vec3(radius / 5.0F * Math.cos(rotation), 0.0, radius / 5.0F * Math.sin(rotation));
               offset.scale(random.nextDouble() + 0.5);
               if (pe.createParticle(
                  (ParticleOptions)ModParticles.PYLON_CONSUME.get(),
                  pos.x() + 0.5,
                  pos.y() + random.nextDouble() * 0.6 + 0.15,
                  pos.z() + 0.5,
                  offset.x / 2.0,
                  random.nextDouble() * 0.25 + 0.1,
                  offset.z / 2.0
               ) instanceof PylonConsumeParticle pylonParticle) {
                  pylonParticle.setLivingEntity(livingEntity);
                  pylonParticle.setColor((color >>> 16 & 0xFF) / 255.0F, (color >>> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
               }
            }

            for (int ix = 0; ix < 12; ix++) {
               float rotation = random.nextFloat() * 360.0F;
               Vec3 offset = new Vec3(0.4F * Math.cos(rotation), 0.0, 0.4F * Math.sin(rotation));
               offset.scale(random.nextDouble() + 0.5);
               Particle particle = pe.createParticle(
                  (ParticleOptions)ModParticles.NOVA_CLOUD.get(),
                  pos.x() + 0.5 + offset.x,
                  pos.y() + random.nextDouble() * 0.15F,
                  pos.z() + 0.5 + offset.z,
                  offset.x / 8.0,
                  random.nextDouble() * 0.125,
                  offset.z / 8.0
               );
               if (particle != null) {
                  particle.setLifetime(60 + random.nextInt(20));
               }

               offset = new Vec3(0.25 * Math.cos(rotation), 0.0, 0.25 * Math.sin(rotation));
               offset.scale(random.nextDouble() + 0.5);
               particle = pe.createParticle(
                  (ParticleOptions)ModParticles.NOVA_CLOUD.get(),
                  pos.x() + 0.5 + offset.x,
                  pos.y() + random.nextDouble() * 0.15F,
                  pos.z() + 0.5 + offset.z,
                  offset.x / 8.0,
                  random.nextDouble() * 0.125,
                  offset.z / 8.0
               );
               if (particle != null) {
                  particle.setLifetime(60 + random.nextInt(20));
               }
            }
         }
      }
   }
}
