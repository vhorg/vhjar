package iskallia.vault.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import iskallia.vault.VaultMod;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement.PieceFactory;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

public class JigsawGeneratorLegacy {
   public static void addPieces(
      RegistryAccess p_242837_0_,
      JigsawConfiguration p_242837_1_,
      PieceFactory p_242837_2_,
      ChunkGenerator p_242837_3_,
      StructureManager p_242837_4_,
      BlockPos p_242837_5_,
      List<? super PoolElementStructurePiece> p_242837_6_,
      Random p_242837_7_,
      boolean p_242837_8_,
      boolean p_242837_9_,
      LevelHeightAccessor levelHeightAccessor
   ) {
      StructureFeature.bootstrap();
      Registry<StructureTemplatePool> mutableregistry = p_242837_0_.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      Rotation rotation = Rotation.getRandom(p_242837_7_);
      Holder<StructureTemplatePool> jigsawpattern = p_242837_1_.startPool();
      StructurePoolElement jigsawpiece = ((StructureTemplatePool)jigsawpattern.value()).getRandomTemplate(p_242837_7_);
      PoolElementStructurePiece abstractvillagepiece = p_242837_2_.create(
         p_242837_4_, jigsawpiece, p_242837_5_, jigsawpiece.getGroundLevelDelta(), rotation, jigsawpiece.getBoundingBox(p_242837_4_, p_242837_5_, rotation)
      );
      BoundingBox mutableboundingbox = abstractvillagepiece.getBoundingBox();
      int i = (mutableboundingbox.maxX() + mutableboundingbox.minX()) / 2;
      int j = (mutableboundingbox.maxZ() + mutableboundingbox.minZ()) / 2;
      int k;
      if (p_242837_9_) {
         k = p_242837_5_.getY() + p_242837_3_.getFirstFreeHeight(i, j, Types.WORLD_SURFACE_WG, levelHeightAccessor);
      } else {
         k = p_242837_5_.getY();
      }

      int l = mutableboundingbox.minY() + abstractvillagepiece.getGroundLevelDelta();
      abstractvillagepiece.move(0, k - l, 0);
      p_242837_6_.add(abstractvillagepiece);
      if (p_242837_1_.maxDepth() > 0) {
         int maxRange = 1073741823;
         AABB axisalignedbb = new AABB(i - maxRange, k - maxRange, j - maxRange, i + maxRange + 1, k + maxRange + 1, j + maxRange + 1);
         JigsawGeneratorLegacy.Assembler jigsawmanager$assembler = new JigsawGeneratorLegacy.Assembler(
            mutableregistry, p_242837_1_.maxDepth(), p_242837_2_, p_242837_3_, p_242837_4_, p_242837_6_, p_242837_7_
         );
         jigsawmanager$assembler.availablePieces
            .addLast(
               new JigsawGeneratorLegacy.Entry(
                  abstractvillagepiece,
                  new MutableObject(Shapes.join(Shapes.create(axisalignedbb), Shapes.create(AABB.of(mutableboundingbox)), BooleanOp.ONLY_FIRST)),
                  k + maxRange,
                  0
               )
            );

         while (!jigsawmanager$assembler.availablePieces.isEmpty()) {
            JigsawGeneratorLegacy.Entry jigsawmanager$entry = jigsawmanager$assembler.availablePieces.removeFirst();
            jigsawmanager$assembler.tryPlacingChildren(
               jigsawmanager$entry.villagePiece,
               jigsawmanager$entry.free,
               jigsawmanager$entry.boundsTop,
               jigsawmanager$entry.depth,
               p_242837_8_,
               levelHeightAccessor
            );
         }
      }
   }

   static final class Assembler {
      private final Registry<StructureTemplatePool> pools;
      private final int maxDepth;
      private final PieceFactory pieceFactory;
      private final ChunkGenerator chunkGenerator;
      private final StructureManager templateManager;
      private final List<? super PoolElementStructurePiece> structurePieces;
      private final Random rand;
      private final Deque<JigsawGeneratorLegacy.Entry> availablePieces = Queues.newArrayDeque();

      private Assembler(
         Registry<StructureTemplatePool> p_i242005_1_,
         int p_i242005_2_,
         PieceFactory p_i242005_3_,
         ChunkGenerator p_i242005_4_,
         StructureManager p_i242005_5_,
         List<? super PoolElementStructurePiece> p_i242005_6_,
         Random p_i242005_7_
      ) {
         this.pools = p_i242005_1_;
         this.maxDepth = p_i242005_2_;
         this.pieceFactory = p_i242005_3_;
         this.chunkGenerator = p_i242005_4_;
         this.templateManager = p_i242005_5_;
         this.structurePieces = p_i242005_6_;
         this.rand = p_i242005_7_;
      }

