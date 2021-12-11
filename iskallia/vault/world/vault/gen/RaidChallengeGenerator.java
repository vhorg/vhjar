package iskallia.vault.world.vault.gen;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModStructures;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.JigsawGenerator;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

public class RaidChallengeGenerator extends VaultGenerator {
   public static final int REGION_SIZE = 8192;

   public RaidChallengeGenerator(ResourceLocation id) {
      super(id);
   }

   public PortalPlacer getPortalPlacer() {
      return new PortalPlacer(
         (pos, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.func_176223_P().func_206870_a(VaultPortalBlock.field_176550_a, facing.func_176740_k()),
         (pos, random, facing) -> Blocks.field_235411_nu_.func_176223_P()
      );
   }

   @Override
   public boolean generate(ServerWorld world, VaultRaid vault, Mutable pos) {
      MutableBoundingBox box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).orElseGet(() -> {
         BlockPos min = pos.func_196234_d(2000, 0, 0).func_185334_h();
         BlockPos max = pos.func_196234_d(8192, 0, 0).func_185334_h();
         return new MutableBoundingBox(min.func_177958_n(), 0, min.func_177952_p(), max.func_177958_n(), 256, max.func_177952_p() + 8192);
      });
      vault.getProperties().create(VaultRaid.BOUNDING_BOX, box);

      try {
         ChunkPos chunkPos = new ChunkPos(box.field_78897_a + box.func_78883_b() / 2 >> 4, box.field_78896_c + box.func_78880_d() / 2 >> 4);
         JigsawGenerator jigsaw = JigsawGenerator.builder(box, chunkPos.func_206849_h().func_177982_a(0, 19, 0)).setDepth(1).build();
         this.startChunk = new ChunkPos(jigsaw.getStartPos().func_177958_n() >> 4, jigsaw.getStartPos().func_177952_p() >> 4);
         StructureStart<?> start = ModFeatures.RAID_CHALLENGE_FEATURE
            .generate(jigsaw, world.func_241828_r(), world.func_72863_F().field_186029_c, world.func_184163_y(), 0, world.func_72905_C());
         jigsaw.getGeneratedPieces().stream().flatMap(piece -> VaultPiece.of(piece).stream()).forEach(this.pieces::add);
         world.func_217353_a(chunkPos.field_77276_a, chunkPos.field_77275_b, ChunkStatus.field_223226_a_, true)
            .func_230344_a_(ModStructures.RAID_CHALLENGE, start);
         this.tick(world, vault);
         return vault.getProperties().exists(VaultRaid.START_POS) && vault.getProperties().exists(VaultRaid.START_FACING)
            ? false
            : this.findStartPosition(world, vault, chunkPos, this::getPortalPlacer);
      } catch (Exception var8) {
         var8.printStackTrace();
         return false;
      }
   }
}
