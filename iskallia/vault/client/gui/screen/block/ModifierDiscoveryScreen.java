package iskallia.vault.client.gui.screen.block;

import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.ModifierDiscoverySelectorElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.ModifierDiscoveryContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class ModifierDiscoveryScreen extends AbstractElementContainerScreen<ModifierDiscoveryContainer> {
   private final ModifierDiscoverySelectorElement selectorElement;

   public ModifierDiscoveryScreen(ModifierDiscoveryContainer container, Inventory inventory, Component title) {
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
               Spatials.positionXY(7, 19).height(83), ((ModifierDiscoveryContainer)this.getMenu()).getGearModifiers()
            )
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      this.selectorElement.onSelect(gearModifier -> ((ModifierDiscoveryContainer)this.getMenu()).discoverModifier(gearModifier));
   }
}
