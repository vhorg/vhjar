package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.client.render.DynamicHumanoidModelLayer;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.entity.model.FighterModel;
import iskallia.vault.init.ModEntities;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;

public class FighterRenderer extends LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>> {
   private final DynamicHumanoidModelLayer<LivingEntity, HumanoidModel<LivingEntity>, HumanoidModel<LivingEntity>> armorLayer;
   private final HumanoidModel<LivingEntity> thisModel;
   private final PlayerModel playerModel;
   private final PlayerModel playerSlimModel;

   public FighterRenderer(Context context, ModelLayerLocation modelLayer) {
      this(context, modelLayer, false);
   }

   public FighterRenderer(Context context, ModelLayerLocation modelLayer, boolean slim) {
      super(context, new FighterModel(context.bakeLayer(modelLayer), slim), 0.5F);
      this.addLayer(
         this.armorLayer = new DynamicHumanoidModelLayer<>(
            this,
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)),
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR))
         )
      );
      this.addLayer(new CustomHeadLayer(this, context.getModelSet()));
      this.addLayer(new ElytraLayer(this, context.getModelSet()));
      this.addLayer(new ItemInHandLayer(this));
      this.thisModel = (HumanoidModel<LivingEntity>)this.model;
      this.playerModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
      this.playerSlimModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.armorLayer.setSlim(slim);
   }

   protected void scale(LivingEntity entity, PoseStack matrixStack, float partialTickTime) {
      if (entity instanceof FighterEntity fighter) {
         float sizeMultiplier = fighter.sizeMultiplier;
         matrixStack.scale(sizeMultiplier, sizeMultiplier, sizeMultiplier);
      }
   }

   protected boolean shouldShowName(LivingEntity entity) {
      return entity.isCustomNameVisible() && super.shouldShowName(entity);
   }

   public void render(LivingEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn) {
      if (entity instanceof FighterEntity fighterEntity) {
         this.setModelVisibilities(fighterEntity);
         if (fighterEntity.hasSkin()) {
            if (fighterEntity.isSlimSkin()) {
               this.model = this.playerSlimModel;
            } else {
               this.model = this.playerModel;
            }
         } else {
            this.model = this.thisModel;
         }

         this.armorLayer.setSlim(fighterEntity.hasSkin() && fighterEntity.isSlimSkin());
         super.render(fighterEntity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
      }
   }

   public Vec3 getRenderOffset(FighterEntity entityIn, float partialTicks) {
      return entityIn.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(entityIn, partialTicks);
   }

   private void setModelVisibilities(LivingEntity entity) {
      if (entity instanceof FighterEntity fighterEntity) {
         if (fighterEntity.hasSkin()) {
            if (fighterEntity.isSlimSkin()) {
               setVisibilities(this.playerSlimModel, fighterEntity);
            } else {
               setVisibilities(this.playerModel, fighterEntity);
            }
         } else {
            HumanoidModel model = (HumanoidModel)this.getModel();
            if (model instanceof FighterModel fighterModel) {
               if (fighterEntity.isSpectator()) {
                  fighterModel.setAllVisible(false);
                  fighterModel.head.visible = true;
                  fighterModel.hat.visible = true;
               } else {
                  fighterModel.setAllVisible(true);
                  fighterModel.crouching = fighterEntity.isCrouching();
                  ArmPose bipedmodel$armpose = getArmPose(fighterEntity, InteractionHand.MAIN_HAND);
                  ArmPose bipedmodel$armpose1 = getArmPose(fighterEntity, InteractionHand.OFF_HAND);
                  if (bipedmodel$armpose.isTwoHanded()) {
                     bipedmodel$armpose1 = fighterEntity.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
                  }

                  if (fighterEntity.getMainArm() == HumanoidArm.RIGHT) {
                     fighterModel.rightArmPose = bipedmodel$armpose;
                     fighterModel.leftArmPose = bipedmodel$armpose1;
                  } else {
                     fighterModel.rightArmPose = bipedmodel$armpose1;
                     fighterModel.leftArmPose = bipedmodel$armpose;
                  }
               }
            }
         }
      }
   }

   private static void setVisibilities(PlayerModel<?> playerModel, LivingEntity entity) {
      playerModel.setAllVisible(true);
      playerModel.hat.visible = true;
      playerModel.jacket.visible = true;
      playerModel.leftPants.visible = true;
      playerModel.rightPants.visible = true;
      playerModel.leftSleeve.visible = true;
      playerModel.rightSleeve.visible = true;
      playerModel.crouching = entity.isCrouching();
      ArmPose mainArmPose = getArmPose(entity, InteractionHand.MAIN_HAND);
      ArmPose offHandPose = getArmPose(entity, InteractionHand.OFF_HAND);
      if (mainArmPose.isTwoHanded()) {
         offHandPose = entity.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
      }

      if (entity.getMainArm() == HumanoidArm.RIGHT) {
         playerModel.rightArmPose = mainArmPose;
         playerModel.leftArmPose = offHandPose;
      } else {
         playerModel.rightArmPose = offHandPose;
         playerModel.leftArmPose = mainArmPose;
      }
   }

   private static ArmPose getArmPose(LivingEntity entity, InteractionHand hand) {
      ItemStack itemstack = entity.getItemInHand(hand);
      if (itemstack.isEmpty()) {
         return ArmPose.EMPTY;
      } else {
         if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
            UseAnim useaction = itemstack.getUseAnimation();
            if (useaction == UseAnim.BLOCK) {
               return ArmPose.BLOCK;
            }

            if (useaction == UseAnim.BOW) {
               return ArmPose.BOW_AND_ARROW;
            }

            if (useaction == UseAnim.SPEAR) {
               return ArmPose.THROW_SPEAR;
            }

            if (useaction == UseAnim.CROSSBOW && hand == entity.getUsedItemHand()) {
               return ArmPose.CROSSBOW_CHARGE;
            }
         } else if (!entity.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
            return ArmPose.CROSSBOW_HOLD;
         }

         return ArmPose.ITEM;
      }
   }

   public ResourceLocation getTextureLocation(LivingEntity entity) {
      if (entity instanceof FighterEntity fighterEntity) {
         if (fighterEntity.hasSkin()) {
            return fighterEntity.getLocationSkin();
         } else {
            for (int i = 0; i < ModEntities.VAULT_FIGHTER_TYPES.size(); i++) {
               if (fighterEntity.getType().equals(ModEntities.VAULT_FIGHTER_TYPES.get(i))) {
                  return VaultMod.id("textures/entity/fighter/vaultfightert" + i + ".png");
               }
            }

            return fighterEntity.getLocationSkin();
         }
      } else {
         return MissingTextureAtlasSprite.getLocation();
      }
   }

   protected void setupRotations(LivingEntity entity, PoseStack matrixStack, float age, float yaw, float pTicks) {
      float f = entity.getSwimAmount(pTicks);
      if (entity.isFallFlying()) {
         super.setupRotations(entity, matrixStack, age, yaw, pTicks);
         float f1 = entity.getFallFlyingTicks() + pTicks;
         float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!entity.isAutoSpinAttack()) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entity.getXRot())));
         }

         Vec3 vec3 = entity.getViewVector(pTicks);
         Vec3 vec31 = entity.getDeltaMovement();
         double d0 = vec31.horizontalDistanceSqr();
         double d1 = vec3.horizontalDistanceSqr();
         if (d0 > 0.0 && d1 > 0.0) {
            double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
            double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
            matrixStack.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
         }
      } else if (f > 0.0F) {
         super.setupRotations(entity, matrixStack, age, yaw, pTicks);
         float f3 = entity.isInWater() ? -90.0F - entity.getXRot() : -90.0F;
         float f4 = Mth.lerp(f, 0.0F, f3);
         matrixStack.mulPose(Vector3f.XP.rotationDegrees(f4));
         if (entity.isVisuallySwimming()) {
            matrixStack.translate(0.0, -1.0, 0.3F);
         }
      } else {
         super.setupRotations(entity, matrixStack, age, yaw, pTicks);
      }
   }
}
