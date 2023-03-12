package iskallia.vault.entity.model.tier3;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.monster.Creeper;

public class Tier3CreeperModel extends CreeperModel<Creeper> {
   public Tier3CreeperModel(ModelPart pRoot) {
      super(pRoot);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -18.0F, -7.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 28)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-3.0F, -13.0F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 6.0F, 0.0F)
      );
      PartDefinition right_hind_leg = partdefinition.addOrReplaceChild(
         "right_hind_leg",
         CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 18.0F, 4.0F)
      );
      PartDefinition left_hind_leg = partdefinition.addOrReplaceChild(
         "left_hind_leg",
         CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 18.0F, 4.0F)
      );
      PartDefinition right_front_leg = partdefinition.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 18.0F, -4.0F)
      );
      PartDefinition left_front_leg = partdefinition.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 18.0F, -4.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
