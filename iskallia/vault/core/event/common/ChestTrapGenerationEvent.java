package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.util.WeightedList;
import net.minecraft.server.level.ServerPlayer;

public class ChestTrapGenerationEvent extends Event<ChestTrapGenerationEvent, ChestTrapGenerationEvent.Data> {
   public ChestTrapGenerationEvent() {
   }

   protected ChestTrapGenerationEvent(ChestTrapGenerationEvent parent) {
      super(parent);
   }

   public ChestTrapGenerationEvent createChild() {
      return new ChestTrapGenerationEvent(this);
   }

   public ChestTrapGenerationEvent.Data invoke(ServerPlayer player, double probability, WeightedList<String> pool) {
      return this.invoke(new ChestTrapGenerationEvent.Data(player, probability, pool));
   }

   public static class Data {
      private final ServerPlayer player;
      private double probability;
      private final WeightedList<String> pool;

      public Data(ServerPlayer player, double probability, WeightedList<String> pool) {
         this.player = player;
         this.probability = probability;
         this.pool = pool;
      }

      public ServerPlayer getPlayer() {
         return this.player;
      }

      public double getProbability() {
         return this.probability;
      }

      public WeightedList<String> getPool() {
         return this.pool;
      }

      public void setProbability(double probability) {
         this.probability = probability;
      }
   }
}
