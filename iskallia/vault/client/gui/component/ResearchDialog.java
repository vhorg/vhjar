package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.ResearchWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ResearchMessage;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.talent.TalentTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ResearchDialog extends AbstractGui {
   private Rectangle bounds;
   private String researchName = null;
   private ResearchTree researchTree;
   private TalentTree talentTree;
   private ResearchWidget researchWidget;
   private ScrollableContainer descriptionComponent;
   private Button researchButton;

   public ResearchDialog(ResearchTree researchTree, TalentTree talentTree) {
      this.researchTree = researchTree;
      this.talentTree = talentTree;
      this.refreshWidgets();
   }

   public void refreshWidgets() {
      if (this.researchName != null) {
         SkillStyle researchStyle = ModConfigs.RESEARCHES_GUI.getStyles().get(this.researchName);
         this.researchWidget = new ResearchWidget(this.researchName, this.researchTree, researchStyle);
         Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
         String buttonText = this.researchTree.isResearched(this.researchName) ? "Researched" : "Research (" + research.getCost() + ")";
         this.researchButton = new Button(
            10,
            this.bounds.getHeight() - 40,
            this.bounds.getWidth() - 30,
            20,
            new StringTextComponent(buttonText),
            button -> this.research(),
            (button, matrixStack, x, y) -> {}
         );
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         this.researchButton.field_230693_o_ = !this.researchTree.isResearched(this.researchName)
            && (
               research.usesKnowledge()
                  ? VaultBarOverlay.unspentKnowledgePoints >= research.getCost()
                  : VaultBarOverlay.unspentSkillPoints >= research.getCost()
            );
      }
   }

   public void setResearchName(String researchName) {
      this.researchName = researchName;
      this.refreshWidgets();
   }

   public ResearchDialog setBounds(Rectangle bounds) {
      this.bounds = bounds;
      return this;
   }

   public Rectangle getHeadingBounds() {
      Rectangle researchBounds = this.researchWidget.getClickableBounds();
      Rectangle headingBounds = new Rectangle();
      headingBounds.x0 = 5;
      headingBounds.y0 = 5;
      headingBounds.x1 = headingBounds.x0 + this.bounds.getWidth() - 20;
      headingBounds.y1 = headingBounds.y0 + researchBounds.getHeight() + 5;
      return headingBounds;
   }

   public Rectangle getDescriptionsBounds() {
      Rectangle headingBounds = this.getHeadingBounds();
      Rectangle descriptionsBounds = new Rectangle();
      descriptionsBounds.x0 = headingBounds.x0;
      descriptionsBounds.y0 = headingBounds.y1 + 10;
      descriptionsBounds.x1 = headingBounds.x1;
      descriptionsBounds.y1 = this.bounds.getHeight() - 50;
      return descriptionsBounds;
   }

   public void research() {
      Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
      int cost = research.getCost();
      int unspentPoints = research.usesKnowledge() ? VaultBarOverlay.unspentKnowledgePoints : VaultBarOverlay.unspentSkillPoints;
      if (cost <= unspentPoints) {
         Minecraft minecraft = Minecraft.func_71410_x();
         if (minecraft.field_71439_g != null) {
            minecraft.field_71439_g.func_184185_a(ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         }

         this.researchTree.research(this.researchName);
         this.refreshWidgets();
         ModNetwork.CHANNEL.sendToServer(new ResearchMessage(this.researchName));
      }
   }

   public void mouseMoved(int screenX, int screenY) {
      if (this.bounds != null) {
         int containerX = screenX - this.bounds.x0;
         int containerY = screenY - this.bounds.y0;
         if (this.researchButton != null) {
            this.researchButton.func_212927_b(containerX, containerY);
         }
      }
   }

   public void mouseClicked(int screenX, int screenY, int button) {
      int containerX = screenX - this.bounds.x0;
      int containerY = screenY - this.bounds.y0;
      if (this.researchButton != null) {
         this.researchButton.func_231044_a_(containerX, containerY, button);
      }
   }

   public void mouseScrolled(double mouseX, double mouseY, double delta) {
      if (this.bounds.contains((int)mouseX, (int)mouseY)) {
         this.descriptionComponent.mouseScrolled(mouseX, mouseY, delta);
      }
   }

   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      matrixStack.func_227860_a_();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.researchName != null) {
         matrixStack.func_227861_a_(this.bounds.x0 + 5, this.bounds.y0 + 5, 0.0);
         this.renderHeading(matrixStack, mouseX, mouseY, partialTicks);
         this.descriptionComponent.setBounds(this.getDescriptionsBounds());
         this.descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);
         this.renderFooter(matrixStack, mouseX, mouseY, partialTicks);
         matrixStack.func_227860_a_();
      }
   }

   private void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      func_238467_a_(matrixStack, this.bounds.x0 + 5, this.bounds.y0 + 5, this.bounds.x1 - 5, this.bounds.y1 - 5, -3750202);
      this.func_238474_b_(matrixStack, this.bounds.x0, this.bounds.y0, 0, 44, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x1 - 5, this.bounds.y0, 8, 44, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x0, this.bounds.y1 - 5, 0, 52, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x1 - 5, this.bounds.y1 - 5, 8, 52, 5, 5);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.bounds.x0 + 5, this.bounds.y0, 0.0);
      matrixStack.func_227862_a_(this.bounds.getWidth() - 10, 1.0F, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 6, 44, 1, 5);
      matrixStack.func_227861_a_(0.0, this.bounds.getHeight() - 5, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 6, 52, 1, 5);
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.bounds.x0, this.bounds.y0 + 5, 0.0);
      matrixStack.func_227862_a_(1.0F, this.bounds.getHeight() - 10, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 0, 50, 5, 1);
      matrixStack.func_227861_a_(this.bounds.getWidth() - 5, 0.0, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 8, 50, 5, 1);
      matrixStack.func_227865_b_();
   }

   private void renderHeading(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
      SkillStyle abilityStyle = ModConfigs.RESEARCHES_GUI.getStyles().get(this.researchName);
      Rectangle abilityBounds = this.researchWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      boolean researched = this.researchTree.getResearchesDone().contains(this.researchName);
      String subText = !researched ? "Not Researched" : "Researched";
      int gap = 5;
      int contentWidth = abilityBounds.getWidth() + gap + Math.max(fontRenderer.func_78256_a(this.researchName), fontRenderer.func_78256_a(subText));
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack, this.researchName, abilityBounds.getWidth() + gap, 13.0F, !researched ? -1 : -1849, !researched ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack, subText, abilityBounds.getWidth() + gap, 23.0F, !researched ? -1 : -1849, !researched ? -16777216 : -12897536
      );
      matrixStack.func_227861_a_(-abilityStyle.x, -abilityStyle.y, 0.0);
      matrixStack.func_227861_a_(abilityBounds.getWidth() / 2.0F, 0.0, 0.0);
      matrixStack.func_227861_a_(0.0, 23.0, 0.0);
      this.researchWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      IFormattableTextComponent description = ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.researchName);
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, description, renderableBounds.getWidth(), 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x0;
      int containerY = mouseY - this.bounds.y0;
      this.researchButton.func_230430_a_(matrixStack, containerX, containerY, partialTicks);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
      boolean researched = this.researchTree.getResearchesDone().contains(this.researchName);
      if (!researched) {
         this.func_238474_b_(matrixStack, 13, this.bounds.getHeight() - 40 - 2, 121 + (research.usesKnowledge() ? 15 : 30), 0, 15, 23);
      }
   }
}
