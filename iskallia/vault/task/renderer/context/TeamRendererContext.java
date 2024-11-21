package iskallia.vault.task.renderer.context;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.helper.LightmapHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeamRendererContext extends RendererContext {
   private final TeamRendererContext.Medium medium;
   private final int packedLight;
   private final int packedOverlay;

   private TeamRendererContext(
      PoseStack matrices, float tickDelta, BufferSource bufferSource, Font font, TeamRendererContext.Medium medium, int packedLight, int packedOverlay
   ) {
      super(matrices, tickDelta, bufferSource, font);
      this.medium = medium;
      this.packedLight = packedLight;
      this.packedOverlay = packedOverlay;
   }

   public static TeamRendererContext forHud(PoseStack matrices, float tickDelta, Font font) {
      return new TeamRendererContext(
         matrices,
         tickDelta,
         MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()),
         font,
         TeamRendererContext.Medium.HUD,
         LightmapHelper.getPackedFullbrightCoords(),
         OverlayTexture.NO_OVERLAY
      );
   }

   public static TeamRendererContext forWorld(PoseStack matrices, float tickDelta, Font font, int packedLight, int packedOverlay) {
      return new TeamRendererContext(
         matrices,
         tickDelta,
         MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()),
         font,
         TeamRendererContext.Medium.WORLD,
         packedLight,
         packedOverlay
      );
   }

   public boolean isWorld() {
      return this.medium == TeamRendererContext.Medium.WORLD;
   }

   public void renderStack(ItemStack stack) {
      this.renderInWorldStack(stack, 0, -15, 2.0F, this.packedLight, this.packedOverlay);
   }

   public void renderIcon(ResourceLocation icon) {
      this.push();
      this.scale(2.0, 2.0, 2.0);
      this.blit(icon, -8, -16, 0, 0, 16, 16, 16, 16);
      this.pop();
   }

   public void renderNameAndProgress(String name, String progress) {
      this.push();
      if (this.isWorld()) {
         this.translate(0.0, 0.0, -0.01);
      }

      RenderSystem.disableDepthTest();
      MutableComponent title = new TextComponent(name).withStyle(Style.EMPTY.withColor(13421772));
      MutableComponent progressText = new TextComponent(progress).withStyle(ChatFormatting.WHITE);
      MutableComponent titleShadow = new TextComponent(name).withStyle(ChatFormatting.BLACK);
      MutableComponent progressShadow = new TextComponent(progress).withStyle(ChatFormatting.BLACK);
      if (this.isWorld()) {
         int width = Minecraft.getInstance().font.width(title);
         int halfWidth = Math.min(width, 200) / 2;
         boolean twolineText = this.font.width(title) > 200;
         this.fill(-halfWidth - 3, twolineText ? -4 : 7, 2 * halfWidth + 6, twolineText ? 33 : 22, 855638016);
         this.setShaderColor(16777215);
         this.push();
         this.translate(0.0, 0.0, 0.01);
         float y = twolineText ? 3.0F : 13.0F;
         this.renderText(titleShadow, 0.0F, y, 200, true, true, 16777215, false);
         this.renderText(progressShadow, 0.0F, 23.0F, true, true);
         this.pop();
         this.renderText(title, 0.0F, y, 200, true, true, 16777215, false);
         this.renderText(progressText, 0.0F, 22.0F, true, true);
      } else {
         this.setShaderColor(16777215);
         this.renderText(titleShadow, 12.0F, 13.0F, false, true);
         this.renderText(title, 11.0F, 12.0F, false, true);
      }

      this.pop();
   }

   protected static enum Medium {
      HUD,
      WORLD;
   }
}
