package iskallia.vault.client.gui.screen.bestiary.element;

import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.EntityModelElement;
import iskallia.vault.client.gui.framework.element.FakeItemSlotElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutStrategy;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bestiary.BestiaryScreen;
import iskallia.vault.client.gui.screen.bounty.element.HeaderElement;
import iskallia.vault.config.BestiaryConfig;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.GroupUtils;
import iskallia.vault.util.TextComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityDefinitionElement extends ElasticContainerElement<EntityDefinitionElement> {
   private BestiaryScreen parent;
   private EntityType<?> entityType;
   private PartialEntityGroup group;
   private HeaderElement header;
   private EntityModelElement<?> entityModelElement;
   private LabelElement<?> description;
   private NineSliceButtonElement<?> backButtonElement;

   public EntityDefinitionElement(ISpatial spatial, EntityType<?> entityType, PartialEntityGroup group, BestiaryScreen parent) {
      super(spatial);
      this.entityType = entityType;
      this.group = group;
      this.parent = parent;
      Optional<BestiaryConfig.EntityEntry> entryOptional = ModConfigs.BESTIARY.getEntityEntry(this.entityType);
      this.addElement(new NineSliceElement(Spatials.positionXY(this.width() / 2 - 50, -45).width(100).height(20), ScreenTextures.DEFAULT_WINDOW_BACKGROUND));
      this.addElement(
         new HeaderElement(
            Spatials.positionXYZ(this.width() / 2 - 50, -45, 1).width(100).height(20), GroupUtils.getEntityName(this.group), LabelTextStyle.center().shadow()
         )
      );
      this.backButtonElement = new NineSliceButtonElement(
         Spatials.positionXY(-5, this.height() + 10).size(100, 20),
         ScreenTextures.BUTTON_EMPTY_TEXTURES,
         () -> this.parent.selectGroup(GroupUtils.getEntityName(this.group).getString())
      );
      this.backButtonElement.label(() -> new TranslatableComponent("gui.back"));
      this.addElement(
         new NineSliceButtonElement(
               Spatials.positionXY(-5, -45).size(100, 20),
               ScreenTextures.BUTTON_EMPTY_TEXTURES,
               () -> this.parent.selectEntity(this.getPreviousEntity(), this.group)
            )
            .label(() -> new TranslatableComponent("screen.the_vault.bestiary.previous_button"))
      );
      this.addElement(
         new NineSliceButtonElement(
               Spatials.positionXY(this.width() - 97, -45).size(100, 20),
               ScreenTextures.BUTTON_EMPTY_TEXTURES,
               () -> this.parent.selectEntity(this.getNextEntity(), this.group)
            )
            .label(() -> new TranslatableComponent("screen.the_vault.bestiary.next_button"))
      );
      this.addElement(this.backButtonElement);
      if (entryOptional.isEmpty()) {
         entryOptional = Optional.of(BestiaryConfig.EntityEntry.getDefault(entityType.getRegistryName()));
      }

      BestiaryConfig.EntityEntry entry = entryOptional.get();
      this.header = new HeaderElement(Spatials.positionXY(0, 0).width(this.width() / 2 - 5).height(20), entityType.getDescription());
      this.entityModelElement = new EntityModelElement(
            Spatials.positionXY(0, this.header.bottom() + 3),
            Spatials.size(this.width() / 2 - 5, 100),
            this::createEntity,
            new Vector3f(0.0F, 8.0F, 0.0F),
            1.0F,
            ScreenTextures.INSET_GREY_BACKGROUND
         )
         .advancedControl();
      this.description = new LabelElement(
            Spatials.positionXY(0, this.header.bottom() + 105 + 3).size(this.width() / 2 - 12, this.height()),
            entry.getDescriptionData(),
            LabelTextStyle.wrap()
         )
         .layout(this.adjustLabelLayout());
      this.addElements(this.header, new IElement[]{this.entityModelElement, this.description});
      int rightX = this.width() / 2 + 5;
      int y = 0;
      MutableComponent minLevelComponent = new TranslatableComponent("screen.the_vault.bestiary.min_level").withStyle(ChatFormatting.BLACK);
      int minLevelWidth = TextComponentUtils.getWidth(minLevelComponent);
      this.addElement(new LabelElement(Spatials.positionXY(rightX, y), minLevelComponent, LabelTextStyle.defaultStyle()));
      this.addElement(
         new LabelElement(
            Spatials.positionXYZ(rightX + minLevelWidth + 2, y, 1),
            new TextComponent(String.valueOf(entry.getMinLevel())).withStyle(ChatFormatting.YELLOW),
            LabelTextStyle.shadow()
         )
      );
      y += 12;
      MutableComponent vaultExpComponent = new TranslatableComponent("screen.the_vault.bestiary.vault_exp").withStyle(ChatFormatting.BLACK);
      int vaultExpWidth = TextComponentUtils.getWidth(vaultExpComponent);
      this.addElement(new LabelElement(Spatials.positionXY(rightX, y), vaultExpComponent, LabelTextStyle.defaultStyle()));
      this.addElement(
         new LabelElement(
            Spatials.positionXYZ(rightX + vaultExpWidth + 2, y, 1),
            new TextComponent(String.valueOf((int)entry.getVaultExp())).withStyle(ChatFormatting.YELLOW),
            LabelTextStyle.shadow()
         )
      );
      y += 12;
      MutableComponent themesComponent = new TranslatableComponent("screen.the_vault.bestiary.themes").withStyle(ChatFormatting.BLACK);
      this.addElement(new LabelElement(Spatials.positionXY(rightX, y), themesComponent, LabelTextStyle.defaultStyle()));
      y += 12;

      for (String theme : entry.getThemes()) {
         this.addElement(
            new LabelElement(
               Spatials.positionXYZ(rightX + 3, y, 1), new TextComponent("-" + theme).withStyle(ChatFormatting.DARK_GRAY), LabelTextStyle.defaultStyle()
            )
         );
         y += 12;
      }

      MutableComponent dropsComponent = new TranslatableComponent("screen.the_vault.bestiary.drops").withStyle(ChatFormatting.BLACK);
      this.addElement(new LabelElement(Spatials.positionXY(rightX, y), dropsComponent, LabelTextStyle.defaultStyle()));
      y += 12;
      int stackX = rightX;
      int stackY = y;

      for (BestiaryConfig.EntityDrop drop : entry.getDrops()) {
         ItemStack stack = drop.getStack();
         List<Component> dropDetails = new ArrayList<>();
         dropDetails.add(new TextComponent(" "));
         IntRangeEntry amount = drop.getAmount();
         TranslatableComponent amountComponent = new TranslatableComponent(
            "screen.the_vault.bestiary.drop_amount", new Object[]{amount.min == -1 ? "?" : amount.min, amount.max == -1 ? "?" : amount.max}
         );
         dropDetails.add(amountComponent);
         IntRangeEntry probability = drop.getProbability();
         TranslatableComponent probabilityComponent = new TranslatableComponent(
            "screen.the_vault.bestiary.drop_probability",
            new Object[]{probability.min == -1 ? "?" : probability.min + "%", probability.max == -1 ? "?" : probability.max + "%"}
         );
         dropDetails.add(probabilityComponent);
         FakeItemSlotElement<?> slotElement = new FakeItemSlotElement(Spatials.positionXY(stackX, stackY), drop::getStack, () -> false)
            .tooltip(Tooltips.shift(Tooltips.multi(() -> {
               List<Component> components = stack.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL);
               components.addAll(dropDetails);
               return components;
            }), Tooltips.multi(() -> {
               List<Component> components = stack.getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED);
               components.addAll(dropDetails);
               return components;
            })));
         this.addElement(slotElement);
         stackX += 18;
         int width = this.width() - 13;
         if (stackX + 18 > width) {
            stackY += 18;
            stackX = rightX;
         }
      }
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      return this.entityModelElement.isDragging()
         && this.isEnabled()
         && this.entityModelElement.isEnabled()
         && this.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
   }

   private LivingEntity createEntity() {
      Level level = Minecraft.getInstance().level;
      if (level == null) {
         return null;
      } else {
         Entity entity = this.entityType.create(level);
         return entity instanceof LivingEntity ? (LivingEntity)entity : null;
      }
   }

   private EntityType<?> getNextEntity() {
      List<EntityType<?>> types = GroupUtils.getEntityTypes(this.group.getId());
      if (types.isEmpty()) {
         return EntityType.BAT;
      } else {
         int index = types.indexOf(this.entityType) + 1;
         if (index >= types.size()) {
            index = 0;
         }

         return types.get(index);
      }
   }

   private EntityType<?> getPreviousEntity() {
      List<EntityType<?>> types = GroupUtils.getEntityTypes(this.group.getId());
      if (types.isEmpty()) {
         return EntityType.BAT;
      } else {
         int index = types.indexOf(this.entityType) - 1;
         if (index < 0) {
            index = types.size() - 1;
         }

         return types.get(index);
      }
   }

   @NotNull
   public ILayoutStrategy adjustLabelLayout() {
      return (screen, gui, parent, world) -> world.size(this.width() / 2 - 5, this.height() - 27);
   }
}
