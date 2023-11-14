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
import iskallia.vault.gear.attribute.VaultGearModifier;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifierDiscoverySelectorElement
   extends ScrollableListSelectorElement<ModifierDiscoverySelectorElement, Tuple<Item, ResourceLocation>, ModifierDiscoverySelectorElement.ModifierDiscoveryElement> {
   public ModifierDiscoverySelectorElement(ISpatial spatial, List<Tuple<Item, ResourceLocation>> gearModifiers) {
      super(
         Spatials.copy(spatial).width(ScreenTextures.BUTTON_MODIFIER_DISCOVERY_TEXTURES.button().width()),
         new ModifierDiscoverySelectorElement.ModifierDiscoverySelectorModel(gearModifiers)
      );
      this.verticalScrollBarElement.setVisible(false);
   }

   public void onSelect(Consumer<Tuple<Item, ResourceLocation>> fn) {
      if (this.getSelectorModel() instanceof ModifierDiscoverySelectorElement.ModifierDiscoverySelectorModel selModel) {
         selModel.whenSelected(cfg -> {
            ModifierDiscoverySelectorElement.ModifierDiscoveryElement option = selModel.getSelectedElement();
            if (option != null) {
               fn.accept(new Tuple(option.gearStack.getItem(), option.modifierId));
            }
         });
      }
   }

   public static class ModifierDiscoveryElement extends SelectableButtonElement<ModifierDiscoverySelectorElement.ModifierDiscoveryElement> {
      private final ItemStack gearStack;
      @Nullable
      private final VaultGearModifier<?> displayModifier;
      private final LabelTextStyle textStyle;
      private final ResourceLocation modifierId;

      public ModifierDiscoveryElement(IPosition position, Item gearItem, ResourceLocation modifierId) {
         super(position, ScreenTextures.BUTTON_MODIFIER_DISCOVERY_TEXTURES, () -> {});
         this.gearStack = new ItemStack(gearItem);
         this.modifierId = modifierId;
         this.displayModifier = VaultGearWorkbenchConfig.getConfig(gearItem)
            .map(c -> c.getConfig(modifierId))
            .flatMap(VaultGearWorkbenchConfig.CraftableModifierConfig::createModifier)
            .orElse(null);
         this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         poseStack.pushPose();
         poseStack.translate(0.0, 0.0, 1.0);
         this.getModifierDescription()
            .ifPresent(
               cfgDisplay -> {
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
                        new TextComponent("(").append(this.gearStack.getHoverName().getString()).append(new TextComponent(")")).withStyle(ChatFormatting.GRAY),
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
         return this.displayModifier == null ? Optional.empty() : this.displayModifier.getConfigDisplay(this.gearStack);
      }
   }

   public static class ModifierDiscoverySelectorModel
      extends ScrollableListSelectorElement.SelectorModel<ModifierDiscoverySelectorElement.ModifierDiscoveryElement, Tuple<Item, ResourceLocation>> {
      private final List<Tuple<Item, ResourceLocation>> gearModifiers;

      public ModifierDiscoverySelectorModel(List<Tuple<Item, ResourceLocation>> gearModifiers) {
         this.gearModifiers = gearModifiers;
      }

      @Override
      public List<Tuple<Item, ResourceLocation>> getEntries() {
         return this.gearModifiers;
      }

      public ModifierDiscoverySelectorElement.ModifierDiscoveryElement createSelectable(ISpatial spatial, Tuple<Item, ResourceLocation> entry) {
         return new ModifierDiscoverySelectorElement.ModifierDiscoveryElement(spatial, (Item)entry.getA(), (ResourceLocation)entry.getB());
      }
   }
}
