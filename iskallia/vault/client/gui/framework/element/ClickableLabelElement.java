package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

public class ClickableLabelElement extends LabelElement<ClickableLabelElement> implements IGuiEventElement {
   private Consumer<ClickableLabelElement> onClick;
   protected Supplier<Boolean> disabled;
   protected boolean wasDisabled = false;
   protected boolean clickHeld = false;
   protected Component originalComponent;
   protected TextColor baseColor;
   protected TextColor hoverColor;
   protected TextColor disabledColor;

   public ClickableLabelElement(IPosition position, Component component, LabelTextStyle.Builder labelTextStyle, Runnable onClick) {
      this(
         position,
         component,
         TextColor.fromLegacyFormat(ChatFormatting.BLACK),
         TextColor.fromLegacyFormat(ChatFormatting.GREEN),
         TextColor.fromLegacyFormat(ChatFormatting.GRAY),
         labelTextStyle,
         onClick
      );
   }

   public ClickableLabelElement(
      IPosition position,
      Component component,
      TextColor baseColor,
      TextColor hoverColor,
      TextColor disabledColor,
      LabelTextStyle.Builder labelTextStyle,
      Runnable onClick
   ) {
      super(position, component, labelTextStyle);
      this.baseColor = baseColor;
      this.hoverColor = hoverColor;
      this.disabledColor = disabledColor;
      this.component = component.copy().withStyle(Style.EMPTY.withUnderlined(true).withColor(baseColor));
      this.originalComponent = this.component;
      this.onClick = label -> onClick.run();
      this.setDisabled(false);
   }

   public void setOnClick(Consumer<ClickableLabelElement> onClick) {
      this.onClick = this.onClick.andThen(onClick);
   }

   public void setOnClick(Runnable onClick) {
      this.setOnClick(btn -> onClick.run());
   }

   public ClickableLabelElement setDisabled(boolean disabled) {
      this.setDisabled(() -> disabled);
      return this;
   }

   public ClickableLabelElement setDisabled(Supplier<Boolean> disabled) {
      if (this.disabled != null && this.isDisabled() && !disabled.get()) {
         this.wasDisabled = true;
      }

      this.disabled = disabled;
      return this;
   }

   public boolean isDisabled() {
      return this.disabled.get();
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      if (buttonIndex == 0) {
         this.clickHeld = true;
      }

      return true;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      boolean dragged = IGuiEventElement.super.mouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
      if (!this.containsMouse(mouseX, mouseY)) {
         this.clickHeld = false;
         this.component = this.originalComponent;
      }

      return dragged;
   }

   @Override
   public void onMouseMoved(double mouseX, double mouseY) {
      IGuiEventElement.super.onMouseMoved(mouseX, mouseY);
      if (this.containsMouse(mouseX, mouseY)) {
         this.component = this.component.copy().withStyle(this.component.getStyle().withColor(this.hoverColor));
      } else {
         this.component = this.originalComponent;
      }
   }

   @Override
   public boolean onMouseReleased(double mouseX, double mouseY, int buttonIndex) {
      if (!this.isDisabled() && this.clickHeld) {
         this.onClick.accept(this);
         this.playDownSound(Minecraft.getInstance().getSoundManager());
      }

      return true;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      boolean release = IGuiEventElement.super.mouseReleased(mouseX, mouseY, buttonIndex);
      this.clickHeld = false;
      return release;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      if (this.isDisabled()) {
         this.component = this.component.copy().withStyle(this.component.getStyle().withColor(this.disabledColor));
      } else if (this.wasDisabled) {
         this.component = this.originalComponent;
         this.wasDisabled = false;
      }
   }
}
