package iskallia.vault.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import iskallia.vault.entity.champion.ChampionLogic;
import iskallia.vault.entity.champion.IChampionAffix;
import iskallia.vault.entity.champion.LeechOnHitAffix;
import iskallia.vault.entity.champion.OnHitApplyPotionAffix;
import iskallia.vault.entity.champion.PotionAuraAffix;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModRenderTypes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ChampionRenderer {
   private static final Map<String, ChampionRenderer.IChampionAffixRenderer<? extends IChampionAffix>> AFFIX_RENDERERS = new HashMap<>();

   public static void render(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, Quaternion cameraOrientation, float partialTicks) {
      if (entity instanceof LivingEntity livingEntity && ChampionLogic.isChampion(livingEntity)) {
         poseStack.pushPose();
         poseStack.translate(0.0, entity.getBbHeight() + 0.5, 0.0);
         poseStack.mulPose(cameraOrientation);
         poseStack.scale(-0.025F, -0.025F, 0.025F);
         renderChampionIndicator(poseStack, bufferSource);
         renderAffixes(entity, poseStack, bufferSource, partialTicks);
         poseStack.popPose();
      }
   }

   private static void renderAffixes(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
      if (entity instanceof ChampionLogic.IChampionLogicHolder championLogicHolder) {
         List<IChampionAffix> affixes = championLogicHolder.getChampionLogic().getAffixes();
         AtomicInteger xOffset = new AtomicInteger(-affixes.size() * 8);
         affixes.forEach(affix -> {
            ChampionRenderer.IChampionAffixRenderer<IChampionAffix> affixRenderer = getAffixRenderer(affix);
            if (affixRenderer != null) {
               RenderSystem.enableDepthTest();
               TextureAtlasSprite icon = affixRenderer.getIcon(affix);
               RenderSystem.setShaderTexture(0, icon.atlas().location());
               GuiComponent.blit(poseStack, -1 + xOffset.get(), 0, 0, icon.getWidth(), icon.getHeight(), icon);
               xOffset.set(xOffset.get() + 16);
               affixRenderer.render(entity, affix, poseStack, bufferSource, partialTicks);
            }
         });
      }
   }

   private static void renderChampionIndicator(PoseStack poseStack, MultiBufferSource bufferSource) {
      VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.CHAMPION_INDICATOR);
      Matrix4f matrix = poseStack.last().pose();
      float size = 10.0F;
      buffer.vertex(matrix, -size, -size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(15728880).endVertex();
      buffer.vertex(matrix, -size, size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(15728880).endVertex();
      buffer.vertex(matrix, size, size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).uv2(15728880).endVertex();
      buffer.vertex(matrix, size, -size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).uv2(15728880).endVertex();
   }

   private static <T extends IChampionAffix> void registerRenderer(Class<T> affixClass, String type, ChampionRenderer.IChampionAffixRenderer<T> renderer) {
      AFFIX_RENDERERS.put(type, renderer);
   }

   @Nullable
   private static <A extends IChampionAffix, T extends ChampionRenderer.IChampionAffixRenderer<A>> T getAffixRenderer(A affix) {
      String type = affix.getType();
      return (T)(!AFFIX_RENDERERS.containsKey(type) ? null : AFFIX_RENDERERS.get(type));
   }

   static {
      registerRenderer(
         OnHitApplyPotionAffix.class,
         "on_hit_apply_potion",
         affix -> affix.getMobEffect() == null ? null : Minecraft.getInstance().getMobEffectTextures().get(affix.getMobEffect())
      );
      registerRenderer(PotionAuraAffix.class, "potion_aura", new ChampionRenderer.IChampionAffixRenderer<PotionAuraAffix>() {
         @Nullable
         public TextureAtlasSprite getIcon(PotionAuraAffix affix) {
            return affix.getMobEffect() == null ? null : Minecraft.getInstance().getMobEffectTextures().get(affix.getMobEffect());
         }

         public void render(Entity entity, PotionAuraAffix affix, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
            if (affix.getMobEffect() != null) {
               int range = affix.getRange();
               Minecraft minecraft = Minecraft.getInstance();
               LocalPlayer player = minecraft.player;
               if (!minecraft.isPaused() && player != null && !(player.distanceTo(entity) > Math.max(range * 2, range + 5))) {
                  int color = affix.getMobEffect().getColor();
                  float red = (color >>> 16 & 0xFF) / 255.0F;
                  float green = (color >>> 8 & 0xFF) / 255.0F;
                  float blue = (color & 0xFF) / 255.0F;
                  Vec3 offset = new Vec3(range, 0.0, 0.0).yRot(entity.level.getRandom().nextFloat((float) (Math.PI * 2)));
                  Vec3 pos = entity.position().add(offset);
                  ParticleEngine pm = Minecraft.getInstance().particleEngine;
                  Particle particle = pm.createParticle((ParticleOptions)ModParticles.NOVA_CLOUD.get(), pos.x, pos.y + 0.2F, pos.z, 0.0, 0.0, 0.0);
                  if (particle != null) {
                     particle.setColor(red, green, blue);
                  }

                  for (int i = 0; i < 3; i++) {
                     offset = new Vec3(range, 0.1F, 0.0).yRot((float)Math.toRadians(entity.tickCount % 90 * 4.0F + 120 * i));
                     pos = entity.position().add(offset);
                     particle = pm.createParticle((ParticleOptions)ModParticles.CHAINING.get(), pos.x, pos.y + 0.2F, pos.z, 0.0, 0.0, 0.0);
                     if (particle != null) {
                        particle.setColor(Mth.clamp(red * 1.25F, 0.0F, 1.0F), Mth.clamp(green * 1.25F, 0.0F, 1.0F), Mth.clamp(blue * 1.25F, 0.0F, 1.0F));
                     }
                  }
               }
            }
         }
      });
      registerRenderer(LeechOnHitAffix.class, "leech_on_hit", affix -> Minecraft.getInstance().getMobEffectTextures().get(ModEffects.RAMPAGE_LEECH));
   }

   private interface IChampionAffixRenderer<T extends IChampionAffix> {
      @Nullable
      TextureAtlasSprite getIcon(T var1);

      default void render(Entity entity, T affix, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
      }
   }
}
