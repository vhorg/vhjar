package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.FoliageDecorTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;

public class FoliageDecorRenderer implements BlockEntityRenderer<FoliageDecorTileEntity> {
   private final Minecraft mc = Minecraft.getInstance();
   private final BlockRenderDispatcher blockRenderer = this.mc.getBlockRenderer();

   public FoliageDecorRenderer(Context context) {
   }

   public void render(
      FoliageDecorTileEntity foliageDecor,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = foliageDecor.getLevel();
      if (world != null) {
         if (foliageDecor.getInventory().getItem(0).getItem() instanceof BlockItem blockItem) {
            matrixStack.pushPose();
            Vec3 offset = blockItem.getBlock().defaultBlockState().getOffset(world, foliageDecor.getBlockPos());
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (state.is(BlockTags.CROPS)) {
               matrixStack.translate(0.0, 0.0625, 0.0);
            }

            matrixStack.translate(-offset.x, -offset.y + 0.0625, -offset.z);
            renderBlockState(this.setMaxAgeBlockstate(blockItem), matrixStack, buffer, this.blockRenderer, foliageDecor.getLevel(), foliageDecor.getBlockPos());
            matrixStack.popPose();
            matrixStack.pushPose();
            if (blockItem.getBlock() instanceof TallFlowerBlock) {
               matrixStack.translate(-offset.x, 1.0 + -offset.y + 0.0625, -offset.z);
               renderBlockState(
                  (BlockState)blockItem.getBlock().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER),
                  matrixStack,
                  buffer,
                  this.blockRenderer,
                  foliageDecor.getLevel(),
                  foliageDecor.getBlockPos()
               );
            }

            matrixStack.popPose();
         }
      }
   }

   private BlockState setMaxAgeBlockstate(BlockItem state) {
      if (state.getBlock() instanceof BambooBlock) {
         return (BlockState)((BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_1, 1))
            .setValue(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.LARGE);
      } else if (state.getBlock() instanceof SmallDripleafBlock) {
         return (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
      } else if (state.getBlock().defaultBlockState().hasProperty(BlockStateProperties.AGE_1)) {
         return (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_1, 1);
      } else if (state.getBlock().defaultBlockState().hasProperty(BlockStateProperties.AGE_2)) {
         return (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_2, 2);
      } else if (state.getBlock().defaultBlockState().hasProperty(BlockStateProperties.AGE_3)) {
         return (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_3, 3);
      } else if (state.getBlock().defaultBlockState().hasProperty(BlockStateProperties.AGE_5)) {
         return (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_5, 5);
      } else if (state.getBlock().defaultBlockState().hasProperty(BlockStateProperties.AGE_7)) {
         return (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_7, 7);
      } else if (state.getBlock().defaultBlockState().hasProperty(BlockStateProperties.AGE_15)) {
         return (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_15, 15);
      } else {
         return state.getBlock().defaultBlockState().hasProperty(BlockStateProperties.AGE_25)
            ? (BlockState)state.getBlock().defaultBlockState().setValue(BlockStateProperties.AGE_25, 25)
            : state.getBlock().defaultBlockState();
      }
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
