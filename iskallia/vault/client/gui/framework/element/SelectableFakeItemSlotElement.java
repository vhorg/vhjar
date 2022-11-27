package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SelectableFakeItemSlotElement<E extends SelectableFakeItemSlotElement<E>> extends FakeItemSlotElement<E> {
   protected Supplier<Boolean> selected = () -> false;

   public SelectableFakeItemSlotElement(ISpatial spatial) {
      super(spatial);
   }

   public SelectableFakeItemSlotElement(ISpatial spatial, Supplier<ItemStack> itemStack, Supplier<Boolean> disabled) {
      super(spatial, itemStack, disabled);
   }

   public SelectableFakeItemSlotElement(
      ISpatial spatial, Supplier<ItemStack> itemStack, TextureAtlasRegion slotTexture, TextureAtlasRegion disabledSlotTexture, Supplier<Boolean> disabled
   ) {
      super(spatial, itemStack, disabled, slotTexture, disabledSlotTexture);
   }

   public void setSelected(boolean selected) {
      this.setSelected(() -> selected);
   }

   public void setSelected(Supplier<Boolean> selected) {
      this.selected = selected;
   }

   public boolean isSelected() {
      return this.selected.get();
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      if (!this.isDisabled() && this.isSelected()) {
         renderer.render(ScreenTextures.INSET_SLOT_SELECT_FRAME, poseStack, this.worldSpatial.copy().translateXYZ(-2, -2, 400));
      }
   }
}
