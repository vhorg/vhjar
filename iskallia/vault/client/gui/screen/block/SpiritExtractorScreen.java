package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.client.atlas.TextureAtlasRegion;
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
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.SpiritExtractorContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
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
      ButtonElement<?> recycleButton = new ButtonElement(
            Spatials.positionXY(8, 69), ScreenTextures.BUTTON_BUTTON_REROLL_TEXTURES, () -> ((SpiritExtractorContainer)this.getMenu()).recycle()
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      recycleButton.setDisabled(
         () -> !((SpiritExtractorContainer)this.getMenu()).isRecycleUnlocked()
            || !((SpiritExtractorContainer)this.getMenu()).hasSpirit()
            || ((SpiritExtractorContainer)this.getMenu()).isSpewingItems()
      );
      recycleButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         tooltipRenderer.renderComponentTooltip(poseStack, this.getRecycleButtonTooltipLines(), mouseX, mouseY, TooltipDirection.RIGHT);
         return true;
      });
      this.addElement(recycleButton);
      ButtonElement<?> recycleLockButton = new SpiritExtractorScreen.RecycleLockButton(
            Spatials.positionXY(28, 69), () -> ((SpiritExtractorContainer)this.getMenu()).toggleRecycleLock()
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      this.addElement(recycleLockButton);
      this.addElement(
         new SpiritExtractorScreen.DynamicLabel(
               Spatials.positionXY(40, 74),
               () -> new TranslatableComponent(
                     "screen.the_vault.spirit_extractor.total_revives", new Object[]{((SpiritExtractorContainer)this.getMenu()).getSpiritRecoveryCount()}
                  )
                  .withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle(),
               new TextComponent("")
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         new SpiritExtractorScreen.DynamicLabel(
               Spatials.positionXY(130, 90),
               () -> new TextComponent(String.format("%.2f", ((SpiritExtractorContainer)this.getMenu()).getMultiplier()))
                  .withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle(),
               new TextComponent("0.00")
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
            .tooltip(
               (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                  tooltipRenderer.renderComponentTooltip(
                     poseStack,
                     List.of(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.multiplier_explained")),
                     mouseX,
                     mouseY,
                     TooltipDirection.RIGHT
                  );
                  return true;
               }
            )
      );
      ButtonElement<?> purchaseButton = new ButtonElement(
            Spatials.positionXY(150, 69), ScreenTextures.BUTTON_PAY_TEXTURES, () -> ((SpiritExtractorContainer)this.getMenu()).startSpewingItems()
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

   private List<Component> getRecycleButtonTooltipLines() {
      if (!((SpiritExtractorContainer)this.getMenu()).hasSpirit()) {
         return Collections.emptyList();
      } else {
         List<Component> ret = new ArrayList<>();
         ret.add(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.recycle").withStyle(ChatFormatting.WHITE));
         ret.add(TextComponent.EMPTY);
         if (!((SpiritExtractorContainer)this.getMenu()).isRecyclable()) {
            ret.add(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.not_recyclable").withStyle(ChatFormatting.RED));
            ret.add(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.not_recyclable_will_delete").withStyle(ChatFormatting.RED));
         } else if (((SpiritExtractorContainer)this.getMenu()).isRecycleUnlocked()) {
            ret.add(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.recycle_unlocked").withStyle(ChatFormatting.GRAY));
            ret.add(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.recycle_warning").withStyle(ChatFormatting.RED));
         } else {
            ret.add(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.recycle_locked").withStyle(ChatFormatting.GRAY));
         }

         return ret;
      }
   }

   @NotNull
   private List<Component> getPurchaseButtonTooltipLines() {
      List<Component> purchaseButtonTooltips = new ArrayList<>();
      SpiritExtractorTileEntity.RecoveryCost recoveryCost = ((SpiritExtractorContainer)this.getMenu()).getRecoveryCost();
      ItemStack totalCost = recoveryCost.getTotalCost();
      if (totalCost.getCount() > 0) {
         if (((SpiritExtractorContainer)this.getMenu()).hasSpirit()) {
            purchaseButtonTooltips.add(new TextComponent("Cost for recovering items"));
            int paymentStackCount = ((SpiritExtractorContainer)this.getMenu()).getSlot(36).getItem().getCount();
            ChatFormatting textColor = paymentStackCount < totalCost.getCount() ? ChatFormatting.RED : ChatFormatting.YELLOW;
            String percentString = (int)((float)paymentStackCount / totalCost.getCount() * 100.0F) + "%";
            purchaseButtonTooltips.add(
               new TranslatableComponent(
                     "screen.the_vault.spirit_extractor.tooltip.total_cost",
                     new Object[]{percentString, totalCost.getItem().getName(totalCost).getString(), totalCost.getCount()}
                  )
                  .withStyle(textColor)
            );
         } else {
            purchaseButtonTooltips.add(
               new TranslatableComponent(
                     "screen.the_vault.spirit_extractor.tooltip.would_be_cost",
                     new Object[]{totalCost.getCount(), totalCost.getItem().getName(totalCost).getString()}
                  )
                  .withStyle(ChatFormatting.GREEN)
            );
         }

         purchaseButtonTooltips.add(TextComponent.EMPTY);
         float baseCostCount = recoveryCost.getBaseCount();
         int levels = Math.max(1, ((SpiritExtractorContainer)this.getMenu()).getPlayerLevel());
         purchaseButtonTooltips.add(
            new TranslatableComponent(
                  "screen.the_vault.spirit_extractor.tooltip.base_cost",
                  new Object[]{String.format("%.0f", baseCostCount * levels), String.format("%.2f", baseCostCount), levels}
               )
               .withStyle(ChatFormatting.GRAY)
         );
         recoveryCost.getStackCost()
            .forEach(
               t -> purchaseButtonTooltips.add(
                  new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.item_cost", new Object[]{t.getB(), ((ItemStack)t.getA()).getHoverName()})
                     .withStyle(ChatFormatting.GRAY)
               )
            );
         float multiplier = ((SpiritExtractorContainer)this.getMenu()).getMultiplier();
         if (!Mth.equal(multiplier, 1.0F)) {
            purchaseButtonTooltips.add(
               new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.multiplier", new Object[]{String.format("%.2f", multiplier)})
                  .withStyle(ChatFormatting.GRAY)
            );
         }

         float heroDiscount = ((SpiritExtractorContainer)this.getMenu()).getHeroDiscount();
         if (heroDiscount >= 0.01) {
            purchaseButtonTooltips.add(
               new TranslatableComponent(
                     "screen.the_vault.spirit_extractor.tooltip.hero_discount", new Object[]{String.format("%.0f%%", heroDiscount * 100.0F)}
                  )
                  .withStyle(ChatFormatting.GRAY)
            );
         }

         float rescuedBonus = ((SpiritExtractorContainer)this.getMenu()).getRescuedBonus();
         if (rescuedBonus > 0.0F) {
            purchaseButtonTooltips.add(
               new TranslatableComponent(
                     "screen.the_vault.spirit_extractor.tooltip.rescued_bonus", new Object[]{String.format("%.0f%%", rescuedBonus * 100.0F)}
                  )
                  .withStyle(ChatFormatting.GRAY)
            );
         }
      }

      return purchaseButtonTooltips;
   }

   private class DynamicLabel extends LabelElement<SpiritExtractorScreen.DynamicLabel> {
      private final Supplier<Component> textSupplier;

      public DynamicLabel(IPosition position, Supplier<Component> textSupplier, LabelTextStyle.Builder labelTextStyle, TextComponent defaultText) {
         super(position, defaultText, labelTextStyle);
         this.textSupplier = textSupplier;
      }

      @Override
      public Component getComponent() {
         return this.textSupplier.get();
      }
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

   private class RecycleLockButton extends ButtonElement<SpiritExtractorScreen.RecycleLockButton> {
      public RecycleLockButton(IPosition position, Runnable onClick) {
         super(position, ScreenTextures.BUTTON_TOGGLE_OFF_TEXTURES, onClick);
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         ButtonElement.ButtonTextures textures = ((SpiritExtractorContainer)SpiritExtractorScreen.this.getMenu()).isRecycleUnlocked()
            ? ScreenTextures.BUTTON_TOGGLE_ON_TEXTURES
            : ScreenTextures.BUTTON_TOGGLE_OFF_TEXTURES;
         TextureAtlasRegion texture = textures.selectTexture(this.isDisabled(), this.containsMouse(mouseX, mouseY), false);
         renderer.render(texture, poseStack, this.worldSpatial);
      }
   }
}
