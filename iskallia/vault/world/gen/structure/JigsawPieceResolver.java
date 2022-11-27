package iskallia.vault.world.gen.structure;

import iskallia.vault.VaultMod;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

public class JigsawPieceResolver {
   private static final Object templateLoadLock = new Object();
   private static final Random rand = new Random();
   private final StructurePoolElement piece;
   private final BlockPos pos;
   private Rotation pieceRotation = Rotation.NONE;
   private Predicate<ResourceLocation> filter = key -> true;
   private final List<AABB> additionalStructureBoxes = new ArrayList<>();

   private JigsawPieceResolver(StructurePoolElement piece, BlockPos pos) {
      this.piece = piece;
      this.pos = pos;
   }

   public static JigsawPieceResolver newResolver(StructurePoolElement piece, BlockPos pos) {
      return new JigsawPieceResolver(piece, pos);
   }

   public JigsawPieceResolver withRotation(Rotation rotation) {
      this.pieceRotation = rotation;
      return this;
   }

   public JigsawPieceResolver andJigsawFilter(Predicate<ResourceLocation> filter) {
      this.filter = filter.and(filter);
      return this;
   }

   public JigsawPieceResolver addStructureBox(AABB boundingBox) {
      this.additionalStructureBoxes.add(boundingBox);
      return this;
   }

   public List<PoolElementStructurePiece> resolveJigsawPieces(StructureManager templateManager, Registry<StructureTemplatePool> jigsawPatternRegistry) {
      PoolElementStructurePiece beginningPiece = new PoolElementStructurePiece(
         templateManager,
         this.piece,
         this.pos,
         this.piece.getGroundLevelDelta(),
         this.pieceRotation,
         this.piece.getBoundingBox(templateManager, this.pos, this.pieceRotation)
      );
      BoundingBox pieceBox = beginningPiece.getBoundingBox();
      int centerY = this.pos.getY();
      int offset = pieceBox.minY() + this.piece.getGroundLevelDelta();
      beginningPiece.move(0, centerY - offset, 0);
      VoxelShape generationShape = Shapes.create(AABB.of(pieceBox).inflate(15.0));

      for (AABB additionalBoxes : this.additionalStructureBoxes) {
         generationShape = Shapes.join(generationShape, Shapes.create(additionalBoxes), BooleanOp.ONLY_FIRST);
      }

      MutableObject<VoxelShape> generationBoxRef = new MutableObject(generationShape);
      List<PoolElementStructurePiece> resolvedPieces = new ArrayList<>();
      resolvedPieces.add(beginningPiece);
      List<JigsawPieceResolver.Entry> generationEntries = new ArrayList<>();
      generationEntries.add(new JigsawPieceResolver.Entry(beginningPiece, generationBoxRef));

      while (!generationEntries.isEmpty()) {
         JigsawPieceResolver.Entry generationEntry = generationEntries.remove(0);
         this.calculatePieces(
            resolvedPieces, generationEntries, generationEntry.villagePiece, generationEntry.generationBox, templateManager, jigsawPatternRegistry
         );
      }

      return resolvedPieces;
   }

