package iskallia.vault.event;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.ClientActiveEternalData;
import iskallia.vault.client.ClientDamageData;
import iskallia.vault.core.world.loot.LootTableInfo;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientEvents {
   @SubscribeEvent
   public static void cleanupHealthTexture(Post event) {
      if (event.getType() == ElementType.ALL) {
         RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      }
   }

   @SubscribeEvent
   public static void onDisconnect(LoggedOutEvent event) {
      ClientActiveEternalData.clearClientCache();
      ClientDamageData.clearClientCache();
   }

   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      ModConfigs.TOOLTIP.getTooltipString(event.getItemStack().getItem()).ifPresent(str -> {
         List<Component> tooltip = event.getToolTip();
         List<String> added = Lists.reverse(Lists.newArrayList(str.split("\n")));
         if (!added.isEmpty()) {
            tooltip.add(1, TextComponent.EMPTY);

            for (String newStr : added) {
               tooltip.add(1, new TextComponent(newStr).withStyle(ChatFormatting.GRAY));
            }
         }
      });
      ItemStack current = event.getItemStack();
      if (!current.isEmpty()) {
         Item item = current.getItem();
         if (ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey(item.getRegistryName())) {
            int value = ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().get(item.getRegistryName());
            if (value > 0) {
               if (Screen.hasShiftDown()) {
                  event.getToolTip()
                     .add(
                        1,
                        new TextComponent("Soul Value: ")
                           .withStyle(ChatFormatting.GRAY)
                           .append(new TextComponent(value + " [" + current.getCount() * value + "]").withStyle(ChatFormatting.DARK_PURPLE))
                     );
               } else {
                  event.getToolTip()
                     .add(
                        1,
                        new TextComponent("Soul Value: ")
                           .withStyle(ChatFormatting.GRAY)
                           .append(new TextComponent(value + "").withStyle(ChatFormatting.DARK_PURPLE))
                     );
               }
            }
         }

         addLootTableInfoToTooltip(current, event.getToolTip());
         if (event.getFlags().isAdvanced()) {
            removeVanillaDurabilityLineFromVaultGearTooltip(current, event.getToolTip());
         }
      }
   }

   private static void removeVanillaDurabilityLineFromVaultGearTooltip(ItemStack itemStack, List<Component> tooltip) {
      Item item = itemStack.getItem();
      if (item instanceof VaultGearItem) {
         for (int i = tooltip.size() - 1; i >= 0; i--) {
            Component component = tooltip.get(i);
            if (component instanceof TranslatableComponent translatableComponent && "item.durability".equals(translatableComponent.getKey())) {
               tooltip.remove(i);
               break;
            }
         }
      }
   }

   private static void addLootTableInfoToTooltip(ItemStack itemStack, List<Component> toolTip) {
      Item item = itemStack.getItem();
      if (item instanceof VaultGearItem) {
         VaultGearState state = VaultGearData.read(itemStack).getState();
         if (state != VaultGearState.UNIDENTIFIED) {
            return;
         }
      }

      if (LootTableInfo.containsInfoForItem(item.getRegistryName())) {
         if (Screen.hasShiftDown()) {
            toolTip.add(new TextComponent("Found in:").withStyle(ChatFormatting.GRAY));
            LootTableInfo.getLootTableKeysForItem(item.getRegistryName())
               .stream()
               .flatMap(resourceLocation -> ModConfigs.LOOT_INFO_CONFIG.getTooltipLines(item).stream())
               .distinct()
               .forEach(name -> toolTip.add(new TextComponent("  - " + name).withStyle(ChatFormatting.GRAY)));
         } else {
            toolTip.add(
               new TextComponent("Hold ")
                  .withStyle(ChatFormatting.DARK_GRAY)
                  .append(new TextComponent("<SHIFT>").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent(" for Vault Loot Info").withStyle(ChatFormatting.DARK_GRAY))
            );
         }
      }
   }
}
