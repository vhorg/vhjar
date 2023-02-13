package iskallia.vault.client.gui.screen.block;

import iskallia.vault.block.entity.ToolStationTileEntity;
import iskallia.vault.client.gui.framework.element.CraftingSelectorElement;
import iskallia.vault.client.gui.screen.block.base.ForgeRecipeContainerScreen;
import iskallia.vault.container.ToolStationContainer;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ToolStationScreen extends ForgeRecipeContainerScreen<ToolStationTileEntity, ToolStationContainer> {
   public ToolStationScreen(ToolStationContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, 173);
   }

   @Nonnull
   @Override
   protected CraftingSelectorElement<?> createCraftingSelector() {
      return this.makeCraftingSelector();
   }
}
