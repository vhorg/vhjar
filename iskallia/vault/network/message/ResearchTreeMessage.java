package iskallia.vault.network.message;

import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.StageManager;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ResearchTreeMessage {
   public ResearchTree researchTree;
   public UUID playerUUID;

   public ResearchTreeMessage() {
   }

   public ResearchTreeMessage(ResearchTree researchTree, UUID playerUUID) {
      this.researchTree = researchTree;
      this.playerUUID = playerUUID;
   }

   public static void encode(ResearchTreeMessage message, PacketBuffer buffer) {
      buffer.func_179252_a(message.playerUUID);
      buffer.func_150786_a(message.researchTree.serializeNBT());
   }

   public static ResearchTreeMessage decode(PacketBuffer buffer) {
      ResearchTreeMessage message = new ResearchTreeMessage();
      message.researchTree = new ResearchTree(buffer.func_179253_g());
      message.researchTree.deserializeNBT(Objects.requireNonNull(buffer.func_150793_b()));
      return message;
   }

   public static void handle(ResearchTreeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> StageManager.RESEARCH_TREE = message.researchTree);
      context.setPacketHandled(true);
   }
}
