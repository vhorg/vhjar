package iskallia.vault.network.message;

import iskallia.vault.client.gui.screen.ToolViseScreen;
import iskallia.vault.container.ToolViseContainerMenu;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundRefreshToolViseMessage(BlockPos pos) {
   public static void encode(ClientboundRefreshToolViseMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
   }

   public static ClientboundRefreshToolViseMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundRefreshToolViseMessage(buffer.readBlockPos());
   }

   public static void handle(ClientboundRefreshToolViseMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         if (player != null) {
            if (minecraft.screen instanceof ToolViseScreen toolViseScreen) {
               if (minecraft.player.containerMenu instanceof ToolViseContainerMenu toolViseContainerMenu) {
                  toolViseContainerMenu.slots.forEach(slot -> toolViseScreen.slotChanged(toolViseContainerMenu, slot.index, slot.getItem()));
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
