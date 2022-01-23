package iskallia.vault.block;

import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.entity.BloodAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.PlayerFavourData;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class BloodAltarBlock extends FillableAltarBlock<BloodAltarTileEntity> {
   public BloodAltarTileEntity createTileEntity(BlockState state, IBlockReader world) {
      return (BloodAltarTileEntity)ModBlocks.BLOOD_ALTAR_TILE_ENTITY.func_200968_a();
   }

   @Override
   public IParticleData getFlameParticle() {
      return (IParticleData)ModParticles.GREEN_FLAME.get();
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.BENEVOLENT;
   }

   @Nonnull
   @Override
   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockState state = super.func_196258_a(context);
      return (BlockState)state.func_206870_a(FACING, ((Direction)state.func_177229_b(FACING)).func_176734_d());
   }

   public ActionResultType rightClicked(
      BlockState state, ServerWorld world, BlockPos pos, BloodAltarTileEntity tileEntity, ServerPlayerEntity player, ItemStack heldStack
   ) {
      if (!tileEntity.initialized()) {
         return ActionResultType.SUCCESS;
      } else if (player.func_184812_l_()) {
         tileEntity.makeProgress(player, 1, sPlayer -> {});
         return ActionResultType.SUCCESS;
      } else {
         EntityHelper.changeHealth(player, -2);
         tileEntity.makeProgress(player, 1, sPlayer -> {
            PlayerFavourData data = PlayerFavourData.get(sPlayer.func_71121_q());
            if (rand.nextFloat() < getFavourChance(sPlayer, PlayerFavourData.VaultGodType.BENEVOLENT)) {
               PlayerFavourData.VaultGodType vg = this.getAssociatedVaultGod();
               if (data.addFavour(sPlayer, vg, 1)) {
                  data.addFavour(sPlayer, vg.getOther(rand), -1);
                  FillableAltarBlock.playFavourInfo(sPlayer);
               }
            }
         });
         return ActionResultType.SUCCESS;
      }
   }
}
