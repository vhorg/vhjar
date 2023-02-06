package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.world.entity.LivingEntity;

public class EntityStunnedEvent extends Event<EntityStunnedEvent, EntityStunnedEvent.Data> {
   public EntityStunnedEvent() {
   }

   protected EntityStunnedEvent(EntityStunnedEvent parent) {
      super(parent);
   }

   public EntityStunnedEvent createChild() {
      return new EntityStunnedEvent(this);
   }

   public static class Data {
      private final LivingEntity source;
      private final LivingEntity target;

      public Data(LivingEntity source, LivingEntity target) {
         this.source = source;
         this.target = target;
      }

      public LivingEntity getSource() {
         return this.source;
      }

      public LivingEntity getTarget() {
         return this.target;
      }
   }
}
