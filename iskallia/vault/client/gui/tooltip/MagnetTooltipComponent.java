package iskallia.vault.client.gui.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.screen.MagnetTableScreen;
import iskallia.vault.event.InputEvents;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.LegacyMagnetItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class MagnetTooltipComponent implements ClientTooltipComponent {
   private static final ResourceLocation TEXTURE = MagnetTableScreen.TEXTURE;
   private final LegacyMagnetItem.MagnetTooltip tooltip;
   private final TextComponent advanced;

   public MagnetTooltipComponent(LegacyMagnetItem.MagnetTooltip tooltip) {
      this.tooltip = tooltip;
      this.advanced = new TextComponent(
         " " + ChatFormatting.DARK_GRAY + ModConfigs.MAGNET_CONFIG.getPerkUpgrade(tooltip.perk).getAdvancedTooltip(tooltip.perkPower)
      );
   }

   public int getHeight() {
      return 16 * (this.tooltip.perk != LegacyMagnetItem.Perk.NONE ? 2 : 1);
   }

   public int getWidth(Font font) {
      return 44
         + font.width(this.tooltip.stats[0] + "")
         + font.width(this.tooltip.stats[1] + "")
         + font.width(this.tooltip.stats[2] + "")
         + (this.tooltip.perk != LegacyMagnetItem.Perk.NONE ? (InputEvents.isShiftDown() ? font.width(this.advanced) : 0) : 0);
   }

   public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer pItemRenderer, int pBlitOffset) {
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, pBlitOffset);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int U = 180;
      int V = 112;
      int W = 10;
      int s = 9 - 1;
      int xo = x;

      for (int stat : this.tooltip.stats) {
         RenderSystem.setShaderTexture(0, TEXTURE);
         GuiComponent.blit(poseStack, x, y, s, s, U, V, W, W, 256, 256);
         V += 18;
         Component text = new TextComponent(stat + "");
         x += W;
         font.draw(poseStack, text, x, y, -1);
         x += font.width(text) + 2;
      }

      if (this.tooltip.perk != LegacyMagnetItem.Perk.NONE) {
         y += 12;
         RenderSystem.setShaderTexture(0, TEXTURE);
         GuiComponent.blit(poseStack, xo, y, s, s, 198.0F, 77 + 18 * this.tooltip.perk.ordinal(), W, W, 256, 256);
         xo += W;
         MutableComponent perkText = new TextComponent(this.tooltip.perk.getSerializedName())
            .withStyle(Style.EMPTY.withColor(ModConfigs.MAGNET_CONFIG.getPerkUpgrade(this.tooltip.perk).getColor()));
         if (InputEvents.isShiftDown()) {
            perkText.append(this.advanced);
         }

         font.draw(poseStack, perkText, xo, y, -1);
      }

      poseStack.popPose();
   }
}
