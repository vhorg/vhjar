package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.config.LegacyMagnetConfigs;
import iskallia.vault.container.inventory.MagnetTableContainerMenu;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.LegacyMagnetItem;
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

public class MagnetTableScreen extends AbstractContainerScreen<MagnetTableContainerMenu> implements ContainerListener {
   public static final ResourceLocation TEXTURE = new ResourceLocation("the_vault", "textures/gui/magnet_table.png");
   private float sturdinessBar = 0.0F;
   private final int[] ingredientCounts = new int[]{0, 0, 0, 0};
   private int upgradeVisual = 0;
   private LegacyMagnetItem.Stat upgradedStat = null;
   private boolean upgradedPerk = false;
   private final List<MagnetTableScreen.UpgradeButton> upgradeButtons = new ArrayList<>();

   public MagnetTableScreen(MagnetTableContainerMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
      this.imageWidth = 176;
      this.imageHeight = 200;
      this.inventoryLabelY += 36;
      ((MagnetTableContainerMenu)this.menu).addSlotListener(this);
   }

   public void init() {
      super.init();
      this.upgradeButtons.clear();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      int u = 0;
      this.slotChanged(this.menu, 0, ((MagnetTableContainerMenu)this.menu).getSlot(0).getItem());
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
      int k = (this.width - this.imageWidth) / 2;
      int l = (this.height - this.imageHeight) / 2;
      this.blit(poseStack, k, l, 0, 0, this.imageWidth, this.imageHeight);
      int barSize = 34;
      int s = Math.round(this.sturdinessBar * barSize);
      ItemStack magnet = this.getMagnet();
      LegacyMagnetItem.Perk perk = LegacyMagnetItem.getPerk(magnet);
      int x0 = k + 46;
      int y0 = l + 48;
      if (s != 0) {
         int cl = LegacyMagnetItem.getSturdinessColor((int)(this.sturdinessBar * 100.0F)).getColor();
         GuiComponent.fill(poseStack, x0, y0, x0 + s, y0 + 3, 0xFF000000 | cl);
         TextComponent percentage = new TextComponent((int)(this.sturdinessBar * 100.0F) + "%");
         int centerX = x0 + barSize / 2;
         this.font.drawShadow(poseStack, percentage, centerX - this.font.width(percentage) / 2.0F, y0 + 5, cl);
         if (perk == LegacyMagnetItem.Perk.NONE) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            GuiComponent.blit(
               poseStack, centerX - 3 + (int)((ModConfigs.MAGNET_CONFIG.getSturdinessCutoff() - 50) * 0.13F), y0 - 2, 6, 6, 0.0F, 204.0F, 7, 7, 256, 256
            );
         }
      }

