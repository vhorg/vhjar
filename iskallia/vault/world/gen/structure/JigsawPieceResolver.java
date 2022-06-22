package iskallia.vault.world.gen.structure;

import iskallia.vault.Vault;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import org.apache.commons.lang3.mutable.MutableObject;

public class JigsawPieceResolver {
   private static final Object templateLoadLock = new Object();
   private static final Random rand = new Random();
   private final JigsawPiece piece;
   private final BlockPos pos;
   private Rotation pieceRotation = Rotation.NONE;
   private Predicate<ResourceLocation> filter = key -> true;
   private final List<AxisAlignedBB> additionalStructureBoxes = new ArrayList<>();

   private JigsawPieceResolver(JigsawPiece piece, BlockPos pos) {
      this.piece = piece;
      this.pos = pos;
   }

   public static JigsawPieceResolver newResolver(JigsawPiece piece, BlockPos pos) {
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

   public JigsawPieceResolver addStructureBox(AxisAlignedBB boundingBox) {
      this.additionalStructureBoxes.add(boundingBox);
      return this;
   }

   public List<AbstractVillagePiece> resolveJigsawPieces(TemplateManager templateManager, Registry<JigsawPattern> jigsawPatternRegistry) {
      AbstractVillagePiece beginningPiece = new AbstractVillagePiece(
         templateManager,
         this.piece,
         this.pos,
         this.piece.func_214850_d(),
         this.pieceRotation,
         this.piece.func_214852_a(templateManager, this.pos, this.pieceRotation)
      );
      MutableBoundingBox pieceBox = beginningPiece.func_74874_b();
      int centerY = this.pos.func_177956_o();
      int offset = pieceBox.field_78895_b + this.piece.func_214850_d();
      beginningPiece.func_181138_a(0, centerY - offset, 0);
      VoxelShape generationShape = VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(pieceBox).func_186662_g(15.0));

      for (AxisAlignedBB additionalBoxes : this.additionalStructureBoxes) {
         generationShape = VoxelShapes.func_197878_a(generationShape, VoxelShapes.func_197881_a(additionalBoxes), IBooleanFunction.field_223234_e_);
      }

      MutableObject<VoxelShape> generationBoxRef = new MutableObject(generationShape);
      List<AbstractVillagePiece> resolvedPieces = new ArrayList<>();
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
      List<AbstractVillagePiece> resolvedPieces,
      List<JigsawPieceResolver.Entry> generationEntries,
      AbstractVillagePiece piece,
      MutableObject<VoxelShape> generationBox,
      TemplateManager templateMgr,
      Registry<JigsawPattern> jigsawPatternRegistry
   ) {
      JigsawPiece jigsawpiece = piece.func_214826_b();
      BlockPos pos = piece.func_214828_c();
      Rotation rotation = piece.func_214809_Y_();
      MutableBoundingBox pieceBox = piece.func_74874_b();
      MutableObject<VoxelShape> thisPieceGenerationBox = new MutableObject();
      int minY = pieceBox.field_78895_b;
      List<BlockInfo> thisPieceBlocks;
      synchronized (templateLoadLock) {
         thisPieceBlocks = jigsawpiece.func_214849_a(templateMgr, pos, rotation, rand);
      }

      label126:
      for (BlockInfo blockInfo : thisPieceBlocks) {
         Direction connectingDirection = JigsawBlock.func_235508_h_(blockInfo.field_186243_b);
         BlockPos jigsawConnectorPos = blockInfo.field_186242_a;
         BlockPos expectedConnectionPos = jigsawConnectorPos.func_177972_a(connectingDirection);
         int jigsawYPos = jigsawConnectorPos.func_177956_o() - minY;
         ResourceLocation connectorPool = new ResourceLocation(blockInfo.field_186244_c.func_74779_i("pool"));
         Optional<JigsawPattern> mainJigsawPattern = jigsawPatternRegistry.func_241873_b(connectorPool);
         if (mainJigsawPattern.isPresent()
            && (mainJigsawPattern.get().func_214946_c() != 0 || Objects.equals(connectorPool, JigsawPatternRegistry.field_244091_a.func_240901_a_()))) {
            ResourceLocation fallbackConnectorPool = mainJigsawPattern.get().func_214948_a();
            Optional<JigsawPattern> fallbackJigsawPattern = jigsawPatternRegistry.func_241873_b(fallbackConnectorPool);
            if (fallbackJigsawPattern.isPresent()
               && (
                  fallbackJigsawPattern.get().func_214946_c() != 0
                     || Objects.equals(fallbackConnectorPool, JigsawPatternRegistry.field_244091_a.func_240901_a_())
               )) {
               MutableObject<VoxelShape> nextGenerationBox;
               if (pieceBox.func_175898_b(expectedConnectionPos)) {
                  nextGenerationBox = thisPieceGenerationBox;
                  if (thisPieceGenerationBox.getValue() == null) {
                     thisPieceGenerationBox.setValue(VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(pieceBox)));
                  }
               } else {
                  nextGenerationBox = generationBox;
               }

               WeightedList<JigsawPiece> weightedPieces = new WeightedList<>();
               if (!connectorPool.equals(new ResourceLocation("empty")) && this.filter.test(connectorPool)) {
                  mainJigsawPattern.get()
                     .field_214952_d
                     .forEach(weightedPiece -> weightedPieces.add((JigsawPiece)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
               }

               if (!fallbackConnectorPool.equals(new ResourceLocation("empty")) && this.filter.test(fallbackConnectorPool)) {
                  fallbackJigsawPattern.get()
                     .field_214952_d
                     .forEach(weightedPiece -> weightedPieces.add((JigsawPiece)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
               }

               while (!weightedPieces.isEmpty()) {
                  JigsawPiece nextPiece = weightedPieces.removeRandom(rand);
                  if (nextPiece == null || nextPiece == EmptyJigsawPiece.field_214856_a) {
                     break;
                  }

                  for (Rotation nextPieceRotation : Rotation.func_222467_b(rand)) {
                     List<BlockInfo> nextPieceBlocks;
                     synchronized (templateLoadLock) {
                        nextPieceBlocks = nextPiece.func_214849_a(templateMgr, BlockPos.field_177992_a, nextPieceRotation, rand);
                     }

                     for (BlockInfo nextPieceBlockInfo : nextPieceBlocks) {
                        if (JigsawBlock.func_220171_a(blockInfo, nextPieceBlockInfo)) {
                           BlockPos nextPiecePos = nextPieceBlockInfo.field_186242_a;
                           if (connectorPool.equals(Vault.id("final_vault/tenos/obelisk"))) {
                              nextPiecePos = nextPiecePos.func_177984_a();
                           }

                           BlockPos pieceDiff = new BlockPos(
                              expectedConnectionPos.func_177958_n() - nextPiecePos.func_177958_n(),
                              expectedConnectionPos.func_177956_o() - nextPiecePos.func_177956_o(),
                              expectedConnectionPos.func_177952_p() - nextPiecePos.func_177952_p()
                           );
                           MutableBoundingBox nextPieceBox = nextPiece.func_214852_a(templateMgr, pieceDiff, nextPieceRotation);
                           boolean isNextPieceRigid = nextPiece.func_214854_c() == PlacementBehaviour.RIGID;
                           int nextY = nextPiecePos.func_177956_o();
                           int l1 = jigsawYPos - nextY + JigsawBlock.func_235508_h_(nextPieceBlockInfo.field_186243_b).func_96559_d();
                           if (VaultPiece.shouldIgnoreCollision(nextPiece)
                              || !VoxelShapes.func_197879_c(
                                 (VoxelShape)nextGenerationBox.getValue(),
                                 VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(nextPieceBox).func_186664_h(0.25)),
                                 IBooleanFunction.field_223232_c_
                              )) {
                              nextGenerationBox.setValue(
                                 VoxelShapes.func_197882_b(
                                    (VoxelShape)nextGenerationBox.getValue(),
                                    VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(nextPieceBox)),
                                    IBooleanFunction.field_223234_e_
                                 )
                              );
                              int l2;
                              if (isNextPieceRigid) {
                                 l2 = piece.func_214830_d() - l1;
                              } else {
                                 l2 = nextPiece.func_214850_d();
                              }

                              AbstractVillagePiece nextPieceVillagePiece = new AbstractVillagePiece(
                                 templateMgr, nextPiece, pieceDiff, l2, nextPieceRotation, nextPieceBox
                              );
                              resolvedPieces.add(nextPieceVillagePiece);
                              generationEntries.add(new JigsawPieceResolver.Entry(nextPieceVillagePiece, nextGenerationBox));
                              continue label126;
                           }
                        }
                     }
                  }
               }
            } else {
               Vault.LOGGER.warn("Empty or none existent fallback pool: {}", fallbackConnectorPool);
            }
         } else {
            Vault.LOGGER.warn("Empty or none existent pool: {}", connectorPool);
         }
      }
   }

   static final class Entry {
      private final AbstractVillagePiece villagePiece;
      private final MutableObject<VoxelShape> generationBox;

      private Entry(AbstractVillagePiece piece, MutableObject<VoxelShape> generationBox) {
         this.villagePiece = piece;
         this.generationBox = generationBox;
      }
   }
}
