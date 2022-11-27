package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class OutputSlotElement<E extends OutputSlotElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected final Slot outputSlot;
   protected final TextureAtlasRegion background;
   protected boolean visible;

   public OutputSlotElement(IPosition position, Slot outputSlot, TextureAtlasRegion background) {
      super(Spatials.positionXYZ(position).size(26, 26));
      this.outputSlot = outputSlot;
      this.background = background;
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
      renderer.render(
         this.background, poseStack, this.outputSlot.x + this.worldSpatial.x() - 5, this.outputSlot.y + this.worldSpatial.y() - 5, this.worldSpatial.z()
      );
   }
}
