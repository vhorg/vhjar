package iskallia.vault.entity.model;

import iskallia.vault.client.gui.helper.Easing;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.entity.entity.SwampZombieEntity;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.IScalablePart;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SwampZombieModel extends ZombieModel<SwampZombieEntity> {
   protected ModelPart shroom;

   public SwampZombieModel(ModelPart root) {
      super(root);
      this.shroom = root.getChild("head").getChild("shroom");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, -1)
            .addBox(-3.0F, 7.0F, -5.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(-3.0F, 7.0F, -6.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(1.0F, 7.0F, -6.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, -1)
            .addBox(2.0F, 7.0F, -5.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(1.0F, 9.0F, -5.75F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, -1)
            .addBox(2.0F, 9.0F, -4.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(-3.0F, 9.0F, -5.75F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, -1)
            .addBox(-3.0F, 9.0F, -4.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(1.0F, 7.0F, -6.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, -1)
            .addBox(2.0F, 7.0F, -5.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(-3.0F, 7.0F, -6.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, -1)
            .addBox(-3.0F, 7.0F, -5.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(1.0F, 5.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, -1)
            .addBox(-3.0F, 5.0F, -6.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(2, 1)
            .addBox(-3.0F, 5.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, -1)
            .addBox(2.0F, 5.0F, -6.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition Body_r1 = body.addOrReplaceChild(
         "Body_r1",
         CubeListBuilder.create().texOffs(32, 18).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 7.0F, -2.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));
      PartDefinition Head_r1 = head.addOrReplaceChild(
         "Head_r1",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(0, 18)
            .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-2.0F, -2.0F, -6.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition shroom = head.addOrReplaceChild(
         "shroom",
         CubeListBuilder.create()
            .texOffs(36, 15)
            .addBox(-3.0F, -1.0F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(50, 10)
            .addBox(0.0F, -4.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(50, 0)
            .addBox(-2.0F, -3.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(46, 34)
            .addBox(-2.0F, -5.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(3.0F, -5.0F, -6.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(18, 34)
            .addBox(-2.0F, 1.0F, -5.0F, 3.0F, 15.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(32, 49)
            .addBox(-3.0F, 5.0F, -6.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(50, 6)
            .addBox(-2.5F, 7.0F, -5.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(46, 41)
            .addBox(-1.0F, 1.0F, -5.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 34)
            .addBox(-1.5F, 0.0F, -5.5F, 4.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(32, 34).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(36, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(@NotNull SwampZombieEntity entity, float pLimbSwing, float pLimbSwingAmount, float ageInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch);
      this.rightArm.setRotation(0.0F, 0.0F, 0.0F);
      this.leftArm.setRotation(0.0F, 0.0F, 0.0F);
      this.hat.visible = false;
      Level level = entity.level;
      float range = 2.0F;
      List<Player> nearbyPlayers = EntityHelper.getNearby(level, entity.blockPosition(), range, Player.class);
      int now = (int)ClientScheduler.INSTANCE.getTick();
      float shroomScale = nearbyPlayers.size() == 0 ? 1.0F : 0.75F + Easing.EASE_IN_OUT_SINE.calc(now / 5.0F) * 0.4F;
      ((IScalablePart)this.shroom).getScale().set(1.0F, shroomScale, 1.0F);
      if ((now - entity.prevPuffTick) % 10L == 0L) {
         entity.prevPuffTick = now;
         if (nearbyPlayers.size() > 0) {
            double dist = 1.25;
            Vec3 dir = new Vec3(this.shroom.x / 16.0F, -this.shroom.y / 16.0F, -this.shroom.z / 16.0F)
               .add(0.0, 0.5, 0.0)
               .xRot(-this.head.xRot)
               .yRot(this.head.yRot)
               .yRot(-((float)Math.toRadians(entity.getYRot())))
               .normalize()
               .multiply(dist, dist, dist);
            Vec3 offset = entity.getEyePosition(Minecraft.getInstance().getDeltaFrameTime());
            Vec3 pos = offset.add(dir);
            int particleCount = 15;

            for (int i = 0; i < particleCount; i++) {
               int particleColor = 10725632;
               float red = (particleColor >> 16 & 0xFF) / 255.0F;
               float green = (particleColor >> 8 & 0xFF) / 255.0F;
               float blue = (particleColor & 0xFF) / 255.0F;
               entity.level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.x, pos.y + entity.getRandom().nextFloat() * 0.15F - 0.25, pos.z, red, green, blue);
            }
         }
      }
   }
}
