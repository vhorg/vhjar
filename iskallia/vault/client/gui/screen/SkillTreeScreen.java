package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.component.AbilityDialog;
import iskallia.vault.client.gui.component.ComponentDialog;
import iskallia.vault.client.gui.component.PlayerStatisticsDialog;
import iskallia.vault.client.gui.component.ResearchDialog;
import iskallia.vault.client.gui.component.TalentDialog;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.tab.SkillTab;
import iskallia.vault.container.SkillTreeContainer;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkillTreeScreen extends ContainerScreen<SkillTreeContainer> {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability-tree.png");
   public static final ResourceLocation BACKGROUNDS_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability-tree-bgs.png");
   public static final int TAB_WIDTH = 28;
   public static final int GAP = 3;
   private final List<ComponentDialog> dialogs = new ArrayList<>();
   protected Tuple<SkillTab, ComponentDialog> activeTabTpl;

   public SkillTreeScreen(SkillTreeContainer container, PlayerInventory inventory, ITextComponent title) {
      super(container, inventory, new StringTextComponent("Ability Tree Screen"));
      PlayerStatisticsDialog statisticsDialog = new PlayerStatisticsDialog(this);
      this.dialogs.add(statisticsDialog);
      this.dialogs.add(new AbilityDialog(((SkillTreeContainer)this.func_212873_a_()).getAbilityTree(), this));
      this.dialogs.add(new TalentDialog(((SkillTreeContainer)this.func_212873_a_()).getTalentTree(), this));
      this.dialogs.add(new ResearchDialog(((SkillTreeContainer)this.func_212873_a_()).getResearchTree(), this));
      this.selectDialog(statisticsDialog);
      this.field_146999_f = 270;
      this.field_147000_g = 200;
   }

   private void selectDialog(ComponentDialog dialog) {
      this.activeTabTpl = new Tuple(dialog.createTab(), dialog);
      this.refreshWidgets();
   }

   protected void func_231160_c_() {
      this.field_146999_f = this.field_230708_k_;
      super.func_231160_c_();
   }

   public void refreshWidgets() {
      ((SkillTab)this.activeTabTpl.func_76341_a()).refresh();
      this.dialogs.forEach(ComponentDialog::refreshWidgets);
   }

   public Rectangle getContainerBounds() {
      return new Rectangle(30, 60, (int)(this.field_230708_k_ * 0.55F) - 30, this.field_230709_l_ - 30 - 60);
   }

   public Rectangle getTabBounds(int index, boolean active) {
      Rectangle containerBounds = this.getContainerBounds();
      return new Rectangle(containerBounds.x + 5 + index * 31, containerBounds.y - 25 - (active ? 21 : 17), 28, active ? 32 : 25);
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      Rectangle containerBounds = this.getContainerBounds();
      if (containerBounds.contains(mouseX, mouseY)) {
         ((SkillTab)this.activeTabTpl.func_76341_a()).func_231044_a_(mouseX, mouseY, button);
      } else {
         boolean updatedTab = false;
         ComponentDialog activeDialog = (ComponentDialog)this.activeTabTpl.func_76340_b();

         for (int i = 0; i < this.dialogs.size(); i++) {
            ComponentDialog thisDialog = this.dialogs.get(i);
            Rectangle tabBounds = this.getTabBounds(i, activeDialog.equals(thisDialog));
            if (tabBounds.contains(mouseX, mouseY)) {
               SkillTab activeTab = (SkillTab)this.activeTabTpl.func_76341_a();
               activeTab.func_231164_f_();
               this.selectDialog(thisDialog);
               updatedTab = true;
            }
         }

         if (!updatedTab) {
            activeDialog.mouseClicked(mouseX, mouseY, button);
         }
      }

      return super.func_231044_a_(mouseX, mouseY, button);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      ((SkillTab)this.activeTabTpl.func_76341_a()).func_231048_c_(mouseX, mouseY, button);
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public void func_212927_b(double mouseX, double mouseY) {
      ((SkillTab)this.activeTabTpl.func_76341_a()).func_212927_b(mouseX, mouseY);
      ((ComponentDialog)this.activeTabTpl.func_76340_b()).mouseMoved((int)mouseX, (int)mouseY);
   }

   public boolean func_231043_a_(double mouseX, double mouseY, double delta) {
      if (this.getContainerBounds().contains((int)mouseX, (int)mouseY)) {
         ((SkillTab)this.activeTabTpl.func_76341_a()).func_231043_a_(mouseX, mouseY, delta);
      } else {
         ((ComponentDialog)this.activeTabTpl.func_76340_b()).mouseScrolled(mouseX, mouseY, delta);
      }

      return super.func_231043_a_(mouseX, mouseY, delta);
   }

   public void func_231164_f_() {
      ((SkillTab)this.activeTabTpl.func_76341_a()).func_231164_f_();
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      this.func_230446_a_(matrixStack);
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      Rectangle containerBounds = this.getContainerBounds();
      List<Runnable> postRender = ((SkillTab)this.activeTabTpl.func_76341_a()).renderTab(containerBounds, matrixStack, mouseX, mouseY, partialTicks);
      this.renderSkillPointOverlay(matrixStack);
      this.renderKnowledgePointOverlay(matrixStack);
      this.renderContainerBorders(matrixStack);
      this.renderContainerTabs(matrixStack);
      this.renderVaultLevelBar(matrixStack);
      int x = containerBounds.x + containerBounds.width + 15;
      int y = containerBounds.y - 18;
      Rectangle dialogBounds = new Rectangle(x, y, this.field_230708_k_ - 21 - x, this.field_230709_l_ - 21 - y);
      ((ComponentDialog)this.activeTabTpl.func_76340_b()).setBounds(dialogBounds);
      ((ComponentDialog)this.activeTabTpl.func_76340_b()).render(matrixStack, mouseX, mouseY, partialTicks);
      postRender.forEach(Runnable::run);
   }

   private void renderSkillPointOverlay(MatrixStack matrixStack) {
      if (VaultBarOverlay.unspentSkillPoints > 0) {
         Minecraft mc = Minecraft.func_71410_x();
         IReorderingProcessor bidiTxt = new StringTextComponent("")
            .func_230529_a_(new StringTextComponent(String.valueOf(VaultBarOverlay.unspentSkillPoints)).func_240699_a_(TextFormatting.YELLOW))
            .func_240702_b_(" unspent skill point" + (VaultBarOverlay.unspentSkillPoints == 1 ? "" : "s"))
            .func_241878_f();
         int unspentWidth = mc.field_71466_p.func_243245_a(bidiTxt) + 5;
         mc.field_71466_p.func_238407_a_(matrixStack, bidiTxt, mc.func_228018_at_().func_198107_o() - unspentWidth, 18.0F, -1);
      }
   }

   private void renderKnowledgePointOverlay(MatrixStack matrixStack) {
      if (VaultBarOverlay.unspentKnowledgePoints > 0) {
         Minecraft mc = Minecraft.func_71410_x();
         IReorderingProcessor bidiTxt = new StringTextComponent("")
            .func_230529_a_(new StringTextComponent(String.valueOf(VaultBarOverlay.unspentKnowledgePoints)).func_240699_a_(TextFormatting.AQUA))
            .func_240702_b_(" unspent knowledge point" + (VaultBarOverlay.unspentKnowledgePoints == 1 ? "" : "s"))
            .func_241878_f();
         int unspentWidth = mc.field_71466_p.func_243245_a(bidiTxt) + 5;
         matrixStack.func_227860_a_();
         if (VaultBarOverlay.unspentSkillPoints > 0) {
            matrixStack.func_227861_a_(0.0, 12.0, 0.0);
         }

         mc.field_71466_p.func_238407_a_(matrixStack, bidiTxt, mc.func_228018_at_().func_198107_o() - unspentWidth, 18.0F, -1);
         matrixStack.func_227865_b_();
      }
   }

   private void renderVaultLevelBar(MatrixStack matrixStack) {
      Rectangle containerBounds = this.getContainerBounds();
      Minecraft minecraft = this.getMinecraft();
      minecraft.field_71446_o.func_110577_a(VaultBarOverlay.VAULT_HUD_SPRITE);
      String text = String.valueOf(VaultBarOverlay.vaultLevel);
      int textWidth = minecraft.field_71466_p.func_78256_a(text);
      int barWidth = 85;
      float expPercentage = (float)VaultBarOverlay.vaultExp / VaultBarOverlay.tnl;
      int barX = containerBounds.x + containerBounds.width - barWidth - 5;
      int barY = containerBounds.y - 10;
      minecraft.field_71456_v.func_238474_b_(matrixStack, barX, barY, 1, 1, barWidth, 5);
      minecraft.field_71456_v.func_238474_b_(matrixStack, barX, barY, 1, 7, (int)(barWidth * expPercentage), 5);
      FontHelper.drawStringWithBorder(matrixStack, text, (float)(barX - textWidth - 1), (float)(barY - 1), -6601, -12698050);
   }

   private void renderContainerTabs(MatrixStack matrixStack) {
      Rectangle containerBounds = this.getContainerBounds();
      ComponentDialog activeDialog = (ComponentDialog)this.activeTabTpl.func_76340_b();

      for (int i = 0; i < this.dialogs.size(); i++) {
         ComponentDialog thisDialog = this.dialogs.get(i);
         Point uv = thisDialog.getIconUV();
         boolean active = activeDialog.equals(thisDialog);
         Rectangle tabBounds = this.getTabBounds(i, active);
         this.func_238474_b_(matrixStack, tabBounds.x, tabBounds.y, 63, active ? 28 : 0, tabBounds.width, tabBounds.height);
         this.func_238474_b_(matrixStack, tabBounds.x + 6, containerBounds.y - 25 - 11, uv.x, uv.y, 16, 16);
      }

      this.getMinecraft()
         .field_71466_p
         .func_238421_b_(matrixStack, ((SkillTab)this.activeTabTpl.func_76341_a()).getTabName(), containerBounds.x, containerBounds.y - 12, -12632257);
   }

   private void renderContainerBorders(MatrixStack matrixStack) {
      assert this.field_230706_i_ != null;

      this.field_230706_i_.func_110434_K().func_110577_a(UI_RESOURCE);
      Rectangle ctBox = this.getContainerBounds();
      RenderSystem.enableBlend();
      this.func_238474_b_(matrixStack, ctBox.x - 9, ctBox.y - 18, 0, 0, 15, 24);
      this.func_238474_b_(matrixStack, ctBox.x + ctBox.width - 7, ctBox.y - 18, 18, 0, 15, 24);
      this.func_238474_b_(matrixStack, ctBox.x - 9, ctBox.y + ctBox.height - 7, 0, 27, 15, 16);
      this.func_238474_b_(matrixStack, ctBox.x + ctBox.width - 7, ctBox.y + ctBox.height - 7, 18, 27, 15, 16);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(ctBox.x + 6, ctBox.y - 18, 0.0);
      matrixStack.func_227862_a_(ctBox.width - 13, 1.0F, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 16, 0, 1, 24);
      matrixStack.func_227861_a_(0.0, ctBox.height + 11, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 16, 27, 1, 16);
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(ctBox.x - 9, ctBox.y + 6, 0.0);
      matrixStack.func_227862_a_(1.0F, ctBox.height - 13, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 0, 25, 15, 1);
      matrixStack.func_227861_a_(ctBox.width + 2, 0.0, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 18, 25, 15, 1);
      matrixStack.func_227865_b_();
   }
}
