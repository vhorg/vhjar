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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class VaultCrateBlock extends Block {
   public VaultCrateBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h)
            .func_200948_a(2.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
      );
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

   public ActionResultType func_225533_a_(BlockState state, final World world, final BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (!world.field_72995_K) {
         TileEntity tileEntity = world.func_175625_s(pos);
         if (!(tileEntity instanceof VaultCrateTileEntity)) {
            throw new IllegalStateException("Our named container provider is missing!");
         }

         INamedContainerProvider containerProvider = new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return new TranslationTextComponent("container.vault.vault_crate");
            }

            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
               return new VaultCrateContainer(i, world, pos, playerInventory, playerEntity);
            }
         };
         NetworkHooks.openGui((ServerPlayerEntity)player, containerProvider, tileEntity.func_174877_v());
      }

      return ActionResultType.SUCCESS;
   }

   public void func_176208_a(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      if (worldIn.field_72995_K) {
         super.func_176208_a(worldIn, pos, state, player);
      }

      VaultCrateBlock block = this.getBlockVariant();
      TileEntity tileentity = worldIn.func_175625_s(pos);
      if (tileentity instanceof VaultCrateTileEntity) {
         VaultCrateTileEntity crate = (VaultCrateTileEntity)tileentity;
         ItemStack itemstack = new ItemStack(block);
         CompoundNBT compoundnbt = crate.saveToNbt();
         if (!compoundnbt.isEmpty()) {
            itemstack.func_77983_a("BlockEntityTag", compoundnbt);
         }

         ItemEntity itementity = new ItemEntity(worldIn, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, itemstack);
         itementity.func_174869_p();
         worldIn.func_217376_c(itementity);
      }

      super.func_176208_a(worldIn, pos, state, player);
   }

   private VaultCrateBlock getBlockVariant() {
      return this.getBlock() == ModBlocks.VAULT_CRATE ? ModBlocks.VAULT_CRATE : ModBlocks.VAULT_CRATE_ARENA;
   }

   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!worldIn.field_72995_K) {
         CompoundNBT compoundnbt = stack.func_179543_a("BlockEntityTag");
         if (compoundnbt != null) {
            VaultCrateTileEntity crate = this.getCrateTileEntity(worldIn, pos);
            if (crate != null) {
               crate.loadFromNBT(compoundnbt);
               super.func_180633_a(worldIn, pos, state, placer, stack);
            }
         }
      }
   }

   private VaultCrateTileEntity getCrateTileEntity(World worldIn, BlockPos pos) {
      TileEntity te = worldIn.func_175625_s(pos);
      return te != null && te instanceof VaultCrateTileEntity ? (VaultCrateTileEntity)worldIn.func_175625_s(pos) : null;
   }
}
