package iskallia.vault.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {SinglePoolElement.class},
   priority = 1001
)
public abstract class MixinSingleJigsawPiece extends StructurePoolElement {
   @Shadow
   @Final
   protected Holder<StructureProcessorList> processors;

   protected MixinSingleJigsawPiece(Projection projection) {
      super(projection);
   }

   @Overwrite
   protected StructurePlaceSettings getSettings(Rotation p_230379_1_, BoundingBox p_230379_2_, boolean p_230379_3_) {
      StructurePlaceSettings placementsettings = new StructurePlaceSettings();
      placementsettings.setBoundingBox(p_230379_2_);
      placementsettings.setRotation(p_230379_1_);
      placementsettings.setKnownShape(true);
      placementsettings.setIgnoreEntities(false);
      placementsettings.setFinalizeEntities(true);
      if (!p_230379_3_) {
         placementsettings.addProcessor(JigsawReplacementProcessor.INSTANCE);
      }

      ((StructureProcessorList)this.processors.value()).list().forEach(placementsettings::addProcessor);
      this.getProjection().getProcessors().forEach(placementsettings::addProcessor);
      return placementsettings;
   }
}
