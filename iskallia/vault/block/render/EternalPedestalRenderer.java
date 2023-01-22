package iskallia.vault.block.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.block.EternalPedestalBlock;
import iskallia.vault.block.entity.EternalPedestalTileEntity;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.entity.renderer.EternalRenderer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.renderer.VaultArmorRenderProperties;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.util.SkinProfile;
import java.awt.Color;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public class EternalPedestalRenderer implements BlockEntityRenderer<EternalPedestalTileEntity> {
   private final PlayerModel<Player> alexModel;
   private final PlayerModel<Player> steveModel;
   private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
   private final HumanoidModel inner;
   private final HumanoidModel outer;

   public EternalPedestalRenderer(Context context) {
      this.alexModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.setupModelAttributes(this.alexModel);
      this.steveModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
      this.setupModelAttributes(this.steveModel);
      this.outer = new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
      this.setupModelAttributes(this.outer);
      this.inner = new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
      this.setupModelAttributes(this.inner);
   }

   private void setupModelAttributes(PlayerModel<Player> model) {
      model.young = false;
      model.body.y += 0.01F;
      model.jacket.y += 0.01F;
      model.head.y += 0.02F;
      model.hat.y += 0.02F;
      model.leftArm.x += 0.01F;
      model.leftSleeve.x += 0.01F;
      model.rightArm.x -= 0.01F;
      model.rightSleeve.x -= 0.01F;
      model.leftLeg.x += 0.01F;
      model.leftPants.x += 0.01F;
      model.rightLeg.x -= 0.01F;
      model.rightPants.x -= 0.01F;
   }

   private void setupModelAttributes(HumanoidModel<Player> model) {
      model.young = false;
      model.body.y += 0.01F;
      model.head.y += 0.02F;
      model.hat.y += 0.02F;
      model.leftArm.z += 0.01F;
      model.rightArm.z -= 0.01F;
      model.leftLeg.z += 0.01F;
      model.rightLeg.z -= 0.01F;
   }

   public void render(
      EternalPedestalTileEntity championStatueBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      SkinProfile skinProfile = championStatueBlockEntity.getSkinProfile();
      float scale = 1.0F;
      ResourceLocation skin = skinProfile.getLocationSkin();
      EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(championStatueBlockEntity.getEternalId());
      PlayerModel<Player> model = skinProfile.isSlim() ? this.alexModel : this.steveModel;
      boolean usingVariant = false;
      if (snapshot != null && !snapshot.isUsingPlayerSkin()) {
         skin = EternalRenderer.getLocationByVariant().get(snapshot.getVariant());
         model = this.steveModel;
         usingVariant = true;
      }

      poseStack.pushPose();
      poseStack.translate(0.5, 2.0, 0.5);
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      poseStack.mulPose(this.getRotation((Direction)championStatueBlockEntity.getBlockState().getValue(EternalPedestalBlock.FACING)));
      RenderType renderType = model.renderType(skin);
      poseStack.scale(scale, scale, scale);
      if (!skinProfile.isEmpty() || usingVariant) {
         model.renderToBuffer(poseStack, buffer.getBuffer(renderType), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (snapshot != null) {
         this.renderArmWithItem(snapshot.getEquipment(EquipmentSlot.MAINHAND), model, poseStack, buffer, packedLight, packedOverlay, HumanoidArm.RIGHT);
         this.renderArmWithItem(snapshot.getEquipment(EquipmentSlot.OFFHAND), model, poseStack, buffer, packedLight, packedOverlay, HumanoidArm.LEFT);
         this.renderArmor(poseStack, snapshot.getEquipment(EquipmentSlot.HEAD), packedLight, packedOverlay);
         this.renderArmor(poseStack, snapshot.getEquipment(EquipmentSlot.CHEST), packedLight, packedOverlay);
         this.renderArmor(poseStack, snapshot.getEquipment(EquipmentSlot.LEGS), packedLight, packedOverlay);
         this.renderArmor(poseStack, snapshot.getEquipment(EquipmentSlot.FEET), packedLight, packedOverlay);
      }

      poseStack.popPose();
      poseStack.pushPose();
      poseStack.translate(0.5, 0.37, 0.5);
      poseStack.mulPose(Vector3f.YN.rotationDegrees(((Direction)championStatueBlockEntity.getBlockState().getValue(EternalPedestalBlock.FACING)).toYRot()));
      if (!skinProfile.isEmpty() && skinProfile.gameProfile.get() != null) {
         this.drawPlayerName(poseStack, skinProfile.gameProfile.get().getName());
      }

      boolean shiftDown = Minecraft.getInstance().player != null && Minecraft.getInstance().player.isShiftKeyDown();
      if (shiftDown && snapshot != null) {
         poseStack.translate(0.0, -0.125, 0.0);
         this.drawVaultLevel(poseStack, snapshot.getLevel(), buffer);
      }

      poseStack.popPose();
   }

   public void addVertex(VertexConsumer builder, PoseStack matrixStack, float x, float y, float z, Color tint, float u, float v) {
      builder.vertex(matrixStack.last().pose(), x / 16.0F, y / 16.0F, z / 16.0F)
         .color(tint.getRed() / 255.0F, tint.getGreen() / 255.0F, tint.getBlue() / 255.0F, 0.8F)
         .uv(u, v)
         .uv2(0, 240)
         .normal(1.0F, 0.0F, 0.0F)
         .endVertex();
   }

   public void renderArmor(PoseStack matrixStack, ItemStack armorStack, int packedLight, int packedOverlay) {
      matrixStack.pushPose();
      Optional<DynamicModelRegistry<?>> modelRegistry = ModDynamicModels.REGISTRIES.getAssociatedRegistry(armorStack.getItem());
      if (modelRegistry.isPresent()) {
         VaultGearData gearData = VaultGearData.read(armorStack);
         ArmorPieceModel armorPiece = gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
            .flatMap(modelId -> (Optional<? extends ArmorPieceModel>)modelRegistry.get().get(modelId))
            .filter(gearModel -> gearModel instanceof ArmorPieceModel)
            .orElse(null);
         if (armorPiece != null) {
            VaultArmorItem vaultArmorItem = VaultArmorItem.forSlot(armorPiece.getEquipmentSlot());
            ArmorLayers.BaseLayer baseLayer = VaultArmorRenderProperties.BAKED_LAYERS.get(armorPiece.getId());
            String baseTexture = vaultArmorItem.getArmorTexture(armorStack, null, armorPiece.getEquipmentSlot(), null);
            String overlayTexture = vaultArmorItem.getArmorTexture(armorStack, null, armorPiece.getEquipmentSlot(), "overlay");
            BufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            float yRotTempHead = baseLayer.head.yRot;
            float zRotTempHead = baseLayer.head.zRot;
            float xRotTempHead = baseLayer.head.xRot;
            float xTempHead = baseLayer.head.x;
            float yTempHead = baseLayer.head.y;
            float zTempHead = baseLayer.head.z;
            baseLayer.head.yRot = 0.0F;
            baseLayer.head.zRot = 0.0F;
            baseLayer.head.xRot = 0.0F;
            baseLayer.head.y = 0.0F;
            baseLayer.head.z = 0.0F;
            baseLayer.head.x = 0.0F;
            float yRotTempBody = baseLayer.body.yRot;
            float zRotTempBody = baseLayer.body.zRot;
            float xRotTempBody = baseLayer.body.xRot;
            float xTempBody = baseLayer.body.x;
            float yTempBody = baseLayer.body.y;
            float zTempBody = baseLayer.body.z;
            baseLayer.body.yRot = 0.0F;
            baseLayer.body.zRot = 0.0F;
            baseLayer.body.xRot = 0.0F;
            baseLayer.body.y = 0.01F;
            baseLayer.body.z = 0.01F;
            baseLayer.body.x = 0.01F;
            float yRotTempRightArm = baseLayer.rightArm.yRot;
            float zRotTempRightArm = baseLayer.rightArm.zRot;
            float xRotTempRightArm = baseLayer.rightArm.xRot;
            float xTempRightArm = baseLayer.rightArm.x;
            float yTempRightArm = baseLayer.rightArm.y;
            float zTempRightArm = baseLayer.rightArm.z;
            baseLayer.rightArm.yRot = 0.0F;
            baseLayer.rightArm.zRot = 0.0F;
            baseLayer.rightArm.xRot = 0.0F;
            baseLayer.rightArm.y = 2.01F;
            baseLayer.rightArm.z = 0.01F;
            baseLayer.rightArm.x = -5.01F;
            float yRotTempLeftArm = baseLayer.leftArm.yRot;
            float zRotTempLeftArm = baseLayer.leftArm.zRot;
            float xRotTempLeftArm = baseLayer.leftArm.xRot;
            float xTempLeftArm = baseLayer.leftArm.x;
            float yTempLeftArm = baseLayer.leftArm.y;
            float zTempLeftArm = baseLayer.leftArm.z;
            baseLayer.leftArm.yRot = 0.0F;
            baseLayer.leftArm.zRot = 0.0F;
            baseLayer.leftArm.xRot = 0.0F;
            baseLayer.leftArm.y = 2.01F;
            baseLayer.leftArm.z = -0.01F;
            baseLayer.leftArm.x = 5.01F;
            float yRotTempRightLeg = baseLayer.rightLeg.yRot;
            float zRotTempRightLeg = baseLayer.rightLeg.zRot;
            float xRotTempRightLeg = baseLayer.rightLeg.xRot;
            float xTempRightLeg = baseLayer.rightLeg.x;
            float yTempRightLeg = baseLayer.rightLeg.y;
            float zTempRightLeg = baseLayer.rightLeg.z;
            baseLayer.rightLeg.yRot = 0.0F;
            baseLayer.rightLeg.zRot = 0.0F;
            baseLayer.rightLeg.xRot = 0.0F;
            baseLayer.rightLeg.y = 12.01F;
            baseLayer.rightLeg.z = 0.11F;
            baseLayer.rightLeg.x = -1.91F;
            float yRotTempLeftLeg = baseLayer.leftLeg.yRot;
            float zRotTempLeftLeg = baseLayer.leftLeg.zRot;
            float xRotTempLeftLeg = baseLayer.leftLeg.xRot;
            float xTempLeftLeg = baseLayer.leftLeg.x;
            float yTempLeftLeg = baseLayer.leftLeg.y;
            float zTempLeftLeg = baseLayer.leftLeg.z;
            baseLayer.leftLeg.yRot = 0.0F;
            baseLayer.leftLeg.zRot = 0.0F;
            baseLayer.leftLeg.xRot = 0.0F;
            baseLayer.leftLeg.y = 12.01F;
            baseLayer.leftLeg.z = 0.09F;
            baseLayer.leftLeg.x = 1.91F;
            matrixStack.scale(1.01F, 1.01F, 1.01F);
            if (baseTexture != null) {
               VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(
                  multiBufferSource, RenderType.armorCutoutNoCull(new ResourceLocation(baseTexture)), false, armorStack.hasFoil()
               );
               baseLayer.renderToBuffer(matrixStack, vertexconsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (overlayTexture != null) {
               VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(
                  multiBufferSource, RenderType.armorCutoutNoCull(new ResourceLocation(overlayTexture)), false, armorStack.hasFoil()
               );
               baseLayer.renderToBuffer(matrixStack, vertexconsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            baseLayer.head.yRot = yRotTempHead;
            baseLayer.head.zRot = zRotTempHead;
            baseLayer.head.xRot = xRotTempHead;
            baseLayer.head.y = yTempHead;
            baseLayer.head.z = zTempHead;
            baseLayer.head.x = xTempHead;
            baseLayer.body.yRot = yRotTempBody;
            baseLayer.body.zRot = zRotTempBody;
            baseLayer.body.xRot = xRotTempBody;
            baseLayer.body.y = yTempBody;
            baseLayer.body.z = zTempBody;
            baseLayer.body.x = xTempBody;
            baseLayer.leftArm.yRot = yRotTempLeftArm;
            baseLayer.leftArm.zRot = zRotTempLeftArm;
            baseLayer.leftArm.xRot = xRotTempLeftArm;
            baseLayer.leftArm.y = yTempLeftArm;
            baseLayer.leftArm.z = zTempLeftArm;
            baseLayer.leftArm.x = xTempLeftArm;
            baseLayer.rightArm.yRot = yRotTempRightArm;
            baseLayer.rightArm.zRot = zRotTempRightArm;
            baseLayer.rightArm.xRot = xRotTempRightArm;
            baseLayer.rightArm.y = yTempRightArm;
            baseLayer.rightArm.z = zTempRightArm;
            baseLayer.rightArm.x = xTempRightArm;
            baseLayer.leftLeg.yRot = yRotTempLeftLeg;
            baseLayer.leftLeg.zRot = zRotTempLeftLeg;
            baseLayer.leftLeg.xRot = xRotTempLeftLeg;
            baseLayer.leftLeg.y = yTempLeftLeg;
            baseLayer.leftLeg.z = zTempLeftLeg;
            baseLayer.leftLeg.x = xTempLeftLeg;
            baseLayer.rightLeg.yRot = yRotTempRightLeg;
            baseLayer.rightLeg.zRot = zRotTempRightLeg;
            baseLayer.rightLeg.xRot = xRotTempRightLeg;
            baseLayer.rightLeg.y = yTempRightLeg;
            baseLayer.rightLeg.z = zTempRightLeg;
            baseLayer.rightLeg.x = xTempRightLeg;
            multiBufferSource.endBatch();
         }
      } else if (armorStack.getItem() instanceof ArmorItem) {
         ArmorItem armoritem = (ArmorItem)armorStack.getItem();
         BufferSource multiBufferSourcex = Minecraft.getInstance().renderBuffers().bufferSource();
         EquipmentSlot equipmentSlot = armoritem.getSlot();
         boolean flag = this.usesInnerModel(equipmentSlot);
         this.setPartVisibility(flag ? this.inner : this.outer, equipmentSlot);
         HumanoidModel var10004 = flag ? this.inner : this.outer;
         Model model = this.getArmorModelHook(Minecraft.getInstance().player, armorStack, equipmentSlot, var10004);
         boolean flag1 = armorStack.hasFoil();
         if (armoritem instanceof DyeableLeatherItem) {
            int i = ((DyeableLeatherItem)armoritem).getColor(armorStack);
            float f = (i >> 16 & 0xFF) / 255.0F;
            float f1 = (i >> 8 & 0xFF) / 255.0F;
            float f2 = (i & 0xFF) / 255.0F;
            this.renderModel(
               matrixStack,
               multiBufferSourcex,
               15728880,
               flag1,
               model,
               f,
               f1,
               f2,
               this.getArmorResource(Minecraft.getInstance().player, armorStack, equipmentSlot, null)
            );
            this.renderModel(
               matrixStack,
               multiBufferSourcex,
               15728880,
               flag1,
               model,
               1.0F,
               1.0F,
               1.0F,
               this.getArmorResource(Minecraft.getInstance().player, armorStack, equipmentSlot, "overlay")
            );
         } else {
            this.renderModel(
               matrixStack,
               multiBufferSourcex,
               15728880,
               flag1,
               model,
               1.0F,
               1.0F,
               1.0F,
               this.getArmorResource(Minecraft.getInstance().player, armorStack, equipmentSlot, null)
            );
         }
      }

      matrixStack.popPose();
   }

   protected void setPartVisibility(HumanoidModel<?> pModel, EquipmentSlot pSlot) {
      pModel.setAllVisible(false);
      switch (pSlot) {
         case HEAD:
            pModel.head.visible = true;
            pModel.hat.visible = true;
            break;
         case CHEST:
            pModel.body.visible = true;
            pModel.rightArm.visible = true;
            pModel.leftArm.visible = true;
            break;
         case LEGS:
            pModel.body.visible = true;
            pModel.rightLeg.visible = true;
            pModel.leftLeg.visible = true;
            break;
         case FEET:
            pModel.rightLeg.visible = true;
            pModel.leftLeg.visible = true;
      }
   }

   private void renderModel(
      PoseStack p_117107_,
      MultiBufferSource p_117108_,
      int p_117109_,
      boolean p_117111_,
      Model p_117112_,
      float p_117114_,
      float p_117115_,
      float p_117116_,
      ResourceLocation armorResource
   ) {
      VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), false, p_117111_);
      p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, p_117114_, p_117115_, p_117116_, 1.0F);
   }

   protected Model getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> model) {
      return ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }

   public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
      ArmorItem item = (ArmorItem)stack.getItem();
      String texture = item.getMaterial().getName();
      String domain = "minecraft";
      int idx = texture.indexOf(58);
      if (idx != -1) {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }

      String s1 = String.format(
         Locale.ROOT,
         "%s:textures/models/armor/%s_layer_%d%s.png",
         domain,
         texture,
         this.usesInnerModel(slot) ? 2 : 1,
         type == null ? "" : String.format(Locale.ROOT, "_%s", type)
      );
      s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
      ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);
      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s1);
         ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
      }

      return resourcelocation;
   }

   private boolean usesInnerModel(EquipmentSlot pSlot) {
      return pSlot == EquipmentSlot.LEGS;
   }

   private void drawPlayerName(PoseStack poseStack, String playerIGN) {
      Font font = Minecraft.getInstance().gui.getFont();
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, 0.501);
      poseStack.scale(0.011F, -0.011F, 0.011F);
      font.draw(poseStack, playerIGN, -font.width(playerIGN) / 2.0F, -9 / 2.0F, ChatFormatting.GRAY.getColor());
      poseStack.popPose();
   }

   private void drawVaultLevel(PoseStack poseStack, int level, MultiBufferSource buffer) {
      Font font = Minecraft.getInstance().gui.getFont();
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, 0.501);
      poseStack.scale(0.011F, -0.011F, 0.011F);
      FormattedCharSequence $$25 = new TranslatableComponent(String.valueOf(level)).getVisualOrderText();
      float $$26 = -font.width($$25) / 2;
      font.drawInBatch8xOutline(
         $$25,
         $$26,
         -9 / 2.0F,
         TextColor.parseColor("#FFE637").getValue(),
         TextBorder.DEFAULT_BORDER_COLOR.getValue(),
         poseStack.last().pose(),
         buffer,
         15728880
      );
      poseStack.popPose();
   }

   protected void renderArmWithItem(
      ItemStack item, PlayerModel<Player> model, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int packedOverlay, HumanoidArm arm
   ) {
      if (!item.isEmpty()) {
         poseStack.pushPose();
         model.translateToHand(arm, poseStack);
         poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         boolean flag = arm == HumanoidArm.LEFT;
         poseStack.translate((flag ? -1 : 1) / 16.0F, 0.125, -0.625);
         Minecraft mc = Minecraft.getInstance();
         TransformType type = !flag ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.THIRD_PERSON_LEFT_HAND;
         mc.getItemRenderer()
            .renderStatic(mc.player, item, type, flag, poseStack, bufferSource, mc.level, combinedLight, packedOverlay, mc.player.getId() + type.ordinal());
         poseStack.popPose();
      }
   }

   private ResourceLocation getPlayerSkin(EternalPedestalTileEntity championStatueBlockEntity) {
      return championStatueBlockEntity.getSkinProfile().getLocationSkin();
   }

   private Quaternion getRotation(Direction direction) {
      return switch (direction) {
         case NORTH -> Quaternion.ONE;
         case SOUTH -> Vector3f.YP.rotationDegrees(180.0F);
         case WEST -> Vector3f.YP.rotationDegrees(-90.0F);
         case EAST -> Vector3f.YP.rotationDegrees(90.0F);
         case UP, DOWN -> Quaternion.ONE;
         default -> throw new IncompatibleClassChangeError();
      };
   }
}
