package iskallia.vault.network.message;

import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PlayerSnapshotMessage {
   private final AttributeSnapshot snapshot;

   private PlayerSnapshotMessage(AttributeSnapshot snapshot) {
      this.snapshot = snapshot;
   }

   public AttributeSnapshot getSnapshot() {
      return this.snapshot;
   }

   public static PlayerSnapshotMessage of(AttributeSnapshot snapshot) {
      return new PlayerSnapshotMessage(snapshot);
   }

   public static void encode(PlayerSnapshotMessage message, FriendlyByteBuf buffer) {
      message.getSnapshot().write(buffer);
   }

   public static PlayerSnapshotMessage decode(FriendlyByteBuf buffer) {
      return new PlayerSnapshotMessage(new AttributeSnapshot(buffer));
   }

   public static void handle(PlayerSnapshotMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> AttributeSnapshotHelper.getInstance().receiveClientSnapshot(message.getSnapshot()));
      context.setPacketHandled(true);
   }
}
