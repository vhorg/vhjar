package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.XpAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.world.data.PlayerFavourData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class XPAltarBlock extends FillableAltarBlock<XpAltarTileEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.XP_ALTAR_TILE_ENTITY.create(pPos, pState);
   }

   @Override
   public ParticleOptions getFlameParticle() {
      return (ParticleOptions)ModParticles.BLUE_FLAME.get();
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.OMNISCIENT;
   }

   @Override
   public ItemStack getAssociatedVaultGodShard() {
      return new ItemStack(ModItems.CRYSTAL_SHARD_OMNISCIENT);
   }

   public InteractionResult rightClicked(
      BlockState state, ServerLevel world, BlockPos pos, XpAltarTileEntity tileEntity, ServerPlayer player, ItemStack heldStack
   ) {
      if (!tileEntity.initialized()) {
         return InteractionResult.SUCCESS;
      } else if (player.isCreative()) {
         tileEntity.makeProgress(player, tileEntity.getMaxProgress() - tileEntity.getCurrentProgress(), sPlayer -> {});
         return InteractionResult.SUCCESS;
      } else if (player.experienceLevel <= 0) {
         return InteractionResult.FAIL;
      } else {
         int levelDrain = Math.min(player.experienceLevel, tileEntity.getMaxProgress() - tileEntity.getCurrentProgress());
         player.setExperienceLevels(player.experienceLevel - levelDrain);
         tileEntity.makeProgress(player, levelDrain, sPlayer -> {
            PlayerFavourData data = PlayerFavourData.get(sPlayer.getLevel());
            if (rand.nextFloat() < getFavourChance(sPlayer, PlayerFavourData.VaultGodType.OMNISCIENT)) {
               PlayerFavourData.VaultGodType vg = this.getAssociatedVaultGod();
               if (data.addFavour(sPlayer, vg, 1)) {
                  data.addFavour(sPlayer, vg.getOther(rand), -1);
                  FillableAltarBlock.playFavourInfo(sPlayer);
               }
            }
         });
         return InteractionResult.SUCCESS;
      }
   }
}
