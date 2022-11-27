package iskallia.vault.client.gui.screen.block;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.client.ClientVaultForgeData;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.CraftingSelectorElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.OutputSlotElement;
import iskallia.vault.client.gui.framework.element.ProficiencyDisplayElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.crafting.VaultForgeHelper;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultForgeRequestCraftMessage;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class VaultForgeScreen extends AbstractElementContainerScreen<VaultForgeContainer> {
   private final ButtonElement<?> craftButton;
   private final Inventory playerInventory;
   private VaultForgeRecipe selectedRecipe = null;

   public VaultForgeScreen(VaultForgeContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      int width = 176;
      this.setGuiSize(Spatials.size(width, 206));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               ((VaultForgeContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 113), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      List<Slot> normalSlots = ((VaultForgeContainer)this.getMenu()).slots;
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.zero(), normalSlots.subList(0, normalSlots.size() - 1), ScreenTextures.INSET_ITEM_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.addElement(
         (OutputSlotElement)new OutputSlotElement(Spatials.zero(), (Slot)Iterables.getLast(normalSlots), ScreenTextures.INSET_CRAFTING_RESULT_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.addElement(
         (CraftingSelectorElement)new CraftingSelectorElement(
               Spatials.positionXY(47, 55).height(54),
               3,
               ClientVaultForgeData.getRecipes(),
               ClientDiscoveredEntriesData.Trinkets.getObserverTrinkets(),
               this::onRecipeSelect,
               this::getMissingRecipeInputs
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         this.craftButton = new ButtonElement(Spatials.positionXY(122, 73), ScreenTextures.BUTTON_CRAFT_TEXTURES, this::onCraftClick)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.craftButton.setDisabled(true);
      int xPadding = 8;
      int availableWidth = width - xPadding * 2;
      int elementWidth = ScreenTextures.PROFICIENCY_DISPLAY_FRAME.width() + 2;
      List<ProficiencyType> displayTypes = ProficiencyType.getCraftableTypes();
      int remainingWidth = availableWidth - displayTypes.size() * elementWidth;
      int spacing = Math.round((float)remainingWidth / displayTypes.size());

      for (int i = 0; i < displayTypes.size(); i++) {
         ProficiencyType type = displayTypes.get(i);
         int xOffset = i * spacing + i * elementWidth;
         this.addElement(
            (ProficiencyDisplayElement)new ProficiencyDisplayElement(Spatials.zero(), type)
               .layout((screen, gui, parent, world) -> world.positionXY(gui).translateXY(xPadding + xOffset, 18))
         );
      }
   }

   private List<ItemStack> getMissingRecipeInputs(List<ItemStack> inputs) {
      return VaultForgeHelper.getMissingInputs(inputs, this.playerInventory, ((VaultForgeContainer)this.menu).getTileEntity());
   }

   private void onCraftClick() {
      if (this.selectedRecipe != null) {
         ModNetwork.CHANNEL.sendToServer(new VaultForgeRequestCraftMessage(this.selectedRecipe.getId()));
      }
   }

   private void onRecipeSelect(VaultForgeRecipe recipe) {
      this.craftButton.setDisabled(false);
      this.selectedRecipe = recipe;
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