package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.IdentificationStandBlock;
import iskallia.vault.block.entity.IdentificationStandTileEntity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.item.IdentifiableItem;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class IdentificationStandRenderer implements BlockEntityRenderer<IdentificationStandTileEntity> {
   public static final ResourceLocation BOOK_TEXTURE = VaultMod.id("entity/identification_tome");
   public static final Material BOOK_MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, BOOK_TEXTURE);
   private final BookModel bookModel;
   private final ItemRenderer itemRenderer;
   private List<ItemStack> cachedItemStacks = new LinkedList<>();

   public IdentificationStandRenderer(Context context) {
      Minecraft minecraft = Minecraft.getInstance();
      this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
      this.itemRenderer = minecraft.getItemRenderer();
   }

   public void render(
      @Nonnull IdentificationStandTileEntity tileEntity,
      float pPartialTick,
      @Nonnull PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      Direction facingDirection = (Direction)tileEntity.getBlockState().getValue(IdentificationStandBlock.FACING);
      poseStack.pushPose();
      poseStack.translate(0.5, 1.01, 0.5);
      float f = tileEntity.time + pPartialTick;
      poseStack.translate(facingDirection.getOpposite().getStepX() * 0.25, 0.3F + Mth.sin(f * 0.1F) * 0.01F, facingDirection.getOpposite().getStepZ() * 0.25);
      float f1 = tileEntity.rot - tileEntity.oRot;

      while (f1 >= (float) Math.PI) {
         f1 -= (float) (Math.PI * 2);
      }

      while (f1 < (float) -Math.PI) {
         f1 += (float) (Math.PI * 2);
      }

      float f2 = tileEntity.oRot + f1 * pPartialTick;
      poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F + facingDirection.getRotation().toYXZDegrees().y()));
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(65.0F));
      float f3 = Mth.lerp(pPartialTick, tileEntity.oFlip, tileEntity.flip);
      float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
      float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
      float f6 = Mth.lerp(pPartialTick, tileEntity.oOpen, tileEntity.open);
      this.bookModel.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
      VertexConsumer vertexconsumer = BOOK_MATERIAL.buffer(bufferSource, RenderType::entitySolid);
      this.bookModel.render(poseStack, vertexconsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();
      if (tileEntity.open != 0.0F) {
         BlockPos pos = tileEntity.getBlockPos();
         Player player = tileEntity.getLevel().getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0, false);
         double sec = System.currentTimeMillis() / 1000.0;
         if (player != null) {
            this.cachedItemStacks = this.getIdentifiableStacks(player.getInventory());
         }

         if (this.cachedItemStacks.isEmpty()) {
            return;
         }

         ItemStack identifiableStack = this.cachedItemStacks.get((int)sec / 2 % this.cachedItemStacks.size());
         poseStack.pushPose();
         poseStack.translate(
            0.5 + facingDirection.getOpposite().getStepX() * 0.3, 1.3 + 0.5 * f6 + Math.sin(sec) * 0.05F, 0.5 + facingDirection.getOpposite().getStepZ() * 0.3
         );
         float scl = (float)(0.53 * f6);
         poseStack.scale(scl, scl, scl);
         poseStack.mulPose(Vector3f.YP.rotation(-f2));
         poseStack.mulPose(Vector3f.YP.rotationDegrees((float)(90.0F * f6 + 20.0 * (sec % 360.0))));
         this.itemRenderer.renderStatic(identifiableStack, TransformType.FIXED, packedLight, packedOverlay, poseStack, bufferSource, 0);
         poseStack.popPose();
      }
   }

   private List<ItemStack> getIdentifiableStacks(Inventory inventory) {
      return inventory.items
         .stream()
         .filter(
            itemStack -> itemStack.getItem() instanceof IdentifiableItem identifiableItem
               && identifiableItem.getState(itemStack) == VaultGearState.UNIDENTIFIED
         )
         .<ItemStack>map(ItemStack::copy)
         .toList();
   }
}
