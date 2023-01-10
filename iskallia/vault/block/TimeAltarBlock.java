package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.TimeAltarTileEntity;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.time.modifier.TimeAltarExtension;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TimeAltarBlock extends FillableAltarBlock<TimeAltarTileEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.TIME_ALTAR_TILE_ENTITY.create(pPos, pState);
   }

   @Override
   public ParticleOptions getFlameParticle() {
      return (ParticleOptions)ModParticles.YELLOW_FLAME.get();
   }

   @Override
   public VaultGod getAssociatedVaultGod() {
      return VaultGod.WENDARR;
   }

   @Override
   public ItemStack getAssociatedVaultGodShard() {
      return new ItemStack(ModItems.CRYSTAL_SHARD_TIMEKEEPER);
   }

   public InteractionResult rightClicked(
      BlockState state, ServerLevel world, BlockPos pos, TimeAltarTileEntity tileEntity, ServerPlayer player, ItemStack heldStack
   ) {
      if (!player.isCreative()) {
         ServerVaults.get(world).ifPresent(vault -> vault.ifPresent(Vault.CLOCK, clock -> clock.addModifier(new TimeAltarExtension(player, 1200))));
      }

      tileEntity.makeProgress(player, 1);
      return InteractionResult.SUCCESS;
   }
}
