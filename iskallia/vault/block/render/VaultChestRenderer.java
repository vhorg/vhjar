package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.ScavengerChestBlock;
import iskallia.vault.block.model.VaultChestModel;
import iskallia.vault.init.ModBlocks;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner.Combiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

public class VaultChestRenderer<T extends ChestBlockEntity> extends ChestRenderer<T> {
   public static final Map<Block, Material> MATERIAL_MAP = Map.ofEntries(
      Map.entry(ModBlocks.WOODEN_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_chest"))),
      Map.entry(ModBlocks.GILDED_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_gilded_chest"))),
      Map.entry(ModBlocks.LIVING_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_mossy_chest"))),
      Map.entry(ModBlocks.ORNATE_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_ornate_chest"))),
      Map.entry(ModBlocks.TREASURE_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_treasure_chest"))),
      Map.entry(ModBlocks.ALTAR_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_altar_chest"))),
      Map.entry(ModBlocks.WOODEN_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_chest"))),
      Map.entry(ModBlocks.GILDED_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_gilded_chest"))),
      Map.entry(ModBlocks.LIVING_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_mossy_chest"))),
      Map.entry(ModBlocks.ORNATE_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_ornate_chest"))),
      Map.entry(ModBlocks.TREASURE_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_treasure_chest"))),
      Map.entry(ModBlocks.ALTAR_CHEST_PLACEABLE, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/vault_altar_chest"))),
      Map.entry(ModBlocks.SCAVENGER_CHEST, new Material(Sheets.CHEST_SHEET, VaultMod.id("entity/chest/scavanger_chest")))
   );
   private static final Map<Block, VaultChestModel> MODEL_MAP = new HashMap<>();

   public VaultChestRenderer(Context context) {
      super(context);
      VaultChestModel chest = new VaultChestModel(context.bakeLayer(VaultChestModel.TREASURE_LOCATION));
      VaultChestModel chestM = new VaultChestModel(context.bakeLayer(VaultChestModel.MOSSY_LOCATION));
      MODEL_MAP.put(ModBlocks.TREASURE_CHEST, chest);
      MODEL_MAP.put(ModBlocks.ORNATE_CHEST, chestM);
      MODEL_MAP.put(ModBlocks.ALTAR_CHEST, chestM);
      MODEL_MAP.put(ModBlocks.WOODEN_CHEST, chestM);
      MODEL_MAP.put(ModBlocks.GILDED_CHEST, chestM);
      MODEL_MAP.put(ModBlocks.LIVING_CHEST, chestM);
      MODEL_MAP.put(ModBlocks.TREASURE_CHEST_PLACEABLE, chest);
      MODEL_MAP.put(ModBlocks.ORNATE_CHEST_PLACEABLE, chestM);
      MODEL_MAP.put(ModBlocks.ALTAR_CHEST_PLACEABLE, chestM);
      MODEL_MAP.put(ModBlocks.WOODEN_CHEST_PLACEABLE, chestM);
      MODEL_MAP.put(ModBlocks.GILDED_CHEST_PLACEABLE, chestM);
      MODEL_MAP.put(ModBlocks.LIVING_CHEST_PLACEABLE, chestM);
      MODEL_MAP.put(ModBlocks.SCAVENGER_CHEST, new VaultChestModel(context.bakeLayer(VaultChestModel.SCAVANGER_LOCATION)));
   }

   protected Material getMaterial(T tileEntity, ChestType chestType) {
      Material m = MATERIAL_MAP.get(tileEntity.getBlockState().getBlock());
      return m != null ? m : super.getMaterial(tileEntity, chestType);
   }

   public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      VaultChestModel model = MODEL_MAP.get(pBlockEntity.getBlockState().getBlock());
      if (model != null) {
         this.customRender(model, pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
      } else {
         super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
      }
   }

   public void customRender(
      VaultChestModel model, T tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      Level level = tileEntity.getLevel();
      boolean flag = level != null;
      Direction dir = flag ? (Direction)tileEntity.getBlockState().getValue(ChestBlock.FACING) : Direction.SOUTH;
      float hAngle = dir.toYRot();
      NeighborCombineResult<? extends ChestBlockEntity> lidCallback = Combiner::acceptNone;
      float lidRotation = ((Float2FloatFunction)lidCallback.apply(ScavengerChestBlock.opennessCombiner(tileEntity))).get(partialTicks);
      lidRotation = 1.0F - lidRotation;
      lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
      model.setLidAngle(lidRotation);
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
}
