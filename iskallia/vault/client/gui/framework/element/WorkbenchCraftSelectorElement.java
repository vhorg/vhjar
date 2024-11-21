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
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.gear.data.VaultGearData;
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

public class WorkbenchCraftSelectorElement<E extends WorkbenchCraftSelectorElement<E, V>, V extends WorkbenchCraftSelectorElement.WorkbenchListElement<V>>
   extends ScrollableListSelectorElement<E, VaultGearWorkbenchConfig.CraftableModifierConfig, V> {
   private final ObservableSupplier<ItemStack> inputSupplier;

   public WorkbenchCraftSelectorElement(ISpatial spatial, ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
      super(
         Spatials.copy(spatial).width(ScreenTextures.BUTTON_WORKBENCH_MODIFIER_TEXTURES.button().width()),
         new WorkbenchCraftSelectorElement.WorkbenchCraftSelectorModel<>(inputSupplier, searchFilter)
      );
      this.inputSupplier = inputSupplier;
   }

   public void onSelect(Consumer<ModifierWorkbenchHelper.CraftingOption> fn) {
      if (this.getSelectorModel() instanceof WorkbenchCraftSelectorElement.WorkbenchCraftSelectorModel<?> selModel) {
         selModel.whenSelected(cfg -> {
            ModifierWorkbenchHelper.CraftingOption option = selModel.getSelectedCraftingOption();
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

   public static class WorkbenchCraftElement<E extends WorkbenchCraftSelectorElement.WorkbenchCraftElement<E>>
      extends WorkbenchCraftSelectorElement.WorkbenchListElement<E> {
      private final ItemStack gearStack;
      private final VaultGearWorkbenchConfig.CraftableModifierConfig modifier;
      private final LabelTextStyle textStyle;
      private final VaultGearModifier<?> displayModifier;

      public WorkbenchCraftElement(IPosition position, ItemStack gearStack, VaultGearWorkbenchConfig.CraftableModifierConfig modifier) {
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
                     ModifierWorkbenchHelper.removeCraftedModifiers(gearCopy);
                     VaultGearData data = VaultGearData.read(gearCopy);
                     Set<String> groups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
                     if (this.displayModifier != null && groups.contains(this.displayModifier.getModifierGroup())) {
                        MutableComponent cmp = new TextComponent("Item already has a modifier of this group.").withStyle(ChatFormatting.RED);
                        tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                        return true;
                     }
                  }

                  VaultGearModifier.AffixType affixType = this.modifier.getAffixGroup().getTargetAffixType();
                  if (affixType == null) {
                     MutableComponent cmp = new TextComponent("This modifier cannot be applied.").withStyle(ChatFormatting.RED);
                     tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                     return true;
                  } else {
                     String affix = affixType.getSingular();
                     MutableComponent cmp = new TextComponent("Item has no open " + affix).withStyle(ChatFormatting.RED);
                     tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                     return true;
                  }
               }
            }
         });
      }

      public VaultGearWorkbenchConfig.CraftableModifierConfig getModifier() {
         return this.modifier;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         this.setDisabled(!this.canCraft());
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         poseStack.pushPose();
         poseStack.translate(0.0, 0.0, 1.0);
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

         poseStack.popPose();
      }

      @Override
      protected List<ItemStack> createNeededInputs() {
         return this.modifier.createCraftingCost(this.gearStack);
      }

      public Optional<MutableComponent> getCraftedModifierDescription() {
         return this.displayModifier.getConfigDisplay(this.gearStack);
      }

      private boolean hasAffixSpace() {
         ItemStack inputCopy = this.gearStack.copy();
         ModifierWorkbenchHelper.removeCraftedModifiers(inputCopy);
         VaultGearModifier.AffixType affixType = this.modifier.getAffixGroup().getTargetAffixType();
         if (affixType == null) {
            return false;
         } else {
            return affixType == VaultGearModifier.AffixType.PREFIX
               ? VaultGearModifierHelper.hasOpenPrefix(inputCopy)
               : VaultGearModifierHelper.hasOpenSuffix(inputCopy);
         }
      }

      private boolean hasGroupApplied() {
         if (this.gearStack.isEmpty()) {
            return false;
         } else {
            ItemStack gearCopy = this.gearStack.copy();
            ModifierWorkbenchHelper.removeCraftedModifiers(gearCopy);
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
            && (!ModifierWorkbenchHelper.hasCraftedModifier(this.gearStack) || ModifierWorkbenchHelper.removeCraftedModifiers(this.gearStack.copy()))
            && !this.hasGroupApplied()
            && this.hasAffixSpace();
      }
   }

   public static class WorkbenchCraftSelectorModel<E extends WorkbenchCraftSelectorElement.WorkbenchListElement<E>>
      extends ScrollableListSelectorElement.SelectorModel<E, VaultGearWorkbenchConfig.CraftableModifierConfig> {
      private final ObservableSupplier<ItemStack> inputSupplier;
      private final Supplier<String> searchFilter;

      public WorkbenchCraftSelectorModel(ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
         this.inputSupplier = inputSupplier;
         this.searchFilter = searchFilter;
      }

      @Override
      public List<VaultGearWorkbenchConfig.CraftableModifierConfig> getEntries() {
         ItemStack input = this.inputSupplier.get();
         if (input.isEmpty()) {
            return Collections.emptyList();
         } else {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
               return Collections.emptyList();
            } else {
               String searchTerm = this.searchFilter.get().toLowerCase(Locale.ROOT);
               List<VaultGearWorkbenchConfig.CraftableModifierConfig> out = new ArrayList<>();
               String locRemove = new TranslatableComponent("the_vault.gear_workbench.remove_crafted_modifiers").getString();
               if (searchTerm.isEmpty() || locRemove.toLowerCase(Locale.ROOT).contains(searchTerm)) {
                  out.add(null);
               }

               VaultGearWorkbenchConfig.getConfig(input.getItem())
                  .map(VaultGearWorkbenchConfig::getAllCraftableModifiers)
                  .ifPresent(
                     craftingConfigs -> craftingConfigs.forEach(
                        cfg -> cfg.createModifier().flatMap(modifier -> modifier.getConfigDisplay(input)).ifPresent(display -> {
                           String locDisplay = display.getString().toLowerCase(Locale.ROOT);
                           if (searchTerm.isEmpty() || locDisplay.contains(searchTerm)) {
                              out.add(cfg);
                           }
                        })
                     )
                  );
               int playerLevel = SidedHelper.getVaultLevel(player);
               out.removeIf(
                  cfg -> cfg != null && cfg.getUnlockCategory() == VaultGearWorkbenchConfig.UnlockCategory.VAULT_DISCOVERY && cfg.getMinLevel() > playerLevel
               );
               return out;
            }
         }
      }

      public E createSelectable(ISpatial spatial, VaultGearWorkbenchConfig.CraftableModifierConfig entry) {
         return (E)(entry == null
            ? new WorkbenchCraftSelectorElement.WorkbenchRemoveCraftElement(spatial, this.inputSupplier.get())
            : new WorkbenchCraftSelectorElement.WorkbenchCraftElement(spatial, this.inputSupplier.get(), entry));
      }

      @Nullable
      protected ModifierWorkbenchHelper.CraftingOption getSelectedCraftingOption() {
         E element = (E)((WorkbenchCraftSelectorElement.WorkbenchListElement)this.getSelectedElement());
         if (element == null) {
            return null;
         } else {
            return element instanceof WorkbenchCraftSelectorElement.WorkbenchCraftElement<?> craftElement
               ? new ModifierWorkbenchHelper.CraftingOption(craftElement.getModifier())
               : new ModifierWorkbenchHelper.CraftingOption(null);
         }
      }
   }

   public abstract static class WorkbenchListElement<E extends WorkbenchCraftSelectorElement.WorkbenchListElement<E>> extends SelectableButtonElement<E> {
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

   public static class WorkbenchRemoveCraftElement<E extends WorkbenchCraftSelectorElement.WorkbenchRemoveCraftElement<E>>
      extends WorkbenchCraftSelectorElement.WorkbenchListElement<E> {
      private final ItemStack gearStack;
      private final LabelTextStyle textStyle;

      public WorkbenchRemoveCraftElement(IPosition position, ItemStack gearStack) {
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
         poseStack.pushPose();
         poseStack.translate(0.0, 0.0, 1.0);
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
         poseStack.popPose();
      }

      @Override
      protected List<ItemStack> createNeededInputs() {
         return this.gearStack.isEmpty()
            ? Collections.emptyList()
            : VaultGearWorkbenchConfig.getConfig(this.gearStack.getItem())
               .map(VaultGearWorkbenchConfig::getCostRemoveCraftedModifiers)
               .orElse(Collections.emptyList());
      }

      private boolean canCraft() {
         return !this.gearStack.isEmpty()
            && ModifierWorkbenchHelper.hasCraftedModifier(this.gearStack)
            && ModifierWorkbenchHelper.removeCraftedModifiers(this.gearStack.copy());
      }
   }
}
