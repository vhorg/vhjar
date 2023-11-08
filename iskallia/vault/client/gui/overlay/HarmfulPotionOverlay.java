package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.VaultMod;
import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.init.ModEffects;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class HarmfulPotionOverlay implements IIngameOverlay {
   protected static final ResourceLocation VIGNETTE_LOCATION = VaultMod.id("textures/gui/vignette.png");
   private static final int MAX_ALPHA_DURATION = 300;
   private static final Map<MobEffect, BiFunction<Integer, Integer, Float>> EFFECT_ALPHA = Map.of(
      MobEffects.POISON,
      (duration, amplifier) -> Math.min(duration, 300) / 300.0F * 0.75F,
      MobEffects.WITHER,
      (duration, amplifier) -> Math.min(duration, 300) / 300.0F * 1.0F,
      ModEffects.BLEED,
      (duration, amplifier) -> (float)Math.min(duration, 3) * amplifier.intValue() / 20.0F
   );

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.getCameraEntity() == minecraft.player && !((IVaultOptions)minecraft.options).doVanillaPotionDamageEffects()) {
         for (Entry<MobEffect, BiFunction<Integer, Integer, Float>> entry : EFFECT_ALPHA.entrySet()) {
            if (minecraft.player.hasEffect(entry.getKey())) {
               this.renderEffect(width, height, entry.getKey(), minecraft.player, entry.getValue());
               break;
            }
         }
      }
   }

   private void renderEffect(int width, int height, MobEffect effect, LocalPlayer player, BiFunction<Integer, Integer, Float> getAlpha) {
      MobEffectInstance effectInstance = player.getEffect(effect);
      if (effectInstance != null) {
         float alpha = getAlpha.apply(effectInstance.getDuration(), effectInstance.getAmplifier());
         this.render(effect.getColor(), width, height, VIGNETTE_LOCATION, alpha);
      }
   }

   protected void render(int color, int width, int height, ResourceLocation vignetteLocation, float alpha) {
      this.renderVignette(color, alpha, width, height, vignetteLocation);
   }

   protected void renderVignette(int color, float alpha, int width, int height, ResourceLocation texture) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      float b = (color & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float r = (color >> 16 & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(r, g, b, alpha);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, texture);
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
