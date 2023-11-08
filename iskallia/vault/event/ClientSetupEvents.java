package iskallia.vault.event;

import iskallia.vault.client.ClientSandEventData;
import iskallia.vault.init.ModAbilityLabelBindings;
import iskallia.vault.init.ModEntityRenderers;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModScreens;
import iskallia.vault.init.ModTooltips;
import iskallia.vault.integration.curios.CuriosCharmCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetupEvents {
   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void setupClient(FMLClientSetupEvent event) {
      ModScreens.register();
      ModScreens.registerOverlayEvents();
      ModScreens.registerOverlays();
      ModKeybinds.register(event);
      ModEntityRenderers.register(event);
      MinecraftForge.EVENT_BUS.register(InputEvents.class);
      ClientSandEventData.getInstance().init();
      ModTooltips.register(event);
      ModAbilityLabelBindings.register();
      CuriosCharmCompat.register();
   }
}
