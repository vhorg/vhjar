package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;

public class EntityPlaceEvent extends ForgeEvent<EntityPlaceEvent, net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent> {
   public EntityPlaceEvent() {
   }

   protected EntityPlaceEvent(EntityPlaceEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public EntityPlaceEvent createChild() {
      return new EntityPlaceEvent(this);
   }
}
