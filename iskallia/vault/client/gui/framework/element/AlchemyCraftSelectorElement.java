package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextAlign;
import iskallia.vault.client.gui.framework.text.TextWrap;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AlchemyCraftSelectorElement<E extends AlchemyCraftSelectorElement<E, V>, V extends AlchemyCraftSelectorElement.WorkbenchListElement<V>>
   extends ScrollableListSelectorElement<E, AlchemyTableConfig.CraftableEffectConfig, V> {
   private final ObservableSupplier<ItemStack> inputSupplier;

   public AlchemyCraftSelectorElement(ISpatial spatial, ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
      super(
         Spatials.copy(spatial).width(ScreenTextures.BUTTON_ALCHEMY_MODIFIER_TEXTURES.button().width()),
         new AlchemyCraftSelectorElement.AlchemyTableEffectSelectorModel<>(inputSupplier, searchFilter)
      );
      this.inputSupplier = inputSupplier;
   }

   public void onSelect(Consumer<AlchemyCraftSelectorElement.CraftingOption> fn) {
      if (this.getSelectorModel() instanceof AlchemyCraftSelectorElement.AlchemyTableEffectSelectorModel<?> selModel) {
         selModel.whenSelected(cfg -> {
            AlchemyCraftSelectorElement.CraftingOption option = selModel.getSelectedCraftingOption();
            if (option != null) {
               fn.accept(option);
            }
         });
      }
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      this.inputSupplier.ifChanged(change -> this.refreshElements());
   }

   public static class AlchemyCraftElement<E extends AlchemyCraftSelectorElement.AlchemyCraftElement<E>>
      extends AlchemyCraftSelectorElement.WorkbenchListElement<E> {
      private final ItemStack bottleStack;
      private final AlchemyTableConfig.CraftableEffectConfig effect;
      private final LabelTextStyle textStyle;
      private final BottleEffect displayEffect;

      public AlchemyCraftElement(IPosition position, ItemStack bottleStack, AlchemyTableConfig.CraftableEffectConfig effectCfg) {
         super(position);
         this.bottleStack = bottleStack;
         this.effect = effectCfg;
         this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
         this.displayEffect = BottleItem.getType(bottleStack).flatMap(effectCfg::createEffect).orElse(null);
         this.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (this.canCraft()) {
               return false;
            } else {
               Player player = Minecraft.getInstance().player;
               if (player == null) {
                  return false;
               } else if (!this.effect.hasPrerequisites(player)) {
                  String displayStr = this.getEffect().getUnlockCategory().formatDisplay();
                  tooltipRenderer.renderTooltip(poseStack, new TextComponent(displayStr).withStyle(ChatFormatting.RED), mouseX, mouseY, TooltipDirection.RIGHT);
                  return true;
               } else {
                  return false;
               }
            }
         });
      }

      public AlchemyTableConfig.CraftableEffectConfig getEffect() {
         return this.effect;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         this.setDisabled(!this.canCraft());
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         if (this.displayEffect != null) {
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
      }

      @Override
      protected List<ItemStack> createNeededInputs() {
         return this.effect.createCraftingCost(this.bottleStack);
      }

      public Optional<Component> getEffectDescription() {
         return Optional.of(this.displayEffect.getTooltip());
      }

      private boolean canCraft() {
         Player player = Minecraft.getInstance().player;
         return player != null && !this.bottleStack.isEmpty() && this.effect.hasPrerequisites(player);
      }
   }

   public static class AlchemyTableEffectSelectorModel<E extends AlchemyCraftSelectorElement.WorkbenchListElement<E>>
      extends ScrollableListSelectorElement.SelectorModel<E, AlchemyTableConfig.CraftableEffectConfig> {
      private final ObservableSupplier<ItemStack> inputSupplier;
      private final Supplier<String> searchFilter;

      public AlchemyTableEffectSelectorModel(ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
         this.inputSupplier = inputSupplier;
         this.searchFilter = searchFilter;
      }

      @Override
      public List<AlchemyTableConfig.CraftableEffectConfig> getEntries() {
         ItemStack input = this.inputSupplier.get();
         if (input.isEmpty()) {
            return Collections.emptyList();
         } else if (Minecraft.getInstance().player == null) {
            return Collections.emptyList();
         } else {
            String searchTerm = this.searchFilter.get().toLowerCase(Locale.ROOT);
            List<AlchemyTableConfig.CraftableEffectConfig> out = new ArrayList<>();
            ModConfigs.VAULT_ALCHEMY_TABLE.getCraftableEffects().forEach(cfg -> BottleItem.getType(input).flatMap(cfg::createEffect).ifPresent(effect -> {
               String locDisplay = effect.getTooltip().getString().toLowerCase(Locale.ROOT);
               if (searchTerm.isEmpty() || locDisplay.contains(searchTerm)) {
                  out.add(cfg);
               }
            }));
            return out;
         }
      }

      public E createSelectable(ISpatial spatial, AlchemyTableConfig.CraftableEffectConfig entry) {
         return (E)(new AlchemyCraftSelectorElement.AlchemyCraftElement(spatial, this.inputSupplier.get(), entry));
      }

      @Nullable
      protected AlchemyCraftSelectorElement.CraftingOption getSelectedCraftingOption() {
         E element = (E)((AlchemyCraftSelectorElement.WorkbenchListElement)this.getSelectedElement());
         if (element == null) {
            return null;
         } else {
            return element instanceof AlchemyCraftSelectorElement.AlchemyCraftElement<?> craftElement
               ? new AlchemyCraftSelectorElement.CraftingOption(craftElement.getEffect())
               : new AlchemyCraftSelectorElement.CraftingOption(null);
         }
      }
   }

   public record CraftingOption(@Nullable AlchemyTableConfig.CraftableEffectConfig cfg) {
      public List<ItemStack> getCraftingCost(ItemStack input) {
         return this.cfg() == null ? Collections.emptyList() : this.cfg().createCraftingCost(input);
      }
   }

   public abstract static class WorkbenchListElement<E extends AlchemyCraftSelectorElement.WorkbenchListElement<E>> extends SelectableButtonElement<E> {
      private List<ItemStack> inputs;

      public WorkbenchListElement(IPosition position) {
         super(position, ScreenTextures.BUTTON_ALCHEMY_MODIFIER_TEXTURES, () -> {});
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
         Font font = Minecraft.getInstance().font;
         int offsetX = this.worldSpatial.x() + this.worldSpatial.width() - 18;
         int offsetY = this.worldSpatial.y() + this.worldSpatial.height() - 18;
         List<ItemStack> inputs = this.getInputs();
         List<ItemStack> missingInputs = new ArrayList<>();
         if (Minecraft.getInstance().player != null) {
            missingInputs = InventoryUtil.getMissingInputs(inputs, Minecraft.getInstance().player.getInventory());
         }

         for (ItemStack stack : inputs) {
            ir.renderGuiItem(stack, offsetX, offsetY);
            MutableComponent text = new TextComponent(String.valueOf(stack.getCount()));
            if (missingInputs.contains(stack)) {
               text.withStyle(ChatFormatting.RED);
            }

            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 200.0);
            BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(
               text,
               offsetX + 17 - font.width(text),
               offsetY + 9,
               16777215,
               true,
               poseStack.last().pose(),
               buffers,
               false,
               0,
               LightmapHelper.getPackedFullbrightCoords()
            );
            buffers.endBatch();
            poseStack.popPose();
            offsetX -= 17;
         }
      }

      protected List<ItemStack> getInputs() {
         if (this.inputs == null) {
            this.inputs = this.createNeededInputs();
         }

         return this.inputs;
      }

      protected abstract List<ItemStack> createNeededInputs();
   }
}
