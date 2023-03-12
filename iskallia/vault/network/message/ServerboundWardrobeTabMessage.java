package iskallia.vault.network.message;

import iskallia.vault.container.WardrobeContainer;
import iskallia.vault.init.ModBlocks;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundWardrobeTabMessage {
   private final boolean openHotbar;
   private final BlockPos pos;

   public ServerboundWardrobeTabMessage(boolean openHotbar, BlockPos pos) {
      this.openHotbar = openHotbar;
      this.pos = pos;
   }

   public static void encode(ServerboundWardrobeTabMessage message, FriendlyByteBuf buffer) {
      buffer.writeBoolean(message.openHotbar);
      buffer.writeBlockPos(message.pos);
   }

   public static ServerboundWardrobeTabMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundWardrobeTabMessage(buffer.readBoolean(), buffer.readBlockPos());
   }

   public static void handle(ServerboundWardrobeTabMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer serverPlayer = context.getSender();
            NetworkHooks.openGui(
               serverPlayer,
               new MenuProvider() {
                  public Component getDisplayName() {
                     return ModBlocks.WARDROBE.getName();
                  }

                  public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                     return (AbstractContainerMenu)(message.openHotbar
                        ? new WardrobeContainer.Hotbar(windowId, inventory, message.pos)
                        : new WardrobeContainer.Gear(windowId, inventory, message.pos));
                  }
               },
               message.pos
            );
         }
      );
      context.setPacketHandled(true);
   }
}
