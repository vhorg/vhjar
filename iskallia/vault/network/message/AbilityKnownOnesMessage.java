package iskallia.vault.network.message;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityKnownOnesMessage {
   private final List<AbilityNode<?, ?>> learnedAbilities;

   public AbilityKnownOnesMessage(AbilityTree abilityTree) {
      this(abilityTree.getLearnedNodes());
   }

   private AbilityKnownOnesMessage(List<AbilityNode<?, ?>> learnedAbilities) {
      this.learnedAbilities = learnedAbilities;
   }

   public List<AbilityNode<?, ?>> getLearnedAbilities() {
      return this.learnedAbilities;
   }

   public static void encode(AbilityKnownOnesMessage message, FriendlyByteBuf buffer) {
      CompoundTag nbt = new CompoundTag();
      ListTag abilities = new ListTag();
      message.learnedAbilities.stream().map(AbilityNode::serializeNBT).forEach(abilities::add);
      nbt.put("LearnedAbilities", abilities);
      buffer.writeNbt(nbt);
   }

   public static AbilityKnownOnesMessage decode(FriendlyByteBuf buffer) {
      ArrayList<AbilityNode<?, ?>> abilities = new ArrayList<>();
      CompoundTag nbt = buffer.readNbt();
      ListTag learnedAbilities = nbt.getList("LearnedAbilities", 10);

      for (int i = 0; i < learnedAbilities.size(); i++) {
         abilities.add(AbilityNode.fromNBT(learnedAbilities.getCompound(i)));
      }

      return new AbilityKnownOnesMessage(abilities);
   }

   public static void handle(AbilityKnownOnesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientAbilityData.updateAbilities(message));
      context.setPacketHandled(true);
   }
}
