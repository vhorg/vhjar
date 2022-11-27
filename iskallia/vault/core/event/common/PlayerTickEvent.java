package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;

public class PlayerTickEvent extends ForgeEvent<PlayerTickEvent, net.minecraftforge.event.TickEvent.PlayerTickEvent> {
   public PlayerTickEvent() {
   }

   protected PlayerTickEvent(PlayerTickEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public PlayerTickEvent createChild() {
      return new PlayerTickEvent(this);
   }

   public PlayerTickEvent at(Phase phase) {
      return this.filter(data -> data.phase == phase);
   }
}
