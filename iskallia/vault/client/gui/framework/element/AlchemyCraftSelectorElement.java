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
import iskallia.vault.config.gear.VaultAlchemyTableConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.AlchemyTableHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.BottleItem;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AlchemyCraftSelectorElement<E extends AlchemyCraftSelectorElement<E, V>, V extends AlchemyCraftSelectorElement.WorkbenchListElement<V>>
   extends ScrollableListSelectorElement<E, VaultAlchemyTableConfig.CraftableModifierConfig, V> {
   private final ObservableSupplier<ItemStack> inputSupplier;

   public AlchemyCraftSelectorElement(ISpatial spatial, ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
      super(
         Spatials.copy(spatial).width(ScreenTextures.BUTTON_WORKBENCH_MODIFIER_TEXTURES.button().width()),
         new AlchemyCraftSelectorElement.WorkbenchCraftSelectorModel<>(inputSupplier, searchFilter)
      );
      this.inputSupplier = inputSupplier;
   }

   public void onSelect(Consumer<AlchemyTableHelper.CraftingOption> fn) {
      if (this.getSelectorModel() instanceof AlchemyCraftSelectorElement.WorkbenchCraftSelectorModel<?> selModel) {
         selModel.whenSelected(cfg -> {
            AlchemyTableHelper.CraftingOption option = selModel.getSelectedCraftingOption();
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
      private final ItemStack gearStack;
      private final VaultAlchemyTableConfig.CraftableModifierConfig modifier;
      private final LabelTextStyle textStyle;
      private final VaultGearModifier<?> displayModifier;

      public AlchemyCraftElement(IPosition position, ItemStack gearStack, VaultAlchemyTableConfig.CraftableModifierConfig modifier) {
         super(position);
         this.gearStack = gearStack;
         this.modifier = modifier;
         this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
         this.displayModifier = this.modifier.createModifier().orElse(null);
         this.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (this.canCraft()) {
               return false;
            } else {
               Player player = Minecraft.getInstance().player;
               if (player == null) {
                  return false;
               } else if (!this.modifier.hasPrerequisites(player)) {
                  String displayStr = this.getModifier().getUnlockCategory().formatDisplay(this.modifier.getMinLevel());
                  tooltipRenderer.renderTooltip(poseStack, new TextComponent(displayStr).withStyle(ChatFormatting.RED), mouseX, mouseY, TooltipDirection.RIGHT);
                  return true;
               } else {
                  if (!this.gearStack.isEmpty()) {
                     int minLevel = this.modifier.getMinLevel();
                     if (VaultGearData.read(this.gearStack).getItemLevel() < minLevel) {
                        MutableComponent cmp = new TextComponent("Item Level required: " + minLevel).withStyle(ChatFormatting.RED);
                        tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                        return true;
                     }

                     ItemStack gearCopy = this.gearStack.copy();
                     VaultGearData data = VaultGearData.read(gearCopy);
                     Set<String> groups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
                     if (this.displayModifier != null && groups.contains(this.displayModifier.getModifierGroup())) {
                        MutableComponent cmp = new TextComponent("Item already has a modifier of this group.").withStyle(ChatFormatting.RED);
                        tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                        return true;
                     }
                  }

                  String affix = this.modifier.getAffixGroup().getTargetAffixType().getSingular();
                  MutableComponent cmp = new TextComponent("Item has no open " + affix).withStyle(ChatFormatting.RED);
                  tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                  return true;
               }
            }
         });
      }

      public VaultAlchemyTableConfig.CraftableModifierConfig getModifier() {
         return this.modifier;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         this.setDisabled(!this.canCraft());
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         if (this.displayModifier != null) {
            this.getCraftedModifierDescription()
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
         return this.modifier.createCraftingCost(this.gearStack);
      }

      public Optional<MutableComponent> getCraftedModifierDescription() {
         return this.displayModifier.getConfigDisplay(this.gearStack);
      }

      private boolean hasAffixSpace() {
         return BottleItem.getEmptyModifierSlots(this.gearStack) > 0;
      }

      private boolean hasGroupApplied() {
         if (this.gearStack.isEmpty()) {
            return false;
         } else {
            ItemStack gearCopy = this.gearStack.copy();
            VaultGearData data = VaultGearData.read(gearCopy);
            Set<String> groups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
            return this.displayModifier != null && groups.contains(this.displayModifier.getModifierGroup());
         }
      }

      private boolean canCraft() {
         Player player = Minecraft.getInstance().player;
         return player != null
            && !this.gearStack.isEmpty()
            && this.modifier.hasPrerequisites(player)
            && VaultGearData.read(this.gearStack).getItemLevel() >= this.modifier.getMinLevel()
            && !this.hasGroupApplied()
            && this.hasAffixSpace();
      }
   }

   public static class AlchemyRemoveCraftElement<E extends AlchemyCraftSelectorElement.AlchemyRemoveCraftElement<E>>
      extends AlchemyCraftSelectorElement.WorkbenchListElement<E> {
      private final ItemStack gearStack;
      private final LabelTextStyle textStyle;

      public AlchemyRemoveCraftElement(IPosition position, ItemStack gearStack) {
         super(position);
         this.gearStack = gearStack;
         this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
         this.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (this.canCraft()) {
               return false;
            } else if (this.gearStack.isEmpty()) {
               return false;
            } else {
               Component cmp = new TranslatableComponent("the_vault.gear_workbench.remove_crafted_modifiers.no_modifier").withStyle(ChatFormatting.RED);
               tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
               return true;
            }
         });
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         this.setDisabled(!this.canCraft());
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         this.textStyle
            .textBorder()
            .render(
               renderer,
               poseStack,
               new TranslatableComponent("the_vault.gear_workbench.remove_crafted_modifiers"),
               TextWrap.wrap(),
               TextAlign.LEFT,
               this.worldSpatial.x() + 4,
               this.worldSpatial.y() + 3,
               this.worldSpatial.z(),
               this.worldSpatial.width()
            );
      }

      @Override
      protected List<ItemStack> createNeededInputs() {
         return this.gearStack.isEmpty() ? Collections.emptyList() : ModConfigs.VAULT_ALCHEMY_TABLE.getCostRemoveCraftedModifiers();
      }

      private boolean canCraft() {
         return BottleItem.getCraftedModifierSlots(this.gearStack) > 0;
      }
   }

   public static class WorkbenchCraftSelectorModel<E extends AlchemyCraftSelectorElement.WorkbenchListElement<E>>
      extends ScrollableListSelectorElement.SelectorModel<E, VaultAlchemyTableConfig.CraftableModifierConfig> {
      private final ObservableSupplier<ItemStack> inputSupplier;
      private final Supplier<String> searchFilter;

      public WorkbenchCraftSelectorModel(ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
         this.inputSupplier = inputSupplier;
         this.searchFilter = searchFilter;
      }

      @Override
      public List<VaultAlchemyTableConfig.CraftableModifierConfig> getEntries() {
         ItemStack input = this.inputSupplier.get();
         if (input.isEmpty()) {
            return Collections.emptyList();
         } else {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
               return Collections.emptyList();
            } else {
               String searchTerm = this.searchFilter.get().toLowerCase(Locale.ROOT);
               List<VaultAlchemyTableConfig.CraftableModifierConfig> out = new ArrayList<>();
               String locRemove = new TranslatableComponent("the_vault.gear_workbench.remove_crafted_modifiers").getString();
               if (searchTerm.isEmpty() || locRemove.toLowerCase(Locale.ROOT).contains(searchTerm)) {
                  out.add(null);
               }

               ModConfigs.VAULT_ALCHEMY_TABLE
                  .getAllCraftableModifiers()
                  .forEach(cfg -> cfg.createModifier().flatMap(modifier -> modifier.getConfigDisplay(input)).ifPresent(display -> {
                     String locDisplay = display.getString().toLowerCase(Locale.ROOT);
                     if (searchTerm.isEmpty() || locDisplay.contains(searchTerm)) {
                        out.add(cfg);
                     }
                  }));
               int playerLevel = SidedHelper.getVaultLevel(player);
               out.removeIf(
                  cfg -> cfg != null && cfg.getUnlockCategory() == VaultAlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY && cfg.getMinLevel() > playerLevel
               );
               return out;
            }
         }
      }

      public E createSelectable(ISpatial spatial, VaultAlchemyTableConfig.CraftableModifierConfig entry) {
         return (E)(entry == null
            ? new AlchemyCraftSelectorElement.AlchemyRemoveCraftElement(spatial, this.inputSupplier.get())
            : new AlchemyCraftSelectorElement.AlchemyCraftElement(spatial, this.inputSupplier.get(), entry));
      }

      @Nullable
      protected AlchemyTableHelper.CraftingOption getSelectedCraftingOption() {
         E element = (E)((AlchemyCraftSelectorElement.WorkbenchListElement)this.getSelectedElement());
         if (element == null) {
            return null;
         } else {
            return element instanceof AlchemyCraftSelectorElement.AlchemyCraftElement<?> craftElement
               ? new AlchemyTableHelper.CraftingOption(craftElement.getModifier())
               : new AlchemyTableHelper.CraftingOption(null);
         }
      }
   }

   public abstract static class WorkbenchListElement<E extends AlchemyCraftSelectorElement.WorkbenchListElement<E>> extends SelectableButtonElement<E> {
      private List<ItemStack> inputs;

      public WorkbenchListElement(IPosition position) {
         super(position, ScreenTextures.BUTTON_WORKBENCH_MODIFIER_TEXTURES, () -> {});
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
