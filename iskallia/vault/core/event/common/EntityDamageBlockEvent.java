package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class EntityDamageBlockEvent extends Event<EntityDamageBlockEvent, EntityDamageBlockEvent.Data> {
   public EntityDamageBlockEvent() {
   }

   protected EntityDamageBlockEvent(EntityDamageBlockEvent parent) {
      super(parent);
   }

   public EntityDamageBlockEvent createChild() {
      return new EntityDamageBlockEvent(this);
   }

   public EntityDamageBlockEvent blockSucceeded() {
      return this.filter(data -> !data.failed);
   }

   public EntityDamageBlockEvent blockFailed() {
      return this.filter(data -> data.failed);
   }

   public static class Data {
      private final boolean failed;
      private final DamageSource damageSource;
      private final LivingEntity attacked;

      public Data(boolean failed, DamageSource damageSource, LivingEntity attacked) {
         this.failed = failed;
         this.damageSource = damageSource;
         this.attacked = attacked;
      }

      public boolean isFailed() {
         return this.failed;
      }

      public DamageSource getDamageSource() {
         return this.damageSource;
      }

      public LivingEntity getAttacked() {
         return this.attacked;
      }
   }
}
