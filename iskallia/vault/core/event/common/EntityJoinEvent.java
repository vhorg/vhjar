package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityJoinEvent extends ForgeEvent<EntityJoinEvent, EntityJoinWorldEvent> {
   public EntityJoinEvent() {
   }

   protected EntityJoinEvent(EntityJoinEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityJoinEvent createChild() {
      return new EntityJoinEvent(this);
   }
}
