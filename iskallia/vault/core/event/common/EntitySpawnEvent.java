package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntitySpawnEvent extends ForgeEvent<EntitySpawnEvent, LivingSpawnEvent> {
   public EntitySpawnEvent() {
   }

   protected EntitySpawnEvent(EntitySpawnEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntitySpawnEvent createChild() {
      return new EntitySpawnEvent(this);
   }
}
