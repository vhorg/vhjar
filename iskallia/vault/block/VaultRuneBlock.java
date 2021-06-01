package iskallia.vault.block;

import iskallia.vault.block.entity.VaultRuneTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultRuneBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.field_208157_J;
   public static final BooleanProperty RUNE_PLACED = BooleanProperty.func_177716_a("rune_placed");

   public VaultRuneBlock() {
      super(Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(Float.MAX_VALUE, Float.MAX_VALUE).func_226896_b_());
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.SOUTH)).func_206870_a(RUNE_PLACED, false)
      );
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f())).func_206870_a(RUNE_PLACED, false);
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
      builder.func_206894_a(new Property[]{RUNE_PLACED});
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_RUNE_TILE_ENTITY.func_200968_a();
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (!world.field_72995_K) {
         TileEntity tileEntity = world.func_175625_s(pos);
         if (tileEntity instanceof VaultRuneTileEntity) {
            VaultRuneTileEntity vaultRuneTE = (VaultRuneTileEntity)tileEntity;
            String playerNick = player.func_145748_c_().getString();
            if (vaultRuneTE.getBelongsTo().equals(playerNick)) {
               ItemStack heldStack = player.func_184586_b(hand);
               if (heldStack.func_77973_b() == ModItems.VAULT_RUNE) {
                  BlockState blockState = world.func_180495_p(pos);
                  world.func_180501_a(pos, (BlockState)blockState.func_206870_a(RUNE_PLACED, true), 3);
                  heldStack.func_190918_g(1);
                  ((ServerWorld)world)
                     .func_184148_a(
                        null, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), SoundEvents.field_193781_bp, SoundCategory.BLOCKS, 1.0F, 1.0F
                     );
               }
            } else {
               StringTextComponent text = new StringTextComponent(vaultRuneTE.getBelongsTo() + " is responsible with this block.");
               text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-26266)));
               player.func_146105_b(text, true);
            }
         }
      }

      return super.func_225533_a_(state, world, pos, player, hand, hit);
   }
}
