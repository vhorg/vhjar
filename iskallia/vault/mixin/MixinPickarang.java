package iskallia.vault.mixin;

import iskallia.vault.block.VaultChestBlock;
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
      return state.getBlock() instanceof VaultChestBlock ? 3.6E7F : state.getDestroySpeed(world, pos);
   }
}
