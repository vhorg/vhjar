package iskallia.vault.client.gui.screen;

import iskallia.vault.block.entity.CatalystInfusionTableTileEntity;
import iskallia.vault.client.gui.framework.element.CraftingSelectorElement;
import iskallia.vault.client.gui.screen.block.base.ForgeRecipeContainerScreen;
import iskallia.vault.container.inventory.CatalystInfusionTableContainer;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CatalystInfusionTableScreen extends ForgeRecipeContainerScreen<CatalystInfusionTableTileEntity, CatalystInfusionTableContainer> {
   public CatalystInfusionTableScreen(CatalystInfusionTableContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, 173);
   }

   @Nonnull
   @Override
   protected CraftingSelectorElement<?> createCraftingSelector() {
      return this.makeCraftingSelector();
   }
}
