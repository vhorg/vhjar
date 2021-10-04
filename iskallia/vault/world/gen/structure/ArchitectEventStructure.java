package iskallia.vault.world.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.Vault;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.MarginedStructureStart;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.Structure.IStartFactory;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ArchitectEventStructure extends Structure<ArchitectEventStructure.Config> {
   public static final int START_Y = 19;

   public ArchitectEventStructure(Codec<ArchitectEventStructure.Config> codec) {
      super(codec);
   }

   public Decoration func_236396_f_() {
      return Decoration.UNDERGROUND_STRUCTURES;
   }

   public IStartFactory<ArchitectEventStructure.Config> func_214557_a() {
      return (structure, x, z, boundingBox, references, seed) -> new ArchitectEventStructure.Start(this, x, z, boundingBox, references, seed);
   }

   public static class Config implements IFeatureConfig {
      public static final Codec<ArchitectEventStructure.Config> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               JigsawPattern.field_244392_b_.fieldOf("start_pool").forGetter(ArchitectEventStructure.Config::getStartPool),
               Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(ArchitectEventStructure.Config::getSize)
            )
            .apply(builder, ArchitectEventStructure.Config::new)
      );
      private final Supplier<JigsawPattern> startPool;
      private final int size;

      public Config(Supplier<JigsawPattern> startPool, int size) {
         this.startPool = startPool;
         this.size = size;
      }

      public int getSize() {
         return this.size;
      }

      public Supplier<JigsawPattern> getStartPool() {
         return this.startPool;
      }

      public VillageConfig toVillageConfig() {
         return new VillageConfig(this.getStartPool(), this.getSize());
      }
   }

   public static class Pools {
      public static final JigsawPattern START = JigsawPatternRegistry.func_244094_a(
         new JigsawPattern(
            Vault.id("architect_event/starts"),
            new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(JigsawPiece.func_242861_b(Vault.sId("architect_event/starts"), ProcessorLists.field_244101_a), 1)),
            PlacementBehaviour.RIGID
         )
      );

      public static void init() {
      }
   }

   public static class Start extends MarginedStructureStart<ArchitectEventStructure.Config> {
      private final ArchitectEventStructure structure;

      public Start(ArchitectEventStructure structure, int chunkX, int chunkZ, MutableBoundingBox box, int references, long worldSeed) {
         super(structure, chunkX, chunkZ, box, references, worldSeed);
         this.structure = structure;
      }

      public void func_230364_a_(
         DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager, int chunkX, int chunkZ, Biome biome, ArchitectEventStructure.Config config
      ) {
         BlockPos blockpos = new BlockPos(chunkX * 16, 19, chunkZ * 16);
         ArchitectEventStructure.Pools.init();
         JigsawGeneratorLegacy.func_242837_a(
            registry, config.toVillageConfig(), AbstractVillagePiece::new, gen, manager, blockpos, this.field_75075_a, this.field_214631_d, false, false
         );
         this.func_202500_a();
      }

      public void generate(JigsawGenerator jigsaw, DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager) {
         VaultStructure.Pools.init();
         jigsaw.generate(
            registry,
            new VaultStructure.Config(() -> (JigsawPattern)registry.func_243612_b(Registry.field_243555_ax).func_82594_a(Vault.id("architect_event/starts")), 1)
               .toVillageConfig(),
            AbstractVillagePiece::new,
            gen,
            manager,
            this.field_75075_a,
            this.field_214631_d,
            false,
            false
         );
         this.func_202500_a();
      }
   }
}
