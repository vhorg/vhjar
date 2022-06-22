package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.item.GodEssenceItem;
import iskallia.vault.world.data.PlayerFavourData;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GodEyeBlock extends FacedBlock {
   public static final BooleanProperty LIT = BooleanProperty.func_177716_a("lit");
   public static final VoxelShape NORTH_SHAPE = Block.func_208617_a(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
   public static final VoxelShape EAST_SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
   public static final VoxelShape SOUTH_SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
   public static final VoxelShape WEST_SHAPE = Block.func_208617_a(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected PlayerFavourData.VaultGodType godType;

   public GodEyeBlock(PlayerFavourData.VaultGodType godType) {
      super(
         Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m)
            .func_200948_a(-1.0F, 3.6E8F)
            .func_222380_e()
            .func_226896_b_()
            .func_200947_a(SoundType.field_185851_d)
      );
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.SOUTH)).func_206870_a(LIT, false)
      );
      this.godType = godType;
   }

   public PlayerFavourData.VaultGodType getGodType() {
      return this.godType;
   }

   public void func_149666_a(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
      super.func_149666_a(group, items);
   }

   @Override
   protected void func_206840_a(Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{LIT});
   }

   @Nonnull
   public VoxelShape func_220053_a(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
      switch ((Direction)state.func_177229_b(FACING)) {
         case EAST:
            return WEST_SHAPE;
         case NORTH:
            return SOUTH_SHAPE;
         case WEST:
            return EAST_SHAPE;
         default:
            return NORTH_SHAPE;
      }
   }

   @Nonnull
   public ActionResultType func_225533_a_(
      @Nonnull BlockState state,
      @Nonnull World world,
      @Nonnull BlockPos pos,
      @Nonnull PlayerEntity player,
      @Nonnull Hand hand,
      @Nonnull BlockRayTraceResult hit
   ) {
      if (!world.field_72995_K && !(Boolean)state.func_177229_b(LIT)) {
         ItemStack heldItem = player.func_184586_b(hand);
         Item item = heldItem.func_77973_b();
         if (item instanceof GodEssenceItem) {
            GodEssenceItem essenceItem = (GodEssenceItem)item;
            if (essenceItem.getGodType() == this.godType) {
               if (!player.func_184812_l_()) {
                  heldItem.func_190918_g(1);
               }

               BlockState newState = (BlockState)state.func_206870_a(LIT, true);
               world.func_180501_a(pos, newState, 3);
               world.func_175669_a(1038, pos, 0);
               ((ServerWorld)world)
                  .func_195598_a(
                     ParticleTypes.field_197598_I,
                     pos.func_177958_n() + 0.5F,
                     pos.func_177956_o() + 0.5F,
                     pos.func_177952_p() + 0.5F,
                     100,
                     0.0,
                     0.0,
                     0.0,
                     Math.PI / 2
                  );
            }
         }
      }

      return super.func_225533_a_(state, world, pos, player, hand, hit);
   }
}
