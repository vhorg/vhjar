package iskallia.vault.network.message;

import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.base.OpcodeMessage;
import iskallia.vault.world.vault.logic.objective.LegacyScavengerHuntObjective;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import iskallia.vault.world.vault.logic.objective.VaultModifierVotingSession;
import iskallia.vault.world.vault.logic.objective.architect.VotingSession;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultGoalMessage extends OpcodeMessage<VaultGoalMessage.VaultGoal> {
   private VaultGoalMessage() {
   }

   public static void encode(VaultGoalMessage message, FriendlyByteBuf buffer) {
      message.encodeSelf(message, buffer);
   }

   public static VaultGoalMessage decode(FriendlyByteBuf buffer) {
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
      Component text = new TextComponent("Obelisks").withStyle(ChatFormatting.BOLD);
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.OBELISK_GOAL, payload -> {
         payload.putString("Message", Serializer.toJson(text));
         payload.putInt("TouchedObelisks", touched);
         payload.putInt("MaxObelisks", max);
      });
   }

   public static VaultGoalMessage killBossGoal() {
      Component text = new TextComponent("Boss Battle").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN);
      return composeMessage(
         new VaultGoalMessage(), VaultGoalMessage.VaultGoal.OBELISK_MESSAGE, payload -> payload.putString("Message", Serializer.toJson(text))
      );
   }

   public static VaultGoalMessage scavengerHunt(List<LegacyScavengerHuntObjective.ItemSubmission> activeSubmissions) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.SCAVENGER_GOAL, payload -> {
         ListTag list = new ListTag();

         for (LegacyScavengerHuntObjective.ItemSubmission submission : activeSubmissions) {
            list.add(submission.serialize());
         }

         payload.put("scavengerItems", list);
      });
   }

   public static VaultGoalMessage treasureHunt(List<TreasureHuntObjective.ItemSubmission> activeSubmissions) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.SCAVENGER_GOAL, payload -> {
         ListTag list = new ListTag();

         for (TreasureHuntObjective.ItemSubmission submission : activeSubmissions) {
            list.add(submission.serialize());
         }

         payload.put("scavengerItems", list);
      });
   }

   public static VaultGoalMessage architectEvent(
      float completedPercent, int ticksUntilNextVote, int totalTicksUntilNextVote, @Nullable VotingSession activeVotingSession
   ) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.ARCHITECT_GOAL, payload -> {
         payload.putFloat("completedPercent", completedPercent);
         payload.putInt("ticksUntilNextVote", ticksUntilNextVote);
         payload.putInt("totalTicksUntilNextVote", totalTicksUntilNextVote);
         if (activeVotingSession != null) {
            payload.put("votingSession", activeVotingSession.serialize());
         }
      });
   }

   public static VaultGoalMessage ancientsHunt(int totalAncients, int foundAncients) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.ANCIENTS_GOAL, payload -> {
         payload.putInt("total", totalAncients);
         payload.putInt("found", foundAncients);
      });
   }

   public static VaultGoalMessage raidChallenge(
      int wave,
      int totalWaves,
      int aliveMobs,
      int totalMobs,
      int tickWaveDelay,
      int completed,
      int target,
      List<Component> positiveModifiers,
      List<Component> negativeModifiers,
      VaultModifierVotingSession modifierVotingSession
   ) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.RAID_GOAL, payload -> {
         payload.putInt("wave", wave);
         payload.putInt("totalWaves", totalWaves);
         payload.putInt("aliveMobs", aliveMobs);
         payload.putInt("totalMobs", totalMobs);
         payload.putInt("tickWaveDelay", tickWaveDelay);
         payload.putInt("completedRaids", completed);
         payload.putInt("targetRaids", target);
         ListTag positives = new ListTag();
         positiveModifiers.forEach(modifier -> positives.add(StringTag.valueOf(Serializer.toJson(modifier))));
         payload.put("positives", positives);
         ListTag negatives = new ListTag();
         negativeModifiers.forEach(modifier -> negatives.add(StringTag.valueOf(Serializer.toJson(modifier))));
         payload.put("negatives", negatives);
         if (modifierVotingSession != null) {
            payload.put("votingSession", modifierVotingSession.serialize());
         }
      });
   }

   public static VaultGoalMessage cakeHunt(int totalCakes, int foundCakes) {
      return composeMessage(new VaultGoalMessage(), VaultGoalMessage.VaultGoal.CAKE_HUNT_GOAL, payload -> {
         payload.putInt("total", totalCakes);
         payload.putInt("found", foundCakes);
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
      RAID_GOAL,
      CAKE_HUNT_GOAL,
      CLEAR;
   }
}
