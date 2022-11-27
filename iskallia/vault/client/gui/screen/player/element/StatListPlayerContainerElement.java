package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.ArrayList;
import java.util.List;

public class StatListPlayerContainerElement extends VerticalScrollClipContainer<StatListPlayerContainerElement> {
   public StatListPlayerContainerElement(
      ISpatial spatial,
      List<StatLabelElementBuilder<?>> statList,
      VaultGodFavorIconElement.ValueSupplier valueSupplierIdona,
      VaultGodFavorIconElement.ValueSupplier valueSupplierTenos,
      VaultGodFavorIconElement.ValueSupplier valueSupplierVelara,
      VaultGodFavorIconElement.ValueSupplier valueSupplierWendarr
   ) {
      super(spatial, Padding.of(2, 0));
      this.addElement(
         new StatListPlayerContainerElement.VaultGodFavorElement(
               Spatials.positionY(3), valueSupplierIdona, valueSupplierTenos, valueSupplierVelara, valueSupplierWendarr
            )
            .postLayout((screen, gui, parent, world) -> {
               world.translateX((this.innerWidth() - world.width()) / 2);
               return true;
            })
      );
      List<StatLabelElementBuilder<?>> mutableStatList = new ArrayList<>(statList);
      mutableStatList.sort(StatLabelElementBuilder.COMPARATOR);
      this.addElement(
         (StatLabelListElement)new StatLabelListElement(Spatials.positionY(25), mutableStatList)
            .layout((screen, gui, parent, world) -> world.width(this.innerWidth()))
      );
   }

   private static final class VaultGodFavorElement extends ElasticContainerElement<StatListPlayerContainerElement.VaultGodFavorElement> {
      private VaultGodFavorElement(
         IPosition position,
         VaultGodFavorIconElement.ValueSupplier valueSupplierIdona,
         VaultGodFavorIconElement.ValueSupplier valueSupplierTenos,
         VaultGodFavorIconElement.ValueSupplier valueSupplierVelara,
         VaultGodFavorIconElement.ValueSupplier valueSupplierWendarr
      ) {
         super(Spatials.positionXYZ(position));
         this.addElements(
            new VaultGodFavorIconElement(Spatials.zero(), ScreenTextures.ICON_IDONA, valueSupplierIdona),
            new IElement[]{
               new VaultGodFavorIconElement(Spatials.positionX(22), ScreenTextures.ICON_TENOS, valueSupplierTenos),
               new VaultGodFavorIconElement(Spatials.positionX(44), ScreenTextures.ICON_VELARA, valueSupplierVelara),
               new VaultGodFavorIconElement(Spatials.positionX(66), ScreenTextures.ICON_WENDARR, valueSupplierWendarr)
            }
         );
      }
   }
}
