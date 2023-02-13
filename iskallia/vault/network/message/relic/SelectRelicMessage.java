package iskallia.vault.network.message.relic;

import iskallia.vault.container.RelicPedestalContainer;
import iskallia.vault.util.EntityHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public record SelectRelicMessage(ResourceLocation relicId) {
   public static void encode(SelectRelicMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.relicId);
   }

   public static SelectRelicMessage decode(FriendlyByteBuf buffer) {
      ResourceLocation modelId = buffer.readResourceLocation();
      return new SelectRelicMessage(modelId);
   }

   public static void handle(SelectRelicMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (sender.containerMenu instanceof RelicPedestalContainer container) {
               container.getInternalInventory().forEachInput(relativeIndex -> {
                  ItemStack itemStack = container.getInternalInventory().getItem(relativeIndex);
                  if (!itemStack.isEmpty()) {
                     EntityHelper.giveItem(sender, itemStack);
                     container.getInternalInventory().setItem(relativeIndex, ItemStack.EMPTY);
                  }
               });
               container.selectRelic(message.relicId);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
