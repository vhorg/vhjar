package iskallia.vault.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier.Context;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement.PieceFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public interface VaultJigsawGenerator {
   BlockPos getStartPos();

   BoundingBox getStructureBox();

   int getSize();

   List<StructurePiece> getGeneratedPieces();

   void generate(
      RegistryAccess var1,
      Context<JigsawConfiguration> var2,
      PieceFactory var3,
      ChunkGenerator var4,
      StructureManager var5,
      List<StructurePiece> var6,
      Random var7,
      boolean var8,
      boolean var9
   );
}
