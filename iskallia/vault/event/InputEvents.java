package iskallia.vault.event;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.screen.AbilitySelectionScreen;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityKeyMessage;
import iskallia.vault.network.message.OpenSkillTreeMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class InputEvents {
   private static boolean isShiftDown;

   public static boolean isShiftDown() {
      return isShiftDown;
   }

   @SubscribeEvent
   public static void onShiftKey(KeyInputEvent event) {
      if (event.getKey() == 340) {
         if (event.getAction() == 1) {
            isShiftDown = true;
         } else if (event.getAction() == 0) {
            isShiftDown = false;
         }
      }
   }

   @SubscribeEvent
   public static void onKey(KeyInputEvent event) {
      Minecraft minecraft = Minecraft.func_71410_x();
      if (minecraft.field_71441_e != null) {
         onInput(minecraft, event.getKey(), event.getAction());
      }
   }

   @SubscribeEvent
   public static void onMouse(MouseInputEvent event) {
      Minecraft minecraft = Minecraft.func_71410_x();
      if (minecraft.field_71441_e != null) {
         onInput(minecraft, event.getButton(), event.getAction());
      }
   }

   private static void onInput(Minecraft minecraft, int key, int action) {
      if (minecraft.field_71462_r == null && ModKeybinds.abilityWheelKey.getKey().func_197937_c() == key) {
         if (action != 1) {
            return;
         }

         if (AbilitiesOverlay.learnedAbilities.size() <= 2) {
            return;
         }

         minecraft.func_147108_a(new AbilitySelectionScreen());
         ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(true));
      } else if (minecraft.field_71462_r == null && ModKeybinds.openAbilityTree.func_151468_f()) {
         ModNetwork.CHANNEL.sendToServer(new OpenSkillTreeMessage());
      } else if (minecraft.field_71462_r == null && ModKeybinds.abilityKey.getKey().func_197937_c() == key) {
         if (action == 0) {
            ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(true, false, false, false));
         } else if (action == 1) {
            ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(false, true, false, false));
         }
      }
   }

   @SubscribeEvent
   public static void onMouseScroll(MouseScrollEvent event) {
      Minecraft minecraft = Minecraft.func_71410_x();
      if (minecraft.field_71441_e != null) {
         double scrollDelta = event.getScrollDelta();
         if (ModKeybinds.abilityKey.func_151470_d()) {
            if (minecraft.field_71462_r == null) {
               if (scrollDelta < 0.0) {
                  ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(false, false, false, true));
               } else {
                  ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(false, false, true, false));
               }
            }

            event.setCanceled(true);
         }
      }
   }
}
