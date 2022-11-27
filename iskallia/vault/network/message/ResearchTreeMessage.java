package iskallia.vault.network.message;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.StageManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ResearchTreeMessage {
   public ResearchTree researchTree;

   public ResearchTreeMessage(ResearchTree researchTree) {
      this.researchTree = researchTree;
   }

   public static void encode(ResearchTreeMessage message, FriendlyByteBuf buffer) {
      buffer.writeNbt(message.researchTree.serializeNBT());
   }

   public static ResearchTreeMessage decode(FriendlyByteBuf buffer) {
      return new ResearchTreeMessage(new ResearchTree(buffer.readNbt()));
   }

   public static void handle(ResearchTreeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> StageManager.RESEARCH_TREE = message.researchTree);
      context.setPacketHandled(true);
   }
}
