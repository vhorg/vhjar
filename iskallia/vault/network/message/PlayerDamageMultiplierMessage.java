package iskallia.vault.network.message;

import iskallia.vault.client.ClientDamageData;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PlayerDamageMultiplierMessage {
   private final float multiplier;

   public PlayerDamageMultiplierMessage(float multiplier) {
      this.multiplier = multiplier;
   }

   public float getMultiplier() {
      return this.multiplier;
   }

   public static void encode(PlayerDamageMultiplierMessage message, PacketBuffer buffer) {
      buffer.writeFloat(message.multiplier);
   }

   public static PlayerDamageMultiplierMessage decode(PacketBuffer buffer) {
      return new PlayerDamageMultiplierMessage(buffer.readFloat());
   }

   public static void handle(PlayerDamageMultiplierMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientDamageData.receiveUpdate(message));
      context.setPacketHandled(true);
   }
}
