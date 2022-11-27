package iskallia.vault.network.message.base;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class OpcodeMessage<OPC extends Enum<OPC>> {
   public OPC opcode;
   public CompoundTag payload;

   public void encodeSelf(OpcodeMessage<OPC> message, FriendlyByteBuf buffer) {
      buffer.writeEnum(message.opcode);
      buffer.writeNbt(message.payload);
   }

   public void decodeSelf(FriendlyByteBuf buffer, Class<OPC> enumClass) {
      this.opcode = (OPC)buffer.readEnum(enumClass);
      this.payload = buffer.readNbt();
   }

   public static <O extends Enum<O>, T extends OpcodeMessage<O>> T composeMessage(T message, O opcode, Consumer<CompoundTag> payloadSerializer) {
      message.opcode = (OPC)opcode;
      CompoundTag payload = new CompoundTag();
      payloadSerializer.accept(payload);
      message.payload = payload;
      return message;
   }
}
