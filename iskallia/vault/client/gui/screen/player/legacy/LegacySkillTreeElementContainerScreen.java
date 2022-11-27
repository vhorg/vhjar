package iskallia.vault.client.gui.screen.player.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;
import iskallia.vault.container.NBTElementContainer;
import java.awt.Rectangle;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class LegacySkillTreeElementContainerScreen<T extends INBTSerializable<CompoundTag>>
   extends AbstractSkillTabElementContainerScreen<NBTElementContainer<T>>
   implements ILegacySkillTreeScreen {
   protected final TabContent content = this.getTabContent();

   public LegacySkillTreeElementContainerScreen(NBTElementContainer<T> container, Inventory inventory, Component title, IElementRenderer elementRenderer) {
      super(container, inventory, title, elementRenderer);
      this.update();
   }

   @Override
   public void update() {
      this.content.update();
   }

   public void removed() {
      super.removed();
      this.content.removed();
   }

   private Rectangle getContentBounds() {
      return new Rectangle(30, 60, this.width - 30, this.height - 30 - 60);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      Rectangle contentBounds = this.getContentBounds();
      return contentBounds.contains(mouseX, mouseY) && this.content.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.content.mouseReleased(mouseX, mouseY, button);
      return super.mouseReleased(mouseX, mouseY, button);
   }

   @Override
   public void mouseMoved(double mouseX, double mouseY) {
      this.content.mouseMoved(mouseX, mouseY);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      return this.content.mouseScrolled(mouseX, mouseY, delta);
   }

   @Override
   public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackgroundFill(matrixStack);
      List<Runnable> postRender = this.content.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderPointOverlay(matrixStack);
      postRender.forEach(Runnable::run);
      if (this.needsLayout) {
         this.layout(Spatials.zero());
         this.needsLayout = false;
      }

      this.renderElements(matrixStack, mouseX, mouseY, partialTicks);
      this.renderSlotItems(matrixStack, mouseX, mouseY, partialTicks);
      this.renderTooltips(matrixStack, mouseX, mouseY);
   }

   protected abstract void renderPointOverlay(PoseStack var1);

   protected void renderSkillPointOverlay(PoseStack matrixStack) {
      if (VaultBarOverlay.unspentSkillPoints > 0) {
         Minecraft minecraft = Minecraft.getInstance();
         FormattedCharSequence text = new TextComponent("")
            .append(new TextComponent(String.valueOf(VaultBarOverlay.unspentSkillPoints)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16766976))))
            .append(" unspent skill point" + (VaultBarOverlay.unspentSkillPoints == 1 ? "" : "s"))
            .getVisualOrderText();
         int unspentWidth = minecraft.font.width(text);
         minecraft.font.drawShadow(matrixStack, text, this.getContentBounds().width - unspentWidth, 12.0F, -1);
      }
   }

   protected void renderRegretPointOverlay(PoseStack matrixStack) {
      if (VaultBarOverlay.unspentRegretPoints > 0) {
         Minecraft minecraft = Minecraft.getInstance();
         FormattedCharSequence text = new TextComponent("")
            .append(new TextComponent(String.valueOf(VaultBarOverlay.unspentRegretPoints)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(14747439))))
            .append(" unspent regret point" + (VaultBarOverlay.unspentRegretPoints == 1 ? "" : "s"))
            .getVisualOrderText();
         int unspentWidth = minecraft.font.width(text);
         minecraft.font.drawShadow(matrixStack, text, this.getContentBounds().width - unspentWidth, 24.0F, -1);
      }
   }

   protected void renderKnowledgePointOverlay(PoseStack matrixStack) {
      if (VaultBarOverlay.unspentKnowledgePoints > 0) {
         Minecraft minecraft = Minecraft.getInstance();
         FormattedCharSequence text = new TextComponent("")
            .append(new TextComponent(String.valueOf(VaultBarOverlay.unspentKnowledgePoints)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4249521))))
            .append(" unspent knowledge point" + (VaultBarOverlay.unspentKnowledgePoints == 1 ? "" : "s"))
            .getVisualOrderText();
         int unspentWidth = minecraft.font.width(text);
         minecraft.font.drawShadow(matrixStack, text, this.getContentBounds().width - unspentWidth, 12.0F, -1);
      }
   }

   protected void renderArchetypePointOverlay(PoseStack matrixStack) {
      if (VaultBarOverlay.unspentArchetypePoints > 0) {
         Minecraft minecraft = Minecraft.getInstance();
         FormattedCharSequence text = new TextComponent("")
            .append(new TextComponent(String.valueOf(VaultBarOverlay.unspentArchetypePoints)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(-1871617))))
            .append(" unspent archetype point" + (VaultBarOverlay.unspentArchetypePoints == 1 ? "" : "s"))
            .getVisualOrderText();
         int unspentWidth = minecraft.font.width(text);
         minecraft.font.drawShadow(matrixStack, text, this.getContentBounds().width - unspentWidth, 12.0F, -1);
      }
   }
}
