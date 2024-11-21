package iskallia.vault.network.message;

import iskallia.vault.client.particles.AbsorbingParticle;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbsorbingParticleMessage {
   private final Vec3 startPos;
   private final int targetEntity;
   private final Vec3 targetPos;
   private final int color;

   private AbsorbingParticleMessage(Vec3 startPos, int targetEntity, Vec3 targetPos, int color) {
      this.startPos = startPos;
      this.targetEntity = targetEntity;
      this.targetPos = targetPos;
      this.color = color;
   }

   public AbsorbingParticleMessage(Vec3 startPos, Entity targetEntity, int color) {
      this.startPos = startPos;
      this.targetEntity = targetEntity.getId();
      this.targetPos = targetEntity.position();
      this.color = color;
   }

   public AbsorbingParticleMessage(Vec3 startPos, Vec3 targetPos, int color) {
      this.startPos = startPos;
      this.targetEntity = -1;
      this.targetPos = targetPos;
      this.color = color;
   }

   public static void encode(AbsorbingParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.startPos.x);
      buffer.writeDouble(message.startPos.y);
      buffer.writeDouble(message.startPos.z);
      buffer.writeVarInt(message.targetEntity);
      buffer.writeDouble(message.targetPos.x);
      buffer.writeDouble(message.targetPos.y);
      buffer.writeDouble(message.targetPos.z);
      buffer.writeInt(message.color);
   }

   public static AbsorbingParticleMessage decode(FriendlyByteBuf buffer) {
      return new AbsorbingParticleMessage(
         new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
         buffer.readVarInt(),
         new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
         buffer.readInt()
      );
   }

   public static void handle(AbsorbingParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            message.spawnParticles();
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public void spawnParticles() {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();
         ParticleEngine pe = Minecraft.getInstance().particleEngine;

         for (int i = 0; i < 80; i++) {
            float rotation = random.nextFloat() * 360.0F;
            float radius = 1.0F + random.nextFloat();
            Vec3 offset = new Vec3(radius / 5.0F * Math.cos(rotation), 0.0, radius / 5.0F * Math.sin(rotation));
            offset.scale(random.nextDouble() + 0.5);
            if (pe.createParticle(
               (ParticleOptions)ModParticles.ABSORBING.get(),
               this.startPos.x() + 0.5,
               this.startPos.y() + random.nextDouble() * 0.6 + 0.15,
               this.startPos.z() + 0.5,
               offset.x / 2.0,
               random.nextDouble() * 0.25 + 0.1,
               offset.z / 2.0
            ) instanceof AbsorbingParticle pylonParticle) {
               pylonParticle.setTarget(
                  () -> level.getEntity(this.targetEntity) instanceof LivingEntity entity
                     ? entity.position().add(entity.getBbWidth() / 2.0, entity.getBbHeight() / 2.0, entity.getBbWidth() / 2.0F)
                     : this.targetPos
               );
               pylonParticle.setColor((this.color >>> 16 & 0xFF) / 255.0F, (this.color >>> 8 & 0xFF) / 255.0F, (this.color & 0xFF) / 255.0F);
            }
         }

         for (int ix = 0; ix < 12; ix++) {
            float rotation = random.nextFloat() * 360.0F;
            Vec3 offset = new Vec3(0.4F * Math.cos(rotation), 0.0, 0.4F * Math.sin(rotation));
            offset.scale(random.nextDouble() + 0.5);
            Particle particle = pe.createParticle(
               (ParticleOptions)ModParticles.NOVA_CLOUD.get(),
               this.startPos.x() + 0.5 + offset.x,
               this.startPos.y() + random.nextDouble() * 0.15,
               this.startPos.z() + 0.5 + offset.z,
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
               this.startPos.x() + 0.5 + offset.x,
               this.startPos.y() + random.nextDouble() * 0.15,
               this.startPos.z() + 0.5 + offset.z,
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
