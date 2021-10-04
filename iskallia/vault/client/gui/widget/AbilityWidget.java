package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.widget.connect.ConnectableWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityRegistry;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.effect.AbilityEffect;
import iskallia.vault.util.ResourceBoundary;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class AbilityWidget extends Widget implements ConnectableWidget, ComponentWidget {
   private static final int PIP_SIZE = 8;
   private static final int GAP_SIZE = 2;
   private static final int ICON_SIZE = 30;
   private static final int MAX_PIPs_INLINE = 4;
   private static final ResourceLocation SKILL_WIDGET_RESOURCE = new ResourceLocation("the_vault", "textures/gui/skill-widget.png");
   private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation("the_vault", "textures/gui/abilities.png");
   private final String abilityName;
   private final AbilityTree abilityTree;
   private final SkillStyle style;
   private boolean selected = false;
   private boolean hoverable = true;
   private boolean renderPips = true;

   public AbilityWidget(String abilityName, AbilityTree abilityTree, SkillStyle style) {
      super(
         style.x,
         style.y,
         48,
         pipRowCount(abilityTree.getNodeOf(AbilityRegistry.getAbility(abilityName)).getLevel()) * 10 - 2,
         new StringTextComponent("the_vault.widgets.ability")
      );
      this.abilityName = abilityName;
      this.abilityTree = abilityTree;
      this.style = style;
   }

   public AbilityNode<?, ?> makeAbilityNode() {
      AbilityGroup<?, ?> group = this.getAbilityGroup();
      AbilityNode<?, ?> node = this.abilityTree.getNodeOf(group);
      int level = node.getLevel();
      if (node.isLearned() && !this.isSpecialization()) {
         level = Math.min(level + 1, group.getMaxLevel());
      }

      return new AbilityNode(this.getAbility().getAbilityGroupName(), level, this.isSpecialization() ? this.abilityName : null);
   }

   public AbilityGroup<?, ?> getAbilityGroup() {
      return ModConfigs.ABILITIES.getAbilityGroupByName(this.getAbility().getAbilityGroupName());
   }

   private AbilityEffect<?> getAbility() {
      return AbilityRegistry.getAbility(this.abilityName);
   }

   public String getAbilityName() {
      return this.abilityName;
   }

   public boolean isSpecialization() {
      return !this.getAbility().getAbilityGroupName().equals(this.abilityName);
   }

   public AbilityTree getAbilityTree() {
      return this.abilityTree;
   }

   public boolean isLocked() {
      if (this.isSpecialization()) {
         AbilityNode<?, ?> existing = this.abilityTree.getNodeOf(this.getAbility());
         if (!existing.isLearned() || existing.getSpecialization() != null && !existing.getSpecialization().equals(this.abilityName)) {
            return true;
         }
      }

      return VaultBarOverlay.vaultLevel < this.makeAbilityNode().getAbilityConfig().getLevelRequirement();
   }

   @Override
   public Double getRenderPosition() {
      return new Double(this.field_230690_l_ - this.getRenderWidth() / 2.0, this.field_230691_m_ - this.getRenderHeight() / 2.0);
   }

   @Override
   public double getRenderWidth() {
      return 15.0;
   }

   @Override
   public double getRenderHeight() {
      return 15.0;
   }

   public int getClickableWidth() {
      int onlyIconWidth = 34;
      int pipLineWidth = Math.min(this.getAbilityGroup().getMaxLevel(), 4) * 10;
      return this.hasPips() ? Math.max(pipLineWidth, onlyIconWidth) : onlyIconWidth;
   }

   public int getClickableHeight() {
      int height = 34;
      if (this.hasPips()) {
         int lines = pipRowCount(this.getAbilityGroup().getMaxLevel());
         height += 2;
         height += lines * 8 + (lines - 1) * 2;
      }

      return height;
   }

   @Override
   public Rectangle getClickableBounds() {
      return new Rectangle(
         this.field_230690_l_ - this.getClickableWidth() / 2, this.field_230691_m_ - 15 - 2, this.getClickableWidth(), this.getClickableHeight()
      );
   }

   public boolean hasPips() {
      return this.renderPips && !this.isSpecialization() && this.getAbilityGroup().getMaxLevel() > 1;
   }

   public void setHoverable(boolean hoverable) {
      this.hoverable = hoverable;
   }

   public void setRenderPips(boolean renderPips) {
      this.renderPips = renderPips;
   }

   public boolean func_231047_b_(double mouseX, double mouseY) {
      return this.getClickableBounds().contains(mouseX, mouseY);
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      if (this.selected) {
         return false;
      } else {
         this.func_230988_a_(Minecraft.func_71410_x().func_147118_V());
         return true;
      }
   }

   public void select() {
      this.selected = true;
   }

   public void deselect() {
      this.selected = false;
   }

   public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, List<Runnable> postContainerRender) {
      this.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      Matrix4f current = matrixStack.func_227866_c_().func_227870_a_().func_226601_d_();
      postContainerRender.add(() -> {
         RenderSystem.pushMatrix();
         RenderSystem.multMatrix(current);
         this.renderHover(matrixStack, mouseX, mouseY, partialTicks);
         RenderSystem.popMatrix();
      });
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderIcon(matrixStack, mouseX, mouseY, partialTicks);
      if (this.hasPips()) {
         this.renderPips(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

   private void renderHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.hoverable && this.getClickableBounds().contains(mouseX, mouseY)) {
         AbilityNode<?, ?> node = this.makeAbilityNode();
         AbilityNode<?, ?> existing = this.abilityTree.getNodeOf(this.getAbility());
         List<ITextComponent> tTip = new ArrayList<>();
         tTip.add(new StringTextComponent(node.getGroup().getParentName()));
         if (this.isSpecialization()) {
            tTip.add(new StringTextComponent(node.getSpecializationName()).func_240699_a_(TextFormatting.ITALIC).func_240699_a_(TextFormatting.GOLD));
         }

         if (this.isLocked()
            && this.isSpecialization()
            && existing.getSpecialization() != null
            && !existing.getSpecialization().equals(node.getSpecialization())) {
            tTip.add(new StringTextComponent("Specialization already in use:").func_240699_a_(TextFormatting.RED));
            tTip.add(new StringTextComponent(existing.getSpecializationName()).func_240699_a_(TextFormatting.RED));
         }

         int levelRequirement = node.getGroup().getAbilityConfig(this.abilityName, Math.max(existing.getLevel() - 1, 0)).getLevelRequirement();
         if (levelRequirement > 0) {
            TextFormatting color;
            if (VaultBarOverlay.vaultLevel < levelRequirement) {
               color = TextFormatting.RED;
            } else {
               color = TextFormatting.GREEN;
            }

            tTip.add(new StringTextComponent("Requires level: " + levelRequirement).func_240699_a_(color));
         }

         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(this.field_230690_l_, this.field_230691_m_ - 15, 0.0);
         GuiUtils.drawHoveringText(matrixStack, tTip, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, -1, Minecraft.func_71410_x().field_71466_p);
         matrixStack.func_227865_b_();
         RenderSystem.enableBlend();
      }
   }

   public void renderIcon(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ResourceBoundary resourceBoundary = this.style.frameType.getResourceBoundary();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(-15.0, -15.0, 0.0);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(resourceBoundary.getResource());
      AbilityNode<?, ?> abilityNode = this.abilityTree.getNodeOf(this.getAbility());
      boolean locked = this.isLocked();
      int vOffset = 0;
      if (this.isSpecialization() && abilityNode.isLearned() && this.abilityName.equals(abilityNode.getSpecialization())) {
         vOffset = 31;
      } else if (!locked || !this.isSpecialization() && abilityNode.isLearned()) {
         if (this.selected || this.getClickableBounds().contains(mouseX, mouseY)) {
            vOffset = -31;
         } else if (this.isSpecialization()) {
            if (this.abilityName.equals(abilityNode.getSpecialization())) {
               vOffset = 31;
            }
         } else if (abilityNode.getLevel() >= 1) {
            vOffset = 31;
         }
      } else {
         vOffset = 62;
      }

      this.func_238474_b_(
         matrixStack,
         this.field_230690_l_,
         this.field_230691_m_,
         resourceBoundary.getU(),
         resourceBoundary.getV() + vOffset,
         resourceBoundary.getW(),
         resourceBoundary.getH()
      );
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(-8.0, -8.0, 0.0);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(ABILITIES_RESOURCE);
      this.func_238474_b_(matrixStack, this.field_230690_l_, this.field_230691_m_, this.style.u, this.style.v, 16, 16);
      matrixStack.func_227865_b_();
   }

   public void renderPips(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().field_71446_o.func_110577_a(SKILL_WIDGET_RESOURCE);
      AbilityGroup<?, ?> group = this.getAbilityGroup();
      int rowCount = pipRowCount(group.getMaxLevel());
      int remainingPips = group.getMaxLevel();
      int remainingFilledPips = this.abilityTree.getNodeOf(group).getLevel();

      for (int r = 0; r < rowCount; r++) {
         this.renderPipLine(
            matrixStack, this.field_230690_l_, this.field_230691_m_ + 15 + 4 + r * 10, Math.min(4, remainingPips), Math.min(4, remainingFilledPips)
         );
         remainingPips -= 4;
         remainingFilledPips -= 4;
      }
   }

   public void renderPipLine(MatrixStack matrixStack, int x, int y, int count, int filledCount) {
      int lineWidth = count * 8 + (count - 1) * 2;
      int remainingFilled = filledCount;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(x, y, 0.0);
      matrixStack.func_227861_a_(-lineWidth / 2.0F, -4.0, 0.0);

      for (int i = 0; i < count; i++) {
         if (remainingFilled > 0) {
            this.func_238474_b_(matrixStack, 0, 0, 1, 133, 8, 8);
            remainingFilled--;
         } else {
            this.func_238474_b_(matrixStack, 0, 0, 1, 124, 8, 8);
         }

         matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      }

      matrixStack.func_227865_b_();
   }

   public static int pipRowCount(int level) {
      return (int)Math.ceil(level / 4.0F);
   }
}
