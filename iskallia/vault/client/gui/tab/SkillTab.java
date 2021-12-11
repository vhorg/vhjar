package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.util.MiscUtils;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;

public abstract class SkillTab extends Screen {
   protected SkillTreeScreen parentScreen;
   protected static Map<Class<? extends SkillTab>, Vector2f> persistedTranslations = new HashMap<>();
   protected static Map<Class<? extends SkillTab>, Float> persistedScales = new HashMap<>();
   private boolean scrollable = true;
   protected Vector2f viewportTranslation;
   protected float viewportScale;
   protected boolean dragging;
   protected Vector2f grabbedPos;

   protected SkillTab(SkillTreeScreen parentScreen, ITextComponent title) {
      super(title);
      this.parentScreen = parentScreen;
      this.viewportTranslation = persistedTranslations.computeIfAbsent((Class<? extends SkillTab>)this.getClass(), clazz -> new Vector2f(0.0F, 0.0F));
      this.viewportScale = persistedScales.computeIfAbsent((Class<? extends SkillTab>)this.getClass(), clazz -> 1.0F);
      this.dragging = false;
      this.grabbedPos = new Vector2f(0.0F, 0.0F);
   }

   protected void setScrollable(boolean scrollable) {
      this.scrollable = scrollable;
   }

   public abstract void refresh();

   public abstract String getTabName();

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      if (this.scrollable) {
         this.dragging = true;
         this.grabbedPos = new Vector2f((float)mouseX, (float)mouseY);
      }

      return super.func_231044_a_(mouseX, mouseY, button);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      if (this.scrollable) {
         this.dragging = false;
      }

      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public void func_212927_b(double mouseX, double mouseY) {
      if (this.scrollable && this.dragging) {
         float dx = (float)(mouseX - this.grabbedPos.field_189982_i) / this.viewportScale;
         float dy = (float)(mouseY - this.grabbedPos.field_189983_j) / this.viewportScale;
         this.viewportTranslation = new Vector2f(this.viewportTranslation.field_189982_i + dx, this.viewportTranslation.field_189983_j + dy);
         this.grabbedPos = new Vector2f((float)mouseX, (float)mouseY);
      }
   }

   public boolean func_231043_a_(double mouseX, double mouseY, double delta) {
      boolean mouseScrolled = super.func_231043_a_(mouseX, mouseY, delta);
      if (!this.scrollable) {
         return mouseScrolled;
      } else {
         java.awt.geom.Point2D.Float midpoint = MiscUtils.getMidpoint(this.parentScreen.getContainerBounds());
         double zoomingX = (mouseX - midpoint.x) / this.viewportScale + this.viewportTranslation.field_189982_i;
         double zoomingY = (mouseY - midpoint.y) / this.viewportScale + this.viewportTranslation.field_189983_j;
         int wheel = delta < 0.0 ? -1 : 1;
         double zoomTargetX = (zoomingX - this.viewportTranslation.field_189982_i) / this.viewportScale;
         double zoomTargetY = (zoomingY - this.viewportTranslation.field_189983_j) / this.viewportScale;
         this.viewportScale = (float)(this.viewportScale + 0.25 * wheel * this.viewportScale);
         this.viewportScale = (float)MathHelper.func_151237_a(this.viewportScale, 0.5, 5.0);
         this.viewportTranslation = new Vector2f((float)(-zoomTargetX * this.viewportScale + zoomingX), (float)(-zoomTargetY * this.viewportScale + zoomingY));
         return mouseScrolled;
      }
   }

   public void func_231164_f_() {
      persistedTranslations.put((Class<? extends SkillTab>)this.getClass(), this.viewportTranslation);
      persistedScales.put((Class<? extends SkillTab>)this.getClass(), this.viewportScale);
   }

   public List<Runnable> renderTab(Rectangle containerBounds, MatrixStack renderStack, int mouseX, int mouseY, float pTicks) {
      List<Runnable> postRender = new ArrayList<>();
      UIHelper.renderOverflowHidden(
         renderStack, ms -> this.renderTabBackground(ms, containerBounds), ms -> this.renderTabForeground(ms, mouseX, mouseY, pTicks, postRender)
      );
      return postRender;
   }

   public void renderTabForeground(MatrixStack renderStack, int mouseX, int mouseY, float pTicks, List<Runnable> postContainerRender) {
      this.func_230430_a_(renderStack, mouseX, mouseY, pTicks);
   }

   public void renderTabBackground(MatrixStack matrixStack, Rectangle containerBounds) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.BACKGROUNDS_RESOURCE);
      ScreenDrawHelper.draw(
         7,
         DefaultVertexFormats.field_227851_o_,
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
}
