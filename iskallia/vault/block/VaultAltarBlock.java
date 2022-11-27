package iskallia.vault.block;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerVaultAltarData;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VaultAltarBlock extends Block implements EntityBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public VaultAltarBlock() {
      super(Properties.of(Material.STONE, MaterialColor.DIAMOND).requiresCorrectToolForDrops().strength(3.0F, 3600000.0F).noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, Boolean.FALSE));
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level p_153212_, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.VAULT_ALTAR_TILE_ENTITY, VaultAltarTileEntity::tick);
   }

   @javax.annotation.Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(POWERED, Boolean.FALSE);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{POWERED});
   }

   public void animateTick(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Random rand) {
      if (world.getBlockEntity(pos) instanceof VaultAltarTileEntity altarTileEntity) {
         AltarInfusionRecipe recipe = altarTileEntity.getRecipe();
         if (recipe != null && recipe.isPogInfused()) {
            for (int i = 0; i < 4; i++) {
               double d0 = pos.getX() + rand.nextDouble();
               double d1 = pos.getY() + rand.nextDouble();
               double d2 = pos.getZ() + rand.nextDouble();
               double d3 = (rand.nextFloat() - 0.5) * 0.5;
               double d4 = (rand.nextFloat() - 0.5) * 0.5;
               double d5 = (rand.nextFloat() - 0.5) * 0.5;
               int j = rand.nextInt(2) * 2 - 1;
               if (!world.getBlockState(pos.west()).is(this) && !world.getBlockState(pos.east()).is(this)) {
                  d0 = pos.getX() + 0.5 + 0.25 * j;
                  d3 = rand.nextFloat() * 2.0F * j;
               } else {
                  d2 = pos.getZ() + 0.5 + 0.25 * j;
                  d5 = rand.nextFloat() * 2.0F * j;
               }

               world.addParticle(ParticleTypes.WITCH, d0, d1, d2, d3, d4, d5);
            }
         }
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.VAULT_ALTAR_TILE_ENTITY.create(pPos, pState);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (!world.isClientSide && handIn == InteractionHand.MAIN_HAND && player instanceof ServerPlayer) {
         ItemStack heldItem = player.getMainHandItem();
         VaultAltarTileEntity altar = this.getAltarTileEntity(world, pos);
         if (altar == null) {
            return InteractionResult.SUCCESS;
         } else if (altar.getAltarState() == VaultAltarTileEntity.AltarState.IDLE) {
            return heldItem.getItem() == ModItems.VAULT_ROCK ? altar.onAddVaultRock((ServerPlayer)player, heldItem) : InteractionResult.SUCCESS;
         } else {
            if (altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING) {
            }

            if (player.isShiftKeyDown()
               && (altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING || altar.getAltarState() == VaultAltarTileEntity.AltarState.COMPLETE)) {
               InteractionResult result = altar.getRecipe() != null && altar.getRecipe().isPogInfused()
                  ? altar.onRemovePogInfusion()
                  : altar.onRemoveVaultRock();
               PlayerVaultAltarData.get((ServerLevel)world).setDirty();
               return result;
            } else {
               return InteractionResult.SUCCESS;
            }
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.isClientSide) {
         boolean powered = worldIn.hasNeighborSignal(pos);
         if (powered != (Boolean)state.getValue(POWERED) && powered) {
            VaultAltarTileEntity altar = this.getAltarTileEntity(worldIn, pos);
            if (altar != null) {
               altar.onAltarPowered();
            }
         }

         worldIn.setBlock(pos, (BlockState)state.setValue(POWERED, powered), 3);
      }
   }

   public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @javax.annotation.Nullable Direction side) {
      return true;
   }

   private VaultAltarTileEntity getAltarTileEntity(Level worldIn, BlockPos pos) {
      BlockEntity te = worldIn.getBlockEntity(pos);
      return te instanceof VaultAltarTileEntity ? (VaultAltarTileEntity)te : null;
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      VaultAltarTileEntity altar = this.getAltarTileEntity(world, pos);
      if (altar != null) {
         if (newState.getBlock() == Blocks.AIR) {
            if (altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING || altar.getAltarState() == VaultAltarTileEntity.AltarState.COMPLETE) {
               ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, new ItemStack(ModItems.VAULT_ROCK));
               world.addFreshEntity(entity);
            }

            ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, new ItemStack(ModBlocks.VAULT_ALTAR));
            world.addFreshEntity(entity);
            PlayerVaultAltarData.get((ServerLevel)world).removeAltar(altar.getOwner(), pos);
            super.onRemove(state, world, pos, newState, isMoving);
         }
      }
   }

   public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @javax.annotation.Nullable LivingEntity placer, ItemStack stack) {
      if (!worldIn.isClientSide) {
         VaultAltarTileEntity altar = (VaultAltarTileEntity)worldIn.getBlockEntity(pos);
         if (altar != null && placer instanceof Player) {
            altar.setOwner(placer.getUUID());
            altar.setAltarState(VaultAltarTileEntity.AltarState.IDLE);
            altar.sendUpdates();
            PlayerVaultAltarData.get((ServerLevel)worldIn).addAltar(placer.getUUID(), pos);
            super.setPlacedBy(worldIn, pos, state, placer, stack);
         }
      }
   }
}
