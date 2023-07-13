package iskallia.vault.network.message;

import iskallia.vault.client.render.IVaultOptions;
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

public record ClientboundHunterParticlesFromJavelinMessage(double x, double y, double z, double r, double g, double b, String type) {
   public static void encode(ClientboundHunterParticlesFromJavelinMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeDouble(pkt.r);
      buffer.writeDouble(pkt.g);
      buffer.writeDouble(pkt.b);
      buffer.writeUtf(pkt.type);
   }

   public static ClientboundHunterParticlesFromJavelinMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      double r = buffer.readDouble();
      double g = buffer.readDouble();
      double b = buffer.readDouble();
      String type = buffer.readUtf();
      return new ClientboundHunterParticlesFromJavelinMessage(x, y, z, r, g, b, type);
   }

   public static void handle(ClientboundHunterParticlesFromJavelinMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.r, pkt.g, pkt.b, pkt.type);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, double r, double g, double b, String type) {
      Color color = getColor(type);
      ParticleEngine pm = Minecraft.getInstance().particleEngine;
      Particle particle = pm.createParticle(
         (ParticleOptions)ModParticles.DEPTH_FIREWORK.get(), x, y, z, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F
      );
      if (particle != null) {
         particle.setLifetime((int)r);
      }
   }

   private static Color getColor(String hunterSpec) {
      IVaultOptions options = (IVaultOptions)Minecraft.getInstance().options;
      switch (hunterSpec) {
         case "Hunter":
         case "Hunter_Wooden":
            return options.getChestHunterSpec().getColor();
         case "Hunter_Blocks":
            return options.getBlockHunterSpec().getColor();
         case "Hunter_Gilded":
            return options.getGildedHunterSpec().getColor();
         case "Hunter_Living":
            return options.getLivingHunterSpec().getColor();
         case "Hunter_Ornate":
            return options.getOrnateHunterSpec().getColor();
         case "Hunter_Coins":
            return options.getCoinsHunterSpec().getColor();
         default:
            return Color.WHITE;
      }
   }
}
