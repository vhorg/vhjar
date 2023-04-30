package iskallia.vault.network.message;

import iskallia.vault.client.particles.LuckyHitDrainParticle;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class LuckyHitLeechParticleMessage {
   private final Vec3 playerPos;
   private final int entityID;
   private final int color;
   private final int count;

   public LuckyHitLeechParticleMessage(Vec3 playerPos, int entityID, int color, int count) {
      this.playerPos = playerPos;
      this.entityID = entityID;
      this.color = color;
      this.count = count;
   }

   public static void encode(LuckyHitLeechParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
      buffer.writeInt(message.entityID);
      buffer.writeInt(message.color);
      buffer.writeInt(message.count);
   }

   public static LuckyHitLeechParticleMessage decode(FriendlyByteBuf buffer) {
      return new LuckyHitLeechParticleMessage(
         new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readInt(), buffer.readInt(), buffer.readInt()
      );
   }

   public static void handle(LuckyHitLeechParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.playerPos, message.entityID, message.color, message.count);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos, int entityID, int color, int count) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();
         ParticleEngine pe = Minecraft.getInstance().particleEngine;
         if (level.getEntity(entityID) instanceof LivingEntity livingEntity) {
            for (int i = 0; i < count; i++) {
               float rotation = random.nextFloat() * 360.0F;
               float radius = random.nextFloat();
               Vec3 offset = new Vec3(radius / 5.0F * Math.cos(rotation), 0.0, radius / 5.0F * Math.sin(rotation));
               offset.scale(random.nextDouble() + 0.5);
               if (pe.createParticle(
                  (ParticleOptions)ModParticles.LUCKY_HIT_LEECH.get(),
                  pos.x(),
                  pos.y() + random.nextDouble() * 0.6 + 0.15,
                  pos.z(),
                  offset.x / 2.0,
                  random.nextDouble() * 0.25 + 0.1,
                  offset.z / 2.0
               ) instanceof LuckyHitDrainParticle pylonParticle) {
                  pylonParticle.setLivingEntity(livingEntity);
                  pylonParticle.setLifetime(20 + (int)(new Random().nextFloat() * 20.0F));
                  pylonParticle.setColor((color >>> 16 & 0xFF) / 255.0F, (color >>> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
               }
            }
         }
      }
   }
}
