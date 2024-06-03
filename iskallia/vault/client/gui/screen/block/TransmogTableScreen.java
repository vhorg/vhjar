package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.DiscoveredModelSelectElement;
import iskallia.vault.client.gui.framework.element.GearModelPreviewElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.transmog.SelectDiscoveredModelMessage;
import iskallia.vault.network.message.transmog.TransmogButtonMessage;
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

public class TransmogTableScreen extends AbstractElementContainerScreen<TransmogTableContainer> {
   protected ButtonElement<?> transmogButton;

   public TransmogTableScreen(TransmogTableContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXY(0, -10).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui).add(Spatials.size(0, 10))))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(7, -4),
               new TextComponent("Transmogrification Table").withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(Spatials.positionXY(83, 67), ScreenTextures.ICON_PLUS_SIGN)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.zero(), ((TransmogTableContainer)this.getMenu()).slots, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.addElement(
         (GearModelPreviewElement)new GearModelPreviewElement(
               Spatials.positionXY(7, 6),
               Spatials.size(51, 72),
               Memo.reactive(
                  container::getPreviewItemStack,
                  container::getSelectedModelId,
                  () -> container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(0)).getItem()
               )
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (DiscoveredModelSelectElement)new DiscoveredModelSelectElement(
               Spatials.positionXY(62, 6).height(50),
               5,
               () -> container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(0)).getItem(),
               ClientDiscoveredEntriesData.Models.getObserverModels(),
               selectedModelId -> {
                  container.selectModelId(selectedModelId);
                  ModNetwork.CHANNEL.sendToServer(new SelectDiscoveredModelMessage(selectedModelId));
               }
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.transmogButton = this.addElement(
         new ButtonElement<ButtonElement<ButtonElement<?>>>(
               Spatials.positionXY(117, 60).width(18).height(18),
               ScreenTextures.BUTTON_TRANSMOG_TEXTURES,
               () -> {
                  ModNetwork.CHANNEL.sendToServer(new TransmogButtonMessage());
                  ((TransmogTableContainer)this.getMenu())
                     .getPlayer()
                     .level
                     .playSound(
                        ((TransmogTableContainer)this.getMenu()).getPlayer(),
                        ((TransmogTableContainer)this.getMenu()).getPlayer().getX(),
                        ((TransmogTableContainer)this.getMenu()).getPlayer().getY(),
                        ((TransmogTableContainer)this.getMenu()).getPlayer().getZ(),
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
                  TransmogTableContainer menu = (TransmogTableContainer)this.getMenu();
                  if (menu.getPreviewItemStack().isEmpty()) {
                     tooltipRenderer.renderTooltip(
                        poseStack,
                        List.of(new TextComponent("Transmogrify").withStyle(Style.EMPTY.withColor(-7835928)), new TextComponent("Select a model")),
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
                           new TextComponent("Transmogrify").withStyle(Style.EMPTY.withColor(-7835928)),
                           new TextComponent("Vault Bronze Cost: " + menu.copperCost())
                              .withStyle(menu.priceFulfilled() ? ChatFormatting.GREEN : ChatFormatting.RED)
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
      this.transmogButton
         .setDisabled(
            ((TransmogTableContainer)this.getMenu()).getPreviewItemStack().isEmpty()
               || !((TransmogTableContainer)this.getMenu()).priceFulfilled()
               || !((TransmogTableContainer)this.getMenu())
                  .getInternalInventory()
                  .getItem(((TransmogTableContainer)this.getMenu()).getInternalInventory().outputSlotIndex())
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
