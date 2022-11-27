package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.ArchetypeRegistry;
import iskallia.vault.util.SkinProfile;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ArchetypeWidgetSelectable extends AbstractWidget {
   protected final ResourceLocation archetypeId;
   protected final AbstractArchetype<?> archetype;
   protected final Map<NodeState, TextureAtlasRegion> background;
   protected final ArchetypeWidgetSelectable.IIconRenderer iconRenderer;
   protected final TranslatableComponent nameComponent;
   private boolean selected;
   protected final Supplier<ResourceLocation> equippedArchetype;

   public ArchetypeWidgetSelectable(
      ResourceLocation archetypeId,
      Supplier<ResourceLocation> equippedArchetype,
      int x,
      int y,
      Map<NodeState, TextureAtlasRegion> background,
      ArchetypeWidgetSelectable.IIconRenderer iconRenderer
   ) {
      super(x, y, background.get(NodeState.DEFAULT).width(), background.get(NodeState.DEFAULT).width(), new TextComponent("the_vault.widgets.archetype"));
      this.equippedArchetype = equippedArchetype;
      this.archetypeId = archetypeId;
      this.archetype = ArchetypeRegistry.getArchetype(this.archetypeId);
      this.background = background;
      this.iconRenderer = iconRenderer;
      this.nameComponent = new TranslatableComponent(this.archetype.getName());
   }

   public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
   }

   protected boolean isEquipped() {
      return this.archetypeId.equals(this.equippedArchetype.get());
   }

   public int getClickableWidth() {
      return this.width;
   }

   public int getClickableHeight() {
      return this.height;
   }

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

   public ResourceLocation getArchetypeId() {
      return this.archetypeId;
   }

   public TranslatableComponent getNameComponent() {
      return this.nameComponent;
   }

   protected int getLevelRequirement() {
      return this.archetype.getConfig().getLevelRequirement();
   }

   protected boolean isLevelLocked() {
      return this.isLevelLocked(this.getLevelRequirement());
   }

   protected boolean isLevelLocked(int levelRequirement) {
      return VaultBarOverlay.vaultLevel < levelRequirement;
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
      this.render(containerBounds, matrixStack, mouseX, mouseY, containerMouseX, containerMouseY, partialTicks);
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

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
   }

   public void render(Rectangle containerBounds, PoseStack matrixStack, int mouseX, int mouseY, int containerMouseX, int containerMouseY, float partialTicks) {
      RenderSystem.enableBlend();
      NodeState state;
      if (this.isEquipped()) {
         state = NodeState.SELECTED;
      } else if (this.isLevelLocked()) {
         state = NodeState.DISABLED;
      } else if (!this.selected && (!this.getClickableBounds().contains(containerMouseX, containerMouseY) || !containerBounds.contains(mouseX, mouseY))) {
         state = NodeState.DEFAULT;
      } else {
         state = NodeState.HOVERED;
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
      this.iconRenderer.render(matrixStack, this.x, this.y);
      matrixStack.popPose();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      ArchetypeNodeTextures.BANNER.blit(matrixStack, this.x - ArchetypeNodeTextures.BANNER.width() / 2, this.y + 44);
      int borderColor = -398145;
      FormattedCharSequence formattedCharSequence = this.nameComponent.getVisualOrderText();
      Minecraft minecraft = Minecraft.getInstance();
      Matrix4f matrix = matrixStack.last().pose();
      float x = this.x - minecraft.font.width(this.nameComponent) / 2.0F;
      float y = this.y + 49;
      BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      minecraft.font.drawInBatch(formattedCharSequence, x - 1.0F, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x + 1.0F, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x - 1.0F, y - 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x + 1.0F, y - 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x, y - 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x, y + 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x + 1.0F, y + 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x - 1.0F, y + 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      bufferSource.endBatch();
      minecraft.font.draw(matrixStack, this.nameComponent, x, y, -10138601);
      RenderSystem.enableDepthTest();
   }

   private void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.getClickableBounds().contains(mouseX, mouseY)) {
         List<FormattedCharSequence> tooltip = new ArrayList<>();
         tooltip.add(this.getNameComponent().getVisualOrderText());
         int levelRequirement = this.getLevelRequirement();
         if (levelRequirement > 0) {
            ChatFormatting color;
            if (this.isLevelLocked(levelRequirement)) {
               color = ChatFormatting.RED;
            } else {
               color = ChatFormatting.GREEN;
            }

            tooltip.add(FormattedCharSequence.forward("Requires level: " + levelRequirement, Style.EMPTY.withColor(color)));
         }

         TooltipUtil.renderTooltip(matrixStack, tooltip, mouseX, mouseY, Integer.MAX_VALUE, Integer.MAX_VALUE);
         RenderSystem.enableBlend();
      }
   }

   public interface IIconRenderer {
      void render(PoseStack var1, int var2, int var3);
   }

   public static class PlayerFaceIconRenderer implements ArchetypeWidgetSelectable.IIconRenderer {
      private final SkinProfile skinProfile;
      private final int size;
      private final int sizeHalf;
      private final int borderSize;
      private final int borderColor;

      public static ArchetypeWidgetSelectable.PlayerFaceIconRenderer of(String playerName, int size, int borderSize, int borderColor) {
         return new ArchetypeWidgetSelectable.PlayerFaceIconRenderer(playerName, size, borderSize, borderColor);
      }

      private PlayerFaceIconRenderer(String playerName, int size, int borderSize, int borderColor) {
         this.size = size;
         this.sizeHalf = size / 2;
         this.borderSize = borderSize;
         this.borderColor = borderColor;
         this.skinProfile = new SkinProfile();
         this.skinProfile.updateSkin(playerName);
      }

      @Override
      public void render(PoseStack poseStack, int x, int y) {
         GuiComponent.fill(
            poseStack,
            x - this.sizeHalf - this.borderSize,
            y - this.sizeHalf - this.borderSize,
            x + this.sizeHalf + this.borderSize,
            y + this.sizeHalf + this.borderSize,
            this.borderColor
         );
         poseStack.translate(-this.sizeHalf, -this.sizeHalf, 0.0);
         RenderSystem.setShaderTexture(0, this.skinProfile.getLocationSkin());
         GuiComponent.blit(poseStack, x, y, this.size, this.size, 8.0F, 8.0F, 8, 8, 64, 64);
         GuiComponent.blit(poseStack, x, y, this.size, this.size, 40.0F, 8.0F, 8, 8, 64, 64);
      }
   }

   public record TextureAtlasRegionIconRenderer(TextureAtlasRegion icon) implements ArchetypeWidgetSelectable.IIconRenderer {
      public static ArchetypeWidgetSelectable.TextureAtlasRegionIconRenderer of(TextureAtlasRegion icon) {
         return new ArchetypeWidgetSelectable.TextureAtlasRegionIconRenderer(icon);
      }

      @Override
      public void render(PoseStack poseStack, int x, int y) {
         poseStack.translate(-(this.icon.width() / 2.0F), -(this.icon.height() / 2.0F), 0.0);
         this.icon.blit(poseStack, x, y);
      }
   }
}
