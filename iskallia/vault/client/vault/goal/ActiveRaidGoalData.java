package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.ActiveRaidOverlay;
import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.world.vault.logic.objective.VaultModifierVotingSession;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class ActiveRaidGoalData extends VaultGoalData {
   private int wave;
   private int totalWaves;
   private int aliveMobs;
   private int totalMobs;
   private int tickWaveDelay;
   private int raidsCompleted;
   private int targetRaids;
   private List<Component> positives = new ArrayList<>();
   private List<Component> negatives = new ArrayList<>();
   private VaultModifierVotingSession session;

   @Nullable
   @Override
   public BossBarOverlay getBossBarOverlay() {
      return new ActiveRaidOverlay(this);
   }

   public int getWave() {
      return this.wave;
   }

   public int getTotalWaves() {
      return this.totalWaves;
   }

   public int getAliveMobs() {
      return this.aliveMobs;
   }

   public int getTotalMobs() {
      return this.totalMobs;
   }

   public int getTickWaveDelay() {
      return this.tickWaveDelay;
   }

   public int getRaidsCompleted() {
      return this.raidsCompleted;
   }

   public int getTargetRaids() {
      return this.targetRaids;
   }

   public List<Component> getPositives() {
      return this.positives;
   }

   public List<Component> getNegatives() {
      return this.negatives;
   }

   @Nullable
   public VaultModifierVotingSession getVotingSession() {
      return this.session;
   }

   @Override
   public void receive(VaultGoalMessage pkt) {
      CompoundTag tag = pkt.payload;
      this.wave = tag.getInt("wave");
      this.totalWaves = tag.getInt("totalWaves");
      this.aliveMobs = tag.getInt("aliveMobs");
      this.totalMobs = tag.getInt("totalMobs");
      this.tickWaveDelay = tag.getInt("tickWaveDelay");
      this.raidsCompleted = tag.getInt("completedRaids");
      this.targetRaids = tag.getInt("targetRaids");
      ListTag positives = tag.getList("positives", 8);
      this.positives = new ArrayList<>();

      for (int i = 0; i < positives.size(); i++) {
         this.positives.add(Serializer.fromJson(positives.getString(i)));
      }

      ListTag negatives = tag.getList("negatives", 8);
      this.negatives = new ArrayList<>();

      for (int i = 0; i < negatives.size(); i++) {
         this.negatives.add(Serializer.fromJson(negatives.getString(i)));
      }

      if (tag.contains("votingSession", 10)) {
         this.session = VaultModifierVotingSession.deserialize(tag.getCompound("votingSession"));
      }
   }
}
