package iskallia.vault.world.gen.structure;

import com.google.common.collect.Queues;
import iskallia.vault.Vault;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.VaultJigsawGenerator;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
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
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import org.apache.commons.lang3.mutable.MutableObject;

public class JigsawGenerator implements VaultJigsawGenerator {
   private final MutableBoundingBox box;
   private final BlockPos startPos;
   private final int depth;
   private List<StructurePiece> pieceList = new ArrayList<>();

   public JigsawGenerator(MutableBoundingBox box, BlockPos pos, int depth) {
      this.box = box;
      this.startPos = pos;
      this.depth = depth;
   }

   @Override
   public BlockPos getStartPos() {
      return this.startPos;
   }

   @Override
   public MutableBoundingBox getStructureBox() {
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

   public static JigsawGenerator.Builder builder(MutableBoundingBox box, BlockPos pos) {
      return new JigsawGenerator.Builder(box, pos);
   }

   @Override
   public void generate(
      DynamicRegistries registries,
      VillageConfig config,
      IPieceFactory pieceFactory,
      ChunkGenerator gen,
      TemplateManager manager,
      List<StructurePiece> pieceList,
      Random random,
      boolean flag1,
      boolean flag2
   ) {
      Structure.func_236397_g_();
      MutableRegistry<JigsawPattern> registry = registries.func_243612_b(Registry.field_243555_ax);
      Rotation rotation = Rotation.func_222466_a(random);
      JigsawPattern pattern = (JigsawPattern)config.func_242810_c().get();
      JigsawPiece startJigsaw = pattern.func_214944_a(random);
      AbstractVillagePiece startPiece = pieceFactory.create(
         manager, startJigsaw, this.getStartPos(), startJigsaw.func_214850_d(), rotation, startJigsaw.func_214852_a(manager, this.getStartPos(), rotation)
      );
      MutableBoundingBox startBox = startPiece.func_74874_b();
      int centerX = (startBox.field_78893_d + startBox.field_78897_a) / 2;
      int centerZ = (startBox.field_78892_f + startBox.field_78896_c) / 2;
      int centerY;
      if (flag2) {
         centerY = this.getStartPos().func_177956_o() + gen.func_222532_b(centerX, centerZ, Type.WORLD_SURFACE_WG);
      } else {
         centerY = this.getStartPos().func_177956_o();
      }

      int offset = startBox.field_78895_b + startPiece.func_214830_d();
      startPiece.func_181138_a(0, centerY - offset, 0);
      pieceList.add(startPiece);
      int depth = this.getSize() == -1 ? config.func_236534_a_() : this.getSize();
      if (depth > 0) {
         AxisAlignedBB boundingBox = new AxisAlignedBB(
            this.getStructureBox().field_78897_a,
            this.getStructureBox().field_78895_b,
            this.getStructureBox().field_78896_c,
            this.getStructureBox().field_78893_d,
            this.getStructureBox().field_78894_e,
            this.getStructureBox().field_78892_f
         );
         MutableObject<VoxelShape> mutableBox = new MutableObject(
            VoxelShapes.func_197878_a(
               VoxelShapes.func_197881_a(boundingBox), VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(startBox)), IBooleanFunction.field_223234_e_
            )
         );
         JigsawGenerator.Assembler assembler = new JigsawGenerator.Assembler(registry, depth, pieceFactory, gen, manager, pieceList, random);
         assembler.availablePieces.addLast(new JigsawGenerator.Entry(startPiece, mutableBox, this.getStructureBox().field_78894_e, 0));

         while (!assembler.availablePieces.isEmpty()) {
            JigsawGenerator.Entry entry = assembler.availablePieces.removeFirst();
            assembler.generate(entry.villagePiece, entry.free, entry.boundsTop, entry.depth, flag1);
         }
      }

      this.pieceList = pieceList;
   }

   static final class Assembler {
      private final Registry<JigsawPattern> registry;
      private final int maxDepth;
      private final IPieceFactory pieceFactory;
      private final ChunkGenerator chunkGenerator;
      private final TemplateManager templateManager;
      private final List<? super AbstractVillagePiece> structurePieces;
      private final Random rand;
      private final Deque<JigsawGenerator.Entry> availablePieces = Queues.newArrayDeque();

