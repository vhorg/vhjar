package iskallia.vault.network.message;

import iskallia.vault.container.VendingMachineContainer;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VendingUIMessage {
   public VendingUIMessage.Opcode opcode;
   public CompoundNBT payload;

   public static void encode(VendingUIMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.opcode.ordinal());
      buffer.func_150786_a(message.payload);
   }

   public static VendingUIMessage decode(PacketBuffer buffer) {
      VendingUIMessage message = new VendingUIMessage();
      message.opcode = VendingUIMessage.Opcode.values()[buffer.readInt()];
      message.payload = buffer.func_150793_b();
      return message;
   }

   public static void handle(VendingUIMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (message.opcode == VendingUIMessage.Opcode.SELECT_TRADE) {
            int index = message.payload.func_74762_e("Index");
            ServerPlayerEntity sender = context.getSender();
            Container openContainer = sender.field_71070_bA;
            if (openContainer instanceof VendingMachineContainer) {
               VendingMachineContainer vendingMachineContainer = (VendingMachineContainer)openContainer;
               vendingMachineContainer.selectTrade(index);
            }
         } else if (message.opcode == VendingUIMessage.Opcode.EJECT_CORE) {
            int index = message.payload.func_74762_e("Index");
            ServerPlayerEntity sender = context.getSender();
            Container openContainer = sender.field_71070_bA;
            if (openContainer instanceof VendingMachineContainer) {
               VendingMachineContainer vendingMachineContainer = (VendingMachineContainer)openContainer;
               vendingMachineContainer.ejectCore(index);
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static VendingUIMessage selectTrade(int index) {
      VendingUIMessage message = new VendingUIMessage();
      message.opcode = VendingUIMessage.Opcode.SELECT_TRADE;
      message.payload = new CompoundNBT();
      message.payload.func_74768_a("Index", index);
      return message;
   }

   public static VendingUIMessage ejectTrade(int index) {
      VendingUIMessage message = new VendingUIMessage();
      message.opcode = VendingUIMessage.Opcode.EJECT_CORE;
      message.payload = new CompoundNBT();
      message.payload.func_74768_a("Index", index);
      return message;
   }

   public static enum Opcode {
      SELECT_TRADE,
      EJECT_CORE;
   }
}
