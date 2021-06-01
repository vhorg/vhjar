package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class SetupEvents {
   @SubscribeEvent
   public static void setupClient(FMLClientSetupEvent event) {
      Vault.LOGGER.info("setupClient()");
      ModScreens.register(event);
      ModScreens.registerOverlays();
      ModKeybinds.register(event);
      ModEntities.Renderers.register(event);
      MinecraftForge.EVENT_BUS.register(InputEvents.class);
      ModBlocks.registerTileEntityRenderers();
   }

   @SubscribeEvent
   public static void setupCommon(FMLCommonSetupEvent event) {
      Vault.LOGGER.info("setupCommon()");
      ModConfigs.register();
      ModNetwork.initialize();
   }

   @SubscribeEvent
   public static void setupDedicatedServer(FMLDedicatedServerSetupEvent event) {
      Vault.LOGGER.info("setupDedicatedServer()");
   }
}
