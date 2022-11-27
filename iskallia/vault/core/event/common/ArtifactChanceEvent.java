package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.player.Listener;

public class ArtifactChanceEvent extends Event<ArtifactChanceEvent, ArtifactChanceEvent.Data> {
   public ArtifactChanceEvent() {
   }

   protected ArtifactChanceEvent(ArtifactChanceEvent parent) {
      super(parent);
   }

   public ArtifactChanceEvent createChild() {
      return new ArtifactChanceEvent(this);
   }

   public ArtifactChanceEvent.Data invoke(Listener listener, float probability) {
      return this.invoke(new ArtifactChanceEvent.Data(listener, probability));
   }

   public static class Data {
      private final Listener listener;
      private float probability;

      public Data(Listener listener, float probability) {
         this.listener = listener;
         this.probability = probability;
      }

      public Listener getListener() {
         return this.listener;
      }

      public float getProbability() {
         return this.probability;
      }

      public void setProbability(float chance) {
         this.probability = chance;
      }
   }
}
