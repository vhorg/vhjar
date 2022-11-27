package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;

public class ServerTickEvent extends ForgeEvent<ServerTickEvent, net.minecraftforge.event.TickEvent.ServerTickEvent> {
   public ServerTickEvent() {
   }

   protected ServerTickEvent(ServerTickEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public ServerTickEvent createChild() {
      return new ServerTickEvent(this);
   }

   public ServerTickEvent at(Phase phase) {
      return this.filter(data -> data.phase == phase);
   }
}
