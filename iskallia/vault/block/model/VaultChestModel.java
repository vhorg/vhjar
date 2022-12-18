package iskallia.vault.block.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class VaultChestModel extends Model {
   public static final ModelLayerLocation TREASURE_LOCATION = new ModelLayerLocation(VaultMod.id("treasure_chest"), "main");
   public static final ModelLayerLocation MOSSY_LOCATION = new ModelLayerLocation(VaultMod.id("mossy_chest"), "main");
   public static final ModelLayerLocation SCAVENGER_LOCATION = new ModelLayerLocation(VaultMod.id("scavanger_chest"), "main");
   public static final ModelLayerLocation PRESENT_LOCATION = new ModelLayerLocation(VaultMod.id("present_chest"), "main");
   private final ModelPart chest;
   private final ModelPart base;
   private final ModelPart lid;

   public VaultChestModel(ModelPart root) {
      super(RenderType::entityCutoutNoCull);
      this.chest = root.getChild("chest");
      this.lid = this.chest.getChild("lid");
      this.base = this.chest.getChild("base");
   }

   public static LayerDefinition createScavangerLayer() {
      return createTreasureLayer();
   }

   public static LayerDefinition createTreasureLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition base = chest.addOrReplaceChild(
         "base",
         CubeListBuilder.create()
            .texOffs(48, 4)
            .addBox(4.5F, 0.0F, 0.0F, 7.0F, 6.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 38)
            .addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(0.0F, 6.0F, 0.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(0.75F, -0.25F, 0.75F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .addBox(0.75F, -0.25F, 12.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(42, 110)
            .addBox(12.25F, -0.25F, 12.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(105, 112)
            .addBox(12.25F, -0.25F, 0.75F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition lid = chest.addOrReplaceChild(
         "lid",
         CubeListBuilder.create()
            .texOffs(0, 20)
            .addBox(0.0F, 0.0F, -1.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(42, 38)
            .addBox(7.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 62)
            .addBox(3.0F, 2.0F, -1.0F, 4.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(48, 26)
            .addBox(9.0F, 2.0F, -1.0F, 4.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 44)
            .addBox(0.75F, 2.25F, 11.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 38)
            .addBox(0.75F, 2.25F, -0.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(42, 48)
            .addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(0, 6)
            .addBox(12.25F, 2.25F, 11.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(12.25F, 2.25F, -0.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 9.0F, 1.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public static LayerDefinition createMossylayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition lid = chest.addOrReplaceChild(
         "lid",
         CubeListBuilder.create()
            .texOffs(0, 20)
            .addBox(0.0F, 0.0F, -1.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(42, 38)
            .addBox(7.0F, -2.0F, 15.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 62)
            .addBox(9.5F, 2.0F, -1.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .addBox(12.25F, 2.25F, 11.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 6)
            .addBox(12.25F, 2.25F, -0.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(42, 48)
            .addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(48, 6)
            .addBox(0.75F, 2.25F, 11.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(48, 0)
            .addBox(0.75F, 2.25F, -0.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(24, 67)
            .addBox(2.5F, 2.0F, -1.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(74, 86)
            .addBox(6.0F, 5.0F, 5.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 9.0F, 1.0F)
      );
      PartDefinition cube_r1 = lid.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(36, 102).addBox(-4.0F, 2.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(8.0F, 6.0F, 1.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = lid.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(5, 111).addBox(-4.0F, 2.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(14.0F, 6.0F, 7.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r3 = lid.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(79, 111).addBox(-4.0F, 6.0F, 4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(8.0F, -1.0F, 7.0F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r4 = lid.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(40, 114).addBox(-4.0F, 2.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.0F, 6.0F, 7.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition base = chest.addOrReplaceChild(
         "base",
         CubeListBuilder.create()
            .texOffs(0, 38)
            .addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(0.0F, 6.0F, 0.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 44)
            .addBox(0.75F, -0.25F, 0.75F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 38)
            .addBox(0.75F, -0.25F, 12.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(12.25F, -0.25F, 12.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(12.25F, -0.25F, 0.75F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(48, 26)
            .addBox(2.5F, 0.0F, 0.0F, 4.0F, 6.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(48, 4)
            .addBox(9.5F, 0.0F, 0.0F, 4.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public static LayerDefinition createPresentLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition chest = partdefinition.addOrReplaceChild(
         "chest", CubeListBuilder.create(), PartPose.offsetAndRotation(8.0F, 0.0F, 8.0F, 0.0F, 0.0F, 3.1416F)
      );
      PartDefinition lid = chest.addOrReplaceChild(
         "lid",
         CubeListBuilder.create()
            .texOffs(0, 21)
            .addBox(-6.5F, -0.8438F, -0.5F, 13.0F, 3.0F, 13.0F, new CubeDeformation(0.0F))
            .texOffs(40, 23)
            .addBox(6.0F, -0.5938F, 5.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(42, 24)
            .addBox(-7.0F, -0.5938F, 5.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(36, 0)
            .addBox(-7.0F, -1.5938F, 5.0F, 14.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(44, 25)
            .addBox(-1.0F, -0.5938F, 12.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(42, 24)
            .addBox(-1.0F, -0.5938F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(42, 25)
            .addBox(-1.0F, -1.5938F, 7.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(41, 24)
            .addBox(-1.0F, -1.5938F, -1.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -11.1563F, -6.0F)
      );
      PartDefinition bow = lid.addOrReplaceChild(
         "bow",
         CubeListBuilder.create()
            .texOffs(2, 2)
            .addBox(-2.0F, -13.75F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(2, 2)
            .addBox(-5.0F, -13.75F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(2, 2)
            .addBox(-4.0F, -14.75F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(3, 24)
            .addBox(-1.0F, -13.75F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(3, 24)
            .addBox(-1.0F, -13.75F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(3, 24)
            .addBox(-1.0F, -14.75F, -4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 30)
            .addBox(-1.0F, -14.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(2, 4)
            .addBox(1.0F, -13.75F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(2, 4)
            .addBox(4.0F, -13.75F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(2, 4)
            .addBox(2.0F, -14.75F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(1, 8)
            .addBox(-1.0F, -13.75F, 1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(1, 8)
            .addBox(-1.0F, -13.75F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(1, 8)
            .addBox(-1.0F, -14.75F, 2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 11.1563F, 6.0F)
      );
      PartDefinition base = chest.addOrReplaceChild(
         "base",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-6.0F, -9.0F, -6.0F, 12.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(40, 22)
            .addBox(-1.0F, -9.0F, -6.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(42, 22)
            .addBox(5.5F, -9.0F, -1.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(4, 22)
            .addBox(-1.0F, -9.0F, 5.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(40, 21)
            .addBox(-6.5F, -9.0F, -1.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition bone = base.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setLidAngle(float lidAngle) {
      this.lid.xRot = -(lidAngle * (float) (Math.PI / 2));
   }

   public void renderToBuffer(
      @Nonnull PoseStack poseStack, @Nonnull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.chest.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
