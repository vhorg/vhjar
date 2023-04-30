package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

public class NineSliceButtonElement<E extends NineSliceButtonElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement, IGuiEventElement {
   private final NineSliceButtonElement.NineSliceButtonTextures textures;
   private final Runnable onClick;
   protected boolean visible;
   protected Supplier<Boolean> disabled;
   protected Supplier<Component> component;
   protected LabelTextStyle labelTextStyle;
   private boolean clickHeld = false;
   private double timeHeld = 0.0;
   private Supplier<Boolean> renderButtonHeld;

   public NineSliceButtonElement(ISpatial spatial, NineSliceButtonElement.NineSliceButtonTextures textures, Runnable onClick) {
      super(Spatials.positionXYZ(spatial.x(), spatial.y(), spatial.z()).width(spatial.width()).height(spatial.height()));
      this.textures = textures;
      this.onClick = onClick;
      this.setVisible(true);
      this.setDisabled(false);
      this.renderButtonHeld = () -> false;
   }

   public <T extends NineSliceButtonElement<E>> T label(Supplier<Component> component, LabelTextStyle.Builder labelTextStyle) {
      this.component = component;
      this.labelTextStyle = labelTextStyle.build();
      ScreenLayout.requestLayout();
      return (T)this;
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   public <T extends NineSliceButtonElement<E>> T setDisabled(boolean disabled) {
      this.setDisabled(() -> disabled);
      return (T)this;
   }

   public <T extends NineSliceButtonElement<E>> T setDisabled(Supplier<Boolean> disabled) {
      this.disabled = disabled;
      return (T)this;
   }

   public boolean isDisabled() {
      return this.disabled.get();
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      if (!this.isDisabled()) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

      this.clickHeld = true;
      return true;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      boolean dragged = IGuiEventElement.super.mouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
      if (!this.containsMouse(mouseX, mouseY)) {
         this.clickHeld = false;
      }

      return dragged;
   }

   @Override
   public boolean onMouseReleased(double mouseX, double mouseY, int buttonIndex) {
      if (!this.isDisabled() && this.clickHeld) {
         this.onClick.run();
      }

      ScreenLayout.requestLayout();
      return true;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      boolean release = IGuiEventElement.super.mouseReleased(mouseX, mouseY, buttonIndex);
      this.clickHeld = false;
      this.timeHeld = 0.0;
      return release;
   }

   public double getTimeHeld() {
      return this.timeHeld;
   }

   public boolean isRenderButtonHeld() {
      return this.renderButtonHeld.get();
   }

   public <T extends NineSliceButtonElement<E>> T setRenderButtonHeld(Supplier<Boolean> renderButtonHeld) {
      this.renderButtonHeld = renderButtonHeld;
      return (T)this;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      if (this.clickHeld) {
         this.timeHeld += 1.0F * partialTick;
         if (this.renderButtonHeld.get()) {
            ScreenRenderers.getImmediate()
               .renderColoredQuad(
                  poseStack,
                  -286392320,
                  this.x() + 2,
                  this.y() + 2,
                  this.z() + 1,
                  Math.min((int)(this.timeHeld / 60.0 * (this.width() - 4)), this.width() - 4),
                  this.height() - 4
               );
         }
      }

      NineSlice.TextureRegion texture = this.textures.selectTexture(this.isDisabled(), this.containsMouse(mouseX, mouseY), this.clickHeld);
      renderer.render(texture, poseStack, this.worldSpatial);
      if (this.component != null) {
         this.renderLabel(
            renderer,
            poseStack,
            this.getWorldSpatial().x(),
            this.getWorldSpatial().y() + this.getWorldSpatial().height() / 2 - 9 / 2,
            this.getWorldSpatial().z() + 5,
            this.getWorldSpatial().width()
         );
      }
   }

   protected void renderLabel(IElementRenderer renderer, PoseStack poseStack, int x, int y, int z, int width) {
      this.labelTextStyle
         .textBorder()
         .render(renderer, poseStack, this.component.get(), this.labelTextStyle.textWrap(), this.labelTextStyle.textAlign(), x, y, z, width);
   }

   public record NineSliceButtonTextures(
      NineSlice.TextureRegion button, NineSlice.TextureRegion buttonHover, NineSlice.TextureRegion buttonHeld, NineSlice.TextureRegion buttonDisabled
   ) {
      private NineSlice.TextureRegion selectTexture(boolean disabled, boolean hover, boolean clicked) {
         if (disabled) {
            return this.buttonDisabled();
         } else if (clicked) {
            return this.buttonHeld();
         } else {
            return hover ? this.buttonHover() : this.button();
         }
      }
   }
}