      private Assembler(
         Registry<JigsawPattern> registry,
         int maxDepth,
         IPieceFactory pieceFactory,
         ChunkGenerator chunkGenerator,
         TemplateManager templateManager,
         List<? super AbstractVillagePiece> structurePieces,
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

      private void generate(AbstractVillagePiece piece, MutableObject<VoxelShape> shape, int p_236831_3_, int currentDepth, boolean p_236831_5_) {
         JigsawPiece jigsawpiece = piece.func_214826_b();
         BlockPos blockpos = piece.func_214828_c();
         Rotation rotation = piece.func_214809_Y_();
         PlacementBehaviour jigsawpattern$placementbehaviour = jigsawpiece.func_214854_c();
         boolean flag = jigsawpattern$placementbehaviour == PlacementBehaviour.RIGID;
         MutableObject<VoxelShape> mutableobject = new MutableObject();
         MutableBoundingBox mutableboundingbox = piece.func_74874_b();
         int i = mutableboundingbox.field_78895_b;

         label144:
         for (BlockInfo template$blockinfo : jigsawpiece.func_214849_a(this.templateManager, blockpos, rotation, this.rand)) {
            Direction direction = JigsawBlock.func_235508_h_(template$blockinfo.field_186243_b);
            BlockPos blockpos1 = template$blockinfo.field_186242_a;
            BlockPos blockpos2 = blockpos1.func_177972_a(direction);
            int j = blockpos1.func_177956_o() - i;
            int k = -1;
            ResourceLocation resourcelocation = new ResourceLocation(template$blockinfo.field_186244_c.func_74779_i("pool"));
            Optional<JigsawPattern> mainJigsawPattern = this.registry.func_241873_b(resourcelocation);
            if (mainJigsawPattern.isPresent()
               && (mainJigsawPattern.get().func_214946_c() != 0 || Objects.equals(resourcelocation, JigsawPatternRegistry.field_244091_a.func_240901_a_()))) {
               ResourceLocation resourcelocation1 = mainJigsawPattern.get().func_214948_a();
               Optional<JigsawPattern> fallbackJigsawPattern = this.registry.func_241873_b(resourcelocation1);
               if (fallbackJigsawPattern.isPresent()
                  && (
                     fallbackJigsawPattern.get().func_214946_c() != 0
                        || Objects.equals(resourcelocation1, JigsawPatternRegistry.field_244091_a.func_240901_a_())
                  )) {
                  boolean flag1 = mutableboundingbox.func_175898_b(blockpos2);
                  MutableObject<VoxelShape> mutableobject1;
                  int l;
                  if (flag1) {
                     mutableobject1 = mutableobject;
                     l = i;
                     if (mutableobject.getValue() == null) {
                        mutableobject.setValue(VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(mutableboundingbox)));
                     }
                  } else {
                     mutableobject1 = shape;
                     l = p_236831_3_;
                  }

                  WeightedList<JigsawPiece> weightedPieces = new WeightedList<>();
                  if (currentDepth != this.maxDepth) {
                     mainJigsawPattern.get()
                        .field_214952_d
                        .forEach(weightedPiece -> weightedPieces.add((JigsawPiece)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
                     fallbackJigsawPattern.get()
                        .field_214952_d
                        .forEach(weightedPiece -> weightedPieces.add((JigsawPiece)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
                  } else {
                     fallbackJigsawPattern.get()
                        .field_214952_d
                        .forEach(weightedPiece -> weightedPieces.add((JigsawPiece)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
                  }

                  while (!weightedPieces.isEmpty()) {
                     JigsawPiece jigsawpiece1 = weightedPieces.removeRandom(this.rand);
                     if (jigsawpiece1 == null || jigsawpiece1 == EmptyJigsawPiece.field_214856_a) {
                        break;
                     }

                     for (Rotation rotation1 : Rotation.func_222467_b(this.rand)) {
                        List<BlockInfo> list1 = jigsawpiece1.func_214849_a(this.templateManager, BlockPos.field_177992_a, rotation1, this.rand);
                        MutableBoundingBox mutableboundingbox1 = jigsawpiece1.func_214852_a(this.templateManager, BlockPos.field_177992_a, rotation1);
                        int i1;
                        if (p_236831_5_ && mutableboundingbox1.func_78882_c() <= 16) {
                           i1 = list1.stream()
                              .mapToInt(
                                 p_242841_2_ -> {
                                    if (!mutableboundingbox1.func_175898_b(
                                       p_242841_2_.field_186242_a.func_177972_a(JigsawBlock.func_235508_h_(p_242841_2_.field_186243_b))
                                    )) {
                                       return 0;
                                    } else {
                                       ResourceLocation resourcelocation2 = new ResourceLocation(p_242841_2_.field_186244_c.func_74779_i("pool"));
                                       Optional<JigsawPattern> optional2 = this.registry.func_241873_b(resourcelocation2);
                                       Optional<JigsawPattern> optional3 = optional2.flatMap(
                                          p_242843_1_ -> this.registry.func_241873_b(p_242843_1_.func_214948_a())
                                       );
                                       int k3 = optional2.<Integer>map(p_242842_1_ -> p_242842_1_.func_214945_a(this.templateManager)).orElse(0);
                                       int l3 = optional3.<Integer>map(p_242840_1_ -> p_242840_1_.func_214945_a(this.templateManager)).orElse(0);
                                       return Math.max(k3, l3);
                                    }
                                 }
                              )
                              .max()
                              .orElse(0);
                        } else {
                           i1 = 0;
                        }

                        for (BlockInfo template$blockinfo1 : list1) {
                           if (JigsawBlock.func_220171_a(template$blockinfo, template$blockinfo1)) {
                              BlockPos blockpos3 = template$blockinfo1.field_186242_a;
                              BlockPos blockpos4 = new BlockPos(
                                 blockpos2.func_177958_n() - blockpos3.func_177958_n(),
                                 blockpos2.func_177956_o() - blockpos3.func_177956_o(),
                                 blockpos2.func_177952_p() - blockpos3.func_177952_p()
                              );
                              MutableBoundingBox mutableboundingbox2 = jigsawpiece1.func_214852_a(this.templateManager, blockpos4, rotation1);
                              int j1 = mutableboundingbox2.field_78895_b;
                              PlacementBehaviour jigsawpattern$placementbehaviour1 = jigsawpiece1.func_214854_c();
                              boolean flag2 = jigsawpattern$placementbehaviour1 == PlacementBehaviour.RIGID;
                              int k1 = blockpos3.func_177956_o();
                              int l1 = j - k1 + JigsawBlock.func_235508_h_(template$blockinfo.field_186243_b).func_96559_d();
                              int i2;
                              if (flag && flag2) {
                                 i2 = i + l1;
                              } else {
                                 if (k == -1) {
                                    k = this.chunkGenerator.func_222532_b(blockpos1.func_177958_n(), blockpos1.func_177952_p(), Type.WORLD_SURFACE_WG);
                                 }

                                 i2 = k - k1;
                              }

                              int j2 = i2 - j1;
                              MutableBoundingBox mutableboundingbox3 = mutableboundingbox2.func_215127_b(0, j2, 0);
                              BlockPos blockpos5 = blockpos4.func_177982_a(0, j2, 0);
                              if (i1 > 0) {
                                 int k2 = Math.max(i1 + 1, mutableboundingbox3.field_78894_e - mutableboundingbox3.field_78895_b);
                                 mutableboundingbox3.field_78894_e = mutableboundingbox3.field_78895_b + k2;
                              }

                              if (!VoxelShapes.func_197879_c(
                                 (VoxelShape)mutableobject1.getValue(),
                                 VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(mutableboundingbox3).func_186664_h(0.25)),
                                 IBooleanFunction.field_223232_c_
                              )) {
                                 mutableobject1.setValue(
                                    VoxelShapes.func_197882_b(
                                       (VoxelShape)mutableobject1.getValue(),
                                       VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(mutableboundingbox3)),
                                       IBooleanFunction.field_223234_e_
                                    )
                                 );
                                 int j3 = piece.func_214830_d();
                                 int l2;
                                 if (flag2) {
                                    l2 = j3 - l1;
                                 } else {
                                    l2 = jigsawpiece1.func_214850_d();
                                 }

                                 AbstractVillagePiece abstractvillagepiece = this.pieceFactory
                                    .create(this.templateManager, jigsawpiece1, blockpos5, l2, rotation1, mutableboundingbox3);
                                 int i3;
                                 if (flag) {
                                    i3 = i + j;
                                 } else if (flag2) {
                                    i3 = i2 + k1;
                                 } else {
                                    if (k == -1) {
                                       k = this.chunkGenerator.func_222532_b(blockpos1.func_177958_n(), blockpos1.func_177952_p(), Type.WORLD_SURFACE_WG);
                                    }

                                    i3 = k + l1 / 2;
                                 }

                                 piece.func_214831_a(
                                    new JigsawJunction(blockpos2.func_177958_n(), i3 - j + j3, blockpos2.func_177952_p(), l1, jigsawpattern$placementbehaviour1)
                                 );
                                 abstractvillagepiece.func_214831_a(
                                    new JigsawJunction(
                                       blockpos1.func_177958_n(), i3 - k1 + l2, blockpos1.func_177952_p(), -l1, jigsawpattern$placementbehaviour
                                    )
                                 );
                                 if (abstractvillagepiece.func_74874_b().field_78895_b > 0 && abstractvillagepiece.func_74874_b().field_78894_e < 256) {
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
                  Vault.LOGGER.warn("Empty or none existent fallback pool: {}", resourcelocation1);
               }
            } else {
               Vault.LOGGER.warn("Empty or none existent pool: {}", resourcelocation);
            }
         }
      }
   }

   public static class Builder {
      private final MutableBoundingBox box;
      private final BlockPos startPos;
      private int depth = -1;

      protected Builder(MutableBoundingBox box, BlockPos startPos) {
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
      private final AbstractVillagePiece villagePiece;
      private final MutableObject<VoxelShape> free;
      private final int boundsTop;
      private final int depth;

      private Entry(AbstractVillagePiece p_i232042_1_, MutableObject<VoxelShape> p_i232042_2_, int p_i232042_3_, int p_i232042_4_) {
         this.villagePiece = p_i232042_1_;
         this.free = p_i232042_2_;
         this.boundsTop = p_i232042_3_;
         this.depth = p_i232042_4_;
      }
   }
}
