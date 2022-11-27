package iskallia.vault.client.gui.framework.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.IClipRegionStrategy;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import org.lwjgl.opengl.GL11;

public final class ClipRegionStrategies {
   public static final IClipRegionStrategy STENCIL = new IClipRegionStrategy() {
      private final PoseStack poseStack = new PoseStack();
      private int index;

      @Override
      public void beginFrame() {
         this.index = 0;
         Minecraft.getInstance().getMainRenderTarget().enableStencil();
         RenderSystem.stencilMask(255);
         RenderSystem.clearStencil(0);
         RenderSystem.clear(1024, Minecraft.ON_OSX);
         RenderSystem.stencilMask(0);
      }

      @Override
      public void endFrame() {
      }

      @Override
      public void beginClipRegion(ISpatial spatial) {
         this.index++;
         if (this.index > 255) {
            throw new IllegalStateException("Stencil buffer index has exceeded 255");
         } else {
            GL11.glEnable(2960);
            RenderSystem.disableDepthTest();
            GL11.glDepthMask(false);
            RenderSystem.colorMask(false, false, false, false);
            RenderSystem.stencilOp(7680, 7680, 7681);
            RenderSystem.stencilFunc(519, this.index, 255);
            RenderSystem.stencilMask(255);
            GuiComponent.fill(this.poseStack, spatial.left(), spatial.top(), spatial.right(), spatial.bottom(), -16777216);
            RenderSystem.stencilFunc(514, this.index, 255);
            RenderSystem.stencilMask(0);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            GL11.glDepthMask(true);
         }
      }

      @Override
      public void endClipRegion() {
         GL11.glDisable(2960);
      }
   };
   public static final IClipRegionStrategy DEPTH = new IClipRegionStrategy() {
      private final PoseStack poseStack = new PoseStack();

      @Override
      public void beginFrame() {
      }

      @Override
      public void endFrame() {
      }

      @Override
      public void beginClipRegion(ISpatial spatial) {
         RenderSystem.depthFunc(519);
         RenderSystem.colorMask(false, false, false, false);
         this.poseStack.pushPose();
         this.poseStack.translate(0.0, 0.0, 950.0);
         GuiComponent.fill(this.poseStack, 4680, 2260, -4680, -2260, -16777216);
         this.poseStack.popPose();
         RenderSystem.depthFunc(518);
         this.poseStack.pushPose();
         this.poseStack.translate(0.0, 0.0, -950.0);
         GuiComponent.fill(this.poseStack, spatial.left(), spatial.top(), spatial.right(), spatial.bottom(), -16777216);
         this.poseStack.popPose();
         RenderSystem.colorMask(true, true, true, true);
         RenderSystem.depthFunc(515);
      }

      @Override
      public void endClipRegion() {
         RenderSystem.depthFunc(519);
         RenderSystem.colorMask(false, false, false, false);
         this.poseStack.pushPose();
         this.poseStack.translate(0.0, 0.0, -950.0);
         GuiComponent.fill(this.poseStack, 4680, 2260, -4680, -2260, -16777216);
         this.poseStack.popPose();
         RenderSystem.colorMask(true, true, true, true);
         RenderSystem.depthFunc(515);
      }
   };

   private ClipRegionStrategies() {
   }
}
