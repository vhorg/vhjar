package iskallia.vault.client.gui.screen;

import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.ModifierDiscoverySelectorElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.modifier.IModifierDiscoveryContainer;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ModifierDiscoveryScreenFixed<T extends AbstractContainerMenu & IModifierDiscoveryContainer> extends AbstractElementContainerScreen<T> {
   private final ModifierDiscoverySelectorElement selectorElement;

   public ModifierDiscoveryScreenFixed(T container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(181, 109));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               new TextComponent("Select modifier to discover").withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
            this.selectorElement = new ModifierDiscoverySelectorElement(
               Spatials.positionXY(7, 19).height(83), ((IModifierDiscoveryContainer)this.getMenu()).getGearModifiers()
            )
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      this.selectorElement.onSelect(gearModifier -> ((IModifierDiscoveryContainer)this.getMenu()).tryDiscoverModifier(gearModifier));
   }

   public static <T extends AbstractContainerMenu & IModifierDiscoveryContainer> ScreenConstructor<T, ModifierDiscoveryScreenFixed<T>> create() {
      return (x$0, x$1, x$2) -> new ModifierDiscoveryScreenFixed((T)x$0, x$1, x$2);
   }
}
