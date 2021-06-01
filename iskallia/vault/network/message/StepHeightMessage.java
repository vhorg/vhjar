package iskallia.vault.network.message;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class StepHeightMessage {
   public float stepHeight;

   protected StepHeightMessage() {
   }

   public StepHeightMessage(float stepHeight) {
      this.stepHeight = stepHeight;
   }

   public static void encode(StepHeightMessage message, PacketBuffer buffer) {
      buffer.writeFloat(message.stepHeight);
   }

   public static StepHeightMessage decode(PacketBuffer buffer) {
      StepHeightMessage message = new StepHeightMessage();
      message.stepHeight = buffer.readFloat();
      return message;
   }

   public static void handle(StepHeightMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.func_71410_x().field_71439_g != null) {
            Minecraft.func_71410_x().field_71439_g.field_70138_W = message.stepHeight;
         }
      });
      context.setPacketHandled(true);
   }
}
