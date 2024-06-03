package iskallia.vault.network.message;

import iskallia.vault.container.CrystalWorkbenchContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class CrystalWorkbenchMessage {
   public static void encode(CrystalWorkbenchMessage message, FriendlyByteBuf buffer) {
   }

   public static CrystalWorkbenchMessage decode(FriendlyByteBuf buffer) {
      return new CrystalWorkbenchMessage();
   }

   public static void handle(CrystalWorkbenchMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null && player.containerMenu instanceof CrystalWorkbenchContainer anvil) {
            anvil.getEntity().onCraft(player);
         }
      });
      context.setPacketHandled(true);
   }
}