   private void calculatePieces(
      List<PoolElementStructurePiece> resolvedPieces,
      List<JigsawPieceResolver.Entry> generationEntries,
      PoolElementStructurePiece piece,
      MutableObject<VoxelShape> generationBox,
      StructureManager templateMgr,
      Registry<StructureTemplatePool> jigsawPatternRegistry
   ) {
      StructurePoolElement jigsawpiece = piece.getElement();
      BlockPos pos = piece.getPosition();
      Rotation rotation = piece.getRotation();
      BoundingBox pieceBox = piece.getBoundingBox();
      MutableObject<VoxelShape> thisPieceGenerationBox = new MutableObject();
      int minY = pieceBox.minY();
      List<StructureBlockInfo> thisPieceBlocks;
      synchronized (templateLoadLock) {
         thisPieceBlocks = jigsawpiece.getShuffledJigsawBlocks(templateMgr, pos, rotation, rand);
      }

      label118:
      for (StructureBlockInfo blockInfo : thisPieceBlocks) {
         Direction connectingDirection = JigsawBlock.getFrontFacing(blockInfo.state);
         BlockPos jigsawConnectorPos = blockInfo.pos;
         BlockPos expectedConnectionPos = jigsawConnectorPos.relative(connectingDirection);
         int jigsawYPos = jigsawConnectorPos.getY() - minY;
         ResourceLocation connectorPool = new ResourceLocation(blockInfo.nbt.getString("pool"));
         Optional<StructureTemplatePool> mainJigsawPattern = jigsawPatternRegistry.getOptional(connectorPool);
         if (mainJigsawPattern.isPresent() && (mainJigsawPattern.get().size() != 0 || Objects.equals(connectorPool, Pools.EMPTY.location()))) {
            ResourceLocation fallbackConnectorPool = mainJigsawPattern.get().getFallback();
            Optional<StructureTemplatePool> fallbackJigsawPattern = jigsawPatternRegistry.getOptional(fallbackConnectorPool);
            if (fallbackJigsawPattern.isPresent() && (fallbackJigsawPattern.get().size() != 0 || Objects.equals(fallbackConnectorPool, Pools.EMPTY.location()))
               )
             {
               MutableObject<VoxelShape> nextGenerationBox;
               if (pieceBox.isInside(expectedConnectionPos)) {
                  nextGenerationBox = thisPieceGenerationBox;
                  if (thisPieceGenerationBox.getValue() == null) {
                     thisPieceGenerationBox.setValue(Shapes.create(AABB.of(pieceBox)));
                  }
               } else {
                  nextGenerationBox = generationBox;
               }

               WeightedList<StructurePoolElement> weightedPieces = new WeightedList<>();
               if (this.filter.test(connectorPool)) {
                  mainJigsawPattern.get()
                     .rawTemplates
                     .forEach(weightedPiece -> weightedPieces.add((StructurePoolElement)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
               }

               if (this.filter.test(fallbackConnectorPool)) {
                  fallbackJigsawPattern.get()
                     .rawTemplates
                     .forEach(weightedPiece -> weightedPieces.add((StructurePoolElement)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
               }

               while (!weightedPieces.isEmpty()) {
                  StructurePoolElement nextPiece = weightedPieces.removeRandom(rand);
                  if (nextPiece == null || nextPiece == EmptyPoolElement.INSTANCE) {
                     break;
                  }

                  for (Rotation nextPieceRotation : Rotation.getShuffled(rand)) {
                     List<StructureBlockInfo> nextPieceBlocks;
                     synchronized (templateLoadLock) {
                        nextPieceBlocks = nextPiece.getShuffledJigsawBlocks(templateMgr, BlockPos.ZERO, nextPieceRotation, rand);
                     }

                     for (StructureBlockInfo nextPieceBlockInfo : nextPieceBlocks) {
                        if (JigsawBlock.canAttach(blockInfo, nextPieceBlockInfo)) {
                           BlockPos nextPiecePos = nextPieceBlockInfo.pos;
                           BlockPos pieceDiff = new BlockPos(
                              expectedConnectionPos.getX() - nextPiecePos.getX(),
                              expectedConnectionPos.getY() - nextPiecePos.getY(),
                              expectedConnectionPos.getZ() - nextPiecePos.getZ()
                           );
                           BoundingBox nextPieceBox = nextPiece.getBoundingBox(templateMgr, pieceDiff, nextPieceRotation);
                           boolean isNextPieceRigid = nextPiece.getProjection() == Projection.RIGID;
                           int nextY = nextPiecePos.getY();
                           int l1 = jigsawYPos - nextY + JigsawBlock.getFrontFacing(nextPieceBlockInfo.state).getStepY();
                           if (VaultPiece.shouldIgnoreCollision(nextPiece)
                              || !Shapes.joinIsNotEmpty(
                                 (VoxelShape)nextGenerationBox.getValue(), Shapes.create(AABB.of(nextPieceBox).deflate(0.25)), BooleanOp.ONLY_SECOND
                              )) {
                              nextGenerationBox.setValue(
                                 Shapes.joinUnoptimized((VoxelShape)nextGenerationBox.getValue(), Shapes.create(AABB.of(nextPieceBox)), BooleanOp.ONLY_FIRST)
                              );
                              int l2;
                              if (isNextPieceRigid) {
                                 l2 = piece.getGroundLevelDelta() - l1;
                              } else {
                                 l2 = nextPiece.getGroundLevelDelta();
                              }

                              PoolElementStructurePiece nextPieceVillagePiece = new PoolElementStructurePiece(
                                 templateMgr, nextPiece, pieceDiff, l2, nextPieceRotation, nextPieceBox
                              );
                              resolvedPieces.add(nextPieceVillagePiece);
                              generationEntries.add(new JigsawPieceResolver.Entry(nextPieceVillagePiece, nextGenerationBox));
                              continue label118;
                           }
                        }
                     }
                  }
               }
            } else {
               VaultMod.LOGGER.warn("Empty or none existent fallback pool: {}", fallbackConnectorPool);
            }
         } else {
            VaultMod.LOGGER.warn("Empty or none existent pool: {}", connectorPool);
         }
      }
   }

   static final class Entry {
      private final PoolElementStructurePiece villagePiece;
      private final MutableObject<VoxelShape> generationBox;

      private Entry(PoolElementStructurePiece piece, MutableObject<VoxelShape> generationBox) {
         this.villagePiece = piece;
         this.generationBox = generationBox;
      }
   }
}
