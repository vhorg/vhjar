package iskallia.vault.mixin;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
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
   public abstract StructureBlockInfo processBlock(
      LevelReader var1, BlockPos var2, BlockPos var3, StructureBlockInfo var4, StructureBlockInfo var5, StructurePlaceSettings var6
   );

   @Inject(
      method = {"process"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   protected void process(
      LevelReader world,
      BlockPos pos1,
      BlockPos pos2,
      StructureBlockInfo info1,
      StructureBlockInfo info2,
      StructurePlaceSettings settings,
      @Nullable StructureTemplate template,
      CallbackInfoReturnable<StructureBlockInfo> ci
   ) {
      try {
         ci.setReturnValue(this.processBlock(world, pos1, pos2, info1, info2, settings));
      } catch (Exception var10) {
         ci.setReturnValue(null);
      }
   }
}
