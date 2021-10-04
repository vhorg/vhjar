package iskallia.vault.block;

import iskallia.vault.container.TransmogTableContainer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class TransmogTableBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.field_185512_D;

   public TransmogTableBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_235861_h_().func_200943_b(0.5F).func_235838_a_(state -> 1).func_226896_b_());
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f().func_176734_d());
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (world.field_72995_K) {
         return ActionResultType.SUCCESS;
      } else {
         NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return new StringTextComponent("Transmogrification Table");
            }

            @Nullable
            public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity playerx) {
               return new TransmogTableContainer(windowId, playerx);
            }
         });
         return ActionResultType.SUCCESS;
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
   public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
      return state.func_185909_g(reader, pos).field_76291_p;
   }
}
