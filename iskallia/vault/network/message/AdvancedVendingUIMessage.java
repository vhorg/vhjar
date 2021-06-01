package iskallia.vault.network.message;

import iskallia.vault.container.AdvancedVendingContainer;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AdvancedVendingUIMessage {
   public AdvancedVendingUIMessage.Opcode opcode;
   public CompoundNBT payload;

   public static void encode(AdvancedVendingUIMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.opcode.ordinal());
      buffer.func_150786_a(message.payload);
   }

   public static AdvancedVendingUIMessage decode(PacketBuffer buffer) {
      AdvancedVendingUIMessage message = new AdvancedVendingUIMessage();
      message.opcode = AdvancedVendingUIMessage.Opcode.values()[buffer.readInt()];
      message.payload = buffer.func_150793_b();
      return message;
   }

   public static void handle(AdvancedVendingUIMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (message.opcode == AdvancedVendingUIMessage.Opcode.SELECT_TRADE) {
            int index = message.payload.func_74762_e("Index");
            ServerPlayerEntity sender = context.getSender();
            Container openContainer = sender.field_71070_bA;
            if (openContainer instanceof AdvancedVendingContainer) {
               AdvancedVendingContainer vendingMachineContainer = (AdvancedVendingContainer)openContainer;
               vendingMachineContainer.selectTrade(index);
            }
         } else if (message.opcode == AdvancedVendingUIMessage.Opcode.EJECT_CORE) {
            int index = message.payload.func_74762_e("Index");
            ServerPlayerEntity sender = context.getSender();
            Container openContainer = sender.field_71070_bA;
            if (openContainer instanceof AdvancedVendingContainer) {
               AdvancedVendingContainer vendingMachineContainer = (AdvancedVendingContainer)openContainer;
               vendingMachineContainer.ejectCore(index);
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static AdvancedVendingUIMessage selectTrade(int index) {
      AdvancedVendingUIMessage message = new AdvancedVendingUIMessage();
      message.opcode = AdvancedVendingUIMessage.Opcode.SELECT_TRADE;
      message.payload = new CompoundNBT();
      message.payload.func_74768_a("Index", index);
      return message;
   }

   public static AdvancedVendingUIMessage ejectTrade(int index) {
      AdvancedVendingUIMessage message = new AdvancedVendingUIMessage();
      message.opcode = AdvancedVendingUIMessage.Opcode.EJECT_CORE;
      message.payload = new CompoundNBT();
      message.payload.func_74768_a("Index", index);
      return message;
   }

   public static enum Opcode {
      SELECT_TRADE,
      EJECT_CORE;
   }
}
