package iskallia.vault.network.message;

import iskallia.vault.client.particles.FloatingAltarItemParticle;
import iskallia.vault.init.ModParticles;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class FloatingAltarItemParticleMessage {
   private final Vec3 playerPos;
   private final int entityID;

   public FloatingAltarItemParticleMessage(Vec3 playerPos, int entityID) {
      this.playerPos = playerPos;
      this.entityID = entityID;
   }

   public static void encode(FloatingAltarItemParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
      buffer.writeInt(message.entityID);
   }

   public static FloatingAltarItemParticleMessage decode(FriendlyByteBuf buffer) {
      return new FloatingAltarItemParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readInt());
   }

   public static void handle(FloatingAltarItemParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.playerPos, message.entityID);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos, int entityID) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         ParticleEngine pe = Minecraft.getInstance().particleEngine;
         Entity entity = level.getEntity(entityID);
         if (entity != null) {
            for (int i = 0; i < 20; i++) {
               if (pe.createParticle((ParticleOptions)ModParticles.FLOATING_ALTAR_ITEM.get(), pos.x(), pos.y(), pos.z(), 1.0, 0.0, 0.0) instanceof FloatingAltarItemParticle floatingAltarItemParticle
                  )
                {
                  floatingAltarItemParticle.setEntity(entity);
               }
            }
         }
      }
   }
}
