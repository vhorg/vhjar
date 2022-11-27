package iskallia.vault.client.atlas;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.gui.framework.render.NineSlice;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class AtlasMultiBuffer implements IMultiBuffer {
   private final Map<ResourceLocation, Buffer> buffers;

   public static AtlasMultiBuffer.Builder builder() {
      return new AtlasMultiBuffer.Builder(new TreeMap<>());
   }

   private AtlasMultiBuffer(Map<ResourceLocation, Buffer> buffers) {
      this.buffers = buffers;
   }

   @Override
   public void begin() {
      this.buffers.values().forEach(buffer -> buffer.bufferBuilder().begin(buffer.mode(), buffer.format()));
   }

   public VertexConsumer getFor(NineSlice.TextureRegion textureRegion) {
      return this.getFor(textureRegion.atlas().get().getAtlasResourceLocation());
   }

   public VertexConsumer getFor(TextureAtlasRegion textureAtlasRegion) {
      return this.getFor(textureAtlasRegion.atlas().get().getAtlasResourceLocation());
   }

   @Override
   public VertexConsumer getFor(ResourceLocation resourceLocation) {
      Buffer buffer = this.buffers.get(resourceLocation);
      if (buffer == null) {
         throw new IllegalStateException("No buffer registered for atlas resource location: " + resourceLocation);
      } else {
         return buffer.bufferBuilder();
      }
   }

   @Override
   public void end(Function<Buffer, Supplier<ShaderInstance>> shaderFunction) {
      this.buffers.forEach((resourceLocation, buffer) -> {
         RenderSystem.setShaderTexture(0, resourceLocation);
         RenderSystem.setShader(shaderFunction.apply(buffer));
         buffer.bufferBuilder().end();
         BufferUploader.end(buffer.bufferBuilder());
      });
   }

   public static class Builder {
      private final Map<ResourceLocation, Buffer> buffers;

      public Builder(Map<ResourceLocation, Buffer> buffers) {
         this.buffers = buffers;
      }

      public AtlasMultiBuffer.Builder add(Supplier<ITextureAtlas> atlasSupplier, Mode mode, VertexFormat format) {
         return this.add(atlasSupplier.get().getAtlasResourceLocation(), mode, format);
      }

      public AtlasMultiBuffer.Builder add(ResourceLocation atlasResourceLocation, Mode mode, VertexFormat format) {
         if (this.buffers.containsKey(atlasResourceLocation)) {
            throw new IllegalStateException("Duplicate buffer registered for atlas resource location: " + atlasResourceLocation);
         } else {
            this.buffers.put(atlasResourceLocation, new Buffer(mode, format, new BufferBuilder(256)));
            return this;
         }
      }

      public AtlasMultiBuffer create() {
         return new AtlasMultiBuffer(this.buffers);
      }
   }
}
