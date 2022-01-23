package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.gen.structure.JigsawPiecePlacer;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import iskallia.vault.world.vault.modifier.ChestModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegistryEvent.Register;

public class BreadcrumbFeature extends Feature<NoFeatureConfig> {
   public static Feature<NoFeatureConfig> INSTANCE;

   public BreadcrumbFeature(Codec<NoFeatureConfig> codec) {
      super(codec);
   }

   public boolean func_241855_a(ISeedReader world, ChunkGenerator gen, Random rand, BlockPos pos, NoFeatureConfig config) {
      VaultRaid vault = VaultRaidData.get(world.func_201672_e()).getAt(world.func_201672_e(), pos);
      if (vault == null) {
         return false;
      } else {
         placeBreadcrumbFeatures(vault, world, (at, state) -> world.func_180501_a(at, state, 2), rand, pos);
         return false;
      }
   }

   public static void generateVaultBreadcrumb(VaultRaid vault, ServerWorld sWorld, List<VaultPiece> pieces) {
      runGeneration(
         () -> {
            Predicate<BlockPos> filter = posx -> false;
            Set<ChunkPos> chunks = new HashSet<>();

            for (VaultPiece piece : pieces) {
               MutableBoundingBox box = piece.getBoundingBox();
               filter = filter.or(box::func_175898_b);
               ChunkPos chMin = new ChunkPos(box.field_78897_a >> 4, box.field_78896_c >> 4);
               ChunkPos chMax = new ChunkPos(box.field_78893_d >> 4, box.field_78892_f >> 4);

               for (int x = chMin.field_77276_a; x <= chMax.field_77276_a; x++) {
                  for (int z = chMin.field_77275_b; z <= chMax.field_77275_b; z++) {
                     chunks.add(new ChunkPos(x, z));
                  }
               }
            }

            Predicate<BlockPos> featurePlacementFilter = filter;

            for (ChunkPos pos : chunks) {
               BlockPos featurePos = pos.func_206849_h();
               placeBreadcrumbFeatures(
                  vault,
                  sWorld,
                  (at, state) -> featurePlacementFilter.test(at) ? sWorld.func_180501_a(at, state, 2) : false,
                  sWorld.func_201674_k(),
                  featurePos
               );
            }
         }
      );
   }

   private static void placeBreadcrumbFeatures(
      VaultRaid vault, ISeedReader world, BiPredicate<BlockPos, BlockState> blockPlacer, Random rand, BlockPos featurePos
   ) {
      vault.getActiveObjective(ScavengerHuntObjective.class).ifPresent(objective -> doTreasureSpawnPass(rand, world, blockPlacer, featurePos));
      doChestSpawnPass(rand, world, blockPlacer, featurePos, ModBlocks.VAULT_CHEST.func_176223_P());
      List<VaultPlayer> runners = vault.getPlayers().stream().filter(vaultPlayer -> vaultPlayer instanceof VaultRunner).collect(Collectors.toList());

      for (int i = 0; i < runners.size() - 1; i++) {
         doChestSpawnPass(rand, world, blockPlacer, featurePos, ModBlocks.VAULT_COOP_CHEST.func_176223_P());
      }

      placeChestModifierFeatures(vault, world, blockPlacer, rand, featurePos);
   }

   private static void placeChestModifierFeatures(
      VaultRaid vault, ISeedReader world, BiPredicate<BlockPos, BlockState> blockPlacer, Random rand, BlockPos featurePos
   ) {
      vault.getActiveModifiersFor(PlayerFilter.any(), ChestModifier.class).forEach(modifier -> {
         int attempts = modifier.getChestGenerationAttempts();

         for (int i = 0; i < modifier.getAdditionalBonusChestPasses(); i++) {
            doChestSpawnPass(rand, world, blockPlacer, featurePos, ModBlocks.VAULT_BONUS_CHEST.func_176223_P(), attempts);
         }
      });
   }

   private static void doTreasureSpawnPass(Random rand, IWorld world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos) {
      doPlacementPass(rand, world, blockPlacer, pos, ModBlocks.SCAVENGER_TREASURE.func_176223_P(), 45, offset -> {});
   }

   private static void doChestSpawnPass(Random rand, IWorld world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos, BlockState toPlace) {
      doChestSpawnPass(rand, world, blockPlacer, pos, toPlace, 12);
   }

   private static void doChestSpawnPass(
      Random rand, IWorld world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos, BlockState toPlace, int attempts
   ) {
      doPlacementPass(rand, world, blockPlacer, pos, toPlace, attempts, offset -> {});
   }

   private static void doPlacementPass(
      Random rand, IWorld world, BiPredicate<BlockPos, BlockState> blockPlacer, BlockPos pos, BlockState toPlace, int attempts, Consumer<BlockPos> pass
   ) {
      for (int i = 0; i < attempts; i++) {
         int x = rand.nextInt(16);
         int z = rand.nextInt(16);
         int y = rand.nextInt(64);
         BlockPos offset = pos.func_177982_a(x, y, z);
         BlockState state = world.func_180495_p(offset);
         if (state.func_177230_c() == Blocks.field_150350_a
            && world.func_180495_p(offset.func_177977_b()).func_224755_d(world, offset, Direction.UP)
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
      INSTANCE = new BreadcrumbFeature(NoFeatureConfig.field_236558_a_);
      INSTANCE.setRegistryName(Vault.id("breadcrumb_chest"));
      event.getRegistry().register(INSTANCE);
   }
}
