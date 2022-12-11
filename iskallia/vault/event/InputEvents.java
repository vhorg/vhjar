package iskallia.vault.event;

import iskallia.vault.client.gui.screen.AbilitySelectionScreen;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityQuickselectMessage;
import iskallia.vault.network.message.ServerboundAbilityKeyMessage;
import iskallia.vault.network.message.ServerboundOpenStatisticsMessage;
import iskallia.vault.network.message.bounty.ServerboundBountyProgressMessage;
import java.util.Map.Entry;
import net.minecraft.client.KeyMapping;
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
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         onInput(minecraft, event.getKey(), event.getAction());
      }
   }

   @SubscribeEvent
   public static void onMouse(MouseInputEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         onInput(minecraft, event.getButton(), event.getAction());
      }
   }

   private static void onInput(Minecraft minecraft, int key, int action) {
      if (minecraft.screen == null && key != -1) {
         for (Entry<String, KeyMapping> quickSelectKeybind : ModKeybinds.abilityQuickfireKey.entrySet()) {
            if (quickSelectKeybind.getValue().getKey().getValue() == key) {
               if (action != 1) {
                  return;
               }

               ModNetwork.CHANNEL.sendToServer(new AbilityQuickselectMessage(quickSelectKeybind.getKey()));
            }
         }

         if (ModKeybinds.abilityWheelKey.getKey().getValue() == key) {
            if (action != 1) {
               return;
            }

            minecraft.setScreen(new AbilitySelectionScreen());
            ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.CancelKeyDown);
         } else if (ModKeybinds.openAbilityTree.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(ServerboundOpenStatisticsMessage.INSTANCE);
         } else if (ModKeybinds.abilityKey.getKey().getValue() == key) {
            if (action == 0) {
               ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyUp);
            } else if (action == 1) {
               ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyDown);
            }
         } else if (ModKeybinds.bountyStatusKey.getKey().getValue() == key) {
            if (action != 1) {
               return;
            }

            ModNetwork.CHANNEL.sendToServer(new ServerboundBountyProgressMessage());
         }
      }
   }

   @SubscribeEvent
   public static void onMouseScroll(MouseScrollEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         double scrollDelta = event.getScrollDelta();
         if (ModKeybinds.abilityKey.isDown()) {
            if (minecraft.screen == null) {
               if (scrollDelta < 0.0) {
                  ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.ScrollDown);
               } else {
                  ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.ScrollUp);
               }
            }

            event.setCanceled(true);
         }
      }
   }
}
