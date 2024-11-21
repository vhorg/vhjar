package iskallia.vault.network.message;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class TrappedMobChestParticlesMessage {
   private final BlockPos chestPos;

   public TrappedMobChestParticlesMessage(BlockPos chestPos) {
      this.chestPos = chestPos;
   }

   public static void encode(TrappedMobChestParticlesMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.chestPos);
   }

   public static TrappedMobChestParticlesMessage decode(FriendlyByteBuf buffer) {
      return new TrappedMobChestParticlesMessage(buffer.readBlockPos());
   }

   public static void handle(TrappedMobChestParticlesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.chestPos);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         ParticleEngine mgr = Minecraft.getInstance().particleEngine;
         Random random = level.getRandom();

         for (int i = 0; i < 40; i++) {
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            Particle p = mgr.createParticle(
               ParticleTypes.POOF,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.25,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
            if (random.nextBoolean()) {
               int col = 6842472;
               float f = ((col & 0xFF0000) >> 16) / 255.0F;
               float f1 = ((col & 0xFF00) >> 8) / 255.0F;
               float f2 = (col & 0xFF) / 255.0F;
               if (p != null) {
                  p.setColor(f, f1, f2);
               }
            } else {
               int col = 10724259;
               float f = ((col & 0xFF0000) >> 16) / 255.0F;
               float f1 = ((col & 0xFF00) >> 8) / 255.0F;
               float f2 = (col & 0xFF) / 255.0F;
               if (p != null) {
                  p.setColor(f, f1, f2);
               }
            }
         }

         for (int ix = 0; ix < 30; ix++) {
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            Particle p = mgr.createParticle(
               ParticleTypes.SMOKE,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 20.0
            );
            if (random.nextBoolean()) {
               int col = 4802889;
               float f = ((col & 0xFF0000) >> 16) / 255.0F;
               float f1 = ((col & 0xFF00) >> 8) / 255.0F;
               float f2 = (col & 0xFF) / 255.0F;
               if (p != null) {
                  p.setColor(f, f1, f2);
               }
            } else {
               int col = 8553090;
               float f = ((col & 0xFF0000) >> 16) / 255.0F;
               float f1 = ((col & 0xFF00) >> 8) / 255.0F;
               float f2 = (col & 0xFF) / 255.0F;
               if (p != null) {
                  p.setColor(f, f1, f2);
               }
            }
         }
      }
   }
}
