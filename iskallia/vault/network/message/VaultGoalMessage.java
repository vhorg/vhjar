package iskallia.vault.network.message;

import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.base.OpcodeMessage;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import iskallia.vault.world.vault.logic.objective.architect.VotingSession;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent.Serializer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VaultGoalMessage extends OpcodeMessage<VaultGoalMessage.VaultGoal> {
   private VaultGoalMessage() {
   }

   public static void encode(VaultGoalMessage message, PacketBuffer buffer) {
      message.encodeSelf(message, buffer);
   }

   public static VaultGoalMessage decode(PacketBuffer buffer) {
      VaultGoalMessage message = new VaultGoalMessage();
      message.decodeSelf(buffer, VaultGoalMessage.VaultGoal.class);
      return message;
   }

   public static void handle(VaultGoalMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> VaultGoalData.create(message));
      context.setPacketHandled(true);
   }

   public static VaultGoalMessage obeliskGoal(int touched, int max) {
      ITextComponent text = new StringTextComponent("Obelisks").func_240699_a_(TextFormatting.BOLD);
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.OBELISK_GOAL, payload -> {
         payload.func_74778_a("Message", Serializer.func_150696_a(text));
         payload.func_74768_a("TouchedObelisks", touched);
         payload.func_74768_a("MaxObelisks", max);
      });
   }

   public static VaultGoalMessage killBossGoal() {
      ITextComponent text = new StringTextComponent("Boss Battle").func_240699_a_(TextFormatting.BOLD).func_240699_a_(TextFormatting.GREEN);
      return composeMessage(
         new VaultGoalMessage(), VaultGoalMessage.VaultGoal.OBELISK_MESSAGE, payload -> payload.func_74778_a("Message", Serializer.func_150696_a(text))
      );
   }

   public static VaultGoalMessage scavengerHunt(List<ScavengerHuntObjective.ItemSubmission> activeSubmissions) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.SCAVENGER_GOAL, payload -> {
         ListNBT list = new ListNBT();

         for (ScavengerHuntObjective.ItemSubmission submission : activeSubmissions) {
            list.add(submission.serialize());
         }

         payload.func_218657_a("scavengerItems", list);
      });
   }

   public static VaultGoalMessage architectEvent(
      float completedPercent, int ticksUntilNextVote, int totalTicksUntilNextVote, @Nullable VotingSession activeVotingSession
   ) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.ARCHITECT_GOAL, payload -> {
         payload.func_74776_a("completedPercent", completedPercent);
         payload.func_74768_a("ticksUntilNextVote", ticksUntilNextVote);
         payload.func_74768_a("totalTicksUntilNextVote", totalTicksUntilNextVote);
         if (activeVotingSession != null) {
            payload.func_218657_a("votingSession", activeVotingSession.serialize());
         }
      });
   }

   public static VaultGoalMessage ancientsHunt(int totalAncients, int foundAncients) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.ANCIENTS_GOAL, payload -> {
         payload.func_74768_a("total", totalAncients);
         payload.func_74768_a("found", foundAncients);
      });
   }

   public static VaultGoalMessage clear() {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.CLEAR, payload -> {});
   }

   public static enum VaultGoal {
      OBELISK_GOAL,
      OBELISK_MESSAGE,
      SCAVENGER_GOAL,
      ARCHITECT_GOAL,
      ANCIENTS_GOAL,
      CLEAR;
   }
}
