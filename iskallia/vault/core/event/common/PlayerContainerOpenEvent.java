package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.eventbus.api.EventPriority;

public class PlayerContainerOpenEvent extends ForgeEvent<PlayerContainerOpenEvent, Open> {
   public PlayerContainerOpenEvent() {
   }

   protected PlayerContainerOpenEvent(PlayerContainerOpenEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public PlayerContainerOpenEvent createChild() {
      return new PlayerContainerOpenEvent(this);
   }
}
