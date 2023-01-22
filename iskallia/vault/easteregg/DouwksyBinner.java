package iskallia.vault.easteregg;

import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class DouwksyBinner {
   @SubscribeEvent
   public static void onDouwskyBinned(ItemCraftedEvent event) {
      if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
         ItemStack result = event.getCrafting();
         Container ingredients = event.getInventory();
         ResourceLocation resultId = ForgeRegistries.ITEMS.getKey(result.getItem());
         if (resultId != null) {
            if (resultId.toString().equals("quark:grate")) {
               int chromaticNuggetCount = ingredients.countItem(ModItems.CHROMATIC_IRON_NUGGET);
               if (chromaticNuggetCount == 6) {
                  DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get(serverPlayer.getLevel());
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.AXE, ModDynamicModels.Axes.JANITORS_BROOM.getId(), serverPlayer);
                  discoveredModelsData.discoverModelAndBroadcast(ModItems.SHIELD, ModDynamicModels.Shields.ABSOLUTE_BINNER.getId(), serverPlayer);
               }
            }
         }
      }
   }
}
