package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityHurtEvent extends ForgeEvent<EntityHurtEvent, LivingHurtEvent> {
   public EntityHurtEvent() {
   }

   protected EntityHurtEvent(EntityHurtEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityHurtEvent createChild() {
      return new EntityHurtEvent(this);
   }
}
