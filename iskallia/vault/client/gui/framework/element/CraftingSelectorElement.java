package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftingSelectorElement<E extends CraftingSelectorElement<E>> extends ScrollableItemStackSelectorElement<E, CraftingSelectorElement.CraftingEntry> {
   private final ObservableSupplier<Set<ResourceLocation>> discoveredTrinkets;

   public CraftingSelectorElement(
      ISpatial spatial,
      int slotColumns,
      List<VaultForgeRecipe> recipes,
      ObservableSupplier<Set<ResourceLocation>> discoveredTrinkets,
      BiConsumer<VaultForgeRecipe, Boolean> onRecipeSelect,
      Function<List<ItemStack>, List<ItemStack>> inputItemCheck
   ) {
      this(spatial, slotColumns, recipes, discoveredTrinkets, onRecipeSelect, inputItemCheck, () -> VaultBarOverlay.vaultLevel);
   }

   public CraftingSelectorElement(
      ISpatial spatial,
      int slotColumns,
      List<VaultForgeRecipe> recipes,
      ObservableSupplier<Set<ResourceLocation>> discoveredTrinkets,
      BiConsumer<VaultForgeRecipe, Boolean> onRecipeSelect,
      Function<List<ItemStack>, List<ItemStack>> inputItemCheck,
      Supplier<Integer> craftingLevelSupplier
   ) {
      super(spatial, slotColumns, new CraftingSelectorElement.CraftingSelector(recipes, onRecipeSelect, inputItemCheck, craftingLevelSupplier));
      this.discoveredTrinkets = discoveredTrinkets;
      this.selectorModel.onSlotSelect(this::changeSelection);
   }

   private void changeSelection(FakeItemSlotElement<?> slotElement) {
      this.getSelectorElements().forEach(fakeSlot -> {
         if (fakeSlot instanceof SelectableFakeItemSlotElement<?> selectableSlotx) {
            selectableSlotx.setSelected(false);
         }
      });
      if (slotElement instanceof SelectableFakeItemSlotElement<?> selectableSlot) {
         selectableSlot.setSelected(true);
      }
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      this.discoveredTrinkets.ifChanged(models -> this.refreshElements());
   }

   public boolean canCraftSelectedEntry() {
      if (this.getSelectorModel() instanceof CraftingSelectorElement.CraftingSelector craftingSelector) {
         CraftingSelectorElement.CraftingEntry entry = (CraftingSelectorElement.CraftingEntry)craftingSelector.getSelectedElement();
         if (entry != null) {
            return entry.canCraft();
         }
      }

      return false;
   }

   @Override
   protected FakeItemSlotElement<?> makeElementSlot(
      ISpatial spatial, Supplier<ItemStack> itemStack, TextureAtlasRegion slotTexture, TextureAtlasRegion disabledSlotTexture, Supplier<Boolean> disabled
   ) {
      return new SelectableFakeItemSlotElement(spatial, itemStack, slotTexture, disabledSlotTexture, disabled);
   }

   public static class CraftingEntry extends ScrollableItemStackSelectorElement.ItemSelectorEntry {
      private final VaultForgeRecipe recipe;
      private final Function<List<ItemStack>, List<ItemStack>> inputItemCheck;
      private final Supplier<Integer> craftingLevelSupplier;

      public CraftingEntry(VaultForgeRecipe recipe, Function<List<ItemStack>, List<ItemStack>> inputItemCheck, Supplier<Integer> craftingLevelSupplier) {
         super(recipe.getDisplayOutput(craftingLevelSupplier.get()), Minecraft.getInstance().player == null || !recipe.canCraft(Minecraft.getInstance().player));
         this.recipe = recipe;
         this.inputItemCheck = inputItemCheck;
         this.craftingLevelSupplier = craftingLevelSupplier;
      }

      public boolean canCraft() {
         List<ItemStack> inputs = this.recipe.getInputs();
         return this.inputItemCheck.apply(inputs).isEmpty();
      }

      @Override
      public ItemStack getDisplayStack() {
         return this.recipe.getDisplayOutput(this.craftingLevelSupplier.get());
      }

      @Override
      public void adjustSlot(FakeItemSlotElement<?> slot) {
         slot.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 500.0);
            if (this.isDisabled()) {
               tooltipRenderer.renderTooltip(poseStack, this.recipe.getDisabledText(), mouseX, mouseY, TooltipDirection.RIGHT);
               poseStack.popPose();
               return true;
            } else {
               List<ItemStack> inputs = this.recipe.getInputs();
               List<ItemStack> missingInputs = this.inputItemCheck.apply(inputs);
               List<Component> text = new ArrayList<>();
               ItemStack result = this.recipe.getDisplayOutput(this.craftingLevelSupplier.get());
               text.add(new TextComponent("Craft: ").append(result.getHoverName()));
               this.recipe.addCraftingDisplayTooltip(result, text);

               for (ItemStack in : this.recipe.getInputs()) {
                  ChatFormatting color = ChatFormatting.GREEN;
                  if (missingInputs.contains(in)) {
                     color = ChatFormatting.RED;
                  }

                  text.add(new TextComponent("- ").append(in.getHoverName()).append(" x" + in.getCount()).withStyle(color));
               }

               tooltipRenderer.renderComponentTooltip(poseStack, text, mouseX, mouseY, TooltipDirection.RIGHT);
               poseStack.popPose();
               return true;
            }
         });
      }
   }

   public static class CraftingSelector extends ScrollableItemStackSelectorElement.SelectorModel<CraftingSelectorElement.CraftingEntry> {
      private final List<VaultForgeRecipe> recipes;
      private final BiConsumer<VaultForgeRecipe, Boolean> onRecipeSelect;
      private final Function<List<ItemStack>, List<ItemStack>> inputItemCheck;
      private final Supplier<Integer> craftingLevelSupplier;

      public CraftingSelector(
         List<VaultForgeRecipe> recipes,
         BiConsumer<VaultForgeRecipe, Boolean> onRecipeSelect,
         Function<List<ItemStack>, List<ItemStack>> inputItemCheck,
         Supplier<Integer> craftingLevelSupplier
      ) {
         this.recipes = recipes;
         this.onRecipeSelect = onRecipeSelect;
         this.inputItemCheck = inputItemCheck;
         this.craftingLevelSupplier = craftingLevelSupplier;
      }

      @Override
      public List<CraftingSelectorElement.CraftingEntry> getEntries() {
         return this.recipes.stream().map(entry -> new CraftingSelectorElement.CraftingEntry(entry, this.inputItemCheck, this.craftingLevelSupplier)).toList();
      }

      public void onSelect(FakeItemSlotElement<?> slot, CraftingSelectorElement.CraftingEntry entry) {
         super.onSelect(slot, entry);
         this.onRecipeSelect.accept(entry.recipe, entry.canCraft());
      }
   }
}
