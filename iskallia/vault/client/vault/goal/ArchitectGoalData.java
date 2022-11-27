package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.ArchitectGoalVoteOverlay;
import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.world.vault.logic.objective.architect.VotingSession;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class ArchitectGoalData extends VaultGoalData {
   private float completedPercent = 0.0F;
   private int ticksUntilNextVote = 0;
   private int totalTicksUntilNextVote = 0;
   private VotingSession activeSession = null;

   @Override
   public void receive(VaultGoalMessage pkt) {
      CompoundTag tag = pkt.payload;
      this.completedPercent = tag.getFloat("completedPercent");
      this.ticksUntilNextVote = tag.getInt("ticksUntilNextVote");
      this.totalTicksUntilNextVote = tag.getInt("totalTicksUntilNextVote");
      if (tag.contains("votingSession", 10)) {
         this.activeSession = VotingSession.deserialize(tag.getCompound("votingSession"));
      }
   }

   @Nullable
   @Override
   public BossBarOverlay getBossBarOverlay() {
      return new ArchitectGoalVoteOverlay(this);
   }

   @Nullable
   public VotingSession getActiveSession() {
      return this.activeSession;
   }

   public float getCompletedPercent() {
      return this.completedPercent;
   }

   public int getTicksUntilNextVote() {
      return this.ticksUntilNextVote;
   }

   public int getTotalTicksUntilNextVote() {
      return this.totalTicksUntilNextVote;
   }
}
