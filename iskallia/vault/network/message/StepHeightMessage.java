package iskallia.vault.network.message;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class StepHeightMessage {
   private final float stepHeight;

   public StepHeightMessage(float stepHeight) {
      this.stepHeight = stepHeight;
   }

   public static void encode(StepHeightMessage message, FriendlyByteBuf buffer) {
      buffer.writeFloat(message.stepHeight);
   }

   public static StepHeightMessage decode(FriendlyByteBuf buffer) {
      return new StepHeightMessage(buffer.readFloat());
   }

   public static void handle(StepHeightMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.maxUpStep = message.stepHeight;
         }
      });
      context.setPacketHandled(true);
   }
}
