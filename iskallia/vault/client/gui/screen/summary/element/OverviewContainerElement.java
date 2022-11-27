package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.summary.VaultExitContainerScreenData;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.network.chat.TextComponent;

public class OverviewContainerElement extends VerticalScrollClipContainer<OverviewContainerElement> {
   public OverviewContainerElement(ISpatial spatial, VaultExitContainerScreenData screenData) {
      super(spatial, Padding.of(2, 0));
      int width = 170;
      this.addElements(
         new StringStatDisplayElement(
            Spatials.positionY(5).positionX(2),
            ScreenTextures.TAB_ICON_PORTAL_VAULT,
            new TextComponent("Generic Info"),
            width - 4,
            108,
            new HashMap<>(),
            new ArrayList<>(screenData.getOverviewGeneric())
         ),
         new IElement[0]
      );
      this.addElements(new VaultLevelBarWithRewardElement(Spatials.positionY(117).positionX(2), width - 4, screenData), new IElement[0]);
      this.addElements(
         new StringStatDisplayElement(
            Spatials.positionY(5).positionX(width + 4),
            ScreenTextures.ICON_COIN_STACKS,
            new TextComponent("Loot Info"),
            width - 4,
            71,
            new HashMap<>(),
            new ArrayList<>(screenData.getOverviewLoot())
         ),
         new IElement[0]
      );
      this.addElements(
         new StringStatDisplayElement(
            Spatials.positionY(81).positionX(width + 4),
            ScreenTextures.TAB_ICON_MOBS_KILLED,
            new TextComponent("Combat Info"),
            width - 4,
            60,
            new HashMap<>(),
            new ArrayList<>(screenData.getOverviewCombat())
         ),
         new IElement[0]
      );
   }
}
