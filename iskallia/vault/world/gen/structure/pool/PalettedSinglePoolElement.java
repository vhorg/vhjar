package iskallia.vault.world.gen.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.init.ModStructures;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class PalettedSinglePoolElement extends StructurePoolElement {
   private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC = Codec.of(
      PalettedSinglePoolElement::encodeTemplate, ResourceLocation.CODEC.map(Either::left)
   );
   public static final Codec<PalettedSinglePoolElement> CODEC = RecordCodecBuilder.create(
      p_236841_0_ -> p_236841_0_.group(templateCodec(), processorsCodec(), projectionCodec()).apply(p_236841_0_, PalettedSinglePoolElement::new)
   );
   protected final Either<ResourceLocation, StructureTemplate> template;
   protected final Holder<StructureProcessorList> processors;

   private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> p_236840_0_, DynamicOps<T> p_236840_1_, T p_236840_2_) {
      Optional<ResourceLocation> optional = p_236840_0_.left();
      return !optional.isPresent()
         ? DataResult.error("Can not serialize a runtime pool element")
         : ResourceLocation.CODEC.encode(optional.get(), p_236840_1_, p_236840_2_);
   }

   protected static <E extends PalettedSinglePoolElement> RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
      return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(p_236845_0_ -> p_236845_0_.processors);
   }

   protected static <E extends PalettedSinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
      return TEMPLATE_CODEC.fieldOf("location").forGetter(p_236842_0_ -> p_236842_0_.template);
   }

   protected PalettedSinglePoolElement(
      Either<ResourceLocation, StructureTemplate> p_i242008_1_, Holder<StructureProcessorList> p_i242008_2_, Projection p_i242008_3_
   ) {
      super(p_i242008_3_);
      this.template = p_i242008_1_;
      this.processors = p_i242008_2_;
   }

   public PalettedSinglePoolElement(StructureTemplate p_210419_) {
      this(Either.right(p_210419_), ProcessorLists.EMPTY, Projection.RIGID);
   }

   public Either<ResourceLocation, StructureTemplate> getTemplate() {
      return this.template;
   }

   public StructureTemplate getTemplate(StructureManager manager) {
      return (StructureTemplate)this.template.map(manager::getOrCreate, Function.identity());
   }

   public List<StructureBlockInfo> getDataMarkers(StructureManager p_214857_1_, BlockPos p_214857_2_, Rotation p_214857_3_, boolean p_214857_4_) {
      StructureTemplate template = this.getTemplate(p_214857_1_);
      List<StructureBlockInfo> list = template.filterBlocks(
         p_214857_2_, new StructurePlaceSettings().setRotation(p_214857_3_), Blocks.STRUCTURE_BLOCK, p_214857_4_
      );
      List<StructureBlockInfo> list1 = Lists.newArrayList();

      for (StructureBlockInfo template$blockinfo : list) {
         if (template$blockinfo.nbt != null) {
            StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
            if (structuremode == StructureMode.DATA) {
               list1.add(template$blockinfo);
            }
         }
      }

      return list1;
   }

   public Vec3i getSize(StructureManager p_210493_, Rotation p_210494_) {
      StructureTemplate structuretemplate = this.getTemplate(p_210493_);
      return structuretemplate.getSize(p_210494_);
   }

   public List<StructureBlockInfo> getShuffledJigsawBlocks(StructureManager templateManager, BlockPos pos, Rotation rotation, Random random) {
      StructureTemplate template = this.getTemplate(templateManager);
      List<StructureBlockInfo> list = template.filterBlocks(pos, new StructurePlaceSettings().setRotation(rotation), Blocks.JIGSAW, true);
      Collections.shuffle(list, random);
      return list;
   }

   public BoundingBox getBoundingBox(StructureManager templateManager, BlockPos pos, Rotation rotation) {
      StructureTemplate template = this.getTemplate(templateManager);
      return template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), pos);
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
      return this.generate(null, templateManager, world, structureManager, chunkGen, pos1, pos2, rotation, box, random, keepJigsaws, 18);
   }

   public boolean generate(
      @Nullable Supplier<StructureProcessorList> extra,
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
      StructureTemplate template = this.getTemplate(templateManager);
      StructurePlaceSettings placementsettings = this.getSettings(extra, rotation, box, keepJigsaws);
      if (!template.placeInWorld(world, pos1, pos2, placementsettings, random, updateFlags)) {
         return false;
      } else {
         for (StructureBlockInfo info : StructureTemplate.processBlockInfos(
            world, pos1, pos2, placementsettings, this.getDataMarkers(templateManager, pos1, rotation, false), template
         )) {
            this.handleDataMarker(world, info, pos1, rotation, random, box);
         }

         return true;
      }
   }

   protected StructurePlaceSettings getSettings(
      @Nullable Supplier<StructureProcessorList> extra, Rotation p_230379_1_, BoundingBox p_230379_2_, boolean p_230379_3_
   ) {
      StructurePlaceSettings placementsettings = new StructurePlaceSettings();
      placementsettings.setBoundingBox(p_230379_2_);
      placementsettings.setRotation(p_230379_1_);
      placementsettings.setKnownShape(true);
      placementsettings.setIgnoreEntities(false);
      placementsettings.setFinalizeEntities(true);
      if (!p_230379_3_) {
         placementsettings.addProcessor(JigsawReplacementProcessor.INSTANCE);
      }

      ((StructureProcessorList)this.processors.value()).list().forEach(placementsettings::addProcessor);
      if (extra != null) {
         extra.get().list().forEach(placementsettings::addProcessor);
      }

      this.getProjection().getProcessors().forEach(placementsettings::addProcessor);
      return placementsettings;
   }

   public StructurePoolElementType<?> getType() {
      return ModStructures.PoolElements.PALETTED_SINGLE_POOL_ELEMENT;
   }

   public String toString() {
      return "PalettedSingle[" + this.template + "]";
   }
}
