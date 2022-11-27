package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.server.level.ServerPlayer;

public class ChestCatalystGenerationEvent extends Event<ChestCatalystGenerationEvent, ChestCatalystGenerationEvent.Data> {
   public ChestCatalystGenerationEvent() {
   }

   protected ChestCatalystGenerationEvent(ChestCatalystGenerationEvent parent) {
      super(parent);
   }

   public ChestCatalystGenerationEvent createChild() {
      return new ChestCatalystGenerationEvent(this);
   }

   public ChestCatalystGenerationEvent.Data invoke(ServerPlayer player, double probability) {
      return this.invoke(new ChestCatalystGenerationEvent.Data(player, probability));
   }

   public static class Data {
      private final ServerPlayer player;
      private double probability;

      public Data(ServerPlayer player, double probability) {
         this.player = player;
         this.probability = probability;
      }

      public ServerPlayer getPlayer() {
         return this.player;
      }

      public double getProbability() {
         return this.probability;
      }

      public void setProbability(double probability) {
         this.probability = probability;
      }
   }
}
