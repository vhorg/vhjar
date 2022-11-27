package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemTooltipElement<E extends ItemTooltipElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   private final ITooltipRenderer tooltipRenderer;
   private final Supplier<ItemStack> stackTooltipSupplier;
   private Function<ItemStack, List<Component>> tooltipFunction;
   protected boolean visible;

   public ItemTooltipElement(ISpatial spatial, ITooltipRenderer tooltipRenderer, ItemStack stack) {
      this(spatial, tooltipRenderer, (Supplier<ItemStack>)(() -> stack));
   }

   public ItemTooltipElement(ISpatial spatial, ITooltipRenderer tooltipRenderer, Supplier<ItemStack> stackTooltipSupplier) {
      super(spatial);
      this.tooltipRenderer = tooltipRenderer;
      this.stackTooltipSupplier = stackTooltipSupplier;
      this.tooltipFunction = this.tooltipRenderer::getTooltipFromItem;
      this.setVisible(true);
   }

   public E setTooltipFunction(Function<ItemStack, List<Component>> tooltipFunction) {
      this.tooltipFunction = tooltipFunction;
      return (E)this;
   }

   public ItemStack getTooltipStack() {
      return this.stackTooltipSupplier.get();
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
      ItemStack tooltipStack = this.getTooltipStack();
      if (!tooltipStack.isEmpty()) {
         this.tooltipRenderer
            .renderComponentTooltip(
               poseStack, this.tooltipFunction.apply(tooltipStack), this.getWorldSpatial().x() - 12, this.getWorldSpatial().y() + 12, TooltipDirection.RIGHT
            );
      }
   }
}
