package iskallia.vault.network.message;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.tree.AbilityTree;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityKnownOnesMessage {
   private AbilityTree tree;

   public AbilityKnownOnesMessage(AbilityTree tree) {
      this.tree = tree;
   }

   public AbilityTree getTree() {
      return this.tree;
   }

   public static void encode(AbilityKnownOnesMessage message, FriendlyByteBuf buffer) {
      ArrayBitBuffer bits = ArrayBitBuffer.empty();
      message.tree.writeBits(bits);
      buffer.writeLongArray(bits.toLongArray());
   }

   public static AbilityKnownOnesMessage decode(FriendlyByteBuf buffer) {
      BitBuffer bits = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
      AbilityTree tree = new AbilityTree();
      tree.readBits(bits);
      return new AbilityKnownOnesMessage(tree);
   }

   public static void handle(AbilityKnownOnesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientAbilityData.updateAbilities(message));
      context.setPacketHandled(true);
   }
}
