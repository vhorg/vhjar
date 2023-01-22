package iskallia.vault.client.gui.screen.player;

import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.EntityModelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.player.element.CuriosElement;
import iskallia.vault.client.gui.screen.player.element.StatLabelListElement;
import iskallia.vault.client.gui.screen.player.element.StatListPlayerContainerElement;
import iskallia.vault.client.gui.screen.player.element.StatListVaultContainerElement;
import iskallia.vault.client.gui.screen.player.element.StatTabContainerElement;
import iskallia.vault.client.gui.screen.player.element.VaultLevelBarElement;
import iskallia.vault.container.StatisticsTabContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ServerboundOpenHistoricMessage;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class StatisticsElementContainerScreen extends AbstractSkillTabElementContainerScreen<StatisticsTabContainer> {
   public static final int TAB_INDEX = 0;
   private static final boolean DEBUG = false;

   public StatisticsElementContainerScreen(StatisticsTabContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getBuffered());
      this.setGuiSize(Spatials.size(350, 186));
      StatisticsElementContainerScreenData screenData = new StatisticsElementContainerScreenData(inventory.player);
      this.addElement(
         (NineSliceElement)new NineSliceElement(
               Spatials.positionXY(this.getTabContentSpatial()).height(this.getTabContentSpatial()), ScreenTextures.DEFAULT_WINDOW_BACKGROUND
            )
            .layout((screen, gui, parent, world) -> world.width(this.getTabContentSpatial()))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionY(-4).positionZ(-10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateX(gui).translateY(this.getTabContentSpatial().bottom()).size(gui))
      );
      this.addElement(
         (SlotsElement)new SlotsElement(Spatials.zero(), ((StatisticsTabContainer)this.getMenu()).slots, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      EntityModelElement<?> playerModelElement = this.addElement(
         new EntityModelElement(Spatials.positionXY(25, 4), Spatials.size(54, 90), () -> inventory.player)
            .layout((screen, gui, parent, world) -> world.translateX(gui).translateY(this.getTabContentSpatial().bottom()))
      );
      this.addElement(
         (VaultLevelBarElement)((VaultLevelBarElement)new VaultLevelBarElement(
                  Spatials.positionXY(4, -8), screenData::getVaultLevelPercentage, screenData::getVaultLevel
               )
               .layout((screen, gui, parent, world) -> world.translateX(playerModelElement.right()).translateY(playerModelElement.bottom())))
            .tooltip(screenData.getVaultLevelTooltip())
      );
      StatLabelListElement<?> prominentStatLabelListElement = this.addElement(
         new StatLabelListElement(Spatials.positionXY(4, 4).width(82), screenData.getStatListProminent())
            .layout((screen, gui, parent, world) -> world.translateX(playerModelElement.right()).translateY(playerModelElement.top()))
      );
      NineSliceElement<?> verticalSeparatorElement = this.addElement(
         new NineSliceElement(Spatials.positionXYZ(8, -2, -5).width(3).height(-4), ScreenTextures.INSET_VERTICAL_SEPARATOR)
            .layout(
               (screen, gui, parent, world) -> world.translateX(prominentStatLabelListElement.right())
                  .translateY(this.getTabContentSpatial().bottom())
                  .height(world.height() + gui.height())
            )
      );
      StatListPlayerContainerElement statListPlayerContainerElement = this.addElement(
         new StatListPlayerContainerElement(
               Spatials.positionX(4).width(-7).height(-16),
               screenData.getStatListPlayer(),
               screenData.getValueProviderIdona(),
               screenData.getValueProviderTenos(),
               screenData.getValueProviderVelara(),
               screenData.getValueProviderWendarr()
            )
            .layout(
               (screen, gui, parent, world) -> world.translateX(verticalSeparatorElement.right())
                  .translateY(playerModelElement.top())
                  .width(world.width() + gui.right() - world.x())
                  .height(world.height() + gui.height())
            )
      );
      StatListVaultContainerElement statListVaultContainerElement = this.addElement(
         new StatListVaultContainerElement(Spatials.positionX(4).width(-7).height(-16), screenData.getStatListVault(container.getData()))
            .layout(
               (screen, gui, parent, world) -> world.translateX(verticalSeparatorElement.right())
                  .translateY(playerModelElement.top())
                  .width(world.width() + gui.right() - world.x())
                  .height(world.height() + gui.height())
            )
      );
      this.addElement((StatTabContainerElement)new StatTabContainerElement(Spatials.positionXY(-3, 3), index -> {
         statListPlayerContainerElement.setEnabled(index == 0);
         statListPlayerContainerElement.setVisible(index == 0);
         statListVaultContainerElement.setEnabled(index == 1);
         statListVaultContainerElement.setVisible(index == 1);
      }).layout((screen, gui, parent, world) -> world.translateX(gui.right()).translateY(this.getTabContentSpatial().bottom())));
      this.addElement(
         (ButtonElement)((ButtonElement)new ButtonElement(Spatials.positionXY(-3, 3), ScreenTextures.BUTTON_HISTORY_TEXTURES, () -> {
               ModNetwork.CHANNEL.sendToServer(ServerboundOpenHistoricMessage.INSTANCE);
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }).layout(
               (screen, gui, parent, world) -> world.width(21).height(21).translateX(gui.right() + 4).translateY(this.getTabContentSpatial().bottom() + 68)
            ))
            .tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
               tooltipRenderer.renderTooltip(
                  poseStack, List.of(new TextComponent("Open Vault History")), mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT
               );
               return false;
            })
      );
      this.addElement(
         new CuriosElement(
            this::getTabContentSpatial,
            ((StatisticsTabContainer)this.getMenu()).getCurioContainerHandler().canScroll(),
            ((StatisticsTabContainer)this.getMenu()).getCurioContainerHandler().getVisibleSlotCount(),
            value -> ((StatisticsTabContainer)this.getMenu()).getCurioContainerHandler().scrollTo(value)
         )
      );
   }

   @Override
   public int getTabIndex() {
      return 0;
   }

   @Override
   public MutableComponent getTabTitle() {
      return new TextComponent("Statistics");
   }

   @Override
   public ISpatial getTabContentSpatial() {
      return Spatials.copy(super.getTabContentSpatial()).height(19);
   }

   @Override
   protected void layout(ISpatial parent) {
      super.layout(parent);
      ISpatial tabContentSpatial = this.getTabContentSpatial();
      ((StatisticsTabContainer)this.menu).setSlotPositionOffset(0, tabContentSpatial.bottom() - 3 - this.getGuiTop());
   }
}
