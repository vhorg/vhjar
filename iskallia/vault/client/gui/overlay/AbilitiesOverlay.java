package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.atlas.AtlasBufferPosColorTex;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModShaders;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.TieredSkill;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class AbilitiesOverlay implements IIngameOverlay {
   private static final AbilitiesOverlay.TextRenderMode TEXT_RENDER_MODE = AbilitiesOverlay.TextRenderMode.MINECRAFT;
   private static final ResourceLocation ABILITY_TRAY_BG = VaultMod.id("gui/abilities/overlay_tray_bg");
   private static final ResourceLocation ABILITY_TRAY_MANA_ONLY_BG = VaultMod.id("gui/abilities/overlay_tray_mana_only_bg");
   private static final ResourceLocation ABILITY_TRAY_MANA_BAR = VaultMod.id("gui/abilities/overlay_tray_mana_bar");
   private static final ResourceLocation ABILITY_TRAY_MANA_BAR_FX_0 = VaultMod.id("gui/abilities/overlay_tray_mana_bar_fx_0");
   private static final ResourceLocation ABILITY_TRAY_MANA_BAR_FX_1 = VaultMod.id("gui/abilities/overlay_tray_mana_bar_fx_1");
   private static final ResourceLocation ABILITY_TRAY_MANA_BAR_FX_2 = VaultMod.id("gui/abilities/overlay_tray_mana_bar_fx_2");
   private static final ResourceLocation ABILITY_TRAY_MANA_FONT = VaultMod.id("gui/abilities/overlay_tray_mana_font");
   private static final ResourceLocation ABILITY_TRAY_COOLDOWN = VaultMod.id("gui/abilities/overlay_tray_cooldown");
   private static final ResourceLocation ABILITY_TRAY_SELECTED = VaultMod.id("gui/abilities/overlay_tray_selected");
   private static final ResourceLocation ABILITY_TRAY_SELECTED_ACTIVE = VaultMod.id("gui/abilities/overlay_tray_selected_active");
   private static final ResourceLocation ABILITY_TRAY_SELECTED_COOLDOWN = VaultMod.id("gui/abilities/overlay_tray_selected_cooldown");
   public static List<ResourceLocation> GUI_ELEMENTS = List.of(
      ABILITY_TRAY_BG,
      ABILITY_TRAY_MANA_ONLY_BG,
      ABILITY_TRAY_MANA_BAR,
      ABILITY_TRAY_MANA_BAR_FX_0,
      ABILITY_TRAY_MANA_BAR_FX_1,
      ABILITY_TRAY_MANA_BAR_FX_2,
      ABILITY_TRAY_MANA_FONT,
      ABILITY_TRAY_COOLDOWN,
      ABILITY_TRAY_SELECTED,
      ABILITY_TRAY_SELECTED_ACTIVE,
      ABILITY_TRAY_SELECTED_COOLDOWN
   );
   private static final AtlasBufferPosColorTex BUFFER = new AtlasBufferPosColorTex(new BufferBuilder(256));
   private static final int LAYER_BG = 0;
   private static final int LAYER_MANA = 1;
   private static final int LAYER_MANA_FX = 2;
   private static final int LAYER_MANA_TEXT = 10;
   private static final int LAYER_COOLDOWNS = 1;
   private static final int LAYER_ICONS = 2;
   private static final int LAYER_SELECTION = 10;
   private static final float[] MANA_FX_LAYER_POSITION_X = new float[]{0.0F, 23.0F, 37.0F};
   private static final float[] MANA_FX_LAYER_POSITION_Y = new float[]{0.0F, 3.0F, 7.0F};
   private static final AtlasBufferPosColorTex.Bounds BOUNDS = new AtlasBufferPosColorTex.Bounds();
   public static final AbilitiesOverlay.AbilityData ABILITY_DATA = new AbilitiesOverlay.AbilityData();

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      ABILITY_DATA.update();
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player != null) {
         minecraft.getProfiler().push("abilityBar");
         ITextureAtlas atlas = ModTextureAtlases.ABILITIES.get();
         TextureAtlasSprite spriteTrayBg = ABILITY_DATA.shouldRender ? atlas.getSprite(ABILITY_TRAY_BG) : atlas.getSprite(ABILITY_TRAY_MANA_ONLY_BG);
         TextureAtlasSprite spriteTrayManaBar = atlas.getSprite(ABILITY_TRAY_MANA_BAR);
         TextureAtlasSprite[] spriteTrayManaBarFX = new TextureAtlasSprite[]{
            atlas.getSprite(ABILITY_TRAY_MANA_BAR_FX_0), atlas.getSprite(ABILITY_TRAY_MANA_BAR_FX_1), atlas.getSprite(ABILITY_TRAY_MANA_BAR_FX_2)
         };
         TextureAtlasSprite spriteTrayCooldown = atlas.getSprite(ABILITY_TRAY_COOLDOWN);
         TextureAtlasSprite spriteTraySelected = atlas.getSprite(ABILITY_TRAY_SELECTED);
         TextureAtlasSprite spriteTraySelectedActive = atlas.getSprite(ABILITY_TRAY_SELECTED_ACTIVE);
         TextureAtlasSprite spriteTraySelectedCooldown = atlas.getSprite(ABILITY_TRAY_SELECTED_COOLDOWN);
         matrixStack.pushPose();
         matrixStack.translate(10.0, height - spriteTrayBg.getHeight(), 0.0);
         Matrix4f matrix = matrixStack.last().pose();
         if (!BUFFER.getBuilder().building()) {
            BUFFER.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
         }

         float mana = Mana.get(player);
         float manaMax = Mana.getMax(player);
         float percentMana = mana / manaMax;
         float manaFillWidth = spriteTrayManaBar.getWidth() * percentMana;
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         BUFFER.add(matrix, 0, 0, 0, spriteTrayBg);
         renderManaBar(matrix, spriteTrayManaBar, percentMana, manaFillWidth);
         renderManaBarFX(
            matrix,
            spriteTrayManaBarFX,
            mana,
            manaMax,
            manaFillWidth,
            partialTick,
            Mana.getRegenPerSecond(player),
            spriteTrayManaBar.getWidth(),
            spriteTrayManaBar.getHeight()
         );
         if (TEXT_RENDER_MODE == AbilitiesOverlay.TextRenderMode.CUSTOM) {
            minecraft.getProfiler().push("text");
            renderManaCustomText(matrix, mana, spriteTrayManaBar.getWidth(), atlas.getSprite(ABILITY_TRAY_MANA_FONT));
            minecraft.getProfiler().pop();
         }

         if (ABILITY_DATA.shouldRender) {
            renderAbilities(matrix, ABILITY_DATA, atlas, spriteTrayCooldown, spriteTraySelected, spriteTraySelectedActive, spriteTraySelectedCooldown);
         }

         RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
         BUFFER.end();
         if (TEXT_RENDER_MODE == AbilitiesOverlay.TextRenderMode.MINECRAFT) {
            minecraft.getProfiler().push("text");
            renderManaText(minecraft, spriteTrayManaBar, matrixStack, (int)mana);
            minecraft.getProfiler().pop();
         }

         RenderSystem.disableDepthTest();
         matrixStack.popPose();
         minecraft.getProfiler().pop();
      }
   }

   private static void renderAbilities(
      Matrix4f matrix,
      AbilitiesOverlay.AbilityData abilityData,
      ITextureAtlas atlas,
      TextureAtlasSprite spriteTrayCooldown,
      TextureAtlasSprite spriteTraySelected,
      TextureAtlasSprite spriteTraySelectedActive,
      TextureAtlasSprite spriteTraySelectedCooldown
   ) {
      TextureAtlasSprite spriteAbilityFocused = getAbilityNodeSprite(abilityData.selectAbilityNode.getParent(), atlas);
      TextureAtlasSprite spriteAbilityPrevious = getAbilityNodeSprite(abilityData.previousAbilityNode.getParent(), atlas);
      TextureAtlasSprite spriteAbilityNext = getAbilityNodeSprite(abilityData.nextAbilityNode.getParent(), atlas);
      int selectedCooldown = abilityData.selectAbilityNode.getRemainingCooldown();
      int previousCooldown = abilityData.previousAbilityNode.getRemainingCooldown();
      int nextCooldown = abilityData.nextAbilityNode.getRemainingCooldown();
      renderAbilityCooldowns(
         matrix,
         abilityData.selectAbilityNode,
         abilityData.previousAbilityNode,
         abilityData.nextAbilityNode,
         spriteTrayCooldown,
         selectedCooldown,
         previousCooldown,
         nextCooldown
      );
      renderAbilityIcons(matrix, spriteAbilityFocused, spriteAbilityPrevious, spriteAbilityNext, selectedCooldown, previousCooldown, nextCooldown);
      renderAbilitySelection(matrix, spriteTraySelected, spriteTraySelectedActive, spriteTraySelectedCooldown, selectedCooldown);
   }

   private static void renderAbilitySelection(
      Matrix4f matrix,
      TextureAtlasSprite spriteTraySelected,
      TextureAtlasSprite spriteTraySelectedActive,
      TextureAtlasSprite spriteTraySelectedCooldown,
      int selectedCooldown
   ) {
      if (selectedCooldown > 0) {
         BUFFER.add(matrix, 19, 13, 10, spriteTraySelectedCooldown);
      } else if (ClientAbilityData.getSelectedAbility().isActive()) {
         BUFFER.add(matrix, 19, 13, 10, spriteTraySelectedActive);
      } else {
         BUFFER.add(matrix, 19, 13, 10, spriteTraySelected);
      }
   }

   private static void renderAbilityIcons(
      Matrix4f matrix,
      TextureAtlasSprite spriteAbilityFocused,
      TextureAtlasSprite spriteAbilityPrevious,
      TextureAtlasSprite spriteAbilityNext,
      int selectedCooldown,
      int previousCooldown,
      int nextCooldown
   ) {
      ITextureAtlas atlas = ModTextureAtlases.ABILITIES.get();
      RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
      renderAbilityIcon(matrix, spriteAbilityFocused, selectedCooldown, 23, 17, 2);
      renderAbilityIcon(matrix, spriteAbilityPrevious, previousCooldown, 43, 17, 2);
      renderAbilityIcon(matrix, spriteAbilityNext, nextCooldown, 3, 17, 2);
   }

   private static void renderAbilityIcon(Matrix4f matrix, TextureAtlasSprite sprite, int cooldown, int x, int y, int z) {
      if (cooldown > 0) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
         ModShaders.getGrayscalePositionTexShader().withGrayscale(1.0F).withBrightness(0.5F).enable();
      } else {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
      }

      ScreenDrawHelper.draw(Mode.QUADS, DefaultVertexFormat.POSITION_TEX, buffer -> {
         buffer.vertex(matrix, x, y + sprite.getHeight(), z).uv(sprite.getU0(), sprite.getV1()).endVertex();
         buffer.vertex(matrix, x + sprite.getWidth(), y + sprite.getHeight(), z).uv(sprite.getU1(), sprite.getV1()).endVertex();
         buffer.vertex(matrix, x + sprite.getWidth(), y, z).uv(sprite.getU1(), sprite.getV0()).endVertex();
         buffer.vertex(matrix, x, y, z).uv(sprite.getU0(), sprite.getV0()).endVertex();
      });
   }

   private static void renderAbilityCooldowns(
      Matrix4f matrix,
      Ability selectedAbilityGroup,
      Ability previousAbilityNode,
      Ability nextAbilityNode,
      TextureAtlasSprite spriteTrayCooldown,
      int selectedCooldown,
      int previousCooldown,
      int nextCooldown
   ) {
      if (selectedCooldown > 0) {
         int selectedMaxCooldown = selectedAbilityGroup.getTotalCooldown();
         int cooldownHeight = getCooldownHeight(selectedCooldown, selectedMaxCooldown);
         BUFFER.add(matrix, 23, 17 + cooldownHeight, 1, 16, 16 - cooldownHeight, 1.0F, 1.0F, 1.0F, 0.3F, spriteTrayCooldown);
      }

      if (previousCooldown > 0) {
         int previousMaxCooldown = previousAbilityNode.getTotalCooldown();
         int cooldownHeight = getCooldownHeight(previousCooldown, previousMaxCooldown);
         BUFFER.add(matrix, 43, 17 + cooldownHeight, 1, 16, 16 - cooldownHeight, 1.0F, 1.0F, 1.0F, 0.3F, spriteTrayCooldown);
      }

      if (nextCooldown > 0) {
         int nextMaxCooldown = nextAbilityNode.getTotalCooldown();
         int cooldownHeight = getCooldownHeight(nextCooldown, nextMaxCooldown);
         BUFFER.add(matrix, 3, 17 + cooldownHeight, 1, 16, 16 - cooldownHeight, 1.0F, 1.0F, 1.0F, 0.3F, spriteTrayCooldown);
      }
   }

   private static void renderManaText(Minecraft minecraft, TextureAtlasSprite spriteTrayManaBar, PoseStack matrixStack, int mana) {
      matrixStack.pushPose();
      String manaString = String.valueOf(mana);
      float pX = 4.0F + (spriteTrayManaBar.getWidth() - minecraft.font.width(manaString) * 0.5F) * 0.5F;
      float pY = 4.0F + (spriteTrayManaBar.getHeight() - 9.0F * 0.5F) * 0.5F;
      matrixStack.translate(pX, pY, 10.0);
      matrixStack.scale(0.5F, 0.5F, 1.0F);
      minecraft.font.drawShadow(matrixStack, manaString, 0.0F, 0.0F, Color.CYAN.getRGB());
      matrixStack.popPose();
   }

   private static void renderManaBar(Matrix4f matrix, TextureAtlasSprite spriteTrayManaBar, float percentMana, float manaFillWidth) {
      int x = 4;
      int y = 4;
      int height = spriteTrayManaBar.getHeight();
      float u0 = spriteTrayManaBar.getU0();
      float u1 = u0 + (spriteTrayManaBar.getU1() - u0) * percentMana;
      float v0 = spriteTrayManaBar.getV0();
      float v1 = spriteTrayManaBar.getV1();
      BufferBuilder builder = BUFFER.getBuilder();
      builder.vertex(matrix, 4.0F, 4 + height, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u0, v1).endVertex();
      builder.vertex(matrix, 4.0F + manaFillWidth, 4 + height, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u1, v1).endVertex();
      builder.vertex(matrix, 4.0F + manaFillWidth, 4.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u1, v0).endVertex();
      builder.vertex(matrix, 4.0F, 4.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u0, v0).endVertex();
   }

   private static void renderManaBarFX(
      Matrix4f matrix,
      TextureAtlasSprite[] spriteTrayManaBarFX,
      float mana,
      float manaMax,
      float manaFillWidth,
      float partialTicks,
      float regenPerSecond,
      int manaBarWidth,
      int manaBarHeight
   ) {
      boolean isManaRegen = !Mth.equal(regenPerSecond, 0.0F) && !Mth.equal(mana, manaMax);
      int trailSize = isManaRegen ? 3 : 1;
      float horizontalSpeedScalar = isManaRegen ? Math.max(0.0F, regenPerSecond) : 0.0F;
      float verticalSpeedScalar = isManaRegen ? 0.0F : 1.0F;
      BOUNDS.set(4.0F, 4.0F, 4.0F + manaFillWidth, 4 + manaBarHeight);

      for (int layer = 0; layer < spriteTrayManaBarFX.length; layer++) {
         MANA_FX_LAYER_POSITION_X[layer] = (float)(
            MANA_FX_LAYER_POSITION_X[layer] + partialTicks * Math.pow(0.005F, layer * 0.25 + 1.0) * 30.0 * horizontalSpeedScalar
         );
         MANA_FX_LAYER_POSITION_Y[layer] = (float)(
            MANA_FX_LAYER_POSITION_Y[layer] - partialTicks * Math.pow(0.002F, layer * 0.25 + 1.0) * 30.0 * verticalSpeedScalar
         );

         for (int trail = 0; trail < trailSize; trail++) {
            TextureAtlasSprite sprite = spriteTrayManaBarFX[layer];
            float alpha = (float)Math.pow(0.4F - 0.1F * layer, trail * 0.5 + 1.0);
            int negX = 4 + (int)(MANA_FX_LAYER_POSITION_X[layer] - spriteTrayManaBarFX[layer].getWidth()) - trail - 1;
            int posX = 4 + (int)MANA_FX_LAYER_POSITION_X[layer] - trail;
            int negY = 4 + (int)(MANA_FX_LAYER_POSITION_Y[layer] - spriteTrayManaBarFX[layer].getHeight()) - 1;
            int posY = 4 + (int)MANA_FX_LAYER_POSITION_Y[layer];
            int z = 2 + layer;
            int spriteWidth = sprite.getWidth();
            int spriteHeight = sprite.getHeight();
            BUFFER.addBounded(matrix, negX, posY, z, spriteWidth, spriteHeight, 0.0F, 1.0F, 1.0F, alpha, sprite, BOUNDS);
            BUFFER.addBounded(matrix, posX, posY, z, spriteWidth, spriteHeight, 0.0F, 1.0F, 1.0F, alpha, sprite, BOUNDS);
            BUFFER.addBounded(matrix, negX, negY, z, spriteWidth, spriteHeight, 0.0F, 1.0F, 1.0F, alpha, sprite, BOUNDS);
            BUFFER.addBounded(matrix, posX, negY, z, spriteWidth, spriteHeight, 0.0F, 1.0F, 1.0F, alpha, sprite, BOUNDS);
         }

         if (MANA_FX_LAYER_POSITION_X[layer] > manaBarWidth) {
            MANA_FX_LAYER_POSITION_X[layer] = MANA_FX_LAYER_POSITION_X[layer] - manaBarWidth;
         }

         if (MANA_FX_LAYER_POSITION_Y[layer] < 0.0F) {
            MANA_FX_LAYER_POSITION_Y[layer] = MANA_FX_LAYER_POSITION_Y[layer] + manaBarHeight;
         }
      }
   }

   private static void renderManaCustomText(Matrix4f matrix, float mana, int manaBarWidth, TextureAtlasSprite spriteTrayManaFont) {
      int fontWidth = 5;
      BufferBuilder builder = BUFFER.getBuilder();
      String text = String.valueOf((int)Math.floor(mana));
      float textOriginX = 4.0F + (manaBarWidth - text.length() * 5) * 0.5F;
      float uWidth = (spriteTrayManaFont.getU1() - spriteTrayManaFont.getU0()) * 0.1F;

      for (int i = 0; i < text.length(); i++) {
         int intValue = Integer.parseInt(text.substring(i, i + 1));
         float uOffset = spriteTrayManaFont.getU0() + uWidth * intValue;
         float textX = textOriginX + 5 * i;
         builder.vertex(matrix, textX, 12.0F, 10.0F).color(0.0F, 1.0F, 1.0F, 1.0F).uv(uOffset, spriteTrayManaFont.getV1()).endVertex();
         builder.vertex(matrix, textX + 5.0F, 12.0F, 10.0F).color(0.0F, 1.0F, 1.0F, 1.0F).uv(uOffset + uWidth, spriteTrayManaFont.getV1()).endVertex();
         builder.vertex(matrix, textX + 5.0F, 5.0F, 10.0F).color(0.0F, 1.0F, 1.0F, 1.0F).uv(uOffset + uWidth, spriteTrayManaFont.getV0()).endVertex();
         builder.vertex(matrix, textX, 5.0F, 10.0F).color(0.0F, 1.0F, 1.0F, 1.0F).uv(uOffset, spriteTrayManaFont.getV0()).endVertex();
      }
   }

   private static int getCooldownHeight(float cooldown, int cooldownMax) {
      float cooldownPercent = 1.0F - cooldown / Math.max(1, cooldownMax);
      return (int)(16.0F * cooldownPercent);
   }

   private static TextureAtlasSprite getAbilityNodeSprite(Skill node, ITextureAtlas atlas) {
      String styleKey = node.getId();
      return atlas.getSprite(ModConfigs.ABILITIES_GUI.getIcon(styleKey));
   }

   public static class AbilityData {
      private Ability selectAbilityNode;
      private Ability previousAbilityNode;
      private Ability nextAbilityNode;
      public boolean shouldRender;

      private void update() {
         List<TieredSkill> abilities = ClientAbilityData.getLearnedAbilities();
         if (abilities.isEmpty()) {
            this.shouldRender = false;
         } else {
            this.selectAbilityNode = ClientAbilityData.getSelectedAbility();
            if (this.selectAbilityNode == null) {
               this.shouldRender = false;
            } else {
               int selectedAbilityIndex = ClientAbilityData.getIndexOf(this.selectAbilityNode.getParent().getId());
               if (selectedAbilityIndex == -1) {
                  this.shouldRender = false;
               } else {
                  int previousIndex = selectedAbilityIndex - 1;
                  if (previousIndex < 0) {
                     previousIndex += abilities.size();
                  }

                  this.previousAbilityNode = (Ability)abilities.get(previousIndex).getChild();
                  int nextIndex = selectedAbilityIndex + 1;
                  if (nextIndex >= abilities.size()) {
                     nextIndex -= abilities.size();
                  }

                  this.nextAbilityNode = (Ability)abilities.get(nextIndex).getChild();
                  this.shouldRender = true;
               }
            }
         }
      }
   }

   private static enum TextRenderMode {
      MINECRAFT,
      CUSTOM;
   }
}
