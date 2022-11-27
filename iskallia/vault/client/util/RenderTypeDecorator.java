package iskallia.vault.client.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Optional;
import net.minecraft.client.renderer.RenderType;

public class RenderTypeDecorator extends RenderType {
   private final RenderType decorated;
   private final Runnable afterSetup;
   private final Runnable beforeClean;

   private RenderTypeDecorator(RenderType type, Runnable afterSetup, Runnable beforeClean) {
      super(type.toString(), type.format(), type.mode(), type.bufferSize(), type.affectsCrumbling(), false, () -> {}, () -> {});
      this.decorated = type;
      this.afterSetup = afterSetup;
      this.beforeClean = beforeClean;
   }

   public static RenderTypeDecorator decorate(RenderType type) {
      return new RenderTypeDecorator(type, () -> {}, () -> {});
   }

   public static RenderTypeDecorator decorate(RenderType type, Runnable setup, Runnable clean) {
      return new RenderTypeDecorator(type, setup, clean);
   }

   public void setupRenderState() {
      this.decorated.setupRenderState();
      this.afterSetup.run();
   }

   public void clearRenderState() {
      this.beforeClean.run();
      this.decorated.clearRenderState();
   }

   public void end(BufferBuilder buf, int sortOffsetX, int sortOffsetY, int sortOffsetZ) {
      super.end(buf, sortOffsetX, sortOffsetY, sortOffsetZ);
   }

   public String toString() {
      return this.decorated.toString();
   }

   public int bufferSize() {
      return this.decorated.bufferSize();
   }

   public VertexFormat format() {
      return this.decorated.format();
   }

   public Mode mode() {
      return this.decorated.mode();
   }

   public Optional<RenderType> outline() {
      return this.decorated.outline().map(type -> decorate(type, this.afterSetup, this.beforeClean));
   }

   public boolean isOutline() {
      return this.decorated.isOutline();
   }

   public boolean affectsCrumbling() {
      return this.decorated.affectsCrumbling();
   }

   public Optional<RenderType> asOptional() {
      return Optional.of(this);
   }
}
