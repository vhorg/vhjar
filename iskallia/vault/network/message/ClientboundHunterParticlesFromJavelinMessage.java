package iskallia.vault.network.message;

import iskallia.vault.init.ModParticles;
import java.awt.Color;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundHunterParticlesFromJavelinMessage(double x, double y, double z, double sightDuration, String type) {
   public static void encode(ClientboundHunterParticlesFromJavelinMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeDouble(pkt.sightDuration);
      buffer.writeUtf(pkt.type);
   }

   public static ClientboundHunterParticlesFromJavelinMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      double sightDuration = buffer.readDouble();
      String type = buffer.readUtf();
      return new ClientboundHunterParticlesFromJavelinMessage(x, y, z, sightDuration, type);
   }

   public static void handle(ClientboundHunterParticlesFromJavelinMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.sightDuration, pkt.type);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, double sightDuration, String type) {
      Color color = ClientboundHunterParticlesMessage.getColor(type);
      ParticleEngine pm = Minecraft.getInstance().particleEngine;
      Particle particle = pm.createParticle(
         (ParticleOptions)ModParticles.DEPTH_FIREWORK.get(), x, y, z, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F
      );
      if (particle != null) {
         particle.setLifetime((int)sightDuration);
      }
   }
}
