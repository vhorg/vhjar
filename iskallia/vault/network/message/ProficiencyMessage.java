package iskallia.vault.network.message;

import iskallia.vault.client.ClientProficiencyData;
import iskallia.vault.gear.crafting.ProficiencyType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ProficiencyMessage {
   private final Map<ProficiencyType, Float> proficiency;

   private ProficiencyMessage() {
      this(new HashMap<>());
   }

   public ProficiencyMessage(Map<ProficiencyType, Float> proficiency) {
      this.proficiency = proficiency;
   }

   public static void encode(ProficiencyMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.proficiency.size());
      message.proficiency.forEach((type, amount) -> {
         buffer.writeEnum(type);
         buffer.writeFloat(amount);
      });
   }

   public static ProficiencyMessage decode(FriendlyByteBuf buffer) {
      ProficiencyMessage message = new ProficiencyMessage();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         ProficiencyType type = (ProficiencyType)buffer.readEnum(ProficiencyType.class);
         message.proficiency.put(type, buffer.readFloat());
      }

      return message;
   }

   public static void handle(ProficiencyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientProficiencyData.updateProficiencies(message.proficiency));
   }
}
