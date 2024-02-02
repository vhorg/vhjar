package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.AscensionForgeSelectElement;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.GearModelPreviewElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.AscensionForgeContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ServerboundAscensionForgeBuyMessage;
import iskallia.vault.network.message.ServerboundSelectAscensionForgeItemMessage;
import iskallia.vault.util.function.Memo;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AscensionForgeScreen extends AbstractElementContainerScreen<AscensionForgeContainer> {
   protected ButtonElement<?> emberPayButton;

   public AscensionForgeScreen(AscensionForgeContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXY(0, -10).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui).add(Spatials.size(0, 10))))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(7, -4), new TextComponent("Ascension Forge").withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.zero(), ((AscensionForgeContainer)this.getMenu()).slots, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.addElement(
         (GearModelPreviewElement)new GearModelPreviewElement(
               Spatials.positionXY(7, 6), Spatials.size(51, 72), Memo.reactive(container::getPreviewItemStack, container::getSelectedModelId)
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (AscensionForgeSelectElement)new AscensionForgeSelectElement(
               Spatials.positionXY(62, 6).height(50), 5, ClientDiscoveredEntriesData.Models.getObserverModels(), (selectedModelId, previewStack) -> {
                  container.selectItem(selectedModelId, previewStack);
                  ModNetwork.CHANNEL.sendToServer(new ServerboundSelectAscensionForgeItemMessage(selectedModelId, previewStack));
               }
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.emberPayButton = this.addElement(
         new ButtonElement<ButtonElement<ButtonElement<?>>>(
               Spatials.positionXY(117, 60).width(18).height(18),
               ScreenTextures.BUTTON_EMBER_PAY_TEXTURES,
               () -> {
                  ModNetwork.CHANNEL.sendToServer(new ServerboundAscensionForgeBuyMessage());
                  ((AscensionForgeContainer)this.getMenu())
                     .getPlayer()
                     .level
                     .playSound(
                        ((AscensionForgeContainer)this.getMenu()).getPlayer(),
                        ((AscensionForgeContainer)this.getMenu()).getPlayer().getX(),
                        ((AscensionForgeContainer)this.getMenu()).getPlayer().getY(),
                        ((AscensionForgeContainer)this.getMenu()).getPlayer().getZ(),
                        SoundEvents.AMETHYST_BLOCK_HIT,
                        SoundSource.BLOCKS,
                        1.5F,
                        0.75F
                     );
               }
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
            .tooltip(
               (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                  AscensionForgeContainer menu = (AscensionForgeContainer)this.getMenu();
                  if (menu.getPreviewItemStack().isEmpty()) {
                     tooltipRenderer.renderTooltip(
                        poseStack,
                        List.of(new TextComponent("Buy").withStyle(Style.EMPTY.withColor(-7835928)), new TextComponent("Select a model or title")),
                        mouseX,
                        mouseY,
                        ItemStack.EMPTY,
                        TooltipDirection.RIGHT
                     );
                     return true;
                  } else {
                     tooltipRenderer.renderTooltip(
                        poseStack,
                        List.of(
                           new TextComponent("Buy").withStyle(Style.EMPTY.withColor(-7835928)),
                           new TextComponent("Ember Cost: " + menu.emberCost()).withStyle(menu.priceFulfilled() ? ChatFormatting.GREEN : ChatFormatting.RED)
                        ),
                        mouseX,
                        mouseY,
                        ItemStack.EMPTY,
                        TooltipDirection.RIGHT
                     );
                     return false;
                  }
               }
            )
      );
   }

   @Override
   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
      this.emberPayButton
         .setDisabled(
            ((AscensionForgeContainer)this.getMenu()).getPreviewItemStack().isEmpty()
               || !((AscensionForgeContainer)this.getMenu()).priceFulfilled()
               || !((AscensionForgeContainer)this.getMenu())
                  .getInternalInventory()
                  .getItem(((AscensionForgeContainer)this.getMenu()).getInternalInventory().outputSlotIndex())
                  .isEmpty()
         );
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
