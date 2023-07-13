package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;

public class ChampionPromoteEvent extends Event<ChampionPromoteEvent, ChampionPromoteEvent.Data> {
   public ChampionPromoteEvent() {
   }

   protected ChampionPromoteEvent(ChampionPromoteEvent parent) {
      super(parent);
   }

   public ChampionPromoteEvent createChild() {
      return new ChampionPromoteEvent(this);
   }

   public ChampionPromoteEvent.Data invoke(double probability) {
      return this.invoke(new ChampionPromoteEvent.Data(probability));
   }

   public static class Data {
      private double probability;

      public Data(double probability) {
         this.probability = probability;
      }

      public double getProbability() {
         return this.probability;
      }

      public void setProbability(double probability) {
         this.probability = probability;
      }
   }

   public static enum Phase {
      PRE,
      POST;
   }
}
