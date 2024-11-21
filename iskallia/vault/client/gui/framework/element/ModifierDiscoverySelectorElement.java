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
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.modifier.DiscoverableModifier;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.util.TextComponentUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifierDiscoverySelectorElement
   extends ScrollableListSelectorElement<ModifierDiscoverySelectorElement, DiscoverableModifier, ModifierDiscoverySelectorElement.ModifierDiscoveryElement> {
   public ModifierDiscoverySelectorElement(ISpatial spatial, List<DiscoverableModifier> gearModifiers) {
      super(
         Spatials.copy(spatial).width(ScreenTextures.BUTTON_MODIFIER_DISCOVERY_TEXTURES.button().width()),
         new ModifierDiscoverySelectorElement.ModifierDiscoverySelectorModel(gearModifiers)
      );
      this.verticalScrollBarElement.setVisible(false);
   }

   public void onSelect(Consumer<DiscoverableModifier> fn) {
      if (this.getSelectorModel() instanceof ModifierDiscoverySelectorElement.ModifierDiscoverySelectorModel selModel) {
         selModel.whenSelected(cfg -> {
            ModifierDiscoverySelectorElement.ModifierDiscoveryElement option = selModel.getSelectedElement();
            if (option != null) {
               fn.accept(option.discoverableModifier);
            }
         });
      }
   }

   public static class ModifierDiscoveryElement extends SelectableButtonElement<ModifierDiscoverySelectorElement.ModifierDiscoveryElement> {
      private final DiscoverableModifier discoverableModifier;
      @Nullable
      private final VaultGearModifier<?> displayModifier;
      private final LabelTextStyle textStyle;

      public ModifierDiscoveryElement(IPosition position, DiscoverableModifier discoverableModifier) {
         super(position, ScreenTextures.BUTTON_MODIFIER_DISCOVERY_TEXTURES, () -> {});
         this.discoverableModifier = discoverableModifier;
         this.displayModifier = VaultGearWorkbenchConfig.getConfig(discoverableModifier.item())
            .map(c -> c.getConfig(discoverableModifier.modifierId()))
            .flatMap(VaultGearWorkbenchConfig.CraftableModifierConfig::createModifier)
            .orElse(null);
         this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         ItemStack stack = new ItemStack(this.discoverableModifier.item());
         boolean strikeThrough = this.discoverableModifier.discovered();
         poseStack.pushPose();
         poseStack.translate(0.0, 0.0, 1.0);
         this.getModifierDescription()
            .ifPresent(
               cfgDisplay -> {
                  Component itemDisplay = new TextComponent("(")
                     .append(stack.getHoverName().getString())
                     .append(new TextComponent(")"))
                     .withStyle(ChatFormatting.GRAY);
                  if (strikeThrough) {
                     TextComponentUtils.applyStyle(cfgDisplay, Style.EMPTY.withStrikethrough(true));
                     TextComponentUtils.applyStyle(itemDisplay, Style.EMPTY.withStrikethrough(true));
                  }

                  this.textStyle
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
                     );
                  this.textStyle
                     .textBorder()
                     .render(
                        renderer,
                        poseStack,
                        itemDisplay,
                        TextWrap.wrap(),
                        TextAlign.LEFT,
                        this.worldSpatial.x() + 4,
                        this.worldSpatial.y() + 15,
                        this.worldSpatial.z(),
                        this.worldSpatial.width()
                     );
               }
            );
         poseStack.popPose();
      }

      public Optional<MutableComponent> getModifierDescription() {
         return this.displayModifier == null ? Optional.empty() : this.displayModifier.getConfigDisplay(new ItemStack(this.discoverableModifier.item()));
      }
   }

   public static class ModifierDiscoverySelectorModel
      extends ScrollableListSelectorElement.SelectorModel<ModifierDiscoverySelectorElement.ModifierDiscoveryElement, DiscoverableModifier> {
      private final List<DiscoverableModifier> gearModifiers;

      public ModifierDiscoverySelectorModel(List<DiscoverableModifier> gearModifiers) {
         this.gearModifiers = gearModifiers;
      }

      @Override
      public List<DiscoverableModifier> getEntries() {
         return this.gearModifiers;
      }

      public ModifierDiscoverySelectorElement.ModifierDiscoveryElement createSelectable(ISpatial spatial, DiscoverableModifier entry) {
         return new ModifierDiscoverySelectorElement.ModifierDiscoveryElement(spatial, entry);
      }
   }
}
