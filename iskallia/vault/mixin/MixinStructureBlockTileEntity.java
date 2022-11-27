package iskallia.vault.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({StructureBlockEntity.class})
public abstract class MixinStructureBlockTileEntity {
   @Redirect(
      method = {"load"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/Mth;clamp(III)I"
      )
   )
   private int read(int num, int min, int max) {
      return Mth.clamp(num, min * 11, max * 11);
   }
}
