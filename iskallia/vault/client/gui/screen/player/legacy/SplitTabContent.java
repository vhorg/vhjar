package iskallia.vault.client.gui.screen.player.legacy;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractPanRegion;
import iskallia.vault.client.render.TextureRegion;
import iskallia.vault.client.render.TextureRegionRenderer;
import iskallia.vault.init.ModConfigs;
import java.awt.Rectangle;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;

public class SplitTabContent extends AbstractTabContent implements TabContent {
   protected final AbstractDialog<?> dialog;
   protected final AbstractPanRegion<?> panRegion;
   private static final TextureRegion CONTAINER_BORDER_CORNER_TOP_LEFT = new TextureRegion(0, 0, 15, 24);
   private static final TextureRegion CONTAINER_BORDER_CORNER_TOP_RIGHT = new TextureRegion(18, 0, 15, 24);
   private static final TextureRegion CONTAINER_BORDER_CORNER_BOTTOM_LEFT = new TextureRegion(0, 27, 15, 16);
   private static final TextureRegion CONTAINER_BORDER_CORNER_BOTTOM_RIGHT = new TextureRegion(18, 27, 15, 16);
   private static final TextureRegion CONTAINER_BORDER_TOP = new TextureRegion(16, 0, 1, 24);
   private static final TextureRegion CONTAINER_BORDER_BOTTOM = new TextureRegion(16, 27, 1, 16);
   private static final TextureRegion CONTAINER_BORDER_LEFT = new TextureRegion(0, 25, 15, 1);
   private static final TextureRegion CONTAINER_BORDER_RIGHT = new TextureRegion(18, 25, 15, 1);

   public SplitTabContent(AbstractSkillTabElementContainerScreen<?> parentScreen, AbstractDialog<?> dialog, AbstractPanRegion<?> panRegion) {
      super(parentScreen);
      this.dialog = dialog;
      this.panRegion = panRegion;
   }

   @Override
   public void init() {
   }

   @Override
   public void update() {
      this.panRegion.update();
      this.dialog.update();
   }

   @Override
   public void removed() {
      this.panRegion.removed();
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.panRegion.getBounds().contains(mouseX, mouseY)
         ? this.panRegion.mouseClicked(mouseX, mouseY, button)
         : this.dialog.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return this.panRegion.mouseReleased(mouseX, mouseY, button);
   }

   @Override
   public void mouseMoved(double mouseX, double mouseY) {
      this.panRegion.mouseMoved(mouseX, mouseY);
      this.dialog.mouseMoved((int)mouseX, (int)mouseY);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      return this.panRegion.getBounds().contains((int)mouseX, (int)mouseY)
         ? this.panRegion.mouseScrolled(mouseX, mouseY, delta)
         : this.dialog.mouseScrolled(mouseX, mouseY, delta);
   }

   @Override
   public List<Runnable> render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle elementBounds = this.panRegion.getBounds();
      List<Runnable> postRender = this.panRegion.renderTab(elementBounds, matrixStack, mouseX, mouseY, partialTicks);
      this.renderContainerBorders(matrixStack);
      this.renderVaultLevelBar(matrixStack);
      int x = elementBounds.x + elementBounds.width + 15;
      int y = elementBounds.y - 18;
      Rectangle dialogBounds = new Rectangle(x, y, this.parentScreen.width - 21 - x, this.parentScreen.height - 21 - y);
      this.dialog.setBounds(dialogBounds);
      this.dialog.render(matrixStack, mouseX, mouseY, partialTicks);
      return postRender;
   }

   private void renderVaultLevelBar(PoseStack matrixStack) {
      Rectangle elementBounds = this.panRegion.getBounds();
      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.setShaderTexture(0, VaultBarOverlay.VAULT_HUD_SPRITE);
      String text = String.valueOf(VaultBarOverlay.vaultLevel);
      int textWidth = minecraft.font.width(text);
      int barWidth = 85;
      float expPercentage = (float)VaultBarOverlay.vaultExp / VaultBarOverlay.tnl;
      if (VaultBarOverlay.vaultLevel >= ModConfigs.LEVELS_META.getMaxLevel()) {
         expPercentage = 1.0F;
      }

      int barX = elementBounds.x + elementBounds.width - barWidth - 5;
      int barY = elementBounds.y - 10;
      matrixStack.pushPose();
      matrixStack.translate(0.0, 0.0, 100.0);
      minecraft.gui.blit(matrixStack, barX, barY, 1, 1, barWidth, 5);
      minecraft.gui.blit(matrixStack, barX, barY, 1, 7, (int)(barWidth * expPercentage), 5);
      FontHelper.drawStringWithBorder(matrixStack, text, (float)(barX - textWidth - 1), (float)(barY - 1), -6601, -12698050);
      matrixStack.popPose();
   }

   private void renderContainerBorders(PoseStack matrixStack) {
      Rectangle bounds = this.panRegion.getBounds();
      RenderSystem.enableBlend();
      TextureRegionRenderer.getInstance()
         .begin(ScreenTextures.UI_RESOURCE)
         .with(matrixStack)
         .draw(bounds.x - 9, bounds.y - 18, CONTAINER_BORDER_CORNER_TOP_LEFT)
         .draw(bounds.x + bounds.width - 7, bounds.y - 18, CONTAINER_BORDER_CORNER_TOP_RIGHT)
         .draw(bounds.x - 9, bounds.y + bounds.height - 7, CONTAINER_BORDER_CORNER_BOTTOM_LEFT)
         .draw(bounds.x + bounds.width - 7, bounds.y + bounds.height - 7, CONTAINER_BORDER_CORNER_BOTTOM_RIGHT)
         .push()
         .translateXY(bounds.x + 6, bounds.y - 18)
         .scaleX(bounds.width - 13)
         .draw(CONTAINER_BORDER_TOP)
         .translateY(bounds.height + 11)
         .draw(CONTAINER_BORDER_BOTTOM)
         .pop()
         .push()
         .translateXY(bounds.x - 9, bounds.y + 6)
         .scaleY(bounds.height - 13)
         .draw(CONTAINER_BORDER_LEFT)
         .translateX(bounds.width + 2)
         .draw(CONTAINER_BORDER_RIGHT)
         .pop()
         .end();
   }
}
