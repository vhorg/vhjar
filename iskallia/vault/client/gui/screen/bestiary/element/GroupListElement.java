package iskallia.vault.client.gui.screen.bestiary.element;

import iskallia.vault.client.gui.framework.element.ClickableLabelElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bestiary.BestiaryScreen;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.GroupUtils;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

public class GroupListElement extends VerticalScrollClipContainer<GroupListElement> {
   private final BestiaryScreen parentScreen;

   public GroupListElement(ISpatial spatial, BestiaryScreen parentScreen) {
      super(spatial, Padding.of(3, 3));
      this.parentScreen = parentScreen;
      int y = 0;

      for (String groupName : GroupUtils.getEntityGroupNames()) {
         Optional<EntityPredicate> byName = GroupUtils.getFilterByName(groupName);
         if (!byName.isPresent() || !(byName.get() instanceof PartialEntityGroup group && ModConfigs.BESTIARY.getHiddenGroups().contains(group.getId()))) {
            ClickableLabelElement groupLabel = new ClickableLabelElement(
               Spatials.positionXY(0, y),
               new TextComponent(groupName).withStyle(ChatFormatting.BLACK),
               LabelTextStyle.defaultStyle(),
               () -> this.selectGroup(groupName)
            );
            this.addElement(groupLabel);
            y += 14;
         }
      }
   }

   private void selectGroup(String groupName) {
      this.parentScreen.selectGroup(groupName);
   }
}
