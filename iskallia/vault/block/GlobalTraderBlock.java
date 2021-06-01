package iskallia.vault.block;

import iskallia.vault.container.GlobalTraderContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.GlobalTraderData;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class GlobalTraderBlock extends VendingMachineBlock {
   public GlobalTraderBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h)
            .func_200948_a(-1.0F, 3600000.0F)
            .func_226896_b_()
            .func_200947_a(SoundType.field_185852_e)
      );
   }

   @Override
   public boolean hasTileEntity(BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
   }

   @Override
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.GLOBAL_TRADER_TILE_ENTITY.func_200968_a() : null;
   }

   @Override
   public void func_196243_a(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
   }

   @Override
   public ActionResultType func_225533_a_(BlockState state, final World world, final BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (world.field_72995_K) {
         playOpenSound();
         return ActionResultType.SUCCESS;
      } else {
         final ListNBT playerTrades = GlobalTraderData.get((ServerWorld)world).getPlayerTradesAsNbt(player);
         NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return new StringTextComponent("Global Trader");
            }

            @Nullable
            public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
               BlockState blockState = world.func_180495_p(pos);
               BlockPos position = VendingMachineBlock.getTileEntityPos(blockState, pos);
               return new GlobalTraderContainer(windowId, world, position, playerInventory, playerEntity, playerTrades);
            }
         }, buffer -> {
            CompoundNBT nbt = new CompoundNBT();
            nbt.func_218657_a("PlayerTradesList", playerTrades);
            BlockState blockState = world.func_180495_p(pos);
            buffer.func_179255_a(getTileEntityPos(blockState, pos));
            buffer.func_150786_a(nbt);
         });
         return ActionResultType.SUCCESS;
      }
   }
}
