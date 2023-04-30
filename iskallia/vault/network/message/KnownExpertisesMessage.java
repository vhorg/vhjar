package iskallia.vault.network.message;

import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.tree.ExpertiseTree;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class KnownExpertisesMessage {
   private ExpertiseTree tree;

   public KnownExpertisesMessage(ExpertiseTree tree) {
      this.tree = tree;
   }

   public ExpertiseTree getTree() {
      return this.tree;
   }

   public static void encode(KnownExpertisesMessage message, FriendlyByteBuf buffer) {
      ArrayBitBuffer bits = ArrayBitBuffer.empty();
      message.tree.writeBits(bits);
      buffer.writeLongArray(bits.toLongArray());
   }

   public static KnownExpertisesMessage decode(FriendlyByteBuf buffer) {
      BitBuffer bits = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
      ExpertiseTree tree = new ExpertiseTree();
      tree.readBits(bits);
      return new KnownExpertisesMessage(tree);
   }

   public static void handle(KnownExpertisesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientExpertiseData.updateTalents(message));
      context.setPacketHandled(true);
   }
}
