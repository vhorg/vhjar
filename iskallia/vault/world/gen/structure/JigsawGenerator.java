package iskallia.vault.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import iskallia.vault.Vault;
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
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import org.apache.commons.lang3.mutable.MutableObject;

public class JigsawGenerator {
   public static void func_242837_a(
      DynamicRegistries p_242837_0_,
      VillageConfig p_242837_1_,
      IPieceFactory p_242837_2_,
      ChunkGenerator p_242837_3_,
      TemplateManager p_242837_4_,
      BlockPos p_242837_5_,
      List<? super AbstractVillagePiece> p_242837_6_,
      Random p_242837_7_,
      boolean p_242837_8_,
      boolean p_242837_9_
   ) {
      Structure.func_236397_g_();
      MutableRegistry<JigsawPattern> mutableregistry = p_242837_0_.func_243612_b(Registry.field_243555_ax);
      Rotation rotation = Rotation.func_222466_a(p_242837_7_);
      JigsawPattern jigsawpattern = (JigsawPattern)p_242837_1_.func_242810_c().get();
      JigsawPiece jigsawpiece = jigsawpattern.func_214944_a(p_242837_7_);
      AbstractVillagePiece abstractvillagepiece = p_242837_2_.create(
         p_242837_4_, jigsawpiece, p_242837_5_, jigsawpiece.func_214850_d(), rotation, jigsawpiece.func_214852_a(p_242837_4_, p_242837_5_, rotation)
      );
      MutableBoundingBox mutableboundingbox = abstractvillagepiece.func_74874_b();
      int i = (mutableboundingbox.field_78893_d + mutableboundingbox.field_78897_a) / 2;
      int j = (mutableboundingbox.field_78892_f + mutableboundingbox.field_78896_c) / 2;
      int k;
      if (p_242837_9_) {
         k = p_242837_5_.func_177956_o() + p_242837_3_.func_222532_b(i, j, Type.WORLD_SURFACE_WG);
      } else {
         k = p_242837_5_.func_177956_o();
      }

      int l = mutableboundingbox.field_78895_b + abstractvillagepiece.func_214830_d();
      abstractvillagepiece.func_181138_a(0, k - l, 0);
      p_242837_6_.add(abstractvillagepiece);
      if (p_242837_1_.func_236534_a_() > 0) {
         int maxRange = 1024;
         AxisAlignedBB axisalignedbb = new AxisAlignedBB(i - maxRange, k - maxRange, j - maxRange, i + maxRange + 1, k + maxRange + 1, j + maxRange + 1);
         JigsawGenerator.Assembler jigsawmanager$assembler = new JigsawGenerator.Assembler(
            mutableregistry, p_242837_1_.func_236534_a_(), p_242837_2_, p_242837_3_, p_242837_4_, p_242837_6_, p_242837_7_
         );
         jigsawmanager$assembler.availablePieces
            .addLast(
               new JigsawGenerator.Entry(
                  abstractvillagepiece,
                  new MutableObject(
                     VoxelShapes.func_197878_a(
                        VoxelShapes.func_197881_a(axisalignedbb),
                        VoxelShapes.func_197881_a(AxisAlignedBB.func_216363_a(mutableboundingbox)),
                        IBooleanFunction.field_223234_e_
                     )
                  ),
                  k + maxRange,
                  0
               )
            );

         while (!jigsawmanager$assembler.availablePieces.isEmpty()) {
            JigsawGenerator.Entry jigsawmanager$entry = jigsawmanager$assembler.availablePieces.removeFirst();
            jigsawmanager$assembler.func_236831_a_(
               jigsawmanager$entry.villagePiece, jigsawmanager$entry.free, jigsawmanager$entry.boundsTop, jigsawmanager$entry.depth, p_242837_8_
            );
         }
      }
   }

   static final class Assembler {
      private final Registry<JigsawPattern> field_242839_a;
      private final int maxDepth;
      private final IPieceFactory pieceFactory;
      private final ChunkGenerator chunkGenerator;
      private final TemplateManager templateManager;
      private final List<? super AbstractVillagePiece> structurePieces;
      private final Random rand;
      private final Deque<JigsawGenerator.Entry> availablePieces = Queues.newArrayDeque();

