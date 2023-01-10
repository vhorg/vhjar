package iskallia.vault.event;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.gui.screen.AbilitySelectionScreen;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityQuickselectMessage;
import iskallia.vault.network.message.ServerboundAbilityKeyMessage;
import iskallia.vault.network.message.ServerboundOpenStatisticsMessage;
import iskallia.vault.network.message.ServerboundPickaxeOffsetKeyMessage;
import iskallia.vault.network.message.bounty.ServerboundBountyProgressMessage;
import iskallia.vault.skill.ability.KeyBehavior;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.skill.ability.group.AbilityGroup;
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
      if (key != -1) {
         if (minecraft.screen != null) {
            AbilityGroup<?, ?> selectedAbility = ClientAbilityData.getSelectedAbility();
            if (selectedAbility != null) {
               AbstractAbility<?> ability = selectedAbility.getAbility(null);
               if (ability != null && ability.getKeyBehavior() == KeyBehavior.ACTIVATE_ON_HOLD && isKey(ModKeybinds.abilityKey, key) && action == 0) {
                  ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyUp);
               }
            }
         } else {
            if (isKey(ModKeybinds.abilityKey, key)) {
               if (action == 0) {
                  ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyUp);
               } else if (action == 1) {
                  ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyDown);
               }
            }

            if (action == 1) {
               if (isShiftDown) {
                  if (key == 263) {
                     ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.LEFT);
                     return;
                  }

                  if (key == 262) {
                     ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.RIGHT);
                     return;
                  }

                  if (key == 265) {
                     ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.UP);
                     return;
                  }

                  if (key == 264) {
                     ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.DOWN);
                     return;
                  }
               }

               for (Entry<String, KeyMapping> entry : ModKeybinds.abilityQuickfireKey.entrySet()) {
                  if (isKey(entry.getValue(), key)) {
                     ModNetwork.CHANNEL.sendToServer(new AbilityQuickselectMessage(entry.getKey()));
                     return;
                  }
               }

               if (isKey(ModKeybinds.abilityWheelKey, key)) {
                  minecraft.setScreen(new AbilitySelectionScreen());
                  ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.CancelKeyDown);
               } else if (isKey(ModKeybinds.openAbilityTree, key)) {
                  ModNetwork.CHANNEL.sendToServer(ServerboundOpenStatisticsMessage.INSTANCE);
               } else if (isKey(ModKeybinds.bountyStatusKey, key)) {
                  ModNetwork.CHANNEL.sendToServer(ServerboundBountyProgressMessage.INSTANCE);
               }
            }
         }
      }
   }

   private static boolean isKey(KeyMapping keyMapping, int key) {
      return keyMapping.getKey().getValue() == key && keyMapping.isConflictContextAndModifierActive();
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
