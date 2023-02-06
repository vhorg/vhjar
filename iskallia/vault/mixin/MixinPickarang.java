package iskallia.vault.mixin;

import iskallia.vault.block.CoinPileBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.VaultOreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.content.tools.entity.Pickarang;

@Mixin({Pickarang.class})
public class MixinPickarang {
   @Redirect(
      method = {"onHit"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
      )
   )
   protected float getDestroySpeed(BlockState state, BlockGetter world, BlockPos pos) {
      return !(state.getBlock() instanceof VaultChestBlock) && !(state.getBlock() instanceof VaultOreBlock) && !(state.getBlock() instanceof CoinPileBlock)
         ? state.getDestroySpeed(world, pos)
         : 3.6E7F;
   }
}
