package iskallia.vault.network.message;

import iskallia.vault.client.particles.ShockedParticle;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class LuckyHitDamageParticleMessage {
   private final Vec3 playerPos;
   private final Vec3 offset;
   private final int entityID;

   public LuckyHitDamageParticleMessage(Vec3 playerPos, Vec3 offset, int entityID) {
      this.playerPos = playerPos;
      this.offset = offset;
      this.entityID = entityID;
   }

   public static void encode(LuckyHitDamageParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeDouble(message.playerPos.x);
      buffer.writeDouble(message.playerPos.y);
      buffer.writeDouble(message.playerPos.z);
      buffer.writeDouble(message.offset.x);
      buffer.writeDouble(message.offset.y);
      buffer.writeDouble(message.offset.z);
      buffer.writeInt(message.entityID);
   }

   public static LuckyHitDamageParticleMessage decode(FriendlyByteBuf buffer) {
      return new LuckyHitDamageParticleMessage(
         new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
         new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
         buffer.readInt()
      );
   }

   public static void handle(LuckyHitDamageParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.playerPos, message.offset, message.entityID);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(Vec3 pos, Vec3 offset, int entityID) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();
         ParticleEngine pe = Minecraft.getInstance().particleEngine;
         if (level.getEntity(entityID) instanceof LivingEntity livingEntity) {
            for (int i = 0; i < 20; i++) {
               if (pe.createParticle(
                  (ParticleOptions)ModParticles.LUCKY_HIT_DAMAGE.get(),
                  pos.x(),
                  pos.y(),
                  pos.z(),
                  Mth.randomBetween(random, (float)(-offset.x), (float)offset.x),
                  Mth.randomBetween(random, (float)(-offset.y), (float)offset.y),
                  Mth.randomBetween(random, (float)(-offset.z), (float)offset.z)
               ) instanceof ShockedParticle shockedParticle) {
                  shockedParticle.setLivingEntity(livingEntity);
                  float colorOffset = random.nextFloat() * 0.15F;
                  shockedParticle.setColor(0.65F + colorOffset, 0.15F + colorOffset, 0.1F + colorOffset);
               }
            }
         }
      }
   }
}
