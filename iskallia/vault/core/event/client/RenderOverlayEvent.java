package iskallia.vault.core.event.client;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class RenderOverlayEvent extends ForgeEvent<RenderOverlayEvent, Post> {
   public RenderOverlayEvent() {
   }

   protected RenderOverlayEvent(RenderOverlayEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public RenderOverlayEvent forType(ElementType type) {
      return this.filter(event -> event.getType() == type);
   }

   public RenderOverlayEvent createChild() {
      return new RenderOverlayEvent(this);
   }
}
