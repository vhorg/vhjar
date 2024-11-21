package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.CardEssenceExtractorTileEntity;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.ProgressElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextWrap;
import iskallia.vault.config.CardEssenceExtractorConfig;
import iskallia.vault.container.inventory.CardEssenceExtractorContainer;
import iskallia.vault.core.card.Card;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.CardItem;
import iskallia.vault.network.message.CardEssenceExtractorUpgradeCardMessage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TooltipFlag.Default;
import org.jetbrains.annotations.NotNull;

public class CardEssenceExtractorScreen extends AbstractElementContainerScreen<CardEssenceExtractorContainer> {
   private final Inventory playerInventory;
   private final LabelTextStyle textStyle;

   public CardEssenceExtractorScreen(CardEssenceExtractorContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(176, 166));
      this.textStyle = LabelTextStyle.defaultStyle().right().wrap(TextWrap.overflow()).build();
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(this.getGuiSpatial(), ScreenTextures.CARD_ESSENCE_EXTRACTOR_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(Spatials.positionXY(16, 33), ScreenTextures.CARD_ESSENCE_EXTRACTOR_SLOT_HINT)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(Spatials.positionXY(106, 28), ScreenTextures.CARD_ESSENCE_EXTRACTOR_SLOT_HINT)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 5),
               ((CardEssenceExtractorContainer)this.getMenu())
                  .getTileEntity()
                  .getBlockState()
                  .getBlock()
                  .getName()
                  .copy()
                  .withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 74), inventory.getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (ProgressElement)new ProgressElement(
               Spatials.positionXY(38, 53),
               ScreenTextures.CARD_ESSENCE_EXTRACTOR_PROGRESS_INPUT,
               () -> ((CardEssenceExtractorContainer)this.getMenu()).getTileEntity().getExtractProgress()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      ButtonElement<?> upgradeBtn = this.addElement(
         new ButtonElement(Spatials.positionXY(105, 48), ScreenTextures.CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE, this::attemptUpgradeCard)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      upgradeBtn.setDisabled(() -> !this.canUpgrade());
      upgradeBtn.tooltip(this::upgradeCardTooltip);
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(106, 53),
               Spatials.size(54, 9),
               new TextComponent("Upgrade").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD),
               LabelTextStyle.defaultStyle().center()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
   }

   private boolean upgradeCardTooltip(ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, TooltipFlag tooltipFlag) {
      CardEssenceExtractorTileEntity tile = ((CardEssenceExtractorContainer)this.getMenu()).getTileEntity();
      if (tile != null && !tile.isRemoved()) {
         ItemStack upgradeable = tile.getCardUpgradeStack();
         if (!upgradeable.isEmpty() && upgradeable.getItem() instanceof CardItem) {
            Card card = CardItem.getCard(upgradeable);
            int tier = card.getTier();
            CardEssenceExtractorConfig.TierConfig cfg = ModConfigs.CARD_ESSENCE_EXTRACTOR.getConfig(tier).orElse(null);
            if (cfg == null) {
               return false;
            } else {
               List<Component> tooltip = new ArrayList<>();
               if (card.canUpgrade()) {
                  tooltip.add(
                     new TextComponent("Required Essence: ")
                        .withStyle(ChatFormatting.RED)
                        .append(new TextComponent(String.valueOf(cfg.getEssencePerUpgrade())).withStyle(ChatFormatting.WHITE))
                  );
                  tooltip.add(TextComponent.EMPTY);
                  ItemStack nextTierCard = new ItemStack(ModItems.CARD);
                  Card nextTier = CardItem.getCard(upgradeable);
                  nextTier.onUpgrade();
                  CardItem.setCard(nextTierCard, nextTier);
                  tooltip.addAll(nextTierCard.getTooltipLines(null, Default.NORMAL));
               } else {
                  tooltip.add(new TextComponent("Maximum Card tier reached").withStyle(ChatFormatting.RED));
               }

               return Tooltips.multi(() -> tooltip).onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void attemptUpgradeCard() {
      if (this.canUpgrade()) {
         ModNetwork.CHANNEL.sendToServer(CardEssenceExtractorUpgradeCardMessage.INSTANCE);
      }
   }

   private boolean canUpgrade() {
      if (!((CardEssenceExtractorContainer)this.getMenu()).stillValid(this.playerInventory.player)) {
         return false;
      } else {
         CardEssenceExtractorTileEntity tile = ((CardEssenceExtractorContainer)this.getMenu()).getTileEntity();
         if (tile != null && !tile.isRemoved()) {
            ItemStack upgradeable = tile.getCardUpgradeStack();
            if (!upgradeable.isEmpty() && upgradeable.getItem() instanceof CardItem) {
               Card card = CardItem.getCard(upgradeable);
               int tier = card.getTier();
               CardEssenceExtractorConfig.TierConfig cfg = ModConfigs.CARD_ESSENCE_EXTRACTOR.getConfig(tier).orElse(null);
               return cfg != null && tile.getEssence() >= cfg.getEssencePerUpgrade() && card.canUpgrade();
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
      Component essenceComponent = new TextComponent(String.valueOf(((CardEssenceExtractorContainer)this.getMenu()).getTileEntity().getEssence()))
         .withStyle(ChatFormatting.BLACK);
      int boxWidth = 36;
      int width = Minecraft.getInstance().font.width(essenceComponent);
      float scale = Math.min(1.0F, (float)boxWidth / width);
      poseStack.pushPose();
      poseStack.translate(this.getGuiSpatial().x(), this.getGuiSpatial().y(), this.getGuiSpatial().z());
      poseStack.translate(62 + boxWidth, 41.0, 0.0);
      poseStack.scale(scale, 1.0F, 1.0F);
      this.elementRenderer.begin();
      this.textStyle.textBorder().render(this.elementRenderer, poseStack, essenceComponent, this.textStyle.textWrap(), this.textStyle.textAlign(), 0, 0, 0, 0);
      this.elementRenderer.end();
      poseStack.popPose();
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
