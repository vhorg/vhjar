package iskallia.vault.network.message;

import iskallia.vault.client.ClientTalentData;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

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

   public static void encode(KnownTalentsMessage message, FriendlyByteBuf buffer) {
      CompoundTag nbt = new CompoundTag();
      ListTag talents = new ListTag();
      message.learnedTalents.stream().map(TalentNode::serializeNBT).forEach(talents::add);
      nbt.put("LearnedTalents", talents);
      buffer.writeNbt(nbt);
   }

   public static KnownTalentsMessage decode(FriendlyByteBuf buffer) {
      List<TalentNode<?>> abilities = new ArrayList<>();
      CompoundTag nbt = buffer.readNbt();
      ListTag learnedTalents = nbt.getList("LearnedTalents", 10);

      for (int i = 0; i < learnedTalents.size(); i++) {
         abilities.add(new TalentNode(learnedTalents.getCompound(i)));
      }

      return new KnownTalentsMessage(abilities);
   }

   public static void handle(KnownTalentsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientTalentData.updateTalents(message));
      context.setPacketHandled(true);
   }
}
