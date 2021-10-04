package iskallia.vault.network.message;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.skill.ability.AbilityGroup;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AbilityFocusMessage {
   private final String selectedAbility;

   public AbilityFocusMessage(AbilityGroup<?, ?> ability) {
      this(ability.getParentName());
   }

   public AbilityFocusMessage(String selectedAbility) {
      this.selectedAbility = selectedAbility;
   }

   public String getSelectedAbility() {
      return this.selectedAbility;
   }

   public static void encode(AbilityFocusMessage message, PacketBuffer buffer) {
      buffer.func_180714_a(message.selectedAbility);
   }

   public static AbilityFocusMessage decode(PacketBuffer buffer) {
      return new AbilityFocusMessage(buffer.func_150789_c(32767));
   }

   public static void handle(AbilityFocusMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientAbilityData.updateSelectedAbility(message));
      context.setPacketHandled(true);
   }
}
