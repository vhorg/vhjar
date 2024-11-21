package iskallia.vault.network.message;

import iskallia.vault.client.ClientProficiencyData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ProficiencyMessage {
   private final int proficiency;

   public ProficiencyMessage(int proficiency) {
      this.proficiency = proficiency;
   }

   public static void encode(ProficiencyMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.proficiency);
   }

   public static ProficiencyMessage decode(FriendlyByteBuf buffer) {
      return new ProficiencyMessage(buffer.readInt());
   }

   public static void handle(ProficiencyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientProficiencyData.updateProficiency(message.proficiency));
      context.setPacketHandled(true);
   }
}
