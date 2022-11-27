package iskallia.vault.world.gen.structure;

import com.google.common.collect.Queues;
import iskallia.vault.VaultMod;
import iskallia.vault.mixin.AccessorStructureTemplatePool;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.VaultJigsawGenerator;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier.Context;
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

public class JigsawGenerator implements VaultJigsawGenerator {
   private final BoundingBox box;
   private final BlockPos startPos;
   private final int depth;
   private List<StructurePiece> pieceList = new ArrayList<>();

   public JigsawGenerator(BoundingBox box, BlockPos pos, int depth) {
      this.box = box;
      this.startPos = pos;
      this.depth = depth;
   }

   @Override
   public BlockPos getStartPos() {
      return this.startPos;
   }

   @Override
   public BoundingBox getStructureBox() {
      return this.box;
   }

   @Override
   public int getSize() {
      return this.depth;
   }

   @Override
   public List<StructurePiece> getGeneratedPieces() {
      return this.pieceList;
   }

   public void setPieceList(List<StructurePiece> pieceList) {
      this.pieceList = pieceList;
   }

   public static JigsawGenerator.Builder builder(BoundingBox box, BlockPos pos) {
      return new JigsawGenerator.Builder(box, pos);
   }

   @Override
   public void generate(
      RegistryAccess registries,
      Context<JigsawConfiguration> config,
      PieceFactory pieceFactory,
      ChunkGenerator gen,
      StructureManager manager,
      List<StructurePiece> pieceList,
      Random random,
      boolean flag1,
      boolean flag2
   ) {
      StructureFeature.bootstrap();
      Registry<StructureTemplatePool> registry = registries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      Rotation rotation = Rotation.getRandom(random);
      StructureTemplatePool pattern = (StructureTemplatePool)((JigsawConfiguration)config.config()).startPool().value();
      StructurePoolElement startJigsaw = pattern.getRandomTemplate(random);
      PoolElementStructurePiece startPiece = pieceFactory.create(
         manager,
         startJigsaw,
         this.getStartPos(),
         startJigsaw.getGroundLevelDelta(),
         rotation,
         startJigsaw.getBoundingBox(manager, this.getStartPos(), rotation)
      );
      BoundingBox startBox = startPiece.getBoundingBox();
      int centerX = (startBox.maxX() + startBox.minX()) / 2;
      int centerZ = (startBox.maxZ() + startBox.minZ()) / 2;
      LevelHeightAccessor heightAccessor = config.heightAccessor();
      int centerY;
      if (flag2) {
         centerY = this.getStartPos().getY() + gen.getFirstFreeHeight(centerX, centerZ, Types.WORLD_SURFACE_WG, heightAccessor);
      } else {
         centerY = this.getStartPos().getY();
      }

      int offset = startBox.minY() + startPiece.getGroundLevelDelta();
      startPiece.move(0, centerY - offset, 0);
      pieceList.add(startPiece);
      int depth = this.getSize() == -1 ? ((JigsawConfiguration)config.config()).maxDepth() : this.getSize();
      if (depth > 0) {
         AABB boundingBox = new AABB(
            this.getStructureBox().minX(),
            this.getStructureBox().minY(),
            this.getStructureBox().minZ(),
            this.getStructureBox().maxX(),
            this.getStructureBox().maxY(),
            this.getStructureBox().maxZ()
         );
         MutableObject<VoxelShape> mutableBox = new MutableObject(
            Shapes.join(Shapes.create(boundingBox), Shapes.create(AABB.of(startBox)), BooleanOp.ONLY_FIRST)
         );
         JigsawGenerator.Assembler assembler = new JigsawGenerator.Assembler(registry, depth, pieceFactory, gen, manager, pieceList, random);
         assembler.availablePieces.addLast(new JigsawGenerator.Entry(startPiece, mutableBox, this.getStructureBox().maxY(), 0));

         while (!assembler.availablePieces.isEmpty()) {
            JigsawGenerator.Entry entry = assembler.availablePieces.removeFirst();
            assembler.generate(entry.villagePiece, entry.free, entry.boundsTop, entry.depth, flag1, heightAccessor);
         }
      }

      this.pieceList = pieceList;
   }

   static final class Assembler {
      private final Registry<StructureTemplatePool> registry;
      private final int maxDepth;
      private final PieceFactory pieceFactory;
      private final ChunkGenerator chunkGenerator;
      private final StructureManager templateManager;
      private final List<? super PoolElementStructurePiece> structurePieces;
      private final Random rand;
      private final Deque<JigsawGenerator.Entry> availablePieces = Queues.newArrayDeque();

      private Assembler(
         Registry<StructureTemplatePool> registry,
         int maxDepth,
         PieceFactory pieceFactory,
         ChunkGenerator chunkGenerator,
         StructureManager templateManager,
         List<? super PoolElementStructurePiece> structurePieces,
         Random rand
      ) {
         this.registry = registry;
         this.maxDepth = maxDepth;
         this.pieceFactory = pieceFactory;
         this.chunkGenerator = chunkGenerator;
         this.templateManager = templateManager;
         this.structurePieces = structurePieces;
         this.rand = rand;
      }

