package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.entity.entity.SpiritEntity;
import java.util.List;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SpiritRenderer extends EntityRenderer<SpiritEntity> {
   private final SpiritRenderer.SpiritPlayerRenderer steveSpiritRenderer;
   private final SpiritRenderer.SpiritPlayerRenderer alexSpiritRenderer;

   public SpiritRenderer(Context context) {
      super(context);
      PlayerModel<SpiritEntity> steveModel = new PlayerModel<SpiritEntity>(context.getModelSet().bakeLayer(ModelLayers.PLAYER), false) {
         public void setupAnim(SpiritEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            this.leftLeg.xRot = 0.0F;
            this.leftPants.xRot = 0.0F;
            this.rightLeg.xRot = 0.0F;
            this.rightPants.xRot = 0.0F;
            this.leftArm.xRot = 0.0F;
            this.leftSleeve.xRot = 0.0F;
            this.rightArm.xRot = 0.0F;
            this.rightSleeve.xRot = 0.0F;
         }
      };
      steveModel.young = false;
      this.steveSpiritRenderer = new SpiritRenderer.SpiritPlayerRenderer(context, steveModel);
      PlayerModel<SpiritEntity> alexModel = new PlayerModel<SpiritEntity>(context.getModelSet().bakeLayer(ModelLayers.PLAYER_SLIM), true) {
         public void setupAnim(SpiritEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            this.leftLeg.xRot = 0.0F;
            this.leftPants.xRot = 0.0F;
            this.rightLeg.xRot = 0.0F;
            this.rightPants.xRot = 0.0F;
            this.leftArm.xRot = 0.0F;
            this.leftSleeve.xRot = 0.0F;
            this.rightArm.xRot = 0.0F;
            this.rightSleeve.xRot = 0.0F;
         }
      };
      alexModel.young = false;
      this.alexSpiritRenderer = new SpiritRenderer.SpiritPlayerRenderer(context, alexModel);
   }

   private static boolean playerCarriesSpirit(Player player) {
      List<Entity> passengers = player.getPassengers();
      return !passengers.isEmpty() && passengers.get(0) instanceof SpiritEntity;
   }

   public static void handleStaticHandHoldingSpirit(Player player, ModelPart leftArm) {
      if (playerCarriesSpirit(player)) {
         leftArm.xRot = (float) Math.PI;
      }
   }

   public void render(SpiritEntity spirit, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
      if (spirit.hasSlimSkin()) {
         this.alexSpiritRenderer.render(spirit, entityYaw, partialTicks, poseStack, buffer, packedLight);
      } else {
         this.steveSpiritRenderer.render(spirit, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }

   public ResourceLocation getTextureLocation(SpiritEntity entity) {
      return DefaultPlayerSkin.getDefaultSkin();
   }

   private static class SpiritPlayerRenderer extends LivingEntityRenderer<SpiritEntity, PlayerModel<SpiritEntity>> {
      private PlayerSkinUpdater playerSkinUpdater = new PlayerSkinUpdater();

      public SpiritPlayerRenderer(Context context, PlayerModel<SpiritEntity> model) {
         super(context, model, 0.5F);
      }

      public ResourceLocation getTextureLocation(SpiritEntity spirit) {
         return spirit.getSkinLocation()
            .orElseGet(() -> spirit.getGameProfile().map(gp -> this.playerSkinUpdater.updatePlayerSkin(spirit, gp)).orElse(DefaultPlayerSkin.getDefaultSkin()));
      }

      protected void scale(SpiritEntity entity, PoseStack poseStack, float partialTickTime) {
         poseStack.scale(0.9375F, 0.9375F, 0.9375F);
      }

      protected boolean shouldShowName(SpiritEntity entity) {
         return false;
      }

      public void render(SpiritEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
         poseStack.pushPose();
         float yBodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
         float yHeadRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
         float netHeadYaw = yHeadRot - yBodyRot;
         float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
         float bob = this.getBob(entity, partialTicks);
         this.setupRotations(entity, poseStack, bob, yBodyRot, partialTicks);
         float yTranslation = -1.501F;
         float zTranslation = 0.0F;
         if (entity.getPose() == Pose.SLEEPING) {
            float angle;
            if (entity.getVehicle() != null && entity.getVehicle() instanceof LivingEntity vehicle) {
               angle = (vehicle.yHeadRot - vehicle.yHeadRotO) * partialTicks + vehicle.yHeadRotO;
            } else {
               Vec3 movement = entity.getDeltaMovement();
               angle = (float)(Mth.atan2(movement.z(), movement.x()) * 180.0 / Math.PI - 90.0);
            }

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(angle - 90.0F));
            yTranslation = -0.5F;
            zTranslation = -0.7F;
         }

         poseStack.scale(-1.0F, -1.0F, 1.0F);
         this.scale(entity, poseStack, partialTicks);
         poseStack.translate(0.0, yTranslation, zTranslation);
         float animationSpeed = 0.0F;
         float animationPosition = 0.0F;
         if (entity.isAlive()) {
            animationSpeed = Mth.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
            animationPosition = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);
            if (animationSpeed > 1.0F) {
               animationSpeed = 1.0F;
            }
         }

         ((PlayerModel)this.model).prepareMobModel(entity, animationPosition, animationSpeed, partialTicks);
         ((PlayerModel)this.model).setupAnim(entity, animationPosition, animationSpeed, bob, netHeadYaw, xRot);
         RenderType rendertype = this.getRenderType(entity, true, true, false);
         if (rendertype != null) {
            VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
            int i = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
            ((PlayerModel)this.model).renderToBuffer(poseStack, vertexconsumer, packedLight, i, 0.5F, 0.5F, 0.5F, 0.5F);
         }

         for (RenderLayer<SpiritEntity, PlayerModel<SpiritEntity>> renderlayer : this.layers) {
            renderlayer.render(poseStack, buffer, packedLight, entity, animationPosition, animationSpeed, partialTicks, bob, netHeadYaw, xRot);
         }

         poseStack.popPose();
      }
   }
}
