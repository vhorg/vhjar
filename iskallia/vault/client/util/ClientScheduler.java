package iskallia.vault.client.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class ClientScheduler {
   public static final ClientScheduler INSTANCE = new ClientScheduler();
   private long tick = 0L;

   private ClientScheduler() {
   }

   public void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         if (!Minecraft.getInstance().isPaused()) {
            this.tick++;
         }
      }
   }

   public long getTick() {
      return this.tick;
   }
}
