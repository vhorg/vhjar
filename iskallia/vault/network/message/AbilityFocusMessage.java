package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AbilityFocusMessage {
   public int focusedIndex;

   public AbilityFocusMessage() {
   }

   public AbilityFocusMessage(int focusedIndex) {
      this.focusedIndex = focusedIndex;
   }

   public static void encode(AbilityFocusMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.focusedIndex);
   }

   public static AbilityFocusMessage decode(PacketBuffer buffer) {
      AbilityFocusMessage message = new AbilityFocusMessage();
      message.focusedIndex = buffer.readInt();
      return message;
   }

   public static void handle(AbilityFocusMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> AbilitiesOverlay.focusedIndex = message.focusedIndex);
      context.setPacketHandled(true);
   }
}
