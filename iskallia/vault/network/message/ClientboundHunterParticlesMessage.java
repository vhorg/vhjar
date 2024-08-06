package iskallia.vault.network.message;

import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.init.ModParticles;
import java.awt.Color;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundHunterParticlesMessage(double x, double y, double z, @Nullable String type, int color) {
   public static void encode(ClientboundHunterParticlesMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeBoolean(pkt.type != null);
      if (pkt.type != null) {
         buffer.writeUtf(pkt.type);
      }

      buffer.writeInt(pkt.color);
   }

   public static ClientboundHunterParticlesMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      boolean hasType = buffer.readBoolean();
      String type = null;
      if (hasType) {
         type = buffer.readUtf();
      }

      int color = buffer.readInt();
      return new ClientboundHunterParticlesMessage(x, y, z, type, color);
   }

   public static void handle(ClientboundHunterParticlesMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.type, pkt.color);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, @Nullable String type, int intColor) {
      Color color = type != null ? getColor(type) : new Color(intColor);
      Minecraft.getInstance()
         .level
         .addAlwaysVisibleParticle(
            (ParticleOptions)ModParticles.DEPTH_FIREWORK.get(), true, x, y, z, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F
         );
   }

   public static Color getColor(String hunterSpec) {
      IVaultOptions options = (IVaultOptions)Minecraft.getInstance().options;
      switch (hunterSpec) {
         case "blocks":
            return options.getBlockHunterSpec().getColor();
         case "gilded":
            return options.getGildedHunterSpec().getColor();
         case "living":
            return options.getLivingHunterSpec().getColor();
         case "ornate":
            return options.getOrnateHunterSpec().getColor();
         case "coins":
            return options.getCoinsHunterSpec().getColor();
         case "wooden":
            return options.getChestHunterSpec().getColor();
         default:
            return Color.WHITE;
      }
   }
}
