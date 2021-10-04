package iskallia.vault.block;

import iskallia.vault.block.entity.VaultTreasureChestTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class VaultTreasureChestBlock extends VaultChestBlock {
   public VaultTreasureChestBlock(Properties builder) {
      super(builder, () -> ModBlocks.VAULT_TREASURE_CHEST_TILE_ENTITY);
   }

   @Nullable
   public INamedContainerProvider func_220052_b(BlockState state, World world, BlockPos pos) {
      final TileEntity te = world.func_175625_s(pos);
      if (!(te instanceof VaultTreasureChestTileEntity)) {
         return null;
      } else {
         final VaultTreasureChestTileEntity chest = (VaultTreasureChestTileEntity)te;
         return new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return ((VaultTreasureChestTileEntity)te).func_145748_c_();
            }

            @Nullable
            public Container createMenu(int containerId, PlayerInventory playerInventory, PlayerEntity player) {
               if (chest.func_213904_e(player)) {
                  chest.func_184281_d(player);
                  return ChestContainer.func_216984_b(containerId, playerInventory, chest);
               } else {
                  return null;
               }
            }
         };
      }
   }

   @Override
   public TileEntity func_196283_a_(IBlockReader world) {
      return new VaultTreasureChestTileEntity();
   }
}
