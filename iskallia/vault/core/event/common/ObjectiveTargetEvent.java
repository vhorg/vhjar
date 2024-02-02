package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;

public class ObjectiveTargetEvent extends Event<ObjectiveTargetEvent, ObjectiveTargetEvent.Data> {
   public ObjectiveTargetEvent() {
   }

   protected ObjectiveTargetEvent(ObjectiveTargetEvent parent) {
      super(parent);
   }

   public ObjectiveTargetEvent createChild() {
      return new ObjectiveTargetEvent(this);
   }

   public ObjectiveTargetEvent.Data invoke(VirtualWorld world, Vault vault, double increase) {
      return this.invoke(new ObjectiveTargetEvent.Data(world, vault, increase));
   }

   public static class Data {
      private final VirtualWorld world;
      private final Vault vault;
      private double increase;

      public Data(VirtualWorld world, Vault vault, double increase) {
         this.world = world;
         this.vault = vault;
         this.increase = increase;
      }

      public VirtualWorld getWorld() {
         return this.world;
      }

      public Vault getVault() {
         return this.vault;
      }

      public double getIncrease() {
         return this.increase;
      }

      public void setIncrease(double increase) {
         this.increase = increase;
      }
   }
}
