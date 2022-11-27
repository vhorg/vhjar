package iskallia.vault.integration;

import iskallia.vault.init.ModGameRules;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.gui.GuiWaypoints;
import xaero.common.settings.ModOptions;
import xaero.common.settings.ModSettings;
import xaero.minimap.XaeroMinimap;

@OnlyIn(Dist.CLIENT)
public class IntegrationMinimap {
   @SubscribeEvent
   public static void onWaypointScreen(ScreenOpenEvent event) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         if (ServerVaults.isInVault(player) && !player.getLevel().getGameRules().getBoolean(ModGameRules.VAULT_ALLOW_WAYPOINTS)) {
            Screen screen = event.getScreen();
            if (screen instanceof GuiWaypoints || screen instanceof GuiAddWaypoint) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (ModList.get().isLoaded("xaerominimap")) {
         if (event.phase != Phase.END) {
            ModList.get().getModObjectById("xaerominimap").ifPresent(mod -> {
               if (mod instanceof XaeroMinimap minimap) {
                  Player player = Minecraft.getInstance().player;
                  if (player == null) {
                     return;
                  }

                  ModSettings settings = minimap.getSettings();
                  if (ServerVaults.isInVault(player)) {
                     int zoom = (Integer)settings.getOptionValue(ModOptions.ZOOM);
                     if (zoom < 2) {
                        try {
                           settings.setOptionValue(ModOptions.ZOOM, 2);
                        } catch (NullPointerException var6) {
                        }
                     }

                     settings.setOptionValue(ModOptions.COORDS, false);
                  } else {
                     settings.setOptionValue(ModOptions.COORDS, true);
                  }
               }
            });
         }
      }
   }
}
