package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

public class EntityCreationEvent extends ForgeEvent<EntityCreationEvent, EntityConstructing> {
   public EntityCreationEvent() {
   }

   protected EntityCreationEvent(EntityCreationEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public EntityCreationEvent createChild() {
      return new EntityCreationEvent(this);
   }
}
