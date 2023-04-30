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
   public static final ModelLayerLocation STRONGBOX_LOCATION = new ModelLayerLocation(VaultMod.id("strongbox_chest"), "main");
   public static final ModelLayerLocation LIVING_STRONGBOX_LOCATION = new ModelLayerLocation(VaultMod.id("living_strongbox_chest"), "main");
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

   public static LayerDefinition createLivingStrongboxlayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(-5.5766F, 14.3536F, 8.2853F));
      PartDefinition lid = chest.addOrReplaceChild(
         "lid",
         CubeListBuilder.create()
            .texOffs(55, 73)
            .addBox(7.0F, -2.92F, 14.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(54, 67)
            .addBox(7.5F, -3.92F, 14.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.5766F, -5.3536F, -7.2853F)
      );
      PartDefinition cube_r1 = lid.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(101, 106).addBox(4.5864F, -3.7168F, -1.6872F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(5.6478F, 6.8173F, 7.0F, -0.1572F, -0.3614F, 0.4215F)
      );
      PartDefinition cube_r2 = lid.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(101, 106).addBox(4.4399F, -3.6562F, -4.5432F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(5.6478F, 6.8173F, 7.0F, 0.1572F, 0.3614F, 0.4215F)
      );
      PartDefinition cube_r3 = lid.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(101, 100).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(8.5F, 1.8992F, 16.4685F, 1.5326F, 0.7094F, 1.5326F)
      );
      PartDefinition cube_r4 = lid.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(101, 100).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(8.5F, 1.8992F, -2.4685F, -1.5326F, -0.7094F, 1.5326F)
      );
      PartDefinition cube_r5 = lid.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(101, 100).mirror().addBox(-4.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(17.3114F, 2.6492F, 7.0429F, -4.0E-4F, -0.0189F, -0.9479F)
      );
      PartDefinition cube_r6 = lid.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(101, 100).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.3114F, 2.5492F, 7.0429F, -4.0E-4F, 0.0189F, 0.9479F)
      );
      PartDefinition cube_r7 = lid.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create().texOffs(101, 100).addBox(-11.3277F, -3.1622F, -4.9543F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(9.8978F, 6.8173F, 7.0F, 0.1572F, -0.3614F, -0.4215F)
      );
      PartDefinition cube_r8 = lid.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(101, 100).addBox(-11.4741F, -3.2229F, -1.2762F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(9.8978F, 6.8173F, 7.0F, -0.1572F, 0.3614F, -0.4215F)
      );
      PartDefinition cube_r9 = lid.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(5, 111).addBox(1.5432F, -1.8985F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.8978F, 6.8173F, 7.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r10 = lid.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create().texOffs(40, 114).addBox(-8.4387F, -1.441F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.8978F, 6.8173F, 7.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r11 = lid.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create().texOffs(79, 111).addBox(-3.4022F, -1.6697F, 0.9909F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.8978F, 6.8173F, 7.0F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r12 = lid.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create().texOffs(36, 102).addBox(-3.4022F, -1.6697F, -8.9909F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.8978F, 6.8173F, 7.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r13 = lid.addOrReplaceChild(
         "cube_r13",
         CubeListBuilder.create().texOffs(70, 104).addBox(-1.4022F, -1.6327F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.8978F, 6.8173F, 7.0F, 0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r14 = lid.addOrReplaceChild(
         "cube_r14",
         CubeListBuilder.create()
            .texOffs(76, 10)
            .addBox(-6.5F, -2.3032F, -8.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(76, 16)
            .addBox(2.5F, -2.3032F, -8.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 10)
            .addBox(-6.5F, -2.3032F, 7.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 16)
            .addBox(2.5F, -2.3032F, 7.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 6)
            .addBox(4.75F, -0.0732F, -7.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .addBox(4.75F, -0.0732F, 4.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .mirror()
            .addBox(-8.0F, -2.3232F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 62)
            .addBox(2.5F, -0.3232F, -8.5F, 4.0F, 4.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(42, 48)
            .mirror()
            .addBox(-7.0F, -2.3232F, -7.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(48, 0)
            .addBox(-7.75F, -0.0732F, -7.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(48, 6)
            .addBox(-7.75F, -0.0732F, 4.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(28, 67)
            .addBox(-6.5F, -0.3232F, -8.5F, 4.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(8.5F, 2.3018F, 7.0F, 0.0F, 0.0F, 0.0F)
      );
      PartDefinition upper_chain = lid.addOrReplaceChild("upper_chain", CubeListBuilder.create(), PartPose.offset(-2.8766F, 3.5464F, -9.5353F));
      PartDefinition cube_r15 = upper_chain.addOrReplaceChild(
         "cube_r15",
         CubeListBuilder.create().texOffs(63, 69).addBox(-6.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.3562F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r16 = upper_chain.addOrReplaceChild(
         "cube_r16",
         CubeListBuilder.create().texOffs(63, 68).addBox(-6.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.7854F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r17 = upper_chain.addOrReplaceChild(
         "cube_r17",
         CubeListBuilder.create().texOffs(64, 73).addBox(-4.7471F, 7.8673F, -2.149F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.0713F, 0.274F, -0.4322F)
      );
      PartDefinition cube_r18 = upper_chain.addOrReplaceChild(
         "cube_r18",
         CubeListBuilder.create().texOffs(64, 72).addBox(-4.7471F, 1.649F, 7.3673F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.5005F, 0.274F, -0.4322F)
      );
      PartDefinition cube_r19 = upper_chain.addOrReplaceChild(
         "cube_r19",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(1.7471F, 7.8673F, -2.149F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.0713F, -0.274F, 0.4322F)
      );
      PartDefinition cube_r20 = upper_chain.addOrReplaceChild(
         "cube_r20",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(1.7471F, 1.649F, 7.3673F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.5005F, -0.274F, 0.4322F)
      );
      PartDefinition cube_r21 = upper_chain.addOrReplaceChild(
         "cube_r21",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(2.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.7854F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r22 = upper_chain.addOrReplaceChild(
         "cube_r22",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(2.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.3562F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r23 = upper_chain.addOrReplaceChild(
         "cube_r23",
         CubeListBuilder.create()
            .texOffs(62, 71)
            .addBox(-8.7147F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(62, 70)
            .addBox(3.7853F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 0.0F, -1.5708F, -2.3562F)
      );
      PartDefinition cube_r24 = upper_chain.addOrReplaceChild(
         "cube_r24",
         CubeListBuilder.create()
            .texOffs(62, 70)
            .addBox(-8.7147F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(62, 71)
            .addBox(3.7853F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 0.0F, -1.5708F, -0.7854F)
      );
      PartDefinition cube_r25 = upper_chain.addOrReplaceChild(
         "cube_r25",
         CubeListBuilder.create().texOffs(63, 68).addBox(-3.8408F, -4.7952F, -5.7249F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, -0.7854F, -1.4399F, -1.5708F)
      );
      PartDefinition cube_r26 = upper_chain.addOrReplaceChild(
         "cube_r26",
         CubeListBuilder.create().texOffs(63, 69).addBox(-3.8408F, -5.2249F, 4.2952F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 0.7854F, -1.4399F, -1.5708F)
      );
      PartDefinition cube_r27 = upper_chain.addOrReplaceChild(
         "cube_r27",
         CubeListBuilder.create().texOffs(63, 68).addBox(-0.1027F, -4.7721F, -5.748F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 2.3562F, -1.4835F, 1.5708F)
      );
      PartDefinition cube_r28 = upper_chain.addOrReplaceChild(
         "cube_r28",
         CubeListBuilder.create().texOffs(63, 69).addBox(-0.1027F, -5.248F, 4.2721F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, -2.3562F, -1.4835F, 1.5708F)
      );
      PartDefinition cube_r29 = upper_chain.addOrReplaceChild(
         "cube_r29",
         CubeListBuilder.create()
            .texOffs(62, 71)
            .mirror()
            .addBox(3.7147F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(62, 70)
            .mirror()
            .addBox(-8.7853F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 0.0F, 1.5708F, 2.3562F)
      );
      PartDefinition cube_r30 = upper_chain.addOrReplaceChild(
         "cube_r30",
         CubeListBuilder.create()
            .texOffs(62, 70)
            .mirror()
            .addBox(3.7147F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(62, 71)
            .mirror()
            .addBox(-8.7853F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 0.0F, 1.5708F, 0.7854F)
      );
      PartDefinition cube_r31 = upper_chain.addOrReplaceChild(
         "cube_r31",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-0.1592F, -4.7952F, -5.7249F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, -0.7854F, 1.4399F, 1.5708F)
      );
      PartDefinition cube_r32 = upper_chain.addOrReplaceChild(
         "cube_r32",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-0.1592F, -5.2249F, 4.2952F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 0.7854F, 1.4399F, 1.5708F)
      );
      PartDefinition cube_r33 = upper_chain.addOrReplaceChild(
         "cube_r33",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-3.8973F, -4.7721F, -5.748F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 2.3562F, 1.4835F, -1.5708F)
      );
      PartDefinition cube_r34 = upper_chain.addOrReplaceChild(
         "cube_r34",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-3.8973F, -5.248F, 4.2721F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, -2.3562F, 1.4835F, -1.5708F)
      );
      PartDefinition cube_r35 = upper_chain.addOrReplaceChild(
         "cube_r35",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(2.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 0.7854F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r36 = upper_chain.addOrReplaceChild(
         "cube_r36",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(2.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 2.3562F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r37 = upper_chain.addOrReplaceChild(
         "cube_r37",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(13.3F, -2.9214F, 8.0706F, 0.6621F, 0.0846F, 0.4167F)
      );
      PartDefinition cube_r38 = upper_chain.addOrReplaceChild(
         "cube_r38",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(13.3F, -2.9214F, 8.0706F, 2.2329F, 0.0846F, 0.4167F)
      );
      PartDefinition cube_r39 = upper_chain.addOrReplaceChild(
         "cube_r39",
         CubeListBuilder.create().texOffs(64, 73).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(9.4533F, -2.9214F, 8.0706F, 2.2329F, -0.0846F, -0.4167F)
      );
      PartDefinition cube_r40 = upper_chain.addOrReplaceChild(
         "cube_r40",
         CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(9.4533F, -2.9214F, 8.0706F, 0.6621F, -0.0846F, -0.4167F)
      );
      PartDefinition cube_r41 = upper_chain.addOrReplaceChild(
         "cube_r41",
         CubeListBuilder.create().texOffs(63, 69).addBox(-6.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 2.3562F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r42 = upper_chain.addOrReplaceChild(
         "cube_r42",
         CubeListBuilder.create().texOffs(63, 68).addBox(-6.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 0.7854F, 0.0F, -0.3927F)
      );
      PartDefinition base = chest.addOrReplaceChild(
         "base",
         CubeListBuilder.create()
            .texOffs(50, 5)
            .addBox(16.5766F, -14.3536F, -8.7853F, 4.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(76, 16)
            .addBox(16.5766F, -8.3536F, -8.7853F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(76, 10)
            .addBox(7.5766F, -8.3536F, -8.7853F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 10)
            .addBox(7.5766F, -8.3536F, 7.2147F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 16)
            .addBox(16.5766F, -8.3536F, 7.2147F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(18.8266F, -14.6236F, -7.5353F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(18.8266F, -14.6236F, 3.9647F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .mirror()
            .addBox(6.0766F, -8.3736F, -8.2853F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 38)
            .mirror()
            .addBox(7.0766F, -14.3736F, -7.2853F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(56, 73)
            .addBox(7.5766F, -14.3536F, -8.7853F, 4.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 38)
            .addBox(6.3266F, -14.6236F, 3.9647F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 44)
            .addBox(6.3266F, -14.6236F, -7.5353F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(54, 67)
            .addBox(13.0766F, -9.2736F, -8.7853F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(55, 73)
            .addBox(12.5766F, -8.2736F, -8.7853F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition bottom_chain = base.addOrReplaceChild("bottom_chain", CubeListBuilder.create(), PartPose.offset(-1.948F, 6.5777F, -8.2948F));
      PartDefinition cube_r43 = bottom_chain.addOrReplaceChild(
         "cube_r43",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, 16.2948F, 2.0713F, -0.274F, -2.7093F)
      );
      PartDefinition cube_r44 = bottom_chain.addOrReplaceChild(
         "cube_r44",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, 16.2948F, 0.5005F, -0.274F, -2.7093F)
      );
      PartDefinition cube_r45 = bottom_chain.addOrReplaceChild(
         "cube_r45",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, 16.5448F, 0.7854F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r46 = bottom_chain.addOrReplaceChild(
         "cube_r46",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, 16.5448F, 2.3562F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r47 = bottom_chain.addOrReplaceChild(
         "cube_r47",
         CubeListBuilder.create().texOffs(62, 71).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 2.2948F, 0.0F, 1.5708F, 0.7854F)
      );
      PartDefinition cube_r48 = bottom_chain.addOrReplaceChild(
         "cube_r48",
         CubeListBuilder.create().texOffs(62, 70).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 2.2948F, 0.0F, 1.5708F, 2.3562F)
      );
      PartDefinition cube_r49 = bottom_chain.addOrReplaceChild(
         "cube_r49",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -18.0691F, 6.1448F, 0.7854F, 1.309F, 1.5708F)
      );
      PartDefinition cube_r50 = bottom_chain.addOrReplaceChild(
         "cube_r50",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -18.0691F, 6.1448F, -0.7854F, 1.309F, 1.5708F)
      );
      PartDefinition cube_r51 = bottom_chain.addOrReplaceChild(
         "cube_r51",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-1.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.8691F, 10.8948F, -2.3562F, 1.309F, -1.5708F)
      );
      PartDefinition cube_r52 = bottom_chain.addOrReplaceChild(
         "cube_r52",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-1.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.8691F, 10.8948F, 2.3562F, 1.309F, -1.5708F)
      );
      PartDefinition cube_r53 = bottom_chain.addOrReplaceChild(
         "cube_r53",
         CubeListBuilder.create().texOffs(62, 70).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 14.7948F, 0.0F, 1.5708F, 0.7854F)
      );
      PartDefinition cube_r54 = bottom_chain.addOrReplaceChild(
         "cube_r54",
         CubeListBuilder.create().texOffs(62, 71).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 14.7948F, 0.0F, 1.5708F, 2.3562F)
      );
      PartDefinition cube_r55 = bottom_chain.addOrReplaceChild(
         "cube_r55",
         CubeListBuilder.create().texOffs(62, 71).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 2.2948F, 0.0F, -1.5708F, -0.7854F)
      );
      PartDefinition cube_r56 = bottom_chain.addOrReplaceChild(
         "cube_r56",
         CubeListBuilder.create().texOffs(62, 70).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 2.2948F, 0.0F, -1.5708F, -2.3562F)
      );
      PartDefinition cube_r57 = bottom_chain.addOrReplaceChild(
         "cube_r57",
         CubeListBuilder.create().texOffs(63, 69).addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -18.0691F, 6.1448F, -0.7854F, -1.309F, -1.5708F)
      );
      PartDefinition cube_r58 = bottom_chain.addOrReplaceChild(
         "cube_r58",
         CubeListBuilder.create().texOffs(63, 68).addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -18.0691F, 6.1448F, 0.7854F, -1.309F, -1.5708F)
      );
      PartDefinition cube_r59 = bottom_chain.addOrReplaceChild(
         "cube_r59",
         CubeListBuilder.create().texOffs(63, 68).addBox(-3.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.8691F, 10.8948F, -2.3562F, -1.309F, 1.5708F)
      );
      PartDefinition cube_r60 = bottom_chain.addOrReplaceChild(
         "cube_r60",
         CubeListBuilder.create().texOffs(63, 69).addBox(-3.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.8691F, 10.8948F, 2.3562F, -1.309F, 1.5708F)
      );
      PartDefinition cube_r61 = bottom_chain.addOrReplaceChild(
         "cube_r61",
         CubeListBuilder.create().texOffs(62, 70).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 14.7948F, 0.0F, -1.5708F, -0.7854F)
      );
      PartDefinition cube_r62 = bottom_chain.addOrReplaceChild(
         "cube_r62",
         CubeListBuilder.create().texOffs(62, 71).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 14.7948F, 0.0F, -1.5708F, -2.3562F)
      );
      PartDefinition cube_r63 = bottom_chain.addOrReplaceChild(
         "cube_r63",
         CubeListBuilder.create().texOffs(63, 68).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, 16.5448F, 0.7854F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r64 = bottom_chain.addOrReplaceChild(
         "cube_r64",
         CubeListBuilder.create().texOffs(63, 69).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, 16.5448F, 2.3562F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r65 = bottom_chain.addOrReplaceChild(
         "cube_r65",
         CubeListBuilder.create().texOffs(64, 73).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, -0.4552F, 2.2329F, -0.0846F, 2.7249F)
      );
      PartDefinition cube_r66 = bottom_chain.addOrReplaceChild(
         "cube_r66",
         CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, -0.4552F, 0.6621F, -0.0846F, 2.7249F)
      );
      PartDefinition cube_r67 = bottom_chain.addOrReplaceChild(
         "cube_r67",
         CubeListBuilder.create().texOffs(63, 69).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, -0.7052F, 2.3562F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r68 = bottom_chain.addOrReplaceChild(
         "cube_r68",
         CubeListBuilder.create().texOffs(63, 68).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, -0.7052F, 0.7854F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r69 = bottom_chain.addOrReplaceChild(
         "cube_r69",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, -0.4552F, 2.2329F, 0.0846F, -2.7249F)
      );
      PartDefinition cube_r70 = bottom_chain.addOrReplaceChild(
         "cube_r70",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, -0.4552F, 0.6621F, 0.0846F, -2.7249F)
      );
      PartDefinition cube_r71 = bottom_chain.addOrReplaceChild(
         "cube_r71",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, -0.7052F, 2.3562F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r72 = bottom_chain.addOrReplaceChild(
         "cube_r72",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, -0.7052F, 0.7854F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r73 = bottom_chain.addOrReplaceChild(
         "cube_r73",
         CubeListBuilder.create().texOffs(64, 73).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, 16.2948F, 2.0713F, 0.274F, 2.7093F)
      );
      PartDefinition cube_r74 = bottom_chain.addOrReplaceChild(
         "cube_r74",
         CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, 16.2948F, 0.5005F, 0.274F, 2.7093F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public static LayerDefinition createStrongboxlayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(-5.5766F, 14.3536F, 8.2853F));
      PartDefinition lid = chest.addOrReplaceChild(
         "lid",
         CubeListBuilder.create()
            .texOffs(55, 73)
            .addBox(7.0F, -2.92F, 14.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(54, 67)
            .addBox(7.5F, -3.92F, 14.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.5766F, -5.3536F, -7.2853F)
      );
      PartDefinition cube_r1 = lid.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(76, 10)
            .addBox(-6.5F, -2.3032F, -8.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(76, 16)
            .addBox(2.5F, -2.3032F, -8.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 10)
            .addBox(-6.5F, -2.3032F, 7.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 16)
            .addBox(2.5F, -2.3032F, 7.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 6)
            .addBox(4.75F, -0.0732F, -7.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .addBox(4.75F, -0.0732F, 4.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .mirror()
            .addBox(-8.0F, -2.3232F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 62)
            .addBox(2.5F, -0.3232F, -8.5F, 4.0F, 4.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(42, 48)
            .mirror()
            .addBox(-7.0F, -2.3232F, -7.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(48, 0)
            .addBox(-7.75F, -0.0732F, -7.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(48, 6)
            .addBox(-7.75F, -0.0732F, 4.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(28, 67)
            .addBox(-6.5F, -0.3232F, -8.5F, 4.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(8.5F, 2.3018F, 7.0F, 0.0F, 0.0F, 0.0F)
      );
      PartDefinition upper_chain = lid.addOrReplaceChild("upper_chain", CubeListBuilder.create(), PartPose.offset(-2.8766F, 3.5464F, -9.5353F));
      PartDefinition cube_r2 = upper_chain.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(63, 69).addBox(-6.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.3562F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r3 = upper_chain.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(63, 68).addBox(-6.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.7854F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r4 = upper_chain.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(64, 73).addBox(-4.7471F, 7.8673F, -2.149F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.0713F, 0.274F, -0.4322F)
      );
      PartDefinition cube_r5 = upper_chain.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(64, 72).addBox(-4.7471F, 1.649F, 7.3673F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.5005F, 0.274F, -0.4322F)
      );
      PartDefinition cube_r6 = upper_chain.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(1.7471F, 7.8673F, -2.149F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.0713F, -0.274F, 0.4322F)
      );
      PartDefinition cube_r7 = upper_chain.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(1.7471F, 1.649F, 7.3673F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.5005F, -0.274F, 0.4322F)
      );
      PartDefinition cube_r8 = upper_chain.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(2.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 0.7854F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r9 = upper_chain.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(2.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, 16.5353F, 2.3562F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r10 = upper_chain.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create()
            .texOffs(62, 71)
            .addBox(-8.7147F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(62, 70)
            .addBox(3.7853F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 0.0F, -1.5708F, -2.3562F)
      );
      PartDefinition cube_r11 = upper_chain.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create()
            .texOffs(62, 70)
            .addBox(-8.7147F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(62, 71)
            .addBox(3.7853F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 0.0F, -1.5708F, -0.7854F)
      );
      PartDefinition cube_r12 = upper_chain.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create().texOffs(63, 68).addBox(-3.8408F, -4.7952F, -5.7249F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, -0.7854F, -1.4399F, -1.5708F)
      );
      PartDefinition cube_r13 = upper_chain.addOrReplaceChild(
         "cube_r13",
         CubeListBuilder.create().texOffs(63, 69).addBox(-3.8408F, -5.2249F, 4.2952F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 0.7854F, -1.4399F, -1.5708F)
      );
      PartDefinition cube_r14 = upper_chain.addOrReplaceChild(
         "cube_r14",
         CubeListBuilder.create().texOffs(63, 68).addBox(-0.1027F, -4.7721F, -5.748F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, 2.3562F, -1.4835F, 1.5708F)
      );
      PartDefinition cube_r15 = upper_chain.addOrReplaceChild(
         "cube_r15",
         CubeListBuilder.create().texOffs(63, 69).addBox(-0.1027F, -5.248F, 4.2721F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.4766F, -1.0447F, 16.5353F, -2.3562F, -1.4835F, 1.5708F)
      );
      PartDefinition cube_r16 = upper_chain.addOrReplaceChild(
         "cube_r16",
         CubeListBuilder.create()
            .texOffs(62, 71)
            .mirror()
            .addBox(3.7147F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(62, 70)
            .mirror()
            .addBox(-8.7853F, -5.1437F, -5.3764F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 0.0F, 1.5708F, 2.3562F)
      );
      PartDefinition cube_r17 = upper_chain.addOrReplaceChild(
         "cube_r17",
         CubeListBuilder.create()
            .texOffs(62, 70)
            .mirror()
            .addBox(3.7147F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(62, 71)
            .mirror()
            .addBox(-8.7853F, -4.8764F, 4.6437F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 0.0F, 1.5708F, 0.7854F)
      );
      PartDefinition cube_r18 = upper_chain.addOrReplaceChild(
         "cube_r18",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-0.1592F, -4.7952F, -5.7249F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, -0.7854F, 1.4399F, 1.5708F)
      );
      PartDefinition cube_r19 = upper_chain.addOrReplaceChild(
         "cube_r19",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-0.1592F, -5.2249F, 4.2952F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 0.7854F, 1.4399F, 1.5708F)
      );
      PartDefinition cube_r20 = upper_chain.addOrReplaceChild(
         "cube_r20",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-3.8973F, -4.7721F, -5.748F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, 2.3562F, 1.4835F, -1.5708F)
      );
      PartDefinition cube_r21 = upper_chain.addOrReplaceChild(
         "cube_r21",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-3.8973F, -5.248F, 4.2721F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.2766F, -1.0447F, 16.5353F, -2.3562F, 1.4835F, -1.5708F)
      );
      PartDefinition cube_r22 = upper_chain.addOrReplaceChild(
         "cube_r22",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(2.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 0.7854F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r23 = upper_chain.addOrReplaceChild(
         "cube_r23",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(2.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 2.3562F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r24 = upper_chain.addOrReplaceChild(
         "cube_r24",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(13.3F, -2.9214F, 8.0706F, 0.6621F, 0.0846F, 0.4167F)
      );
      PartDefinition cube_r25 = upper_chain.addOrReplaceChild(
         "cube_r25",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(13.3F, -2.9214F, 8.0706F, 2.2329F, 0.0846F, 0.4167F)
      );
      PartDefinition cube_r26 = upper_chain.addOrReplaceChild(
         "cube_r26",
         CubeListBuilder.create().texOffs(64, 73).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(9.4533F, -2.9214F, 8.0706F, 2.2329F, -0.0846F, -0.4167F)
      );
      PartDefinition cube_r27 = upper_chain.addOrReplaceChild(
         "cube_r27",
         CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(9.4533F, -2.9214F, 8.0706F, 0.6621F, -0.0846F, -0.4167F)
      );
      PartDefinition cube_r28 = upper_chain.addOrReplaceChild(
         "cube_r28",
         CubeListBuilder.create().texOffs(63, 69).addBox(-6.5124F, 7.7286F, -4.8422F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 2.3562F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r29 = upper_chain.addOrReplaceChild(
         "cube_r29",
         CubeListBuilder.create().texOffs(63, 68).addBox(-6.5124F, 4.3422F, 7.2286F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(11.3766F, -1.2447F, -0.7147F, 0.7854F, 0.0F, -0.3927F)
      );
      PartDefinition base = chest.addOrReplaceChild(
         "base",
         CubeListBuilder.create()
            .texOffs(50, 5)
            .addBox(16.5766F, -14.3536F, -8.7853F, 4.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(76, 16)
            .addBox(16.5766F, -8.3536F, -8.7853F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(76, 10)
            .addBox(7.5766F, -8.3536F, -8.7853F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 10)
            .addBox(7.5766F, -8.3536F, 7.2147F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(89, 16)
            .addBox(16.5766F, -8.3536F, 7.2147F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(18.8266F, -14.6236F, -7.5353F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(18.8266F, -14.6236F, 3.9647F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .mirror()
            .addBox(6.0766F, -8.3736F, -8.2853F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 38)
            .mirror()
            .addBox(7.0766F, -14.3736F, -7.2853F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(56, 73)
            .addBox(7.5766F, -14.3536F, -8.7853F, 4.0F, 6.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 38)
            .addBox(6.3266F, -14.6236F, 3.9647F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 44)
            .addBox(6.3266F, -14.6236F, -7.5353F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(54, 67)
            .addBox(13.0766F, -9.2736F, -8.7853F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(55, 73)
            .addBox(12.5766F, -8.2736F, -8.7853F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition bottom_chain = base.addOrReplaceChild("bottom_chain", CubeListBuilder.create(), PartPose.offset(-1.948F, 6.5777F, -8.2948F));
      PartDefinition cube_r30 = bottom_chain.addOrReplaceChild(
         "cube_r30",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, 16.2948F, 2.0713F, -0.274F, -2.7093F)
      );
      PartDefinition cube_r31 = bottom_chain.addOrReplaceChild(
         "cube_r31",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, 16.2948F, 0.5005F, -0.274F, -2.7093F)
      );
      PartDefinition cube_r32 = bottom_chain.addOrReplaceChild(
         "cube_r32",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, 16.5448F, 0.7854F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r33 = bottom_chain.addOrReplaceChild(
         "cube_r33",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, 16.5448F, 2.3562F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r34 = bottom_chain.addOrReplaceChild(
         "cube_r34",
         CubeListBuilder.create().texOffs(62, 71).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 2.2948F, 0.0F, 1.5708F, 0.7854F)
      );
      PartDefinition cube_r35 = bottom_chain.addOrReplaceChild(
         "cube_r35",
         CubeListBuilder.create().texOffs(62, 70).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 2.2948F, 0.0F, 1.5708F, 2.3562F)
      );
      PartDefinition cube_r36 = bottom_chain.addOrReplaceChild(
         "cube_r36",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -18.0691F, 6.1448F, 0.7854F, 1.309F, 1.5708F)
      );
      PartDefinition cube_r37 = bottom_chain.addOrReplaceChild(
         "cube_r37",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -18.0691F, 6.1448F, -0.7854F, 1.309F, 1.5708F)
      );
      PartDefinition cube_r38 = bottom_chain.addOrReplaceChild(
         "cube_r38",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-1.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.8691F, 10.8948F, -2.3562F, 1.309F, -1.5708F)
      );
      PartDefinition cube_r39 = bottom_chain.addOrReplaceChild(
         "cube_r39",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-1.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.8691F, 10.8948F, 2.3562F, 1.309F, -1.5708F)
      );
      PartDefinition cube_r40 = bottom_chain.addOrReplaceChild(
         "cube_r40",
         CubeListBuilder.create().texOffs(62, 70).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 14.7948F, 0.0F, 1.5708F, 0.7854F)
      );
      PartDefinition cube_r41 = bottom_chain.addOrReplaceChild(
         "cube_r41",
         CubeListBuilder.create().texOffs(62, 71).mirror().addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.8894F, -17.5691F, 14.7948F, 0.0F, 1.5708F, 2.3562F)
      );
      PartDefinition cube_r42 = bottom_chain.addOrReplaceChild(
         "cube_r42",
         CubeListBuilder.create().texOffs(62, 71).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 2.2948F, 0.0F, -1.5708F, -0.7854F)
      );
      PartDefinition cube_r43 = bottom_chain.addOrReplaceChild(
         "cube_r43",
         CubeListBuilder.create().texOffs(62, 70).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 2.2948F, 0.0F, -1.5708F, -2.3562F)
      );
      PartDefinition cube_r44 = bottom_chain.addOrReplaceChild(
         "cube_r44",
         CubeListBuilder.create().texOffs(63, 69).addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -18.0691F, 6.1448F, -0.7854F, -1.309F, -1.5708F)
      );
      PartDefinition cube_r45 = bottom_chain.addOrReplaceChild(
         "cube_r45",
         CubeListBuilder.create().texOffs(63, 68).addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -18.0691F, 6.1448F, 0.7854F, -1.309F, -1.5708F)
      );
      PartDefinition cube_r46 = bottom_chain.addOrReplaceChild(
         "cube_r46",
         CubeListBuilder.create().texOffs(63, 68).addBox(-3.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.8691F, 10.8948F, -2.3562F, -1.309F, 1.5708F)
      );
      PartDefinition cube_r47 = bottom_chain.addOrReplaceChild(
         "cube_r47",
         CubeListBuilder.create().texOffs(63, 69).addBox(-3.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.8691F, 10.8948F, 2.3562F, -1.309F, 1.5708F)
      );
      PartDefinition cube_r48 = bottom_chain.addOrReplaceChild(
         "cube_r48",
         CubeListBuilder.create().texOffs(62, 70).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 14.7948F, 0.0F, -1.5708F, -0.7854F)
      );
      PartDefinition cube_r49 = bottom_chain.addOrReplaceChild(
         "cube_r49",
         CubeListBuilder.create().texOffs(62, 71).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(23.16F, -17.5691F, 14.7948F, 0.0F, -1.5708F, -2.3562F)
      );
      PartDefinition cube_r50 = bottom_chain.addOrReplaceChild(
         "cube_r50",
         CubeListBuilder.create().texOffs(63, 68).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, 16.5448F, 0.7854F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r51 = bottom_chain.addOrReplaceChild(
         "cube_r51",
         CubeListBuilder.create().texOffs(63, 69).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, 16.5448F, 2.3562F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r52 = bottom_chain.addOrReplaceChild(
         "cube_r52",
         CubeListBuilder.create().texOffs(64, 73).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, -0.4552F, 2.2329F, -0.0846F, 2.7249F)
      );
      PartDefinition cube_r53 = bottom_chain.addOrReplaceChild(
         "cube_r53",
         CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, -0.4552F, 0.6621F, -0.0846F, 2.7249F)
      );
      PartDefinition cube_r54 = bottom_chain.addOrReplaceChild(
         "cube_r54",
         CubeListBuilder.create().texOffs(63, 69).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, -0.7052F, 2.3562F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r55 = bottom_chain.addOrReplaceChild(
         "cube_r55",
         CubeListBuilder.create().texOffs(63, 68).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(20.648F, -16.8277F, -0.7052F, 0.7854F, 0.0F, 2.7489F)
      );
      PartDefinition cube_r56 = bottom_chain.addOrReplaceChild(
         "cube_r56",
         CubeListBuilder.create().texOffs(64, 73).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, -0.4552F, 2.2329F, 0.0846F, -2.7249F)
      );
      PartDefinition cube_r57 = bottom_chain.addOrReplaceChild(
         "cube_r57",
         CubeListBuilder.create().texOffs(64, 72).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(14.1013F, -15.8277F, -0.4552F, 0.6621F, 0.0846F, -2.7249F)
      );
      PartDefinition cube_r58 = bottom_chain.addOrReplaceChild(
         "cube_r58",
         CubeListBuilder.create().texOffs(63, 69).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, -0.7052F, 2.3562F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r59 = bottom_chain.addOrReplaceChild(
         "cube_r59",
         CubeListBuilder.create().texOffs(63, 68).mirror().addBox(-1.5F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(11.4013F, -16.8277F, -0.7052F, 0.7854F, 0.0F, -2.7489F)
      );
      PartDefinition cube_r60 = bottom_chain.addOrReplaceChild(
         "cube_r60",
         CubeListBuilder.create().texOffs(64, 73).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, 16.2948F, 2.0713F, 0.274F, 2.7093F)
      );
      PartDefinition cube_r61 = bottom_chain.addOrReplaceChild(
         "cube_r61",
         CubeListBuilder.create().texOffs(64, 72).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(17.948F, -15.8277F, 16.2948F, 0.5005F, 0.274F, 2.7093F)
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
