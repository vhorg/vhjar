package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.summary.VaultExitContainerScreenData;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class XpWithRewardDisplayElement<E extends XpWithRewardDisplayElement<E>> extends ContainerElement<E> {
   public XpWithRewardDisplayElement(IPosition position, Component name, int width, VaultExitContainerScreenData screenData) {
      super(Spatials.positionXYZ(position).size(width, 24));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(5, 0, 3).size(24, 24), ScreenTextures.VAULT_EXIT_ELEMENT_ICON)
            .layout((screen, gui, parent, world) -> world.size(24, 24))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXYZ(0, 2, 2).size(width, 20), ScreenTextures.VAULT_EXIT_ELEMENT_TITLE)
            .layout((screen, gui, parent, world) -> world.size(width, 20))
      );
      this.addElement(
         new XpWithRewardDisplayElement.StringElement(
            Spatials.positionXYZ(32, 8, 5), Spatials.size(16, 7), (Supplier<Component>)(() -> name), LabelTextStyle.shadow().left()
         )
      );
   }

   private static final class StringElement extends DynamicLabelElement<Component, XpWithRewardDisplayElement.StringElement> {
      private StringElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }
}
