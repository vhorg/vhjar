package iskallia.vault.network.message;

import iskallia.vault.container.SpiritExtractorContainer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundRefreshSpiritExtractorMessage(int spiritRecoveryCount, float multiplier, float heroDiscount) {
   public static void encode(ClientboundRefreshSpiritExtractorMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.spiritRecoveryCount);
      buffer.writeFloat(message.multiplier);
      buffer.writeFloat(message.heroDiscount);
   }

   public static ClientboundRefreshSpiritExtractorMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundRefreshSpiritExtractorMessage(buffer.readInt(), buffer.readFloat(), buffer.readFloat());
   }

   public static void handle(ClientboundRefreshSpiritExtractorMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         if (player != null) {
            if (player.containerMenu instanceof SpiritExtractorContainer container) {
               container.setSpiritRecoveryCountAndMultiplier(message.spiritRecoveryCount, message.multiplier, message.heroDiscount);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
