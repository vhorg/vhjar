package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityLeaveEvent extends ForgeEvent<EntityLeaveEvent, EntityLeaveWorldEvent> {
   public EntityLeaveEvent() {
   }

   protected EntityLeaveEvent(EntityLeaveEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityLeaveEvent createChild() {
      return new EntityLeaveEvent(this);
   }
}
