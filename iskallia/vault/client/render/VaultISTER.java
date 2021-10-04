package iskallia.vault.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.block.ScavengerChestBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.ScavengerChestTileEntity;
import iskallia.vault.block.entity.VaultChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class VaultISTER extends ItemStackTileEntityRenderer {
   public static final VaultISTER INSTANCE = new VaultISTER();

   private VaultISTER() {
   }

   public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
      World world = Minecraft.func_71410_x().field_71441_e;
      if (world != null && stack.func_77973_b() instanceof BlockItem) {
         Block block = ((BlockItem)stack.func_77973_b()).func_179223_d();
         if (block instanceof VaultChestBlock) {
            TileEntity te = ((VaultChestBlock)block).func_196283_a_(world);
            if (te instanceof VaultChestTileEntity) {
               ((VaultChestTileEntity)te).setRenderState(block.func_176223_P());
               TileEntityRendererDispatcher.field_147556_a.func_228852_a_(te, matrixStack, buffer, combinedLight, combinedOverlay);
            }
         }

         if (block instanceof ScavengerChestBlock) {
            TileEntity te = ((ScavengerChestBlock)block).func_196283_a_(world);
            if (te instanceof ScavengerChestTileEntity) {
               TileEntityRendererDispatcher.field_147556_a.func_228852_a_(te, matrixStack, buffer, combinedLight, combinedOverlay);
            }
         }
      }
   }
}
