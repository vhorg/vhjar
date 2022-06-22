package iskallia.vault.block;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.PlayerVaultAltarData;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultAltarBlock extends Block {
   public static final BooleanProperty POWERED = BlockStateProperties.field_208194_u;

   public VaultAltarBlock() {
      super(Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151648_G).func_235861_h_().func_200948_a(3.0F, 3600000.0F).func_226896_b_());
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(POWERED, Boolean.FALSE));
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(POWERED, Boolean.FALSE);
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{POWERED});
   }

   public void func_180655_c(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
      TileEntity tileEntity = world.func_175625_s(pos);
      if (tileEntity instanceof VaultAltarTileEntity) {
         VaultAltarTileEntity altarTileEntity = (VaultAltarTileEntity)tileEntity;
         AltarInfusionRecipe recipe = altarTileEntity.getRecipe();
         if (recipe != null && recipe.isPogInfused()) {
            for (int i = 0; i < 4; i++) {
               double d0 = pos.func_177958_n() + rand.nextDouble();
               double d1 = pos.func_177956_o() + rand.nextDouble();
               double d2 = pos.func_177952_p() + rand.nextDouble();
               double d3 = (rand.nextFloat() - 0.5) * 0.5;
               double d4 = (rand.nextFloat() - 0.5) * 0.5;
               double d5 = (rand.nextFloat() - 0.5) * 0.5;
               int j = rand.nextInt(2) * 2 - 1;
               if (!world.func_180495_p(pos.func_177976_e()).func_203425_a(this) && !world.func_180495_p(pos.func_177974_f()).func_203425_a(this)) {
                  d0 = pos.func_177958_n() + 0.5 + 0.25 * j;
                  d3 = rand.nextFloat() * 2.0F * j;
               } else {
                  d2 = pos.func_177952_p() + 0.5 + 0.25 * j;
                  d5 = rand.nextFloat() * 2.0F * j;
               }

               world.func_195594_a(ParticleTypes.field_197607_R, d0, d1, d2, d3, d4, d5);
            }
         }
      }
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_ALTAR_TILE_ENTITY.func_200968_a();
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (!world.field_72995_K && handIn == Hand.MAIN_HAND && player instanceof ServerPlayerEntity) {
         ItemStack heldItem = player.func_184614_ca();
         VaultAltarTileEntity altar = this.getAltarTileEntity(world, pos);
         if (altar == null) {
            return ActionResultType.SUCCESS;
         } else if (altar.getAltarState() == VaultAltarTileEntity.AltarState.IDLE) {
            return heldItem.func_77973_b() == ModItems.VAULT_ROCK ? altar.onAddVaultRock((ServerPlayerEntity)player, heldItem) : ActionResultType.SUCCESS;
         } else if (altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING && heldItem.func_77973_b() == ModItems.POG) {
            return altar.getRecipe().isPogInfused() ? ActionResultType.FAIL : altar.onPogRightClick((ServerPlayerEntity)player, heldItem);
         } else if (player.func_225608_bj_()
            && (altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING || altar.getAltarState() == VaultAltarTileEntity.AltarState.COMPLETE)) {
            ActionResultType result = altar.getRecipe() != null && altar.getRecipe().isPogInfused() ? altar.onRemovePogInfusion() : altar.onRemoveVaultRock();
            PlayerVaultAltarData.get((ServerWorld)world).func_76185_a();
            return result;
         } else {
            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.SUCCESS;
      }
   }

   public void func_220069_a(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.field_72995_K) {
         boolean powered = worldIn.func_175640_z(pos);
         if (powered != (Boolean)state.func_177229_b(POWERED) && powered) {
            VaultAltarTileEntity altar = this.getAltarTileEntity(worldIn, pos);
            if (altar != null) {
               altar.onAltarPowered();
            }
         }

         worldIn.func_180501_a(pos, (BlockState)state.func_206870_a(POWERED, powered), 3);
      }
   }

   public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
      return true;
   }

   private VaultAltarTileEntity getAltarTileEntity(World worldIn, BlockPos pos) {
      TileEntity te = worldIn.func_175625_s(pos);
      return te != null && te instanceof VaultAltarTileEntity ? (VaultAltarTileEntity)worldIn.func_175625_s(pos) : null;
   }

   public void func_196243_a(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
      VaultAltarTileEntity altar = this.getAltarTileEntity(world, pos);
      if (altar != null) {
         if (newState.func_177230_c() == Blocks.field_150350_a) {
            if (altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING || altar.getAltarState() == VaultAltarTileEntity.AltarState.COMPLETE) {
               ItemEntity entity = new ItemEntity(
                  world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 1.2, pos.func_177952_p() + 0.5, new ItemStack(ModItems.VAULT_ROCK)
               );
               world.func_217376_c(entity);
            }

            ItemEntity entity = new ItemEntity(
               world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 1.2, pos.func_177952_p() + 0.5, new ItemStack(ModBlocks.VAULT_ALTAR)
            );
            world.func_217376_c(entity);
            PlayerVaultAltarData.get((ServerWorld)world).removeAltar(altar.getOwner(), pos);
            super.func_196243_a(state, world, pos, newState, isMoving);
         }
      }
   }

   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!worldIn.field_72995_K) {
         VaultAltarTileEntity altar = (VaultAltarTileEntity)worldIn.func_175625_s(pos);
         if (altar != null && placer instanceof PlayerEntity) {
            altar.setOwner(placer.func_110124_au());
            altar.setAltarState(VaultAltarTileEntity.AltarState.IDLE);
            altar.sendUpdates();
            PlayerVaultAltarData.get((ServerWorld)worldIn).addAltar(placer.func_110124_au(), pos);
            super.func_180633_a(worldIn, pos, state, placer, stack);
         }
      }
   }
}
