package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityDeathEvent extends ForgeEvent<EntityDeathEvent, LivingDeathEvent> {
   public EntityDeathEvent() {
   }

   protected EntityDeathEvent(EntityDeathEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityDeathEvent createChild() {
      return new EntityDeathEvent(this);
   }
}
