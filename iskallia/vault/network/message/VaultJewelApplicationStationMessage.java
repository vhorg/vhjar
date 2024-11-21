package iskallia.vault.network.message;

import iskallia.vault.container.VaultJewelApplicationStationContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultJewelApplicationStationMessage {
   public static final VaultJewelApplicationStationMessage INSTANCE = new VaultJewelApplicationStationMessage();

   private VaultJewelApplicationStationMessage() {
   }

   public static void encode(VaultJewelApplicationStationMessage message, FriendlyByteBuf buffer) {
   }

   public static VaultJewelApplicationStationMessage decode(FriendlyByteBuf buffer) {
      return new VaultJewelApplicationStationMessage();
   }

   public static void handle(VaultJewelApplicationStationMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer requester = context.getSender();
         if (requester != null && requester.containerMenu instanceof VaultJewelApplicationStationContainer container) {
            container.getTileEntity().applyJewels();
         }
      });
      context.setPacketHandled(true);
   }
}
