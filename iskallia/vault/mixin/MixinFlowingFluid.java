package iskallia.vault.mixin;

import iskallia.vault.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FlowingFluid.class})
public class MixinFlowingFluid {
   @Redirect(
      method = {"flowInto"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/block/BlockState;isAir()Z"
      )
   )
   public boolean flowInto(BlockState state) {
      return state.func_196958_f() && state.func_177230_c() != ModBlocks.VAULT_AIR;
   }
}
