package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.config.PaxelConfigs;
import iskallia.vault.container.ToolViseContainerMenu;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.paxel.PaxelItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ToolViseScreen extends AbstractContainerScreen<ToolViseContainerMenu> implements ContainerListener {
   public static final ResourceLocation TEXTURE = new ResourceLocation("the_vault", "textures/gui/tool_vise.png");
   private float sturdinessPercentage = 1.0F;
   private float level = 0.0F;
   private float maxLevel = 0.0F;
   private int levelPerSocket = 0;
   private final int[] ingredientCounts = new int[6];
   private int upgradeVisual = 0;
   private PaxelItem.Stat upgradedStat = null;
   private boolean upgradedPerk = false;
   private final List<ToolViseScreen.UpgradeButton> upgradeButtons = new ArrayList<>();

   public ToolViseScreen(ToolViseContainerMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
      this.imageWidth = 176;
      this.imageHeight = 218;
      this.inventoryLabelY += 54;
      ((ToolViseContainerMenu)this.getMenu()).addSlotListener(this);
   }

   public void init() {
      super.init();
      this.upgradeButtons.clear();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      int u = 0;

      for (PaxelItem.Stat stat : PaxelItem.Stat.values()) {
         PaxelConfigs.Upgrade upgrade = ((ToolViseContainerMenu)this.menu).upgrades.get(stat);
         this.upgradeButtons
            .add((ToolViseScreen.UpgradeButton)this.addRenderableWidget(new ToolViseScreen.UpgradeButton(i + 10, j + 19 + 22 * u, u, upgrade, stat)));
         u++;
      }

      this.slotChanged(this.menu, 36, ((ToolViseContainerMenu)this.menu).getSlot(36).getItem());
   }

   protected void containerTick() {
      super.containerTick();
      if (this.upgradeVisual > 0) {
         this.upgradeVisual--;
      }
   }

   protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int xOffset = (this.width - this.imageWidth) / 2;
      int yOffset = (this.height - this.imageHeight) / 2;
      this.blit(poseStack, xOffset, yOffset, 0, 0, this.imageWidth, this.imageHeight);
      int barSize = 34;
      float normalizedLevel = this.level / this.maxLevel;
      int barLevel = Math.round(normalizedLevel * barSize);
      ItemStack paxel = this.getPaxel();
      List<PaxelItem.Perk> perks = PaxelItem.getPerks(paxel);
      int x0 = xOffset + 46;
      int y0 = yOffset + 48;
      if (this.sturdinessPercentage != 0.0F) {
         GuiComponent.fill(poseStack, x0, y0, x0 + barLevel, y0 + 3, 0xFF000000 | ChatFormatting.YELLOW.getColor());
         int cl = PaxelItem.getSturdinessColor((int)(this.sturdinessPercentage * 100.0F)).getColor();
         TextComponent percentage = new TextComponent((int)(this.sturdinessPercentage * 100.0F) + "%");
         int centerX = x0 + barSize / 2;
         this.font.drawShadow(poseStack, percentage, centerX - this.font.width(percentage) / 2.0F, y0 + 5, cl);
         if (this.level < this.maxLevel) {
            float nextSlot = (this.level + (this.levelPerSocket - this.level % this.levelPerSocket)) / this.maxLevel;
            RenderSystem.setShaderTexture(0, TEXTURE);
            GuiComponent.blit(poseStack, centerX - barSize / 2 - 3 + (int)(nextSlot * barSize), y0 - 2, 6, 6, 230.0F, 0.0F, 7, 7, 256, 256);
         }
      }

      if (!paxel.isEmpty()) {
         xOffset += 100;
         yOffset += 22;
         this.font.drawShadow(poseStack, new TranslatableComponent("tooltip.the_vault.magnet_upgrades"), xOffset, yOffset, -1);
         yOffset += 14;
         int uStat = 0;
         int vStat = 218;
         int widthHeight = 14;

         for (PaxelItem.Stat stat : PaxelItem.Stat.values()) {
            float value = PaxelItem.getStatUpgrade(paxel, stat);
            if (value != 0.0F) {
               PaxelConfigs.Upgrade upgradeCfg = ModConfigs.PAXEL_CONFIGS.getUpgrade(stat);
               String valueStr = upgradeCfg.formatValue(value);
               RenderSystem.setShaderTexture(0, TEXTURE);
               GuiComponent.blit(poseStack, xOffset, yOffset, widthHeight, widthHeight, uStat, vStat, widthHeight, widthHeight, 256, 256);
               int color = -1;
               if (this.upgradeVisual != 0 && this.upgradedStat.equals(stat)) {
                  color = ColorUtil.blendColors(ChatFormatting.GREEN.getColor(), -1, this.upgradeVisual / 30.0F);
               }

               this.font.drawShadow(poseStack, new TextComponent((value > 0.0F ? " +" : " ") + valueStr), xOffset + 12, yOffset + 3, color);
               yOffset += 16;
            }

            uStat += widthHeight;
         }

         for (PaxelItem.Perk perk : perks) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            GuiComponent.blit(poseStack, xOffset, yOffset, widthHeight, widthHeight, 14 * perk.ordinal(), 232.0F, widthHeight, widthHeight, 256, 256);
            int color = -1;
            MutableComponent perkText = new TextComponent(" " + perk.getSerializedName())
               .withStyle(Style.EMPTY.withColor(ModConfigs.PAXEL_CONFIGS.getPerkUpgrade(perk).getColor()));
            if (this.upgradedPerk) {
               color = ColorUtil.blendColors(ChatFormatting.GREEN.getColor(), -1, this.upgradeVisual / 30.0F);
            }

            this.font.drawShadow(poseStack, perkText, xOffset + 12, yOffset + 3, color);
            yOffset += 16;
         }
      }
   }

   public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(poseStack);
      super.render(poseStack, mouseX, mouseY, partialTicks);

      for (ToolViseScreen.UpgradeButton b : this.upgradeButtons) {
         b.renderToolTip(poseStack, mouseX, mouseY);
      }

      this.renderTooltip(poseStack, mouseX, mouseY);
   }

   private void renderUpgradeCost(PoseStack poseStack, PaxelConfigs.Upgrade upgrade) {
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, this.getBlitOffset() + 300);
      int k = (this.width - this.imageWidth) / 2 + 4;
      int l = (this.height - this.imageHeight) / 2;

      for (int i = 0; i < this.ingredientCounts.length; i++) {
         int cost = upgrade.getMaterialCost(i);
         if (cost != 0) {
            Slot slot = ((ToolViseContainerMenu)this.menu).getSlot(i + 1 + 36);
            this.font
               .drawShadow(poseStack, (cost > this.ingredientCounts[i] ? ChatFormatting.RED : ChatFormatting.GREEN) + "-" + cost, k + slot.x, l + slot.y, -1);
         }
      }

      poseStack.popPose();
   }

   protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
      super.renderLabels(pPoseStack, pMouseX, pMouseY);
   }

   private ItemStack getPaxel() {
      return ((ToolViseContainerMenu)this.menu).getSlot(36).getItem();
   }

   public boolean keyPressed(int key, int b, int c) {
      if (key == 256) {
         this.minecraft.player.closeContainer();
         return true;
      } else {
         return super.keyPressed(key, b, c);
      }
   }

   public void removed() {
      super.removed();
      Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
   }

   public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
      ItemStack paxel = this.getPaxel();
      if (paxel.isEmpty()) {
         this.upgradeButtons.forEach(u -> u.active = false);
         this.sturdinessPercentage = 0.0F;
         this.level = 0.0F;
         this.maxLevel = 0.0F;
         this.levelPerSocket = 0;
      } else {
         for (int i = 0; i < this.ingredientCounts.length; i++) {
            ItemStack item = ((ToolViseContainerMenu)this.menu).getSlot(i + 1 + 36).getItem();
            this.ingredientCounts[i] = item.isEmpty() ? 0 : item.getCount();
         }

         for (ToolViseScreen.UpgradeButton b : this.upgradeButtons) {
            b.active = b.upgrade.canCraftAndApply(this.ingredientCounts, pStack, b.stat);
         }

         this.sturdinessPercentage = PaxelItem.getSturdiness(paxel) / 100.0F;
         this.level = PaxelItem.getLevel(paxel);
         this.maxLevel = ModConfigs.PAXEL_CONFIGS.getTierValues(paxel).getMaxLevel();
         this.levelPerSocket = ModConfigs.PAXEL_CONFIGS.getTierValues(paxel).getLevelsPerSocket();
      }
   }

   public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
      int a = 1;
   }

   public class UpgradeButton extends AbstractButton {
      private final int textureIndex;
      private final PaxelConfigs.Upgrade upgrade;
      private final PaxelItem.Stat stat;

      protected UpgradeButton(int x, int y, int index, PaxelConfigs.Upgrade upgrade, PaxelItem.Stat stat) {
         super(x, y, 18, 18, TextComponent.EMPTY);
         this.textureIndex = index;
         this.upgrade = upgrade;
         this.stat = stat;
      }

      public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
         RenderSystem.setShaderTexture(0, ToolViseScreen.TEXTURE);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int v = this.textureIndex * this.width;
         int u = 176;
         if (!this.active) {
            u += this.width;
         } else if (this.isHovered) {
            u += this.width * 2;
         }

         this.blit(poseStack, this.x, this.y, u, v, this.width, this.height);
      }

      public void renderToolTip(PoseStack pPoseStack, int x, int y) {
         if (this.isHoveredOrFocused()) {
            ToolViseScreen.this.renderUpgradeCost(pPoseStack, this.upgrade);
            TooltipUtil.renderTooltip(
               pPoseStack, this.upgrade.getTooltip(ToolViseScreen.this.ingredientCounts, this.stat), this.x + this.width / 2, y, ToolViseScreen.this, true
            );
         }
      }

      public void onPress() {
         ToolViseScreen.this.upgradeVisual = 30;
         ToolViseScreen.this.upgradedStat = this.stat;
         ToolViseScreen.this.upgradedPerk = false;
         ToolViseScreen.this.minecraft.gameMode.handleInventoryButtonClick(((ToolViseContainerMenu)ToolViseScreen.this.menu).containerId, this.stat.ordinal());
      }

      public void updateNarration(NarrationElementOutput output) {
      }
   }
}
