package iskallia.vault.network.message;

import iskallia.vault.init.ModParticles;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class StunnedParticleMessage {
   private final Vec3 playerPos;
   private final float radius;

   public StunnedParticleMessage(Vec3 playerPos, float radius) {
      this.playerPos = playerPos;
      this.radius = radius;
   }

   public static void encode(StunnedParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
      buffer.writeFloat(message.radius);
   }

   public static StunnedParticleMessage decode(FriendlyByteBuf buffer) {
      return new StunnedParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readFloat());
   }

   public static void handle(StunnedParticleMessage message, Supplier<Context> contextSupplier) {
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
         level.addParticle((ParticleOptions)ModParticles.STUNNED.get(), true, pos.x(), pos.y() + 0.25, pos.z(), radius, 0.0, 0.0);
      }
   }
}
