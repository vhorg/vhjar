package iskallia.vault.block;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.RenameType;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CryoChamberBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.field_185512_D;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.field_208163_P;
   public static final EnumProperty<CryoChamberBlock.ChamberState> CHAMBER_STATE = EnumProperty.func_177709_a(
      "chamber_state", CryoChamberBlock.ChamberState.class
   );

   public CryoChamberBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h)
            .func_200948_a(5.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
            .func_226896_b_()
            .func_235828_a_(CryoChamberBlock::isntSolid)
            .func_235847_c_(CryoChamberBlock::isntSolid)
      );
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.NORTH))
               .func_206870_a(HALF, DoubleBlockHalf.LOWER))
            .func_206870_a(CHAMBER_STATE, CryoChamberBlock.ChamberState.NONE)
      );
   }

   private static boolean isntSolid(BlockState state, IBlockReader reader, BlockPos pos) {
      return false;
   }

   public void func_149666_a(ItemGroup group, NonNullList<ItemStack> items) {
      for (CryoChamberBlock.ChamberState state : CryoChamberBlock.ChamberState.values()) {
         ItemStack stack = new ItemStack(this);
         stack.func_196085_b(state.ordinal());
         items.add(stack);
      }
   }

   public boolean hasTileEntity(BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      if (state.func_177229_b(HALF) == DoubleBlockHalf.LOWER) {
         return state.func_177229_b(CHAMBER_STATE) == CryoChamberBlock.ChamberState.NONE
            ? ModBlocks.CRYO_CHAMBER_TILE_ENTITY.func_200968_a()
            : ModBlocks.ANCIENT_CRYO_CHAMBER_TILE_ENTITY.func_200968_a();
      } else {
         return null;
      }
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockPos pos = context.func_195995_a();
      World world = context.func_195991_k();
      return pos.func_177956_o() < 255 && world.func_180495_p(pos.func_177984_a()).func_196953_a(context)
         ? (BlockState)((BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f()))
               .func_206870_a(HALF, DoubleBlockHalf.LOWER))
            .func_206870_a(CHAMBER_STATE, MiscUtils.getEnumEntry(CryoChamberBlock.ChamberState.class, context.func_195996_i().func_77952_i()))
         : null;
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{HALF, FACING, CHAMBER_STATE});
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

   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      worldIn.func_180501_a(pos.func_177984_a(), (BlockState)state.func_206870_a(HALF, DoubleBlockHalf.UPPER), 3);
      if (placer != null) {
         CryoChamberTileEntity te = getCryoChamberTileEntity(worldIn, pos, state);
         te.setOwner(placer.func_110124_au());
      }
   }

   public void func_196243_a(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!worldIn.field_72995_K) {
         if (newState.func_196958_f()) {
            CryoChamberTileEntity chamber = getCryoChamberTileEntity(worldIn, pos, state);
            if (chamber != null) {
               if (state.func_177229_b(HALF) == DoubleBlockHalf.LOWER) {
                  this.dropCryoChamber(worldIn, pos, state, chamber);
               }

               super.func_196243_a(state, worldIn, pos, newState, isMoving);
            }
         }
      }
   }

   private void dropCryoChamber(World world, BlockPos pos, BlockState state, CryoChamberTileEntity te) {
      ItemStack chamberStack = new ItemStack(ModBlocks.CRYO_CHAMBER);
      chamberStack.func_196085_b(((CryoChamberBlock.ChamberState)state.func_177229_b(CHAMBER_STATE)).ordinal());
      CompoundNBT nbt = chamberStack.func_196082_o();
      nbt.func_218657_a("BlockEntityTag", te.serializeNBT());
      chamberStack.func_77982_d(nbt);
      ItemEntity entity = new ItemEntity(world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), chamberStack);
      world.func_217376_c(entity);
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (!world.func_201670_d() && player instanceof ServerPlayerEntity) {
         CryoChamberTileEntity chamber = getCryoChamberTileEntity(world, pos, state);
         if (chamber == null) {
            return ActionResultType.SUCCESS;
         } else if (chamber.getOwner() != null && !chamber.getOwner().equals(player.func_110124_au())) {
            return ActionResultType.SUCCESS;
         } else {
            ItemStack heldStack = player.func_184586_b(hand);
            if (chamber.getEternal() != null) {
               if (!player.func_225608_bj_()) {
                  NetworkHooks.openGui((ServerPlayerEntity)player, chamber, buffer -> buffer.func_179255_a(pos));
                  return ActionResultType.SUCCESS;
               }

               if (heldStack.func_190926_b()) {
                  final CompoundNBT nbt = new CompoundNBT();
                  nbt.func_74768_a("RenameType", RenameType.CRYO_CHAMBER.ordinal());
                  nbt.func_218657_a("Data", chamber.getRenameNBT());
                  NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
                     public ITextComponent func_145748_c_() {
                        return new StringTextComponent("Cryo Chamber");
                     }

                     @Nullable
                     public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new RenamingContainer(windowId, nbt);
                     }
                  }, buffer -> buffer.func_150786_a(nbt));
                  return ActionResultType.SUCCESS;
               }
            } else if (!((CryoChamberBlock.ChamberState)state.func_177229_b(CHAMBER_STATE)).containsAncient()
               && heldStack.func_77973_b() == ModItems.TRADER_CORE) {
               TraderCore coreToInsert = ItemTraderCore.getCoreFromStack(heldStack);
               if (chamber.getOwner() == null) {
                  chamber.setOwner(player.func_110124_au());
               }

               if (chamber.addTraderCore(coreToInsert)) {
                  if (!player.func_184812_l_()) {
                     heldStack.func_190918_g(1);
                  }

                  chamber.sendUpdates();
               }
            }

            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.SUCCESS;
      }
   }

   public static BlockPos getCryoChamberPos(BlockState state, BlockPos pos) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.UPPER ? pos.func_177977_b() : pos;
   }

   public static CryoChamberTileEntity getCryoChamberTileEntity(World world, BlockPos pos, BlockState state) {
      BlockPos cryoChamberPos = getCryoChamberPos(state, pos);
      TileEntity tileEntity = world.func_175625_s(cryoChamberPos);
      return !(tileEntity instanceof CryoChamberTileEntity) ? null : (CryoChamberTileEntity)tileEntity;
   }

   public static enum ChamberState implements IStringSerializable {
      NONE("none"),
      RUSTY("rusty");

      private final String name;

      private ChamberState(String name) {
         this.name = name;
      }

      public boolean containsAncient() {
         return this == RUSTY;
      }

      public String func_176610_l() {
         return this.name;
      }
   }
}
