package iskallia.vault.world.vault.gen;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.JigsawGenerator;
import iskallia.vault.world.gen.structure.VaultTroveStructure;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class VaultTroveGenerator extends VaultGenerator {
   public static final int REGION_SIZE = 1024;

   public VaultTroveGenerator(ResourceLocation id) {
      super(id);
   }

   public PortalPlacer getPortalPlacer() {
      return new PortalPlacer(
         (pos, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, facing.getAxis()),
         (pos, random, facing) -> Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState()
      );
   }

   @Override
   public boolean generate(ServerLevel world, VaultRaid vault, MutableBlockPos pos) {
      BoundingBox box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).orElseGet(() -> {
         BlockPos min = pos.move(2000, 0, 0).immutable();
         BlockPos max = pos.move(1024, 0, 0).immutable();
         return new BoundingBox(min.getX(), 0, min.getZ(), max.getX(), 256, max.getZ() + 1024);
      });
      vault.getProperties().create(VaultRaid.BOUNDING_BOX, box);

      try {
         ChunkPos chunkPos = new ChunkPos(box.minX() + box.getXSpan() / 2 >> 4, box.minZ() + box.getZSpan() / 2 >> 4);
         JigsawGenerator jigsaw = JigsawGenerator.builder(box, chunkPos.getWorldPosition().offset(0, 19, 0)).setDepth(1).build();
         this.startChunk = new ChunkPos(jigsaw.getStartPos().getX() >> 4, jigsaw.getStartPos().getZ() >> 4);
         StructureStart start = ((VaultTroveStructure.Feature)ModFeatures.VAULT_TROVE_FEATURE.value())
            .generate(jigsaw, world.registryAccess(), world.getChunkSource().getGenerator(), world.getStructureManager(), 0, world.getSeed(), world);
         jigsaw.getGeneratedPieces().stream().flatMap(piece -> VaultPiece.of(piece).stream()).forEach(this.pieces::add);
         world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY, true)
            .setStartForFeature((ConfiguredStructureFeature)ModFeatures.VAULT_TROVE_FEATURE.value(), start);
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
