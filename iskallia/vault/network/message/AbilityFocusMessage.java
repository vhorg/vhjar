package iskallia.vault.network.message;

import iskallia.vault.client.ClientAbilityData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityFocusMessage {
   private final String selectedAbility;

   public AbilityFocusMessage(String selectedAbility) {
      this.selectedAbility = selectedAbility;
   }

   public String getSelectedAbility() {
      return this.selectedAbility;
   }

   public static void encode(AbilityFocusMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.selectedAbility);
   }

   public static AbilityFocusMessage decode(FriendlyByteBuf buffer) {
      return new AbilityFocusMessage(buffer.readUtf(32767));
   }

   public static void handle(AbilityFocusMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientAbilityData.updateSelectedAbility(message));
      context.setPacketHandled(true);
   }
}
