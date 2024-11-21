package iskallia.vault.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class GoblinArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? GoblinArmorLayers.LeggingsLayer::createBodyLayer : GoblinArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? GoblinArmorLayers.LeggingsLayer::new : GoblinArmorLayers.MainLayer::new;
   }

   @SubscribeEvent
   public static void shiningParticles(LivingUpdateEvent event) {
      LivingEntity entity = event.getEntityLiving();
      if (entity.level.isClientSide()) {
         Level world = entity.level;
         Minecraft minecraft = Minecraft.getInstance();
         if (entity != minecraft.player || minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
            Set<EquipmentSlot> slots = StreamSupport.<ItemStack>stream(entity.getArmorSlots().spliterator(), false)
               .filter(itemStack -> itemStack.getItem() instanceof VaultGearItem)
               .map(VaultGearData::read)
               .map(vaultGearData -> vaultGearData.getFirstValue(ModGearAttributes.GEAR_MODEL).orElse(null))
               .filter(Objects::nonNull)
               .map(modelId -> ModDynamicModels.Armor.PIECE_REGISTRY.get(modelId).orElse(null))
               .filter(Objects::nonNull)
               .filter(armorPieceModel -> ModDynamicModels.Armor.GOBLIN.getId().equals(armorPieceModel.getArmorModel().getId()))
               .map(ArmorPieceModel::getEquipmentSlot)
               .collect(Collectors.toSet());
            if (slots.contains(EquipmentSlot.HEAD)) {
               addShiningParticle(entity, world, 1.75F);
            }

            if (slots.contains(EquipmentSlot.CHEST)) {
               addShiningParticle(entity, world, 1.1F);
            }

            if (slots.contains(EquipmentSlot.LEGS)) {
               addShiningParticle(entity, world, 0.8F);
            }

            if (slots.contains(EquipmentSlot.FEET)) {
               addShiningParticle(entity, world, 0.1F);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void addShiningParticle(LivingEntity entity, Level world, float yOffset) {
      Minecraft minecraft = Minecraft.getInstance();
      if (entity.tickCount % 4 == 0 && world.random.nextBoolean()) {
         Particle particle = minecraft.particleEngine
            .createParticle(
               ParticleTypes.SCRAPE,
               entity.getX() + (world.random.nextFloat() - 0.5F),
               entity.getY() + (world.random.nextFloat() - 0.5F) + yOffset,
               entity.getZ() + (world.random.nextFloat() - 0.5F),
               0.0,
               0.0,
               0.0
            );
         if (particle != null) {
            particle.setColor(0.5F, 0.5F, 0.2F);
         }
      }
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
               .addBox(-3.0F, 9.0F, -4.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 32)
               .addBox(-1.0F, 10.0F, -5.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(14, 32)
               .addBox(-2.25F, 0.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 16)
               .addBox(-2.25F, -1.0F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.25F, 10.0F, -3.5F, 0.0F, 0.1309F, -0.6109F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(32, 20)
               .addBox(-1.75F, 0.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 18)
               .addBox(-2.75F, -1.0F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.25F, 10.0F, -3.5F, 0.0F, -0.1309F, 0.6109F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r3 = right_leg.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(32, 22)
               .addBox(-3.5F, -3.0F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 11)
               .addBox(-4.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 4.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = right_leg.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(29, 29).addBox(-2.5F, -0.2146F, -2.6955F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 4.4611F, -5.4797F, -0.6545F, 0.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_leg.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(8, 32).addBox(-3.0F, -0.803F, -0.2744F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 4.4611F, -5.4797F, 0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r6 = left_leg.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(12, 16).addBox(1.5F, -0.2146F, -2.6955F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.8F, 4.4611F, -5.4797F, -0.6545F, 0.0F, 0.0F)
         );
         PartDefinition cube_r7 = left_leg.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(0, 32).addBox(1.0F, -0.803F, -0.2744F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.8F, 4.4611F, -5.4797F, 0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r8 = left_leg.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(32, 24)
               .addBox(0.5F, -3.0F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 6)
               .addBox(0.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.8F, 4.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
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
            CubeListBuilder.create()
               .texOffs(32, 35)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(34, 67)
               .addBox(-3.0F, -20.0F, -6.0F, 2.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 35)
               .addBox(-5.0F, -6.0F, -6.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 67)
               .addBox(-1.0F, -13.0F, -6.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 22)
               .addBox(-6.0F, -6.0F, -5.0F, 12.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(34, 38)
               .addBox(2.0F, -6.0F, -6.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 67)
               .addBox(0.0F, -15.0F, -6.0F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(35, 22)
               .addBox(3.0F, -10.0F, -6.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(46, 67).addBox(-0.5F, -4.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, -8.0F, -3.0F, 0.0F, 0.0F, -0.2182F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(52, 67).addBox(-0.5F, -4.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, -8.0F, -3.0F, 0.0F, 0.0F, 0.1309F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 35).addBox(-1.0F, -3.5F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -7.5F, -6.5F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(44, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, -3.0F, 3.0F, 12.0F, 12.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(35, 22)
               .addBox(-4.0F, -5.0F, 5.0F, 8.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(6, 67)
               .addBox(4.25F, -7.25F, -1.0F, 3.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(1.25F, -5.25F, -1.0F, 3.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(66, 34)
               .addBox(-0.75F, -5.25F, -1.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.75F, -13.75F, 8.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(16, 67)
               .addBox(-9.0F, -2.0F, -1.5F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 22)
               .addBox(-7.0F, -10.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 51)
               .addBox(-7.0F, -1.0F, -0.5F, 2.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -13.0F, 10.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(54, 51).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(0, 35).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(38, 51).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = left_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(44, 16).addBox(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.7014F, -1.8278F, 3.0F, 0.0F, -0.2182F, 0.2618F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(44, 19).addBox(0.0F, -4.0F, -1.0F, 12.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(46, 31).addBox(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.7599F, -2.3073F, -3.0F, 0.0F, 0.2618F, 0.2618F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(22, 51)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(65, 62)
               .addBox(-2.0F, 10.0F, -7.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r10 = right_leg.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create()
               .texOffs(56, 34)
               .addBox(-1.5F, -1.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 35)
               .addBox(-1.5F, -1.5F, -3.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.5F, -6.0F, 0.48F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(65, 22)
               .addBox(-1.8F, 10.0F, -7.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(6, 51)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r11 = left_leg.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(34, 7).addBox(2.5F, -1.7F, -1.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.8F, 7.5F, -6.0F, 0.48F, 0.0F, 0.0F)
         );
         PartDefinition cube_r12 = left_leg.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(34, 0).addBox(-1.5F, -2.5F, -2.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 8.7544F, -6.653F, 0.48F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
