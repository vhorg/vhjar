package iskallia.vault.world.gen.decorator;

import iskallia.vault.world.gen.structure.JigsawGenerator;
import iskallia.vault.world.gen.structure.VaultTroveStructure;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class VaultTroveFeature extends StructureFeature<VaultTroveStructure.Config, Structure<VaultTroveStructure.Config>> {
   public VaultTroveFeature(Structure<VaultTroveStructure.Config> structure, VaultTroveStructure.Config config) {
      super(structure, config);
   }

   public StructureStart<?> generate(
      JigsawGenerator jigsaw, DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager, int references, long worldSeed
   ) {
      VaultTroveStructure.Start start = (VaultTroveStructure.Start)this.field_236268_b_
         .func_214557_a()
         .create(
            this.field_236268_b_,
            jigsaw.getStartPos().func_177958_n() >> 4,
            jigsaw.getStartPos().func_177952_p() >> 4,
            MutableBoundingBox.func_78887_a(),
            references,
            worldSeed
         );
      start.generate(jigsaw, registry, gen, manager);
      return (StructureStart<?>)(start.func_75069_d() ? start : StructureStart.field_214630_a);
   }
}
