package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.Renderable;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ScrollableContainer extends AbstractGui {
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability-tree.png");
   public static final int SCROLL_WIDTH = 8;
   protected Rectangle bounds;
   protected Renderable renderer;
   protected int innerHeight;
   protected int yOffset;
   protected boolean scrolling;
   protected double scrollingStartY;
   protected int scrollingOffsetY;

   public ScrollableContainer(Renderable renderer) {
      this.renderer = renderer;
   }

   public int getyOffset() {
      return this.yOffset;
   }

   public float scrollPercentage() {
      Rectangle scrollBounds = this.getScrollBounds();
      return (float)this.yOffset / (this.innerHeight - scrollBounds.getHeight());
   }

   public void setInnerHeight(int innerHeight) {
      this.innerHeight = innerHeight;
   }

   public void setBounds(Rectangle bounds) {
      this.bounds = bounds;
   }

   public Rectangle getRenderableBounds() {
      Rectangle renderableBounds = new Rectangle(this.bounds);
      int margin = 2;
      renderableBounds.x1 -= 8 + margin;
      return renderableBounds;
   }

   public Rectangle getScrollBounds() {
      Rectangle scrollBounds = new Rectangle(this.bounds);
      int margin = 2;
      scrollBounds.x0 = scrollBounds.x1 - 8;
      return scrollBounds;
   }

   public void mouseMoved(double mouseX, double mouseY) {
      if (this.scrolling) {
         double deltaY = mouseY - this.scrollingStartY;
         Rectangle renderableBounds = this.getRenderableBounds();
         Rectangle scrollBounds = this.getScrollBounds();
         double deltaOffset = deltaY * this.innerHeight / scrollBounds.getHeight();
         this.yOffset = MathHelper.func_76125_a(
            this.scrollingOffsetY + (int)(deltaOffset * this.innerHeight / scrollBounds.getHeight()), 0, this.innerHeight - renderableBounds.getHeight() + 2
         );
      }
   }

   public void mouseClicked(double mouseX, double mouseY, int button) {
      Rectangle renderableBounds = this.getRenderableBounds();
      Rectangle scrollBounds = this.getScrollBounds();
      float viewportRatio = (float)renderableBounds.getHeight() / this.innerHeight;
      if (viewportRatio < 1.0F && scrollBounds.contains((int)mouseX, (int)mouseY)) {
         this.scrolling = true;
         this.scrollingStartY = mouseY;
         this.scrollingOffsetY = this.yOffset;
      }
   }

   public void mouseReleased(double mouseX, double mouseY, int button) {
      this.scrolling = false;
   }

   public void mouseScrolled(double mouseX, double mouseY, double delta) {
      Rectangle renderableBounds = this.getRenderableBounds();
      float viewportRatio = (float)renderableBounds.getHeight() / this.innerHeight;
      if (viewportRatio < 1.0F) {
         this.yOffset = MathHelper.func_76125_a(this.yOffset + (int)(-delta * 5.0), 0, this.innerHeight - renderableBounds.getHeight() + 2);
      }
   }

   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      TextureManager textureManager = Minecraft.func_71410_x().func_110434_K();
      Rectangle renderableBounds = this.getRenderableBounds();
      Rectangle scrollBounds = this.getScrollBounds();
      textureManager.func_110577_a(SkillTreeScreen.UI_RESOURCE);
      UIHelper.renderContainerBorder(this, matrixStack, renderableBounds, 14, 44, 2, 2, 2, 2, -7631989);
      UIHelper.renderOverflowHidden(
         matrixStack,
         ms -> func_238467_a_(ms, renderableBounds.x0 + 1, renderableBounds.y0 + 1, renderableBounds.x1 - 1, renderableBounds.y1 - 1, -7631989),
         ms -> {
            ms.func_227860_a_();
            ms.func_227861_a_(renderableBounds.x0 + 1, renderableBounds.y0 - this.yOffset + 1, 0.0);
            this.renderer.render(matrixStack, mouseX, mouseY, partialTicks);
            ms.func_227865_b_();
         }
      );
      textureManager.func_110577_a(SkillTreeScreen.UI_RESOURCE);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(scrollBounds.x0 + 2, scrollBounds.y0, 0.0);
      matrixStack.func_227862_a_(1.0F, scrollBounds.getHeight(), 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 1, 146, 6, 1);
      matrixStack.func_227865_b_();
      this.func_238474_b_(matrixStack, scrollBounds.x0 + 2, scrollBounds.y0, 1, 145, 6, 1);
      this.func_238474_b_(matrixStack, scrollBounds.x0 + 2, scrollBounds.y1 - 1, 1, 251, 6, 1);
      float scrollPercentage = this.scrollPercentage();
      float viewportRatio = (float)renderableBounds.getHeight() / this.innerHeight;
      int scrollHeight = (int)(renderableBounds.getHeight() * viewportRatio);
      if (viewportRatio <= 1.0F) {
         int scrollU = this.scrolling ? 28 : (scrollBounds.contains(mouseX, mouseY) ? 18 : 8);
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, (scrollBounds.getHeight() - scrollHeight) * scrollPercentage, 0.0);
         this.func_238474_b_(matrixStack, scrollBounds.x0 + 1, scrollBounds.y0, scrollU, 104, 8, scrollHeight);
         this.func_238474_b_(matrixStack, scrollBounds.x0 + 1, scrollBounds.y0 - 2, scrollU, 101, 8, 2);
         this.func_238474_b_(matrixStack, scrollBounds.x0 + 1, scrollBounds.y0 + scrollHeight, scrollU, 253, 8, 2);
         matrixStack.func_227865_b_();
      }
   }
}
