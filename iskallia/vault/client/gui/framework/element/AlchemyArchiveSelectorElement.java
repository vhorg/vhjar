package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextAlign;
import iskallia.vault.client.gui.framework.text.TextWrap;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AlchemyArchiveSelectorElement
   extends ScrollableListSelectorElement<AlchemyArchiveSelectorElement, BottleEffect, AlchemyArchiveSelectorElement.AlchemyArchiveElement> {
   public AlchemyArchiveSelectorElement(ISpatial spatial, List<String> effects) {
      super(
         Spatials.copy(spatial).width(ScreenTextures.BUTTON_ALCHEMY_MODIFIER_TEXTURES.button().width()),
         new AlchemyArchiveSelectorElement.AlchemyArchiveEffectSelectorModel(effects)
      );
      this.verticalScrollBarElement.setVisible(false);
   }

   public void onSelect(Consumer<String> fn) {
      if (this.getSelectorModel() instanceof AlchemyArchiveSelectorElement.AlchemyArchiveEffectSelectorModel selModel) {
         selModel.whenSelected(cfg -> {
            AlchemyArchiveSelectorElement.AlchemyArchiveElement option = selModel.getSelectedElement();
            if (option != null) {
               fn.accept(option.getEffect().getEffectId());
            }
         });
      }
   }

   public static class AlchemyArchiveEffectSelectorModel
      extends ScrollableListSelectorElement.SelectorModel<AlchemyArchiveSelectorElement.AlchemyArchiveElement, BottleEffect> {
      private final List<String> effects;

      public AlchemyArchiveEffectSelectorModel(List<String> effects) {
         this.effects = effects;
      }

      @Override
      public List<BottleEffect> getEntries() {
         return this.effects
            .stream()
            .map(effectId -> ModConfigs.VAULT_ALCHEMY_TABLE.getConfig(effectId).createEffect(BottleItem.Type.POTION))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
      }

      public AlchemyArchiveSelectorElement.AlchemyArchiveElement createSelectable(ISpatial spatial, BottleEffect entry) {
         return new AlchemyArchiveSelectorElement.AlchemyArchiveElement(spatial, entry);
      }
   }

   public static class AlchemyArchiveElement extends SelectableButtonElement<AlchemyArchiveSelectorElement.AlchemyArchiveElement> {
      private final BottleEffect effect;
      private final LabelTextStyle textStyle;

      public AlchemyArchiveElement(IPosition position, BottleEffect effect) {
         super(position, ScreenTextures.BUTTON_ALCHEMY_MODIFIER_TEXTURES, () -> {});
         this.effect = effect;
         this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         this.getEffectDescription()
            .ifPresent(
               cfgDisplay -> this.textStyle
                  .textBorder()
                  .render(
                     renderer,
                     poseStack,
                     cfgDisplay,
                     TextWrap.wrap(),
                     TextAlign.LEFT,
                     this.worldSpatial.x() + 4,
                     this.worldSpatial.y() + 3,
                     this.worldSpatial.z(),
                     this.worldSpatial.width()
                  )
            );
      }

      public BottleEffect getEffect() {
         return this.effect;
      }

      public Optional<Component> getEffectDescription() {
         return Optional.of(this.effect.getTooltip());
      }
   }
}
