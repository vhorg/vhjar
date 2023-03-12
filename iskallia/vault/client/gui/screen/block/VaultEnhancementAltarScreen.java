package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.VaultEnhancementAltarContainer;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultEnhancementRequestMessage;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VaultEnhancementAltarScreen extends AbstractElementContainerScreen<VaultEnhancementAltarContainer> {
   private final Inventory playerInventory;

   public VaultEnhancementAltarScreen(VaultEnhancementAltarContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(176, 156));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               ((VaultEnhancementAltarContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 63), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      ButtonElement<?> button = this.addElement(
         new ButtonElement(Spatials.positionXY(64, 42), ScreenTextures.BUTTON_EMPTY_16_48_TEXTURES, this::tryCraft)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      button.setDisabled(() -> {
         VaultEnhancementAltarTileEntity tile = ((VaultEnhancementAltarContainer)this.getMenu()).getTileEntity();
         return !tile.canBeUsed(this.playerInventory.player);
      });
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(64, 42).translateY(button.height() / 2 - 9 / 2),
               button,
               new TranslatableComponent("screen.the_vault.enhancement_altar.button"),
               LabelTextStyle.center()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
   }

   private void tryCraft() {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         if (ServerVaults.isInVault(player)) {
            VaultEnhancementAltarTileEntity altarTile = ((VaultEnhancementAltarContainer)this.getMenu()).getTileEntity();
            if (altarTile.canBeUsed(player)) {
               ItemStack gearItem = altarTile.getInventory().getItem(0);
               if (AttributeGearData.hasData(gearItem)) {
                  ModNetwork.CHANNEL.sendToServer(new VaultEnhancementRequestMessage(((VaultEnhancementAltarContainer)this.getMenu()).getTilePos()));
                  altarTile.setUsedByPlayer(player);
               }
            }
         }
      }
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
