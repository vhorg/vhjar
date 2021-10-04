package iskallia.vault.network.message;

import iskallia.vault.block.entity.LootStatueTileEntity;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class OmegaStatueUIMessage {
   public OmegaStatueUIMessage.Opcode opcode;
   public CompoundNBT payload;

   public static void encode(OmegaStatueUIMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.opcode.ordinal());
      buffer.func_150786_a(message.payload);
   }

   public static OmegaStatueUIMessage decode(PacketBuffer buffer) {
      OmegaStatueUIMessage message = new OmegaStatueUIMessage();
      message.opcode = OmegaStatueUIMessage.Opcode.values()[buffer.readInt()];
      message.payload = buffer.func_150793_b();
      return message;
   }

   public static void handle(OmegaStatueUIMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (message.opcode == OmegaStatueUIMessage.Opcode.SELECT_ITEM) {
            ItemStack stack = ItemStack.func_199557_a(message.payload.func_74775_l("Item"));
            BlockPos statuePos = NBTUtil.func_186861_c(message.payload.func_74775_l("Position"));
            World world = context.getSender().func_71121_q();
            TileEntity te = world.func_175625_s(statuePos);
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
      message.payload = new CompoundNBT();
      message.payload.func_218657_a("Item", stack.serializeNBT());
      message.payload.func_218657_a("Position", NBTUtil.func_186859_a(statuePos));
      return message;
   }

   public static enum Opcode {
      SELECT_ITEM;
   }
}
