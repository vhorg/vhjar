package iskallia.vault.block.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class WendarrChainModel extends Model {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(VaultMod.id("wendarr_chain"), "main");
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("block/wendarr_chains");
   public static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, TEXTURE_LOCATION);
   private final ModelPart chain;

   public WendarrChainModel(ModelPart root) {
      super(RenderType::entityTranslucent);
      this.chain = root.getChild("chain");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition chain = partdefinition.addOrReplaceChild("chain", CubeListBuilder.create(), PartPose.offset(-15.4587F, 17.126F, 8.1537F));
      PartDefinition cube_r1 = chain.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-0.7685F, -15.1514F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.7685F, -17.1514F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(1.2315F, -16.1514F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-2.7685F, -16.1514F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-2.7685F, -16.1514F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-2.7685F, -16.1514F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 1.5708F)
      );
      PartDefinition cube_r2 = chain.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(13.8259F, -9.4157F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(13.8259F, -10.4157F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(13.8259F, -8.4157F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r3 = chain.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(7.1865F, -14.9109F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(7.1865F, -14.1609F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, -1.1781F, 0.0F)
      );
      PartDefinition cube_r4 = chain.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-15.1488F, -9.3043F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-17.1488F, -9.3043F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-17.1488F, -9.3043F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, -0.7854F, -3.1416F)
      );
      PartDefinition cube_r5 = chain.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-10.5737F, -15.2007F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-10.5737F, -13.2007F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, -1.1781F, -3.1416F)
      );
      PartDefinition cube_r6 = chain.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(15.2815F, 1.2986F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(15.2815F, -0.7014F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(17.2815F, 0.2986F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(13.2815F, 0.2986F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(13.2815F, 0.2986F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(13.2815F, 0.2986F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.4587F, 0.0F, 6.8707F, 1.5708F, 0.0F, 0.0F)
      );
      PartDefinition cube_r7 = chain.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(13.5431F, 13.5652F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(13.5431F, 12.5652F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(13.5431F, 14.5652F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.4587F, 0.0F, 6.8707F, 1.5708F, 0.7854F, 0.0F)
      );
      PartDefinition cube_r8 = chain.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(15.7196F, 6.429F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(15.7196F, 7.179F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.4587F, 0.0F, 6.8707F, 1.5708F, 0.3927F, 0.0F)
      );
      PartDefinition cube_r9 = chain.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(7.8321F, -9.0215F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(5.8321F, -9.0215F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(5.8321F, -9.0215F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.4587F, 0.0F, 6.8707F, 1.5708F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r10 = chain.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(10.5497F, -6.145F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(10.5497F, -4.145F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.4587F, 0.0F, 6.8707F, 1.5708F, -0.3927F, 0.0F)
      );
      PartDefinition cube_r11 = chain.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-1.1685F, 17.3486F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-1.1685F, 15.3486F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(0.8315F, 16.3486F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-3.1685F, 16.3486F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-3.1685F, 16.3486F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-3.1685F, 16.3486F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(14.3294F, 0.0F, -0.588F, 0.0F, 1.5708F, -1.5708F)
      );
      PartDefinition cube_r12 = chain.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-9.4379F, 13.2824F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-9.4379F, 12.2824F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-9.4379F, 14.2824F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(14.3294F, 0.0F, -0.588F, -1.5708F, 0.7854F, -3.1416F)
      );
      PartDefinition cube_r13 = chain.addOrReplaceChild(
         "cube_r13",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-5.6202F, 14.9621F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-5.6202F, 15.7121F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(14.3294F, 0.0F, -0.588F, -1.5708F, 1.1781F, -3.1416F)
      );
      PartDefinition cube_r14 = chain.addOrReplaceChild(
         "cube_r14",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(7.5493F, 13.9595F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(5.5493F, 13.9595F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(5.5493F, 13.9595F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(14.3294F, 0.0F, -0.588F, 1.5708F, 0.7854F, 0.0F)
      );
      PartDefinition cube_r15 = chain.addOrReplaceChild(
         "cube_r15",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(1.494F, 14.9784F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(1.494F, 16.9784F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(14.3294F, 0.0F, -0.588F, 1.5708F, 1.1781F, 0.0F)
      );
      PartDefinition cube_r16 = chain.addOrReplaceChild(
         "cube_r16",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-17.2185F, 0.8986F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-17.2185F, -1.1014F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-15.2185F, -0.1014F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-19.2185F, -0.1014F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-19.2185F, -0.1014F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-19.2185F, -0.1014F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.8707F, 0.0F, -7.4587F, -1.5708F, 0.0F, -3.1416F)
      );
      PartDefinition cube_r17 = chain.addOrReplaceChild(
         "cube_r17",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-9.155F, -9.6986F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-9.155F, -10.6986F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-9.155F, -8.6986F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.8707F, 0.0F, -7.4587F, -1.5708F, -0.7854F, -3.1416F)
      );
      PartDefinition cube_r18 = chain.addOrReplaceChild(
         "cube_r18",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-14.1534F, -6.3778F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-14.1534F, -5.6278F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.8707F, 0.0F, -7.4587F, -1.5708F, -0.3927F, -3.1416F)
      );
      PartDefinition cube_r19 = chain.addOrReplaceChild(
         "cube_r19",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-15.4317F, 13.6766F, 8.5143F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-17.4317F, 13.6766F, 7.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-17.4317F, 13.6766F, 9.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.8707F, 0.0F, -7.4587F, -1.5708F, 0.7854F, -3.1416F)
      );
      PartDefinition cube_r20 = chain.addOrReplaceChild(
         "cube_r20",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-19.6294F, 5.9227F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-19.6294F, 7.9227F, 8.5143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.8707F, 0.0F, -7.4587F, -1.5708F, 0.3927F, -3.1416F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void renderToBuffer(
      PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha
   ) {
      this.chain.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
   }
}
