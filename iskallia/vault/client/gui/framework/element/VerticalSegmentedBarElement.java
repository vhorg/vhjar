package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class VerticalSegmentedBarElement<E extends VerticalSegmentedBarElement<E, S>, S extends VerticalSegmentedBarElement.BarSegment>
   extends VerticalBarElement<E> {
   private final Map<Float, S> segmentMap = new TreeMap<>(Comparator.reverseOrder());

   public VerticalSegmentedBarElement(ISpatial spatial, Supplier<TextureAtlasRegion> barTexture, Supplier<Float> fillPercent) {
      super(spatial, barTexture, Color.WHITE::getRGB, fillPercent);
   }

   public void addSegment(float percent, S segment) {
      this.segmentMap.put(percent, segment);
   }

   @Override
   protected int getColor() {
      return this.getCurrentSegment().map(VerticalSegmentedBarElement.BarSegment::getColor).orElse(super.getColor());
   }

   public Optional<S> getNextSegment() {
      S previousSegment = null;
      float fillPercent = this.getFillPercent();

      for (Float key : this.segmentMap.keySet()) {
         if (key <= fillPercent) {
            return Optional.ofNullable(previousSegment);
         }

         previousSegment = this.segmentMap.get(key);
      }

      return Optional.ofNullable(previousSegment);
   }

   public Optional<S> getCurrentSegment() {
      float fillPercent = this.getFillPercent();

      for (Float key : this.segmentMap.keySet()) {
         if (key <= fillPercent) {
            return Optional.of(this.segmentMap.get(key));
         }
      }

      return Optional.empty();
   }

   @Override
   public boolean onHoverTooltip(ITooltipRenderer tooltipRenderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, TooltipFlag tooltipFlag) {
      List<Component> tooltip = this.getCurrentSegment().map(VerticalSegmentedBarElement.BarSegment::getTooltipComponents).orElseGet(Collections::emptyList);
      if (!tooltip.isEmpty()) {
         List<Component> renderTooltip = new ArrayList<>(tooltip);
         List<Component> nextTooltip = this.getNextSegment()
            .map(VerticalSegmentedBarElement.BarSegment::getTooltipComponents)
            .orElseGet(Collections::emptyList);
         if (!nextTooltip.isEmpty()) {
            renderTooltip.add(TextComponent.EMPTY);
            this.getCurrentSegment().ifPresent(segment -> renderTooltip.add(segment.getNextComponentDescriptor()));
            renderTooltip.addAll(nextTooltip);
         }

         tooltipRenderer.renderTooltip(poseStack, renderTooltip, mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
         return true;
      } else {
         return super.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag);
      }
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, 10.0);
      this.segmentMap
         .forEach(
            (percent, segment) -> {
               if (!(percent <= 0.0F)) {
                  int percentOffset = this.barSpatial.height() - Math.round(this.barSpatial.height() * percent);
                  renderer.renderColoredQuad(
                     poseStack,
                     654311424,
                     Spatials.positionXY(this.worldSpatial.x() + 1, this.worldSpatial.y() + percentOffset).size(this.barSpatial.width(), 1)
                  );
               }
            }
         );
      poseStack.popPose();
      RenderSystem.disableBlend();
   }

   public static class BarSegment {
      private final int color;

      public BarSegment(int color) {
         this.color = color;
      }

      protected int getColor() {
         return this.color;
      }

      protected Component getNextComponentDescriptor() {
         return new TextComponent("Next:").withStyle(ChatFormatting.BOLD);
      }

      protected List<Component> getTooltipComponents() {
         return Collections.emptyList();
      }
   }
}
