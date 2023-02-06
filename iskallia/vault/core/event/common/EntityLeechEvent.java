package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.world.entity.LivingEntity;

public class EntityLeechEvent extends Event<EntityLeechEvent, EntityLeechEvent.Data> {
   public EntityLeechEvent() {
   }

   protected EntityLeechEvent(EntityLeechEvent parent) {
      super(parent);
   }

   public EntityLeechEvent createChild() {
      return new EntityLeechEvent(this);
   }

   public static class Data {
      private final LivingEntity leecher;
      private final LivingEntity attacked;
      private final float amountLeeched;

      public Data(LivingEntity leecher, LivingEntity attacked, float amountLeeched) {
         this.leecher = leecher;
         this.attacked = attacked;
         this.amountLeeched = amountLeeched;
      }

      public LivingEntity getLeecher() {
         return this.leecher;
      }

      public LivingEntity getAttacked() {
         return this.attacked;
      }

      public float getAmountLeeched() {
         return this.amountLeeched;
      }
   }
}
