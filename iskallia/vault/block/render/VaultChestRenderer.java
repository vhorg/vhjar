package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultBarrelBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.block.model.VaultChestModel;
import iskallia.vault.init.ModBlocks;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner.Combiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class VaultChestRenderer<T extends ChestBlockEntity> extends ChestRenderer<T> {
   public static final Map<Block, Material> NORMAL_MATERIAL_MAP = Map.ofEntries(
      Map.entry(ModBlocks.WOODEN_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_chest"))),
      Map.entry(ModBlocks.GILDED_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_gilded_chest"))),
      Map.entry(ModBlocks.LIVING_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_mossy_chest"))),
      Map.entry(ModBlocks.ORNATE_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_ornate_chest"))),
      Map.entry(ModBlocks.TREASURE_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_treasure_chest"))),
      Map.entry(ModBlocks.ALTAR_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_altar_chest"))),
      Map.entry(ModBlocks.HARDENED_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_hardened_chest"))),
      Map.entry(ModBlocks.ENIGMA_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_enigma_chest"))),
      Map.entry(ModBlocks.FLESH_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_flesh_chest"))),
      Map.entry(ModBlocks.GILDED_STRONGBOX, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_gilded_strongbox"))),
      Map.entry(ModBlocks.ORNATE_STRONGBOX, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_ornate_strongbox"))),
      Map.entry(ModBlocks.LIVING_STRONGBOX, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_living_strongbox"))),
      Map.entry(ModBlocks.WOODEN_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_chest"))),
      Map.entry(ModBlocks.GILDED_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_gilded_chest"))),
      Map.entry(ModBlocks.LIVING_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_mossy_chest"))),
      Map.entry(ModBlocks.ORNATE_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_ornate_chest"))),
      Map.entry(ModBlocks.TREASURE_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_treasure_chest"))),
      Map.entry(ModBlocks.ALTAR_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_altar_chest"))),
      Map.entry(ModBlocks.HARDENED_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_hardened_chest"))),
      Map.entry(ModBlocks.ENIGMA_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_enigma_chest"))),
      Map.entry(ModBlocks.FLESH_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_flesh_chest")))
   );
   public static final Map<Block, Material> PRESENT_MATERIAL_MAP = Map.ofEntries(
      Map.entry(ModBlocks.WOODEN_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange"))),
      Map.entry(ModBlocks.GILDED_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/yellow"))),
      Map.entry(ModBlocks.LIVING_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/green"))),
      Map.entry(ModBlocks.ORNATE_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/red"))),
      Map.entry(ModBlocks.TREASURE_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/pink"))),
      Map.entry(ModBlocks.ALTAR_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/cyan"))),
      Map.entry(ModBlocks.HARDENED_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange"))),
      Map.entry(ModBlocks.ENIGMA_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange"))),
      Map.entry(ModBlocks.FLESH_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange"))),
      Map.entry(ModBlocks.ORNATE_STRONGBOX, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_ornate_strongbox"))),
      Map.entry(ModBlocks.GILDED_STRONGBOX, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_gilded_strongbox"))),
      Map.entry(ModBlocks.LIVING_STRONGBOX, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_living_strongbox"))),
      Map.entry(ModBlocks.WOODEN_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange"))),
      Map.entry(ModBlocks.GILDED_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/yellow"))),
      Map.entry(ModBlocks.LIVING_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/green"))),
      Map.entry(ModBlocks.ORNATE_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/red"))),
      Map.entry(ModBlocks.TREASURE_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/pink"))),
      Map.entry(ModBlocks.ALTAR_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/cyan"))),
      Map.entry(ModBlocks.HARDENED_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange"))),
      Map.entry(ModBlocks.ENIGMA_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange"))),
      Map.entry(ModBlocks.FLESH_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/present/orange")))
   );
   private static final Map<Block, VaultChestModel> NORMAL_MODEL_MAP = new HashMap<>();
   private static final Map<Block, VaultChestModel> PRESENT_MODEL_MAP = new HashMap<>();

   protected Map<Block, Material> getMaterialMap(BlockState blockState) {
      VaultChestBlock.Variant variant = blockState.getOptionalValue(VaultChestBlock.VARIANT).orElse(VaultChestBlock.Variant.NORMAL);

      return switch (variant) {
         case NORMAL -> NORMAL_MATERIAL_MAP;
         case PRESENT -> PRESENT_MATERIAL_MAP;
      };
   }

   protected Map<Block, VaultChestModel> getModelMap(BlockState blockState) {
      VaultChestBlock.Variant variant = blockState.getOptionalValue(VaultChestBlock.VARIANT).orElse(VaultChestBlock.Variant.NORMAL);

      return switch (variant) {
         case NORMAL -> NORMAL_MODEL_MAP;
         case PRESENT -> PRESENT_MODEL_MAP;
      };
   }

   public VaultChestRenderer(Context context) {
      super(context);
      VaultChestModel treasureChest = new VaultChestModel(context.bakeLayer(VaultChestModel.TREASURE_LOCATION));
      VaultChestModel livingChest = new VaultChestModel(context.bakeLayer(VaultChestModel.MOSSY_LOCATION));
      VaultChestModel strongbox = new VaultChestModel(context.bakeLayer(VaultChestModel.STRONGBOX_LOCATION));
      VaultChestModel livingStrongbox = new VaultChestModel(context.bakeLayer(VaultChestModel.LIVING_STRONGBOX_LOCATION));
      new VaultChestModel(context.bakeLayer(VaultChestModel.SCAVENGER_LOCATION));
      VaultChestModel presentChest = new VaultChestModel(context.bakeLayer(VaultChestModel.PRESENT_LOCATION));
      VaultChestModel hardenedChest = new VaultChestModel(context.bakeLayer(VaultChestModel.HARDENED_LOCATION));
      VaultChestModel fleshChest = new VaultChestModel(context.bakeLayer(VaultChestModel.FLESH_LOCATION));
      NORMAL_MODEL_MAP.put(ModBlocks.TREASURE_CHEST, treasureChest);
      NORMAL_MODEL_MAP.put(ModBlocks.ORNATE_CHEST, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.ALTAR_CHEST, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.WOODEN_CHEST, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.GILDED_CHEST, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.LIVING_CHEST, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.HARDENED_CHEST, hardenedChest);
      NORMAL_MODEL_MAP.put(ModBlocks.ENIGMA_CHEST, hardenedChest);
      NORMAL_MODEL_MAP.put(ModBlocks.FLESH_CHEST, fleshChest);
      NORMAL_MODEL_MAP.put(ModBlocks.TREASURE_CHEST_PLACEABLE, treasureChest);
      NORMAL_MODEL_MAP.put(ModBlocks.ORNATE_CHEST_PLACEABLE, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.ALTAR_CHEST_PLACEABLE, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.WOODEN_CHEST_PLACEABLE, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.GILDED_CHEST_PLACEABLE, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.LIVING_CHEST_PLACEABLE, livingChest);
      NORMAL_MODEL_MAP.put(ModBlocks.HARDENED_CHEST_PLACEABLE, hardenedChest);
      NORMAL_MODEL_MAP.put(ModBlocks.ENIGMA_CHEST_PLACEABLE, hardenedChest);
      NORMAL_MODEL_MAP.put(ModBlocks.FLESH_CHEST_PLACEABLE, fleshChest);
      NORMAL_MODEL_MAP.put(ModBlocks.ORNATE_STRONGBOX, strongbox);
      NORMAL_MODEL_MAP.put(ModBlocks.GILDED_STRONGBOX, strongbox);
      NORMAL_MODEL_MAP.put(ModBlocks.LIVING_STRONGBOX, livingStrongbox);
      PRESENT_MODEL_MAP.put(ModBlocks.TREASURE_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.ORNATE_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.ALTAR_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.WOODEN_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.GILDED_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.LIVING_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.HARDENED_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.ENIGMA_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.FLESH_CHEST, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.TREASURE_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.ORNATE_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.ALTAR_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.WOODEN_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.GILDED_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.LIVING_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.HARDENED_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.ENIGMA_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.FLESH_CHEST_PLACEABLE, presentChest);
      PRESENT_MODEL_MAP.put(ModBlocks.ORNATE_STRONGBOX, strongbox);
      PRESENT_MODEL_MAP.put(ModBlocks.GILDED_STRONGBOX, strongbox);
      PRESENT_MODEL_MAP.put(ModBlocks.LIVING_STRONGBOX, livingStrongbox);
   }

   @Nonnull
   protected Material getMaterial(T tileEntity, ChestType chestType) {
      BlockState blockState = tileEntity.getBlockState();
      Material m = this.getMaterialMap(blockState).get(blockState.getBlock());
      return m != null ? m : super.getMaterial(tileEntity, chestType);
   }

   public void render(T blockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      BlockState blockState = blockEntity.getBlockState();
      if (blockState.getBlock() instanceof VaultBarrelBlock) {
         if (!((VaultChestTileEntity)blockEntity).isVaultChest()) {
            this.renderFirstItem(blockEntity.getItem(0), blockState, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
         }
      } else if (!(blockState.getBlock() instanceof VaultChestBlock vaultChestBlock && !vaultChestBlock.hasDynamicRenderer())) {
         VaultChestModel model = this.getModelMap(blockState).get(blockState.getBlock());
         if (model != null) {
            this.customRender(model, blockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
         } else {
            super.render(blockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
         }
      }
   }

   public void customRender(
      VaultChestModel model, T tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      Level level = tileEntity.getLevel();
      BlockState blockState = tileEntity.getBlockState();
      boolean flag = level != null;
      Direction dir = flag ? (Direction)blockState.getValue(ChestBlock.FACING) : Direction.SOUTH;
      float hAngle = dir.toYRot();
      NeighborCombineResult<? extends ChestBlockEntity> lidCallback = Combiner::acceptNone;
      float lidRotation = ((Float2FloatFunction)lidCallback.apply(ChestBlock.opennessCombiner(tileEntity))).get(partialTicks);
      lidRotation = 1.0F - lidRotation;
      lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
      model.setLidAngle(lidRotation);
      VaultChestBlock.Variant variant = (VaultChestBlock.Variant)blockState.getValue(VaultChestBlock.VARIANT);
      if (variant == VaultChestBlock.Variant.PRESENT) {
         model.setLidAngle(-lidRotation);
      }

      int combinedLidLight = ((Int2IntFunction)lidCallback.apply(new BrightnessCombiner())).applyAsInt(combinedLight);
      Material material = this.getMaterial(tileEntity, null);
      VertexConsumer vb = material.buffer(buffer, RenderType::entityCutout);
      matrixStack.pushPose();
      matrixStack.translate(0.5, 0.5, 0.5);
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(-hAngle));
      matrixStack.translate(-0.5, -0.5, -0.5);
      model.renderToBuffer(matrixStack, vb, combinedLidLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
   }

   private void renderFirstItem(
      ItemStack itemStack, BlockState blockState, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      if (!itemStack.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         Direction direction = (Direction)blockState.getValue(VaultBarrelBlock.DIRECTION);
         label26:
         switch (direction) {
            case SOUTH:
               poseStack.translate(0.0, 0.0, 0.5);
               poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
               break;
            case WEST:
               poseStack.translate(-0.5, 0.0, 0.0);
               poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
               break;
            case EAST:
               poseStack.translate(0.5, 0.0, 0.0);
               poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
               break;
            case UP:
               poseStack.translate(0.0, 0.5, 0.0);
               poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
               switch ((Direction)blockState.getValue(VaultBarrelBlock.FACING)) {
                  case SOUTH:
                     poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                     break label26;
                  case WEST:
                     poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
                     break label26;
                  case EAST:
                     poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                  default:
                     break label26;
               }
            case DOWN:
               poseStack.translate(0.0, -0.5, 0.0);
               poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
               switch ((Direction)blockState.getValue(VaultBarrelBlock.FACING)) {
                  case SOUTH:
                     poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                     break label26;
                  case WEST:
                     poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                     break label26;
                  case EAST:
                     poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
                  default:
                     break label26;
               }
            case NORTH:
               poseStack.translate(0.0, 0.0, -0.5);
         }

         poseStack.scale(0.3F, 0.3F, 0.3F);
         Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
         poseStack.popPose();
      }
   }
}
