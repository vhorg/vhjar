package iskallia.vault.world.gen.structure.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.init.ModStructures;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class PalettedListPoolElement extends StructurePoolElement {
   public static final Codec<PalettedListPoolElement> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter(piece -> piece.elements), projection(), processors())
         .apply(instance, PalettedListPoolElement::new)
   );
   private final List<StructurePoolElement> elements;
   protected final List<Holder<StructureProcessorList>> processors;

   public PalettedListPoolElement(List<StructurePoolElement> elements, Projection behaviour, List<Holder<StructureProcessorList>> processors) {
      super(behaviour);
      if (elements.isEmpty() && FMLEnvironment.production) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = elements;
         this.processors = processors;
         this.setProjectionOnEachElement(behaviour);
      }
   }

   public List<StructurePoolElement> getElements() {
      return this.elements;
   }

   protected static <E extends StructurePoolElement> RecordCodecBuilder<E, Projection> projection() {
      return Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
   }

   protected static <E extends PalettedListPoolElement> RecordCodecBuilder<E, List<Holder<StructureProcessorList>>> processors() {
      return StructureProcessorType.LIST_CODEC.listOf().fieldOf("processors").forGetter(piece -> piece.processors);
   }

   public Vec3i getSize(StructureManager p_210389_, Rotation p_210390_) {
      int i = 0;
      int j = 0;
      int k = 0;

      for (StructurePoolElement structurepoolelement : this.elements) {
         Vec3i vec3i = structurepoolelement.getSize(p_210389_, p_210390_);
         i = Math.max(i, vec3i.getX());
         j = Math.max(j, vec3i.getY());
         k = Math.max(k, vec3i.getZ());
      }

      return new Vec3i(i, j, k);
   }

   public List<StructureBlockInfo> getShuffledJigsawBlocks(StructureManager templateManager, BlockPos pos, Rotation rotation, Random random) {
      return (List<StructureBlockInfo>)(this.elements.isEmpty()
         ? new ArrayList<>()
         : this.elements.get(0).getShuffledJigsawBlocks(templateManager, pos, rotation, random));
   }

   public BoundingBox getBoundingBox(StructureManager templateManager, BlockPos pos, Rotation rotation) {
      return BoundingBox.encapsulatingBoxes(this.elements.stream().map(piece -> piece.getBoundingBox(templateManager, pos, rotation)).toList())
         .orElse(BoundingBox.fromCorners(Vec3i.ZERO, Vec3i.ZERO));
   }

   public boolean place(
      StructureManager templateManager,
      WorldGenLevel world,
      StructureFeatureManager structureManager,
      ChunkGenerator chunkGen,
      BlockPos pos1,
      BlockPos pos2,
      Rotation rotation,
      BoundingBox box,
      Random random,
      boolean keepJigsaws
   ) {
      return this.generate(templateManager, world, structureManager, chunkGen, pos1, pos2, rotation, box, random, keepJigsaws, 18);
   }

   public boolean generate(
      StructureManager templateManager,
      WorldGenLevel world,
      StructureFeatureManager structureManager,
      ChunkGenerator chunkGen,
      BlockPos pos1,
      BlockPos pos2,
      Rotation rotation,
      BoundingBox box,
      Random random,
      boolean keepJigsaws,
      int updateFlags
   ) {
      Supplier<StructureProcessorList> extra = this.getRandomProcessor(world, pos1);

      for (StructurePoolElement piece : this.elements) {
         if (piece instanceof PalettedSinglePoolElement) {
            if (!((PalettedSinglePoolElement)piece)
               .generate(extra, templateManager, world, structureManager, chunkGen, pos1, pos2, rotation, box, random, keepJigsaws, updateFlags)) {
               return false;
            }
         } else if (!piece.place(templateManager, world, structureManager, chunkGen, pos1, pos2, rotation, box, random, keepJigsaws)) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public Supplier<StructureProcessorList> getRandomProcessor(WorldGenLevel world, BlockPos pos) {
      if (this.processors.isEmpty()) {
         return null;
      } else {
         WorldgenRandom seedRand = new WorldgenRandom(new LegacyRandomSource(0L));
         seedRand.setLargeFeatureSeed(world.getSeed(), pos.getX(), pos.getZ());
         return () -> (StructureProcessorList)this.processors.get(seedRand.nextInt(this.processors.size())).value();
      }
   }

   public StructurePoolElementType<?> getType() {
      return ModStructures.PoolElements.PALETTED_LIST_POOL_ELEMENT;
   }

   public StructurePoolElement setProjection(Projection placementBehaviour) {
      super.setProjection(placementBehaviour);
      this.setProjectionOnEachElement(placementBehaviour);
      return this;
   }

   public String toString() {
      return "PalettedList[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
   }

   private void setProjectionOnEachElement(Projection p_214864_1_) {
      this.elements.forEach(p_214863_1_ -> p_214863_1_.setProjection(p_214864_1_));
   }
}
