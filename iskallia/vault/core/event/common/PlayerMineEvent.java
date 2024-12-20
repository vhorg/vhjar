package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class PlayerMineEvent extends ForgeEvent<PlayerMineEvent, BreakEvent> {
   public PlayerMineEvent() {
   }

   protected PlayerMineEvent(PlayerMineEvent parent) {
      super(parent);
   }

   @Override
   protected void initialize() {
      for (EventPriority priority : EventPriority.values()) {
         MinecraftForge.EVENT_BUS.addListener(priority, true, event -> this.invoke(event));
      }
   }

   public PlayerMineEvent createChild() {
      return new PlayerMineEvent(this);
   }
}
