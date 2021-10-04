package iskallia.vault.mixin;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({StructureProcessor.class})
public abstract class MixinStructureProcessor {
   @Shadow
   @Deprecated
   @Nullable
   public abstract BlockInfo func_230386_a_(IWorldReader var1, BlockPos var2, BlockPos var3, BlockInfo var4, BlockInfo var5, PlacementSettings var6);

   @Inject(
      method = {"process"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   protected void process(
      IWorldReader world,
      BlockPos pos1,
      BlockPos pos2,
      BlockInfo info1,
      BlockInfo info2,
      PlacementSettings settings,
      @Nullable Template template,
      CallbackInfoReturnable<BlockInfo> ci
   ) {
      try {
         ci.setReturnValue(this.func_230386_a_(world, pos1, pos2, info1, info2, settings));
      } catch (Exception var10) {
         ci.setReturnValue(null);
      }
   }
}
