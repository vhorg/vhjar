package iskallia.vault.network.message;

import iskallia.vault.client.ClientTalentData;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.tree.TalentTree;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class KnownTalentsMessage {
   private TalentTree tree;

   public KnownTalentsMessage(TalentTree tree) {
      this.tree = tree;
   }

   public TalentTree getTree() {
      return this.tree;
   }

   public static void encode(KnownTalentsMessage message, FriendlyByteBuf buffer) {
      ArrayBitBuffer bits = ArrayBitBuffer.empty();
      message.tree.writeBits(bits);
      buffer.writeLongArray(bits.toLongArray());
   }

   public static KnownTalentsMessage decode(FriendlyByteBuf buffer) {
      BitBuffer bits = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
      TalentTree tree = new TalentTree();
      tree.readBits(bits);
      return new KnownTalentsMessage(tree);
   }

   public static void handle(KnownTalentsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientTalentData.updateTalents(message));
      context.setPacketHandled(true);
   }
}
