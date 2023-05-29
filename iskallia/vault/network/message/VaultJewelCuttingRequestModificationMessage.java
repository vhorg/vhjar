package iskallia.vault.network.message;

import iskallia.vault.container.VaultJewelCuttingStationContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultJewelCuttingRequestModificationMessage {
   public static void encode(VaultJewelCuttingRequestModificationMessage message, FriendlyByteBuf buffer) {
   }

   public static VaultJewelCuttingRequestModificationMessage decode(FriendlyByteBuf buffer) {
      return new VaultJewelCuttingRequestModificationMessage();
   }

   public static void handle(VaultJewelCuttingRequestModificationMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer requester = context.getSender();
         if (requester != null && requester.containerMenu instanceof VaultJewelCuttingStationContainer container) {
            container.getTileEntity().cutJewel(container, requester);
         }
      });
      context.setPacketHandled(true);
   }
}
