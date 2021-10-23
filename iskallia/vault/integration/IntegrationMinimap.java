package iskallia.vault.integration;

import iskallia.vault.Vault;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber({Dist.CLIENT})
public class IntegrationMinimap {
   private static boolean initialized = false;
   private static Consumer<Integer> setZoomSetting;
   private static Consumer<Boolean> setShowItems;

   public static void overrideItemCheck() {
   }

   private static void makeAccessible(Field f) throws Exception {
      if (Modifier.isFinal(f.getModifiers())) {
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         if (!modifiersField.isAccessible()) {
            modifiersField.setAccessible(true);
         }

         modifiersField.setInt(f, f.getModifiers() & -17);
      }
   }

   private static void initialize() {
      if (!initialized) {
         try {
            setupConfigAccessors();
         } catch (Exception var1) {
            var1.printStackTrace();
            return;
         }

         initialized = true;
      }
   }

   private static void setupConfigAccessors() throws Exception {
      Object minimapInstance = ModList.get().getModObjectById("xaerominimap").orElseThrow(IllegalStateException::new);
      Object minimapSettings = minimapInstance.getClass().getMethod("getSettings").invoke(minimapInstance);
      Class<?> modSettingsClass = Class.forName("xaero.common.settings.ModSettings");
      Field fModSettingsZoom = modSettingsClass.getDeclaredField("zoom");
      fModSettingsZoom.setAccessible(true);
      setZoomSetting = val -> {
         try {
            fModSettingsZoom.setInt(minimapSettings, val);
         } catch (IllegalAccessException var4x) {
            var4x.printStackTrace();
         }
      };
      setZoomSetting.accept(fModSettingsZoom.getInt(minimapSettings));
      Field fModSettingsShowItems = modSettingsClass.getDeclaredField("showItems");
      fModSettingsShowItems.setAccessible(true);
      setShowItems = val -> {
         try {
            fModSettingsShowItems.setBoolean(minimapSettings, val);
         } catch (IllegalAccessException var4x) {
            var4x.printStackTrace();
         }
      };
      setShowItems.accept(fModSettingsShowItems.getBoolean(minimapSettings));
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (ModList.get().isLoaded("xaerominimap")) {
         if (!initialized) {
            initialize();
         } else if (event.phase != Phase.END) {
            World world = Minecraft.func_71410_x().field_71441_e;
            if (world != null && world.func_234923_W_() == Vault.VAULT_KEY) {
               setZoomSetting.accept(4);
               setShowItems.accept(false);
            }
         }
      }
   }
}
