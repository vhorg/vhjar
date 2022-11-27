package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.SoulAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerFavourData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SoulAltarBlock extends FillableAltarBlock<SoulAltarTileEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.SOUL_ALTAR_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level p_153212_, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.SOUL_ALTAR_TILE_ENTITY, SoulAltarTileEntity::tick);
   }

   @Override
   public ParticleOptions getFlameParticle() {
      return (ParticleOptions)ModParticles.RED_FLAME.get();
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.MALEVOLENT;
   }

   @Override
   public ItemStack getAssociatedVaultGodShard() {
      return new ItemStack(ModItems.CRYSTAL_SHARD_MALEVOLENCE);
   }

   public InteractionResult rightClicked(
      BlockState state, ServerLevel world, BlockPos pos, SoulAltarTileEntity tileEntity, ServerPlayer player, ItemStack heldStack
   ) {
      if (!tileEntity.initialized()) {
         return InteractionResult.SUCCESS;
      } else if (player.isCreative()) {
         tileEntity.makeProgress(player, 1, sPlayer -> {});
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.FAIL;
      }
   }
}
