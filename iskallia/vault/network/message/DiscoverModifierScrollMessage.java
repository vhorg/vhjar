package iskallia.vault.network.message;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.container.modifier.DiscoverableModifier;
import iskallia.vault.item.ModifierScrollItem;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class DiscoverModifierScrollMessage {
   private final UUID scrollId;
   private final int scrollSlot;
   private final DiscoverableModifier gearModifier;

   public DiscoverModifierScrollMessage(UUID scrollId, int scrollSlot, DiscoverableModifier gearModifier) {
      this.scrollId = scrollId;
      this.scrollSlot = scrollSlot;
      this.gearModifier = gearModifier;
   }

   public static void encode(DiscoverModifierScrollMessage message, FriendlyByteBuf buffer) {
      buffer.writeUUID(message.scrollId);
      buffer.writeInt(message.scrollSlot);
      message.gearModifier.serialize(buffer);
   }

   public static DiscoverModifierScrollMessage decode(FriendlyByteBuf buffer) {
      UUID scrollId = buffer.readUUID();
      int scrollSlot = buffer.readInt();
      return new DiscoverModifierScrollMessage(scrollId, scrollSlot, DiscoverableModifier.deserialize(buffer));
   }

   public static void handle(DiscoverModifierScrollMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null) {
            ItemStack scroll = player.getInventory().getItem(message.scrollSlot);
            if (!scroll.isEmpty() && scroll.getItem() instanceof ModifierScrollItem) {
               UUID scrollId = ModifierScrollItem.getUuid(scroll);
               if (scrollId != null && scrollId.equals(message.scrollId)) {
                  UUID playerUid = ModifierScrollItem.getPlayerUuid(scroll);
                  if (playerUid != null && playerUid.equals(player.getUUID())) {
                     if (ModifierScrollItem.getDiscoverableModifiers(scroll).contains(message.gearModifier)) {
                        ModifierDiscoveryTileEntity.discoverGearModifier(player, message.gearModifier);
                        scroll.shrink(1);
                        player.getInventory().setItem(message.scrollSlot, scroll);
                     }
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