      private void tryPlacingChildren(
         PoolElementStructurePiece p_236831_1_,
         MutableObject<VoxelShape> p_236831_2_,
         int p_236831_3_,
         int currentDepth,
         boolean p_236831_5_,
         LevelHeightAccessor levelHeightAccessor
      ) {
         StructurePoolElement jigsawpiece = p_236831_1_.getElement();
         BlockPos blockpos = p_236831_1_.getPosition();
         Rotation rotation = p_236831_1_.getRotation();
         Projection jigsawpattern$placementbehaviour = jigsawpiece.getProjection();
         boolean flag = jigsawpattern$placementbehaviour == Projection.RIGID;
         MutableObject<VoxelShape> mutableobject = new MutableObject();
         BoundingBox mutableboundingbox = p_236831_1_.getBoundingBox();
         int i = mutableboundingbox.minY();

         label143:
         for (StructureBlockInfo template$blockinfo : jigsawpiece.getShuffledJigsawBlocks(this.templateManager, blockpos, rotation, this.rand)) {
            Direction direction = JigsawBlock.getFrontFacing(template$blockinfo.state);
            BlockPos blockpos1 = template$blockinfo.pos;
            BlockPos blockpos2 = blockpos1.relative(direction);
            int j = blockpos1.getY() - i;
            int k = -1;
            ResourceLocation resourcelocation = new ResourceLocation(template$blockinfo.nbt.getString("pool"));
            Optional<StructureTemplatePool> mainJigsawPattern = this.pools.getOptional(resourcelocation);
            if (mainJigsawPattern.isPresent() && (mainJigsawPattern.get().size() != 0 || Objects.equals(resourcelocation, Pools.EMPTY.location()))) {
               ResourceLocation resourcelocation1 = mainJigsawPattern.get().getFallback();
               Optional<StructureTemplatePool> fallbackJigsawPattern = this.pools.getOptional(resourcelocation1);
               if (fallbackJigsawPattern.isPresent() && (fallbackJigsawPattern.get().size() != 0 || Objects.equals(resourcelocation1, Pools.EMPTY.location()))) {
                  boolean flag1 = mutableboundingbox.isInside(blockpos2);
                  MutableObject<VoxelShape> mutableobject1;
                  int l;
                  if (flag1) {
                     mutableobject1 = mutableobject;
                     l = i;
                     if (mutableobject.getValue() == null) {
                        mutableobject.setValue(Shapes.create(AABB.of(mutableboundingbox)));
                     }
                  } else {
                     mutableobject1 = p_236831_2_;
                     l = p_236831_3_;
                  }

                  List<StructurePoolElement> list = Lists.newArrayList();
                  if (currentDepth != this.maxDepth) {
                     list.addAll(mainJigsawPattern.get().getShuffledTemplates(this.rand));
                     list.addAll(fallbackJigsawPattern.get().getShuffledTemplates(this.rand));
                  } else {
                     list.addAll(fallbackJigsawPattern.get().getShuffledTemplates(this.rand));
                  }

                  for (StructurePoolElement jigsawpiece1 : list) {
                     if (jigsawpiece1 == EmptyPoolElement.INSTANCE) {
                        break;
                     }

                     for (Rotation rotation1 : Rotation.getShuffled(this.rand)) {
                        List<StructureBlockInfo> list1 = jigsawpiece1.getShuffledJigsawBlocks(this.templateManager, BlockPos.ZERO, rotation1, this.rand);
                        BoundingBox mutableboundingbox1 = jigsawpiece1.getBoundingBox(this.templateManager, BlockPos.ZERO, rotation1);
                        int i1;
                        if (p_236831_5_ && mutableboundingbox1.getYSpan() <= 16) {
                           i1 = list1.stream()
                              .mapToInt(
                                 p_242841_2_ -> {
                                    if (!mutableboundingbox1.isInside(p_242841_2_.pos.relative(JigsawBlock.getFrontFacing(p_242841_2_.state)))) {
                                       return 0;
                                    } else {
                                       ResourceLocation resourcelocation2 = new ResourceLocation(p_242841_2_.nbt.getString("pool"));
                                       Optional<StructureTemplatePool> optional2 = this.pools.getOptional(resourcelocation2);
                                       Optional<StructureTemplatePool> optional3 = optional2.flatMap(
                                          p_242843_1_ -> this.pools.getOptional(p_242843_1_.getFallback())
                                       );
                                       int k3 = optional2.<Integer>map(p_242842_1_ -> p_242842_1_.getMaxSize(this.templateManager)).orElse(0);
                                       int l3 = optional3.<Integer>map(p_242840_1_ -> p_242840_1_.getMaxSize(this.templateManager)).orElse(0);
                                       return Math.max(k3, l3);
                                    }
                                 }
                              )
                              .max()
                              .orElse(0);
                        } else {
                           i1 = 0;
                        }

                        for (StructureBlockInfo template$blockinfo1 : list1) {
                           if (JigsawBlock.canAttach(template$blockinfo, template$blockinfo1)) {
                              BlockPos blockpos3 = template$blockinfo1.pos;
                              BlockPos blockpos4 = new BlockPos(
                                 blockpos2.getX() - blockpos3.getX(), blockpos2.getY() - blockpos3.getY(), blockpos2.getZ() - blockpos3.getZ()
                              );
                              BoundingBox mutableboundingbox2 = jigsawpiece1.getBoundingBox(this.templateManager, blockpos4, rotation1);
                              int j1 = mutableboundingbox2.minY();
                              Projection jigsawpattern$placementbehaviour1 = jigsawpiece1.getProjection();
                              boolean flag2 = jigsawpattern$placementbehaviour1 == Projection.RIGID;
                              int k1 = blockpos3.getY();
                              int l1 = j - k1 + JigsawBlock.getFrontFacing(template$blockinfo.state).getStepY();
                              int i2;
                              if (flag && flag2) {
                                 i2 = i + l1;
                              } else {
                                 if (k == -1) {
                                    k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Types.WORLD_SURFACE_WG, levelHeightAccessor);
                                 }

                                 i2 = k - k1;
                              }

                              int j2 = i2 - j1;
                              BoundingBox mutableboundingbox3 = mutableboundingbox2.moved(0, j2, 0);
                              BlockPos blockpos5 = blockpos4.offset(0, j2, 0);
                              if (i1 > 0) {
                                 int k2 = Math.max(i1 + 1, mutableboundingbox3.maxY() - mutableboundingbox3.minY());
                                 mutableboundingbox3.encapsulate(
                                    new BlockPos(mutableboundingbox3.minX(), mutableboundingbox3.minY() + k2, mutableboundingbox3.minZ())
                                 );
                              }

                              if (!Shapes.joinIsNotEmpty(
                                 (VoxelShape)mutableobject1.getValue(), Shapes.create(AABB.of(mutableboundingbox3).deflate(0.25)), BooleanOp.ONLY_SECOND
                              )) {
                                 mutableobject1.setValue(
                                    Shapes.joinUnoptimized(
                                       (VoxelShape)mutableobject1.getValue(), Shapes.create(AABB.of(mutableboundingbox3)), BooleanOp.ONLY_FIRST
                                    )
                                 );
                                 int j3 = p_236831_1_.getGroundLevelDelta();
                                 int l2;
                                 if (flag2) {
                                    l2 = j3 - l1;
                                 } else {
                                    l2 = jigsawpiece1.getGroundLevelDelta();
                                 }

                                 PoolElementStructurePiece abstractvillagepiece = this.pieceFactory
                                    .create(this.templateManager, jigsawpiece1, blockpos5, l2, rotation1, mutableboundingbox3);
                                 int i3;
                                 if (flag) {
                                    i3 = i + j;
                                 } else if (flag2) {
                                    i3 = i2 + k1;
                                 } else {
                                    if (k == -1) {
                                       k = this.chunkGenerator
                                          .getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Types.WORLD_SURFACE_WG, levelHeightAccessor);
                                    }

                                    i3 = k + l1 / 2;
                                 }

                                 p_236831_1_.addJunction(
                                    new JigsawJunction(blockpos2.getX(), i3 - j + j3, blockpos2.getZ(), l1, jigsawpattern$placementbehaviour1)
                                 );
                                 abstractvillagepiece.addJunction(
                                    new JigsawJunction(blockpos1.getX(), i3 - k1 + l2, blockpos1.getZ(), -l1, jigsawpattern$placementbehaviour)
                                 );
                                 if (abstractvillagepiece.getBoundingBox().minY() > 0 && abstractvillagepiece.getBoundingBox().maxY() < 256) {
                                    this.structurePieces.add(abstractvillagepiece);
                                    if (currentDepth + 1 <= this.maxDepth) {
                                       this.availablePieces.addLast(new JigsawGeneratorLegacy.Entry(abstractvillagepiece, mutableobject1, l, currentDepth + 1));
                                    }
                                 }
                                 continue label143;
                              }
                           }
                        }
                     }
                  }
               } else {
                  VaultMod.LOGGER.warn("Empty or none existent fallback pool: {}", resourcelocation1);
               }
            } else {
               VaultMod.LOGGER.warn("Empty or none existent pool: {}", resourcelocation);
            }
         }
      }
   }

   static final class Entry {
      private final PoolElementStructurePiece villagePiece;
      private final MutableObject<VoxelShape> free;
      private final int boundsTop;
      private final int depth;

      private Entry(PoolElementStructurePiece p_i232042_1_, MutableObject<VoxelShape> p_i232042_2_, int p_i232042_3_, int p_i232042_4_) {
         this.villagePiece = p_i232042_1_;
         this.free = p_i232042_2_;
         this.boundsTop = p_i232042_3_;
         this.depth = p_i232042_4_;
      }
   }
}
