package iskallia.vault.network.message.base;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class OpcodeMessage<OPC extends Enum<OPC>> {
   public OPC opcode;
   public CompoundNBT payload;

   public void encodeSelf(OpcodeMessage<OPC> message, PacketBuffer buffer) {
      buffer.writeInt(message.opcode.ordinal());
      buffer.func_150786_a(message.payload);
   }

   public void decodeSelf(PacketBuffer buffer, Class<OPC> enumClass) {
      this.opcode = enumClass.getEnumConstants()[buffer.readInt()];
      this.payload = buffer.func_150793_b();
   }

   public static <O extends Enum<O>, T extends OpcodeMessage<O>> T composeMessage(T message, O opcode, Consumer<CompoundNBT> payloadSerializer) {
      message.opcode = (OPC)opcode;
      CompoundNBT payload = new CompoundNBT();
      payloadSerializer.accept(payload);
      message.payload = payload;
      return message;
   }
}
