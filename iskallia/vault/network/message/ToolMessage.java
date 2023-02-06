package iskallia.vault.network.message;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.tool.ToolItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToolMessage {
   public static void sendOffset(int key) {
      ModNetwork.CHANNEL.sendToServer(new ToolMessage.Offset(key));
   }

   public static class Offset {
      public static final int RIGHT = 262;
      public static final int LEFT = 263;
      public static final int DOWN = 264;
      public static final int UP = 265;
      private int key;

      public Offset(int key) {
         this.key = key;
      }

      public static boolean isKey(int key) {
         return key >= 262 && key <= 265;
      }

      public static void encode(ToolMessage.Offset message, FriendlyByteBuf buffer) {
         buffer.writeVarInt(message.key);
      }

      public static ToolMessage.Offset decode(FriendlyByteBuf buffer) {
         return new ToolMessage.Offset(buffer.readVarInt());
      }

      public static void handle(ToolMessage.Offset message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
               ItemStack stack = player.getMainHandItem();
               if (stack.getItem() == ModItems.TOOL) {
                  switch (message.key) {
                     case 262:
                        ToolItem.offset(stack, 1, 0);
                        break;
                     case 263:
                        ToolItem.offset(stack, -1, 0);
                        break;
                     case 264:
                        ToolItem.offset(stack, 0, -1);
                        break;
                     case 265:
                        ToolItem.offset(stack, 0, 1);
                  }
               }
            }
         });
         context.setPacketHandled(true);
      }
   }
}
