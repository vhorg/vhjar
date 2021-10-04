package iskallia.vault.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public interface VaultJigsawGenerator {
   BlockPos getStartPos();

   MutableBoundingBox getStructureBox();

   int getSize();

   List<StructurePiece> getGeneratedPieces();

   void generate(
      DynamicRegistries var1,
      VillageConfig var2,
      IPieceFactory var3,
      ChunkGenerator var4,
      TemplateManager var5,
      List<StructurePiece> var6,
      Random var7,
      boolean var8,
      boolean var9
   );
}
