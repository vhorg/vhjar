package iskallia.vault.client.gui.screen.summary;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.ScalableItemElement;
import iskallia.vault.client.gui.framework.element.TitleElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.screen.AbstractElementScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.player.element.PointLabelContainerElement;
import iskallia.vault.client.gui.screen.summary.element.CombatStatsContainerElement;
import iskallia.vault.client.gui.screen.summary.element.CoopStatsElement;
import iskallia.vault.client.gui.screen.summary.element.CrystalStatsContainerElement;
import iskallia.vault.client.gui.screen.summary.element.LootStatsContainerElement;
import iskallia.vault.client.gui.screen.summary.element.OverviewContainerElement;
import iskallia.vault.client.gui.screen.summary.element.VaultExitTabContainerElement;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ServerboundOpenHistoricMessage;
import iskallia.vault.network.message.VaultPlayerStatsMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class VaultEndScreen extends AbstractElementScreen {
   public static final int TITLE_WIDTH = 128;
   public static final int BUTTON_WIDTH = 52;
   public static final int BUTTON_HEIGHT = 19;
   protected ButtonElement<?> closeButton;
   private static final boolean DEBUG = false;
   public static final TextColor XP_COLOR = TextColor.parseColor("#FFE637");
   private final VaultSnapshot snapshot;
   private final boolean isHistory;
   private final boolean fromLink;
   private final UUID asPlayer;

   public VaultEndScreen(VaultSnapshot snapshot, Component title, UUID asPlayer) {
      this(snapshot, title, asPlayer, false);
   }

   public VaultEndScreen(VaultSnapshot snapshot, Component title, UUID asPlayer, boolean isHistory) {
      this(snapshot, title, asPlayer, isHistory, false);
   }

   public VaultEndScreen(VaultSnapshot snapshot, Component title, UUID asPlayer, boolean isHistory, boolean fromLink) {
      super(title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.isHistory = isHistory;
      this.asPlayer = asPlayer;
      this.snapshot = snapshot;
      this.fromLink = fromLink;
      this.setGuiSize(Spatials.size(350, 186));
      VaultExitContainerScreenData screenData = new VaultExitContainerScreenData(snapshot, asPlayer);
      Vault vault = screenData.snapshot.getEnd();
      int numOfPlayers = vault.get(Vault.STATS).getMap().size();
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionY(-4).positionZ(-12).positionY(6), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.width(128).height(32).translateX((gui.right() - gui.left()) / 2 + gui.left() - 64))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(
               Spatials.positionXY(0, 42).size(this.width, 19).height(this.getTabContentSpatial()), ScreenTextures.DEFAULT_WINDOW_BACKGROUND
            )
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 7).size(gui.width() + 16 + 26, this.getTabContentSpatial().height()))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionY(-4).positionZ(-10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.x() - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .size(gui.width() + 26, gui.height() - 10)
            )
      );
      this.addElement(
         (PointLabelContainerElement)new PointLabelContainerElement(
               Spatials.positionXY(-6, 18), screenData::getUnspentSkillPoints, screenData::getUnspentKnowledgePoints
            )
            .layout((screen, gui, parent, world) -> world.width(screen))
      );
      OverviewContainerElement overviewContainerElement = this.addElement(
         new OverviewContainerElement(Spatials.positionX(4).width(-7).height(-16), screenData)
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.left() + 2 - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .width(world.width() + gui.right() - world.x() + 7)
                  .height(world.height() + gui.height() - 22)
            )
      );
      LootStatsContainerElement lootStatsContainerElement = this.addElement(
         new LootStatsContainerElement(Spatials.positionX(4).width(-7).height(-16), screenData)
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.left() + 2 - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .width(world.width() + gui.right() - world.x() + 7)
                  .height(world.height() + gui.height() - 22)
            )
      );
      CrystalStatsContainerElement crystalStatsContainerElement = this.addElement(
         new CrystalStatsContainerElement(Spatials.positionX(4).width(-7).height(-16), screenData.getModifiers())
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.left() + 2 - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .width(world.width() + gui.right() - world.x() + 7)
                  .height(world.height() + gui.height() - 22)
            )
      );
      CombatStatsContainerElement combatStatsContainerElement = this.addElement(
         new CombatStatsContainerElement(Spatials.positionX(4).width(-7).height(-16), screenData.getStatsCollector())
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.left() + 2 - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .width(world.width() + gui.right() - world.x() + 7)
                  .height(world.height() + gui.height() - 22)
            )
      );
      CoopStatsElement coopStatsElement = this.addElement(
         new CoopStatsElement(Spatials.positionX(4).width(-7).height(-16), screenData)
            .layout(
               (screen, gui, parent, world) -> world.translateX(gui.left() + 2 - 26 + 7)
                  .translateY(this.getTabContentSpatial().bottom())
                  .width(world.width() + gui.right() - world.x() + 7)
                  .height(world.height() + gui.height() - 22)
            )
      );
      LabelElement<?> overviewLabel = this.addElement(
         new LabelElement(Spatials.zero(), new TextComponent("Overview").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 13).translateY(48).translateZ(2))
      );
      LabelElement<?> crystalLabel = this.addElement(
         new LabelElement(Spatials.zero(), new TextComponent("Crystal Modifiers").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 13).translateY(48).translateZ(2))
      );
      LabelElement<?> lootLabel = this.addElement(
         new LabelElement(Spatials.zero(), new TextComponent("Loot").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 13).translateY(48).translateZ(2))
      );
      LabelElement<?> mobsLabel = this.addElement(
         new LabelElement(Spatials.zero(), new TextComponent("Combat Stats").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 13).translateY(48).translateZ(2))
      );
      LabelElement<?> coopLabel = this.addElement(
         new LabelElement(Spatials.zero(), new TextComponent("Coop").withStyle(ChatFormatting.BLACK), LabelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.translateX(gui.left() - 8 - 26 + 13).translateY(48).translateZ(2))
      );
      boolean isCoop = numOfPlayers > 1;
      this.addElement((VaultExitTabContainerElement)new VaultExitTabContainerElement(Spatials.positionXY(-3, 3), index -> {
         overviewContainerElement.setEnabled(index == 0);
         overviewContainerElement.setVisible(index == 0);
         overviewLabel.setEnabled(index == 0);
         overviewLabel.setVisible(index == 0);
         crystalStatsContainerElement.setEnabled(index == 1);
         crystalStatsContainerElement.setVisible(index == 1);
         crystalLabel.setEnabled(index == 1);
         crystalLabel.setVisible(index == 1);
         lootStatsContainerElement.setEnabled(index == 2);
         lootStatsContainerElement.setVisible(index == 2);
         lootLabel.setEnabled(index == 2);
         lootLabel.setVisible(index == 2);
         combatStatsContainerElement.setEnabled(index == 3);
         combatStatsContainerElement.setVisible(index == 3);
         mobsLabel.setEnabled(index == 3);
         mobsLabel.setVisible(index == 3);
         coopStatsElement.setEnabled(index == 4);
         coopStatsElement.setVisible(index == 4);
         coopLabel.setEnabled(index == 4);
         coopLabel.setVisible(index == 4);
      }, isCoop).layout((screen, gui, parent, world) -> world.translateX(gui.right() + 7).translateY(this.getTabContentSpatial().bottom())));
      this.addElement(
         (TitleElement)new TitleElement(
               Spatials.positionY(16),
               new TranslatableComponent(screenData.getCompletionTranslationString()).withStyle(ChatFormatting.BLACK),
               LabelTextStyle.left()
            )
            .layout(
               (screen, gui, parent, world) -> world.translateX(
                  (gui.right() - gui.left()) / 2
                     + gui.left()
                     - TextBorder.DEFAULT_FONT.get().width(new TranslatableComponent(screenData.getCompletionTranslationString()))
               )
            )
      );
      Component buttonText = new TextComponent("Claim").withStyle(ChatFormatting.WHITE);
      if (this.isHistory) {
         buttonText = new TextComponent("Back").withStyle(ChatFormatting.WHITE);
      }

      if (this.fromLink) {
         buttonText = new TextComponent("Close").withStyle(ChatFormatting.WHITE);
      }

      Component finalComponent = buttonText;
      this.addElement(
         (LabelElement)new LabelElement(Spatials.zero(), finalComponent, LabelTextStyle.border4(ChatFormatting.BLACK).center())
            .layout(
               (screen, gui, parent, world) -> world.translateZ(2)
                  .translateX(gui.right() - gui.left() + gui.left() - 26 - 1 - TextBorder.DEFAULT_FONT.get().width(finalComponent) / 2)
                  .translateY(this.getTabContentSpatial().bottom() + gui.height() - 31)
            )
      );
      this.addElement(
         (LabelElement)new LabelElement(Spatials.zero(), new TextComponent("Rewards: ").withStyle(ChatFormatting.BLACK), LabelTextStyle.right())
            .layout(
               (screen, gui, parent, world) -> world.translateZ(2)
                  .translateX(gui.left() - 8)
                  .translateY(this.getTabContentSpatial().bottom() + gui.height() - 31)
            )
      );
      List<ItemStack> list = screenData.getStatsCollector().getReward();
      int increment = 0;

      for (ItemStack stack : list) {
         int finalIncrement = increment;
         this.addElement(
            (ScalableItemElement)((ScalableItemElement)new ScalableItemElement(Spatials.zero(), () -> stack, 1.0F)
                  .layout(
                     (screen, gui, parent, world) -> world.translateZ(2)
                        .translateX(gui.left() - 8 + TextBorder.DEFAULT_FONT.get().width(new TextComponent("Rewards: ")) + 18 * finalIncrement)
                        .translateY(this.getTabContentSpatial().bottom() + gui.height() - 36)
                        .size(16, 16)
                  ))
               .tooltip(Tooltips.multi(() -> this.getTooltipFromItem(stack)))
         );
         increment++;
      }

      Component xpGainedComponent = new TextComponent("+ " + screenData.getStatsCollector().getExperience(vault) + " xp")
         .withStyle(Style.EMPTY.withColor(XP_COLOR));
      int finalIncrement1 = increment;
      this.addElement(
         (LabelElement)((LabelElement)new LabelElement(Spatials.zero(), xpGainedComponent, LabelTextStyle.shadow(ChatFormatting.BLACK))
               .layout(
                  (screen, gui, parent, world) -> world.translateZ(2)
                     .translateX(gui.left() - 8 + TextBorder.DEFAULT_FONT.get().width(new TextComponent("Rewards: ")) + 5 + 18 * finalIncrement1)
                     .translateY(this.getTabContentSpatial().bottom() + gui.height() - 31)
                     .width(TextBorder.DEFAULT_FONT.get().width(xpGainedComponent))
                     .height(9)
               ))
            .tooltip(
               Tooltips.multi(
                  () -> {
                     StatCollector statCollector = screenData.getStatsCollector();
                     long window = Minecraft.getInstance().getWindow().getWindow();
                     boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
                     float xpMultiplier = statCollector.getExpMultiplier();
                     List<Component> xpReceipt = VaultExitContainerScreenData.getXpReceipt(vault, screenData.getStatsCollector(), shiftDown, xpMultiplier);
                     int maxWidth = 0;
                     int spaceWidth = TextBorder.DEFAULT_FONT.get().width(" ");

                     for (Component component : xpReceipt) {
                        maxWidth = Math.max(TextBorder.DEFAULT_FONT.get().width(component), maxWidth);
                     }

                     MutableComponent titleText = new TextComponent("Vault Xp").withStyle(Style.EMPTY.withColor(XP_COLOR));
                     MutableComponent spacer = new TextComponent("");
                     String totalXpString = shiftDown
                        ? String.format("(x%.1f) %s xp", xpMultiplier, ModConfigs.VAULT_STATS.getExperienceWithoutMultiplier(vault, statCollector))
                        : String.format("%s xp", statCollector.getExperience(vault));

                     for (int i = 0; i < maxWidth / spaceWidth - 10 - TextBorder.DEFAULT_FONT.get().width(totalXpString) / spaceWidth; i++) {
                        spacer.append(" ");
                     }

                     xpReceipt.add(0, titleText.append(spacer).append(totalXpString));
                     return xpReceipt;
                  }
               )
            )
      );
      this.closeButton = this.addElement(
         new ButtonElement<ButtonElement<ButtonElement<?>>>(Spatials.zero(), ScreenTextures.BUTTON_CLOSE_TEXTURES, () -> {
               if (!this.isHistory) {
                  this.onClose();
                  ModNetwork.CHANNEL.sendToServer(new VaultPlayerStatsMessage.C2S(this.snapshot.getEnd().get(Vault.ID)));
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.VAULT_CHEST_RARE_OPEN, 1.25F));
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               } else if (!this.fromLink) {
                  ModNetwork.CHANNEL.sendToServer(ServerboundOpenHistoricMessage.INSTANCE);
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               } else {
                  this.onClose();
               }
            })
            .layout(
               (screen, gui, parent, world) -> world.width(52)
                  .height(19)
                  .translateX(gui.right() - gui.left() + gui.left() - 52)
                  .translateY(this.getTabContentSpatial().bottom() + gui.height() - 37)
            )
            .tooltip(
               (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                  List<Component> xpComponentList = new ArrayList<>(
                     List.of(
                        new TextComponent("Claim Rewards").withStyle(Style.EMPTY.withColor(-6710887)).withStyle(ChatFormatting.GREEN),
                        new TextComponent("  " + screenData.getStatsCollector().getExperience(vault) + " xp").withStyle(Style.EMPTY.withColor(XP_COLOR))
                     )
                  );

                  for (ItemStack stack : list) {
                     xpComponentList.add(
                        new TextComponent("  " + stack.getCount() + "x ")
                           .withStyle(ChatFormatting.WHITE)
                           .append(new TranslatableComponent(stack.getDescriptionId()).withStyle(ChatFormatting.WHITE))
                     );
                  }

                  if (!this.isHistory) {
                     tooltipRenderer.renderTooltip(poseStack, xpComponentList, mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
                  } else if (this.fromLink) {
                     tooltipRenderer.renderTooltip(poseStack, List.of(new TextComponent("Close")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
                  } else {
                     tooltipRenderer.renderTooltip(
                        poseStack, List.of(new TextComponent("Back to History")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT
                     );
                  }

                  return false;
               }
            )
      );
   }

   @Override
   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
   }

   public void onClose() {
      if (!this.isHistory) {
         ModNetwork.CHANNEL.sendToServer(new VaultPlayerStatsMessage.C2S(this.snapshot.getEnd().get(Vault.ID)));
      }

      super.onClose();
   }

   public ISpatial getTabContentSpatial() {
      int padLeft = 21;
      int padTop = 42;
      int width = this.width - padLeft * 2;
      int height = 19;
      return Spatials.positionXY(padLeft, padTop).size(width, height);
   }

   @Override
   protected void layout(ISpatial parent) {
      super.layout(parent);
   }
}
