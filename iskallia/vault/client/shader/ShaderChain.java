package iskallia.vault.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.shader.glsl.NativeShader;
import iskallia.vault.event.event.WindowResizeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@EventBusSubscriber({Dist.CLIENT})
public class ShaderChain {
   private static final List<ShaderChain> globalChains = new ArrayList<>();
   private final List<NativeShader> shaders = new ArrayList<>();
   private int[] frameBuffers;
   private int[] colorTextures;
   private int[] depthTextures;
   private int width = 0;
   private int height = 0;

   private ShaderChain(List<NativeShader> shaders) {
      this.shaders.addAll(shaders);
      globalChains.add(this);
   }

   public static ShaderChain.Builder builder() {
      return new ShaderChain.Builder();
   }

   private void build() {
      if (this.shaders.isEmpty()) {
         throw new IllegalStateException("Cannot build chain with no shaders.");
      } else {
         Window gameWindow = Minecraft.getInstance().getWindow();
         this.width = gameWindow.getWidth();
         this.height = gameWindow.getHeight();
         int layers = this.shaders.size() + 1;
         this.frameBuffers = new int[layers];
         this.colorTextures = new int[layers];
         this.depthTextures = new int[layers];
         GL30.glGenFramebuffers(this.frameBuffers);
         GL11.glGenTextures(this.colorTextures);
         GL11.glGenTextures(this.depthTextures);

         for (int i = 0; i < this.frameBuffers.length; i++) {
            int bufId = this.frameBuffers[i];
            int colorId = this.colorTextures[i];
            int depthId = this.depthTextures[i];
            GlStateManager._glBindFramebuffer(36160, bufId);
            GlStateManager._bindTexture(depthId);
            GlStateManager._texParameter(3553, 10241, 9728);
            GlStateManager._texParameter(3553, 10240, 9728);
            GlStateManager._texParameter(3553, 34892, 0);
            GlStateManager._texParameter(3553, 10242, 33071);
            GlStateManager._texParameter(3553, 10243, 33071);
            GlStateManager._texImage2D(3553, 0, 36013, this.width, this.height, 0, 34041, 36269, null);
            GlStateManager._bindTexture(colorId);
            GlStateManager._texParameter(3553, 10241, 9729);
            GlStateManager._texParameter(3553, 10240, 9729);
            GlStateManager._texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, null);
            GlStateManager._glBindFramebuffer(36160, bufId);
            GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, colorId, 0);
            GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, depthId, 0);
            GlStateManager._glFramebufferTexture2D(36160, 36128, 3553, depthId, 0);
            int fbStatus = GL30.glCheckFramebufferStatus(36160);
            if (fbStatus != 36053) {
               throw new IllegalStateException("Framebuffer init failed. " + fbStatus);
            }

