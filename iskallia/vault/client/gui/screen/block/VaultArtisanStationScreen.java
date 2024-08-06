package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.ModificationButtonElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.VerticalTextTabElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultArtisanRequestModificationMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class VaultArtisanStationScreen extends AbstractElementContainerScreen<VaultArtisanStationContainer> {
   private final Inventory playerInventory;
   private final Map<VaultArtisanStationContainer.Tab, List<ButtonElement<?>>> tabElements = new HashMap<>();

   public VaultArtisanStationScreen(VaultArtisanStationContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(176, 230));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               ((VaultArtisanStationContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 137), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.createActionButtons();
      List<VerticalTextTabElement.TabInfo> tabList = new ArrayList<>();

      for (VaultArtisanStationContainer.Tab tab : VaultArtisanStationContainer.Tab.values()) {
         tabList.add(new VerticalTextTabElement.TabInfo(tab.getName(), () -> this.selectTab(tab), () -> {}));
      }

      this.addElement(
         (VerticalTextTabElement)new VerticalTextTabElement(
               Spatials.copyPosition(this.getGuiSpatial()).translateXY(-7, 5),
               tabList,
               ScreenTextures.TAB_BACKGROUND_LEFT_9,
               ScreenTextures.TAB_BACKGROUND_LEFT_9_DISABLED
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
   }

   private void createActionButtons() {
      this.tabElements.clear();

      for (GearModificationAction action : ((VaultArtisanStationContainer)this.getMenu()).getModificationActions()) {
         Slot slot = action.getCorrespondingSlot((VaultArtisanStationContainer)this.getMenu());
         if (slot != null) {
            IMutableSpatial btnPosition = Spatials.positionXY(slot.x - 1, slot.y - 1).translateX(action.side().getXShift());
            ModificationButtonElement<?> button = new ModificationButtonElement(
                  btnPosition, () -> this.attemptCraft(action), (VaultArtisanStationContainer)this.getMenu(), action.modification()
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui));
            button.setDisabled(() -> !action.canApply((VaultArtisanStationContainer)this.getMenu(), this.playerInventory.player));
            this.addElement(button);
            this.tabElements.computeIfAbsent(action.tab(), tab -> new ArrayList<>()).add(button);
         }
      }
   }

   public void selectTab(VaultArtisanStationContainer.Tab tab) {
      this.tabElements.values().forEach(buttons -> buttons.forEach(btn -> {
         btn.setVisible(false);
         btn.setEnabled(false);
      }));
      this.tabElements.getOrDefault(tab, Collections.emptyList()).forEach(btn -> {
         btn.setVisible(true);
         btn.setEnabled(true);
      });
      VaultArtisanStationContainer container = (VaultArtisanStationContainer)this.getMenu();

      for (GearModificationAction action : container.getModificationActions()) {
         if (action.getCorrespondingSlot(container) instanceof TabSlot tabSlot) {
            tabSlot.setActive(tab == action.tab());
         }
      }
   }

   private void attemptCraft(GearModificationAction action) {
      if (action.canApply((VaultArtisanStationContainer)this.getMenu(), this.playerInventory.player)) {
         VaultArtisanRequestModificationMessage msg = new VaultArtisanRequestModificationMessage(action.modification().getRegistryName());
         ModNetwork.CHANNEL.sendToServer(msg);
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
