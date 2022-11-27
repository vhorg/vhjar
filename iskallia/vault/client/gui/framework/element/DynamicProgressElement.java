package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import java.util.function.Supplier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DynamicProgressElement<E extends DynamicProgressElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected boolean visible;
   protected final DynamicProgressElement.ProgressTextures textures;
   protected Supplier<Float> progressSupplier;

   public DynamicProgressElement(IPosition position, ISize size, DynamicProgressElement.ProgressTextures textures, Supplier<Float> progressSupplier) {
      super(Spatials.positionXYZ(position).size(size));
      this.textures = textures;
      this.progressSupplier = progressSupplier;
      this.setVisible(true);
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
      renderer.render(this.textures.background(), poseStack, this.worldSpatial);
      NineSlice.TextureRegion foreground = this.textures.foreground();
      int progressWidth = Mth.ceil(this.progressSupplier.get() * this.worldSpatial.width());
      renderer.render(foreground, poseStack, this.worldSpatial.x(), this.worldSpatial.y(), this.worldSpatial.z(), progressWidth, this.worldSpatial.height());
   }

   public record ProgressTextures(NineSlice.TextureRegion background, NineSlice.TextureRegion foreground) {
   }
}
