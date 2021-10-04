package iskallia.vault.block;

import iskallia.vault.block.entity.CatalystDecryptionTableTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

public class CatalystDecryptionTableBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.field_185512_D;

   public CatalystDecryptionTableBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200948_a(1.5F, 6.0F).func_226896_b_());
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f().func_176734_d());
   }

   public VoxelShape func_196247_c(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return LecternBlock.field_220161_f;
   }

   public VoxelShape func_220071_b(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return LecternBlock.field_220164_h;
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      switch ((Direction)state.func_177229_b(FACING)) {
         case NORTH:
            return LecternBlock.field_220166_j;
         case SOUTH:
            return LecternBlock.field_220163_w;
         case EAST:
            return LecternBlock.field_220167_k;
         case WEST:
            return LecternBlock.field_220165_i;
         default:
            return LecternBlock.field_220161_f;
      }
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (world.func_201670_d()) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity te = world.func_175625_s(pos);
         if (!(te instanceof CatalystDecryptionTableTileEntity)) {
            return ActionResultType.SUCCESS;
         } else if (!(player instanceof ServerPlayerEntity)) {
            return ActionResultType.SUCCESS;
         } else {
            NetworkHooks.openGui((ServerPlayerEntity)player, (CatalystDecryptionTableTileEntity)te, buffer -> buffer.func_179255_a(pos));
            return ActionResultType.SUCCESS;
         }
      }
   }

   public void func_196243_a(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.func_203425_a(newState.func_177230_c())) {
         TileEntity tileentity = worldIn.func_175625_s(pos);
         if (tileentity instanceof CatalystDecryptionTableTileEntity) {
            tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
               for (int i = 0; i < handler.getSlots(); i++) {
                  InventoryHelper.func_180173_a(worldIn, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), handler.getStackInSlot(i));
               }
            });
         }
      }

      super.func_196243_a(state, worldIn, pos, newState, isMoving);
   }

   public boolean func_220074_n(BlockState state) {
      return true;
   }

   public boolean func_196266_a(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   public BlockState func_185499_a(BlockState state, Rotation rot) {
      return (BlockState)state.func_206870_a(FACING, rot.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirrorIn) {
      return state.func_185907_a(mirrorIn.func_185800_a((Direction)state.func_177229_b(FACING)));
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.CATALYST_DECRYPTION_TABLE_TILE_ENTITY.func_200968_a();
   }
}
