package iskallia.vault.client.gui.screen.player;

import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.player.element.SkillTabContainerElement;
import iskallia.vault.container.spi.AbstractElementContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractSkillTabElementContainerScreen<C extends AbstractElementContainer> extends AbstractElementContainerScreen<C> {
   public AbstractSkillTabElementContainerScreen(C container, Inventory inventory, Component title, IElementRenderer elementRenderer) {
      super(container, inventory, title, elementRenderer, ScreenTooltipRenderer::create);
      this.addElement(
         (SkillTabContainerElement)new SkillTabContainerElement(Spatials.positionXYZ(15, -28, 1), this.getTabIndex())
            .layout((screen, gui, parent, world) -> world.translateXY(this.getTabContentSpatial()))
      );
      this.addElement(
         new LabelElement(
            Spatials.positionXY(this.getTabContentSpatial()).translateXYZ(8, 6, 2),
            this.getTabTitle().withStyle(ChatFormatting.BLACK),
            LabelTextStyle.defaultStyle()
         )
      );
   }

   public ISpatial getTabContentSpatial() {
      int padLeft = 21;
      int padTop = 42;
      int padBottom = 30;
      int width = this.width - padLeft * 2;
      int height = this.height - padBottom - padTop;
      return Spatials.positionXY(padLeft, padTop).size(width, height);
   }

   public abstract int getTabIndex();

   public abstract MutableComponent getTabTitle();
}
