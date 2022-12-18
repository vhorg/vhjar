package iskallia.vault.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OrcArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? OrcArmorLayers.LeggingsLayer::createBodyLayer : OrcArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? OrcArmorLayers.LeggingsLayer::new : OrcArmorLayers.MainLayer::new;
   }

   public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
      public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(82, 22)
               .addBox(-1.0F, 9.4318F, -4.3418F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(79, 20)
               .addBox(-0.5F, 10.9318F, -4.0918F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(80, 26)
               .addBox(-1.5F, 8.75F, 2.1F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(80, 26)
               .addBox(-1.5F, 8.75F, -3.1F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(51, 22)
               .addBox(-5.5F, 10.5F, -3.5F, 11.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(42, 18)
               .mirror()
               .addBox(-5.75F, 13.25F, -3.5F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(42, 18)
               .addBox(4.75F, 13.25F, -3.5F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(30, 19)
               .mirror()
               .addBox(-5.5F, 16.5F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(30, 19)
               .addBox(4.5F, 16.5F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(30, 26)
               .mirror()
               .addBox(-6.0F, 11.25F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(30, 26)
               .addBox(5.0F, 11.25F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(42, 32)
               .addBox(-5.0F, 10.55F, -3.0F, 10.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body_r1 = body.addOrReplaceChild(
            "body_r1",
            CubeListBuilder.create().texOffs(87, 20).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.8827F, 10.6057F, -3.3418F, 0.6545F, 0.0F, -1.1781F)
         );
         PartDefinition body_r2 = body.addOrReplaceChild(
            "body_r2",
            CubeListBuilder.create().texOffs(83, 20).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.7691F, 11.6253F, -3.1418F, -0.7854F, 0.0F, -2.0508F)
         );
         PartDefinition body_r3 = body.addOrReplaceChild(
            "body_r3",
            CubeListBuilder.create().texOffs(87, 20).mirror().addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(1.8827F, 10.6057F, -3.3418F, 1.0472F, 0.0F, 1.1781F)
         );
         PartDefinition body_r4 = body.addOrReplaceChild(
            "body_r4",
            CubeListBuilder.create().texOffs(83, 20).mirror().addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(1.7691F, 11.6253F, -3.1418F, -0.7854F, 0.0F, 2.0508F)
         );
         PartDefinition body_r5 = body.addOrReplaceChild(
            "body_r5",
            CubeListBuilder.create()
               .texOffs(27, 38)
               .mirror()
               .addBox(-4.4598F, -16.3746F, 1.91F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(27, 38)
               .mirror()
               .addBox(-4.4598F, -16.3746F, -2.89F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(-0.5983F, 25.6438F, 0.0F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition body_r6 = body.addOrReplaceChild(
            "body_r6",
            CubeListBuilder.create()
               .texOffs(27, 38)
               .addBox(-0.5402F, -16.3746F, 1.91F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(27, 38)
               .addBox(-0.5402F, -16.3746F, -2.89F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5983F, 25.6438F, 0.0F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition body_r7 = body.addOrReplaceChild(
            "body_r7",
            CubeListBuilder.create().texOffs(83, 18).addBox(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.1818F, -3.5918F, -0.7854F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(8, 52).addBox(-3.0F, -1.0F, -2.99F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(8, 64).mirror().addBox(-2.0F, -1.0F, -2.99F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }

   public static class MainLayer extends ArmorLayers.MainLayer {
      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-5.0F, -8.5F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(5.0F, -5.5F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(-5.5F, -7.5F, -4.0F, 11.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(6.0F, -5.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .mirror()
               .addBox(-8.0F, -5.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 0)
               .mirror()
               .addBox(-6.0F, -5.5F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 34)
               .addBox(-4.5F, -9.0F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition head_r1 = head.addOrReplaceChild(
            "head_r1",
            CubeListBuilder.create().texOffs(0, 5).mirror().addBox(4.0134F, -32.9954F, -0.9737F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.479F, -10.0239F, 32.3807F, 1.5708F, 0.0F, 0.5672F)
         );
         PartDefinition head_r2 = head.addOrReplaceChild(
            "head_r2",
            CubeListBuilder.create().texOffs(0, 5).addBox(-5.0134F, -32.9954F, -0.9737F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.479F, -10.0239F, 32.3807F, 1.5708F, 0.0F, -0.5672F)
         );
         PartDefinition head_r3 = head.addOrReplaceChild(
            "head_r3",
            CubeListBuilder.create().texOffs(0, 52).mirror().addBox(-6.0203F, -37.0685F, -1.8647F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-0.8478F, -11.3626F, 32.4538F, 1.5708F, 0.0F, -0.5672F)
         );
         PartDefinition head_r4 = head.addOrReplaceChild(
            "head_r4",
            CubeListBuilder.create().texOffs(0, 52).addBox(4.0203F, -37.0685F, -1.8647F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.8478F, -11.3626F, 32.4538F, 1.5708F, 0.0F, 0.5672F)
         );
         PartDefinition head_r5 = head.addOrReplaceChild(
            "head_r5",
            CubeListBuilder.create()
               .texOffs(0, 5)
               .addBox(-0.4797F, -66.2518F, 33.3391F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 52)
               .addBox(-0.9797F, -70.2518F, 33.3391F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 25.7704F, 65.6372F, 1.5708F, 0.0F, 0.0F)
         );
         PartDefinition head_r6 = head.addOrReplaceChild(
            "head_r6",
            CubeListBuilder.create().texOffs(66, 19).mirror().addBox(18.5F, -68.1F, -25.5042F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(44.5448F, -52.7719F, -34.4268F, -0.7516F, -0.05F, -2.7519F)
         );
         PartDefinition head_r7 = head.addOrReplaceChild(
            "head_r7",
            CubeListBuilder.create().texOffs(70, 16).mirror().addBox(18.4348F, -68.7203F, -23.7542F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(44.6796F, -53.3485F, -34.6649F, -0.7516F, -0.05F, -2.7519F)
         );
         PartDefinition head_r8 = head.addOrReplaceChild(
            "head_r8",
            CubeListBuilder.create().texOffs(70, 16).addBox(-19.4348F, -68.7203F, -23.7542F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-44.6796F, -53.3485F, -34.6649F, -0.7516F, 0.05F, 2.7519F)
         );
         PartDefinition head_r9 = head.addOrReplaceChild(
            "head_r9",
            CubeListBuilder.create().texOffs(66, 19).addBox(-19.5F, -68.1F, -25.5042F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-44.5448F, -52.7719F, -34.4268F, -0.7516F, 0.05F, 2.7519F)
         );
         PartDefinition head_r10 = head.addOrReplaceChild(
            "head_r10",
            CubeListBuilder.create().texOffs(0, 5).mirror().addBox(4.0134F, -29.6506F, -0.3767F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-8.2275F, 3.6434F, 28.0509F, 1.0472F, 0.0F, 0.5672F)
         );
         PartDefinition head_r11 = head.addOrReplaceChild(
            "head_r11",
            CubeListBuilder.create().texOffs(0, 5).addBox(-5.0134F, -29.6506F, -0.3767F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(8.2275F, 3.6434F, 28.0509F, 1.0472F, 0.0F, -0.5672F)
         );
         PartDefinition head_r12 = head.addOrReplaceChild(
            "head_r12",
            CubeListBuilder.create().texOffs(0, 5).addBox(-0.4797F, -46.3451F, 28.6826F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 41.1759F, 27.9791F, 1.0472F, 0.0F, 0.0F)
         );
         PartDefinition head_r13 = head.addOrReplaceChild(
            "head_r13",
            CubeListBuilder.create().texOffs(0, 5).mirror().addBox(4.0134F, -30.9518F, -0.9378F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-3.29F, -4.1074F, 31.6174F, 1.3526F, 0.0F, 0.5672F)
         );
         PartDefinition head_r14 = head.addOrReplaceChild(
            "head_r14",
            CubeListBuilder.create().texOffs(0, 5).addBox(-5.0134F, -30.9518F, -0.9378F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.29F, -4.1074F, 31.6174F, 1.3526F, 0.0F, -0.5672F)
         );
         PartDefinition head_r15 = head.addOrReplaceChild(
            "head_r15",
            CubeListBuilder.create().texOffs(0, 5).addBox(-0.4797F, -57.0386F, 32.2157F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 36.4871F, 49.9089F, 1.3526F, 0.0F, 0.0F)
         );
         PartDefinition head_r16 = head.addOrReplaceChild(
            "head_r16",
            CubeListBuilder.create().texOffs(0, 5).mirror().addBox(4.0134F, -30.9965F, -3.0981F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-3.314F, -4.0697F, -31.6532F, -1.3526F, 0.0F, 0.5672F)
         );
         PartDefinition head_r17 = head.addOrReplaceChild(
            "head_r17",
            CubeListBuilder.create().texOffs(0, 5).addBox(-5.0134F, -30.9965F, -3.0981F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.314F, -4.0697F, -31.6532F, -1.3526F, 0.0F, -0.5672F)
         );
         PartDefinition head_r18 = head.addOrReplaceChild(
            "head_r18",
            CubeListBuilder.create().texOffs(0, 5).addBox(-0.4797F, -57.0386F, -36.2157F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 36.4871F, -49.9089F, -1.3526F, 0.0F, 0.0F)
         );
         PartDefinition head_r19 = head.addOrReplaceChild(
            "head_r19",
            CubeListBuilder.create().texOffs(0, 5).mirror().addBox(4.0134F, -29.6903F, -3.6462F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-8.2488F, 3.6769F, -28.0738F, -1.0472F, 0.0F, 0.5672F)
         );
         PartDefinition head_r20 = head.addOrReplaceChild(
            "head_r20",
            CubeListBuilder.create().texOffs(0, 5).addBox(-5.0134F, -29.6903F, -3.6462F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(8.2488F, 3.6769F, -28.0738F, -1.0472F, 0.0F, -0.5672F)
         );
         PartDefinition head_r21 = head.addOrReplaceChild(
            "head_r21",
            CubeListBuilder.create().texOffs(0, 5).addBox(-0.4797F, -46.3451F, -32.6826F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 41.1759F, -27.9791F, -1.0472F, 0.0F, 0.0F)
         );
         PartDefinition head_r22 = head.addOrReplaceChild(
            "head_r22",
            CubeListBuilder.create().texOffs(30, 6).addBox(7.7707F, -49.2099F, 35.6748F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(24.7329F, 39.4247F, 36.6226F, 1.2859F, -0.274F, -0.7459F)
         );
         PartDefinition head_r23 = head.addOrReplaceChild(
            "head_r23",
            CubeListBuilder.create().texOffs(30, 6).addBox(-5.1585F, -55.743F, 38.3807F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(25.7884F, 35.2455F, 45.4248F, 1.2859F, -0.274F, -0.7459F)
         );
         PartDefinition head_r24 = head.addOrReplaceChild(
            "head_r24",
            CubeListBuilder.create().texOffs(36, 0).addBox(-5.0F, -50.7347F, 37.4193F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 44.3905F, 36.3222F, 1.1781F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(42, 32)
               .addBox(-5.0F, 9.55F, -3.0F, 10.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(42, 32)
               .addBox(-5.0F, 10.55F, -3.0F, 10.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body_r1 = body.addOrReplaceChild(
            "body_r1",
            CubeListBuilder.create()
               .texOffs(84, 0)
               .addBox(7.4905F, -22.0095F, 1.95F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(84, 0)
               .addBox(7.4905F, -22.0095F, -2.95F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.731F, 30.731F, 0.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition body_r2 = body.addOrReplaceChild(
            "body_r2",
            CubeListBuilder.create()
               .texOffs(78, 3)
               .addBox(-13.6013F, -22.5631F, 1.96F, 11.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(78, 3)
               .addBox(-13.6013F, -22.5631F, -2.96F, 11.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.005F, 26.4263F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition body_r3 = body.addOrReplaceChild(
            "body_r3",
            CubeListBuilder.create().texOffs(69, 0).addBox(6.7962F, -19.5216F, -2.3981F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, 24.0F, -9.7962F, 0.0F, -1.5708F, 0.0F)
         );
         PartDefinition body_r4 = body.addOrReplaceChild(
            "body_r4",
            CubeListBuilder.create().texOffs(56, 3).addBox(-1.5F, -1.5F, 0.25F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.5F, 4.6818F, -3.3418F, 0.0F, 0.0F, -1.1781F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(102, 34)
               .addBox(-5.5F, -3.5F, -3.5F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(106, 22)
               .addBox(-4.5F, -2.5F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(88, 41)
               .addBox(-4.0F, 4.75F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(100, 47)
               .mirror()
               .addBox(-5.94F, -4.14F, -3.99F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_arm_r1 = right_arm.addOrReplaceChild(
            "right_arm_r1",
            CubeListBuilder.create().texOffs(86, 14).addBox(-3.0F, -0.5F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.7954F, -0.2228F, 0.0F, 0.0F, 0.0F, 0.6109F)
         );
         PartDefinition right_arm_r2 = right_arm.addOrReplaceChild(
            "right_arm_r2",
            CubeListBuilder.create().texOffs(86, 14).addBox(-0.25F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.844F, 0.2475F, 0.0F, 3.1416F, 0.0F, -0.2618F)
         );
         PartDefinition right_arm_r3 = right_arm.addOrReplaceChild(
            "right_arm_r3",
            CubeListBuilder.create()
               .texOffs(83, 14)
               .addBox(-30.9989F, -47.229F, -31.6171F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(83, 14)
               .addBox(-30.7489F, -46.979F, -31.8671F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-37.3208F, 38.0125F, -33.796F, -1.0768F, 0.1771F, 1.1228F)
         );
         PartDefinition right_arm_r4 = right_arm.addOrReplaceChild(
            "right_arm_r4",
            CubeListBuilder.create()
               .texOffs(83, 14)
               .addBox(-30.9989F, -47.229F, 29.6171F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(83, 14)
               .addBox(-30.7489F, -46.979F, 29.8671F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-37.3208F, 38.0125F, 33.796F, 1.0768F, -0.1771F, 1.1228F)
         );
         PartDefinition right_arm_r5 = right_arm.addOrReplaceChild(
            "right_arm_r5",
            CubeListBuilder.create().texOffs(86, 12).addBox(-26.6698F, -75.9607F, 8.2542F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-33.7486F, -73.7767F, 24.7004F, 0.6926F, 0.3885F, -3.0124F)
         );
         PartDefinition right_arm_r6 = right_arm.addOrReplaceChild(
            "right_arm_r6",
            CubeListBuilder.create().texOffs(86, 12).addBox(-26.6698F, -75.9607F, -9.2542F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-33.7486F, -73.7767F, -24.7004F, -0.6926F, -0.3885F, -3.0124F)
         );
         PartDefinition right_arm_r7 = right_arm.addOrReplaceChild(
            "right_arm_r7",
            CubeListBuilder.create().texOffs(90, 11).addBox(-43.4479F, -61.1187F, -13.1983F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-76.1129F, -11.5105F, 9.7021F, -0.8177F, -0.9419F, 2.4841F)
         );
         PartDefinition right_arm_r8 = right_arm.addOrReplaceChild(
            "right_arm_r8",
            CubeListBuilder.create().texOffs(90, 11).addBox(-43.4479F, -61.1187F, 11.1983F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-76.1129F, -11.5105F, -9.7021F, 0.8177F, 0.9419F, 2.4841F)
         );
         PartDefinition right_arm_r9 = right_arm.addOrReplaceChild(
            "right_arm_r9",
            CubeListBuilder.create().texOffs(86, 12).addBox(-10.2492F, -24.3911F, -4.6563F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.9759F, -27.1978F, -5.2811F, -2.7296F, -0.8486F, -0.9174F)
         );
         PartDefinition right_arm_r10 = right_arm.addOrReplaceChild(
            "right_arm_r10",
            CubeListBuilder.create().texOffs(90, 11).addBox(-26.1611F, -74.8057F, -10.5888F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-34.115F, -75.5674F, -2.7241F, -2.3707F, -1.1811F, -1.3326F)
         );
         PartDefinition right_arm_r11 = right_arm.addOrReplaceChild(
            "right_arm_r11",
            CubeListBuilder.create().texOffs(86, 12).addBox(-10.2492F, -24.3911F, 3.6563F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.9759F, -27.1978F, 5.2811F, 2.7296F, 0.8486F, -0.9174F)
         );
         PartDefinition right_arm_r12 = right_arm.addOrReplaceChild(
            "right_arm_r12",
            CubeListBuilder.create().texOffs(90, 11).addBox(-26.1611F, -74.8057F, 8.5888F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-34.115F, -75.5674F, 2.7241F, 2.3707F, 1.1811F, -1.3326F)
         );
         PartDefinition right_arm_r13 = right_arm.addOrReplaceChild(
            "right_arm_r13",
            CubeListBuilder.create().texOffs(90, 11).addBox(-33.6902F, -72.1786F, -7.1379F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-59.3749F, -54.8325F, 16.362F, -1.3384F, -1.3947F, -2.7044F)
         );
         PartDefinition right_arm_r14 = right_arm.addOrReplaceChild(
            "right_arm_r14",
            CubeListBuilder.create().texOffs(90, 11).addBox(-33.6902F, -72.1786F, 5.1379F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-59.3749F, -54.8325F, -16.362F, 1.3384F, 1.3947F, -2.7044F)
         );
         PartDefinition right_arm_r15 = right_arm.addOrReplaceChild(
            "right_arm_r15",
            CubeListBuilder.create().texOffs(90, 11).addBox(-38.1292F, -51.7845F, -24.735F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-64.6298F, 19.6053F, -14.987F, -0.8986F, -0.3292F, 1.8908F)
         );
         PartDefinition right_arm_r16 = right_arm.addOrReplaceChild(
            "right_arm_r16",
            CubeListBuilder.create().texOffs(90, 11).addBox(-38.1292F, -51.7845F, 22.735F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-64.6298F, 19.6053F, 14.987F, 0.8986F, 0.3292F, 1.8908F)
         );
         PartDefinition right_arm_r17 = right_arm.addOrReplaceChild(
            "right_arm_r17",
            CubeListBuilder.create().texOffs(88, 7).addBox(-25.1525F, -38.3442F, -27.0579F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.8389F, 41.7815F, -22.9519F, -0.9228F, 0.3331F, 0.4321F)
         );
         PartDefinition right_arm_r18 = right_arm.addOrReplaceChild(
            "right_arm_r18",
            CubeListBuilder.create().texOffs(88, 7).addBox(-25.1525F, -38.3442F, 25.0579F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.8389F, 41.7815F, 22.9519F, 0.9228F, -0.3331F, 0.4321F)
         );
         PartDefinition right_arm_r19 = right_arm.addOrReplaceChild(
            "right_arm_r19",
            CubeListBuilder.create()
               .texOffs(59, 3)
               .addBox(-31.9479F, -27.9652F, -2.5F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(77, 6)
               .addBox(-32.4479F, -27.4652F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(45, 3)
               .addBox(-31.5979F, -28.4652F, -2.0F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.6266F, 36.3886F, 0.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_arm_r20 = right_arm.addOrReplaceChild(
            "right_arm_r20",
            CubeListBuilder.create()
               .texOffs(104, 53)
               .addBox(-0.5F, -0.5F, 1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(104, 53)
               .addBox(-0.5F, -0.5F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(104, 53)
               .addBox(-0.5F, -0.5F, 5.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.25F, 10.75F, -4.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(29, 116)
               .mirror()
               .addBox(-2.522F, 8.66F, -3.54F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(33, 94)
               .mirror()
               .addBox(-1.9307F, 9.7F, -4.5718F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(34, 99)
               .mirror()
               .addBox(-1.4307F, 10.575F, -5.0718F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(29, 103)
               .mirror()
               .addBox(-2.0F, 5.5F, -2.99F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg_r1 = left_leg.addOrReplaceChild(
            "left_leg_r1",
            CubeListBuilder.create().texOffs(40, 109).addBox(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.6193F, 6.075F, -3.0718F, -0.9553F, 0.5236F, -0.6155F)
         );
         PartDefinition left_leg_r2 = left_leg.addOrReplaceChild(
            "left_leg_r2",
            CubeListBuilder.create().texOffs(40, 109).mirror().addBox(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(1.5693F, 6.075F, -3.0718F, -0.9553F, -0.5236F, 0.6155F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(29, 116)
               .addBox(-3.478F, 8.66F, -3.54F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(33, 94)
               .addBox(-3.0693F, 9.7F, -4.5718F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(34, 99)
               .addBox(-2.5693F, 10.575F, -5.0718F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(29, 103)
               .addBox(-3.0F, 5.5F, -2.99F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg_r1 = right_leg.addOrReplaceChild(
            "right_leg_r1",
            CubeListBuilder.create().texOffs(40, 109).mirror().addBox(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.6193F, 6.075F, -3.0718F, -0.9553F, -0.5236F, 0.6155F)
         );
         PartDefinition right_leg_r2 = right_leg.addOrReplaceChild(
            "right_leg_r2",
            CubeListBuilder.create().texOffs(40, 109).addBox(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5693F, 6.075F, -3.0718F, -0.9553F, 0.5236F, -0.6155F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
