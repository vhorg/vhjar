package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.gen.structure.JigsawPiecePlacer;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.LegacyScavengerHuntObjective;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import iskallia.vault.world.vault.modifier.modifier.DecoratorAddModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.event.RegistryEvent.Register;

public class BreadcrumbFeature extends Feature<NoneFeatureConfiguration> {
   public static Feature<NoneFeatureConfiguration> INSTANCE;

   public BreadcrumbFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel world = context.level();
      BlockPos pos = context.origin();
      Random rand = context.random();
      return false;
   }

   public static void generateVaultBreadcrumb(VaultRaid vault, ServerLevel sWorld, List<VaultPiece> pieces) {
      runGeneration(
         () -> {
            Predicate<BlockPos> filter = posx -> false;
            Set<ChunkPos> chunks = new HashSet<>();

            for (VaultPiece piece : pieces) {
               BoundingBox box = piece.getBoundingBox();
               filter = filter.or(box::isInside);
               ChunkPos chMin = new ChunkPos(box.minX() >> 4, box.minZ() >> 4);
               ChunkPos chMax = new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4);

               for (int x = chMin.x; x <= chMax.x; x++) {
                  for (int z = chMin.z; z <= chMax.z; z++) {
                     chunks.add(new ChunkPos(x, z));
                  }
               }
            }

            Predicate<BlockPos> featurePlacementFilter = filter;

            for (ChunkPos pos : chunks) {
               BlockPos featurePos = pos.getWorldPosition();
               placeBreadcrumbFeatures(
                  vault, sWorld, (at, state) -> featurePlacementFilter.test(at) ? sWorld.setBlock(at, state, 2) : false, sWorld.getRandom(), featurePos
               );
            }
         }
      );
   }

   private static void placeBreadcrumbFeatures(
      VaultRaid vault, WorldGenLevel world, BiPredicate<BlockPos, BlockState> blockPlacer, Random rand, BlockPos featurePos
   ) {
      vault.getActiveObjective(LegacyScavengerHuntObjective.class).ifPresent(objective -> doTreasureSpawnPass(rand, world, blockPlacer, featurePos));
      vault.getActiveObjective(TreasureHuntObjective.class).ifPresent(objective -> doTreasureSpawnPass(rand, world, blockPlacer, featurePos));
      doChestSpawnPass(rand, world, blockPlacer, featurePos, ModBlocks.WOODEN_CHEST.defaultBlockState());
      List<VaultPlayer> runners = vault.getPlayers().stream().filter(vaultPlayer -> vaultPlayer instanceof VaultRunner).toList();
      int i = 0;

      while (i < runners.size() - 1) {
         i++;
      }

      placeChestModifierFeatures(vault, world, blockPlacer, rand, featurePos);
   }

   private static void placeChestModifierFeatures(
      VaultRaid vault, WorldGenLevel world, BiPredicate<BlockPos, BlockState> blockPlacer, Random rand, BlockPos featurePos
   ) {
      vault.withActiveModifiersFor(PlayerFilter.any(), DecoratorAddModifier.class, (decoratorAddModifier, stackSize) -> {
         for (int j = 0; j < stackSize; j++) {
            int attempts = 1;

            for (int i = 0; i < decoratorAddModifier.properties().getAttemptsPerChunk(); i++) {
               doChestSpawnPass(rand, world, blockPlacer, featurePos, ModBlocks.GILDED_CHEST.defaultBlockState(), attempts);
            }
         }
      });
   }

   private static void doTreasureSpawnPass(Random rand, LevelAccessor world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos) {
      doPlacementPass(rand, world, blockPlacer, pos, ModBlocks.SCAVENGER_TREASURE.defaultBlockState(), 45, offset -> {});
   }

   private static void doChestSpawnPass(Random rand, LevelAccessor world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos, BlockState toPlace) {
      doChestSpawnPass(rand, world, blockPlacer, pos, toPlace, 12);
   }

   private static void doChestSpawnPass(
      Random rand, LevelAccessor world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos, BlockState toPlace, int attempts
   ) {
      doPlacementPass(rand, world, blockPlacer, pos, toPlace, attempts, offset -> {});
   }

   private static void doPlacementPass(
      Random rand, LevelAccessor world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos, BlockState toPlace, int attempts, Consumer<BlockPos> pass
   ) {
      for (int i = 0; i < attempts; i++) {
         int x = rand.nextInt(16);
         int z = rand.nextInt(16);
         int y = rand.nextInt(64);
         BlockPos offset = pos.offset(x, y, z);
         BlockState state = world.getBlockState(offset);
         if (state.getBlock() == Blocks.AIR
            && world.getBlockState(offset.below()).isFaceSturdy(world, offset, Direction.UP)
            && blockPlacer.test(offset, toPlace)) {
            pass.accept(offset);
         }
      }
   }

   private static void runGeneration(Runnable run) {
      JigsawPiecePlacer.generationPlacementCount++;

      try {
         run.run();
      } finally {
         JigsawPiecePlacer.generationPlacementCount--;
      }
   }

   public static void register(Register<Feature<?>> event) {
      INSTANCE = new BreadcrumbFeature(NoneFeatureConfiguration.CODEC);
      INSTANCE.setRegistryName(VaultMod.id("breadcrumb_chest"));
      event.getRegistry().register(INSTANCE);
   }
}
