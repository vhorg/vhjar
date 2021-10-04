package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.SoulAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import iskallia.vault.world.data.PlayerFavourData;
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

public class SoulAltarBlock extends FillableAltarBlock<SoulAltarTileEntity> {
   public SoulAltarTileEntity createTileEntity(BlockState state, IBlockReader world) {
      return (SoulAltarTileEntity)ModBlocks.SOUL_ALTAR_TILE_ENTITY.func_200968_a();
   }

   @Override
   public IParticleData getFlameParticle() {
      return (IParticleData)ModParticles.RED_FLAME.get();
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.MALEVOLENCE;
   }

   public ActionResultType rightClicked(
      BlockState state, ServerWorld world, BlockPos pos, SoulAltarTileEntity tileEntity, ServerPlayerEntity player, ItemStack heldStack
   ) {
      if (!tileEntity.initialized()) {
         return ActionResultType.SUCCESS;
      } else if (player.func_184812_l_()) {
         tileEntity.makeProgress(player, 1, sPlayer -> {});
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.FAIL;
      }
   }

   @Override
   protected BlockState getSuccessChestState(BlockState altarState) {
      BlockState chestState = super.getSuccessChestState(altarState);
      return (BlockState)chestState.func_206870_a(ChestBlock.field_176459_a, ((Direction)chestState.func_177229_b(FACING)).func_176734_d());
   }
}
