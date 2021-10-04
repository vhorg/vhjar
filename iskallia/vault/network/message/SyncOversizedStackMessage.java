package iskallia.vault.network.message;

import iskallia.vault.container.inventory.ShardPouchContainer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SyncOversizedStackMessage {
   private int windowId = 0;
   private int slot = 0;
   private ItemStack stack;

   public SyncOversizedStackMessage() {
      this.stack = ItemStack.field_190927_a;
   }

   public SyncOversizedStackMessage(int windowId, int slot, ItemStack stack) {
      this.windowId = windowId;
      this.slot = slot;
      this.stack = stack.func_77946_l();
   }

   public SyncOversizedStackMessage(PacketBuffer buf) {
      this.windowId = buf.readInt();
      this.slot = buf.readInt();
      this.stack = buf.func_150791_c();
      this.stack.func_190920_e(buf.readInt());
   }

   public static void encode(SyncOversizedStackMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.windowId);
      buffer.writeInt(message.slot);
      buffer.func_150788_a(message.stack);
      buffer.writeInt(message.stack.func_190916_E());
   }

   public static SyncOversizedStackMessage decode(PacketBuffer buffer) {
      return new SyncOversizedStackMessage(buffer);
   }

   public static void handle(SyncOversizedStackMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> setClientStack(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void setClientStack(SyncOversizedStackMessage message) {
      PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      if (player != null) {
         if (player.field_71070_bA instanceof ShardPouchContainer && message.windowId == player.field_71070_bA.field_75152_c) {
            ((Slot)player.field_71070_bA.field_75151_b.get(message.slot)).func_75215_d(message.stack);
         }
      }
   }
}
