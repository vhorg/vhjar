package iskallia.vault.world.gen.structure;

import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

public class JigsawPiecePlacer {
   private static final Random rand = new Random();
   private final ServerWorld world;
   private final JigsawPieceResolver resolver;
   private final TemplateManager templateManager;
   private final StructureManager structureManager;
   private final ChunkGenerator chunkGenerator;
   private final Registry<JigsawPattern> jigsawPatternRegistry;

   private JigsawPiecePlacer(JigsawPiece piece, ServerWorld world, BlockPos pos) {
      this.world = world;
      this.resolver = JigsawPieceResolver.newResolver(piece, pos);
      this.templateManager = world.func_184163_y();
      this.structureManager = world.func_241112_a_();
      this.chunkGenerator = world.func_72863_F().field_186029_c;
      this.jigsawPatternRegistry = world.func_73046_m().func_244267_aX().func_243612_b(Registry.field_243555_ax);
   }

   public static JigsawPiecePlacer newPlacer(JigsawPiece piece, ServerWorld world, BlockPos pos) {
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
      List<AbstractVillagePiece> resolvedPieces = this.resolver.resolveJigsawPieces(this.templateManager, this.jigsawPatternRegistry);
      resolvedPieces.forEach(this::placeStructurePiece);
      return resolvedPieces.stream().flatMap(piece -> VaultPiece.of(piece).stream()).filter(Objects::nonNull).collect(Collectors.toList());
   }

   private void placeStructurePiece(AbstractVillagePiece structurePiece) {
      MutableBoundingBox structureBox = structurePiece.func_74874_b();
      Vector3i center = structureBox.func_215126_f();
      BlockPos generationPos = new BlockPos(center.func_177958_n(), structureBox.field_78895_b, center.func_177952_p());
      JigsawPiece toGenerate = structurePiece.func_214826_b();
      this.placeJigsawPiece(toGenerate, structurePiece.func_214828_c(), generationPos, structurePiece.func_214809_Y_(), structureBox);
   }

   private void placeJigsawPiece(JigsawPiece jigsawPiece, BlockPos seedPos, BlockPos generationPos, Rotation pieceRotation, MutableBoundingBox pieceBox) {
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
         jigsawPiece.func_230378_a_(
            this.templateManager, this.world, this.structureManager, this.chunkGenerator, seedPos, generationPos, pieceRotation, pieceBox, rand, false
         );
      }
   }
}
