package iskallia.vault.network.message;

import iskallia.vault.client.ClientPartyData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PartyMembersMessage {
   private ListNBT serializedMembers;

   public PartyMembersMessage(ListNBT serializedMembers) {
      this.serializedMembers = serializedMembers;
   }

   public static void encode(PartyMembersMessage message, PacketBuffer buffer) {
      CompoundNBT tag = new CompoundNBT();
      tag.func_218657_a("list", message.serializedMembers);
      buffer.func_150786_a(tag);
   }

   public static PartyMembersMessage decode(PacketBuffer buffer) {
      return new PartyMembersMessage(buffer.func_150793_b().func_150295_c("list", 10));
   }

   public static void handle(PartyMembersMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientPartyData.receivePartyMembers(message.serializedMembers));
      context.setPacketHandled(true);
   }
}
