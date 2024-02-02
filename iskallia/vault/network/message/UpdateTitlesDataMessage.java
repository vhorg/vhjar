package iskallia.vault.network.message;

import iskallia.vault.world.data.PlayerTitlesData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateTitlesDataMessage {
   private final Map<UUID, PlayerTitlesData.Entry> changed;

   public UpdateTitlesDataMessage(Map<UUID, PlayerTitlesData.Entry> changed) {
      this.changed = changed;
   }

   public static void encode(UpdateTitlesDataMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.changed.size());
      message.changed.forEach((uuid, entry) -> {
         buffer.writeUUID(uuid);
         buffer.writeNbt(entry.writeNbt().orElse(new CompoundTag()));
      });
   }

   public static UpdateTitlesDataMessage decode(FriendlyByteBuf buffer) {
      int size = buffer.readInt();
      Map<UUID, PlayerTitlesData.Entry> changed = new HashMap<>();

      for (int i = 0; i < size; i++) {
         UUID uuid = buffer.readUUID();
         CompoundTag tag = buffer.readNbt();
         PlayerTitlesData.Entry entry = new PlayerTitlesData.Entry();
         entry.readNbt(tag);
         changed.put(uuid, entry);
      }

      return new UpdateTitlesDataMessage(changed);
   }

   public static void handle(UpdateTitlesDataMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> PlayerTitlesData.CLIENT.putAll(message.changed));
      context.setPacketHandled(true);
   }
}
