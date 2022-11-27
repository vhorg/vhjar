package iskallia.vault.network.message;

import iskallia.vault.client.ClientSandEventData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SandEventUpdateMessage {
   private final float percentFilled;
   private final int sandSpawned;
   private final int sandCollected;

   public SandEventUpdateMessage(float percentFilled, int sandSpawned, int sandCollected) {
      this.percentFilled = percentFilled;
      this.sandSpawned = sandSpawned;
      this.sandCollected = sandCollected;
   }

   public float getPercentFilled() {
      return this.percentFilled;
   }

   public int getSandSpawned() {
      return this.sandSpawned;
   }

   public int getSandCollected() {
      return this.sandCollected;
   }

   public static void encode(SandEventUpdateMessage message, FriendlyByteBuf buffer) {
      buffer.writeFloat(message.percentFilled);
      buffer.writeInt(message.sandSpawned);
      buffer.writeInt(message.sandCollected);
   }

   public static SandEventUpdateMessage decode(FriendlyByteBuf buffer) {
      return new SandEventUpdateMessage(buffer.readFloat(), buffer.readInt(), buffer.readInt());
   }

   public static void handle(SandEventUpdateMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientSandEventData.getInstance().receive(message));
      context.setPacketHandled(true);
   }
}
