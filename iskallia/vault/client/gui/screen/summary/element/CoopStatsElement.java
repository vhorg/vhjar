package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.summary.VaultExitContainerScreenData;
import iskallia.vault.core.vault.Vault;
import net.minecraft.network.chat.TextComponent;

public class CoopStatsElement extends VerticalScrollClipContainer<CoopStatsElement> {
   public CoopStatsElement(ISpatial spatial, VaultExitContainerScreenData screenData) {
      super(spatial, Padding.of(2, 0));
      this.addElement(new CoopStatsElement.VaultCoopStatsElement(Spatials.positionY(3), screenData).postLayout((screen, gui, parent, world) -> {
         world.translateX((this.innerWidth() - world.width()) / 2);
         return true;
      }));
   }

   private static final class VaultCoopStatsElement extends ElasticContainerElement<CoopStatsElement.VaultCoopStatsElement> {
      private VaultCoopStatsElement(IPosition position, VaultExitContainerScreenData screenData) {
         super(Spatials.positionXYZ(position));
         this.addElements(
            new VaultCoopPlayersElement(
               Spatials.positionY(10).positionX(0),
               ScreenTextures.TAB_ICON_COOP,
               160,
               30,
               new TextComponent("Players"),
               screenData.getSnapshot().getEnd(),
               screenData.getSnapshot().getEnd().get(Vault.STATS).getMap()
            ),
            new IElement[0]
         );
      }
   }
}
