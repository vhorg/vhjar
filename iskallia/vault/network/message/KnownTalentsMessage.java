package iskallia.vault.network.message;

import iskallia.vault.client.ClientTalentData;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KnownTalentsMessage {
   private final List<TalentNode<?>> learnedTalents;

   public KnownTalentsMessage(TalentTree talentTree) {
      this(talentTree.getLearnedNodes());
   }

   private KnownTalentsMessage(List<TalentNode<?>> learnedTalents) {
      this.learnedTalents = learnedTalents;
   }

   public List<TalentNode<?>> getLearnedTalents() {
      return this.learnedTalents;
   }

   public static void encode(KnownTalentsMessage message, PacketBuffer buffer) {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT talents = new ListNBT();
      message.learnedTalents.stream().map(TalentNode::serializeNBT).forEach(talents::add);
      nbt.func_218657_a("LearnedTalents", talents);
      buffer.func_150786_a(nbt);
   }

   public static KnownTalentsMessage decode(PacketBuffer buffer) {
      List<TalentNode<?>> abilities = new ArrayList<>();
      CompoundNBT nbt = buffer.func_150793_b();
      ListNBT learnedTalents = nbt.func_150295_c("LearnedTalents", 10);

      for (int i = 0; i < learnedTalents.size(); i++) {
         abilities.add(TalentNode.fromNBT(null, learnedTalents.func_150305_b(i), 1));
      }

      return new KnownTalentsMessage(abilities);
   }

   public static void handle(KnownTalentsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientTalentData.updateTalents(message));
      context.setPacketHandled(true);
   }
}
