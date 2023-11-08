package iskallia.vault.client.gui.screen.bestiary.element;

import iskallia.vault.client.gui.framework.element.BackButtonElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutStrategy;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bestiary.BestiaryScreen;
import iskallia.vault.client.gui.screen.bounty.element.HeaderElement;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.EntityGroupsUtils;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class EntityGroupElement extends ElasticContainerElement<EntityGroupElement> {
   private BestiaryScreen parent;
   private PartialEntityGroup selectedGroup;
   private HeaderElement groupHeader;
   private LabelElement<?> groupDescription;
   private EntityListElement entityListElement;
   private BackButtonElement backButtonElement;

   public EntityGroupElement(ISpatial spatial, String groupName, BestiaryScreen parent) {
      super(spatial);
      this.parent = parent;
      Optional<EntityPredicate> groupOptional = EntityGroupsUtils.getByName(groupName);
      groupOptional.ifPresent(group -> this.selectedGroup = (PartialEntityGroup)group);
      if (this.selectedGroup != null) {
         this.groupHeader = new HeaderElement(Spatials.positionXY(0, 0).width(this.width() / 2 - 6).height(20), EntityGroupsUtils.getName(this.selectedGroup));
         this.groupDescription = new LabelElement(
               Spatials.positionXY(0, this.groupHeader.bottom() + 3).size(this.width() / 2 - 12, this.height()),
               ModConfigs.BESTIARY.getGroupDescription(this.selectedGroup.getId()).getComponent(),
               LabelTextStyle.wrap()
            )
            .layout(this.adjustLabelLayout());
         this.entityListElement = new EntityListElement(
            Spatials.positionXY(this.width() / 2 + 4, 0).size(this.width() / 2 - 6, this.height()), this.parent, this.selectedGroup
         );
         this.backButtonElement = new BackButtonElement(Spatials.positionXY(-5, this.height() + 10).size(100, 20), new BestiaryScreen());
         this.addElements(this.groupHeader, new IElement[]{this.groupDescription, this.entityListElement, this.backButtonElement});
      }
   }

   @NotNull
   public ILayoutStrategy adjustLabelLayout() {
      return (screen, gui, parent, world) -> world.size(this.width() / 2 - 5, this.height() - 27);
   }
}
