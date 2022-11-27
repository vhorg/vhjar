package iskallia.vault.network.message;

import iskallia.vault.client.ClientPartyData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PartyMembersMessage {
   private ListTag serializedMembers;

   public PartyMembersMessage(ListTag serializedMembers) {
      this.serializedMembers = serializedMembers;
   }

   public static void encode(PartyMembersMessage message, FriendlyByteBuf buffer) {
      CompoundTag tag = new CompoundTag();
      tag.put("list", message.serializedMembers);
      buffer.writeNbt(tag);
   }

   public static PartyMembersMessage decode(FriendlyByteBuf buffer) {
      return new PartyMembersMessage(buffer.readNbt().getList("list", 10));
   }

   public static void handle(PartyMembersMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientPartyData.receivePartyMembers(message.serializedMembers));
      context.setPacketHandled(true);
   }
}
