package iskallia.vault.event;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.crystal.recipe.AnvilRecipes;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class SetupEvents {
   @SubscribeEvent
   public static void setupCommon(FMLCommonSetupEvent event) {
      ModConfigs.register();
      ModConfigs.registerGen();
      ModNetwork.initialize();
      ModRecipes.initialize();
      ModGameRules.initialize();
      AnvilRecipes.register();
   }

   @SubscribeEvent
   public static void setupDedicatedServer(FMLDedicatedServerSetupEvent event) {
   }

   @SubscribeEvent
   static void registerLayers(RegisterLayerDefinitions event) {
      ModEntities.Models.registerLayers(event);
   }

   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      ModEntities.registerAttributes(event);
   }
}
