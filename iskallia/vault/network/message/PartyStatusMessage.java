package iskallia.vault.network.message;

import iskallia.vault.client.ClientPartyData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PartyStatusMessage {
   private final ListTag serializedParties;

   public PartyStatusMessage(ListTag serializedParties) {
      this.serializedParties = serializedParties;
   }

   public static void encode(PartyStatusMessage message, FriendlyByteBuf buffer) {
      CompoundTag tag = new CompoundTag();
      tag.put("list", message.serializedParties);
      buffer.writeNbt(tag);
   }

   public static PartyStatusMessage decode(FriendlyByteBuf buffer) {
      return new PartyStatusMessage(buffer.readNbt().getList("list", 10));
   }

   public static void handle(PartyStatusMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientPartyData.receivePartyUpdate(message.serializedParties));
      context.setPacketHandled(true);
   }
}
