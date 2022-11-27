package iskallia.vault.client.gui.screen.block;

import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.FakeItemSlotElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.ScrollableItemStackSelectorElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.SpiritExtractorContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpiritExtractorScreen extends AbstractElementContainerScreen<SpiritExtractorContainer> {
   public SpiritExtractorScreen(SpiritExtractorContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(176, 182));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXY(0, -10).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui).add(Spatials.size(0, 10))))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(7, -4), new TextComponent("Spirit Extractor").withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (ScrollableItemStackSelectorElement)new ScrollableItemStackSelectorElement(
               Spatials.positionXY(7, 6).height(54),
               8,
               new ScrollableItemStackSelectorElement.SelectorModel<ScrollableItemStackSelectorElement.ItemSelectorEntry>() {
                  @Override
                  public List<ScrollableItemStackSelectorElement.ItemSelectorEntry> getEntries() {
                     return ((SpiritExtractorContainer)SpiritExtractorScreen.this.getMenu())
                        .getStoredItems()
                        .stream()
                        .map(SpiritExtractorScreen.ItemSelectorWithTooltipEntry::new)
                        .collect(Collectors.toList());
                  }
               }
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, ((SpiritExtractorContainer)this.getMenu()).getSlot(0).y - 12), inventoryName, LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      ButtonElement<?> purchaseButton = new ButtonElement(
            Spatials.positionXY(53, 69), ScreenTextures.BUTTON_PAY_TEXTURES, () -> ((SpiritExtractorContainer)this.getMenu()).startSpewingItems()
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      purchaseButton.setDisabled(
         () -> !((SpiritExtractorContainer)this.getMenu()).coinsCoverTotalCost() || ((SpiritExtractorContainer)this.getMenu()).isSpewingItems()
      );
      purchaseButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         tooltipRenderer.renderComponentTooltip(poseStack, this.getPurchaseButtonTooltipLines(), mouseX, mouseY, TooltipDirection.RIGHT);
         return true;
      });
      this.addElement(purchaseButton);
   }

   @NotNull
   private List<Component> getPurchaseButtonTooltipLines() {
      List<Component> purchaseButtonTooltips = new ArrayList<>();
      purchaseButtonTooltips.add(new TextComponent("Cost for recovering items"));
      int paymentStackCount = ((SpiritExtractorContainer)this.getMenu()).getSlot(36).getItem().getCount();
      ItemStack totalCost = ((SpiritExtractorContainer)this.getMenu()).getTotalCost();
      ChatFormatting textColor = paymentStackCount < totalCost.getCount() ? ChatFormatting.RED : ChatFormatting.YELLOW;
      String percentString = (int)((float)paymentStackCount / totalCost.getCount() * 100.0F) + "%";
      purchaseButtonTooltips.add(
         new TextComponent(percentString + " " + totalCost.getItem().getName(totalCost).getString() + " (of " + totalCost.getCount() + ")")
            .withStyle(textColor)
      );
      purchaseButtonTooltips.add(new TextComponent(this.getSpiritRecoveryCountText()).withStyle(ChatFormatting.GRAY));
      return purchaseButtonTooltips;
   }

   private String getSpiritRecoveryCountText() {
      int spiritRecoveryCount = ((SpiritExtractorContainer)this.getMenu()).getSpiritRecoveryCount();
      return spiritRecoveryCount <= 0 ? "No Spirits recovered yet" : "Number of Spirits recovered: " + spiritRecoveryCount;
   }

   private static class ItemSelectorWithTooltipEntry extends ScrollableItemStackSelectorElement.ItemSelectorEntry {
      public ItemSelectorWithTooltipEntry(ItemStack displayStack) {
         super(displayStack, false);
      }

      @Override
      public void adjustSlot(FakeItemSlotElement<?> slot) {
         slot.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (!this.getDisplayStack().isEmpty()) {
               tooltipRenderer.renderTooltip(poseStack, this.getDisplayStack(), mouseX, mouseY, TooltipDirection.RIGHT);
            }

            return true;
         });
         slot.setLabelStackCount();
      }
   }
}
