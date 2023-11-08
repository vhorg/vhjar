package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.block.entity.ArtifactProjectorTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;

public class ArtifactProjectorRenderer implements BlockEntityRenderer<ArtifactProjectorTileEntity> {
   public static final ResourceLocation BOOK_TEXTURE = VaultMod.id("entity/vault_projector_book");
   public static final Material BOOK_MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, BOOK_TEXTURE);
   private final BookModel bookModel;

   public ArtifactProjectorRenderer(Context context) {
      this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
   }

   public void render(
      ArtifactProjectorTileEntity tile,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = tile.getLevel();
      if (world != null) {
         Direction dir = (Direction)tile.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

         Direction usedDir = switch (dir) {
            case SOUTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case WEST -> Direction.SOUTH;
            default -> Direction.EAST;
         };
         int rot = 90;
         if (dir == Direction.WEST) {
            rot = 0;
         }

         if (dir == Direction.SOUTH) {
            rot = 270;
         }

         if (dir == Direction.EAST) {
            rot = 180;
         }

         matrixStack.pushPose();
         matrixStack.translate(0.5, 1.05, 0.5);
         float f = tile.time + partialTicks;
         matrixStack.mulPose(Vector3f.YP.rotation(-((float)Math.toRadians(rot))));
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees(66.0F));
         float f3 = Mth.lerp(partialTicks, tile.oFlip, tile.flip);
         float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
         float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
         float f6 = Mth.lerp(partialTicks, tile.oOpen, tile.open);
         this.bookModel.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
         VertexConsumer vertexconsumer = BOOK_MATERIAL.buffer(buffer, RenderType::entitySolid);
         this.bookModel.render(matrixStack, vertexconsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.popPose();
         if (tile.completed) {
            for (int y = 1; y < 6; y++) {
               for (int x = 1; x < 6; x++) {
                  ArtifactProjectorRenderer.TransparentRenderType newBuffer = new ArtifactProjectorRenderer.TransparentRenderType(
                     Minecraft.getInstance().renderBuffers().bufferSource(), 1.0F, null
                  );
                  int value = x + (y - 1) * 5;
                  int order = (25 - value + 24) % 25;
                  BlockPos pos = tile.getBlockPos().above(6 - y).relative(usedDir, 3 - x);
                  if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                     pos = tile.getBlockPos().above(6 - y).relative(usedDir, -3 + x);
                  }

                  BlockState artifact = (BlockState)((BlockState)((BlockState)ModBlocks.VAULT_ARTIFACT
                           .defaultBlockState()
                           .setValue(VaultArtifactBlock.ORDER_PROPERTY, order + 1))
                        .setValue(VaultArtifactBlock.GRAYSCALE, false))
                     .setValue(HorizontalDirectionalBlock.FACING, dir);
                  matrixStack.pushPose();
                  if (dir == Direction.EAST) {
                     matrixStack.translate(-0.015625, 6 - y, -3 + x);
                  }

                  if (dir == Direction.NORTH) {
                     matrixStack.translate(-3 + x, 6 - y, 0.015625);
                  }

                  if (dir == Direction.WEST) {
                     matrixStack.translate(0.015625, 6 - y, 3 - x);
                  }

                  if (dir == Direction.SOUTH) {
                     matrixStack.translate(3 - x, 6 - y, -0.015625);
                  }

                  renderBlockState(artifact, matrixStack, newBuffer, Minecraft.getInstance().getBlockRenderer(), tile.getLevel(), pos);
                  matrixStack.popPose();
               }
            }
         } else {
            for (int y = 1; y < 6; y++) {
               for (int x = 1; x < 6; x++) {
                  ArtifactProjectorRenderer.TransparentRenderType newBufferx = new ArtifactProjectorRenderer.TransparentRenderType(
                     Minecraft.getInstance().renderBuffers().bufferSource(), 0.5F, null
                  );
                  int valuex = x + (y - 1) * 5;
                  int orderx = (25 - valuex + 24) % 25;
                  BlockPos posx = tile.getBlockPos().above(6 - y).relative(usedDir, 3 - x);
                  if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                     posx = tile.getBlockPos().above(6 - y).relative(usedDir, -3 + x);
                  }

                  BlockState blockState = tile.getLevel().getBlockState(posx);
                  BlockState artifactx = (BlockState)((BlockState)((BlockState)ModBlocks.VAULT_ARTIFACT
                           .defaultBlockState()
                           .setValue(VaultArtifactBlock.ORDER_PROPERTY, orderx + 1))
                        .setValue(VaultArtifactBlock.GRAYSCALE, true))
                     .setValue(HorizontalDirectionalBlock.FACING, dir);
                  if (blockState.hasProperty(VaultArtifactBlock.ORDER_PROPERTY)) {
                     if ((Integer)blockState.getValue(VaultArtifactBlock.ORDER_PROPERTY) == orderx + 1
                        && blockState.getValue(HorizontalDirectionalBlock.FACING) == dir) {
                        continue;
                     }

                     newBufferx = new ArtifactProjectorRenderer.TransparentRenderType(
                        Minecraft.getInstance().renderBuffers().bufferSource(), 0.5F, new Vec3(0.8F, 0.0, 0.0)
                     );
                  }

                  matrixStack.pushPose();
                  if (dir == Direction.EAST) {
                     matrixStack.translate(-0.015625, 6 - y, -3 + x);
                  }

                  if (dir == Direction.NORTH) {
                     matrixStack.translate(-3 + x, 6 - y, 0.015625);
                  }

                  if (dir == Direction.WEST) {
                     matrixStack.translate(0.015625, 6 - y, 3 - x);
                  }

                  if (dir == Direction.SOUTH) {
                     matrixStack.translate(3 - x, 6 - y, -0.015625);
                  }

                  renderBlockState(artifactx, matrixStack, newBufferx, Minecraft.getInstance().getBlockRenderer(), tile.getLevel(), posx);
                  matrixStack.popPose();
               }
            }
         }
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

   public static class TransparentRenderType implements MultiBufferSource {
      private final float alpha;
      private final Vec3 color;
      private final MultiBufferSource source;

      public TransparentRenderType(MultiBufferSource source, float alpha, Vec3 color) {
         this.source = source;
         this.alpha = alpha;
         this.color = color;
      }

      public VertexConsumer getBuffer(RenderType type) {
         RenderType renderType = type;
         if (type instanceof CompositeRenderType compositeRenderType) {
            ResourceLocation texture = ((TextureStateShard)compositeRenderType.state.textureState).texture.orElse(InventoryMenu.BLOCK_ATLAS);
            renderType = RenderType.entityTranslucentCull(texture);
         } else if (type.toString().equals(Sheets.translucentCullBlockSheet().toString())) {
            renderType = Sheets.translucentCullBlockSheet();
         }

         return new ArtifactProjectorRenderer.TransparentRenderVertex(this.source.getBuffer(renderType), this.alpha, this.color);
      }
   }

   public static class TransparentRenderVertex implements VertexConsumer {
      private final VertexConsumer source;
      private final float alpha;
      private final Vec3 color;

      public TransparentRenderVertex(VertexConsumer source, float alpha, Vec3 color) {
         this.source = source;
         this.alpha = alpha;
         this.color = color;
      }

      public VertexConsumer vertex(double x, double y, double z) {
         return this.source.vertex(x, y, z);
      }

      public VertexConsumer vertex(Matrix4f matrixIn, float x, float y, float z) {
         return this.source.vertex(matrixIn, x, y, z);
      }

      public VertexConsumer color(int red, int green, int blue, int alpha) {
         return this.color != null
            ? this.source.color((int)(this.color.x * 256.0), (int)(this.color.y * 256.0), (int)(this.color.z * 256.0), (int)(alpha * this.alpha))
            : this.source.color(red, green, blue, (int)(alpha * this.alpha));
      }

      public VertexConsumer uv(float u, float v) {
         return this.source.uv(u, v);
      }

      public VertexConsumer overlayCoords(int u, int v) {
         return this.source.overlayCoords(u, v);
      }

      public VertexConsumer uv2(int u, int v) {
         return this.source.uv2(u, v);
      }

      public VertexConsumer normal(float x, float y, float z) {
         return this.source.normal(x, y, z);
      }

      public VertexConsumer normal(Matrix3f matrixIn, float x, float y, float z) {
         return this.source.normal(matrixIn, x, y, z);
      }

      public void endVertex() {
         this.source.endVertex();
      }

      public void defaultColor(int p_166901_, int p_166902_, int p_166903_, int p_166904_) {
         this.source.defaultColor(p_166901_, p_166902_, p_166903_, p_166904_);
      }

      public void unsetDefaultColor() {
         this.source.unsetDefaultColor();
      }
   }
}
