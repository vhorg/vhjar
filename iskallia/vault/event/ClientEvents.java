package iskallia.vault.event;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientActiveEternalData;
import iskallia.vault.client.ClientDamageData;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientEvents {
   private static final ResourceLocation OVERLAY_ICONS = VaultMod.id("textures/gui/overlay_icons.png");

   public static void setupHealthTexture(Pre event) {
      if (event.getType() == ElementType.ALL) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            ;
         }
      }
   }

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
      if (!current.isEmpty() && ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey(current.getItem().getRegistryName())) {
         int value = ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().get(current.getItem().getRegistryName());
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
   }
}