      private Assembler(
         Registry<JigsawPattern> p_i242005_1_,
         int p_i242005_2_,
         IPieceFactory p_i242005_3_,
         ChunkGenerator p_i242005_4_,
         TemplateManager p_i242005_5_,
         List<? super AbstractVillagePiece> p_i242005_6_,
         Random p_i242005_7_
      ) {
         this.field_242839_a = p_i242005_1_;
         this.maxDepth = p_i242005_2_;
         this.pieceFactory = p_i242005_3_;
         this.chunkGenerator = p_i242005_4_;
         this.templateManager = p_i242005_5_;
         this.structurePieces = p_i242005_6_;
         this.rand = p_i242005_7_;
      }

      private void func_236831_a_(
         AbstractVillagePiece p_236831_1_, MutableObject<VoxelShape> p_236831_2_, int p_236831_3_, int currentDepth, boolean p_236831_5_
      ) {
         JigsawPiece jigsawpiece = p_236831_1_.func_214826_b();
         BlockPos blockpos = p_236831_1_.func_214828_c();
         Rotation rotation = p_236831_1_.func_214809_Y_();
         PlacementBehaviour jigsawpattern$placementbehaviour = jigsawpiece.func_214854_c();
         boolean flag = jigsawpattern$placementbehaviour == PlacementBehaviour.RIGID;
         MutableObject<VoxelShape> mutableobject = new MutableObject();
         MutableBoundingBox mutableboundingbox = p_236831_1_.func_74874_b();
         int i = mutableboundingbox.field_78895_b;

         label143:
         for (BlockInfo template$blockinfo : jigsawpiece.func_214849_a(this.templateManager, blockpos, rotation, this.rand)) {
            Direction direction = JigsawBlock.func_235508_h_(template$blockinfo.field_186243_b);
            BlockPos blockpos1 = template$blockinfo.field_186242_a;
            BlockPos blockpos2 = blockpos1.func_177972_a(direction);
            int j = blockpos1.func_177956_o() - i;
            int k = -1;
            ResourceLocation resourcelocation = new ResourceLocation(template$blockinfo.field_186244_c.func_74779_i("pool"));
            Optional<JigsawPattern> mainJigsawPattern = this.field_242839_a.func_241873_b(resourcelocation);
            if (mainJigsawPattern.isPresent()
               && (mainJigsawPattern.get().func_214946_c() != 0 || Objects.equals(resourcelocation, JigsawPatternRegistry.field_244091_a.func_240901_a_()))) {
               ResourceLocation resourcelocation1 = mainJigsawPattern.get().func_214948_a();
               Optional<JigsawPattern> fallbackJigsawPattern = this.field_242839_a.func_241873_b(resourcelocation1);
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
                     mutableobject1 = p_236831_2_;
                     l = p_236831_3_;
                  }

                  List<JigsawPiece> list = Lists.newArrayList();
                  if (currentDepth != this.maxDepth) {
                     list.addAll(mainJigsawPattern.get().func_214943_b(this.rand));
                     list.addAll(fallbackJigsawPattern.get().func_214943_b(this.rand));
                  } else {
                     list.addAll(fallbackJigsawPattern.get().func_214943_b(this.rand));
                  }

                  for (JigsawPiece jigsawpiece1 : list) {
                     if (jigsawpiece1 == EmptyJigsawPiece.field_214856_a) {
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
                                       Optional<JigsawPattern> optional2 = this.field_242839_a.func_241873_b(resourcelocation2);
                                       Optional<JigsawPattern> optional3 = optional2.flatMap(
                                          p_242843_1_ -> this.field_242839_a.func_241873_b(p_242843_1_.func_214948_a())
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
                                 int j3 = p_236831_1_.func_214830_d();
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

                                 p_236831_1_.func_214831_a(
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
                                 continue label143;
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
