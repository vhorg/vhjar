package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelAutoResize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class LabelElement<E extends LabelElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected Component component;
   protected final LabelTextStyle labelTextStyle;
   protected LabelAutoResize autoResize;
   protected boolean visible;

   public LabelElement(IPosition position, LabelTextStyle.Builder labelTextStyle) {
      this(position, Spatials.height(9), new TextComponent(""), labelTextStyle);
   }

   public LabelElement(IPosition position, Component component, LabelTextStyle.Builder labelTextStyle) {
      this(position, Spatials.width(TextBorder.DEFAULT_FONT.get().width(component.getVisualOrderText())).height(9), component, labelTextStyle);
   }

   public LabelElement(IPosition position, ISize size, LabelTextStyle.Builder labelTextStyle) {
      this(position, size, new TextComponent(""), labelTextStyle);
   }

   public LabelElement(IPosition position, ISize size, Component component, LabelTextStyle.Builder labelTextStyle) {
      super(Spatials.positionXYZ(position).size(size));
      this.component = component;
      this.labelTextStyle = labelTextStyle.build();
      this.autoResize = LabelAutoResize.HEIGHT;
      this.setVisible(true);
   }

   public void setAutoResize(LabelAutoResize autoResize) {
      this.autoResize = autoResize;
   }

   @Override
   protected void layoutSelf(ISize screen, ISpatial gui, ISpatial parent) {
      super.layoutSelf(screen, gui, parent);
      if (this.autoResize.isWidth()) {
         this.worldSpatial.width(Minecraft.getInstance().font.width(this.component));
      }

      if (this.autoResize.isHeight()) {
         this.worldSpatial.height(this.getTextStyle().calculateLines(this.getComponent(), this.width()) * 9);
      }
   }

   public void set(String text) {
      this.set(new TextComponent(text));
   }

   public void set(Component component) {
      this.component = component;
      ScreenLayout.requestLayout();
   }

   public Component getComponent() {
      return this.component;
   }

   public LabelTextStyle getTextStyle() {
      return this.labelTextStyle;
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.getTextStyle()
         .textBorder()
         .render(
            renderer,
            poseStack,
            this.getComponent(),
            this.getTextStyle().textWrap(),
            this.getTextStyle().textAlign(),
            this.getWorldSpatial().x(),
            this.getWorldSpatial().y(),
            this.getWorldSpatial().z(),
            this.getWorldSpatial().width()
         );
   }
}
