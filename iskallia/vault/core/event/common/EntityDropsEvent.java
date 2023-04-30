package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityDropsEvent extends ForgeEvent<EntityDropsEvent, LivingDropsEvent> {
   public EntityDropsEvent() {
   }

   protected EntityDropsEvent(EntityDropsEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityDropsEvent createChild() {
      return new EntityDropsEvent(this);
   }
}
