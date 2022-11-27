package iskallia.vault.core.event.client;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;

public class ClientTickEvent extends ForgeEvent<ClientTickEvent, net.minecraftforge.event.TickEvent.ClientTickEvent> {
   public ClientTickEvent() {
   }

   protected ClientTickEvent(ClientTickEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public ClientTickEvent createChild() {
      return new ClientTickEvent(this);
   }

   public ClientTickEvent at(Phase phase) {
      return this.filter(data -> data.phase == phase);
   }
}
