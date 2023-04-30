package iskallia.vault.network.message;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class TauntParticleMessage {
   private final Vec3 playerPos;
   private final float radius;

   public TauntParticleMessage(Vec3 playerPos, float radius) {
      this.playerPos = playerPos;
      this.radius = radius;
   }

   public static void encode(TauntParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
      buffer.writeFloat(message.radius);
   }

   public static TauntParticleMessage decode(FriendlyByteBuf buffer) {
      return new TauntParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readFloat());
   }

   public static void handle(TauntParticleMessage message, Supplier<Context> contextSupplier) {
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
         float usedRadius = level.random.nextFloat() * radius;
         float rotation = level.random.nextFloat() * 360.0F;
         Vec3 offset = new Vec3(Math.cos(rotation) * usedRadius, 0.0, Math.sin(rotation) * usedRadius);
         level.addParticle(ParticleTypes.ANGRY_VILLAGER, true, pos.x() + offset.x, pos.y() + offset.y, pos.z() + offset.z, 0.0, 0.0, 0.0);
      }
   }
}
