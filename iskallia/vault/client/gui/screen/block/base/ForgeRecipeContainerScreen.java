package iskallia.vault.client.gui.screen.block.base;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.client.ClientForgeRecipesData;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.CraftingSelectorElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.OutputSlotElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TextInputElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultForgeRequestCraftMessage;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class ForgeRecipeContainerScreen<V extends ForgeRecipeTileEntity, T extends ForgeRecipeContainer<V>> extends AbstractElementContainerScreen<T> {
   private final ButtonElement<?> craftButton;
   private final Inventory playerInventory;
   private final CraftingSelectorElement<?> craftingSelectorElement;
   private final TextInputElement<?> levelInput;
   private VaultForgeRecipe selectedRecipe = null;

   public ForgeRecipeContainerScreen(T container, Inventory inventory, Component title, int height) {
      this(container, inventory, title, height, 176);
   }

   public ForgeRecipeContainerScreen(T container, Inventory inventory, Component title, int height, int width) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(width, height));
      V tile = (V)((ForgeRecipeContainer)this.getMenu()).getTile();
      if (tile == null) {
         this.craftButton = null;
         this.craftingSelectorElement = null;
         this.levelInput = null;
      } else {
         this.addBackgroundElement();
         this.addElement(
            (LabelElement)new LabelElement(
                  Spatials.positionXY(8, 7), tile.getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
         MutableComponent inventoryName = inventory.getDisplayName().copy();
         inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
         this.addElement(
            (LabelElement)new LabelElement(
                  Spatials.positionXY(((ForgeRecipeContainer)this.getMenu()).getPlayerInventoryOffset().x, height - 93),
                  inventoryName,
                  LabelTextStyle.defaultStyle()
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
         List<Slot> normalSlots = ((ForgeRecipeContainer)this.getMenu()).slots;
         this.addElement(
            (SlotsElement)new SlotsElement(Spatials.zero(), normalSlots.subList(0, normalSlots.size() - 1), ScreenTextures.INSET_ITEM_SLOT_BACKGROUND)
               .layout((screen, gui, parent, world) -> world.positionXY(gui))
         );
         this.addElement(
            (OutputSlotElement)new OutputSlotElement(
                  Spatials.zero(), (Slot)Iterables.getLast(normalSlots), ScreenTextures.INSET_CRAFTING_RESULT_SLOT_BACKGROUND
               )
               .layout((screen, gui, parent, world) -> world.positionXY(gui))
         );
         this.craftingSelectorElement = this.addElement(this.createCraftingSelector());
         int offsetX = ((ForgeRecipeContainer)this.getMenu()).getOffset().x;
         int offsetY = ((ForgeRecipeContainer)this.getMenu()).getOffset().y;
         this.addElement(
            this.craftButton = new ButtonElement(
                  Spatials.positionXY(this.craftingSelectorElement.right() + 3, height - 133), ScreenTextures.BUTTON_CRAFT_TEXTURES, this::onCraftClick
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
         this.craftButton.setDisabled(true);
         this.levelInput = ((TextInputElement)this.addElement(
               (TextInputElement)new TextInputElement(Spatials.positionXY(143, offsetY - 1).size(26, 12), Minecraft.getInstance().font)
                  .layout((screen, gui, parent, world) -> world.translateXY(gui))
            ))
            .adjustEditBox(editBox -> {
               editBox.setFilter(input -> {
                  if (input.isEmpty()) {
                     return true;
                  } else {
                     int parsedLevel;
                     try {
                        parsedLevel = Integer.parseInt(input);
                     } catch (NumberFormatException var3x) {
                        return false;
                     }

                     return parsedLevel <= VaultBarOverlay.vaultLevel && parsedLevel <= ModConfigs.LEVELS_META.getMaxLevel();
                  }
               });
               editBox.setMaxLength(3);
               editBox.setValue(String.valueOf(VaultBarOverlay.vaultLevel));
            });
         this.levelInput.setVisible(false);
         this.levelInput.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (!this.levelInput.isVisible()) {
               return false;
            } else {
               Component cmp = new TextComponent("Level of crafted gear");
               tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
               return true;
            }
         });
      }
   }

   protected void addBackgroundElement() {
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
   }

   @Nonnull
   protected abstract CraftingSelectorElement<?> createCraftingSelector();

   protected void setLevelInputVisible(boolean visible) {
      if (this.levelInput != null) {
         boolean changed = this.levelInput.isVisible() != visible;
         this.levelInput.setVisible(visible);
         if (changed && !visible) {
            this.levelInput.setInput(String.valueOf(VaultBarOverlay.vaultLevel));
         }
      }
   }

   protected int getCraftedLevel() {
      int requestedLevel = VaultBarOverlay.vaultLevel;
      if (this.levelInput != null) {
         String input = this.levelInput.getInput();
         if (!input.isEmpty()) {
            try {
               requestedLevel = Mth.clamp(Integer.parseInt(input), 0, Math.min(VaultBarOverlay.vaultLevel, ModConfigs.LEVELS_META.getMaxLevel()));
            } catch (NumberFormatException var4) {
            }
         }
      }

      return requestedLevel;
   }

   protected void containerTick() {
      super.containerTick();
      if (this.craftingSelectorElement != null) {
         this.craftButton.setDisabled(!this.craftingSelectorElement.canCraftSelectedEntry());
      }

      if (this.levelInput != null) {
         this.levelInput.tickEditBox();
      }
   }

   protected CraftingSelectorElement<?> makeCraftingSelector() {
      return this.makeCraftingSelector(ObservableSupplier.empty());
   }

   protected CraftingSelectorElement<?> makeCraftingSelector(ObservableSupplier<Set<ResourceLocation>> discoveredRecipes) {
      if (this.craftingSelectorElement != null) {
         return this.craftingSelectorElement;
      } else {
         int slotWidth = 0;
         List<VaultForgeRecipe> recipes = Collections.emptyList();
         V tile = (V)((ForgeRecipeContainer)this.getMenu()).getTile();
         if (tile != null) {
            recipes = ClientForgeRecipesData.getRecipes(tile.getSupportedRecipeTypes());
            slotWidth = Mth.ceil(tile.getInventory().getContainerSize() / 3.0F);
         }

         int offsetX = ((ForgeRecipeContainer)this.getMenu()).getOffset().x;
         return new CraftingSelectorElement(
               Spatials.positionXY(offsetX + slotWidth * 18 + 3, this.imageHeight - 151).height(54),
               3,
               recipes,
               discoveredRecipes,
               this::onRecipeSelect,
               this::getMissingRecipeInputs
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui));
      }
   }

   public Inventory getPlayerInventory() {
      return this.playerInventory;
   }

   private void onCraftClick() {
      if (this.selectedRecipe != null) {
         ModNetwork.CHANNEL.sendToServer(new VaultForgeRequestCraftMessage(this.selectedRecipe.getId(), this.getCraftedLevel()));
      }
   }

   protected void onRecipeSelect(VaultForgeRecipe recipe, boolean canCraft) {
      this.craftButton.setDisabled(!canCraft);
      this.selectedRecipe = recipe;
      this.setLevelInputVisible(recipe.usesLevel());
   }

   protected List<ItemStack> getMissingRecipeInputs(List<ItemStack> inputs) {
      ForgeRecipeTileEntity tile = ((ForgeRecipeContainer)this.menu).getTile();
      return tile == null ? inputs : InventoryUtil.getMissingInputs(inputs, this.getPlayerInventory(), tile.getInventory());
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      Key key = InputConstants.getKey(keyCode, scanCode);
      if (this.levelInput != null && this.levelInput.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (!Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
         return super.keyPressed(keyCode, scanCode, modifiers);
      } else {
         if (this.levelInput == null || !this.levelInput.isFocused()) {
            this.onClose();
         }

         return true;
      }
   }

   public boolean charTyped(char codePoint, int modifiers) {
      return this.levelInput != null && this.levelInput.charTyped(codePoint, modifiers) ? true : super.charTyped(codePoint, modifiers);
   }
}
