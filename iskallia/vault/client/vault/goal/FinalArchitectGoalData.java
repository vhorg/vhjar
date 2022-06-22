package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.client.gui.overlay.goal.FinalArchitectBossGoalOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.world.vault.logic.objective.architect.VotingSession;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;

public class FinalArchitectGoalData extends VaultGoalData {
   private ITextComponent message = null;
   private int killedBosses = 0;
   private int totalKilledBossesNeeded = 0;
   private int knowledge = 0;
   private int totalKnowledgeNeeded = 0;
   private VotingSession activeSession = null;

   @Override
   public void receive(VaultGoalMessage pkt) {
      CompoundNBT tag = pkt.payload;
      this.message = Serializer.func_240643_a_(tag.func_74779_i("message"));
      this.killedBosses = tag.func_74762_e("killedBosses");
      this.totalKilledBossesNeeded = tag.func_74762_e("totalBosses");
      this.knowledge = tag.func_74762_e("knowledge");
      this.totalKnowledgeNeeded = tag.func_74762_e("totalKnowledge");
      if (tag.func_150297_b("votingSession", 10)) {
         this.activeSession = VotingSession.deserialize(tag.func_74775_l("votingSession"));
      }
   }

   @Nullable
   @Override
   public BossBarOverlay getBossBarOverlay() {
      return new FinalArchitectBossGoalOverlay(this);
   }

   @Nullable
   public VotingSession getActiveSession() {
      return this.activeSession;
   }

   public ITextComponent getMessage() {
      return this.message;
   }

   public int getKilledBosses() {
      return this.killedBosses;
   }

   public int getTotalKilledBossesNeeded() {
      return this.totalKilledBossesNeeded;
   }

   public int getKnowledge() {
      return this.knowledge;
   }

   public int getTotalKnowledgeNeeded() {
      return this.totalKnowledgeNeeded;
   }
}
