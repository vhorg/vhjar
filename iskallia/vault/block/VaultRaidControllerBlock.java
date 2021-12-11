package iskallia.vault.block;

import com.google.common.collect.Lists;
import iskallia.vault.block.entity.VaultRaidControllerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.VoxelUtils;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultRaidControllerBlock extends Block {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.field_208163_P;
   private static final VoxelShape SHAPE_TOP = makeShape().func_197751_a(0.0, -1.0, 0.0);
   private static final VoxelShape SHAPE_BOTTOM = makeShape();

   public VaultRaidControllerBlock() {
      super(
         Properties.func_200945_a(Material.field_151592_s)
            .func_200947_a(SoundType.field_185853_f)
            .func_200948_a(-1.0F, 3600000.0F)
            .func_200942_a()
            .func_226896_b_()
            .func_222380_e()
      );
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(HALF, DoubleBlockHalf.LOWER));
   }

   private static VoxelShape makeShape() {
      VoxelShape m1 = Block.func_208617_a(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
      VoxelShape m2 = Block.func_208617_a(2.0, 2.0, 2.0, 14.0, 29.0, 14.0);
      return VoxelUtils.combineAll(IBooleanFunction.field_223244_o_, m1, m2);
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE_BOTTOM;
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (state.func_177229_b(HALF) == DoubleBlockHalf.UPPER) {
         BlockState downState = world.func_180495_p(pos.func_177977_b());
         return !(downState.func_177230_c() instanceof VaultRaidControllerBlock)
            ? ActionResultType.SUCCESS
            : this.func_225533_a_(downState, world, pos.func_177977_b(), player, hand, hit);
      } else if (!world.func_201670_d() && world instanceof ServerWorld && hand == Hand.MAIN_HAND) {
         this.startRaid((ServerWorld)world, pos);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.SUCCESS;
      }
   }

   private void startRaid(ServerWorld world, BlockPos pos) {
      VaultRaid vault = VaultRaidData.get(world).getAt(world, pos);
      if (vault != null && vault.getActiveRaid() == null) {
         TileEntity tile = world.func_175625_s(pos);
         if (tile instanceof VaultRaidControllerTileEntity) {
            VaultRaidControllerTileEntity ctrl = (VaultRaidControllerTileEntity)tile;
            if (!ctrl.didTriggerRaid() && vault.triggerRaid(world, pos)) {
               ctrl.setTriggeredRaid(true);
            }
         }
      }
   }

   public void func_196243_a(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
      super.func_196243_a(state, world, pos, newState, isMoving);
      if (!state.func_203425_a(newState.func_177230_c())) {
         if (state.func_177229_b(HALF) == DoubleBlockHalf.UPPER) {
            BlockState otherState = world.func_180495_p(pos.func_177977_b());
            if (otherState.func_203425_a(state.func_177230_c())) {
               world.func_217377_a(pos.func_177977_b(), isMoving);
            }
         } else {
            BlockState otherState = world.func_180495_p(pos.func_177984_a());
            if (otherState.func_203425_a(state.func_177230_c())) {
               world.func_217377_a(pos.func_177984_a(), isMoving);
            }
         }
      }
   }

   public List<ItemStack> func_220076_a(BlockState state, net.minecraft.loot.LootContext.Builder builder) {
      return Lists.newArrayList();
   }

   public boolean hasTileEntity(BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return this.hasTileEntity(state) ? ModBlocks.RAID_CONTROLLER_TILE_ENTITY.func_200968_a() : null;
   }

   public BlockRenderType func_149645_b(BlockState state) {
      return BlockRenderType.MODEL;
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{HALF});
   }
}
