package iskallia.vault.world.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.Vault;
import iskallia.vault.world.gen.VaultJigsawGenerator;
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

public class FinalVaultBossStructure extends Structure<FinalVaultBossStructure.Config> {
   public static final int START_Y = 19;

   public FinalVaultBossStructure(Codec<FinalVaultBossStructure.Config> config) {
      super(config);
   }

   public Decoration func_236396_f_() {
      return Decoration.UNDERGROUND_STRUCTURES;
   }

   public IStartFactory<FinalVaultBossStructure.Config> func_214557_a() {
      return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) -> new FinalVaultBossStructure.Start(
         this, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_
      );
   }

   public static class Config implements IFeatureConfig {
      public static final Codec<FinalVaultBossStructure.Config> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               JigsawPattern.field_244392_b_.fieldOf("start_pool").forGetter(FinalVaultBossStructure.Config::getStartPool),
               Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(FinalVaultBossStructure.Config::getSize)
            )
            .apply(builder, FinalVaultBossStructure.Config::new)
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
            Vault.id("final_vault/boss"),
            new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(JigsawPiece.func_242861_b(Vault.sId("final_vault/boss"), ProcessorLists.field_244101_a), 1)),
            PlacementBehaviour.RIGID
         )
      );

      public static void init() {
      }
   }

   public static class Start extends MarginedStructureStart<FinalVaultBossStructure.Config> {
      private final FinalVaultBossStructure structure;

      public Start(FinalVaultBossStructure structure, int chunkX, int chunkZ, MutableBoundingBox box, int references, long worldSeed) {
         super(structure, chunkX, chunkZ, box, references, worldSeed);
         this.structure = structure;
      }

      public void func_230364_a_(
         DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager, int chunkX, int chunkZ, Biome biome, FinalVaultBossStructure.Config config
      ) {
         BlockPos blockpos = new BlockPos(chunkX * 16, 19, chunkZ * 16);
         FinalVaultBossStructure.Pools.init();
         JigsawGeneratorLegacy.func_242837_a(
            registry, config.toVillageConfig(), AbstractVillagePiece::new, gen, manager, blockpos, this.field_75075_a, this.field_214631_d, false, false
         );
         this.func_202500_a();
      }

      public void generate(VaultJigsawGenerator jigsaw, DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager) {
         FinalVaultBossStructure.Pools.init();
         jigsaw.generate(
            registry,
            new VaultStructure.Config(() -> (JigsawPattern)registry.func_243612_b(Registry.field_243555_ax).func_82594_a(Vault.id("final_vault/boss")), 1)
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
