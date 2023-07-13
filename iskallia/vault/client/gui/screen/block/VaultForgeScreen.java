package iskallia.vault.client.gui.screen.block;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.CraftingSelectorElement;
import iskallia.vault.client.gui.framework.element.ProficiencyDisplayElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.screen.block.base.ForgeRecipeContainerScreen;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.gear.crafting.ProficiencyType;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VaultForgeScreen extends ForgeRecipeContainerScreen<VaultForgeTileEntity, VaultForgeContainer> {
   public VaultForgeScreen(VaultForgeContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, 206, 206);
      int xPadding = 4;
      int availableWidth = this.imageWidth - xPadding * 2;
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

   @Override
   protected void addBackgroundElement() {
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(this.getGuiSpatial(), ScreenTextures.VAULT_FORGE_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
   }

   @Nonnull
   @Override
   protected CraftingSelectorElement<?> createCraftingSelector() {
      return this.makeCraftingSelector(ClientDiscoveredEntriesData.Trinkets.getObserverTrinkets());
   }
}
