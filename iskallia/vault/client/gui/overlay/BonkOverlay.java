package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.atlas.AtlasBufferPosColorTex;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModShaders;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.skill.ability.effect.spi.AbstractBonkAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.TieredSkill;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class BonkOverlay implements IIngameOverlay {
   private static final Minecraft minecraft = Minecraft.getInstance();
   private static final AtlasBufferPosColorTex BUFFER = new AtlasBufferPosColorTex(new BufferBuilder(256));
   private static final int LAYER_MANA_TEXT = 10;
   private static final int LAYER_ICONS = 2;

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player != null) {
         MobEffectInstance battleCry = player.getEffect(ModEffects.BATTLE_CRY);
         if (battleCry == null) {
            battleCry = player.getEffect(ModEffects.BATTLE_CRY_SPECTRAL_STRIKE);
            if (battleCry == null) {
               battleCry = player.getEffect(ModEffects.BATTLE_CRY_LUCKY_STRIKE);
               if (battleCry == null) {
                  return;
               }
            }
         }

         minecraft.getProfiler().push("abilityBar");
         ITextureAtlas atlas = ModTextureAtlases.ABILITIES.get();
         matrixStack.pushPose();
         if (!BUFFER.getBuilder().building()) {
            BUFFER.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
         }

         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         List<TieredSkill> abilities = ClientAbilityData.getLearnedAbilities();
         renderBonk(matrixStack, battleCry, abilities, atlas);
         RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
         BUFFER.end();
         minecraft.getProfiler().push("text");
         renderBonkText(minecraft, matrixStack, battleCry, abilities);
         minecraft.getProfiler().pop();
         RenderSystem.disableDepthTest();
         matrixStack.popPose();
         minecraft.getProfiler().pop();
      }
   }

   private static void renderBonk(PoseStack matrixStack, MobEffectInstance battleCry, List<TieredSkill> abilities, ITextureAtlas atlas) {
      for (TieredSkill skill : abilities) {
         matrixStack.pushPose();
         Ability ability = (Ability)skill.getChild();
         if (ability instanceof AbstractBonkAbility) {
            int x = 0;
            int y = 0;
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            x += screenWidth / 2 - 9;
            y += screenHeight / 2 + 10;
            TextureAtlasSprite spriteAbilityFocused = getAbilityNodeSprite(ability.getParent(), atlas);
            RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
            Matrix4f matrix = matrixStack.last().pose();
            matrixStack.translate(1.5, 0.0, 0.0);
            renderAbilityIcon(matrix, spriteAbilityFocused, x, y, 2);
            matrixStack.popPose();
         }
      }
   }

   private static void renderAbilityIcon(Matrix4f matrix, TextureAtlasSprite sprite, int x, int y, int z) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
      ModShaders.getGrayscalePositionTexShader().withGrayscale(0.5F).withBrightness(0.5F).enable();
      ScreenDrawHelper.draw(Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buffer -> {
         buffer.vertex(matrix, x, y + sprite.getHeight(), z).uv(sprite.getU0(), sprite.getV1()).endVertex();
         buffer.vertex(matrix, x + sprite.getWidth(), y + sprite.getHeight(), z).uv(sprite.getU1(), sprite.getV1()).endVertex();
         buffer.vertex(matrix, x + sprite.getWidth(), y, z).uv(sprite.getU1(), sprite.getV0()).endVertex();
         buffer.vertex(matrix, x, y, z).uv(sprite.getU0(), sprite.getV0()).endVertex();
      });
   }

   private static void renderBonkText(Minecraft minecraft, PoseStack matrixStack, MobEffectInstance battleCry, List<TieredSkill> abilities) {
      for (TieredSkill skill : abilities) {
         Ability ability = (Ability)skill.getChild();
         if (ability instanceof AbstractBonkAbility bonkAbility) {
            matrixStack.pushPose();
            String stacks = String.valueOf(battleCry.getAmplifier() + 1);
            int x = 0;
            int y = 0;
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            x += screenWidth / 2 + 2;
            y += screenHeight / 2 + 10 + 16;
            int max = bonkAbility.getMaxStacksTotal();
            float f = Math.max(0.0F, (float)(battleCry.getAmplifier() + 1) / max);
            matrixStack.translate(x - minecraft.font.width(stacks) / 2.0F, y, 10.0);
            matrixStack.scale(0.75F, 0.75F, 1.0F);
            minecraft.font.drawShadow(matrixStack, stacks, 0.0F, 0.0F, Mth.hsvToRgb(f / 3.0F, 1.0F, 0.25F), true);
            matrixStack.popPose();
         }
      }
   }

   private static TextureAtlasSprite getAbilityNodeSprite(Skill node, ITextureAtlas atlas) {
      String styleKey = node.getId();
      return atlas.getSprite(ModConfigs.ABILITIES_GUI.getIcon(styleKey));
   }
}
