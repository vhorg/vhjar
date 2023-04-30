package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityHealEvent extends ForgeEvent<EntityHealEvent, LivingHealEvent> {
   public EntityHealEvent() {
   }

   protected EntityHealEvent(EntityHealEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public EntityHealEvent createChild() {
      return new EntityHealEvent(this);
   }
}
