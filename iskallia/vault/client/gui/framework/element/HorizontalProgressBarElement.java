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

public class HorizontalProgressBarElement<E extends HorizontalProgressBarElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected final TextureAtlasRegion background;
   protected final TextureAtlasRegion foreground;
   protected final Supplier<Float> percentageSupplier;
   protected final HorizontalProgressBarElement.Direction direction;
   protected boolean visible;

   public HorizontalProgressBarElement(IPosition position, TextureAtlasRegion background, TextureAtlasRegion foreground, Supplier<Float> percentageSupplier) {
      this(position, background, foreground, percentageSupplier, HorizontalProgressBarElement.Direction.LEFT_TO_RIGHT);
   }

   public HorizontalProgressBarElement(
      IPosition position,
      TextureAtlasRegion background,
      TextureAtlasRegion foreground,
      Supplier<Float> percentageSupplier,
      HorizontalProgressBarElement.Direction direction
   ) {
      super(Spatials.positionXYZ(position).size(background));
      this.background = background;
      this.foreground = foreground;
      this.percentageSupplier = percentageSupplier;
      this.direction = direction;
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
      renderer.render(this.background, poseStack, this.worldSpatial);
      TextureAtlasSprite sprite = this.foreground.getSprite();
      float percentFull = Mth.clamp(this.percentageSupplier.get(), 0.0F, 1.0F);
      int width = Math.round(this.foreground.width() * percentFull);
      if (this.direction == HorizontalProgressBarElement.Direction.LEFT_TO_RIGHT) {
         renderer.render(
            this.foreground,
            poseStack,
            this.worldSpatial.x(),
            this.worldSpatial.y(),
            this.worldSpatial.z(),
            width,
            this.foreground.height(),
            sprite.getU0(),
            this.foreground.getU1(percentFull),
            sprite.getV0(),
            sprite.getV1()
         );
      } else if (this.direction == HorizontalProgressBarElement.Direction.RIGHT_TO_LEFT) {
         renderer.render(
            this.foreground,
            poseStack,
            this.worldSpatial.x() + (this.foreground.width() - width),
            this.worldSpatial.y(),
            this.worldSpatial.z(),
            width,
            this.foreground.height(),
            this.foreground.getU0(percentFull),
            sprite.getU1(),
            sprite.getV0(),
            sprite.getV1()
         );
      }
   }

   public static enum Direction {
      RIGHT_TO_LEFT,
      LEFT_TO_RIGHT;
   }
}
