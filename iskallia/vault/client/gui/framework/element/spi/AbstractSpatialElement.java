package iskallia.vault.client.gui.framework.element.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.IDebugRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.spatial.LayoutDebugLoggers;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ILayoutDebugLogger;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSpatialElement<E extends AbstractSpatialElement<E>> implements ILayoutElement<E>, ITooltipElement {
   protected final ISpatial fixedSpatial;
   protected final IMutableSpatial worldSpatial;
   protected final ISpatial unmodifiableWorldSpatial;
   protected ITooltipRenderFunction tooltipRenderFunction;
   protected ILayoutStrategy layoutStrategy;
   private boolean enabled;
   protected ILayoutDebugLogger layoutDebugLogger;
   private byte spatialDebugRender;
   private static final int DEBUG_SPATIAL_FIXED_COLOR = TextColor.parseColor("#FF0000").getValue();
   private static final int DEBUG_SPATIAL_WORLD_COLOR = TextColor.parseColor("#00FF00").getValue();

   public AbstractSpatialElement(ISpatial spatial) {
      this.fixedSpatial = Spatials.copy(spatial).unmodifiableView();
      this.worldSpatial = Spatials.copy(this.fixedSpatial);
      this.unmodifiableWorldSpatial = this.worldSpatial.unmodifiableView();
      this.tooltipRenderFunction = ITooltipRenderFunction.NONE;
      this.layoutStrategy = ILayoutStrategy.NONE;
      this.enabled = true;
      this.layoutDebugLogger = LayoutDebugLoggers.NONE;
   }

   @Override
   public void onLayout(ISize screen, ISpatial gui, ISpatial parent) {
      this.layoutSelf(screen, gui, parent);
   }

   protected void layoutSelf(ISize screen, ISpatial gui, ISpatial parent) {
      this.worldSpatial.set(this.fixedSpatial).translateXYZ(parent);
      this.layoutDebugLogger.out("[{}] fixed = {}", this.getClass().getSimpleName(), this.fixedSpatial);
      this.layoutDebugLogger.out("[{}] parent = {}", this.getClass().getSimpleName(), parent);
      this.layoutDebugLogger.out("[{}: world.set(fixed).translate(parent)] -> world = {}", this.getClass().getSimpleName(), this.worldSpatial);
      this.layoutStrategy.apply(screen, gui, parent, this.worldSpatial);
      this.layoutDebugLogger.out("[{}: layout] -> world = {}", this.getClass().getSimpleName(), this.worldSpatial);
   }

   public E layout(ILayoutStrategy layoutStrategy) {
      this.layoutStrategy = layoutStrategy;
      return (E)this;
   }

   @Override
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   public boolean isEnabled() {
      return this.enabled;
   }

   public E tooltip(Supplier<Component> componentSupplier) {
      this.tooltipRenderFunction = Tooltips.single(componentSupplier);
      return (E)this;
   }

   public E tooltip(ITooltipRenderFunction tooltipRenderFunction) {
      this.tooltipRenderFunction = tooltipRenderFunction;
      return (E)this;
   }

   @Override
   public boolean onHoverTooltip(ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, TooltipFlag tooltipFlag) {
      return this.tooltipRenderFunction.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag);
   }

   @Override
   public final ISpatial getFixedSpatial() {
      return this.fixedSpatial;
   }

   @Override
   public final ISpatial getWorldSpatial() {
      return this.unmodifiableWorldSpatial;
   }

   @Override
   public final int x() {
      return this.worldSpatial.x();
   }

   @Override
   public final int y() {
      return this.worldSpatial.y();
   }

   @Override
   public final int z() {
      return this.worldSpatial.z();
   }

   @Override
   public final int width() {
      return this.worldSpatial.width();
   }

   @Override
   public final int height() {
      return this.worldSpatial.height();
   }

   @Override
   public final boolean contains(double x, double y) {
      return this.worldSpatial.contains(x, y);
   }

   @Override
   public final int right() {
      return this.worldSpatial.right();
   }

   @Override
   public final int left() {
      return this.worldSpatial.left();
   }

   @Override
   public final int top() {
      return this.worldSpatial.top();
   }

   @Override
   public final int bottom() {
      return this.worldSpatial.bottom();
   }

   @Override
   public final ISpatial unmodifiableView() {
      return this.unmodifiableWorldSpatial;
   }

   @Override
   public void enableLayoutDebugLogging() {
      this.layoutDebugLogger = LayoutDebugLoggers.getModLogger();
   }

   public E enableSpatialDebugRender(boolean fixed, boolean world) {
      this.spatialDebugRender = (byte)((fixed ? 1 : 0) | (world ? 2 : 0));
      return (E)this;
   }

   @Override
   public void renderDebug(IDebugRenderer debugRenderer, PoseStack poseStack) {
      if ((this.spatialDebugRender & 1) == 1) {
         debugRenderer.renderSpatial(poseStack, this.getFixedSpatial(), DEBUG_SPATIAL_FIXED_COLOR);
      }

      if ((this.spatialDebugRender & 2) == 2) {
         debugRenderer.renderSpatial(poseStack, this.worldSpatial, DEBUG_SPATIAL_WORLD_COLOR);
      }
   }
}
