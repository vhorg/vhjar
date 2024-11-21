package iskallia.vault.integration;

import iskallia.vault.mixin.AccessorServerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class IntegrationAlexMobs {
   @SubscribeEvent
   public static void onUnload(Unload event) {
      if (event.getWorld() instanceof ServerLevel sLevel) {
         AccessorServerEvents.getBeachedWhaleMap().remove(sLevel);
      }
   }
}
