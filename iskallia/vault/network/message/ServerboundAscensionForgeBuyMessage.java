package iskallia.vault.network.message;

import iskallia.vault.container.AscensionForgeContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundAscensionForgeBuyMessage() {
   private static final ServerboundAscensionForgeBuyMessage instance = new ServerboundAscensionForgeBuyMessage();

   public static void encode(ServerboundAscensionForgeBuyMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundAscensionForgeBuyMessage decode(FriendlyByteBuf buffer) {
      return instance;
   }

   public static void handle(ServerboundAscensionForgeBuyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null && sender.containerMenu instanceof AscensionForgeContainer ascensionForgeContainer) {
            ascensionForgeContainer.buy(sender);
         }
      });
      context.setPacketHandled(true);
   }
}
