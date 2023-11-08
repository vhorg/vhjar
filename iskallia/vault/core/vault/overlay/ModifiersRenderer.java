package iskallia.vault.core.vault.overlay;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.config.VaultModifierOverlayConfig;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModifiersRenderer {
   public static final ResourceLocation RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");
   private static final boolean TEST_MODIFIERS = false;
   private static final Modifiers TEST_VAULT_MODIFIERS = new Modifiers();
   private static final ModifiersRenderer.ModifierTextRenderMode MODIFIER_TEXT_RENDER_MODE = ModifiersRenderer.ModifierTextRenderMode.SHADOW;
   private static final int BG_COLOR = Color.BLACK.getRGB();
   private static final float BG_A = (BG_COLOR >> 24 & 0xFF) / 255.0F;
   private static final float BG_R = (BG_COLOR >> 16 & 0xFF) / 255.0F;
   private static final float BG_G = (BG_COLOR >> 8 & 0xFF) / 255.0F;
   private static final float BG_B = (BG_COLOR & 0xFF) / 255.0F;
   private static final BufferBuilder ICON_BUFFER = new BufferBuilder(256);
   private static final BufferSource TEXT_BUFFER = MultiBufferSource.immediate(new BufferBuilder(256));
   private static final BufferBuilder BOX_BUFFER = new BufferBuilder(256);
   public static final Vector3f SHADOW_OFFSET = new Vector3f(0.0F, 0.0F, -1.0F);

   public static void render(Map<VaultModifier<?>, Integer> modifiers, PoseStack matrixStack) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, RESOURCE);
      RenderSystem.disableDepthTest();
      renderVaultModifiers(modifiers, matrixStack);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
   }

   public static void renderVaultModifiers(List<VaultModifierStack> group, PoseStack matrixStack) {
      Map<VaultModifier<?>, Integer> modifiers = new Object2IntLinkedOpenHashMap();

      for (VaultModifierStack stack : group) {
         modifiers.put(stack.getModifier(), stack.getSize());
      }

      renderVaultModifiers(modifiers, matrixStack);
   }

   public static void renderVaultModifiers(Map<VaultModifier<?>, Integer> group, PoseStack matrixStack) {
      Minecraft minecraft = Minecraft.getInstance();
      int right = minecraft.getWindow().getGuiScaledWidth();
      int bottom = minecraft.getWindow().getGuiScaledHeight();
      VaultModifierOverlayConfig config = ModConfigs.VAULT_MODIFIER_OVERLAY;
      int textOffsetX = 4;
      int textOffsetY = 2;
      ITextureAtlas textureAtlas = ModTextureAtlases.MODIFIERS.get();
      Matrix4f matrix = matrixStack.last().pose();
      if (!ICON_BUFFER.building()) {
         ICON_BUFFER.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      }

      if (MODIFIER_TEXT_RENDER_MODE == ModifiersRenderer.ModifierTextRenderMode.BOX && !BOX_BUFFER.building()) {
         BOX_BUFFER.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      }

      int index = 0;

      for (Entry<VaultModifier<?>, Integer> entry : group.entrySet()) {
         VaultModifier<?> modifier = entry.getKey();
         int amount = entry.getValue();
         Optional<ResourceLocation> icon = modifier.getIcon();
         if (!icon.isEmpty()) {
            TextureAtlasSprite sprite = textureAtlas.getSprite(icon.get());
            int iconOffsetX = (config.size + config.spacingX) * (index % config.columns);
            int iconOffsetY = (config.size + config.spacingY) * (index / config.columns);
            int iconX = right - (config.rightMargin + config.size) - iconOffsetX;
            int iconY = bottom - config.size - config.bottomMargin - iconOffsetY;
            ICON_BUFFER.vertex(matrix, iconX, (float)iconY + config.size, 0.0F).uv(sprite.getU0(), sprite.getV1()).endVertex();
            ICON_BUFFER.vertex(matrix, (float)iconX + config.size, (float)iconY + config.size, 0.0F).uv(sprite.getU1(), sprite.getV1()).endVertex();
            ICON_BUFFER.vertex(matrix, (float)iconX + config.size, iconY, 0.0F).uv(sprite.getU1(), sprite.getV0()).endVertex();
            ICON_BUFFER.vertex(matrix, iconX, iconY, 0.0F).uv(sprite.getU0(), sprite.getV0()).endVertex();
            if (amount > 1) {
               String textString = "x" + amount;
               int textWidth = minecraft.font.width(textString);
               if (MODIFIER_TEXT_RENDER_MODE != ModifiersRenderer.ModifierTextRenderMode.NONE) {
                  minecraft.font
                     .drawInBatch(
                        textString,
                        iconX + config.size - textWidth + 4,
                        iconY + config.size - 9 + 2,
                        Color.WHITE.getRGB(),
                        false,
                        matrix,
                        TEXT_BUFFER,
                        false,
                        0,
                        15728880
                     );
               }

               if (MODIFIER_TEXT_RENDER_MODE == ModifiersRenderer.ModifierTextRenderMode.OUTLINE) {
                  Matrix4f outlineMatrix = matrix.copy();
                  outlineMatrix.translate(SHADOW_OFFSET);

                  for (int x = -1; x <= 1; x++) {
                     for (int y = -1; y <= 1; y++) {
                        if (x != 0 || y != 0) {
                           minecraft.font
                              .drawInBatch(
                                 textString,
                                 x + iconX + config.size - textWidth + 4,
                                 y + iconY + config.size - 9 + 2,
                                 Color.BLACK.getRGB(),
                                 false,
                                 outlineMatrix,
                                 TEXT_BUFFER,
                                 false,
                                 0,
                                 15728880
                              );
                        }
                     }
                  }
               }

               if (MODIFIER_TEXT_RENDER_MODE == ModifiersRenderer.ModifierTextRenderMode.SHADOW) {
                  Matrix4f outlineMatrix = matrix.copy();
                  outlineMatrix.translate(SHADOW_OFFSET);
                  minecraft.font
                     .drawInBatch(
                        textString,
                        1 + iconX + config.size - textWidth + 4,
                        1 + iconY + config.size - 9 + 2,
                        Color.BLACK.getRGB(),
                        false,
                        outlineMatrix,
                        TEXT_BUFFER,
                        false,
                        0,
                        15728880
                     );
               }

               if (MODIFIER_TEXT_RENDER_MODE == ModifiersRenderer.ModifierTextRenderMode.BOX) {
                  float minX = iconX + config.size - textWidth - 1 + 4;
                  float minY = iconY + config.size - 9 - 1 + 2;
                  float maxX = iconX + config.size + 4;
                  float maxY = iconY + config.size - 1 + 2;
                  BOX_BUFFER.vertex(matrix, minX, maxY, 0.0F).color(BG_R, BG_G, BG_B, BG_A).endVertex();
                  BOX_BUFFER.vertex(matrix, maxX, maxY, 0.0F).color(BG_R, BG_G, BG_B, BG_A).endVertex();
                  BOX_BUFFER.vertex(matrix, maxX, minY, 0.0F).color(BG_R, BG_G, BG_B, BG_A).endVertex();
                  BOX_BUFFER.vertex(matrix, minX, minY, 0.0F).color(BG_R, BG_G, BG_B, BG_A).endVertex();
               }
            }

            index++;
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, textureAtlas.getAtlasResourceLocation());
      ICON_BUFFER.end();
      BufferUploader.end(ICON_BUFFER);
      if (MODIFIER_TEXT_RENDER_MODE == ModifiersRenderer.ModifierTextRenderMode.BOX) {
         RenderSystem.disableTexture();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         BOX_BUFFER.end();
         BufferUploader.end(BOX_BUFFER);
      }

      if (MODIFIER_TEXT_RENDER_MODE != ModifiersRenderer.ModifierTextRenderMode.NONE) {
         TEXT_BUFFER.endBatch();
      }
   }

   public static enum ModifierTextRenderMode {
      @SerializedName("plain")
      PLAIN,
      @SerializedName("outline")
      OUTLINE,
      @SerializedName("shadow")
      SHADOW,
      @SerializedName("box")
      BOX,
      @SerializedName("none")
      NONE;
   }
}
