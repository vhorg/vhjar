package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.JewelCuttingButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.network.message.VaultJewelCuttingRequestModificationMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class VaultJewelCuttingStationScreen extends AbstractElementContainerScreen<VaultJewelCuttingStationContainer> {
   private final Inventory playerInventory;

   public VaultJewelCuttingStationScreen(VaultJewelCuttingStationContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(176, 170));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               ((VaultJewelCuttingStationContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 77), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      Slot slot = ((VaultJewelCuttingStationContainer)this.getMenu()).getJewelInputSlot();
      if (slot != null) {
         IMutableSpatial btnPosition = Spatials.positionXY(slot.x - 1, slot.y - 1).translateY(-10).translateX(40);
         JewelCuttingButtonElement<?> button = new JewelCuttingButtonElement(btnPosition, () -> {
            VaultJewelCuttingRequestModificationMessage msg = new VaultJewelCuttingRequestModificationMessage();
            ModNetwork.CHANNEL.sendToServer(msg);
         }, (VaultJewelCuttingStationContainer)this.getMenu()).layout((screen, gui, parent, world) -> world.translateXY(gui));
         button.setDisabled(
            () -> {
               if (((VaultJewelCuttingStationContainer)this.getMenu()).getTileEntity() != null
                  && !((VaultJewelCuttingStationContainer)this.getMenu()).getTileEntity().canCraft()) {
                  return true;
               } else if (((VaultJewelCuttingStationContainer)this.getMenu()).getJewelInputSlot().getItem().getItem() instanceof JewelItem) {
                  VaultGearData data = VaultGearData.read(((VaultJewelCuttingStationContainer)this.getMenu()).getJewelInputSlot().getItem());
                  return data.getFirstValue(ModGearAttributes.JEWEL_SIZE).orElse(0) <= 10;
               } else {
                  return true;
               }
            }
         );
         this.addElement(button);
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
