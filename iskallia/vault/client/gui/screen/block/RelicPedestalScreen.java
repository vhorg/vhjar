package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.RelicPedestalPreviewElement;
import iskallia.vault.client.gui.framework.element.RelicSelectElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.RelicPedestalContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModRelics;
import iskallia.vault.network.message.relic.RelicAssembleButtonMessage;
import iskallia.vault.network.message.relic.SelectRelicMessage;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RelicPedestalScreen extends AbstractElementContainerScreen<RelicPedestalContainer> {
   protected ButtonElement<?> assembleButton;

   public RelicPedestalScreen(RelicPedestalContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXY(0, -10).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui).add(Spatials.size(0, 10))))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(7, -4), new TextComponent("Relic Assembly").withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.zero(), ((RelicPedestalContainer)this.getMenu()).slots, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.addElement((RelicSelectElement)new RelicSelectElement(Spatials.positionXY(7, 5).height(50), 5, Collections::emptySet, selectRelicId -> {
         container.selectRelic(selectRelicId);
         ModNetwork.CHANNEL.sendToServer(new SelectRelicMessage(selectRelicId));
      }).layout((screen, gui, parent, world) -> world.translateXY(gui)));
      this.addElement(
         (RelicPedestalPreviewElement)new RelicPedestalPreviewElement(Spatials.zero(), Spatials.size(51, 73), container::getSelectedRelicId)
            .layout((screen, gui, parent, world) -> world.positionXY(gui).translateXY(118, 5))
      );
      this.assembleButton = this.addElement(
         new ButtonElement<ButtonElement<ButtonElement<?>>>(
               Spatials.positionXY(99, 60).width(18).height(18),
               ScreenTextures.BUTTON_RELIC_TEXTURES,
               () -> ModNetwork.CHANNEL.sendToServer(new RelicAssembleButtonMessage())
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
            .tooltip(
               (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                  if (container.getSelectedRelicId() == ModRelics.EMPTY.getResultingRelic()) {
                     tooltipRenderer.renderTooltip(
                        poseStack,
                        List.of(
                           new TextComponent("Assemble Relic").withStyle(Style.EMPTY.withColor(-2505149)),
                           new TextComponent("Select a relic").withStyle(ChatFormatting.RED)
                        ),
                        mouseX,
                        mouseY,
                        ItemStack.EMPTY,
                        TooltipDirection.RIGHT
                     );
                     return true;
                  } else if (!container.recipeFulfilled()) {
                     tooltipRenderer.renderTooltip(
                        poseStack,
                        List.of(
                           new TextComponent("Assemble Relic").withStyle(Style.EMPTY.withColor(-2505149)),
                           new TextComponent("Place all fragments to assemble").withStyle(ChatFormatting.RED)
                        ),
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
                           new TextComponent("Assemble Relic").withStyle(Style.EMPTY.withColor(-2505149)),
                           new TextComponent("Click to assemble").withStyle(ChatFormatting.GREEN)
                        ),
                        mouseX,
                        mouseY,
                        ItemStack.EMPTY,
                        TooltipDirection.RIGHT
                     );
                     return true;
                  }
               }
            )
      );
   }

   @Override
   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
      this.assembleButton.setDisabled(!((RelicPedestalContainer)this.getMenu()).recipeFulfilled());
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