      private void generate(
         PoolElementStructurePiece piece,
         MutableObject<VoxelShape> shape,
         int p_236831_3_,
         int currentDepth,
         boolean p_236831_5_,
         LevelHeightAccessor heightAccessor
      ) {
         StructurePoolElement jigsawpiece = piece.getElement();
         BlockPos blockpos = piece.getPosition();
         Rotation rotation = piece.getRotation();
         Projection jigsawpattern$placementbehaviour = jigsawpiece.getProjection();
         boolean flag = jigsawpattern$placementbehaviour == Projection.RIGID;
         MutableObject<VoxelShape> mutableobject = new MutableObject();
         BoundingBox mutableboundingbox = piece.getBoundingBox();
         int i = mutableboundingbox.minY();

         label144:
         for (StructureBlockInfo template$blockinfo : jigsawpiece.getShuffledJigsawBlocks(this.templateManager, blockpos, rotation, this.rand)) {
            Direction direction = JigsawBlock.getFrontFacing(template$blockinfo.state);
            BlockPos blockpos1 = template$blockinfo.pos;
            BlockPos blockpos2 = blockpos1.relative(direction);
            int j = blockpos1.getY() - i;
            int k = -1;
            ResourceLocation resourcelocation = new ResourceLocation(template$blockinfo.nbt.getString("pool"));
            Optional<StructureTemplatePool> mainJigsawPattern = this.registry.getOptional(resourcelocation);
            if (mainJigsawPattern.isPresent() && (mainJigsawPattern.get().size() != 0 || Objects.equals(resourcelocation, Pools.EMPTY.location()))) {
               ResourceLocation resourcelocation1 = mainJigsawPattern.get().getFallback();
               Optional<StructureTemplatePool> fallbackJigsawPattern = this.registry.getOptional(resourcelocation1);
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
                     mutableobject1 = shape;
                     l = p_236831_3_;
                  }

                  WeightedList<StructurePoolElement> weightedPieces = new WeightedList<>();
                  if (currentDepth != this.maxDepth) {
                     ((AccessorStructureTemplatePool)mainJigsawPattern.get())
                        .getRawTemplates()
                        .forEach(weightedPiece -> weightedPieces.add((StructurePoolElement)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
                     ((AccessorStructureTemplatePool)fallbackJigsawPattern.get())
                        .getRawTemplates()
                        .forEach(weightedPiece -> weightedPieces.add((StructurePoolElement)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
                  } else {
                     ((AccessorStructureTemplatePool)fallbackJigsawPattern.get())
                        .getRawTemplates()
                        .forEach(weightedPiece -> weightedPieces.add((StructurePoolElement)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
                  }

                  while (!weightedPieces.isEmpty()) {
                     StructurePoolElement jigsawpiece1 = weightedPieces.removeRandom(this.rand);
                     if (jigsawpiece1 == null || jigsawpiece1 == EmptyPoolElement.INSTANCE) {
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
                                       Optional<StructureTemplatePool> optional2 = this.registry.getOptional(resourcelocation2);
                                       Optional<StructureTemplatePool> optional3 = optional2.flatMap(
                                          p_242843_1_ -> this.registry.getOptional(p_242843_1_.getFallback())
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
                                    k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Types.WORLD_SURFACE_WG, heightAccessor);
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
                                 int j3 = piece.getGroundLevelDelta();
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
                                       k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Types.WORLD_SURFACE_WG, heightAccessor);
                                    }

                                    i3 = k + l1 / 2;
                                 }

                                 piece.addJunction(new JigsawJunction(blockpos2.getX(), i3 - j + j3, blockpos2.getZ(), l1, jigsawpattern$placementbehaviour1));
                                 abstractvillagepiece.addJunction(
                                    new JigsawJunction(blockpos1.getX(), i3 - k1 + l2, blockpos1.getZ(), -l1, jigsawpattern$placementbehaviour)
                                 );
                                 if (abstractvillagepiece.getBoundingBox().minY() > 0 && abstractvillagepiece.getBoundingBox().maxY() < 256) {
                                    this.structurePieces.add(abstractvillagepiece);
                                    if (currentDepth + 1 <= this.maxDepth) {
                                       this.availablePieces.addLast(new JigsawGenerator.Entry(abstractvillagepiece, mutableobject1, l, currentDepth + 1));
                                    }
                                 }
                                 continue label144;
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

   public static class Builder {
      private final BoundingBox box;
      private final BlockPos startPos;
      private int depth = -1;

      protected Builder(BoundingBox box, BlockPos startPos) {
         this.box = box;
         this.startPos = startPos;
      }

      public JigsawGenerator.Builder setDepth(int depth) {
         this.depth = depth;
         return this;
      }

      public JigsawGenerator build() {
         return new JigsawGenerator(this.box, this.startPos, this.depth);
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
