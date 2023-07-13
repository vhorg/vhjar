package iskallia.vault.network.message;

import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundNightVisionGogglesParticlesMessage(double x, double y, double z, double r, double g, double b, String type) {
   public static void encode(ClientboundNightVisionGogglesParticlesMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeDouble(pkt.r);
      buffer.writeDouble(pkt.g);
      buffer.writeDouble(pkt.b);
      buffer.writeUtf(pkt.type);
   }

   public static ClientboundNightVisionGogglesParticlesMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      double r = buffer.readDouble();
      double g = buffer.readDouble();
      double b = buffer.readDouble();
      String type = buffer.readUtf();
      return new ClientboundNightVisionGogglesParticlesMessage(x, y, z, r, g, b, type);
   }

   public static void handle(ClientboundNightVisionGogglesParticlesMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.r, pkt.g, pkt.b, pkt.type);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, double r, double g, double b, String type) {
      ParticleEngine pm = Minecraft.getInstance().particleEngine;
      Color color = getColor(type);

      for (int i = 0; i < 8; i++) {
         Vec3 v = MiscUtils.getRandomOffset(new BlockPos(x, y, z), new Random());
         Particle particle = pm.createParticle(
            (ParticleOptions)ModParticles.DEPTH_NIGHT_VISION.get(), v.x, v.y, v.z, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F
         );
         if (particle != null) {
            particle.setLifetime((int)r);
         }
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
