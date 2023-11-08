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

public class WendarrSparkParticleMessage {
   private final Vec3 playerPos;

   public WendarrSparkParticleMessage(Vec3 playerPos) {
      this.playerPos = playerPos;
   }

   public static void encode(WendarrSparkParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
   }

   public static WendarrSparkParticleMessage decode(FriendlyByteBuf buffer) {
      return new WendarrSparkParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
   }

   public static void handle(WendarrSparkParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.playerPos);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();

         for (int i = 0; i < 70; i++) {
            float rotation = random.nextFloat() * 360.0F;
            float radiusOffset = random.nextFloat() + 0.5F;
            Vec3 offset = new Vec3(radiusOffset / 5.0F * Math.cos(rotation), 0.0, radiusOffset / 5.0F * Math.sin(rotation));
            level.addParticle(
               (ParticleOptions)ModParticles.WENDARR_SPARK_EXPLODE.get(),
               true,
               pos.x() + offset.x,
               pos.y() + random.nextDouble() * 0.15F,
               pos.z() + offset.z,
               offset.x / 8.0,
               random.nextDouble() * 0.025,
               offset.z / 8.0
            );
         }
      }
   }
}
