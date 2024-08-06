package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.ThreeSliceHorizontalElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.ThreeSliceHorizontal;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.block.TooltipContainerElement;
import iskallia.vault.container.inventory.CardDeckContainer;
import iskallia.vault.container.inventory.CardDeckContainerMenu;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.CardDeckItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CardDeckScreen extends AbstractElementContainerScreen<CardDeckContainerMenu> implements MenuAccess<CardDeckContainerMenu> {
   private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
   private final TooltipContainerElement tooltip;

   public CardDeckScreen(CardDeckContainerMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.passEvents = false;
      this.setGuiSize(Spatials.size(menu.getTotalWidth(), menu.getTotalHeight()));
      this.addElement(
         (NineSliceElement)new NineSliceElement(
               Spatials.positionXY((menu.getTotalWidth() - menu.getInventoryWidth()) / 2, menu.getTotalHeight() - menu.getInventoryHeight())
                  .size(menu.getInventoryWidth(), menu.getInventoryHeight()),
               ScreenTextures.DEFAULT_WINDOW_BACKGROUND
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(
               Spatials.positionXY((menu.getTotalWidth() - menu.getDeckWidth()) / 2, 0).size(menu.getDeckWidth(), menu.getDeckHeight()),
               ScreenTextures.CARD_DECK_BACKGROUND_9
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.zero(), ((CardDeckContainerMenu)this.getMenu()).getPlayerSlots())
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.zero(), ((CardDeckContainerMenu)this.getMenu()).getCardSlots(), ScreenTextures.INSET_CARD_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.tooltip = this.addElement(
         new TooltipContainerElement(Spatials.positionXY(-116, 0).height(92).width(107), ItemStack.EMPTY)
            .layout((screen, gui, parent, world) -> world.translateXYZ(gui))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.tooltip.getWorldSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout(
               (screen, gui, parent, world) -> world.translateXY(gui)
                  .translateX(-5)
                  .translateY(-5)
                  .translateZ(-10)
                  .width(this.tooltip.width() + 10)
                  .height(this.tooltip.height() + 10)
            )
      );
      ThreeSliceHorizontal.TextureRegion titleIcon = ScreenTextures.CARD_DECK_TITLE_LARGE_3;
      if (menu.getContainer().getDeckWidth() <= 3) {
         titleIcon = ScreenTextures.CARD_DECK_TITLE_SMALL_3;
      }

      String titleStr = ModConfigs.CARD_DECK.getName(CardDeckItem.getId(menu.getDeckStack(playerInventory.player))).orElse("Card Deck");
      Component cardDeckTitle = new TextComponent(titleStr).withStyle(ChatFormatting.DARK_GRAY);
      Font font = Minecraft.getInstance().font;
      int titleWidth = font.width(cardDeckTitle);
      if (titleWidth % 2 == 1) {
         titleWidth--;
      }

      int titleX = (menu.getTotalWidth() - titleWidth) / 2;
      int titleY = 5;
      int elementWidth = titleWidth + titleIcon.slices().left() + titleIcon.slices().right();
      this.addElement(new ThreeSliceHorizontalElement(Spatials.positionXY(titleX - titleIcon.slices().left(), titleY).size(elementWidth, 12), titleIcon))
         .layout((screen, gui, parent, world) -> world.translateXYZ(gui));
      this.addElement(new LabelElement(Spatials.positionXY(titleX, titleY + 2), cardDeckTitle, LabelTextStyle.defaultStyle()))
         .layout((screen, gui, parent, world) -> world.translateXYZ(gui));
   }

   @Override
   public void render(PoseStack matrixStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.tooltip.refresh(((CardDeckContainerMenu)this.menu).getDeckStack(Minecraft.getInstance().player));
      this.renderBackground(matrixStack);
      super.render(matrixStack, pMouseX, pMouseY, pPartialTick);
      this.renderMatchingNeighbors(matrixStack);
      long window = Minecraft.getInstance().getWindow().getWindow();
      if (!InputConstants.isKeyDown(window, 341) || !(this.getSlotUnderMouse() instanceof CardDeckContainerMenu.DeckSlot)) {
         this.renderTooltip(matrixStack, pMouseX, pMouseY);
      }
   }

   private void renderMatchingNeighbors(PoseStack matrixStack) {
      if (((CardDeckContainerMenu)this.menu).getCarried().isEmpty() && this.getSlotUnderMouse() instanceof CardDeckContainerMenu.DeckSlot deckSlot) {
         List<CardDeckContainer.SlotColor> matchingNeighbors = ((CardDeckContainerMenu)this.menu).getContainer().getMatchingNeighbors(deckSlot.index);
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, 500.0);
         matchingNeighbors.forEach(matchingNeighbor -> {
            Slot slot = ((CardDeckContainerMenu)this.menu).getSlot(matchingNeighbor.slotIndex());
            if (slot != null) {
               renderSlotHighlight(matrixStack, slot.x + this.leftPos, slot.y + this.topPos, 0, matchingNeighbor.color() | 1996488704);
            }
         });
         matrixStack.popPose();
      }
   }

   public List<Component> getTooltipFromItem(ItemStack stack) {
      List<Component> tooltip = super.getTooltipFromItem(stack);
      if (this.getSlotUnderMouse() instanceof CardDeckContainerMenu.DeckSlot) {
         tooltip.add(new TextComponent("Hold <CTRL> to hide tooltip").withStyle(ChatFormatting.DARK_GRAY));
      }

      return tooltip;
   }

   @Override
   protected void renderTooltips(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
   }
}