            this.clearFramebuffer(bufId);
         }
      }
   }

   public void render(Runnable renderCall, Consumer<NativeShader> preShaderRun) {
      if (this.frameBuffers != null) {
         int currentFramebufferId = GL11.glGetInteger(36006);
         this.copyDepthFromBuffer(currentFramebufferId, this.width, this.height);
         int prevTexture = -1;

         for (int i = 0; i < this.frameBuffers.length; i++) {
            GlStateManager._glBindFramebuffer(36160, this.frameBuffers[i]);
            GlStateManager._clear(16640, Minecraft.ON_OSX);
            if (i == 0) {
               renderCall.run();
            } else if (prevTexture != -1) {
               GlStateManager._bindTexture(prevTexture);
               RenderSystem.setShaderTexture(0, prevTexture);
               RenderSystem.enableTexture();
               RenderSystem.disableDepthTest();
               RenderSystem.disableCull();
               Tesselator tesselator = RenderSystem.renderThreadTesselator();
               BufferBuilder bufferbuilder = tesselator.getBuilder();
               bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               bufferbuilder.vertex(-this.width / 2, -this.height / 2, 0.0).uv(0.0F, 0.0F).endVertex();
               bufferbuilder.vertex(-this.width / 2, this.height / 2, 0.0).uv(0.0F, 1.0F).endVertex();
               bufferbuilder.vertex(this.width / 2, this.height / 2, 0.0).uv(1.0F, 1.0F).endVertex();
               bufferbuilder.vertex(this.width / 2, -this.height / 2, 0.0).uv(1.0F, 0.0F).endVertex();
               bufferbuilder.end();
               BufferUploader._endInternal(bufferbuilder);
               RenderSystem.enableCull();
               RenderSystem.enableDepthTest();
               RenderSystem.disableTexture();
            }

            prevTexture = this.colorTextures[i];
         }

         this.unbindFramebuffer();
         this.copyAllInternal(this.frameBuffers[1], Minecraft.getInstance().getMainRenderTarget().frameBufferId);
         GlStateManager._glBindFramebuffer(36160, currentFramebufferId);

         for (int bufId : this.frameBuffers) {
            this.clearFramebuffer(bufId);
         }

         GlStateManager._glBindFramebuffer(36160, currentFramebufferId);
      }
   }

   public void copyColorDepthFromBuffer(int otherBuffer, int otherWidth, int otherHeight) {
      this.copyFromBuffer(otherBuffer, otherWidth, otherHeight, 16640);
   }

   public void copyDepthFromBuffer(int otherBuffer, int otherWidth, int otherHeight) {
      this.copyFromBuffer(otherBuffer, otherWidth, otherHeight, 256);
   }

   public void copyFromBuffer(int otherBuffer, int otherWidth, int otherHeight, int copyMask) {
      if (this.frameBuffers != null) {
         this.copyInternal(otherBuffer, otherWidth, otherHeight, copyMask, this.frameBuffers[0]);
      }
   }

   private void copyAllInternal(int buffer, int targetBuffer) {
      this.copyInternal(buffer, this.width, this.height, 16640, targetBuffer);
   }

   private void copyInternal(int buffer, int otherWidth, int otherHeight, int copyMask, int targetBuffer) {
      GlStateManager._glBindFramebuffer(36008, buffer);
      GlStateManager._glBindFramebuffer(36009, targetBuffer);
      GlStateManager._glBlitFrameBuffer(0, 0, otherWidth, otherHeight, 0, 0, this.width, this.height, copyMask, 9728);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   private void bindFramebuffer(int bufId, boolean setViewport) {
      GlStateManager._glBindFramebuffer(36160, bufId);
      if (setViewport) {
         GlStateManager._viewport(0, 0, this.width, this.height);
      }
   }

   private void unbindFramebuffer() {
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   private void clearFramebuffer(int bufId) {
      this.bindFramebuffer(bufId, false);
      GlStateManager._clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GlStateManager._clearDepth(1.0);
      GlStateManager._clear(16640, Minecraft.ON_OSX);
      this.unbindFramebuffer();
   }

   private void clean() {
      if (this.frameBuffers != null) {
         GlStateManager._bindTexture(0);
         this.unbindFramebuffer();
         GL11.glDeleteTextures(this.colorTextures);
         GL11.glDeleteTextures(this.depthTextures);
         GL30.glDeleteFramebuffers(this.frameBuffers);
         this.frameBuffers = null;
      }
   }

   public void destroy() {
      this.clean();
      globalChains.remove(this);
   }

   @SubscribeEvent
   public static void onResize(WindowResizeEvent event) {
      globalChains.forEach(chain -> {
         chain.clean();
         chain.build();
      });
   }

   public static class Builder {
      private final List<NativeShader> shaders = new ArrayList<>();

      private Builder() {
      }

      public ShaderChain.Builder addShader(NativeShader shader) {
         this.shaders.add(shader);
         return this;
      }

      public ShaderChain build() {
         ShaderChain chain = new ShaderChain(this.shaders);
         chain.build();
         return chain;
      }
   }
}
