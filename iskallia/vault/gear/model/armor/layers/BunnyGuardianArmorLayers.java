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

public class BunnyGuardianArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS
         ? BunnyGuardianArmorLayers.LeggingsLayer::createBodyLayer
         : BunnyGuardianArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BunnyGuardianArmorLayers.LeggingsLayer::new : BunnyGuardianArmorLayers.MainLayer::new;
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
               .texOffs(0, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(24, 0)
               .addBox(-2.0F, 9.0F, 2.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 5)
               .addBox(-2.0F, 8.0F, 4.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 9)
               .addBox(-2.0F, 7.0F, 3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(30, 30).addBox(-4.7813F, -0.3252F, -3.8663F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, 0.0F, 0.3927F, 0.3927F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(28, 12).addBox(-4.7888F, -0.3252F, 1.8483F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, 0.0F, -0.3927F, 0.3927F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 32).addBox(3.7888F, -0.3252F, 1.8483F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, 0.0F, 0.3927F, -0.3927F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(6, 32).addBox(3.7813F, -0.3252F, -3.8663F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, 0.0F, -0.3927F, -0.3927F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(12, 32).addBox(-1.0F, -1.2269F, 2.7716F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(34, 5)
               .addBox(-0.3751F, -1.0943F, 3.2176F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 19)
               .addBox(-5.9184F, -1.0943F, 0.9215F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, 0.4215F, 0.3614F, 0.1572F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(24, 32)
               .addBox(3.9184F, -1.0943F, 0.9215F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 32)
               .addBox(-1.6249F, -1.0943F, 3.2176F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, 0.4215F, -0.3614F, -0.1572F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(34, 25)
               .addBox(3.9253F, -1.0868F, -1.9382F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 37)
               .addBox(-1.618F, -1.0868F, -4.2343F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, -0.4215F, 0.3614F, -0.1572F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create()
               .texOffs(34, 11)
               .addBox(-0.382F, -1.0868F, -4.2343F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 31)
               .addBox(-5.9253F, -1.0868F, -1.9382F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.4241F, 0.0098F, -0.4215F, -0.3614F, 0.1572F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-3.5054F, -6.395F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(49, 48)
               .addBox(-4.5054F, -1.395F, -1.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.3053F, -15.9432F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(24, 0)
               .addBox(0.75F, -5.75F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 0)
               .addBox(0.75F, -0.75F, -1.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.25F, -16.25F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(51, 36)
               .addBox(-2.0F, 1.0F, -4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(47, 26)
               .addBox(-7.0F, -2.0F, -4.0F, 5.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 10)
               .addBox(-6.0F, 5.0F, -3.75F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(32, 27)
               .addBox(1.0F, 5.0F, -3.75F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(46, 16)
               .addBox(2.0F, -2.0F, -4.0F, 5.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(16, 38)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(17, 27)
               .addBox(-4.75F, -1.0F, -3.5F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(24, 16)
               .addBox(0.5F, -1.0F, -3.5F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(35, 34)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(32, 0)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(32, 43)
               .addBox(-3.0F, 5.0F, -2.0F, 0.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 32)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 41)
               .addBox(3.2F, 5.0F, -3.0F, 0.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
