package iskallia.vault.client.gui.screen.bestiary.element;

import iskallia.vault.client.gui.framework.element.ClickableLabelElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bestiary.BestiaryScreen;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.util.GroupUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;

public class EntityListElement extends VerticalScrollClipContainer<EntityListElement> {
   private final BestiaryScreen parentScreen;
   private final PartialEntityGroup group;

   public EntityListElement(ISpatial spatial, BestiaryScreen parentScreen, PartialEntityGroup group) {
      super(spatial, Padding.of(3, 3));
      this.group = group;
      this.parentScreen = parentScreen;
      int y = 0;

      for (EntityType<?> type : GroupUtils.getEntityTypes(group.getId())) {
         ClickableLabelElement entityLabel = new ClickableLabelElement(
            Spatials.positionXY(0, y),
            type.getDescription().copy().withStyle(ChatFormatting.BLACK),
            LabelTextStyle.defaultStyle(),
            () -> this.selectEntity(type)
         );
         this.addElement(entityLabel);
         y += 14;
      }
   }

   private void selectEntity(EntityType<?> type) {
      this.parentScreen.selectEntity(type, this.group);
   }
}
