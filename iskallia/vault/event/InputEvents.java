package iskallia.vault.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.gui.screen.AbilitySelectionScreen;
import iskallia.vault.client.gui.screen.achievements.AchievementScreen;
import iskallia.vault.client.gui.screen.bestiary.BestiaryScreen;
import iskallia.vault.client.gui.screen.quest.QuestOverviewElementScreen;
import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityQuickselectMessage;
import iskallia.vault.network.message.AngelToggleMessage;
import iskallia.vault.network.message.OpenCardDeckMessage;
import iskallia.vault.network.message.ServerboundAbilityKeyMessage;
import iskallia.vault.network.message.ServerboundMagnetToggleMessage;
import iskallia.vault.network.message.ServerboundOpenStatisticsMessage;
import iskallia.vault.network.message.ServerboundPickaxeOffsetKeyMessage;
import iskallia.vault.network.message.ToolMessage;
import iskallia.vault.network.message.bounty.ServerboundBountyProgressMessage;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.HoldAbility;
import java.util.HashSet;
import java.util.Set;
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
   private static boolean scrollingAbilities = false;
   private static final Set<Key> KEY_DOWN_SET = new HashSet<>();

   public static boolean isShiftDown() {
      return isShiftDown;
   }

   @SubscribeEvent
   public static void onShiftKey(KeyInputEvent event) {
      Key shiftKey = Minecraft.getInstance().options.keyShift.getKey();
      if (event.getKey() == shiftKey.getValue()) {
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
         onInput(minecraft, InputConstants.getKey(event.getKey(), event.getScanCode()), event.getAction());
      }
   }

   @SubscribeEvent
   public static void onMouse(MouseInputEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         onInput(minecraft, Type.MOUSE.getOrCreate(event.getButton()), event.getAction());
      }
   }

   private static void onInput(Minecraft minecraft, Key key, int action) {
      if (key != InputConstants.UNKNOWN) {
         if (action == 1) {
            KEY_DOWN_SET.add(key);
         } else if (action == 0) {
            KEY_DOWN_SET.remove(key);
         }

         if (minecraft.screen != null) {
            if (action == 0) {
               checkAndReleaseHoldAbility(key);
               scrollingAbilities = false;
            }
         } else if (action != 0) {
            if (action == 1) {
               for (Entry<String, KeyMapping> entry : ModKeybinds.abilityQuickfireKey.entrySet()) {
                  if (entry.getValue().isActiveAndMatches(key)) {
                     ModNetwork.CHANNEL.sendToServer(new AbilityQuickselectMessage(entry.getKey(), 1));
                     return;
                  }
               }

               if (isShiftDown && ToolMessage.Offset.isKey(key.getValue())) {
                  ToolMessage.sendOffset(key.getValue());
               } else {
                  if (isShiftDown) {
                     if (key.getValue() == 263) {
                        ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.LEFT);
                        return;
                     }

                     if (key.getValue() == 262) {
                        ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.RIGHT);
                        return;
                     }

                     if (key.getValue() == 265) {
                        ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.UP);
                        return;
                     }

                     if (key.getValue() == 264) {
                        ServerboundPickaxeOffsetKeyMessage.send(ServerboundPickaxeOffsetKeyMessage.Opcode.DOWN);
                        return;
                     }
                  }

                  if (ModKeybinds.abilityKey.isActiveAndMatches(key)) {
                     ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyDown);
                  } else if (ModKeybinds.abilityWheelKey.isActiveAndMatches(key)) {
                     minecraft.setScreen(new AbilitySelectionScreen());
                     ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.CancelKeyDown);
                  } else if (ModKeybinds.openAbilityTree.isActiveAndMatches(key)) {
                     ModNetwork.CHANNEL.sendToServer(ServerboundOpenStatisticsMessage.INSTANCE);
                  } else if (ModKeybinds.bountyStatusKey.isActiveAndMatches(key)) {
                     ModNetwork.CHANNEL.sendToServer(ServerboundBountyProgressMessage.INSTANCE);
                  } else if (ModKeybinds.angelToggleKey.isActiveAndMatches(key)) {
                     ModNetwork.CHANNEL.sendToServer(AngelToggleMessage.INSTANCE);
                  } else if (ModKeybinds.magnetToggleKey.isActiveAndMatches(key)) {
                     ModNetwork.CHANNEL.sendToServer(ServerboundMagnetToggleMessage.INSTANCE);
                  } else if (ModKeybinds.openQuestScreen.isActiveAndMatches(key)) {
                     Minecraft.getInstance().setScreen(new QuestOverviewElementScreen());
                  } else if (ModKeybinds.openBestiary.isActiveAndMatches(key)) {
                     Minecraft.getInstance().setScreen(new BestiaryScreen());
                  } else if (ModKeybinds.openAchievements.isActiveAndMatches(key)) {
                     Minecraft.getInstance().setScreen(new AchievementScreen());
                  } else if (ModKeybinds.openCardDeck.isActiveAndMatches(key)) {
                     ModNetwork.CHANNEL.sendToServer(new OpenCardDeckMessage());
                  }
               }
            }
         } else if ((!isKeyDown(ModKeybinds.abilityKey.getKey()) || !ModKeybinds.abilityKey.getKeyModifier().matches(key))
            && !ModKeybinds.abilityKey.getKey().equals(key)) {
            for (Entry<String, KeyMapping> entryx : ModKeybinds.abilityQuickfireKey.entrySet()) {
               KeyMapping keyMapping = entryx.getValue();
               if (isKeyDown(keyMapping.getKey()) && keyMapping.getKeyModifier().matches(key) || keyMapping.getKey().equals(key)) {
                  ModNetwork.CHANNEL.sendToServer(new AbilityQuickselectMessage(entryx.getKey(), 0));
                  return;
               }
            }
         } else {
            if (!scrollingAbilities) {
               ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyUp);
            } else {
               scrollingAbilities = false;
            }
         }
      }
   }

   private static void checkAndReleaseHoldAbility(Key key) {
      Ability ability = ClientAbilityData.getSelectedAbility();
      if (ability instanceof HoldAbility && ability.isActive()) {
         if (!ModKeybinds.abilityKey.getKeyModifier().matches(key) && !ModKeybinds.abilityKey.getKey().equals(key)) {
            KeyMapping keyMapping = ModKeybinds.abilityQuickfireKey.get(ability.getParent().getParent().getId());
            if (keyMapping != null && (keyMapping.getKeyModifier().matches(key) || keyMapping.getKey().equals(key))) {
               ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyUp);
            }
         } else {
            ServerboundAbilityKeyMessage.send(ServerboundAbilityKeyMessage.Opcode.KeyUp);
         }
      }
   }

   private static boolean isKeyDown(Key key) {
      return KEY_DOWN_SET.contains(key);
   }

   @SubscribeEvent
   public static void onMouseScroll(MouseScrollEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         IVaultOptions options = (IVaultOptions)Minecraft.getInstance().options;
         if (options.isAbilityScrollingEnabled()) {
            double scrollDelta = event.getScrollDelta();
            if (ModKeybinds.abilityKey.isDown()) {
               if (minecraft.screen == null) {
                  scrollingAbilities = true;
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
}
