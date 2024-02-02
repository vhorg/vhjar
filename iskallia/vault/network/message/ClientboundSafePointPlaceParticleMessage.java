package iskallia.vault.network.message;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.ArtifactBossImmunityParticleOptions;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundSafePointPlaceParticleMessage {
   private final BlockPos safePointPos;

   public ClientboundSafePointPlaceParticleMessage(BlockPos safePointPos) {
      this.safePointPos = safePointPos;
   }

   public static void encode(ClientboundSafePointPlaceParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.safePointPos);
   }

   public static ClientboundSafePointPlaceParticleMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundSafePointPlaceParticleMessage(buffer.readBlockPos());
   }

   public static void handle(ClientboundSafePointPlaceParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            spawnParticles(message.safePointPos);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnParticles(BlockPos safePointPos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Random random = new Random();
         Vec3 center = new Vec3(safePointPos.getX() + 0.5F, safePointPos.getY() + 1, safePointPos.getZ() + 0.5F);
         int col = 16769280;
         int col2 = 16774829;
         float r = (col >> 16 & 0xFF) / 255.0F;
         float g = (col >> 8 & 0xFF) / 255.0F;
         float b = (col & 0xFF) / 255.0F;
         float r2 = (col2 >> 16 & 0xFF) / 255.0F;
         float g2 = (col2 >> 8 & 0xFF) / 255.0F;
         float b2 = (col2 & 0xFF) / 255.0F;

         for (int i = 0; i < 100; i++) {
            float rotation = random.nextFloat() * 360.0F;
            float radius = 4.0F * random.nextFloat();
            Vec3 offset = new Vec3(radius * Math.cos(rotation), 0.0, radius * Math.sin(rotation));
            Vec3 spawnPos = center.add(offset);
            level.addParticle(
               new ArtifactBossImmunityParticleOptions(
                  (ParticleType<ArtifactBossImmunityParticleOptions>)ModParticles.ARTIFACT_BOSS_IMMUNITY.get(),
                  20,
                  new Vector3f(r, g, b),
                  new Vector3f(r2, g2, b2)
               ),
               true,
               spawnPos.x(),
               spawnPos.y(),
               spawnPos.z(),
               0.0,
               random.nextFloat(0.5F, 2.0F),
               0.0
            );
         }
      }
   }
}
