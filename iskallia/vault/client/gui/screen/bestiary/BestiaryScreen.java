package iskallia.vault.client.gui.screen.bestiary;

import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutStrategy;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementScreen;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bestiary.element.EntityDefinitionElement;
import iskallia.vault.client.gui.screen.bestiary.element.EntityGroupElement;
import iskallia.vault.client.gui.screen.bestiary.element.GroupListElement;
import iskallia.vault.client.gui.screen.bounty.element.HeaderElement;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.util.EntityGroupsUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class BestiaryScreen extends AbstractElementScreen {
   private HeaderElement initialHeader;
   private LabelElement<?> initialDescription;
   private GroupListElement groupListElement;
   private NineSliceButtonElement<?> backButtonElement;
   private EntityGroupElement entityGroupElement;
   private EntityDefinitionElement entityDefinitionElement;
   int center;
   int innerWidth;
   int innerHeight;
   int leftX = 7;
   int rightX;
   int startY = 25;

   public BestiaryScreen(EntityPredicate group) {
      super(new TranslatableComponent("screen.the_vault.bestiary.title"), ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(400, 192));
      this.center = this.getGuiSpatial().width() / 2;
      this.innerWidth = this.getGuiSpatial().width() / 2 - 12;
      this.innerHeight = this.getGuiSpatial().height() - 35;
      this.rightX = this.getGuiSpatial().width() / 2 + 5;
      this.setupBase();
      if (group != null) {
         this.selectGroup(EntityGroupsUtils.getName(group).getString());
      } else {
         this.initialHeader = new HeaderElement(
               Spatials.positionXY(this.leftX, this.startY).width(this.innerWidth).height(20), new TranslatableComponent("screen.the_vault.bestiary.welcome")
            )
            .layout(this.translateToGuiSpatial());
         this.initialDescription = new LabelElement(
               Spatials.positionXY(this.leftX, this.initialHeader.bottom() + 3).size(this.innerWidth, this.innerHeight - 27),
               new TranslatableComponent("screen.the_vault.bestiary.welcome_desc", new Object[]{ChatFormatting.BOLD, ChatFormatting.RESET})
                  .withStyle(ChatFormatting.BLACK),
               LabelTextStyle.wrap()
            )
            .layout(this.adjustLabelLayout());
         this.groupListElement = new GroupListElement(Spatials.positionXY(this.rightX, this.startY).size(this.innerWidth, this.innerHeight), this)
            .layout(this.translateToGuiSpatial());
         this.backButtonElement = new NineSliceButtonElement(
               Spatials.positionXY(2, this.getGuiSpatial().height()).size(100, 20), ScreenTextures.BUTTON_EMPTY_TEXTURES, this::onClose
            )
            .<NineSliceButtonElement<NineSliceButtonElement<?>>>label(() -> new TranslatableComponent("screen.the_vault.bestiary.close"))
            .layout(this.translateToGuiSpatial());
         this.addElements(this.initialHeader, new IElement[]{this.initialDescription, this.groupListElement, this.backButtonElement});
      }
   }

   public BestiaryScreen() {
      this(null);
   }

   private void clearInitialState() {
      if (this.initialHeader != null) {
         this.removeElement(this.initialHeader);
      }

      if (this.initialDescription != null) {
         this.removeElement(this.initialDescription);
      }

      if (this.groupListElement != null) {
         this.removeElement(this.groupListElement);
      }

      if (this.backButtonElement != null) {
         this.removeElement(this.backButtonElement);
      }
   }

   private void clearGroupState() {
      if (this.entityGroupElement != null) {
         this.removeElement(this.entityGroupElement);
      }
   }

   private void clearEntityState() {
      if (this.entityDefinitionElement != null) {
         this.removeElement(this.entityDefinitionElement);
      }
   }

   private void setupBase() {
      NineSliceElement<?> background = new NineSliceElement(
            Spatials.positionXY(0, 0).size(this.getGuiSpatial().width(), this.getGuiSpatial().height()), ScreenTextures.DEFAULT_WINDOW_BACKGROUND
         )
         .layout(this.translateToGuiSpatial());
      this.addElement(background);
      LabelElement<?> title = new LabelElement(
            Spatials.positionXY(this.leftX, 9).size(this.getGuiSpatial().width() / 2, 20),
            this.title.copy().withStyle(ChatFormatting.BLACK),
            LabelTextStyle.defaultStyle()
         )
         .layout(this.translateToGuiSpatial());
      this.addElement(title);
      NineSliceElement<?> separator = new NineSliceElement(
            Spatials.positionXY(this.center - 1, 1).width(3).height(this.getGuiSpatial().height() - 2), ScreenTextures.INSET_VERTICAL_SEPARATOR
         )
         .layout(this.translateToGuiSpatial());
      this.addElement(separator);
   }

   public void selectGroup(String groupName) {
      this.clearInitialState();
      this.clearEntityState();
      this.entityGroupElement = new EntityGroupElement(
            Spatials.positionXY(this.leftX, this.startY).size(this.getGuiSpatial().width() - 12, this.innerHeight), groupName, this
         )
         .layout(this.translateToGuiSpatial());
      this.addElement(this.entityGroupElement);
      ScreenLayout.requestLayout();
   }

   public void selectEntity(EntityType<?> type, PartialEntityGroup group) {
      this.clearInitialState();
      this.clearGroupState();
      this.clearEntityState();
      this.entityDefinitionElement = new EntityDefinitionElement(
            Spatials.positionXY(this.leftX, this.startY).size(this.getGuiSpatial().width() - 12, this.innerHeight), type, group, this
         )
         .layout(this.translateToGuiSpatial());
      this.addElement(this.entityDefinitionElement);
      ScreenLayout.requestLayout();
   }

   @NotNull
   public ILayoutStrategy translateToGuiSpatial() {
      return (screen, gui, parent, world) -> world.translateXY(this.getGuiSpatial());
   }

   @NotNull
   public ILayoutStrategy adjustLabelLayout() {
      return (screen, gui, parent, world) -> {
         world.translateXY(this.getGuiSpatial());
         world.size(this.innerWidth, this.innerHeight - 27);
      };
   }

   public boolean isPauseScreen() {
      return false;
   }
}
