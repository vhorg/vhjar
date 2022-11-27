package iskallia.vault.network.message;

import iskallia.vault.block.entity.LootStatueTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class OmegaStatueUIMessage {
   public OmegaStatueUIMessage.Opcode opcode;
   public CompoundTag payload;

   public static void encode(OmegaStatueUIMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.opcode.ordinal());
      buffer.writeNbt(message.payload);
   }

   public static OmegaStatueUIMessage decode(FriendlyByteBuf buffer) {
      OmegaStatueUIMessage message = new OmegaStatueUIMessage();
      message.opcode = OmegaStatueUIMessage.Opcode.values()[buffer.readInt()];
      message.payload = buffer.readNbt();
      return message;
   }

   public static void handle(OmegaStatueUIMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (message.opcode == OmegaStatueUIMessage.Opcode.SELECT_ITEM) {
            ItemStack stack = ItemStack.of(message.payload.getCompound("Item"));
            BlockPos statuePos = NbtUtils.readBlockPos(message.payload.getCompound("Position"));
            Level world = context.getSender().getLevel();
            BlockEntity te = world.getBlockEntity(statuePos);
            if (te instanceof LootStatueTileEntity) {
               ((LootStatueTileEntity)te).setLootItem(stack);
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static OmegaStatueUIMessage selectItem(ItemStack stack, BlockPos statuePos) {
      OmegaStatueUIMessage message = new OmegaStatueUIMessage();
      message.opcode = OmegaStatueUIMessage.Opcode.SELECT_ITEM;
      message.payload = new CompoundTag();
      message.payload.put("Item", stack.serializeNBT());
      message.payload.put("Position", NbtUtils.writeBlockPos(statuePos));
      return message;
   }

   public static enum Opcode {
      SELECT_ITEM;
   }
}
