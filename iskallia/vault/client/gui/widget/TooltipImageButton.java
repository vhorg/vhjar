package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.gui.widget.button.Button.ITooltip;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TooltipImageButton extends Button {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffText;
   private final int textureWidth;
   private final int textureHeight;

   public TooltipImageButton(
      int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation texture, IPressable onPressIn
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
      IPressable onPressIn
   ) {
      this(
         xIn,
         yIn,
         widthIn,
         heightIn,
         xTexStartIn,
         yTexStartIn,
         yDiffTextIn,
         texture,
         textureWidth,
         textureHeight,
         onPressIn,
         StringTextComponent.field_240750_d_
      );
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
      IPressable onPress,
      ITextComponent title
   ) {
      this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, textureWidth, textureHeight, onPress, field_238486_s_, title);
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
      IPressable onPress,
      ITooltip onTooltip,
      ITextComponent title
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
      this.field_230690_l_ = xIn;
      this.field_230691_m_ = yIn;
   }

   public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.func_230449_g_()) {
         this.func_230443_a_(matrixStack, mouseX, mouseY);
      }

      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_110434_K().func_110577_a(this.resourceLocation);
      int v = this.yTexStart;
      if (this.func_230449_g_()) {
         v += this.yDiffText;
      }

      RenderSystem.enableDepthTest();
      func_238463_a_(
         matrixStack,
         this.field_230690_l_,
         this.field_230691_m_,
         this.xTexStart,
         v,
         this.field_230688_j_,
         this.field_230689_k_,
         this.textureWidth,
         this.textureHeight
      );
   }
}
