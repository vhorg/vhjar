package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.util.MathUtilities;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class AbilitySelectionWidget extends Widget {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation("the_vault", "textures/gui/abilities.png");
   protected AbilityNode<?, ?> abilityNode;
   protected double angleBoundary;

   public AbilitySelectionWidget(int x, int y, AbilityNode<?, ?> abilityNode, double angleBoundary) {
      super(x, y, 24, 24, new StringTextComponent(abilityNode.getName()));
      this.abilityNode = abilityNode;
      this.angleBoundary = angleBoundary;
   }

   public AbilityNode<?, ?> getAbilityNode() {
      return this.abilityNode;
   }

   public Rectangle getBounds() {
      return new Rectangle(this.field_230690_l_ - 12, this.field_230691_m_ - 12, this.field_230688_j_, this.field_230689_k_);
   }

   public boolean func_231047_b_(double mouseX, double mouseY) {
      Minecraft minecraft = Minecraft.func_71410_x();
      float midX = minecraft.func_228018_at_().func_198107_o() / 2.0F;
      float midY = minecraft.func_228018_at_().func_198087_p() / 2.0F;
      Vector2f towardsWidget = new Vector2f(this.field_230690_l_ - midX, this.field_230691_m_ - midY);
      Vector2f towardsMouse = new Vector2f((float)mouseX - midX, (float)(mouseY - midY));
      double dot = towardsWidget.field_189982_i * towardsMouse.field_189982_i + towardsWidget.field_189983_j * towardsMouse.field_189983_j;
      double angleBetween = Math.acos(dot / (MathUtilities.length(towardsWidget) * MathUtilities.length(towardsMouse)));
      return angleBetween < this.angleBoundary;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle bounds = this.getBounds();
      Minecraft minecraft = Minecraft.func_71410_x();
      String styleKey = this.abilityNode.getSpecialization() != null ? this.abilityNode.getSpecialization() : this.abilityNode.getGroup().getParentName();
      SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles().get(styleKey);
      AbilityGroup<?, ?> thisAbility = this.abilityNode.getGroup();
      int cooldown = ClientAbilityData.getCooldown(thisAbility);
      int maxCooldown = ClientAbilityData.getMaxCooldown(thisAbility);
      if (thisAbility.equals(ClientAbilityData.getSelectedAbility())) {
         RenderSystem.color4f(0.7F, 0.7F, 0.7F, 0.3F);
      } else {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RenderSystem.enableBlend();
      minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
      this.func_238474_b_(matrixStack, bounds.x + 1, bounds.y + 1, 28, 36, 22, 22);
      minecraft.func_110434_K().func_110577_a(ABILITIES_RESOURCE);
      this.func_238474_b_(matrixStack, bounds.x + 4, bounds.y + 4, abilityStyle.u, abilityStyle.v, 16, 16);
      if (cooldown > 0) {
         RenderSystem.color4f(0.7F, 0.7F, 0.7F, 0.5F);
         float cooldownPercent = (float)cooldown / maxCooldown;
         int cooldownHeight = (int)(16.0F * cooldownPercent);
         AbstractGui.func_238467_a_(matrixStack, bounds.x + 4, bounds.y + 4 + (16 - cooldownHeight), bounds.x + 4 + 16, bounds.y + 4 + 16, -1711276033);
         RenderSystem.enableBlend();
      }

      if (thisAbility.equals(ClientAbilityData.getSelectedAbility())) {
         minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
         this.func_238474_b_(matrixStack, bounds.x, bounds.y, 89, 13, 24, 24);
      } else if (this.func_231047_b_(mouseX, mouseY)) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
         this.func_238474_b_(matrixStack, bounds.x, bounds.y, 64 + (cooldown > 0 ? 50 : 0), 13, 24, 24);
      }
   }
}
