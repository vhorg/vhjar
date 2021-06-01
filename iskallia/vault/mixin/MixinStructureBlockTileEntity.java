package iskallia.vault.mixin;

import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({StructureBlockTileEntity.class})
public abstract class MixinStructureBlockTileEntity {
   @Redirect(
      method = {"read"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"
      )
   )
   private int read(int num, int min, int max) {
      return MathHelper.func_76125_a(num, min * 11, max * 11);
   }

   @OnlyIn(Dist.CLIENT)
   @Overwrite
   public double func_145833_n() {
      return 816.0;
   }
}
