package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ProgressElement<E extends ProgressElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected boolean visible;
   protected final ProgressElement.ProgressTextures textures;
   protected Supplier<Float> progressSupplier;

   public ProgressElement(IPosition position, ProgressElement.ProgressTextures textures, Supplier<Float> progressSupplier) {
      super(Spatials.positionXYZ(position).size(textures.background().size()));
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
      TextureAtlasRegion foreground = this.textures.foreground();
      TextureAtlasSprite sprite = foreground.getSprite();
      int progressWidth = Mth.ceil(this.progressSupplier.get() * foreground.width());
      float widthProgress = (float)progressWidth / foreground.width();
      renderer.render(
         this.textures.foreground(),
         poseStack,
         this.worldSpatial.x(),
         this.worldSpatial.y(),
         this.worldSpatial.z(),
         progressWidth,
         foreground.height(),
         sprite.getU0(),
         foreground.getU1(widthProgress),
         sprite.getV0(),
         sprite.getV1()
      );
   }

   public record ProgressTextures(TextureAtlasRegion background, TextureAtlasRegion foreground) {
   }
}
