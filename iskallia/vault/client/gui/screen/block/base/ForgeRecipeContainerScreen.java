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
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.gear.crafting.VaultForgeHelper;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultForgeRequestCraftMessage;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class ForgeRecipeContainerScreen<V extends ForgeRecipeTileEntity & MenuProvider, T extends ForgeRecipeContainer<V>>
   extends AbstractElementContainerScreen<T> {
   private final ButtonElement<?> craftButton;
   private final Inventory playerInventory;
   private final CraftingSelectorElement<?> craftingSelectorElement;
   private VaultForgeRecipe selectedRecipe = null;

   public ForgeRecipeContainerScreen(T container, Inventory inventory, Component title, int height) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      int width = 176;
      this.setGuiSize(Spatials.size(width, height));
      V tile = (V)((ForgeRecipeContainer)this.getMenu()).getTile();
      if (tile == null) {
         this.craftButton = null;
         this.craftingSelectorElement = null;
      } else {
         this.addElement(
            (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
               .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
         );
         this.addElement(
            (LabelElement)new LabelElement(
                  Spatials.positionXY(8, 7), tile.getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
         MutableComponent inventoryName = inventory.getDisplayName().copy();
         inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
         this.addElement(
            (LabelElement)new LabelElement(Spatials.positionXY(8, height - 93), inventoryName, LabelTextStyle.defaultStyle())
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
         this.addElement(
            this.craftButton = new ButtonElement(
                  Spatials.positionXY(this.imageWidth - 54, this.imageHeight - 133), ScreenTextures.BUTTON_CRAFT_TEXTURES, this::onCraftClick
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
         this.craftButton.setDisabled(true);
         this.craftingSelectorElement = this.addElement(this.createCraftingSelector());
      }
   }

   @Nonnull
   protected abstract CraftingSelectorElement<?> createCraftingSelector();

   protected void containerTick() {
      super.containerTick();
      if (this.craftingSelectorElement != null) {
         this.craftButton.setDisabled(!this.craftingSelectorElement.canCraftSelectedEntry());
      }
   }

   protected CraftingSelectorElement<?> makeCraftingSelector() {
      return this.makeCraftingSelector(ObservableSupplier.empty());
   }

   protected CraftingSelectorElement<?> makeCraftingSelector(ObservableSupplier<Set<ResourceLocation>> discoveredRecipes) {
      if (this.craftingSelectorElement != null) {
         return this.craftingSelectorElement;
      } else {
         List<VaultForgeRecipe> recipes = Collections.emptyList();
         V tile = (V)((ForgeRecipeContainer)this.getMenu()).getTile();
         if (tile != null) {
            recipes = ClientForgeRecipesData.getRecipes(tile.getSupportedRecipeTypes());
         }

         return new CraftingSelectorElement(
               Spatials.positionXY(47, this.imageHeight - 151).height(54), 3, recipes, discoveredRecipes, this::onRecipeSelect, this::getMissingRecipeInputs
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui));
      }
   }

   public Inventory getPlayerInventory() {
      return this.playerInventory;
   }

   private void onCraftClick() {
      if (this.selectedRecipe != null) {
         ModNetwork.CHANNEL.sendToServer(new VaultForgeRequestCraftMessage(this.selectedRecipe.getId()));
      }
   }

   protected void onRecipeSelect(VaultForgeRecipe recipe, boolean canCraft) {
      this.craftButton.setDisabled(!canCraft);
      this.selectedRecipe = recipe;
   }

   protected List<ItemStack> getMissingRecipeInputs(List<ItemStack> inputs) {
      ForgeRecipeTileEntity tile = ((ForgeRecipeContainer)this.menu).getTile();
      return tile == null ? inputs : VaultForgeHelper.getMissingInputs(inputs, this.getPlayerInventory(), tile.getInventory());
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      Key key = InputConstants.getKey(pKeyCode, pScanCode);
      if (pKeyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
         return super.keyPressed(pKeyCode, pScanCode, pModifiers);
      } else {
         this.onClose();
         return true;
      }
   }
}
