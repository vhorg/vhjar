package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.BloodAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.PlayerFavourData;
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
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.BENEVOLENT;
   }

   @Override
   public ItemStack getAssociatedVaultGodShard() {
      return new ItemStack(ModItems.CRYSTAL_SHARD_BENEVOLENT);
   }

   public InteractionResult rightClicked(
      BlockState state, ServerLevel world, BlockPos pos, BloodAltarTileEntity tileEntity, ServerPlayer player, ItemStack heldStack
   ) {
      if (!tileEntity.initialized()) {
         return InteractionResult.SUCCESS;
      } else if (player.isCreative()) {
         tileEntity.makeProgress(player, 1, sPlayer -> {});
         return InteractionResult.SUCCESS;
      } else {
         EntityHelper.changeHealth(player, -2);
         tileEntity.makeProgress(player, 1, sPlayer -> {
            PlayerFavourData data = PlayerFavourData.get(sPlayer.getLevel());
            if (rand.nextFloat() < getFavourChance(sPlayer, PlayerFavourData.VaultGodType.BENEVOLENT)) {
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
