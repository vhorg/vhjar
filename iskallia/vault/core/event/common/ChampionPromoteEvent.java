package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.world.entity.LivingEntity;

public class ChampionPromoteEvent extends Event<ChampionPromoteEvent, ChampionPromoteEvent.Data> {
   public ChampionPromoteEvent() {
   }

   protected ChampionPromoteEvent(ChampionPromoteEvent parent) {
      super(parent);
   }

   public ChampionPromoteEvent createChild() {
      return new ChampionPromoteEvent(this);
   }

   public ChampionPromoteEvent.Data invoke(LivingEntity entity, double probability) {
      return this.invoke(new ChampionPromoteEvent.Data(entity, probability));
   }

   public static class Data {
      private final LivingEntity entity;
      private double probability;

      public Data(LivingEntity entity, double probability) {
         this.entity = entity;
         this.probability = probability;
      }

      public LivingEntity getEntity() {
         return this.entity;
      }

      public double getProbability() {
         return this.probability;
      }

      public void setProbability(double probability) {
         this.probability = probability;
      }
   }
}
