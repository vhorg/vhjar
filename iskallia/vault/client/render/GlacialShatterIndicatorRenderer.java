package iskallia.vault.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class GlacialShatterIndicatorRenderer {
   public static void render(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, Quaternion cameraOrientation) {
      if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.GLACIAL_SHATTER)) {
         if (!livingEntity.isDeadOrDying()) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-((LivingEntity)entity).yBodyRot));
            poseStack.translate(-0.5, 0.1F, -0.5);
            Minecraft.getInstance()
               .getBlockRenderer()
               .renderSingleBlock(Blocks.ICE.defaultBlockState(), poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
            float height = 1.0F;

            while (height + 0.5F < entity.getBbHeight()) {
               poseStack.translate(0.0, 1.0, 0.0);
               Minecraft.getInstance()
                  .getBlockRenderer()
                  .renderSingleBlock(Blocks.ICE.defaultBlockState(), poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
               if (++height > 6.0F) {
                  break;
               }
            }

            poseStack.popPose();
            poseStack.pushPose();
            if (height > entity.getBbHeight()) {
               poseStack.translate(0.0, height + 0.5, 0.0);
            } else {
               poseStack.translate(0.0, entity.getBbHeight() + 0.5, 0.0);
            }

            poseStack.mulPose(cameraOrientation);
            poseStack.scale(-0.025F, -0.025F, 0.025F);
            VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.GLACIAL_SHATTER_INDICATOR);
            Matrix4f matrix = poseStack.last().pose();
            float size = 10.0F;
            buffer.vertex(matrix, -size, -size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(15728880).endVertex();
            buffer.vertex(matrix, -size, size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(15728880).endVertex();
            buffer.vertex(matrix, size, size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).uv2(15728880).endVertex();
            buffer.vertex(matrix, size, -size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).uv2(15728880).endVertex();
            poseStack.popPose();
         }
      }
   }

   @SubscribeEvent
   public static void onTick(LivingUpdateEvent event) {
      if (event.getEntity() instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.GLACIAL_SHATTER)) {
         if (!livingEntity.isDeadOrDying()) {
            if (livingEntity.level.random.nextInt(2) == 0) {
               ParticleEngine pm = Minecraft.getInstance().particleEngine;
               Particle particle = pm.createParticle(
                  (ParticleOptions)ModParticles.NOVA_SPEED.get(),
                  livingEntity.position().x
                     - (livingEntity.getBbWidth() / 2.0F - 0.5F)
                     + (livingEntity.getBbWidth() * livingEntity.level.random.nextFloat() + 1.0F),
                  livingEntity.position().y + (livingEntity.getBbHeight() + 1.0F) * livingEntity.level.random.nextFloat(),
                  livingEntity.position().z
                     - (livingEntity.getBbWidth() / 2.0F - 0.5F)
                     + (livingEntity.getBbWidth() * livingEntity.level.random.nextFloat() + 1.0F),
                  0.0,
                  0.0,
                  0.0
               );
               if (particle != null) {
                  particle.setParticleSpeed((Math.random() - 0.5) * 0.1F, -0.1, (Math.random() - 0.5) * 0.1F);
               }
            }
         }
      }
   }
}
