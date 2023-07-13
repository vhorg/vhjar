package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.widget.ComponentWidget;
import iskallia.vault.client.util.LineRenderUtil;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.config.AbilitiesGUIConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.AbilityTree;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

public class AbilityWidgetSelectable extends AbilityWidget implements ComponentWidget {
   private boolean selected = false;
   private boolean keybindConflict = false;

   public AbilityWidgetSelectable(
      String abilityName, AbilityTree abilityTree, int x, int y, Map<NodeState, TextureAtlasRegion> background, TextureAtlasRegion icon
   ) {
      super(abilityName, abilityTree, x, y, background, icon);
      if (!this.isSpecialization()) {
         SpecializedSkill node = this.makeAbilityNode();
         KeyMapping keyMapping = ModKeybinds.abilityQuickfireKey.get(node.getId());

         for (KeyMapping km : Minecraft.getInstance().options.keyMappings) {
            if (km != keyMapping && keyMapping.same(km)) {
               this.keybindConflict = true;
               break;
            }
         }
      }
   }

   public int getClickableWidth() {
      return this.width;
   }

   public int getClickableHeight() {
      return this.height;
   }

   @Override
   public Rectangle getClickableBounds() {
      return new Rectangle(this.x - this.getClickableWidth() / 2, this.y - this.getClickableHeight() / 2, this.getClickableWidth(), this.getClickableHeight());
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.getClickableBounds().contains(mouseX, mouseY);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.selected) {
         return false;
      } else {
         this.playDownSound(Minecraft.getInstance().getSoundManager());
         return true;
      }
   }

   public void select() {
      this.selected = true;
   }

   public void deselect() {
      this.selected = false;
   }

   @Override
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
      if (containerBounds.contains(mouseX, mouseY)) {
         Matrix4f current = matrixStack.last().pose().copy();
         postContainerRender.add(() -> {
            matrixStack.pushPose();
            matrixStack.mulPoseMatrix(current);
            this.renderHover(matrixStack, containerMouseX, containerMouseY, partialTicks);
            matrixStack.popPose();
         });
      }
   }

   @Override
   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         RenderSystem.enableBlend();
         SpecializedSkill abilityNode = this.getAbilityGroup();
         int abilityLevel = ((TieredSkill)abilityNode.getSpecialization()).getUnmodifiedTier();
         int actualAbilityLevel = ((TieredSkill)abilityNode.getSpecialization()).getActualTier();
         int addedLevelDiff = actualAbilityLevel - abilityLevel;
         int abilityLevelMax = ((TieredSkill)abilityNode.getSpecialization()).getMaxLearnableTier();
         boolean isSpecialization = this.isSpecialization();
         NodeState state;
         if (isSpecialization && abilityNode.isUnlocked() && this.abilityName.equals(abilityNode.getSpecialization().getId())) {
            state = NodeState.SELECTED;
         } else if (!this.isLocked() || !isSpecialization && abilityNode.isUnlocked()) {
            if (this.selected || this.getClickableBounds().contains(mouseX, mouseY)) {
               state = NodeState.HOVERED;
            } else if (isSpecialization) {
               if (this.abilityName.equals(abilityNode.getSpecialization().getId())) {
                  state = NodeState.SELECTED;
               } else {
                  state = NodeState.DEFAULT;
               }
            } else if (abilityLevel >= 1) {
               state = NodeState.SELECTED;
            } else {
               state = NodeState.DEFAULT;
            }
         } else {
            state = NodeState.DISABLED;
         }

         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, 1.0);
         if (!isSpecialization) {
            AbilitiesGUIConfig.AbilityStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles().get(this.getAbilityGroup().getId());
            if (abilityStyle != null) {
               int specializationCount = abilityStyle.getSpecializationStyles().size() - 1;
               boolean hasSpecialization = this.getAbilityGroup().getIndex() != 0;
               if (specializationCount > 0) {
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  matrixStack.pushPose();
                  BufferBuilder builder = Tesselator.getInstance().getBuilder();
                  builder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                  matrixStack.translate(0.0, 0.0, -1.0);
                  int secondaryNodeHeight = AbilityNodeTextures.SECONDARY_NODE.get(NodeState.DEFAULT).height();
                  float lineY1 = this.y + this.height / 2.0F + 3.0F + secondaryNodeHeight / 2.0F + secondaryNodeHeight * (specializationCount - 1);
                  LineRenderUtil.getInstance().drawLine(builder, matrixStack, this.x, this.y, this.x, lineY1, 5.0, -16777216);
                  if (hasSpecialization) {
                     LineRenderUtil.getInstance().drawLine(builder, matrixStack, this.x, this.y, this.x, lineY1, 4.0, -3755746);
                     LineRenderUtil.getInstance().drawLine(builder, matrixStack, this.x, this.y, this.x, lineY1, 3.0, -7130);
                     LineRenderUtil.getInstance().drawLine(builder, matrixStack, this.x, this.y, this.x, lineY1, 2.0, -5016);
                  } else {
                     LineRenderUtil.getInstance().drawLine(builder, matrixStack, this.x, this.y, this.x, lineY1, 4.0, -11184811);
                  }

                  builder.end();
                  RenderSystem.setShader(GameRenderer::getPositionColorShader);
                  BufferUploader.end(builder);
                  matrixStack.popPose();
               }
            }
         }

         if (!isSpecialization && this.getAbilityGroup().isUnlocked()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pushPose();
            TextureAtlasRegion region = AbilityNodeTextures.NODE_BACKGROUND_LEVEL;
            matrixStack.translate(-region.width() / 2.0F, -this.height / 2.0F + (2 - region.height()), 0.0);
            region.blit(matrixStack, this.x, this.y);
            Font font = Minecraft.getInstance().font;
            String text = String.valueOf(abilityLevel);
            if (addedLevelDiff != 0) {
               text = text + (addedLevelDiff > 0 ? "+" : "") + addedLevelDiff;
            }

            font.draw(matrixStack, text, this.x + region.width() / 2.0F - font.width(text) / 2.0F, this.y + 2, abilityLevel >= abilityLevelMax ? -16711936 : -1);
            RenderSystem.enableDepthTest();
            matrixStack.popPose();
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.pushPose();
         matrixStack.translate(-this.width / 2.0F, -this.height / 2.0F, 0.0);
         this.background.get(state).blit(matrixStack, this.x, this.y);
         matrixStack.popPose();
         if (state == NodeState.DISABLED) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
         }

         matrixStack.pushPose();
         matrixStack.translate(-8.0, -8.0, 0.0);
         this.icon.blit(matrixStack, this.x, this.y);
         matrixStack.popPose();
         matrixStack.popPose();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.getClickableBounds().contains(mouseX, mouseY)) {
         SpecializedSkill node = this.makeAbilityNode();
         TieredSkill existing = this.getAbility();
         List<FormattedCharSequence> tooltip = new ArrayList<>();
         tooltip.add(FormattedCharSequence.forward(node.getSpecialization().getName(), Style.EMPTY));
         boolean specialization = this.isSpecialization();
         if (specialization) {
            tooltip.add(FormattedCharSequence.forward(node.getName(), Style.EMPTY.withItalic(true).withColor(ChatFormatting.GOLD)));
         }

         if (this.isLocked() && specialization && this.getAbilityGroup().getIndex() != 0 && this.getAbilityGroup().getIndex() != node.getIndex()) {
            tooltip.add(
               FormattedCharSequence.forward("Already using " + this.getAbilityGroup().getSpecialization().getName(), Style.EMPTY.withColor(ChatFormatting.RED))
            );
         }

         int levelRequirement = this.getAbility().getUnlockLevel();
         if (levelRequirement > 0) {
            ChatFormatting color;
            if (VaultBarOverlay.vaultLevel < levelRequirement) {
               color = ChatFormatting.RED;
            } else {
               color = ChatFormatting.GREEN;
            }

            tooltip.add(FormattedCharSequence.forward("Requires level: " + levelRequirement, Style.EMPTY.withColor(color)));
         }

         KeyMapping keyMapping = ModKeybinds.abilityQuickfireKey.get(node.getId());
         if (keyMapping != null && !specialization) {
            int color;
            if (keyMapping.isUnbound()) {
               color = 6710886;
            } else {
               color = this.keybindConflict ? 16733525 : 5635925;
            }

            MutableComponent translatedKeyMessage = (MutableComponent)(keyMapping.isUnbound()
               ? new TextComponent("[NONE]")
               : new TextComponent("").append(keyMapping.getTranslatedKeyMessage()));
            MutableComponent keybindComponent = new TextComponent("Keybind: ")
               .withStyle(Style.EMPTY.withColor(10066329))
               .append(translatedKeyMessage.withStyle(Style.EMPTY.withColor(color)));
            tooltip.add(keybindComponent.getVisualOrderText());
         }

         TooltipUtil.renderTooltip(matrixStack, tooltip, mouseX, mouseY, Integer.MAX_VALUE, Integer.MAX_VALUE);
         RenderSystem.enableBlend();
      }
   }
}
