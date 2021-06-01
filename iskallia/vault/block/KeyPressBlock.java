package iskallia.vault.block;

import iskallia.vault.container.KeyPressContainer;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeyPressBlock extends FallingBlock {
   public static final DirectionProperty FACING = HorizontalBlock.field_185512_D;
   private static final VoxelShape PART_BASE = Block.func_208617_a(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
   private static final VoxelShape PART_LOWER_X = Block.func_208617_a(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
   private static final VoxelShape PART_MID_X = Block.func_208617_a(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
   private static final VoxelShape PART_UPPER_X = Block.func_208617_a(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
   private static final VoxelShape PART_LOWER_Z = Block.func_208617_a(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
   private static final VoxelShape PART_MID_Z = Block.func_208617_a(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
   private static final VoxelShape PART_UPPER_Z = Block.func_208617_a(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
   private static final VoxelShape X_AXIS_AABB = VoxelShapes.func_216384_a(PART_BASE, new VoxelShape[]{PART_LOWER_X, PART_MID_X, PART_UPPER_X});
   private static final VoxelShape Z_AXIS_AABB = VoxelShapes.func_216384_a(PART_BASE, new VoxelShape[]{PART_LOWER_Z, PART_MID_Z, PART_UPPER_Z});

   public KeyPressBlock() {
      super(
         Properties.func_200949_a(Material.field_151574_g, MaterialColor.field_151668_h)
            .func_200947_a(SoundType.field_185858_k)
            .func_200948_a(2.0F, 3600000.0F)
      );
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f().func_176746_e());
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      Direction direction = (Direction)state.func_177229_b(FACING);
      return direction.func_176740_k() == Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (world.field_72995_K) {
         return ActionResultType.SUCCESS;
      } else {
         NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return new StringTextComponent("Key Press");
            }

            @Nullable
            public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity playerx) {
               return new KeyPressContainer(windowId, playerx);
            }
         }, buffer -> {});
         return ActionResultType.SUCCESS;
      }
   }

   protected void func_149829_a(FallingBlockEntity fallingEntity) {
      fallingEntity.func_145806_a(true);
   }

   public void func_176502_a_(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
      if (!fallingBlock.func_174814_R()) {
         worldIn.func_217379_c(1031, pos, 0);
      }
   }

   public void func_190974_b(World worldIn, BlockPos pos, FallingBlockEntity fallingBlock) {
      if (!fallingBlock.func_174814_R()) {
         worldIn.func_217379_c(1029, pos, 0);
      }
   }

   public BlockState func_185499_a(BlockState state, Rotation rot) {
      return (BlockState)state.func_206870_a(FACING, rot.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
   }

   public boolean func_196266_a(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_189876_x(BlockState state, IBlockReader reader, BlockPos pos) {
      return state.func_185909_g(reader, pos).field_76291_p;
   }

   public void func_196243_a(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!worldIn.field_72995_K) {
         if (newState.func_196958_f()) {
            ItemEntity entity = new ItemEntity(worldIn, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), new ItemStack(ModBlocks.KEY_PRESS));
            worldIn.func_217376_c(entity);
            super.func_196243_a(state, worldIn, pos, newState, isMoving);
         }
      }
   }
}
