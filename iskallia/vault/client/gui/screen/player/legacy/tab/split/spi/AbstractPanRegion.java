package iskallia.vault.client.gui.screen.player.legacy.tab.split.spi;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public abstract class AbstractPanRegion<S extends AbstractSkillTabElementContainerScreen<?>> extends Screen {
   protected S parentScreen;
   protected static Map<Class<?>, Vec2> persistedTranslations = new HashMap<>();
   protected static Map<Class<?>, Float> persistedScales = new HashMap<>();
   protected final AbstractPanRegion.CenterButton centerButton;
   private boolean scrollable = true;
   protected Vec2 viewportTranslation;
   protected float viewportScale;
   protected boolean dragging;
   protected Vec2 grabbedPos;

   protected AbstractPanRegion(S parentScreen, Component title) {
      super(title);
      this.parentScreen = parentScreen;
      this.dragging = false;
      this.grabbedPos = new Vec2(0.0F, 0.0F);
      this.centerButton = this.createCenterButton();
   }

   @Nullable
   protected AbstractPanRegion.CenterButton createCenterButton() {
      return new AbstractPanRegion.CenterButton(0, 0, 18, 16, TextComponent.EMPTY, button -> this.loadViewportTransforms(true));
   }

   protected void loadViewportTransforms(boolean forceCenter) {
      Class<?> aClass = this.getClass();
      if (forceCenter || !persistedTranslations.containsKey(aClass)) {
         persistedTranslations.put(aClass, this.getCenter());
         persistedScales.put(aClass, this.clampViewportScale(0.5F));
      }

      this.viewportTranslation = persistedTranslations.get(aClass);
      this.viewportScale = persistedScales.get(aClass);
   }

   protected void saveViewportTransforms() {
      persistedTranslations.put(this.getClass(), this.viewportTranslation);
      persistedScales.put(this.getClass(), this.viewportScale);
   }

   public Rectangle getBounds() {
      return new Rectangle(30, 60, (int)(this.parentScreen.width * 0.55F) - 30, this.parentScreen.height - 30 - 60);
   }

   protected void setScrollable(boolean scrollable) {
      this.scrollable = scrollable;
   }

   public abstract void update();

   protected abstract Collection<? extends AbstractWidget> getWidgets();

   protected Vec2 getCenter() {
      int minX = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxY = Integer.MIN_VALUE;

      for (AbstractWidget widget : this.getWidgets()) {
         if (widget.x < minX) {
            minX = widget.x;
         }

         if (widget.x > maxX) {
            maxX = widget.x;
         }

         if (widget.y < minY) {
            minY = widget.y;
         }

         if (widget.y > maxY) {
            maxY = widget.y;
         }
      }

      return new Vec2((minX + maxX) / -2, (minY + maxY) / -2);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.centerButton != null && this.centerButton.mouseClicked(mouseX, mouseY, 0)) {
         return true;
      } else {
         if (this.scrollable) {
            this.dragging = true;
            this.grabbedPos = new Vec2((float)mouseX, (float)mouseY);
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.scrollable) {
         this.dragging = false;
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public void mouseMoved(double mouseX, double mouseY) {
      if (this.scrollable && this.dragging) {
         this.calculateViewportTranslation(mouseX, mouseY);
         this.grabbedPos = new Vec2((float)mouseX, (float)mouseY);
      }
   }

   protected void calculateViewportTranslation(double mouseX, double mouseY) {
      float dx = (float)(mouseX - this.grabbedPos.x) / this.viewportScale;
      float dy = (float)(mouseY - this.grabbedPos.y) / this.viewportScale;
      this.viewportTranslation = new Vec2(this.viewportTranslation.x + dx, this.viewportTranslation.y + dy);
      this.saveViewportTransforms();
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      boolean mouseScrolled = super.mouseScrolled(mouseX, mouseY, delta);
      if (!this.scrollable) {
         return mouseScrolled;
      } else {
         java.awt.geom.Point2D.Float midpoint = MiscUtils.getMidpoint(this.getBounds());
         double zoomingX = (mouseX - midpoint.x) / this.viewportScale + this.viewportTranslation.x;
         double zoomingY = (mouseY - midpoint.y) / this.viewportScale + this.viewportTranslation.y;
         int wheel = delta < 0.0 ? -1 : 1;
         double zoomTargetX = (zoomingX - this.viewportTranslation.x) / this.viewportScale;
         double zoomTargetY = (zoomingY - this.viewportTranslation.y) / this.viewportScale;
         this.viewportScale = (float)(this.viewportScale + 0.25 * wheel * this.viewportScale);
         this.viewportScale = this.clampViewportScale(this.viewportScale);
         this.viewportTranslation = new Vec2((float)(-zoomTargetX * this.viewportScale + zoomingX), (float)(-zoomTargetY * this.viewportScale + zoomingY));
         this.saveViewportTransforms();
         return mouseScrolled;
      }
   }

   protected float clampViewportScale(float viewportScale) {
      return Mth.clamp(viewportScale, 0.5F, 5.0F);
   }

   public void removed() {
      this.saveViewportTransforms();
   }

   public List<Runnable> renderTab(Rectangle containerBounds, PoseStack renderStack, int mouseX, int mouseY, float pTicks) {
      List<Runnable> postRender = new ArrayList<>();
      UIHelper.renderOverflowHidden(
         renderStack,
         ms -> this.renderTabBackground(ms, containerBounds),
         ms -> this.renderTabForeground(ms, containerBounds, mouseX, mouseY, pTicks, postRender)
      );
      if (this.centerButton != null) {
         if (this.centerButton.isHoveredOrFocused()) {
            postRender.clear();
            postRender.add(
               () -> TooltipUtil.renderTooltip(
                  renderStack,
                  List.of(FormattedCharSequence.forward("Center", Style.EMPTY.withColor(Color.WHITE.getRGB()))),
                  mouseX,
                  mouseY,
                  Integer.MAX_VALUE,
                  Integer.MAX_VALUE
               )
            );
         }

         this.centerButton.x = containerBounds.x + containerBounds.width - 20;
         this.centerButton.y = containerBounds.y + containerBounds.height - 16;
         this.centerButton.render(renderStack, mouseX, mouseY, pTicks);
      }

      return postRender;
   }

   public void renderTabForeground(PoseStack renderStack, Rectangle containerBounds, int mouseX, int mouseY, float pTicks, List<Runnable> postContainerRender) {
      this.render(renderStack, mouseX, mouseY, pTicks);
   }

   public void renderTabBackground(PoseStack matrixStack, Rectangle containerBounds) {
      RenderSystem.setShaderTexture(0, ScreenTextures.BACKGROUNDS_RESOURCE);
      ScreenDrawHelper.draw(
         Mode.QUADS,
         DefaultVertexFormat.POSITION_COLOR_TEX,
         buf -> {
            float textureSize = 16.0F * this.viewportScale;
            float currentX = containerBounds.x;
            float currentY = containerBounds.y;
            float uncoveredWidth = containerBounds.width;

            for (float uncoveredHeight = containerBounds.height; uncoveredWidth > 0.0F; currentY = containerBounds.y) {
               while (uncoveredHeight > 0.0F) {
                  float pWidth = Math.min(textureSize, uncoveredWidth) / textureSize;
                  float pHeight = Math.min(textureSize, uncoveredHeight) / textureSize;
                  ScreenDrawHelper.rect(buf, matrixStack, currentX, currentY, 0.0F, pWidth * textureSize, pHeight * textureSize)
                     .tex(0.31254F, 0.0F, 0.999F * pWidth / 16.0F, 0.999F * pHeight / 16.0F)
                     .draw();
                  uncoveredHeight -= textureSize;
                  currentY += textureSize;
               }

               uncoveredWidth -= textureSize;
               currentX += textureSize;
               uncoveredHeight = containerBounds.height;
            }
         }
      );
   }

   public static class CenterButton extends Button {
      public CenterButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
         super(pX, pY, pWidth, pHeight, pMessage, pOnPress, Button.NO_TOOLTIP);
      }

      protected void renderBg(@Nonnull PoseStack poseStack, @Nonnull Minecraft minecraft, int mouseX, int mouseY) {
         RenderSystem.setShaderTexture(0, ScreenTextures.UI_RESOURCE);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(poseStack, this.x + 1, this.y, 192, 0, 16, 16);
      }
   }
}
