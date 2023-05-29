package iskallia.vault.network.message;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.init.ModParticles;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundSightParticlesFromJavelinMessage(double x, double y, double z, double r, double g, double b) {
   public static void encode(ClientboundSightParticlesFromJavelinMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeDouble(pkt.r);
      buffer.writeDouble(pkt.g);
      buffer.writeDouble(pkt.b);
   }

   public static ClientboundSightParticlesFromJavelinMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      double r = buffer.readDouble();
      double g = buffer.readDouble();
      double b = buffer.readDouble();
      return new ClientboundSightParticlesFromJavelinMessage(x, y, z, r, g, b);
   }

   public static void handle(ClientboundSightParticlesFromJavelinMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.r, pkt.g, pkt.b);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, double r, double g, double b) {
      ParticleEngine pm = Minecraft.getInstance().particleEngine;

      for (int i = 0; i < 600; i++) {
         pm.createParticle(
            new SphericalParticleOptions(
               (ParticleType<SphericalParticleOptions>)ModParticles.SIGHT_JAVELIN_RANGE.get(), (float)r, new Vector3f(0.0F, 1.0F, 1.0F)
            ),
            x,
            y,
            z,
            x,
            y,
            z
         );
      }
   }
}
