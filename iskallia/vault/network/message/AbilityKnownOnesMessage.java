package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.type.PlayerAbility;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AbilityKnownOnesMessage {
   public List<AbilityNode<?>> learnedAbilities;

   public AbilityKnownOnesMessage() {
   }

   public AbilityKnownOnesMessage(AbilityTree abilityTree) {
      this(abilityTree.learnedNodes());
   }

   public AbilityKnownOnesMessage(List<AbilityNode<?>> learnedAbilities) {
      this.learnedAbilities = learnedAbilities;
   }

   public static void encode(AbilityKnownOnesMessage message, PacketBuffer buffer) {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT abilities = new ListNBT();
      message.learnedAbilities.stream().map(AbilityNode::serializeNBT).forEach(abilities::add);
      nbt.func_218657_a("LearnedAbilities", abilities);
      buffer.func_150786_a(nbt);
   }

   public static AbilityKnownOnesMessage decode(PacketBuffer buffer) {
      AbilityKnownOnesMessage message = new AbilityKnownOnesMessage();
      message.learnedAbilities = new LinkedList<>();
      CompoundNBT nbt = buffer.func_150793_b();
      ListNBT learnedAbilities = nbt.func_150295_c("LearnedAbilities", 10);

      for (int i = 0; i < learnedAbilities.size(); i++) {
         message.learnedAbilities.add(AbilityNode.fromNBT(learnedAbilities.func_150305_b(i), PlayerAbility.class));
      }

      return message;
   }

   public static void handle(AbilityKnownOnesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         System.out.println("Received tree! " + message.learnedAbilities.size());
         AbilitiesOverlay.learnedAbilities = message.learnedAbilities;
      });
      context.setPacketHandled(true);
   }
}
