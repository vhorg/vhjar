package iskallia.vault.world.vault.event;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

public class VaultListener {
   public static final Map<VaultRaid, Void> REGISTRY = new WeakHashMap<>();

   public static void listen(VaultRaid vault) {
      REGISTRY.put(vault, null);
   }

   public static synchronized <T extends Event> void onEvent(T event) {
      if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
         try {
            REGISTRY.keySet().removeIf(VaultRaid::isFinished);
            REGISTRY.keySet().forEach(vault -> vault.getEvents().forEach(listener -> {
               if (event.getClass().isAssignableFrom(listener.getType())) {
                  listener.accept(vault, event);
               }
            }));
         } catch (Exception var2) {
            VaultMod.LOGGER.error("Upsie, you know what causes this but are lazy to fix it :(");
            var2.printStackTrace();
         }
      }
   }
}
