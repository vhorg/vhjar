package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.component.AbilityDialog;
import iskallia.vault.client.gui.component.ResearchDialog;
import iskallia.vault.client.gui.component.TalentDialog;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.tab.AbilitiesTab;
import iskallia.vault.client.gui.tab.ResearchesTab;
import iskallia.vault.client.gui.tab.SkillTab;
import iskallia.vault.client.gui.tab.TalentsTab;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkillTreeScreen extends ContainerScreen<SkillTreeContainer> {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability-tree.png");
   public static final ResourceLocation BACKGROUNDS_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability-tree-bgs.png");
   public static final int TAB_WIDTH = 28;
   public static final int GAP = 3;
   protected SkillTab activeTab = new AbilitiesTab(this);
   protected TalentDialog talentDialog;
   protected AbilityDialog abilityDialog;
   protected ResearchDialog researchDialog;

   public SkillTreeScreen(SkillTreeContainer container, PlayerInventory inventory, ITextComponent title) {
      super(container, inventory, new StringTextComponent("Ability Tree Screen!"));
      AbilityTree abilityTree = ((SkillTreeContainer)this.func_212873_a_()).getAbilityTree();
      TalentTree talentTree = ((SkillTreeContainer)this.func_212873_a_()).getTalentTree();
      ResearchTree researchTree = ((SkillTreeContainer)this.func_212873_a_()).getResearchTree();
      this.abilityDialog = new AbilityDialog(abilityTree);
      this.talentDialog = new TalentDialog(talentTree);
      this.researchDialog = new ResearchDialog(researchTree, talentTree);
      this.refreshWidgets();
      this.field_146999_f = 270;
      this.field_147000_g = 200;
   }

   protected void func_231160_c_() {
      this.field_146999_f = this.field_230708_k_;
      super.func_231160_c_();
   }

   public void refreshWidgets() {
      this.activeTab.refresh();
      if (this.talentDialog != null) {
         this.talentDialog.refreshWidgets();
      }

      if (this.researchDialog != null) {
         this.researchDialog.refreshWidgets();
      }

      if (this.abilityDialog != null) {
         this.abilityDialog.refreshWidgets();
      }
   }

   public Rectangle getContainerBounds() {
      Rectangle bounds = new Rectangle();
      bounds.x0 = 30;
      bounds.y0 = 60;
      bounds.x1 = (int)(this.field_230708_k_ * 0.55);
      bounds.y1 = this.field_230709_l_ - 30;
      return bounds;
   }

   public Rectangle getTabBounds(int index, boolean active) {
      Rectangle containerBounds = this.getContainerBounds();
      Rectangle bounds = new Rectangle();
      bounds.x0 = containerBounds.x0 + 5 + index * 31;
      bounds.y0 = containerBounds.y0 - 25 - (active ? 21 : 17);
      bounds.setWidth(28);
      bounds.setHeight(active ? 32 : 25);
      return bounds;
   }

   public TalentDialog getTalentDialog() {
      return this.talentDialog;
   }

   public ResearchDialog getResearchDialog() {
      return this.researchDialog;
   }

   public AbilityDialog getAbilityDialog() {
      return this.abilityDialog;
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      Rectangle containerBounds = this.getContainerBounds();
      if (containerBounds.contains((int)mouseX, (int)mouseY)) {
         this.activeTab.func_231044_a_(mouseX, mouseY, button);
      } else {
         Rectangle abilitiesTabBounds = this.getTabBounds(0, this.activeTab instanceof AbilitiesTab);
         Rectangle talentsTabBounds = this.getTabBounds(1, this.activeTab instanceof TalentsTab);
         Rectangle researchesTabBounds = this.getTabBounds(2, this.activeTab instanceof ResearchesTab);
         if (abilitiesTabBounds.contains((int)mouseX, (int)mouseY)) {
            this.activeTab.func_231164_f_();
            this.activeTab = new AbilitiesTab(this);
            this.refreshWidgets();
         } else if (talentsTabBounds.contains((int)mouseX, (int)mouseY)) {
            this.activeTab.func_231164_f_();
            this.activeTab = new TalentsTab(this);
            this.refreshWidgets();
         } else if (researchesTabBounds.contains((int)mouseX, (int)mouseY)) {
            this.activeTab.func_231164_f_();
            this.activeTab = new ResearchesTab(this);
            this.refreshWidgets();
         } else if (this.activeTab instanceof ResearchesTab) {
            this.researchDialog.mouseClicked((int)mouseX, (int)mouseY, button);
         } else if (this.activeTab instanceof TalentsTab) {
            this.talentDialog.mouseClicked((int)mouseX, (int)mouseY, button);
         } else if (this.activeTab instanceof AbilitiesTab) {
            this.abilityDialog.mouseClicked((int)mouseX, (int)mouseY, button);
         }
      }

      return super.func_231044_a_(mouseX, mouseY, button);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      this.activeTab.func_231048_c_(mouseX, mouseY, button);
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public void func_212927_b(double mouseX, double mouseY) {
      this.activeTab.func_212927_b(mouseX, mouseY);
      if (this.activeTab instanceof ResearchesTab) {
         this.researchDialog.mouseMoved((int)mouseX, (int)mouseY);
      } else if (this.activeTab instanceof TalentsTab) {
         this.talentDialog.mouseMoved((int)mouseX, (int)mouseY);
      } else if (this.activeTab instanceof AbilitiesTab) {
         this.abilityDialog.mouseMoved((int)mouseX, (int)mouseY);
      }
   }

   public boolean func_231043_a_(double mouseX, double mouseY, double delta) {
      if (this.getContainerBounds().contains((int)mouseX, (int)mouseY)) {
         this.activeTab.func_231043_a_(mouseX, mouseY, delta);
      } else if (this.activeTab instanceof ResearchesTab) {
         this.researchDialog.mouseScrolled(mouseX, mouseY, delta);
      } else if (this.activeTab instanceof TalentsTab) {
         this.talentDialog.mouseScrolled(mouseX, mouseY, delta);
      } else if (this.activeTab instanceof AbilitiesTab) {
         this.abilityDialog.mouseScrolled(mouseX, mouseY, delta);
      }

      return super.func_231043_a_(mouseX, mouseY, delta);
   }

   public void func_231164_f_() {
      this.activeTab.func_231164_f_();
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      this.func_230446_a_(matrixStack);
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      this.field_230712_o_.func_243248_b(matrixStack, new StringTextComponent(""), this.field_238742_p_, this.field_238743_q_, 4210752);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      UIHelper.renderOverflowHidden(matrixStack, this::renderContainerBackground, ms -> this.activeTab.func_230430_a_(ms, mouseX, mouseY, partialTicks));
      Rectangle containerBounds = this.getContainerBounds();
      if (VaultBarOverlay.unspentSkillPoints > 0) {
         this.getMinecraft().func_110434_K().func_110577_a(HUD_RESOURCE);
         int toastWidth = 160;
         int right = this.getMinecraft().func_228018_at_().func_198107_o();
         String unspentText = VaultBarOverlay.unspentSkillPoints == 1 ? " unspent skill point" : " unspent skill points";
         String unspentPointsText = VaultBarOverlay.unspentSkillPoints + "";
         int unspentPointsWidth = this.field_230706_i_.field_71466_p.func_78256_a(unspentPointsText);
         int unspentWidth = this.field_230706_i_.field_71466_p.func_78256_a(unspentText);
         int gap = 5;
         int yOffset = 18;
         this.field_230706_i_
            .field_71466_p
            .func_238405_a_(matrixStack, VaultBarOverlay.unspentSkillPoints + "", right - unspentWidth - unspentPointsWidth - gap, yOffset, -10240);
         this.field_230706_i_.field_71466_p.func_238405_a_(matrixStack, unspentText, right - unspentWidth - gap, yOffset, -1);
      }

      if (VaultBarOverlay.unspentKnowledgePoints > 0) {
         this.getMinecraft().func_110434_K().func_110577_a(HUD_RESOURCE);
         int right = this.getMinecraft().func_228018_at_().func_198107_o();
         String unspentText = VaultBarOverlay.unspentKnowledgePoints == 1 ? " unspent knowledge point" : " unspent knowledge points";
         String unspentPointsText = VaultBarOverlay.unspentKnowledgePoints + "";
         int unspentPointsWidth = this.field_230706_i_.field_71466_p.func_78256_a(unspentPointsText);
         int unspentWidth = this.field_230706_i_.field_71466_p.func_78256_a(unspentText);
         int gap = 5;
         int yOffset = 18;
         matrixStack.func_227860_a_();
         if (VaultBarOverlay.unspentSkillPoints > 0) {
            matrixStack.func_227861_a_(0.0, 12.0, 0.0);
         }

         this.field_230706_i_
            .field_71466_p
            .func_238405_a_(matrixStack, VaultBarOverlay.unspentKnowledgePoints + "", right - unspentWidth - unspentPointsWidth - gap, yOffset, -12527695);
         this.field_230706_i_.field_71466_p.func_238405_a_(matrixStack, unspentText, right - unspentWidth - gap, yOffset, -1);
         matrixStack.func_227865_b_();
      }

      this.renderContainerBorders(matrixStack);
      this.renderContainerTabs(matrixStack);
      Rectangle dialogBounds = new Rectangle();
      dialogBounds.x0 = containerBounds.x1 + 15;
      dialogBounds.y0 = containerBounds.y0 - 18;
      dialogBounds.x1 = this.field_230708_k_ - 21;
      dialogBounds.y1 = this.field_230709_l_ - 21;
      this.abilityDialog.setBounds(dialogBounds);
      this.researchDialog.setBounds(dialogBounds);
      this.talentDialog.setBounds(dialogBounds);
      if (this.activeTab instanceof ResearchesTab) {
         this.researchDialog.render(matrixStack, mouseX, mouseY, partialTicks);
      } else if (this.activeTab instanceof TalentsTab) {
         this.talentDialog.render(matrixStack, mouseX, mouseY, partialTicks);
      } else if (this.activeTab instanceof AbilitiesTab) {
         this.abilityDialog.render(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

   private void renderContainerTabs(MatrixStack matrixStack) {
      Rectangle containerBounds = this.getContainerBounds();
      Rectangle abilitiesTabBounds = this.getTabBounds(0, this.activeTab instanceof AbilitiesTab);
      this.func_238474_b_(
         matrixStack,
         abilitiesTabBounds.x0,
         abilitiesTabBounds.y0,
         63,
         this.activeTab instanceof AbilitiesTab ? 28 : 0,
         abilitiesTabBounds.getWidth(),
         abilitiesTabBounds.getHeight()
      );
      this.func_238474_b_(matrixStack, abilitiesTabBounds.x0 + 6, containerBounds.y0 - 25 - 11, 32, 60, 16, 16);
      Rectangle talentsTabBounds = this.getTabBounds(1, this.activeTab instanceof TalentsTab);
      this.func_238474_b_(
         matrixStack,
         talentsTabBounds.x0,
         talentsTabBounds.y0,
         63,
         this.activeTab instanceof TalentsTab ? 28 : 0,
         talentsTabBounds.getWidth(),
         talentsTabBounds.getHeight()
      );
      this.func_238474_b_(matrixStack, talentsTabBounds.x0 + 6, containerBounds.y0 - 25 - 11, 16, 60, 16, 16);
      Rectangle researchesTabBounds = this.getTabBounds(2, this.activeTab instanceof ResearchesTab);
      this.func_238474_b_(
         matrixStack,
         researchesTabBounds.x0,
         researchesTabBounds.y0,
         63,
         this.activeTab instanceof ResearchesTab ? 28 : 0,
         researchesTabBounds.getWidth(),
         researchesTabBounds.getHeight()
      );
      this.func_238474_b_(matrixStack, researchesTabBounds.x0 + 6, containerBounds.y0 - 25 - 11, 0, 60, 16, 16);
      Minecraft minecraft = this.getMinecraft();
      if (this.activeTab instanceof AbilitiesTab) {
         minecraft.field_71466_p.func_238421_b_(matrixStack, "Abilities", containerBounds.x0, containerBounds.y0 - 12, -12632257);
      } else if (this.activeTab instanceof TalentsTab) {
         minecraft.field_71466_p.func_238421_b_(matrixStack, "Talents", containerBounds.x0, containerBounds.y0 - 12, -12632257);
      } else if (this.activeTab instanceof ResearchesTab) {
         minecraft.field_71466_p.func_238421_b_(matrixStack, "Researches", containerBounds.x0, containerBounds.y0 - 12, -12632257);
      }

      minecraft.field_71446_o.func_110577_a(VaultBarOverlay.RESOURCE);
      String text = String.valueOf(VaultBarOverlay.vaultLevel);
      int textWidth = minecraft.field_71466_p.func_78256_a(text);
      int barWidth = 85;
      float expPercentage = (float)VaultBarOverlay.vaultExp / VaultBarOverlay.tnl;
      int barX = containerBounds.x1 - barWidth - 5;
      int barY = containerBounds.y0 - 10;
      minecraft.func_213239_aq().func_76320_a("vaultBar");
      minecraft.field_71456_v.func_238474_b_(matrixStack, barX, barY, 1, 1, barWidth, 5);
      minecraft.field_71456_v.func_238474_b_(matrixStack, barX, barY, 1, 7, (int)(barWidth * expPercentage), 5);
      FontHelper.drawStringWithBorder(matrixStack, text, barX - textWidth - 1, barY - 1, -6601, -12698050);
      minecraft.func_213239_aq().func_76319_b();
   }

   private void renderContainerBorders(MatrixStack matrixStack) {
      assert this.field_230706_i_ != null;

      this.field_230706_i_.func_110434_K().func_110577_a(UI_RESOURCE);
      Rectangle containerBounds = this.getContainerBounds();
      RenderSystem.enableBlend();
      this.func_238474_b_(matrixStack, containerBounds.x0 - 9, containerBounds.y0 - 18, 0, 0, 15, 24);
      this.func_238474_b_(matrixStack, containerBounds.x1 - 7, containerBounds.y0 - 18, 18, 0, 15, 24);
      this.func_238474_b_(matrixStack, containerBounds.x0 - 9, containerBounds.y1 - 7, 0, 27, 15, 16);
      this.func_238474_b_(matrixStack, containerBounds.x1 - 7, containerBounds.y1 - 7, 18, 27, 15, 16);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(containerBounds.x0 + 6, containerBounds.y0 - 18, 0.0);
      matrixStack.func_227862_a_(containerBounds.x1 - containerBounds.x0 - 13, 1.0F, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 16, 0, 1, 24);
      matrixStack.func_227861_a_(0.0, containerBounds.y1 - containerBounds.y0 + 11, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 16, 27, 1, 16);
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(containerBounds.x0 - 9, containerBounds.y0 + 6, 0.0);
      matrixStack.func_227862_a_(1.0F, containerBounds.y1 - containerBounds.y0 - 13, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 0, 25, 15, 1);
      matrixStack.func_227861_a_(containerBounds.x1 - containerBounds.x0 + 2, 0.0, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 18, 25, 15, 1);
      matrixStack.func_227865_b_();
   }

   private void renderContainerBackground(MatrixStack matrixStack) {
      assert this.field_230706_i_ != null;

      this.field_230706_i_.func_110434_K().func_110577_a(BACKGROUNDS_RESOURCE);
      Rectangle containerBounds = this.getContainerBounds();
      int textureSize = 16;
      int currentX = containerBounds.x0;
      int currentY = containerBounds.y0;
      int uncoveredWidth = containerBounds.getWidth();

      for (int uncoveredHeight = containerBounds.getHeight(); uncoveredWidth > 0; currentY = containerBounds.y0) {
         while (uncoveredHeight > 0) {
            this.func_238474_b_(matrixStack, currentX, currentY, 80, 0, Math.min(textureSize, uncoveredWidth), Math.min(textureSize, uncoveredHeight));
            uncoveredHeight -= textureSize;
            currentY += textureSize;
         }

         uncoveredWidth -= textureSize;
         currentX += textureSize;
         uncoveredHeight = containerBounds.getHeight();
      }
   }
}
