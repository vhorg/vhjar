package iskallia.vault.mixin;

import java.util.function.Supplier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.template.JigsawReplacementStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {SingleJigsawPiece.class},
   priority = 1001
)
public abstract class MixinSingleJigsawPiece extends JigsawPiece {
   @Shadow
   @Final
   protected Supplier<StructureProcessorList> field_214862_b;

   protected MixinSingleJigsawPiece(PlacementBehaviour projection) {
      super(projection);
   }

   @Overwrite
   protected PlacementSettings func_230379_a_(Rotation p_230379_1_, MutableBoundingBox p_230379_2_, boolean p_230379_3_) {
      PlacementSettings placementsettings = new PlacementSettings();
      placementsettings.func_186223_a(p_230379_2_);
      placementsettings.func_186220_a(p_230379_1_);
      placementsettings.func_215223_c(true);
      placementsettings.func_186222_a(false);
      placementsettings.func_237133_d_(true);
      if (!p_230379_3_) {
         placementsettings.func_215222_a(JigsawReplacementStructureProcessor.field_215196_a);
      }

      this.field_214862_b.get().func_242919_a().forEach(placementsettings::func_215222_a);
      this.func_214854_c().func_214937_b().forEach(placementsettings::func_215222_a);
      return placementsettings;
   }
}
