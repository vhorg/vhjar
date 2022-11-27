package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;

public class PlayerContainerCloseEvent extends ForgeEvent<PlayerContainerCloseEvent, Close> {
   public PlayerContainerCloseEvent() {
   }

   protected PlayerContainerCloseEvent(PlayerContainerCloseEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public PlayerContainerCloseEvent createChild() {
      return new PlayerContainerCloseEvent(this);
   }
}
