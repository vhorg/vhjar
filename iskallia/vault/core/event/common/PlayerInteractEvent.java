package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class PlayerInteractEvent extends ForgeEvent<PlayerInteractEvent, net.minecraftforge.event.entity.player.PlayerInteractEvent> {
   public PlayerInteractEvent() {
   }

   protected PlayerInteractEvent(PlayerInteractEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public PlayerInteractEvent createChild() {
      return new PlayerInteractEvent(this);
   }
}
