package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackDisplayElement<E extends ItemStackDisplayElement<E>> extends ElasticContainerElement<E> {
   private final ItemStack display;
   private float scale = 1.0F;

   public ItemStackDisplayElement(ISpatial spatial, ItemStack display) {
      super(spatial);
      this.display = display;
   }

   public ItemStack getDisplay() {
      return this.display;
   }

   public float getScale() {
      return this.scale;
   }

   public void setScale(float scale) {
      this.scale = scale;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      float scale = this.getScale();
      PoseStack mvStack = RenderSystem.getModelViewStack();
      mvStack.pushPose();
      mvStack.translate(this.worldSpatial.x(), this.worldSpatial.y(), this.worldSpatial.z());
      mvStack.scale(scale, scale, 1.0F);
      ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
      ir.renderGuiItem(this.getDisplay(), 0, 0);
      mvStack.popPose();
      RenderSystem.applyModelViewMatrix();
   }
}
