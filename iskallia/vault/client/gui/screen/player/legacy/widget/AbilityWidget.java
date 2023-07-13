package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.AbilityTree;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextComponent;

public class AbilityWidget extends AbstractWidget {
   protected final String abilityName;
   protected final AbilityTree abilityTree;
   protected final Map<NodeState, TextureAtlasRegion> background;
   protected final TextureAtlasRegion icon;

   public AbilityWidget(String abilityName, AbilityTree abilityTree, int x, int y, Map<NodeState, TextureAtlasRegion> background, TextureAtlasRegion icon) {
      super(x, y, background.get(NodeState.DEFAULT).width(), background.get(NodeState.DEFAULT).height(), new TextComponent("the_vault.widgets.ability"));
      this.abilityName = abilityName;
      this.abilityTree = abilityTree;
      this.background = background;
      this.icon = icon;
   }

   public SpecializedSkill makeAbilityNode() {
      SpecializedSkill group = this.getAbilityGroup();
      TieredSkill node = (TieredSkill)group.getSpecialization();
      int level = node.getUnmodifiedTier();
      SpecializedSkill result = group.copy();
      result.specialize(this.abilityName, SkillContext.ofClient());
      if (node.isUnlocked() && !this.isSpecialization() && level < node.getMaxLearnableTier()) {
         result.learn(SkillContext.ofClient());
      }

      return result;
   }

   public SpecializedSkill getAbilityGroup() {
      return (SpecializedSkill)this.getAbility().getParent();
   }

   public String getAbilityName() {
      return this.abilityName;
   }

   protected TieredSkill getAbility() {
      return (TieredSkill)this.abilityTree.getForId(this.abilityName).orElse(null);
   }

   protected boolean isSpecialization() {
      return !this.getAbilityGroup().getSpecialization(0).getId().equals(this.abilityName);
   }

   protected boolean isLocked() {
      if (this.isSpecialization()) {
         SpecializedSkill existing = this.getAbilityGroup();
         if (!existing.isUnlocked() || existing.getIndex() != 0 && !existing.getSpecialization().getId().equals(this.abilityName)) {
            return true;
         }
      }

      return VaultBarOverlay.vaultLevel < this.makeAbilityNode().getUnlockLevel();
   }

   public void renderWidget(
      PoseStack matrixStack,
      Rectangle containerBounds,
      int mouseX,
      int mouseY,
      int containerMouseX,
      int containerMouseY,
      float partialTicks,
      List<Runnable> postContainerRender
   ) {
      this.render(matrixStack, containerMouseX, containerMouseY, partialTicks);
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      matrixStack.pushPose();
      TextureAtlasRegion textureAtlasRegion = this.background.get(NodeState.DEFAULT);
      TextureAtlasSprite sprite = textureAtlasRegion.getSprite();
      matrixStack.translate(-sprite.getWidth() / 2.0F, -sprite.getHeight() / 2.0F, 0.0);
      textureAtlasRegion.blit(matrixStack, this.x, this.y);
      matrixStack.popPose();
      matrixStack.pushPose();
      TextureAtlasSprite spritex = this.icon.getSprite();
      matrixStack.translate(-spritex.getWidth() / 2.0F, -spritex.getHeight() / 2.0F, 0.0);
      this.icon.blit(matrixStack, this.x, this.y);
      matrixStack.popPose();
   }

   public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
   }
}
