package iskallia.vault.network.message;

import iskallia.vault.init.ModParticles;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class LuckyHitSweepingParticleMessage {
   private final Vec3 playerPos;
   private final float radius;

   public LuckyHitSweepingParticleMessage(Vec3 playerPos, float radius) {
      this.playerPos = playerPos;
      this.radius = radius;
   }

   public static void encode(LuckyHitSweepingParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
      buffer.writeFloat(message.radius);
   }

   public static LuckyHitSweepingParticleMessage decode(FriendlyByteBuf buffer) {
      return new LuckyHitSweepingParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readFloat());
   }

   public static void handle(LuckyHitSweepingParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.playerPos, message.radius);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos, float radius) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();

         for (int i = 0; i < 75; i++) {
            float rotation = random.nextFloat() * 360.0F;
            Vec3 offset = new Vec3(radius / 3.0F * Math.cos(rotation), 0.0, radius / 3.0F * Math.sin(rotation));
            level.addParticle(
               (ParticleOptions)ModParticles.LUCKY_HIT_SWEEPING.get(),
               true,
               pos.x() + offset.x,
               pos.y() + random.nextDouble() * 0.15F,
               pos.z() + offset.z,
               offset.x / 4.0,
               random.nextDouble() * 0.035,
               offset.z / 4.0
            );
         }

         for (int i = 0; i < 35; i++) {
            float rotation = random.nextFloat() * 360.0F;
            Vec3 offset = new Vec3(radius / 6.0F * Math.cos(rotation), 0.0, radius / 6.0F * Math.sin(rotation));
            level.addParticle(
               (ParticleOptions)ModParticles.NOVA_CLOUD.get(),
               true,
               pos.x() + offset.x,
               pos.y() + random.nextDouble() * 0.15F,
               pos.z() + offset.z,
               offset.x / 5.0,
               random.nextDouble() * 0.035,
               offset.z / 5.0
            );
         }
      }
   }
}