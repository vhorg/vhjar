package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityRegistry;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.skill.ability.group.AbilityGroup;
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

   public AbilityNode<?, ?> makeAbilityNode() {
      AbilityGroup<?, ?> group = this.getAbilityGroup();
      AbilityNode<?, ?> node = this.abilityTree.getNodeOf(group);
      int level = node.getLevel();
      if (node.isLearned() && !this.isSpecialization()) {
         level = Math.min(level + 1, group.getMaxLevel());
      }

      return new AbilityNode(this.getAbility().getAbilityGroupName(), level, this.isSpecialization() ? this.abilityName : null);
   }

   private AbilityGroup<?, ?> getAbilityGroup() {
      return ModConfigs.ABILITIES.getAbilityGroupByName(this.getAbility().getAbilityGroupName());
   }

   public String getAbilityName() {
      return this.abilityName;
   }

   protected AbstractAbility<?> getAbility() {
      return AbilityRegistry.getAbility(this.abilityName);
   }

   protected boolean isSpecialization() {
      return !this.getAbility().getAbilityGroupName().equals(this.abilityName);
   }

   protected boolean isLocked() {
      if (this.isSpecialization()) {
         AbilityNode<?, ?> existing = this.abilityTree.getNodeOf(this.getAbility());
         if (!existing.isLearned() || existing.getSpecialization() != null && !existing.getSpecialization().equals(this.abilityName)) {
            return true;
         }
      }

      return VaultBarOverlay.vaultLevel < this.makeAbilityNode().getAbilityConfig().getLevelRequirement();
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
