package iskallia.vault.mixin;

import iskallia.vault.Vault;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Biome.class})
public abstract class MixinBiome {
   @Inject(
      method = {"generateFeatures"},
      at = {@At("HEAD")}
   )
   public void generate(
      StructureManager structureManager,
      ChunkGenerator chunkGenerator,
      WorldGenRegion worldGenRegion,
      long seed,
      SharedSeedRandom rand,
      BlockPos pos,
      CallbackInfo ci
   ) {
      this.generateVault(structureManager, chunkGenerator, worldGenRegion, seed, rand, pos);
   }

   private void generateVault(
      StructureManager structureManager, ChunkGenerator chunkGenerator, WorldGenRegion worldGenRegion, long seed, SharedSeedRandom rand, BlockPos pos
   ) {
      ServerWorld world = worldGenRegion.func_201672_e();
      VaultRaid vault = VaultRaidData.get(world).getAt(world, pos);
      if (vault != null) {
         ChunkPos startChunk = vault.getGenerator().getStartChunk();
         if ((pos.func_177958_n() >> 4 != startChunk.field_77276_a || pos.func_177952_p() >> 4 != startChunk.field_77275_b)
            && worldGenRegion.func_201672_e().func_72863_F().func_73149_a(startChunk.field_77276_a, startChunk.field_77275_b)) {
            worldGenRegion.func_201672_e()
               .func_212866_a_(startChunk.field_77276_a, startChunk.field_77275_b)
               .func_201609_c()
               .values()
               .forEach(
                  start -> start.func_230366_a_(
                     worldGenRegion,
                     structureManager,
                     chunkGenerator,
                     rand,
                     new MutableBoundingBox(pos.func_177958_n(), pos.func_177952_p(), pos.func_177958_n() + 15, pos.func_177952_p() + 15),
                     new ChunkPos(pos)
                  )
               );
         } else {
            Vault.LOGGER
               .error(
                  "Start chunk at ["
                     + startChunk.field_77276_a
                     + ", "
                     + startChunk.field_77275_b
                     + "] has no ticket. Failed to generate chunk ["
                     + (pos.func_177958_n() >> 4)
                     + ", "
                     + (pos.func_177952_p() >> 4)
                     + "]."
               );
         }
      }
   }
}
