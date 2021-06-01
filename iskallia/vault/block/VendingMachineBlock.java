package iskallia.vault.block;

import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.container.VendingMachineContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.vending.TraderCore;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class VendingMachineBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.field_185512_D;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.field_208163_P;

   public VendingMachineBlock() {
      this(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h)
            .func_200948_a(2.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
            .func_226896_b_()
      );
   }

   public VendingMachineBlock(Properties properties) {
      super(properties);
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.NORTH))
            .func_206870_a(HALF, DoubleBlockHalf.LOWER)
      );
   }

   public boolean hasTileEntity(BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.VENDING_MACHINE_TILE_ENTITY.func_200968_a() : null;
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockPos pos = context.func_195995_a();
      World world = context.func_195991_k();
      return pos.func_177956_o() < 255 && world.func_180495_p(pos.func_177984_a()).func_196953_a(context)
         ? (BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f())).func_206870_a(HALF, DoubleBlockHalf.LOWER)
         : null;
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{HALF});
      builder.func_206894_a(new Property[]{FACING});
   }

   public void func_176208_a(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!worldIn.field_72995_K && player.func_184812_l_()) {
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

   public BlockState func_196271_a(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      DoubleBlockHalf half = (DoubleBlockHalf)stateIn.func_177229_b(HALF);
      if (facing.func_176740_k() == Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
         return facingState.func_203425_a(this) && facingState.func_177229_b(HALF) != half
            ? (BlockState)stateIn.func_206870_a(FACING, facingState.func_177229_b(FACING))
            : Blocks.field_150350_a.func_176223_P();
      } else {
         return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.func_196955_c(worldIn, currentPos)
            ? Blocks.field_150350_a.func_176223_P()
            : super.func_196271_a(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      worldIn.func_180501_a(pos.func_177984_a(), (BlockState)state.func_206870_a(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public void func_196243_a(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!worldIn.field_72995_K) {
         if (newState.func_196958_f()) {
            VendingMachineTileEntity machine = (VendingMachineTileEntity)getBlockTileEntity(worldIn, pos, state);
            if (machine != null) {
               if (state.func_177229_b(HALF) == DoubleBlockHalf.LOWER) {
                  ItemStack stack = new ItemStack(this.getBlock());
                  CompoundNBT machineNBT = machine.serializeNBT();
                  CompoundNBT stackNBT = new CompoundNBT();
                  stackNBT.func_218657_a("BlockEntityTag", machineNBT);
                  stack.func_77982_d(stackNBT);
                  this.dropVendingMachine(stack, worldIn, pos);
               }

               super.func_196243_a(state, worldIn, pos, newState, isMoving);
            }
         }
      }
   }

   private void dropVendingMachine(ItemStack stack, World world, BlockPos pos) {
      ItemEntity entity = new ItemEntity(world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), stack);
      world.func_217376_c(entity);
   }

   public ActionResultType func_225533_a_(BlockState state, final World world, final BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      ItemStack heldStack = player.func_184586_b(hand);
      VendingMachineTileEntity machine = (VendingMachineTileEntity)getBlockTileEntity(world, pos, state);
      if (machine == null) {
         return ActionResultType.SUCCESS;
      } else if (!world.func_201670_d() && player.func_225608_bj_()) {
         ItemStack core = machine.getTraderCoreStack();
         if (!player.func_191521_c(core)) {
            player.func_71019_a(core, false);
         }

         machine.sendUpdates();
         return ActionResultType.SUCCESS;
      } else if (!(heldStack.func_77973_b() instanceof ItemTraderCore)) {
         if (world.field_72995_K) {
            playOpenSound();
            return ActionResultType.SUCCESS;
         } else {
            NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
               public ITextComponent func_145748_c_() {
                  return new StringTextComponent("Vending Machine");
               }

               @Nullable
               public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                  BlockState blockState = world.func_180495_p(pos);
                  BlockPos vendingMachinePos = VendingMachineBlock.getTileEntityPos(blockState, pos);
                  return new VendingMachineContainer(windowId, world, vendingMachinePos, playerInventory, playerEntity);
               }
            }, buffer -> {
               BlockState blockState = world.func_180495_p(pos);
               buffer.func_179255_a(getTileEntityPos(blockState, pos));
            });
            return super.func_225533_a_(state, world, pos, player, hand, hit);
         }
      } else {
         TraderCore lastCore = machine.getLastCore();
         TraderCore coreToInsert = ItemTraderCore.getCoreFromStack(heldStack);
         if (coreToInsert != null && coreToInsert.getTrade() != null) {
            if (lastCore != null && !lastCore.getName().equalsIgnoreCase(coreToInsert.getName())) {
               StringTextComponent text = new StringTextComponent("This vending machine is already occupied.");
               text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-10240)));
               player.func_146105_b(text, true);
            } else {
               machine.addCore(coreToInsert);
               heldStack.func_190918_g(1);
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void playOpenSound() {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_147118_V().func_147682_a(SimpleSound.func_194007_a(ModSounds.VENDING_MACHINE_SFX, 1.0F, 1.0F));
   }

   public static BlockPos getTileEntityPos(BlockState state, BlockPos pos) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.UPPER ? pos.func_177977_b() : pos;
   }

   public static TileEntity getBlockTileEntity(World world, BlockPos pos, BlockState state) {
      BlockPos vendingMachinePos = getTileEntityPos(state, pos);
      return world.func_175625_s(vendingMachinePos);
   }
}
