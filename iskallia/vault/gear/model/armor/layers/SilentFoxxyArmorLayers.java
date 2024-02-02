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

public class SilentFoxxyArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SilentFoxxyArmorLayers.LeggingsLayer::createBodyLayer : SilentFoxxyArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SilentFoxxyArmorLayers.LeggingsLayer::new : SilentFoxxyArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
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
               .texOffs(0, 17)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(19, 32)
               .addBox(3.5F, 11.0F, -3.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(18, 0)
               .addBox(-2.0F, 10.0F, -3.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(-3.75F, -4.5F, -3.5F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, 14.5F, 0.5F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(24, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MainLayer extends ArmorLayers.MainLayer {
      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(56, 32)
               .addBox(-2.0F, -0.75F, 4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 38)
               .addBox(-3.0F, -1.75F, 4.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 25)
               .addBox(1.0F, -0.75F, 4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 44)
               .addBox(-1.0F, -2.75F, 4.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 28)
               .addBox(2.0F, -1.75F, 4.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 35)
               .addBox(1.0F, -0.5F, 2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 38)
               .addBox(-2.0F, -0.5F, 2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 38)
               .addBox(2.0F, -1.5F, 2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 44)
               .addBox(-3.0F, -1.5F, 2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(14, 54)
               .addBox(-2.0F, 0.5F, 0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 41)
               .addBox(-0.5F, -3.5F, 0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 54)
               .addBox(1.0F, 0.5F, 0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(22, 54)
               .addBox(2.0F, -0.5F, 0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 54)
               .addBox(-3.0F, -0.5F, 0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 28)
               .addBox(-1.0F, -2.5F, 2.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 54)
               .addBox(-1.0F, -1.5F, 0.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -10.5F, -5.5F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(32, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(36, 9)
               .addBox(-1.0732F, 3.2706F, -4.4697F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(48, 54)
               .addBox(1.4268F, -1.7294F, -0.4697F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(54, 54)
               .addBox(-2.5732F, -1.7294F, -0.4697F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(27, 0)
               .addBox(-2.5732F, 0.2706F, -1.4697F, 6.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(8.4937F, -5.982F, -1.5825F, -0.0288F, -0.3614F, 0.1572F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(5.0F, 2.1036F, 0.4142F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(28, 14)
               .addBox(1.0F, -3.8964F, 0.4142F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, -2.9396F, -1.2067F, -0.4215F, -0.3614F, 0.1572F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -3.0F, 9.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, 4.0F, 5.0F, 0.0917F, -0.0134F, 0.3811F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(48, 0)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(10, 54)
               .addBox(-3.0F, -7.0F, -2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 16)
               .addBox(-2.0F, -5.0F, -2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 54)
               .addBox(-3.0F, -7.0F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 22)
               .addBox(-5.2723F, -0.862F, -3.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 44)
               .addBox(-5.0F, 2.0F, -3.0F, 2.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(48, 44)
               .addBox(-4.5F, -2.0F, -0.5F, 2.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(27, 0).addBox(0.0536F, -1.0113F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.0471F, -2.26F, 1.5F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(48, 16).addBox(-1.4529F, 1.26F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.0471F, -2.26F, 1.5F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(12, 38)
               .addBox(-0.5F, 0.0F, -0.25F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(43, 0)
               .addBox(-0.5F, 0.0F, -3.25F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -3.0F, 0.75F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r8 = right_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(56, 19)
               .addBox(0.25F, -1.0F, 0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 22)
               .addBox(0.25F, -1.0F, -3.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.8827F, -3.9239F, 1.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r9 = right_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create()
               .texOffs(43, 0)
               .addBox(-1.1449F, 1.6798F, -1.625F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 22)
               .addBox(-1.2764F, -1.1661F, -0.125F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5471F, -0.7987F, -1.875F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r10 = right_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(24, 22).addBox(-0.9464F, -5.2226F, 0.375F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5471F, -0.7987F, -1.875F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(27, 0).addBox(0.0536F, -1.0113F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5471F, 1.74F, -1.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(48, 16).addBox(-1.4529F, 1.26F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5471F, 1.74F, -1.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(24, 22).addBox(-0.9464F, -4.7613F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5471F, -5.26F, 0.0F, 0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r14 = right_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(0, 22).addBox(-1.4529F, -0.74F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5471F, -5.26F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(32, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
