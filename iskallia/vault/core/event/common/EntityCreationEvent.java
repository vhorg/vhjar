package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityCreationEvent extends ForgeEvent<EntityCreationEvent, EntityConstructing> {
   public EntityCreationEvent() {
   }

   protected EntityCreationEvent(EntityCreationEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityCreationEvent createChild() {
      return new EntityCreationEvent(this);
   }
}
