package iskallia.vault.block;

import iskallia.vault.block.entity.PlayerStatueTileEntity;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.RenameType;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class PlayerStatueBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.field_208157_J;
   public static final VoxelShape SHAPE = Block.func_208617_a(1.0, 0.0, 1.0, 15.0, 5.0, 15.0);

   public PlayerStatueBlock() {
      super(Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(1.0F, 3600000.0F).func_226896_b_().func_200942_a());
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.SOUTH));
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f());
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.PLAYER_STATUE_TILE_ENTITY.func_200968_a();
   }

   public void func_176208_a(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!world.field_72995_K) {
         TileEntity tileEntity = world.func_175625_s(pos);
         ItemStack itemStack = new ItemStack(this.getBlock());
         if (tileEntity instanceof PlayerStatueTileEntity) {
            PlayerStatueTileEntity statueTileEntity = (PlayerStatueTileEntity)tileEntity;
            CompoundNBT statueNBT = statueTileEntity.serializeNBT();
            CompoundNBT stackNBT = new CompoundNBT();
            stackNBT.func_218657_a("BlockEntityTag", statueNBT);
            itemStack.func_77982_d(stackNBT);
         }

         ItemEntity itemEntity = new ItemEntity(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, itemStack);
         itemEntity.func_174869_p();
         world.func_217376_c(itemEntity);
      }

      super.func_176208_a(world, pos, state, player);
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (world.field_72995_K) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity te = world.func_175625_s(pos);
         if (!(te instanceof PlayerStatueTileEntity)) {
            return ActionResultType.SUCCESS;
         } else {
            PlayerStatueTileEntity statue = (PlayerStatueTileEntity)te;
            final CompoundNBT nbt = new CompoundNBT();
            nbt.func_74768_a("RenameType", RenameType.PLAYER_STATUE.ordinal());
            nbt.func_218657_a("Data", statue.serializeNBT());
            NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
               public ITextComponent func_145748_c_() {
                  return new StringTextComponent("Player Statue");
               }

               @Nullable
               public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                  return new RenamingContainer(windowId, nbt);
               }
            }, buffer -> buffer.func_150786_a(nbt));
            return super.func_225533_a_(state, world, pos, player, handIn, hit);
         }
      }
   }
}
