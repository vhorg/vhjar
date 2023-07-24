package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.Cooldown;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.util.MathUtilities;
import java.awt.Rectangle;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

public class AbilitySelectionWidget extends AbstractWidget {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");
   protected SpecializedSkill ability;
   protected double angleBoundary;

   public AbilitySelectionWidget(int x, int y, SpecializedSkill ability, double angleBoundary) {
      super(x, y, 24, 24, new TextComponent(ability.getId()));
      this.ability = ability;
      this.angleBoundary = angleBoundary;
   }

   public SpecializedSkill getAbility() {
      return this.ability;
   }

   public Ability getSelectedAbility() {
      Player player = Minecraft.getInstance().player;
      return player == null
         ? null
         : (Ability)Optional.of(this.ability)
            .map(SpecializedSkill::getSpecialization)
            .filter(skill -> skill instanceof TieredSkill)
            .map(skill -> ((TieredSkill)skill).getChild())
            .filter(skill -> skill instanceof Ability)
            .orElse(null);
   }

   public Rectangle getBounds() {
      return new Rectangle(this.x - 12, this.y - 12, this.width, this.height);
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      Minecraft minecraft = Minecraft.getInstance();
      float midX = minecraft.getWindow().getGuiScaledWidth() / 2.0F;
      float midY = minecraft.getWindow().getGuiScaledHeight() / 2.0F;
      Vec2 towardsWidget = new Vec2(this.x - midX, this.y - midY);
      Vec2 towardsMouse = new Vec2((float)mouseX - midX, (float)(mouseY - midY));
      double dot = towardsWidget.x * towardsMouse.x + towardsWidget.y * towardsMouse.y;
      double angleBetween = Math.acos(dot / (MathUtilities.length(towardsWidget) * MathUtilities.length(towardsMouse)));
      return angleBetween < this.angleBoundary;
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         Rectangle bounds = this.getBounds();
         String styleKey = this.ability.getSpecialization().getId();
         Ability ability = this.getSelectedAbility();
         Cooldown cooldown = ability == null ? null : ability.getTreeCooldown().orElse(null);
         if (ClientAbilityData.isSelectedAbility(this.ability)) {
            RenderSystem.setShaderColor(0.7F, 0.7F, 0.7F, 0.3F);
         } else {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }

         RenderSystem.enableBlend();
         RenderSystem.setShaderTexture(0, HUD_RESOURCE);
         this.blit(matrixStack, bounds.x + 1, bounds.y + 1, 28, 36, 22, 22);
         ITextureAtlas atlas = ModTextureAtlases.ABILITIES.get();
         RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
         GuiComponent.blit(matrixStack, bounds.x + 4, bounds.y + 4, 0, 16, 16, atlas.getSprite(ModConfigs.ABILITIES_GUI.getIcon(styleKey)));
         if (cooldown != null && cooldown.getRemainingTicks() > 0) {
            RenderSystem.setShaderColor(0.7F, 0.7F, 0.7F, 0.5F);
            float cooldownPercent = (float)cooldown.getRemainingTicks() / cooldown.getMaxTicks();
            int cooldownHeight = (int)(16.0F * cooldownPercent);
            GuiComponent.fill(matrixStack, bounds.x + 4, bounds.y + 4 + (16 - cooldownHeight), bounds.x + 4 + 16, bounds.y + 4 + 16, -1711276033);
            RenderSystem.enableBlend();
         }

         if (ClientAbilityData.isSelectedAbility(this.ability)) {
            RenderSystem.setShaderTexture(0, HUD_RESOURCE);
            this.blit(matrixStack, bounds.x, bounds.y, 89, 13, 24, 24);
         } else if (this.isMouseOver(mouseX, mouseY)) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, HUD_RESOURCE);
            this.blit(matrixStack, bounds.x, bounds.y, 64 + (cooldown != null && cooldown.getRemainingTicks() > 0 ? 50 : 0), 13, 24, 24);
         }
      }
   }

   public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
   }
}
