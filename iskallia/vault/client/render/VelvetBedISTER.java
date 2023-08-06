package iskallia.vault.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.VelvetBedTileEntity;
import iskallia.vault.init.ModBlocks;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;

public class VelvetBedISTER extends BlockEntityWithoutLevelRenderer {
   private final BlockEntityRenderDispatcher blockEntityRenderer;
   private final VelvetBedTileEntity bed = new VelvetBedTileEntity(BlockPos.ZERO, ModBlocks.VELVET_BED.defaultBlockState());
   public static final IItemRenderProperties INSTANCE = new IItemRenderProperties() {
      final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
         () -> new VelvetBedISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
      );

      public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
         return (BlockEntityWithoutLevelRenderer)this.renderer.get();
      }
   };

   public VelvetBedISTER(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
      super(pBlockEntityRenderDispatcher, pEntityModelSet);
      this.blockEntityRenderer = pBlockEntityRenderDispatcher;
   }

   public void renderByItem(ItemStack stack, TransformType type, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
      if (stack.getItem() instanceof BedItem) {
         this.blockEntityRenderer.renderItem(this.bed, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
      }
   }

   public static void registerISTER(
      Consumer<IItemRenderProperties> consumer, final BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory
   ) {
      consumer.accept(
         new IItemRenderProperties() {
            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
               () -> factory.apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
            );

            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
               return (BlockEntityWithoutLevelRenderer)this.renderer.get();
            }
         }
      );
   }
}
