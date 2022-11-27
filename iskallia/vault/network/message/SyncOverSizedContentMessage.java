package iskallia.vault.network.message;

import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.util.NetcodeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncOverSizedContentMessage {
   private final int containerId;
   private final int stateId;
   private final List<OverSizedItemStack> items;
   private final ItemStack carriedItem;

   public SyncOverSizedContentMessage(int containerId, int stateId, List<ItemStack> items, ItemStack carriedItem) {
      this.containerId = containerId;
      this.stateId = stateId;
      this.items = items.stream().map(OverSizedItemStack::of).toList();
      this.carriedItem = carriedItem.copy();
   }

   public SyncOverSizedContentMessage(FriendlyByteBuf buf) {
      this.containerId = buf.readInt();
      this.stateId = buf.readInt();
      this.items = NetcodeUtils.readCollection(buf, ArrayList::new, buffer -> OverSizedItemStack.read(buf));
      this.carriedItem = buf.readItem();
   }

   public static void encode(SyncOverSizedContentMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.containerId);
      buffer.writeInt(message.stateId);
      NetcodeUtils.writeCollection(buffer, message.items, (stack, buf) -> stack.write(buffer));
      buffer.writeItem(message.carriedItem);
   }

   public static SyncOverSizedContentMessage decode(FriendlyByteBuf buffer) {
      return new SyncOverSizedContentMessage(buffer);
   }

   public static void handle(SyncOverSizedContentMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> handleContent(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void handleContent(SyncOverSizedContentMessage message) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         if (message.containerId == player.containerMenu.containerId) {
            List<ItemStack> contents = message.items.stream().map(OverSizedItemStack::overSizedStack).toList();
            player.containerMenu.initializeContents(message.stateId, contents, message.carriedItem);
         }
      }
   }
}
