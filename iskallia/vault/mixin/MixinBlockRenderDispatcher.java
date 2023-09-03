package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({BlockRenderDispatcher.class})
public class MixinBlockRenderDispatcher {
   @Shadow
   @Final
   private BlockModelShaper blockModelShaper;

   @Redirect(
      method = {"renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraftforge/client/model/data/IModelData;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"
      )
   )
   public RenderShape getRenderShape(BlockState instance) {
      return instance.getBlock() instanceof LiquidBlock ? RenderShape.MODEL : instance.getRenderShape();
   }

   @Redirect(
      method = {"renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraftforge/client/model/data/IModelData;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z",
         remap = false
      ),
      remap = false
   )
   public boolean renderBreakingTexture(
      ModelBlockRenderer renderer,
      BlockAndTintGetter getter,
      BakedModel model,
      BlockState state,
      BlockPos pos,
      PoseStack matrices,
      VertexConsumer consumer,
      boolean checkSides,
      Random random,
      long seed,
      int overlay,
      IModelData modelData
   ) {
      if (state.getBlock() instanceof LiquidBlock) {
         state = Blocks.STONE.defaultBlockState();
         model = this.blockModelShaper.getBlockModel(state);
      }

      return renderer.tesselateBlock(getter, model, state, pos, matrices, consumer, checkSides, random, seed, overlay, modelData);
   }
}
