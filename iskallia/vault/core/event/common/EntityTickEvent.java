package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class EntityTickEvent extends ForgeEvent<EntityTickEvent, LivingUpdateEvent> {
   public EntityTickEvent() {
   }

   protected EntityTickEvent(EntityTickEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public EntityTickEvent createChild() {
      return new EntityTickEvent(this);
   }
}
