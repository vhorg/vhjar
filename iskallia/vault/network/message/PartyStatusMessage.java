package iskallia.vault.network.message;

import iskallia.vault.client.ClientPartyData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PartyStatusMessage {
   private final ListNBT serializedParties;

   public PartyStatusMessage(ListNBT serializedParties) {
      this.serializedParties = serializedParties;
   }

   public static void encode(PartyStatusMessage message, PacketBuffer buffer) {
      CompoundNBT tag = new CompoundNBT();
      tag.func_218657_a("list", message.serializedParties);
      buffer.func_150786_a(tag);
   }

   public static PartyStatusMessage decode(PacketBuffer buffer) {
      return new PartyStatusMessage(buffer.func_150793_b().func_150295_c("list", 10));
   }

   public static void handle(PartyStatusMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientPartyData.receivePartyUpdate(message.serializedParties));
      context.setPacketHandled(true);
   }
}
