package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class LuckyAltarTalent extends PlayerTalent {
   @Expose
   private final float luckyAltarChance;

   public LuckyAltarTalent(int cost, float luckyAltarChance) {
      super(cost);
      this.luckyAltarChance = luckyAltarChance;
   }

   public float getLuckyAltarChance() {
      return this.luckyAltarChance;
   }
}
