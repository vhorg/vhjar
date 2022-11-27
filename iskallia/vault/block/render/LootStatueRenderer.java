package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.SkinProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;

public class LootStatueRenderer implements BlockEntityRenderer<LootStatueTileEntity> {
   protected static StatuePlayerModel PLAYER_MODEL;
   private final Minecraft mc = Minecraft.getInstance();
   private final BlockRenderDispatcher blockRenderer = this.mc.getBlockRenderer();
   private final ItemRenderer itemRenderer = this.mc.getItemRenderer();
   private static final ResourceLocation STONE_SKIN = VaultMod.id("textures/entity/stoneskin.png");

   public LootStatueRenderer(Context context) {
      PLAYER_MODEL = new StatuePlayerModel(context);
   }

   public void render(
      LootStatueTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      BlockState blockState = tileEntity.getBlockState();
      Direction direction = (Direction)blockState.getValue(LootStatueBlock.FACING);
      renderBlockState(tileEntity.getStand(), matrixStack, buffer, this.blockRenderer, tileEntity.getLevel(), tileEntity.getBlockPos());
      matrixStack.translate(0.5, 0.5, 0.5);
      matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180.0F));
      if (tileEntity.getChipCount() > 0 && this.mc.player != null) {
         matrixStack.pushPose();
         LocalPlayer player = this.mc.player;
         int lightLevel = this.getLightAtPos(tileEntity.getLevel(), tileEntity.getBlockPos().above());
         matrixStack.translate(0.0, 0.25, 0.25);
         matrixStack.mulPose(Vector3f.YN.rotationDegrees(-direction.toYRot() - 180.0F));
         this.renderItemWithLabel(
            new ItemStack(ModItems.ACCELERATION_CHIP),
            player,
            matrixStack,
            buffer,
            combinedOverlay,
            lightLevel,
            new TextComponent(String.valueOf(tileEntity.getChipCount())),
            -1
         );
         matrixStack.popPose();
      }

      ItemStack loot = tileEntity.getLootItem();
      if (!loot.isEmpty()) {
         matrixStack.pushPose();
         matrixStack.translate(0.0, 1.25, 0.35);
         BakedModel bakedmodel = this.itemRenderer.getModel(loot, null, null, 0);
         float scale = bakedmodel.isGui3d() ? 1.25F : 0.8F;
         matrixStack.scale(scale, scale, scale);
         this.itemRenderer.render(loot, TransformType.FIXED, false, matrixStack, buffer, combinedLight, combinedOverlay, bakedmodel);
         matrixStack.popPose();
      }

      this.drawPlayerModel(matrixStack, buffer, tileEntity, combinedLight, combinedOverlay, partialTicks);
      String latestNickname = tileEntity.getSkin().getLatestNickname();
      if (!StringUtil.isNullOrEmpty(latestNickname)) {
         this.drawStatueNameplate(matrixStack, buffer, latestNickname, direction, tileEntity, combinedLight, combinedOverlay);
      }
   }

   private void drawStatueNameplate(
      PoseStack matrixStack,
      MultiBufferSource buffer,
      String latestNickname,
      Direction direction,
      LootStatueTileEntity tileEntity,
      int combinedLight,
      int combinedOverlay
   ) {
      FormattedCharSequence text = new TextComponent(latestNickname).withStyle(ChatFormatting.GRAY).getVisualOrderText();
      Font fr = this.mc.gui.getFont();
      int xOffset = fr.width(text);
      matrixStack.pushPose();
      matrixStack.translate(0.0, -0.25, 0.51);
      matrixStack.scale(0.011F, -0.011F, 0.011F);
      fr.drawInBatch(text, -xOffset / 2.0F, -9 / 2.0F, -65536, true, matrixStack.last().pose(), buffer, true, 0, combinedLight);
      matrixStack.popPose();
   }

   private void drawStatueCrownHat(PoseStack matrixStack, MultiBufferSource buffer, Direction direction, int combinedLight, int combinedOverlay) {
      float crownScale = 3.0F;
      matrixStack.pushPose();
      matrixStack.translate(0.0, 2.5, 0.0);
      matrixStack.scale(crownScale, crownScale, crownScale);
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(this.mc.player.tickCount));
      ItemStack stack = new ItemStack(ModBlocks.MVP_CROWN);
      BakedModel ibakedmodel = this.mc.getItemRenderer().getModel(stack, null, null, 0);
      this.mc.getItemRenderer().render(stack, TransformType.GROUND, true, matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
      matrixStack.popPose();
   }

   private void drawPlayerModel(
      PoseStack matrixStack, MultiBufferSource buffer, LootStatueTileEntity tileEntity, int combinedLight, int combinedOverlay, float partialTicks
   ) {
      BlockState blockState = tileEntity.getBlockState();
      Direction direction = (Direction)blockState.getValue(LootStatueBlock.FACING);
      SkinProfile skin = tileEntity.getSkin();
      ResourceLocation skinLocation = StringUtil.isNullOrEmpty(skin.getLatestNickname()) ? STONE_SKIN : skin.getLocationSkin();
      RenderType renderType = PLAYER_MODEL.renderType(skinLocation);
      PLAYER_MODEL.young = false;
      PLAYER_MODEL.leftArm.xRot = -120.0F;
      PLAYER_MODEL.leftSleeve.xRot = -120.0F;
      PLAYER_MODEL.rightArm.xRot = -120.0F;
      PLAYER_MODEL.rightSleeve.xRot = -120.0F;
      PLAYER_MODEL.setSlim(skin.isSlim());
      int wobble = tileEntity.getWobbleTime();
      if (wobble != 0) {
         float w = wobble + (1.0F - partialTicks);
         float angle = (float)(0.1F * Mth.sin(w) * (Math.exp(0.05 * w) - 1.0));
         PLAYER_MODEL.head.zRot = angle;
         PLAYER_MODEL.hat.zRot = angle;
      } else {
         PLAYER_MODEL.head.zRot = 0.0F;
         PLAYER_MODEL.hat.zRot = 0.0F;
      }

      VertexConsumer vertexBuilder = buffer.getBuffer(renderType);
      float scale = 1.25F;
      float statueOffset = -0.125F;
      matrixStack.pushPose();
      matrixStack.translate(0.0, 0.0, statueOffset);
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      matrixStack.scale(scale, scale, scale);
      matrixStack.translate(0.0, -1.5, 0.0);
      PLAYER_MODEL.renderToBuffer(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
      if (buffer instanceof BufferSource) {
         ((BufferSource)buffer).endBatch(renderType);
      }
   }

   private void renderItemWithLabel(
      ItemStack stack, Player player, PoseStack matrixStack, MultiBufferSource buffer, int combinedOverlay, int lightLevel, TextComponent text, int color
   ) {
      matrixStack.pushPose();
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(-player.getYRot()));
      matrixStack.scale(0.5F, 0.5F, 0.5F);
      this.itemRenderer.renderStatic(stack, TransformType.GROUND, lightLevel, combinedOverlay, matrixStack, buffer, 0);
      float scale = -0.025F;
      float offset = -Minecraft.getInstance().font.width(text) / 2;
      matrixStack.translate(0.0, 0.75, 0.0);
      matrixStack.scale(scale, scale, scale);
      Matrix4f matrix4f = matrixStack.last().pose();
      Minecraft.getInstance().font.drawInBatch(text, offset, 0.0F, color, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.popPose();
   }

   private int getLightAtPos(Level world, BlockPos pos) {
      int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
      int skyLight = world.getBrightness(LightLayer.SKY, pos);
      return LightTexture.pack(blockLight, skyLight);
   }

   private static void renderBlockState(
      BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos
   ) {
      try {
         for (RenderType type : RenderType.chunkBufferLayers()) {
            if (ItemBlockRenderTypes.canRenderInLayer(state, type)) {
               renderBlockState(state, matrixStack, buffer, blockRenderer, world, pos, type);
            }
         }
      } catch (Exception var8) {
      }
   }

   public static void renderBlockState(
      BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos, RenderType type
   ) {
      ForgeHooksClient.setRenderType(type);
      blockRenderer.getModelRenderer()
         .tesselateBlock(
            world, blockRenderer.getBlockModel(state), state, pos, matrixStack, buffer.getBuffer(type), false, world.random, 0L, OverlayTexture.NO_OVERLAY
         );
      ForgeHooksClient.setRenderType(null);
   }
}
