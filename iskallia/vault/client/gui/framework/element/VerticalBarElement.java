package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class VerticalBarElement<E extends VerticalBarElement<E>> extends ContainerElement<E> implements IRenderedElement {
   protected final ISpatial barSpatial;
   private final Supplier<TextureAtlasRegion> barTexture;
   private final Supplier<Integer> colorSupplier;
   private final Supplier<Float> barFillPercentSupplier;

   public VerticalBarElement(ISpatial spatial, Supplier<TextureAtlasRegion> barTexture, int color, Supplier<Float> fillPercent) {
      this(spatial, barTexture, () -> color, fillPercent);
   }

   public VerticalBarElement(ISpatial spatial, Supplier<TextureAtlasRegion> barTexture, Supplier<Integer> colorSupplier, Supplier<Float> barFillPercentSupplier) {
      super(spatial);
      this.barSpatial = Spatials.positionXY(spatial.x() + 1, spatial.y() + 1).size(spatial.width() - 2, spatial.height() - 2);
      this.barTexture = barTexture;
      this.colorSupplier = colorSupplier;
      this.barFillPercentSupplier = barFillPercentSupplier;
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.zero(), ScreenTextures.INSET_GREY_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.size(parent))
      );
   }

   protected float getFillPercent() {
      return this.barFillPercentSupplier.get();
   }

   protected int getColor() {
      return this.colorSupplier.get();
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, 5.0);
      int color = this.getColor();
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      float fillPercent = this.getFillPercent();
      int fillHeight = Math.round(this.barSpatial.height() * fillPercent);
      TextureAtlasRegion texture = this.barTexture.get();
      TextureAtlasSprite sprite = texture.getSprite();
      RenderSystem.setShaderColor(r, g, b, 1.0F);
      ISpatial barFillSpatial = Spatials.copySize(this.barSpatial)
         .positionXY(this.worldSpatial)
         .translateXY(1, 1)
         .translateY(this.barSpatial.height() - fillHeight)
         .height(fillHeight);
      renderer.render(
         texture,
         poseStack,
         barFillSpatial.x(),
         barFillSpatial.y(),
         barFillSpatial.z(),
         barFillSpatial.width(),
         barFillSpatial.height(),
         sprite.getU0(),
         sprite.getU1(),
         sprite.getV1() - (sprite.getV1() - sprite.getV0()) * ((float)fillHeight / this.barSpatial.height()),
         sprite.getV1()
      );
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();
   }
}
