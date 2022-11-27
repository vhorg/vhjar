package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.Button.OnTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class TooltipImageButton extends Button {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffText;
   private final int textureWidth;
   private final int textureHeight;

   public TooltipImageButton(
      int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation texture, OnPress onPressIn
   ) {
      this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, texture, 256, 256, onPressIn);
   }

   public TooltipImageButton(
      int xIn,
      int yIn,
      int widthIn,
      int heightIn,
      int xTexStartIn,
      int yTexStartIn,
      int yDiffTextIn,
      ResourceLocation texture,
      int textureWidth,
      int textureHeight,
      OnPress onPressIn
   ) {
      this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, texture, textureWidth, textureHeight, onPressIn, TextComponent.EMPTY);
   }

   public TooltipImageButton(
      int x,
      int y,
      int width,
      int height,
      int xTexStart,
      int yTexStart,
      int yDiffText,
      ResourceLocation texture,
      int textureWidth,
      int textureHeight,
      OnPress onPress,
      Component title
   ) {
      this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, textureWidth, textureHeight, onPress, NO_TOOLTIP, title);
   }

   public TooltipImageButton(
      int x,
      int y,
      int width,
      int height,
      int xTexStart,
      int yTexStart,
      int yDiffText,
      ResourceLocation texture,
      int textureWidth,
      int textureHeight,
      OnPress onPress,
      OnTooltip onTooltip,
      Component title
   ) {
      super(x, y, width, height, title, onPress, onTooltip);
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.xTexStart = xTexStart;
      this.yTexStart = yTexStart;
      this.yDiffText = yDiffText;
      this.resourceLocation = texture;
   }

   public void setPosition(int xIn, int yIn) {
      this.x = xIn;
      this.y = yIn;
   }

   public void renderButton(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.isHovered) {
         this.renderToolTip(matrixStack, mouseX, mouseY);
      }

      RenderSystem.setShaderTexture(0, this.resourceLocation);
      int v = this.yTexStart;
      if (this.isHovered) {
         v += this.yDiffText;
      }

      RenderSystem.enableDepthTest();
      blit(matrixStack, this.x, this.y, this.xTexStart, v, this.width, this.height, this.textureWidth, this.textureHeight);
   }
}
