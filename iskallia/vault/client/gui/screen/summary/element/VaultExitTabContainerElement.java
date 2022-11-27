package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
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

public class VaultExitTabContainerElement<E extends VaultExitTabContainerElement<E>> extends ElasticContainerElement<E> {
   private int selectedIndex;

   public VaultExitTabContainerElement(IPosition position, Consumer<Integer> selectedIndexChangeAction, boolean isCoop) {
      super(Spatials.positionXYZ(position));
      ObservableSupplier<Integer> selectedIndexObserver = ObservableSupplier.of(() -> this.selectedIndex, Integer::equals);
      selectedIndexObserver.ifChanged(selectedIndexChangeAction);
      this.addElement(
         new VaultExitTabContainerElement.StatTabElement(
            Spatials.positionZ(position).size(31, 28),
            new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_PORTAL_VAULT),
            () -> this.selectedIndex == 0,
            () -> {
               this.selectedIndex = 0;
               selectedIndexObserver.ifChanged(selectedIndexChangeAction);
            },
            false
         )
      );
      this.addElement(
         new VaultExitTabContainerElement.StatTabElement(
            Spatials.positionY(31).positionZ(position).size(31, 28),
            new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_CRYSTAL),
            () -> this.selectedIndex == 1,
            () -> {
               this.selectedIndex = 1;
               selectedIndexObserver.ifChanged(selectedIndexChangeAction);
            },
            false
         )
      );
      this.addElement(
         new VaultExitTabContainerElement.StatTabElement(
            Spatials.positionY(62).positionZ(position).size(31, 28),
            new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_LOOT),
            () -> this.selectedIndex == 2,
            () -> {
               this.selectedIndex = 2;
               selectedIndexObserver.ifChanged(selectedIndexChangeAction);
            },
            false
         )
      );
      this.addElement(
         new VaultExitTabContainerElement.StatTabElement(
            Spatials.positionY(93).positionZ(position).size(31, 28),
            new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_MOBS_KILLED),
            () -> this.selectedIndex == 3,
            () -> {
               this.selectedIndex = 3;
               selectedIndexObserver.ifChanged(selectedIndexChangeAction);
            },
            false
         )
      );
      VaultExitTabContainerElement.StatTabElement element = this.addElement(
         new VaultExitTabContainerElement.StatTabElement(
            Spatials.positionY(124).positionZ(position).size(31, 28),
            new TextureAtlasElement(Spatials.positionXYZ(4, 6, position.z() + 1), ScreenTextures.TAB_ICON_COOP),
            () -> this.selectedIndex == 4,
            () -> {
               this.selectedIndex = 4;
               selectedIndexObserver.ifChanged(selectedIndexChangeAction);
            },
            !isCoop
         )
      );
      element.setEnabled(isCoop);
   }

   private static class StatTabElement extends ElasticContainerElement<VaultExitTabContainerElement.StatTabElement> {
      private final Runnable onClick;

      public StatTabElement(ISpatial spatial, IRenderedElement iconElement, Supplier<Boolean> selected, Runnable onClick, boolean disabled) {
         super(spatial);
         this.onClick = onClick;
         this.addElement(
            new RenderIndexedElement(
               Spatials.zero(),
               List.of(
                  new TextureAtlasElement(Spatials.positionX(3), ScreenTextures.TAB_BACKGROUND_RIGHT),
                  new TextureAtlasElement(ScreenTextures.TAB_BACKGROUND_RIGHT_SELECTED),
                  new TextureAtlasElement(Spatials.positionX(3), ScreenTextures.TAB_BACKGROUND_RIGHT_DISABLED)
               ),
               () -> disabled ? 2 : (selected.get() ? 1 : 0)
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
