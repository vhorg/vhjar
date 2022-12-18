package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.VaultMod;
import iskallia.vault.config.AbilitiesVignetteConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.util.PlayerRageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class VignetteOverlay implements IIngameOverlay {
   protected static final ResourceLocation VIGNETTE_LOCATION = VaultMod.id("textures/gui/vignette.png");

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      if (ModConfigs.ABILITIES_VIGNETTE.ENABLED) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player != null) {
            if (minecraft.player.hasEffect(ModEffects.GHOST_WALK)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.GHOST_WALK, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.TANK)) {
               if (minecraft.player.hasEffect(ModEffects.TANK_RESISTANCE)) {
                  int amplifier = minecraft.player.getEffect(ModEffects.TANK_RESISTANCE).getAmplifier();
                  float alpha = Math.min(ModConfigs.ABILITIES_VIGNETTE.TANK.alpha + amplifier / 255.0F, 0.4F);
                  TextColor color = ModConfigs.ABILITIES_VIGNETTE.TANK.color;
                  AbilitiesVignetteConfig.VignetteStyle style = ModConfigs.ABILITIES_VIGNETTE.TANK.style;
                  this.render(color, alpha, style, matrixStack, width, height);
               } else {
                  this.render(ModConfigs.ABILITIES_VIGNETTE.TANK, matrixStack, width, height);
               }
            } else if (minecraft.player.hasEffect(ModEffects.TANK_PROJECTILE)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.TANK_PROJECTILE, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.TANK_REFLECT)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.TANK_REFLECT, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.EXECUTE)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.EXECUTE, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.MANA_SHIELD)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.MANA_SHIELD, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.RAMPAGE)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.RAMPAGE, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.RAMPAGE_LEECH)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.RAMPAGE_LEECH, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.RAMPAGE_CHAIN)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.RAMPAGE_CHAIN, matrixStack, width, height);
            } else if (minecraft.player.hasEffect(ModEffects.RAGE)) {
               int rage = PlayerRageHelper.getCurrentRage(minecraft.player);
               if (rage > 0) {
                  AbilitiesVignetteConfig.VignetteData data = ModConfigs.ABILITIES_VIGNETTE.RAGE;
                  this.render(data.color, data.alpha * (rage / 100.0F), data.style, matrixStack, width, height);
               }
            } else if (minecraft.player.hasEffect(ModEffects.STONEFALL)) {
               this.render(ModConfigs.ABILITIES_VIGNETTE.STONEFALL, matrixStack, width, height);
            }
         }
      }
   }

   protected void render(AbilitiesVignetteConfig.VignetteData config, PoseStack matrixStack, int width, int height) {
      this.render(config.color, config.alpha, config.style, matrixStack, width, height);
   }

   protected void render(TextColor color, float alpha, AbilitiesVignetteConfig.VignetteStyle style, PoseStack matrixStack, int width, int height) {
      if (style == AbilitiesVignetteConfig.VignetteStyle.FILL) {
         this.renderFill(color, alpha, matrixStack, width, height);
      } else if (style == AbilitiesVignetteConfig.VignetteStyle.VIGNETTE) {
         this.renderVignette(color, alpha, matrixStack, width, height);
      }
   }

   protected void renderFill(TextColor color, float alpha, PoseStack matrixStack, int width, int height) {
      int colorValue = (int)(alpha * 255.0F) << 24 | 16777215 & color.getValue();
      GuiComponent.fill(matrixStack, 0, 0, width, height, colorValue);
   }

   protected void renderVignette(TextColor color, float alpha, PoseStack matrixStack, int width, int height) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      int colorValue = color.getValue();
      float b = (colorValue & 0xFF) / 255.0F;
      float g = (colorValue >> 8 & 0xFF) / 255.0F;
      float r = (colorValue >> 16 & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(r, g, b, alpha);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(0.0, height, -90.0).uv(0.0F, 1.0F).endVertex();
      bufferbuilder.vertex(width, height, -90.0).uv(1.0F, 1.0F).endVertex();
      bufferbuilder.vertex(width, 0.0, -90.0).uv(1.0F, 0.0F).endVertex();
      bufferbuilder.vertex(0.0, 0.0, -90.0).uv(0.0F, 0.0F).endVertex();
      tesselator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
   }
}
