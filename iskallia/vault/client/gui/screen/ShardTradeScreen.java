package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.FakeItemSlotElement;
import iskallia.vault.client.gui.framework.element.ItemStackDisplayElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.event.InputEvents;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.network.message.ServerboundResetBlackMarketTradesMessage;
import iskallia.vault.network.message.ShardTradeTradeMessage;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.expertise.type.BlackMarketExpertise;
import iskallia.vault.util.ScreenParticle;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShardTradeScreen extends AbstractElementContainerScreen<ShardTradeContainer> {
   protected ScreenParticle screenParticleLeft = new ScreenParticle()
      .angleRange(150.0F, 210.0F)
      .quantityRange(1, 2)
      .delayRange(0, 10)
      .lifespanRange(10, 50)
      .sizeRange(1, 4)
      .speedRange(0.05F, 0.45F)
      .spawnedPosition(this.leftPos + 76, this.topPos + 43)
      .spawnedWidthHeight(0, 28);
   protected ScreenParticle screenParticleRight = new ScreenParticle()
      .angleRange(-30.0F, 30.0F)
      .quantityRange(1, 2)
      .delayRange(0, 10)
      .lifespanRange(10, 50)
      .sizeRange(1, 4)
      .speedRange(0.05F, 0.45F)
      .spawnedPosition(this.leftPos + 77 + 90, this.topPos + 43)
      .spawnedWidthHeight(0, 28);
   private final LabelElement<?> labelRandomTrade;
   private final LabelElement<?>[] labelShopTrades = new LabelElement[3];
   private ButtonElement<?> omegaButton;
   private float dt;

   public ShardTradeScreen(ShardTradeContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(176, 194));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7), new TextComponent("Black Market").withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 100), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(Spatials.positionXY(13, 33), ScreenTextures.SOUL_SHARD_TRADE_ORNAMENT)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      ((FakeItemSlotElement)this.addElement(
            (FakeItemSlotElement)new FakeItemSlotElement(
                  Spatials.positionXY(29, 49), () -> new ItemStack(ModItems.UNKNOWN_ITEM), () -> !this.canBuyRandomTrade()
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         ))
         .whenClicked(this::buyRandomTrade)
         .tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            tooltipRenderer.renderTooltip(poseStack, new ItemStack(ModItems.UNKNOWN_ITEM), mouseX, mouseY, TooltipDirection.RIGHT);
            return true;
         });
      this.addElement(
         (ItemStackDisplayElement)new ItemStackDisplayElement(Spatials.positionXY(29, 69), new ItemStack(ModItems.SOUL_SHARD))
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.labelRandomTrade = this.addElement(
         new LabelElement(Spatials.positionXYZ(37, 79, 200), TextComponent.EMPTY, LabelTextStyle.border8().center())
            .layout((screen, gui, parent, world) -> world.translateXYZ(gui))
      );
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(Spatials.positionXY(74, 6), ScreenTextures.BLACK_MARKET_ORNAMENT)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(Spatials.positionXY(72, 37), ScreenTextures.OMEGA_BLACK_MARKET_ORNAMENT)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );

      for (int i = 0; i < 2; i++) {
         if (i == 1) {
            i = 2;
         }

         int tradeIndex = i;
         int yOffsetTrade = 10 + i * 33;
         this.addElement(
               ((ButtonElement)new ButtonElement(Spatials.positionXY(78, yOffsetTrade), ScreenTextures.BUTTON_TRADE_WIDE_TEXTURES, () -> {})
                     .layout((screen, gui, parent, world) -> world.translateXY(gui)))
                  .setDisabled(() -> !this.canBuyTrade(tradeIndex))
            )
            .setEnabled(false);
         int yOffset = 14 + i * 33;
         ((FakeItemSlotElement)this.addElement((FakeItemSlotElement)new FakeItemSlotElement(Spatials.positionXY(141, yOffset), () -> {
               Tuple<ItemStack, Integer> trade = ClientShardTradeData.getTradeInfo(tradeIndex);
               return trade == null ? ItemStack.EMPTY : ((ItemStack)trade.getA()).copy();
            }, () -> !this.canBuyTrade(tradeIndex)).setLabelStackCount().layout((screen, gui, parent, world) -> world.translateXY(gui))))
            .whenClicked(() -> this.buyTrade(tradeIndex))
            .tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
               Tuple<ItemStack, Integer> trade = ClientShardTradeData.getTradeInfo(tradeIndex);
               if (trade != null && !((ItemStack)trade.getA()).isEmpty()) {
                  tooltipRenderer.renderTooltip(poseStack, (ItemStack)trade.getA(), mouseX, mouseY, TooltipDirection.RIGHT);
               }

               return true;
            });
         this.addElement(
            (ItemStackDisplayElement)new ItemStackDisplayElement(Spatials.positionXY(89, yOffset), new ItemStack(ModItems.SOUL_SHARD))
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
         this.labelShopTrades[i] = this.addElement(
            new LabelElement(Spatials.positionXYZ(97, yOffset + 10, 200), TextComponent.EMPTY, LabelTextStyle.border8().center())
               .layout((screen, gui, parent, world) -> world.translateXYZ(gui))
         );
      }

      int tradeIndex = 1;
      int yOffsetTrade = 43;
      this.addElement(
            this.omegaButton = ((ButtonElement)new ButtonElement(
                     Spatials.positionXY(77, yOffsetTrade - 1), ScreenTextures.OMEGA_BUTTON_TRADE_WIDE_TEXTURES, () -> {}
                  )
                  .layout((screen, gui, parent, world) -> world.translateXY(gui)))
               .setDisabled(() -> !this.canBuyTrade(tradeIndex))
         )
         .setEnabled(false);
      int yOffset = 47;
      ((FakeItemSlotElement)this.addElement((FakeItemSlotElement)new FakeItemSlotElement(Spatials.positionXY(141, yOffset), () -> {
         Tuple<ItemStack, Integer> trade = ClientShardTradeData.getTradeInfo(tradeIndex);
         return trade == null ? ItemStack.EMPTY : ((ItemStack)trade.getA()).copy();
      }, () -> !this.canBuyTrade(tradeIndex)).setLabelStackCount().layout((screen, gui, parent, world) -> world.translateXY(gui)))).whenClicked(() -> {
         this.buyTrade(tradeIndex);
         this.screenParticleLeft.pop(4.0F, 20.0F);
         this.screenParticleRight.pop(4.0F, 20.0F);
      }).tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         Tuple<ItemStack, Integer> trade = ClientShardTradeData.getTradeInfo(tradeIndex);
         if (trade != null && !((ItemStack)trade.getA()).isEmpty()) {
            tooltipRenderer.renderTooltip(poseStack, (ItemStack)trade.getA(), mouseX, mouseY, TooltipDirection.RIGHT);
         }

         return true;
      });
      this.addElement(
         (ItemStackDisplayElement)new ItemStackDisplayElement(Spatials.positionXY(89, yOffset), new ItemStack(ModItems.SOUL_SHARD))
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.labelShopTrades[1] = this.addElement(
         new LabelElement(Spatials.positionXYZ(97, yOffset + 10, 200), TextComponent.EMPTY, LabelTextStyle.border8().center())
            .layout((screen, gui, parent, world) -> world.translateXYZ(gui))
      );
      LocalDateTime endTime = ClientShardTradeData.getNextReset();
      LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("UTC")).withNano(0);
      LocalTime diff = LocalTime.MIN.plusSeconds(ChronoUnit.SECONDS.between(nowTime, endTime));
      Component component = new TextComponent(diff.format(DateTimeFormatter.ISO_LOCAL_TIME));
      this.addElement(
         new ShardTradeScreen.CountDownElement(
               Spatials.positionXYZ(this.getGuiSpatial().width() / 2 - TextBorder.DEFAULT_FONT.get().width(component) / 2 - 11, -10, 200),
               Spatials.size(TextBorder.DEFAULT_FONT.get().width(component), 9),
               (Supplier<Component>)(() -> component),
               LabelTextStyle.shadow()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui.x(), gui.y()))
      );
      this.addElement(
         (TextureAtlasElement)((TextureAtlasElement)new TextureAtlasElement(
                  Spatials.positionXY(
                     this.getGuiSpatial().width() / 2 - ScreenTextures.TAB_COUNTDOWN_BACKGROUND.width() / 2 - 10,
                     -ScreenTextures.TAB_COUNTDOWN_BACKGROUND.height()
                  ),
                  ScreenTextures.TAB_COUNTDOWN_BACKGROUND
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui)))
            .tooltip(Tooltips.multi(() -> List.of(new TextComponent("Shop resets in"))))
      );
      this.addElement(
         (ButtonElement)((ButtonElement)new ButtonElement(
                  Spatials.positionXY(
                     this.getGuiSpatial().width() / 2 - ScreenTextures.TAB_COUNTDOWN_BACKGROUND.width() / 2 + 50,
                     -ScreenTextures.TAB_COUNTDOWN_BACKGROUND.height()
                  ),
                  ScreenTextures.BUTTON_RESET_TRADES_TEXTURES,
                  () -> {
                     ModNetwork.CHANNEL.sendToServer(ServerboundResetBlackMarketTradesMessage.INSTANCE);
                     ((ShardTradeContainer)this.getMenu())
                        .getPlayer()
                        .level
                        .playSound(
                           ((ShardTradeContainer)this.getMenu()).getPlayer(),
                           ((ShardTradeContainer)this.getMenu()).getPlayer().getX(),
                           ((ShardTradeContainer)this.getMenu()).getPlayer().getY(),
                           ((ShardTradeContainer)this.getMenu()).getPlayer().getZ(),
                           ModSounds.SKILL_TREE_LEARN_SFX,
                           SoundSource.BLOCKS,
                           0.75F,
                           1.0F
                        );
                  }
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui)))
            .setDisabled(() -> {
               for (TieredSkill learnedTalentNode : ClientExpertiseData.getLearnedTalentNodes()) {
                  if (learnedTalentNode.getChild() instanceof BlackMarketExpertise blackMarketExpertise) {
                     return blackMarketExpertise.getNumberOfRolls() <= ClientShardTradeData.getRerollsUsed();
                  }
               }

               return true;
            })
            .tooltip(
               Tooltips.multi(
                  () -> {
                     int numOfRollsLeft = 0;
                     boolean hasExpertise = false;

                     for (TieredSkill learnedTalentNode : ClientExpertiseData.getLearnedTalentNodes()) {
                        if (learnedTalentNode.getChild() instanceof BlackMarketExpertise blackMarketExpertise) {
                           numOfRollsLeft = blackMarketExpertise.getNumberOfRolls() - ClientShardTradeData.getRerollsUsed();
                           hasExpertise = true;
                        }
                     }

                     return hasExpertise
                        ? List.of(new TextComponent("Rolls Left: " + numOfRollsLeft))
                        : List.of(new TextComponent("Unlock Marketer Expertise to Re-roll"));
                  }
               )
            )
      );
      this.updateTradeLabels();
   }

   @Override
   public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.dt += partialTick;
      this.screenParticleLeft.spawnedPosition(this.leftPos + 77, this.topPos + 43).spawnedWidthHeight(0, 29);
      this.screenParticleRight.spawnedPosition(this.leftPos + 77 + 90, this.topPos + 43).spawnedWidthHeight(0, 29);

      for (; this.dt >= 0.5F; this.dt -= 0.5F) {
         this.screenParticleLeft.tick();
         this.screenParticleRight.tick();
         if (ClientShardTradeData.getAvailableTrades().containsKey(1)) {
            this.screenParticleLeft.pop();
            this.screenParticleRight.pop();
         }
      }

      if (this.needsLayout) {
         this.layout(Spatials.zero());
         this.needsLayout = false;
      }

      this.renderBackgroundFill(poseStack);
      this.renderElements(poseStack, mouseX, mouseY, partialTick);
      this.renderSlotItems(poseStack, mouseX, mouseY, partialTick);
      this.renderDebug(poseStack);
      this.screenParticleLeft.render(poseStack, partialTick);
      this.screenParticleRight.render(poseStack, partialTick);
      this.renderTooltips(poseStack, mouseX, mouseY);
   }

   protected void containerTick() {
      super.containerTick();
      this.updateTradeLabels();
   }

   private void updateTradeLabels() {
      int playerShards = ItemShardPouch.getShardCount(Minecraft.getInstance().player);
      int randomCost = ClientShardTradeData.getRandomTradeCost();
      LocalDateTime nextReset = ClientShardTradeData.getNextReset();
      LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC")).withNano(0);
      LocalTime diff = LocalTime.MIN.plusSeconds(ChronoUnit.SECONDS.between(now, nextReset));
      new TextComponent(diff.format(DateTimeFormatter.ISO_LOCAL_TIME));
      int randomCostColor = playerShards >= randomCost ? 16777215 : 8257536;
      Component randomCostComponent = new TextComponent(String.valueOf(randomCost)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(randomCostColor)));
      this.labelRandomTrade.set(randomCostComponent);

      for (int i = 0; i < 3; i++) {
         Tuple<ItemStack, Integer> tradeInfo = ClientShardTradeData.getTradeInfo(i);
         if (tradeInfo == null) {
            this.labelShopTrades[i].set(TextComponent.EMPTY);
         } else {
            int tradeCost = (Integer)tradeInfo.getB();
            int tradeCostColor = playerShards >= tradeCost ? 16777215 : 8257536;
            Component tradeCostComponent = new TextComponent(String.valueOf(tradeCost)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(tradeCostColor)));
            this.labelShopTrades[i].set(tradeCostComponent);
         }
      }
   }

   private boolean canBuyRandomTrade() {
      return ItemShardPouch.getShardCount(Minecraft.getInstance().player) >= ClientShardTradeData.getRandomTradeCost();
   }

   private boolean canBuyTrade(int tradeIndex) {
      Tuple<ItemStack, Integer> tradeInfo = ClientShardTradeData.getTradeInfo(tradeIndex);
      return tradeInfo == null ? false : ItemShardPouch.getShardCount(Minecraft.getInstance().player) >= (Integer)tradeInfo.getB();
   }

   private void buyRandomTrade() {
      if (this.canBuyRandomTrade()) {
         ModNetwork.CHANNEL
            .sendToServer(
               new ShardTradeTradeMessage(
                  -1, InputEvents.isShiftDown(), Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getUUID() : null
               )
            );
      }
   }

   private void buyTrade(int tradeIndex) {
      if (this.canBuyTrade(tradeIndex)) {
         ModNetwork.CHANNEL
            .sendToServer(
               new ShardTradeTradeMessage(
                  tradeIndex, InputEvents.isShiftDown(), Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getUUID() : null
               )
            );
      }
   }

   private static final class CountDownElement extends DynamicLabelElement<Component, ShardTradeScreen.CountDownElement> {
      private CountDownElement(IPosition position, ISize size, Supplier<Component> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         LocalDateTime endTime = ClientShardTradeData.getNextReset();
         LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("UTC")).withNano(0);
         LocalTime diff = LocalTime.MIN.plusSeconds(ChronoUnit.SECONDS.between(nowTime, endTime));
         Component component = new TextComponent(diff.format(DateTimeFormatter.ISO_LOCAL_TIME));
         this.onValueChanged(component);
      }
   }
}
