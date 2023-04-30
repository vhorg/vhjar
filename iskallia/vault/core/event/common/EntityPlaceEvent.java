package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityPlaceEvent extends ForgeEvent<EntityPlaceEvent, net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent> {
   public EntityPlaceEvent() {
   }

   protected EntityPlaceEvent(EntityPlaceEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityPlaceEvent createChild() {
      return new EntityPlaceEvent(this);
   }
}
