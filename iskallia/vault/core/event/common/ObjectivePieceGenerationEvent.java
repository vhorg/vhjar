package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;

public class ObjectivePieceGenerationEvent extends Event<ObjectivePieceGenerationEvent, ObjectivePieceGenerationEvent.Data> {
   public ObjectivePieceGenerationEvent() {
   }

   protected ObjectivePieceGenerationEvent(ObjectivePieceGenerationEvent parent) {
      super(parent);
   }

   public ObjectivePieceGenerationEvent createChild() {
      return new ObjectivePieceGenerationEvent(this);
   }

   public ObjectivePieceGenerationEvent.Data invoke(Vault vault, double probability) {
      return this.invoke(new ObjectivePieceGenerationEvent.Data(vault, probability));
   }

   public static class Data {
      private final Vault vault;
      private double probability;

      public Data(Vault vault, double probability) {
         this.vault = vault;
         this.probability = probability;
      }

      public Vault getVault() {
         return this.vault;
      }

      public double getProbability() {
         return this.probability;
      }

      public void setProbability(double probability) {
         this.probability = probability;
      }
   }
}
