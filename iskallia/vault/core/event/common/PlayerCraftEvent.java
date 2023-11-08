package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class PlayerCraftEvent extends ForgeEvent<PlayerCraftEvent, ItemCraftedEvent> {
   public PlayerCraftEvent() {
   }

   protected PlayerCraftEvent(PlayerCraftEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public PlayerCraftEvent createChild() {
      return new PlayerCraftEvent(this);
   }
}
