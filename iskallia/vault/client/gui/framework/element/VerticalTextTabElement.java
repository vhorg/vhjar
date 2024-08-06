package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class VerticalTextTabElement<E extends VerticalTextTabElement<E>> extends ElasticContainerElement<E> {
   private LabelTextStyle textStyle = LabelTextStyle.defaultStyle().build();
   private final NineSlice.TextureRegion activeBackground;
   private final NineSlice.TextureRegion inactiveBackground;
   private final List<VerticalTextTabElement.SelectableTab> tabs;
   private VerticalTextTabElement.SelectableTab selectedTab = null;

   public VerticalTextTabElement(
      ISpatial offset, List<VerticalTextTabElement.TabInfo> tabs, NineSlice.TextureRegion activeBackground, NineSlice.TextureRegion inactiveBackground
   ) {
      super(computeSize(offset, tabs));
      this.activeBackground = activeBackground;
      this.inactiveBackground = inactiveBackground;
      this.tabs = makeTabs(offset, tabs);
      this.tabs.forEach(tab -> {
         tab.parent = this;
         this.addElement(tab);
      });
      this.tabs.stream().findFirst().ifPresent(this::selectTab);
   }

   private static ISpatial computeSize(ISpatial offset, List<VerticalTextTabElement.TabInfo> tabs) {
      IMutableSpatial spatial = Spatials.copyPosition(offset);

      for (VerticalTextTabElement.TabInfo tab : tabs) {
         ISpatial tabBox = VerticalTextTabElement.SelectableTab.getSize(tab.text, Minecraft.getInstance().font);
         spatial.width(Math.max(spatial.width(), tabBox.width()));
         spatial.height(spatial.height() + tabBox.height());
      }

      return spatial;
   }

   private static List<VerticalTextTabElement.SelectableTab> makeTabs(ISpatial spatial, List<VerticalTextTabElement.TabInfo> tabs) {
      IMutableSpatial offset = Spatials.copyPosition(spatial);
      List<VerticalTextTabElement.SelectableTab> tabElements = new ArrayList<>();

      for (VerticalTextTabElement.TabInfo info : tabs) {
         ISpatial tabBox = VerticalTextTabElement.SelectableTab.getSize(info.text, Minecraft.getInstance().font);
         VerticalTextTabElement.SelectableTab tab = new VerticalTextTabElement.SelectableTab(
            Spatials.copyPosition(offset).size(tabBox), info.text, info.onSelect, info.onDeselect
         );
         tabElements.add(tab);
         offset.translateY(tabBox.height());
      }

      return tabElements;
   }

   public void setTextStyle(LabelTextStyle textStyle) {
      this.textStyle = textStyle;
   }

   public void selectTab(VerticalTextTabElement.SelectableTab tab) {
      if (this.tabs.contains(tab)) {
         if (this.selectedTab != null) {
            this.selectedTab.onDeselect.run();
         }

         this.selectedTab = tab;
         this.selectedTab.onSelect.run();
      }
   }

   public VerticalTextTabElement.SelectableTab getSelectedTab() {
      return this.selectedTab;
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      ISpatial offset = this.getWorldSpatial();

      for (VerticalTextTabElement.SelectableTab tab : this.tabs) {
         ISpatial tabBox = tab.getWorldSpatial();
         poseStack.pushPose();
         poseStack.translate(offset.x(), offset.y(), offset.z());
         if (tab == this.selectedTab) {
            poseStack.translate(0.0, 0.0, 100.0);
            renderer.render(this.activeBackground, poseStack, Spatials.size(tabBox));
         } else {
            poseStack.translate(0.0, 0.0, -100.0);
            renderer.render(this.inactiveBackground, poseStack, Spatials.size(tabBox));
         }

         poseStack.pushPose();
         poseStack.translate(5.0, tabBox.height() - 5, 0.0);
         poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
         this.textStyle
            .textBorder()
            .render(
               renderer,
               poseStack,
               tab.getText().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               this.textStyle.textWrap(),
               this.textStyle.textAlign(),
               0,
               0,
               0,
               Integer.MAX_VALUE
            );
         poseStack.popPose();
         poseStack.popPose();
         offset = Spatials.copy(offset).translateY(tabBox.height());
      }
   }

   private boolean onSelect(VerticalTextTabElement.SelectableTab tab) {
      this.selectTab(tab);
      return true;
   }

   private static class SelectableTab extends AbstractSpatialElement<VerticalTextTabElement.SelectableTab> implements IGuiEventElement {
      VerticalTextTabElement<?> parent = null;
      private final MutableComponent text;
      final Runnable onSelect;
      final Runnable onDeselect;

      public SelectableTab(ISpatial spatial, MutableComponent text, Runnable onSelect, Runnable onDeselect) {
         super(spatial);
         this.text = text;
         this.onSelect = onSelect;
         this.onDeselect = onDeselect;
      }

      void moveTo(ISpatial offset) {
         this.worldSpatial.positionXYZ(offset);
      }

      public MutableComponent getText() {
         return this.text;
      }

      public static ISpatial getSize(MutableComponent text, Font font) {
         int textWidth = font.width(text);
         ISpatial textBox = Spatials.size(9, textWidth);
         return Spatials.size(textBox.width() + 8, textBox.height() + 9);
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
         return this.parent != null ? this.parent.onSelect(this) : IGuiEventElement.super.onMouseClicked(mouseX, mouseY, buttonIndex);
      }
   }

   public record TabInfo(MutableComponent text, Runnable onSelect, Runnable onDeselect) {
      public TabInfo(MutableComponent text, Runnable onSelect) {
         this(text, onSelect, () -> {});
      }
   }
}
