package iskallia.vault.world.gen.structure;

import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class JigsawPiecePlacer {
   private static final Random rand = new Random();
   public static int generationPlacementCount = 0;
   private final ServerLevel world;
   private final JigsawPieceResolver resolver;
   private final StructureManager templateManager;
   private final StructureFeatureManager structureManager;
   private final ChunkGenerator chunkGenerator;
   private final Registry<StructureTemplatePool> jigsawPatternRegistry;

   private JigsawPiecePlacer(StructurePoolElement piece, ServerLevel world, BlockPos pos) {
      this.world = world;
      this.resolver = JigsawPieceResolver.newResolver(piece, pos);
      this.templateManager = world.getStructureManager();
      this.structureManager = world.structureFeatureManager();
      this.chunkGenerator = world.getChunkSource().getGenerator();
      this.jigsawPatternRegistry = world.getServer().registryAccess().registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
   }

   public static JigsawPiecePlacer newPlacer(StructurePoolElement piece, ServerLevel world, BlockPos pos) {
      return new JigsawPiecePlacer(piece, world, pos);
   }

   public JigsawPiecePlacer withRotation(Rotation rotation) {
      this.resolver.withRotation(rotation);
      return this;
   }

   public JigsawPiecePlacer andJigsawFilter(Predicate<ResourceLocation> filter) {
      this.resolver.andJigsawFilter(filter);
      return this;
   }

   public List<VaultPiece> placeJigsaw() {
      List<PoolElementStructurePiece> resolvedPieces = this.resolver.resolveJigsawPieces(this.templateManager, this.jigsawPatternRegistry);
      resolvedPieces.forEach(this::placeStructurePiece);
      return resolvedPieces.stream().flatMap(piece -> VaultPiece.of(piece).stream()).filter(Objects::nonNull).collect(Collectors.toList());
   }

   private void placeStructurePiece(PoolElementStructurePiece structurePiece) {
      BoundingBox structureBox = structurePiece.getBoundingBox();
      Vec3i center = structureBox.getCenter();
      BlockPos generationPos = new BlockPos(center.getX(), structureBox.minY(), center.getZ());
      StructurePoolElement toGenerate = structurePiece.getElement();

      try {
         generationPlacementCount++;
         this.placeJigsawPiece(toGenerate, structurePiece.getPosition(), generationPos, structurePiece.getRotation(), structureBox);
      } finally {
         generationPlacementCount--;
      }
   }

   private void placeJigsawPiece(StructurePoolElement jigsawPiece, BlockPos seedPos, BlockPos generationPos, Rotation pieceRotation, BoundingBox pieceBox) {
      if (jigsawPiece instanceof PalettedListPoolElement) {
         ((PalettedListPoolElement)jigsawPiece)
            .generate(
               this.templateManager, this.world, this.structureManager, this.chunkGenerator, seedPos, generationPos, pieceRotation, pieceBox, rand, false, 18
            );
      } else if (jigsawPiece instanceof PalettedSinglePoolElement) {
         ((PalettedSinglePoolElement)jigsawPiece)
            .generate(
               null,
               this.templateManager,
               this.world,
               this.structureManager,
               this.chunkGenerator,
               seedPos,
               generationPos,
               pieceRotation,
               pieceBox,
               rand,
               false,
               18
            );
      } else {
         jigsawPiece.place(
            this.templateManager, this.world, this.structureManager, this.chunkGenerator, seedPos, generationPos, pieceRotation, pieceBox, rand, false
         );
      }
   }

   public static boolean isPlacingRoom() {
      return generationPlacementCount > 0;
   }
}
