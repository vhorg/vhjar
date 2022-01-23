package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.TimeAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.time.extension.TimeAltarExtension;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class TimeAltarBlock extends FillableAltarBlock<TimeAltarTileEntity> {
   public TimeAltarTileEntity createTileEntity(BlockState state, IBlockReader world) {
      return (TimeAltarTileEntity)ModBlocks.TIME_ALTAR_TILE_ENTITY.func_200968_a();
   }

   @Override
   public IParticleData getFlameParticle() {
      return (IParticleData)ModParticles.YELLOW_FLAME.get();
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.TIMEKEEPER;
   }

   public ActionResultType rightClicked(
      BlockState state, ServerWorld world, BlockPos pos, TimeAltarTileEntity tileEntity, ServerPlayerEntity player, ItemStack heldStack
   ) {
      if (!tileEntity.initialized()) {
         return ActionResultType.SUCCESS;
      } else if (player.func_184812_l_()) {
         tileEntity.makeProgress(player, 1, sPlayer -> {});
         return ActionResultType.SUCCESS;
      } else {
         VaultRaid vault = VaultRaidData.get(world).getActiveFor(player);
         if (vault == null) {
            return ActionResultType.FAIL;
         } else {
            tileEntity.makeProgress(player, 1, sPlayer -> {
               PlayerFavourData data = PlayerFavourData.get(sPlayer.func_71121_q());
               if (rand.nextFloat() < getFavourChance(sPlayer, PlayerFavourData.VaultGodType.TIMEKEEPER)) {
                  PlayerFavourData.VaultGodType vg = this.getAssociatedVaultGod();
                  if (data.addFavour(sPlayer, vg, 1)) {
                     data.addFavour(sPlayer, vg.getOther(rand), -1);
                     FillableAltarBlock.playFavourInfo(sPlayer);
                  }
               }
            });
            vault.getPlayers().forEach(vaultPlayer -> vaultPlayer.getTimer().addTime(new TimeAltarExtension(-1200), 0));
            return ActionResultType.SUCCESS;
         }
      }
   }

   @Override
   protected BlockState getSuccessChestState(BlockState altarState) {
      BlockState chestState = super.getSuccessChestState(altarState);
      return (BlockState)chestState.func_206870_a(ChestBlock.field_176459_a, ((Direction)chestState.func_177229_b(FACING)).func_176734_d());
   }
}
