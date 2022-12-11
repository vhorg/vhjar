package iskallia.vault.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.AnimalJarItem;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.util.NonNullLazy;

public class AnimalJarISTER extends BlockEntityWithoutLevelRenderer {
   private final BlockEntityRenderDispatcher blockEntityRenderer;
   public static final IItemRenderProperties INSTANCE = new IItemRenderProperties() {
      final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
         () -> new AnimalJarISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
      );

      public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
         return (BlockEntityWithoutLevelRenderer)this.renderer.get();
      }
   };

   public AnimalJarISTER(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
      super(pBlockEntityRenderDispatcher, pEntityModelSet);
      this.blockEntityRenderer = pBlockEntityRenderDispatcher;
   }

   public void renderByItem(ItemStack stack, TransformType type, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      if (stack.getItem() instanceof AnimalJarItem jarItem) {
         matrixStack.pushPose();
         Minecraft.getInstance()
            .getBlockRenderer()
            .renderSingleBlock(ModBlocks.ANIMAL_JAR.defaultBlockState(), matrixStack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
         matrixStack.popPose();
         if (AnimalJarItem.containsEntity(stack)) {
            Animal animal = AnimalJarItem.getAnimalFromItemStack(stack, Minecraft.getInstance().level);
            if (animal != null) {
               if (animal instanceof Sheep sheep) {
                  sheep.setSheared(stack.hasTag() && stack.getTag().contains("shearTimer"));
               }

               Minecraft.getInstance().getEntityRenderDispatcher().setRenderShadow(false);
               float yBodyRot = animal.yBodyRot;
               float yBodyRotO = animal.yBodyRotO;
               float yRot = animal.getYRot();
               float xRot = animal.getXRot();
               float yHeadRotO = animal.yHeadRotO;
               float yHeadRot = animal.yHeadRot;
               float scale = 0.25F / animal.getBbWidth();
               scale = 0.25F + (scale - 0.25F) / 2.0F;
               animal.yBodyRot = 180.0F;
               animal.yBodyRotO = 180.0F;
               animal.setYRot(180.0F);
               animal.setXRot(0.0F);
               animal.xRotO = 0.0F;
               animal.yHeadRot = animal.getYRot();
               animal.yHeadRotO = animal.getYRot();
               matrixStack.pushPose();
               matrixStack.scale(scale, scale, scale);
               Minecraft.getInstance()
                  .getEntityRenderDispatcher()
                  .render(
                     animal, 0.5F / scale, 0.03125F / scale, 0.5F / scale, 0.0F, Minecraft.getInstance().getFrameTime(), matrixStack, buffer, combinedLight
                  );
               matrixStack.popPose();
               animal.yBodyRot = yBodyRot;
               animal.yBodyRotO = yBodyRotO;
               animal.setYRot(yRot);
               animal.setXRot(xRot);
               animal.yHeadRotO = yHeadRotO;
               animal.yHeadRot = yHeadRot;
               Minecraft.getInstance().getEntityRenderDispatcher().setRenderShadow(true);
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
