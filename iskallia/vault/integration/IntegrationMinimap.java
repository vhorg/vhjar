package iskallia.vault.integration;

import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.init.ModGameRules;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
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
         if (ClientVaults.getActive().isPresent() && !player.getLevel().getGameRules().getBoolean(ModGameRules.ALLOW_WAYPOINTS)) {
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
                  if (ClientVaults.getActive().isPresent()) {
                     int zoom = (Integer)settings.getOptionValue(ModOptions.ZOOM);
                     if (zoom < 2) {
                        try {
                           settings.setOptionValue(ModOptions.ZOOM, 2);
                        } catch (NullPointerException var8) {
                        }
                     }

                     if ((Boolean)settings.getOptionValue(ModOptions.COORDS)) {
                        settings.setOptionValue(ModOptions.COORDS, false);
                     }

                     double size = settings.getOptionDoubleValue(ModOptions.SIZE);
                     int defaultSize = getDefaultMinimapSize(settings);
                     if (size > defaultSize) {
                        settings.setOptionDoubleValue(ModOptions.SIZE, 0.0);
                     }
                  } else if (!(Boolean)settings.getOptionValue(ModOptions.COORDS)) {
                     settings.setOptionValue(ModOptions.COORDS, true);
                  }
               }
            });
         }
      }
   }

   public static Optional<ModSettings> getMinimapSettings() {
      return ModList.get()
         .getModObjectById("xaerominimap")
         .filter(mod -> mod instanceof XaeroMinimap)
         .map(mod -> (XaeroMinimap)mod)
         .map(XaeroMinimap::getSettings);
   }

   public static int getDefaultMinimapSize(ModSettings settings) {
      int height = Minecraft.getInstance().getWindow().getHeight();
      int width = Minecraft.getInstance().getWindow().getWidth();
      int settingsSize = Mth.floor(Math.min(height, width) / settings.getMinimapScale());
      return Math.min(
         Math.max((int)(ModOptions.SIZE.getValueMin() + ModOptions.SIZE.getValueStep()), 2 * settingsSize * 130 / 1080), (int)ModOptions.SIZE.getValueMax()
      );
   }
}
