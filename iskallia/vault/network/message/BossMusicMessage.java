package iskallia.vault.network.message;

import iskallia.vault.client.ClientVaultRaidData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class BossMusicMessage {
   private final boolean state;

   public BossMusicMessage(boolean state) {
      this.state = state;
   }

   public static void encode(BossMusicMessage message, FriendlyByteBuf buffer) {
      buffer.writeBoolean(message.state);
   }

   public static BossMusicMessage decode(FriendlyByteBuf buffer) {
      return new BossMusicMessage(buffer.readBoolean());
   }

   public boolean isInFight() {
      return this.state;
   }

   public static void handle(BossMusicMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientVaultRaidData.receiveBossUpdate(message));
      context.setPacketHandled(true);
   }
}
