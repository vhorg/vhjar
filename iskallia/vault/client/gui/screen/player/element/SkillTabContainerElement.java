package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.TabElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ServerboundOpenAbilitiesMessage;
import iskallia.vault.network.message.ServerboundOpenExpertisesMessage;
import iskallia.vault.network.message.ServerboundOpenResearchesMessage;
import iskallia.vault.network.message.ServerboundOpenStatisticsMessage;
import iskallia.vault.network.message.ServerboundOpenTalentsMessage;

public class SkillTabContainerElement<E extends SkillTabContainerElement<E>> extends ElasticContainerElement<E> {
   public SkillTabContainerElement(IPosition position, int selectedIndex) {
      super(Spatials.positionXYZ(position));
      TextureAtlasRegion[] icons = new TextureAtlasRegion[]{
         ScreenTextures.TAB_ICON_STATISTICS,
         ScreenTextures.TAB_ICON_ABILITIES,
         ScreenTextures.TAB_ICON_TALENTS,
         ScreenTextures.TAB_ICON_EXPERTISES,
         ScreenTextures.TAB_ICON_RESEARCHES
      };
      int tabWidth = 28;
      int tabSpacing = 3;

      for (int i = 0; i < icons.length; i++) {
         int index = i;
         int x = 31 * i;
         boolean isSelected = selectedIndex == i;
         TextureAtlasRegion icon = icons[i];
         this.addElement(new SkillTabContainerElement.SkillTabElement(Spatials.positionX(x), isSelected, icon, () -> {
            if (selectedIndex != index) {
               switch (index) {
                  case 0:
                     ModNetwork.CHANNEL.sendToServer(ServerboundOpenStatisticsMessage.INSTANCE);
                     break;
                  case 1:
                     ModNetwork.CHANNEL.sendToServer(ServerboundOpenAbilitiesMessage.INSTANCE);
                     break;
                  case 2:
                     ModNetwork.CHANNEL.sendToServer(ServerboundOpenTalentsMessage.INSTANCE);
                     break;
                  case 3:
                     ModNetwork.CHANNEL.sendToServer(ServerboundOpenExpertisesMessage.INSTANCE);
                     break;
                  case 4:
                     ModNetwork.CHANNEL.sendToServer(ServerboundOpenResearchesMessage.INSTANCE);
               }
            }
         }));
      }
   }

   private static class SkillTabElement extends TabElement<SkillTabContainerElement.SkillTabElement> {
      private SkillTabElement(IPosition position, boolean selected, TextureAtlasRegion icon, Runnable onClick) {
         super(
            position,
            selected
               ? new TextureAtlasElement(ScreenTextures.TAB_BACKGROUND_TOP_SELECTED)
               : new TextureAtlasElement(Spatials.positionY(4), ScreenTextures.TAB_BACKGROUND_TOP),
            new TextureAtlasElement(Spatials.positionXYZ(6, 9, 1), icon),
            onClick
         );
      }
   }
}
