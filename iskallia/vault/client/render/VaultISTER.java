package iskallia.vault.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;

public class VaultISTER extends BlockEntityWithoutLevelRenderer {
   private final BlockEntityRenderDispatcher blockEntityRenderer;
   private final Map<Block, BlockEntity> CHEST_INSTANCES = new HashMap<>();
   public static final IItemRenderProperties INSTANCE = new IItemRenderProperties() {
      final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
         () -> new VaultISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
      );

      public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
         return (BlockEntityWithoutLevelRenderer)this.renderer.get();
      }
   };

   public VaultISTER(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
      super(pBlockEntityRenderDispatcher, pEntityModelSet);
      this.blockEntityRenderer = pBlockEntityRenderDispatcher;
   }

   public void renderByItem(ItemStack stack, TransformType type, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      if (stack.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         if (block instanceof EntityBlock) {
            BlockEntity instance = this.CHEST_INSTANCES
               .computeIfAbsent(block, b -> ((EntityBlock)block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState()));
            if (instance != null) {
               this.blockEntityRenderer.renderItem(instance, matrixStack, buffer, combinedLight, combinedOverlay);
            }
         }
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
