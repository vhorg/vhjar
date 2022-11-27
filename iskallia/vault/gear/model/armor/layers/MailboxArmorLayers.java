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

public class MailboxArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? MailboxArmorLayers.LeggingsLayer::createBodyLayer : MailboxArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? MailboxArmorLayers.LeggingsLayer::new : MailboxArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .texOffs(0, 0)
               .addBox(-3.0F, 3.0F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .mirror()
               .addBox(-0.05F, 3.0F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 16)
               .mirror()
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 32);
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
               .addBox(-4.0F, -7.0F, -4.25F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(20, 16)
               .addBox(-6.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(20, 16)
               .addBox(5.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(32, 5)
               .addBox(-6.0F, -2.0F, -5.75F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 5)
               .addBox(3.0F, -2.0F, -5.75F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-3.0F, -9.0F, -4.5F, 6.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.5F, -7.0F, -2.5F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .mirror()
               .addBox(4.5F, -7.0F, -2.5F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(3, 1)
               .addBox(-2.0F, -10.0F, -3.5F, 4.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(28, 0).addBox(-3.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -5.5F, -5.25F, -0.7418F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 32)
               .mirror()
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .mirror(false)
               .texOffs(0, 43)
               .addBox(-6.0F, -2.0F, -4.0F, 12.0F, 13.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(38, 9)
               .addBox(-5.0F, -1.0F, -6.0F, 10.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(13, 51)
               .addBox(-5.0F, -1.0F, 3.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 43)
               .addBox(-4.5F, 7.75F, 2.0F, 9.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(40, 43).addBox(-4.75F, -1.875F, -0.875F, 9.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 1.875F, -5.875F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(40, 43).addBox(-4.5F, -0.875F, -1.375F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.75F, 8.875F, -3.375F, 0.6545F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(36, 32)
               .mirror()
               .addBox(-5.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 55)
               .mirror()
               .addBox(-6.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 55)
               .mirror()
               .addBox(-6.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(20, 52)
               .mirror()
               .addBox(-5.0F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false)
               .texOffs(20, 52)
               .addBox(-0.25F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(36, 32)
               .addBox(-2.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 55)
               .addBox(1.25F, -6.25F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(48, 57).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(48, 57).mirror().addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
