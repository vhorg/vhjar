package iskallia.vault.network.message;

import iskallia.vault.world.data.GodAltarData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateGodAltarDataMessage {
   private final List<GodAltarData.Entry> entries;

   public UpdateGodAltarDataMessage(List<GodAltarData.Entry> entries) {
      this.entries = entries;
   }

   public static void encode(UpdateGodAltarDataMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.entries.size());
      message.entries.forEach(entry -> entry.writeNbt().ifPresent(buffer::writeNbt));
   }

   public static UpdateGodAltarDataMessage decode(FriendlyByteBuf buffer) {
      int size = buffer.readInt();
      List<GodAltarData.Entry> entries = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         GodAltarData.Entry entry = new GodAltarData.Entry();
         entry.readNbt(buffer.readNbt());
         entries.add(entry);
      }

      return new UpdateGodAltarDataMessage(entries);
   }

   public static void handle(UpdateGodAltarDataMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> GodAltarData.CLIENT = message.entries);
      context.setPacketHandled(true);
   }
}
