package iskallia.vault.entity.model.tier2;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.monster.Creeper;

public class Tier2CreeperModel extends CreeperModel<Creeper> {
   public Tier2CreeperModel(ModelPart pRoot) {
      super(pRoot);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(44, 3)
            .addBox(-3.0F, -13.0F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 6.0F, 0.0F)
      );
      PartDefinition right_hind_leg = partdefinition.addOrReplaceChild(
         "right_hind_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 18.0F, 4.0F)
      );
      PartDefinition left_hind_leg = partdefinition.addOrReplaceChild(
         "left_hind_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 18.0F, 4.0F)
      );
      PartDefinition right_front_leg = partdefinition.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 18.0F, -4.0F)
      );
      PartDefinition left_front_leg = partdefinition.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 18.0F, -4.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -18.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 32);
   }
}
