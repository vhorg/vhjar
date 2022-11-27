package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.PlayerFaceElement;
import iskallia.vault.client.gui.framework.element.RenderIndexedElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StatTabContainerElement<E extends StatTabContainerElement<E>> extends ElasticContainerElement<E> {
   private int selectedIndex;

   public StatTabContainerElement(IPosition position, Consumer<Integer> selectedIndexChangeAction) {
      super(Spatials.positionXYZ(position));
      ObservableSupplier<Integer> selectedIndexObserver = ObservableSupplier.of(() -> this.selectedIndex, Integer::equals);
      selectedIndexObserver.ifChanged(selectedIndexChangeAction);
      this.addElement(
         new StatTabContainerElement.StatTabElement(
            Spatials.positionZ(position).size(31, 28),
            new PlayerFaceElement(Spatials.positionXYZ(7, 8, position.z() + 1).size(12, 12), PlayerFaceElement::getLocalPlayerName),
            () -> this.selectedIndex == 0,
            () -> {
               this.selectedIndex = 0;
               selectedIndexObserver.ifChanged(selectedIndexChangeAction);
            }
         )
      );
      this.addElement(
         new StatTabContainerElement.StatTabElement(
            Spatials.positionY(31).positionZ(position).size(31, 28),
            new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_PORTAL_VAULT),
            () -> this.selectedIndex == 1,
            () -> {
               this.selectedIndex = 1;
               selectedIndexObserver.ifChanged(selectedIndexChangeAction);
            }
         )
      );
   }

   private static class StatTabElement extends ElasticContainerElement<StatTabContainerElement.StatTabElement> {
      private final Runnable onClick;

      public StatTabElement(ISpatial spatial, IRenderedElement iconElement, Supplier<Boolean> selected, Runnable onClick) {
         super(spatial);
         this.onClick = onClick;
         this.addElement(
            new RenderIndexedElement(
               Spatials.zero(),
               List.of(
                  new TextureAtlasElement(Spatials.positionX(3), ScreenTextures.TAB_BACKGROUND_RIGHT),
                  new TextureAtlasElement(ScreenTextures.TAB_BACKGROUND_RIGHT_SELECTED)
               ),
               () -> selected.get() ? 1 : 0
            )
         );
         this.addElement(iconElement);
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
         this.onClick.run();
         return true;
      }
   }
}
