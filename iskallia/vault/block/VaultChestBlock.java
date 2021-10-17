package iskallia.vault.block;

import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.init.ModBlocks;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class VaultChestBlock extends ChestBlock {
   protected VaultChestBlock(Properties builder, Supplier<TileEntityType<? extends ChestTileEntity>> tileSupplier) {
      super(builder, tileSupplier);
   }

   public VaultChestBlock(Properties builder) {
      this(builder, () -> ModBlocks.VAULT_CHEST_TILE_ENTITY);
   }

   public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
      TileEntity te = world.func_175625_s(pos);
      if (!(te instanceof VaultChestTileEntity) || player.func_184812_l_()) {
         return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
      } else if (this != ModBlocks.VAULT_BONUS_CHEST && this != ModBlocks.VAULT_COOP_CHEST) {
         return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
      } else {
         VaultChestTileEntity chest = (VaultChestTileEntity)te;
         if (chest.func_191420_l()) {
            return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
         } else {
            this.getBlock().func_176208_a(world, pos, state, player);
            return true;
         }
      }
   }

   public void func_180657_a(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (this != ModBlocks.VAULT_BONUS_CHEST && this != ModBlocks.VAULT_COOP_CHEST) {
         super.func_180657_a(world, player, pos, state, te, stack);
      } else {
         player.func_71029_a(Stats.field_188065_ae.func_199076_b(this));
         player.func_71020_j(0.005F);
         if (te instanceof VaultChestTileEntity) {
            VaultChestTileEntity chest = (VaultChestTileEntity)te;

            for (int slot = 0; slot < chest.func_70302_i_(); slot++) {
               ItemStack invStack = chest.func_70301_a(slot);
               if (!invStack.func_190926_b()) {
                  Block.func_180635_a(world, pos, invStack);
                  chest.func_70299_a(slot, ItemStack.field_190927_a);
                  break;
               }
            }
         }
      }
   }

   public TileEntity func_196283_a_(IBlockReader world) {
      return new VaultChestTileEntity();
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockState state = super.func_196258_a(context);
      return state == null ? null : (BlockState)state.func_206870_a(field_196314_b, ChestType.SINGLE);
   }
}
