package iskallia.vault.network.message;

import iskallia.vault.world.data.ParadoxCrystalData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateParadoxDataMessage {
   private final Map<UUID, ParadoxCrystalData.Entry> changed;

   public UpdateParadoxDataMessage(Map<UUID, ParadoxCrystalData.Entry> changed) {
      this.changed = changed;
   }

   public static void encode(UpdateParadoxDataMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.changed.size());
      message.changed.forEach((uuid, entry) -> {
         buffer.writeUUID(uuid);
         buffer.writeNbt(entry.writeNbt().orElse(new CompoundTag()));
      });
   }

   public static UpdateParadoxDataMessage decode(FriendlyByteBuf buffer) {
      int size = buffer.readInt();
      Map<UUID, ParadoxCrystalData.Entry> changed = new HashMap<>();

      for (int i = 0; i < size; i++) {
         UUID uuid = buffer.readUUID();
         CompoundTag tag = buffer.readNbt();
         ParadoxCrystalData.Entry entry = new ParadoxCrystalData.Entry();
         entry.readNbt(tag);
         changed.put(uuid, entry);
      }

      return new UpdateParadoxDataMessage(changed);
   }

   public static void handle(UpdateParadoxDataMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ParadoxCrystalData.CLIENT.putAll(message.changed));
      context.setPacketHandled(true);
   }
}
