package iskallia.vault.network.message;

import iskallia.vault.client.ClientSandEventData;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SandEventContributorMessage {
   private final ITextComponent contributor;

   public SandEventContributorMessage(ITextComponent contributor) {
      this.contributor = contributor;
   }

   public static void encode(SandEventContributorMessage message, PacketBuffer buffer) {
      buffer.func_179256_a(message.contributor);
   }

   public static SandEventContributorMessage decode(PacketBuffer buffer) {
      return new SandEventContributorMessage(buffer.func_179258_d());
   }

   public static void handle(SandEventContributorMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientSandEventData.getInstance().addContributor(message.contributor));
      context.setPacketHandled(true);
   }
}
