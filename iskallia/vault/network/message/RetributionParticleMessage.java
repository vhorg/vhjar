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

public class RetributionParticleMessage {
   private final Vec3 startPos;
   private final float distance;
   private final float rotation;

   public RetributionParticleMessage(Vec3 startPos, float distance, float rotation) {
      this.startPos = startPos;
      this.distance = distance;
      this.rotation = rotation;
   }

   public static void encode(RetributionParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.startPos.x);
      buffer.writeDouble(message.startPos.y);
      buffer.writeDouble(message.startPos.z);
      buffer.writeFloat(message.distance);
      buffer.writeFloat(message.rotation);
   }

   public static RetributionParticleMessage decode(FriendlyByteBuf buffer) {
      return new RetributionParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readFloat(), buffer.readFloat());
   }

   public static void handle(RetributionParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.startPos, message.distance, message.rotation);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 startPos, float distance, float rotation) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = level.getRandom();

         for (int i = 0; i < 140; i++) {
            float rotationOffset = random.nextFloat() * 180.0F;
            float modifiedDistance = 1.0F / (Math.abs(rotationOffset - 90.0F) / 90.0F) * distance + 1.0F;
            double posX = startPos.x + random.nextFloat() * 0.5F;
            double posY = startPos.y + random.nextFloat() * 0.5F;
            double posZ = startPos.z + random.nextFloat() * 0.5F;
            double perTickSpeed = modifiedDistance / 40.0F;
            perTickSpeed = perTickSpeed * 0.5 + perTickSpeed * 0.5 * random.nextDouble();
            double xSpeed = Math.cos(Math.toRadians(rotation + rotationOffset)) * perTickSpeed;
            double zSpeed = Math.sin(Math.toRadians(rotation + rotationOffset)) * perTickSpeed;
            level.addParticle((ParticleOptions)ModParticles.NOVA_CLOUD.get(), true, posX, posY, posZ, xSpeed, 0.0, zSpeed);
         }
      }
   }
}
