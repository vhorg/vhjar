package iskallia.vault.network.message;

import iskallia.vault.container.oversized.OverSizedItemStack;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncOverSizedStackMessage {
   private final int containerId;
   private final int stateId;
   private final int slot;
   private final OverSizedItemStack stack;

   public SyncOverSizedStackMessage(int containerId, int stateId, int slot, ItemStack stack) {
      this.containerId = containerId;
      this.stateId = stateId;
      this.slot = slot;
      this.stack = OverSizedItemStack.of(stack);
   }

   public SyncOverSizedStackMessage(FriendlyByteBuf buf) {
      this.containerId = buf.readInt();
      this.stateId = buf.readInt();
      this.slot = buf.readInt();
      this.stack = OverSizedItemStack.read(buf);
   }

   public static void encode(SyncOverSizedStackMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.containerId);
      buffer.writeInt(message.stateId);
      buffer.writeInt(message.slot);
      message.stack.write(buffer);
   }

   public static SyncOverSizedStackMessage decode(FriendlyByteBuf buffer) {
      return new SyncOverSizedStackMessage(buffer);
   }

   public static void handle(SyncOverSizedStackMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> setClientStack(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void setClientStack(SyncOverSizedStackMessage message) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         if (message.containerId == player.containerMenu.containerId) {
            player.containerMenu.setItem(message.slot, message.stateId, message.stack.overSizedStack());
         }
      }
   }
}
