package iskallia.vault.block;

import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultStart;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class HourglassBlock extends Block {
   private static final Random rand = new Random();
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.field_208163_P;

   public HourglassBlock() {
      super(
         Properties.func_200949_a(Material.field_151592_s, MaterialColor.field_151650_B)
            .func_226896_b_()
            .func_235861_h_()
            .harvestTool(ToolType.AXE)
            .harvestLevel(1)
            .func_200948_a(3.0F, 3600000.0F)
      );
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(HALF, DoubleBlockHalf.LOWER));
   }

   public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
      if (world instanceof ServerWorld) {
         VaultRaid vault = VaultRaidData.get((ServerWorld)world).getAt((ServerWorld)world, pos);
         Optional<TreasureHuntObjective> opt = vault.getActiveObjective(TreasureHuntObjective.class);
         if (opt.isPresent()) {
            Collection<VaultStart> rooms = vault.getGenerator().getPiecesAt(pos, VaultStart.class);
            if (!rooms.isEmpty()) {
               return false;
            }
         }
      }

      return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
   }

   public boolean hasTileEntity(BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.HOURGLASS_TILE_ENTITY.func_200968_a() : null;
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (world.func_201670_d()) {
         return ActionResultType.SUCCESS;
      } else if (state.func_177229_b(HALF) == DoubleBlockHalf.UPPER) {
         BlockState down = world.func_180495_p(pos.func_177977_b());
         return down.func_227031_a_(world, player, hand, hit.func_237485_a_(pos.func_177977_b()));
      } else {
         ItemStack interacted = player.func_184586_b(hand);
         if (ModItems.VAULT_SAND.equals(interacted.func_77973_b())) {
            VaultRaid vault = VaultRaidData.get((ServerWorld)world).getAt((ServerWorld)world, pos);
            if (vault != null) {
               CompoundNBT sandNBT = interacted.func_77978_p();
               if (sandNBT == null) {
                  return ActionResultType.SUCCESS;
               }

               UUID vaultId = sandNBT.func_186857_a("vault_id");
               if (!vaultId.equals(vault.getProperties().getValue(VaultRaid.IDENTIFIER))) {
                  return ActionResultType.SUCCESS;
               }
            }

            TileEntity te = world.func_175625_s(pos);
            if (te instanceof HourglassTileEntity) {
               HourglassTileEntity hourglass = (HourglassTileEntity)te;
               if (hourglass.addSand(player, 1)) {
                  if (!player.func_184812_l_()) {
                     interacted.func_190918_g(1);
                  }

                  if (hourglass.getFilledPercentage() >= 1.0F) {
                     this.playFullEffects(world, pos);
                  } else {
                     world.func_184148_a(
                        null,
                        player.func_226277_ct_(),
                        player.func_226278_cu_(),
                        player.func_226281_cx_(),
                        SoundEvents.field_187747_eB,
                        SoundCategory.BLOCKS,
                        0.6F,
                        1.0F
                     );
                  }
               }
            }
         }

         return ActionResultType.SUCCESS;
      }
   }

   private void playFullEffects(World world, BlockPos pos) {
      for (int i = 0; i < 30; i++) {
         Vector3d offset = MiscUtils.getRandomOffset(pos, rand, 2.0F);
         ((ServerWorld)world)
            .func_195598_a(ParticleTypes.field_197632_y, offset.field_72450_a, offset.field_72448_b, offset.field_72449_c, 3, 0.0, 0.0, 0.0, 1.0);
      }

      world.func_184133_a(null, pos, SoundEvents.field_187802_ec, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockPos pos = context.func_195995_a();
      World world = context.func_195991_k();
      return World.func_175701_a(pos) && world.func_180495_p(pos.func_177984_a()).func_196953_a(context)
         ? (BlockState)this.func_176223_P().func_206870_a(HALF, DoubleBlockHalf.LOWER)
         : null;
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{HALF});
   }

   public void func_176208_a(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!worldIn.func_201670_d() && player.func_184812_l_()) {
         DoubleBlockHalf half = (DoubleBlockHalf)state.func_177229_b(HALF);
         if (half == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.func_177977_b();
            BlockState blockstate = worldIn.func_180495_p(blockpos);
            if (blockstate.func_177230_c() == state.func_177230_c() && blockstate.func_177229_b(HALF) == DoubleBlockHalf.LOWER) {
               worldIn.func_180501_a(blockpos, Blocks.field_150350_a.func_176223_P(), 35);
               worldIn.func_217378_a(player, 2001, blockpos, Block.func_196246_j(blockstate));
            }
         }
      }

      super.func_176208_a(worldIn, pos, state, player);
   }

   public BlockState func_196271_a(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      DoubleBlockHalf half = (DoubleBlockHalf)state.func_177229_b(HALF);
      if (facing.func_176740_k() == Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
         return facingState.func_203425_a(this) && facingState.func_177229_b(HALF) != half ? state : Blocks.field_150350_a.func_176223_P();
      } else {
         return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.func_196955_c(worldIn, currentPos)
            ? Blocks.field_150350_a.func_176223_P()
            : super.func_196271_a(state, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      worldIn.func_180501_a(pos.func_177984_a(), (BlockState)state.func_206870_a(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public void func_196243_a(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.func_203425_a(newState.func_177230_c()) || !newState.hasTileEntity()) {
         TileEntity te = getBlockTileEntity(world, pos, state);
         if (te instanceof HourglassTileEntity && state.func_177229_b(HALF) == DoubleBlockHalf.LOWER) {
            ItemStack stack = new ItemStack(ModBlocks.HOURGLASS);
            stack.func_196082_o().func_218657_a("BlockEntityTag", te.serializeNBT());
            Block.func_180635_a(world, pos, stack);
         }
      }

      super.func_196243_a(state, world, pos, newState, isMoving);
   }

   public static BlockPos getTileEntityPos(BlockState state, BlockPos pos) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.UPPER ? pos.func_177977_b() : pos;
   }

   public static TileEntity getBlockTileEntity(World world, BlockPos pos, BlockState state) {
      BlockPos vendingMachinePos = getTileEntityPos(state, pos);
      return world.func_175625_s(vendingMachinePos);
   }
}
