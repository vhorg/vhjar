package iskallia.vault.network.message;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.util.RenameType;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class RenameUIMessage {
   public RenameType renameType;
   public CompoundTag payload;

   public static void encode(RenameUIMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.renameType.ordinal());
      buffer.writeNbt(message.payload);
   }

   public static RenameUIMessage decode(FriendlyByteBuf buffer) {
      RenameUIMessage message = new RenameUIMessage();
      message.renameType = RenameType.values()[buffer.readInt()];
      message.payload = buffer.readNbt();
      return message;
   }

   public static void handle(RenameUIMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         CompoundTag data = message.payload.getCompound("Data");
         ServerPlayer sender = context.getSender();
         switch (message.renameType) {
            case PLAYER_STATUE:
               BlockPos statuePos = NbtUtils.readBlockPos(data.getCompound("Pos"));
               if (sender.getCommandSenderWorld().getBlockEntity(statuePos) instanceof LootStatueTileEntity statue) {
                  statue.getSkin().updateSkin(data.getString("PlayerNickname"));
                  statue.sendUpdates();
               }
               break;
            case VAULT_CRYSTAL:
               sender.getInventory().items.set(sender.getInventory().selected, ItemStack.of(data));
               break;
            case CRYO_CHAMBER:
               BlockPos pos = NbtUtils.readBlockPos(data.getCompound("BlockPos"));
               String name = data.getString("EternalName");
               if (sender.getCommandSenderWorld().getBlockEntity(pos) instanceof CryoChamberTileEntity chamber) {
                  chamber.renameEternal(name);
                  chamber.getSkin().updateSkin(name);
                  chamber.sendUpdates();
               }
         }
      });
      context.setPacketHandled(true);
   }

   public static RenameUIMessage updateName(RenameType type, CompoundTag nbt) {
      RenameUIMessage message = new RenameUIMessage();
      message.renameType = type;
      message.payload = nbt;
      return message;
   }
}
