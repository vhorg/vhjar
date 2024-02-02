package iskallia.vault.network.message;

import iskallia.vault.container.AscensionForgeContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundSelectAscensionForgeItemMessage(ResourceLocation modelId, ItemStack stack) {
   public static void encode(ServerboundSelectAscensionForgeItemMessage message, FriendlyByteBuf buffer) {
      if (message.modelId == null) {
         buffer.writeBoolean(false);
      } else {
         buffer.writeBoolean(true);
         buffer.writeResourceLocation(message.modelId);
      }

      buffer.writeItemStack(message.stack, false);
   }

   public static ServerboundSelectAscensionForgeItemMessage decode(FriendlyByteBuf buffer) {
      return buffer.readBoolean()
         ? new ServerboundSelectAscensionForgeItemMessage(buffer.readResourceLocation(), buffer.readItem())
         : new ServerboundSelectAscensionForgeItemMessage(null, buffer.readItem());
   }

   public static void handle(ServerboundSelectAscensionForgeItemMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (sender.containerMenu instanceof AscensionForgeContainer container) {
               container.selectItem(message.modelId(), message.stack());
            }
         }
      });
      context.setPacketHandled(true);
   }
}
