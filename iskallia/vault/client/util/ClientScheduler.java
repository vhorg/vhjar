package iskallia.vault.client.util;

import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class ClientScheduler {
   public static final ClientScheduler INSTANCE = new ClientScheduler();
   private long tickCount = 0L;

   private ClientScheduler() {
   }

   public void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         this.tickCount++;
      }
   }

   public long getTickCount() {
      return this.tickCount;
   }
}
