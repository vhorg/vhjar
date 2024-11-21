package iskallia.vault.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.shader.ShaderChain;
import iskallia.vault.client.shader.glsl.NativeGrayscaleShader;
import iskallia.vault.init.ModShaders;

public class ShaderChainRenderer {
   @Deprecated
   public static void renderGrayscale(PoseStack poseStack, float grayscale, float brightness, Runnable render) {
      PoseStack renderStack = RenderSystem.getModelViewStack();
      renderStack.pushPose();
      renderStack.mulPoseMatrix(poseStack.last().pose());
      RenderSystem.applyModelViewMatrix();
      ShaderChain grayscaleChain = ModShaders.getGrayscaleShaderChain();
      grayscaleChain.render(render, shader -> {
         if (shader instanceof NativeGrayscaleShader grayscaleShader) {
            grayscaleShader.withGrayscale(grayscale).withBrightness(brightness);
         }
      });
      renderStack.popPose();
      RenderSystem.applyModelViewMatrix();
   }
}
