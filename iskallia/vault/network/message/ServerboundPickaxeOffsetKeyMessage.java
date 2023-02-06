package iskallia.vault.network.message;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.tool.PaxelItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundPickaxeOffsetKeyMessage {
   private final ServerboundPickaxeOffsetKeyMessage.Opcode opcode;

   private ServerboundPickaxeOffsetKeyMessage(ServerboundPickaxeOffsetKeyMessage.Opcode opcode) {
      this.opcode = opcode;
   }

   public static void send(ServerboundPickaxeOffsetKeyMessage.Opcode opcode) {
      ModNetwork.CHANNEL.sendToServer(new ServerboundPickaxeOffsetKeyMessage(opcode));
   }

   public static void encode(ServerboundPickaxeOffsetKeyMessage message, FriendlyByteBuf buffer) {
      buffer.writeEnum(message.opcode);
   }

   public static ServerboundPickaxeOffsetKeyMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundPickaxeOffsetKeyMessage((ServerboundPickaxeOffsetKeyMessage.Opcode)buffer.readEnum(ServerboundPickaxeOffsetKeyMessage.Opcode.class));
   }

   public static void handle(ServerboundPickaxeOffsetKeyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof PaxelItem) {
               PaxelItem.addOffset(stack, new Vec2(message.opcode.x, message.opcode.y));
               Vec2 offset = PaxelItem.getOffset(stack);
               player.sendMessage(new TextComponent("Offset: x" + (int)offset.x + ", y" + (int)offset.y), ChatType.GAME_INFO, player.getUUID());
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static enum Opcode {
      LEFT(-1, 0),
      RIGHT(1, 0),
      UP(0, 1),
      DOWN(0, -1);

      private final int x;
      private final int y;

      private Opcode(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }
   }
}
