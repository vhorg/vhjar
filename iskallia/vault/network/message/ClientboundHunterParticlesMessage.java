package iskallia.vault.network.message;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.skill.base.Skill;
import java.awt.Color;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundHunterParticlesMessage(double x, double y, double z, double r, double g, double b) {
   public static void encode(ClientboundHunterParticlesMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeDouble(pkt.x);
      buffer.writeDouble(pkt.y);
      buffer.writeDouble(pkt.z);
      buffer.writeDouble(pkt.r);
      buffer.writeDouble(pkt.g);
      buffer.writeDouble(pkt.b);
   }

   public static ClientboundHunterParticlesMessage decode(FriendlyByteBuf buffer) {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      double r = buffer.readDouble();
      double g = buffer.readDouble();
      double b = buffer.readDouble();
      return new ClientboundHunterParticlesMessage(x, y, z, r, g, b);
   }

   public static void handle(ClientboundHunterParticlesMessage pkt, Supplier<Context> contextSupplier) {
      createParticles(pkt.x, pkt.y, pkt.z, pkt.r, pkt.g, pkt.b);
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void createParticles(double x, double y, double z, double r, double g, double b) {
      Color color = new Color((float)r, (float)g, (float)b);
      IVaultOptions options = (IVaultOptions)Minecraft.getInstance().options;
      if (options.isHunterCustomColorsEnabled()) {
         for (HunterAbility ability : ClientAbilityData.getTree().getAll(HunterAbility.class, Skill::isUnlocked)) {
            color = getColor(ability.getParent().getId());
         }
      }

      Minecraft.getInstance()
         .level
         .addAlwaysVisibleParticle(
            (ParticleOptions)ModParticles.DEPTH_FIREWORK.get(), true, x, y, z, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F
         );
   }

   private static Color getColor(String hunterSpec) {
      IVaultOptions options = (IVaultOptions)Minecraft.getInstance().options;
      switch (hunterSpec) {
         case "Hunter_Base":
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
