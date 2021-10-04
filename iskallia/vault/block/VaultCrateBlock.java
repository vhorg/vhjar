package iskallia.vault.block;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.container.VaultCrateContainer;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class VaultCrateBlock extends Block {
   public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.func_177712_a("horizontal_facing", Plane.HORIZONTAL);
   public static final DirectionProperty FACING = BlockStateProperties.field_208155_H;

   public VaultCrateBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h)
            .func_200948_a(2.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
            .func_226896_b_()
      );
      this.func_180632_j((BlockState)((BlockState)this.func_176223_P().func_206870_a(HORIZONTAL_FACING, Direction.NORTH)).func_206870_a(FACING, Direction.UP));
   }

   public static ItemStack getCrateWithLoot(VaultCrateBlock crateType, NonNullList<ItemStack> items) {
      if (items.size() > 54) {
         Vault.LOGGER.error("Attempted to get a crate with more than 54 items. Check crate loot table.");
         items = NonNullList.func_193580_a(ItemStack.field_190927_a, items.stream().limit(54L).toArray(ItemStack[]::new));
      }

      ItemStack crate = new ItemStack(crateType);
      CompoundNBT nbt = new CompoundNBT();
      ItemStackHelper.func_191282_a(nbt, items);
      if (!nbt.isEmpty()) {
         crate.func_77983_a("BlockEntityTag", nbt);
      }

      return crate;
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_CRATE_TILE_ENTITY.func_200968_a();
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{HORIZONTAL_FACING, FACING});
   }

   public ActionResultType func_225533_a_(
      final BlockState state, final World world, final BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit
   ) {
      if (!world.field_72995_K) {
         TileEntity tileEntity = world.func_175625_s(pos);
         if (!(tileEntity instanceof VaultCrateTileEntity)) {
            throw new IllegalStateException("Our named container provider is missing!");
         }

         INamedContainerProvider containerProvider = new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return state.func_177230_c() == ModBlocks.VAULT_CRATE_ARENA
                  ? new TranslationTextComponent("container.vault.vault_crate_arena")
                  : (
                     state.func_177230_c() == ModBlocks.VAULT_CRATE_SCAVENGER
                        ? new TranslationTextComponent("container.vault.vault_crate_scavenger")
                        : new TranslationTextComponent("container.vault.vault_crate")
                  );
            }

            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
               return new VaultCrateContainer(i, world, pos, playerInventory, playerEntity);
            }
         };
         NetworkHooks.openGui((ServerPlayerEntity)player, containerProvider, tileEntity.func_174877_v());
         world.func_184148_a(
            null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), SoundEvents.field_219602_O, SoundCategory.BLOCKS, 1.0F, 1.0F
         );
      }

      return ActionResultType.SUCCESS;
   }

   public void func_176208_a(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      super.func_176208_a(world, pos, state, player);
      VaultCrateBlock block = this.getBlockVariant();
      TileEntity tileentity = world.func_175625_s(pos);
      if (tileentity instanceof VaultCrateTileEntity) {
         VaultCrateTileEntity crate = (VaultCrateTileEntity)tileentity;
         ItemStack itemstack = new ItemStack(block);
         CompoundNBT compoundnbt = crate.saveToNbt();
         if (!compoundnbt.isEmpty()) {
            itemstack.func_77983_a("BlockEntityTag", compoundnbt);
         }

         ItemEntity itementity = new ItemEntity(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, itemstack);
         itementity.func_174869_p();
         world.func_217376_c(itementity);
      }
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      Direction placeDir = context.func_196010_d().func_176734_d();
      Direction horizontalDir = Direction.NORTH;
      if (placeDir.func_176740_k().func_200128_b()) {
         for (Direction direction : context.func_196009_e()) {
            if (direction.func_176740_k().func_176722_c()) {
               horizontalDir = direction;
               break;
            }
         }
      }

      return (BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, placeDir)).func_206870_a(HORIZONTAL_FACING, horizontalDir);
   }

   private VaultCrateBlock getBlockVariant() {
      if (this.getBlock() == ModBlocks.VAULT_CRATE) {
         return ModBlocks.VAULT_CRATE;
      } else {
         return this.getBlock() == ModBlocks.VAULT_CRATE_SCAVENGER ? ModBlocks.VAULT_CRATE_SCAVENGER : ModBlocks.VAULT_CRATE_ARENA;
      }
   }

   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!worldIn.func_201670_d()) {
         CompoundNBT tag = stack.func_179543_a("BlockEntityTag");
         if (tag != null) {
            VaultCrateTileEntity crate = this.getCrateTileEntity(worldIn, pos);
            if (crate != null) {
               crate.loadFromNBT(tag);
               super.func_180633_a(worldIn, pos, state, placer, stack);
            }
         }
      }
   }

   private VaultCrateTileEntity getCrateTileEntity(World worldIn, BlockPos pos) {
      TileEntity te = worldIn.func_175625_s(pos);
      return !(te instanceof VaultCrateTileEntity) ? null : (VaultCrateTileEntity)worldIn.func_175625_s(pos);
   }
}
