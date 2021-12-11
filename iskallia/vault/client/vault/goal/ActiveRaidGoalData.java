package iskallia.vault.client.vault.goal;

import iskallia.vault.client.gui.overlay.goal.ActiveRaidOverlay;
import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.network.message.VaultGoalMessage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;

public class ActiveRaidGoalData extends VaultGoalData {
   private int wave;
   private int totalWaves;
   private int aliveMobs;
   private int totalMobs;
   private int tickWaveDelay;
   private int raidsCompleted;
   private List<ITextComponent> positives = new ArrayList<>();
   private List<ITextComponent> negatives = new ArrayList<>();

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

   public List<ITextComponent> getPositives() {
      return this.positives;
   }

   public List<ITextComponent> getNegatives() {
      return this.negatives;
   }

   @Override
   public void receive(VaultGoalMessage pkt) {
      CompoundNBT tag = pkt.payload;
      this.wave = tag.func_74762_e("wave");
      this.totalWaves = tag.func_74762_e("totalWaves");
      this.aliveMobs = tag.func_74762_e("aliveMobs");
      this.totalMobs = tag.func_74762_e("totalMobs");
      this.tickWaveDelay = tag.func_74762_e("tickWaveDelay");
      this.raidsCompleted = tag.func_74762_e("completedRaids");
      ListNBT positives = tag.func_150295_c("positives", 8);
      this.positives = new ArrayList<>();

      for (int i = 0; i < positives.size(); i++) {
         this.positives.add(Serializer.func_240643_a_(positives.func_150307_f(i)));
      }

      ListNBT negatives = tag.func_150295_c("negatives", 8);
      this.negatives = new ArrayList<>();

      for (int i = 0; i < negatives.size(); i++) {
         this.negatives.add(Serializer.func_240643_a_(negatives.func_150307_f(i)));
      }
   }
}