      if (!magnet.isEmpty()) {
         k += 100;
         l += 22;
         this.font.drawShadow(poseStack, new TranslatableComponent("tooltip.the_vault.magnet_upgrades"), k, l, -1);
         int U = 180;
         int V = 94;
         int W = 10;

         for (LegacyMagnetItem.Stat stat : LegacyMagnetItem.Stat.values()) {
            int value = LegacyMagnetItem.getStatUpgrade(magnet, stat);
            if (value != 0) {
               l += 14;
               int iconSize = 9 - 1;
               RenderSystem.setShaderTexture(0, TEXTURE);
               GuiComponent.blit(poseStack, k, l, iconSize, iconSize, U, V, W, W, 256, 256);
               int color = -1;
               if (this.upgradeVisual != 0 && this.upgradedStat.equals(stat)) {
                  color = ColorUtil.blendColors(ChatFormatting.GREEN.getColor(), -1, this.upgradeVisual / 30.0F);
               }

               this.font.drawShadow(poseStack, new TextComponent((value > 0 ? " +" : " ") + value), k + iconSize, l, color);
            }

            V += 18;
         }

         if (perk != LegacyMagnetItem.Perk.NONE) {
            l += 14;
            int iconSize = 9 - 1;
            RenderSystem.setShaderTexture(0, TEXTURE);
            GuiComponent.blit(poseStack, k, l, iconSize, iconSize, 198.0F, 77 + 18 * perk.ordinal(), W, W, 256, 256);
            int color = -1;
            MutableComponent perkText = new TextComponent(" " + perk.getSerializedName())
               .withStyle(Style.EMPTY.withColor(ModConfigs.MAGNET_CONFIG.getPerkUpgrade(perk).getColor()));
            if (this.upgradedPerk) {
               color = ColorUtil.blendColors(ChatFormatting.GREEN.getColor(), -1, this.upgradeVisual / 30.0F);
            }

            this.font.drawShadow(poseStack, perkText, k + iconSize, l, color);
         }
      }
   }

   public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(poseStack);
      super.render(poseStack, mouseX, mouseY, partialTicks);

      for (MagnetTableScreen.UpgradeButton b : this.upgradeButtons) {
         b.renderToolTip(poseStack, mouseX, mouseY);
      }

      this.renderTooltip(poseStack, mouseX, mouseY);
   }

   private void renderUpgradeCost(PoseStack poseStack, LegacyMagnetConfigs.Upgrade upgrade) {
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, this.getBlitOffset() + 300);
      int k = (this.width - this.imageWidth) / 2 + 4;
      int l = (this.height - this.imageHeight) / 2;

      for (int i = 1; i < 5; i++) {
         int cost = upgrade.getMaterialCost(i - 1);
         if (cost != 0) {
            Slot slot = ((MagnetTableContainerMenu)this.menu).getSlot(i);
            this.font
               .drawShadow(
                  poseStack, (cost > this.ingredientCounts[i - 1] ? ChatFormatting.RED : ChatFormatting.GREEN) + "-" + cost, k + slot.x, l + slot.y, -1
               );
         }
      }

      poseStack.popPose();
   }

   protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
      super.renderLabels(pPoseStack, pMouseX, pMouseY);
   }

   private ItemStack getMagnet() {
      return ((MagnetTableContainerMenu)this.menu).getSlot(0).getItem();
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
      ItemStack magnet = this.getMagnet();
      if (magnet.isEmpty()) {
         this.upgradeButtons.forEach(u -> u.active = false);
         this.sturdinessBar = 0.0F;
      } else {
         for (int i = 1; i < 5; i++) {
            ItemStack item = ((MagnetTableContainerMenu)this.menu).getSlot(i).getItem();
            this.ingredientCounts[i - 1] = item.isEmpty() ? 0 : item.getCount();
         }

         for (MagnetTableScreen.UpgradeButton b : this.upgradeButtons) {
            b.active = b.upgrade.canCraftAndApply(this.ingredientCounts, pStack, b.stat);
         }

         this.sturdinessBar = LegacyMagnetItem.getSturdiness(magnet) / 100.0F;
      }
   }

   public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
      int a = 1;
   }

   protected class UpgradeButton extends AbstractButton {
      private final int textureIndex;
      private final LegacyMagnetConfigs.Upgrade upgrade;
      private final LegacyMagnetItem.Stat stat;

      protected UpgradeButton(int x, int y, int index, LegacyMagnetConfigs.Upgrade upgrade, LegacyMagnetItem.Stat stat) {
         super(x, y, 18, 18, TextComponent.EMPTY);
         this.textureIndex = index;
         this.upgrade = upgrade;
         this.stat = stat;
      }

      public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
         RenderSystem.setShaderTexture(0, MagnetTableScreen.TEXTURE);
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
            MagnetTableScreen.this.renderUpgradeCost(pPoseStack, this.upgrade);
            TooltipUtil.renderTooltip(
               pPoseStack,
               this.upgrade.getTooltip(MagnetTableScreen.this.ingredientCounts, this.stat),
               this.x + this.width / 2,
               y,
               MagnetTableScreen.this,
               true
            );
         }
      }

      public void onPress() {
         MagnetTableScreen.this.upgradeVisual = 30;
         MagnetTableScreen.this.upgradedStat = this.stat;
         MagnetTableScreen.this.upgradedPerk = false;
         if (LegacyMagnetItem.getPerk(MagnetTableScreen.this.getMagnet()) == LegacyMagnetItem.Perk.NONE
            && LegacyMagnetItem.getSturdiness(MagnetTableScreen.this.getMagnet()) - ModConfigs.MAGNET_CONFIG.getSturdinessDecrement()
               <= ModConfigs.MAGNET_CONFIG.getSturdinessCutoff()) {
            MagnetTableScreen.this.upgradedPerk = true;
         }

         MagnetTableScreen.this.minecraft
            .gameMode
            .handleInventoryButtonClick(((MagnetTableContainerMenu)MagnetTableScreen.this.menu).containerId, this.stat.ordinal());
      }

      public void updateNarration(NarrationElementOutput output) {
      }
   }
}
