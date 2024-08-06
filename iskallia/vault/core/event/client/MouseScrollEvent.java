package iskallia.vault.core.event.client;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class MouseScrollEvent extends ForgeEvent<MouseScrollEvent, net.minecraftforge.client.event.InputEvent.MouseScrollEvent> {
   public MouseScrollEvent() {
   }

   protected MouseScrollEvent(MouseScrollEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public MouseScrollEvent createChild() {
      return new MouseScrollEvent(this);
   }
}
