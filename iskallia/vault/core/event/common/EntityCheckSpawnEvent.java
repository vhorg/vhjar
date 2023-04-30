package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityCheckSpawnEvent extends ForgeEvent<EntityCheckSpawnEvent, CheckSpawn> {
   public EntityCheckSpawnEvent() {
   }

   protected EntityCheckSpawnEvent(EntityCheckSpawnEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityCheckSpawnEvent createChild() {
      return new EntityCheckSpawnEvent(this);
   }
}
