package iskallia.vault.entity.model.miner_zombie;

import iskallia.vault.entity.entity.miner_zombie.Tier5MinerZombieEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier5MinerZombieModel extends ZombieModel<Tier5MinerZombieEntity> {
   public Tier5MinerZombieModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 13)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-5.0F, -7.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(25, 22)
            .addBox(-4.0F, -11.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.05F))
            .texOffs(50, 17)
            .addBox(-2.75F, -12.0F, -5.0F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.05F))
            .texOffs(25, 13)
            .addBox(-2.75F, -12.0F, -5.0F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(42, 35).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(25, 35).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 30)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(47, 52)
            .addBox(-3.5F, 9.0F, -4.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(36, 52)
            .addBox(0.5F, 9.0F, -4.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(17, 52)
            .addBox(-3.0F, 1.0F, 2.0F, 6.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 47).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition pouch_r1 = left_leg.addOrReplaceChild(
         "pouch_r1",
         CubeListBuilder.create().texOffs(36, 52).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, 2.0F, 0.0F, 0.0F, -1.5708F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(41, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition pouch_r2 = right_leg.addOrReplaceChild(
         "pouch_r2",
         CubeListBuilder.create().texOffs(36, 52).mirror().addBox(-1.5F, -2.0F, 0.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-4.0F, 2.0F, 0.0F, 0.0F, 1.5708F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
