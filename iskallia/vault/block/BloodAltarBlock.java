package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.BloodAltarTileEntity;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.EntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BloodAltarBlock extends FillableAltarBlock<BloodAltarTileEntity> {
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.BLOOD_ALTAR_TILE_ENTITY.create(pos, state);
   }

   @Override
   public ParticleOptions getFlameParticle() {
      return (ParticleOptions)ModParticles.GREEN_FLAME.get();
   }

   @Override
   public VaultGod getAssociatedVaultGod() {
      return VaultGod.VELARA;
   }

   @Override
   public ItemStack getAssociatedVaultGodShard() {
      return new ItemStack(ModItems.CRYSTAL_SHARD_BENEVOLENT);
   }

   public InteractionResult rightClicked(
      BlockState state, ServerLevel world, BlockPos pos, BloodAltarTileEntity altar, ServerPlayer player, ItemStack heldStack
   ) {
      if (!player.isCreative()) {
         EntityHelper.changeHealth(player, -2);
      }

      altar.makeProgress(player, 1);
      return InteractionResult.SUCCESS;
   }
}
