package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import iskallia.vault.block.TotemMobDamageBlock;
import iskallia.vault.block.entity.TotemMobDamageTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModRenderTypes;
import iskallia.vault.util.EntityHelper;
import java.util.ArrayList;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TotemMobDamageRenderer extends TotemGlowRenderer<TotemMobDamageTileEntity> {
   private static final Predicate<Entity> ENTITY_SELECTION_FILTER = entity -> !(entity instanceof Player) && entity instanceof LivingEntity;
   private static final float LASER_WIDTH = 0.02F;
   private static final Vector4f LASER_COLOR = new Vector4f(TotemMobDamageTileEntity.PARTICLE_EFFECT_COLOR);

   public TotemMobDamageRenderer(Context context) {
      super(context);
   }

   @Nonnull
   @Override
   protected BlockState getGlowBlockState() {
      return (BlockState)ModBlocks.TOTEM_MOB_DAMAGE.defaultBlockState().setValue(TotemMobDamageBlock.TYPE, TotemMobDamageBlock.Type.GLOW);
   }

   @ParametersAreNonnullByDefault
   public void render(
      TotemMobDamageTileEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      Level level = blockEntity.getLevel();
      if (level != null) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderTypes.TOTEM_LASER_EFFECT);
            TotemMobDamageTileEntity.RenderContext renderContext = blockEntity.getRenderContext();
            AABB effectBounds = blockEntity.getEffectBounds();
            Vec3 effectOrigin = blockEntity.getEffectOrigin();
            float effectRadius = blockEntity.getEffectRadius();
            this.updateTargetList(level, renderContext, effectBounds, effectOrigin, effectRadius);

            for (LivingEntity livingEntity : renderContext.targetList) {
               if (livingEntity.isAlive()) {
                  Vec3 target = livingEntity.getBoundingBox().getCenter();
                  this.renderLaser(effectOrigin, target, 0.02F, LASER_COLOR, vertexConsumer, poseStack);
                  if (level.getGameTime() % 10L == 0L) {
                     target = livingEntity.getEyePosition(partialTick);
                     level.addParticle(blockEntity.getFountainParticleOptions(), target.x, target.y, target.z, 0.0, 0.0, 0.0);
                  }
               }
            }
         }
      }
   }

   private void updateTargetList(Level level, TotemMobDamageTileEntity.RenderContext renderContext, AABB effectBounds, Vec3 effectOrigin, float effectRadius) {
      boolean updateTargetList = false;
      if (renderContext.targetList == null) {
         renderContext.targetList = new ArrayList<>();
         updateTargetList = true;
      }

      if (level.getGameTime() % 10L == 0L) {
         updateTargetList = true;
      }

      if (updateTargetList) {
         renderContext.targetList.clear();
         EntityHelper.getEntitiesInRange(level, effectBounds, effectOrigin, effectRadius, ENTITY_SELECTION_FILTER, renderContext.targetList);
      }
   }

   private void renderLaser(Vec3 origin, Vec3 target, float laserWidth, Vector4f color, VertexConsumer vertexConsumer, PoseStack poseStack) {
      Vec3 vec = target.subtract(origin);
      float rotation = 0.0F;
      float pitch = (float)Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z));
      float yaw = (float)Math.atan2(-vec.z, vec.x);
      float length = (float)vec.length();
      poseStack.pushPose();
      poseStack.translate(0.5, 0.5, 0.5);
      poseStack.mulPose(Quaternion.fromXYZ(rotation, yaw, pitch));
      this.renderLaser(vertexConsumer, poseStack.last().pose(), laserWidth, length, color.x(), color.y(), color.z(), color.w());
      poseStack.popPose();
   }

   private void renderLaser(VertexConsumer buf, Matrix4f matrix, float laserWidth, float length, float r, float g, float b, float a) {
      for (int i = 0; i < 4; i++) {
         float width = laserWidth * i * 0.25F;
         buf.vertex(matrix, length, width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, -width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, length, -width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, length, -width, -width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, -width, -width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, width, -width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, length, width, -width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, length, width, -width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, width, -width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, length, width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, length, -width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, -width, width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, 0.0F, -width, -width).color(r, g, b, a).endVertex();
         buf.vertex(matrix, length, -width, -width).color(r, g, b, a).endVertex();
      }
   }

   static {
      LASER_COLOR.setW(0.04F);
   }
}
