package iskallia.vault.gear.model.armor.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.client.particles.NovaExplosionCloudParticle;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.ModelPartHelper;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class ChampionArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ChampionArmorLayers.LeggingsLayer::createBodyLayer : ChampionArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ChampionArmorLayers.LeggingsLayer::new : ChampionArmorLayers.MainLayer::new;
   }

   @SubscribeEvent
   public static void shiningParticles(LivingUpdateEvent event) {
      LivingEntity entity = event.getEntityLiving();
      if (entity.level.isClientSide()) {
         ClientLevel world = (ClientLevel)entity.level;
         Set<EquipmentSlot> slots = StreamSupport.<ItemStack>stream(entity.getArmorSlots().spliterator(), false)
            .filter(itemStack -> itemStack.getItem() instanceof VaultGearItem)
            .map(VaultGearData::read)
            .map(vaultGearData -> vaultGearData.getFirstValue(ModGearAttributes.GEAR_MODEL).orElse(null))
            .filter(Objects::nonNull)
            .map(modelId -> ModDynamicModels.Armor.PIECE_REGISTRY.get(modelId).orElse(null))
            .filter(Objects::nonNull)
            .filter(armorPieceModel -> ModDynamicModels.Armor.CHAMPION.getId().equals(armorPieceModel.getArmorModel().getId()))
            .map(ArmorPieceModel::getEquipmentSlot)
            .collect(Collectors.toSet());
         if (slots.contains(EquipmentSlot.FEET)) {
            addTrailingParticle(entity);
         }

         Minecraft minecraft = Minecraft.getInstance();
         if (entity != minecraft.player || minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
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
   private static void addShiningParticle(LivingEntity entity, ClientLevel world, float yOffset) {
      Minecraft minecraft = Minecraft.getInstance();
      if (entity.tickCount % 2 == 0 && world.random.nextBoolean()) {
         GlowParticle particle = (GlowParticle)minecraft.particleEngine
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
            int index = world.random.nextInt(4);
            int color = index == 0 ? 7798486 : (index == 1 ? 5307221 : (index == 2 ? 16113515 : 16740207));
            particle.setColor((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
         }
      }
   }

   private static void addTrailingParticle(LivingEntity entity) {
      Minecraft minecraft = Minecraft.getInstance();
      if (entity.tickCount % 2 == 0 && entity.isOnGround()) {
         Vec3 velocity = entity.getDeltaMovement();
         if (velocity.x != 0.0 && velocity.y != 0.0 && velocity.z != 0.0) {
            NovaExplosionCloudParticle particle = (NovaExplosionCloudParticle)minecraft.particleEngine
               .createParticle((ParticleOptions)ModParticles.NOVA_CLOUD.get(), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            if (particle != null) {
               int color = 14676375;
               particle.setColor((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
            }
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(10, 32)
               .addBox(-0.5F, 1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(31, 31)
               .addBox(-1.0F, -5.0F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.2655F, 15.9141F, 2.7325F, 2.8424F, -0.8559F, 3.0531F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(0, 32)
               .addBox(-1.0F, -3.75F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 14)
               .addBox(-0.5F, 2.25F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, 15.0F, -3.5F, -0.3232F, -0.4934F, -0.1154F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(12, 16)
               .addBox(-2.0F, 0.5F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 7)
               .addBox(-2.5F, -5.5F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.2221F, 16.2544F, 3.243F, 2.7323F, 1.0029F, 3.1031F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(28, 17)
               .addBox(-0.7F, 1.7F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-1.2F, -4.3F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, 15.0F, -3.5F, -0.3142F, 0.4887F, 0.1222F)
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
      protected ModelPart leftWing;
      protected ModelPart rightWing;

      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
         ModelPart body = root.getChild("body");
         this.leftWing = body.getChild("left_wing");
         this.rightWing = body.getChild("right_wing");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(76, 94)
               .addBox(-6.0F, -8.0F, 0.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 90)
               .addBox(-6.0F, -2.0F, -5.0F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(87, 76)
               .addBox(5.0F, -2.0F, -5.0F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(70, 94)
               .addBox(5.0F, -8.0F, 0.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(78, 47)
               .addBox(-3.0F, -11.0F, -8.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(66, 36)
               .addBox(-4.0F, -12.0F, -4.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(16, 65)
               .addBox(-4.0F, -12.0F, 3.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-5.0F, -13.0F, 0.0F, 10.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(80, 25)
               .addBox(-9.0F, -10.0F, -1.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(70, 78)
               .addBox(5.0F, -10.0F, -1.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(64, 51).addBox(-5.0F, -4.5F, -5.5F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.21F, -7.8551F, 8.2645F, 0.3927F, -0.5672F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(49, 16).addBox(-4.0F, -1.5F, 0.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.1329F, -9.8257F, 5.597F, 0.5256F, 0.1268F, 0.144F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(83, 11).addBox(-1.9F, -2.0F, -1.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.6812F, -11.0286F, 7.5001F, 0.5256F, -0.1268F, -0.144F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(66, 25).addBox(-2.0F, -3.0F, -3.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.0F, -8.0F, 3.0F, 0.3927F, 0.5672F, 0.0F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(85, 18)
               .addBox(1.0F, -8.5F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 8)
               .addBox(1.0F, -2.5F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(64, 47)
               .addBox(-1.0F, -1.5F, 2.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.4462F, -0.5F, -9.0F, 0.0F, -0.5236F, 0.0F)
         );
         PartDefinition cube_r6 = head.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(42, 41)
               .addBox(-3.0F, -1.5F, 0.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(85, 5)
               .addBox(0.0F, -8.5F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 0)
               .addBox(0.0F, -2.5F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0179F, -0.5F, -6.2679F, 0.0F, 0.5236F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(42, 59)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(32, 75)
               .addBox(-4.0F, 0.0F, 3.0F, 8.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(42, 25)
               .addBox(-0.8097F, 6.0F, -2.9567F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 31)
               .addBox(-2.8097F, 4.0F, -2.9567F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(82, 62)
               .addBox(-4.8097F, 0.0F, -1.9567F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4518F, -1.0F, -7.0F, 0.4215F, 0.3614F, 0.1572F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(58, 25)
               .addBox(1.8097F, 4.0F, -2.9567F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-0.1903F, 6.0F, -2.9567F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(69, 16)
               .addBox(-1.1903F, 0.0F, -1.9567F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.25F, -1.0F, -7.0F, 0.4215F, -0.3614F, -0.1572F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(0, 16).addBox(-1.1903F, -2.0F, -1.9567F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.25F, 9.0F, -5.0F, -0.4215F, -0.3614F, 0.1572F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(58, 16).addBox(3.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.2645F, 2.777F, -6.709F, -0.2898F, -0.015F, 0.27F)
         );
         PartDefinition cube_r11 = body.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(20, 16).addBox(-0.5F, -4.0F, -1.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0817F, 4.9613F, -6.2673F, -0.2633F, -0.1235F, -0.1085F)
         );
         PartDefinition cube_r12 = body.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(16, 16).addBox(9.0F, -2.0F, -1.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.8324F, 5.4909F, -5.6323F, -0.2633F, -0.1235F, -0.1085F)
         );
         PartDefinition cube_r13 = body.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(57, 40).addBox(-1.0F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.8324F, 5.4909F, -5.6323F, -0.2898F, -0.015F, 0.27F)
         );
         PartDefinition cube_r14 = body.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(85, 0).addBox(-4.8097F, -2.0F, -1.9567F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4518F, 9.0F, -5.0F, -0.4215F, 0.3614F, -0.1572F)
         );
         PartDefinition left_wing = body.addOrReplaceChild(
            "left_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, 2.0F, 3.5F, 0.0F, -0.0436F, 0.0F)
         );
         PartDefinition cube_r15 = left_wing.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create()
               .texOffs(0, 65)
               .addBox(2.0F, -2.0F, 16.5F, 2.0F, 23.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(46, 8)
               .addBox(1.0F, -6.0F, 15.5F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(42, 25)
               .addBox(2.0F, -5.0F, -4.5F, 2.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
               .texOffs(0, 22)
               .addBox(2.75F, -3.0F, -4.5F, 0.0F, 22.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.7316F, 0.443F, 0.0211F)
         );
         PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-3.0F, 2.0F, 3.5F));
         PartDefinition cube_r16 = right_wing.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create()
               .texOffs(8, 65)
               .addBox(-1.0F, -11.5F, -1.0F, 2.0F, 23.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(0.0F, -12.5F, -22.0F, 0.0F, 22.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-11.5571F, -5.76F, 24.4215F, 0.7156F, -0.4363F, -0.0209F)
         );
         PartDefinition cube_r17 = right_wing.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create()
               .texOffs(54, 75)
               .addBox(-5.0F, -6.5F, 17.5F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(42, 0)
               .addBox(-4.0F, -5.5F, -5.5F, 2.0F, 2.0F, 23.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.7156F, -0.4363F, -0.0209F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(16, 74)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(72, 86)
               .addBox(-6.0F, 4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r18 = right_arm.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create()
               .texOffs(52, 0)
               .addBox(-0.5F, 2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(94, 84)
               .addBox(-1.0F, -1.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.0F, 9.5F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r19 = right_arm.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create().texOffs(91, 92).addBox(-2.5F, -4.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.0F, 9.5F, 0.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r20 = right_arm.addOrReplaceChild(
            "cube_r20",
            CubeListBuilder.create()
               .texOffs(94, 69)
               .addBox(1.5F, -1.5F, 3.375F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(32, 84)
               .addBox(-4.5F, -5.5F, 2.375F, 3.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 84)
               .addBox(-4.5F, -5.5F, -0.375F, 3.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(84, 84)
               .addBox(-4.5F, -4.5F, -2.875F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(22, 90)
               .addBox(-4.5F, -2.5F, -5.375F, 3.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 47)
               .addBox(-1.5F, 0.5F, -4.625F, 7.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, -3.5F, 0.625F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(69, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r21 = left_arm.addOrReplaceChild(
            "cube_r21",
            CubeListBuilder.create()
               .texOffs(62, 84)
               .addBox(1.5F, -4.5F, -2.875F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 83)
               .addBox(1.5F, -5.5F, -0.375F, 3.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(82, 69)
               .addBox(1.5F, -5.5F, 2.375F, 3.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(94, 41)
               .addBox(-4.5F, -1.5F, 3.375F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 8)
               .addBox(-5.5F, 0.5F, -4.625F, 7.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(12, 90)
               .addBox(1.5F, -2.5F, -5.375F, 3.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, -3.5F, 0.625F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r22 = left_arm.addOrReplaceChild(
            "cube_r22",
            CubeListBuilder.create()
               .texOffs(94, 56)
               .addBox(-1.0F, -1.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 20)
               .addBox(-0.5F, 2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.0F, 9.5F, 0.0F, -3.1416F, 0.0F, -2.7489F)
         );
         PartDefinition cube_r23 = left_arm.addOrReplaceChild(
            "cube_r23",
            CubeListBuilder.create().texOffs(92, 18).addBox(-2.5F, -4.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.0F, 9.5F, 0.0F, -3.1416F, 0.0F, 2.3562F)
         );
         PartDefinition cube_r24 = left_arm.addOrReplaceChild(
            "cube_r24",
            CubeListBuilder.create().texOffs(52, 0).addBox(7.0F, 4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(13.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(66, 62).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r25 = right_leg.addOrReplaceChild(
            "cube_r25",
            CubeListBuilder.create().texOffs(66, 25).addBox(0.0F, -4.5F, -1.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 9.0671F, -5.4526F, 0.5672F, 0.0F, 0.0F)
         );
         PartDefinition cube_r26 = right_leg.addOrReplaceChild(
            "cube_r26",
            CubeListBuilder.create()
               .texOffs(86, 56)
               .addBox(-1.0F, -1.5F, -2.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(92, 5)
               .addBox(-2.0F, -3.5F, -0.5F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 9.5F, -3.5F, -0.2182F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(42, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r27 = left_leg.addOrReplaceChild(
            "cube_r27",
            CubeListBuilder.create()
               .texOffs(90, 33)
               .addBox(-2.0F, -3.5F, -0.5F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 47)
               .addBox(-1.0F, -1.5F, -2.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 9.5F, -3.5F, -0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r28 = left_leg.addOrReplaceChild(
            "cube_r28",
            CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -4.5F, -1.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.3F, 9.0671F, -5.4526F, 0.5672F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }

      @Override
      public void renderToBuffer(
         @Nonnull PoseStack poseStack,
         @Nonnull VertexConsumer vertexConsumer,
         int packedLight,
         int packedOverlay,
         float red,
         float green,
         float blue,
         float alpha
      ) {
         ModelPartHelper.runPreservingTransforms(() -> {
            this.animateParts();
            super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         }, this.rightWing, this.leftWing);
      }

      private void animateParts() {
         this.leftWing.xRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 1000.0), -1.0F, 1.0F, 0.0F, 0.17453294F);
         this.leftWing.yRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 500.0), -1.0F, 1.0F, 0.0F, (float) (Math.PI / 6));
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
      }
   }
}
