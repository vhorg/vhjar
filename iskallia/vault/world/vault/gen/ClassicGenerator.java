package iskallia.vault.world.vault.gen;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.JigsawGenerator;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class ClassicGenerator extends VaultGenerator {
   public static final int REGION_SIZE = 4096;
   protected VListNBT<Block, StringTag> frameBlocks = new VListNBT<>(
      block -> StringTag.valueOf(block.getRegistryName().toString()),
      nbt -> Registry.BLOCK.getOptional(new ResourceLocation(nbt.getAsString())).orElse(Blocks.AIR)
   );
   protected int depth = -1;

   public ClassicGenerator(ResourceLocation id) {
      super(id);
      this.frameBlocks
         .addAll(
            Arrays.asList(
               Blocks.BLACKSTONE, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
            )
         );
   }

   public PortalPlacer getPortalPlacer() {
      return new PortalPlacer(
         (pos, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, facing.getAxis()),
         (pos, random, facing) -> this.frameBlocks.get(random.nextInt(this.frameBlocks.size())).defaultBlockState()
      );
   }

   public ClassicGenerator setDepth(int depth) {
      this.depth = depth;
      return this;
   }

   @Override
   public boolean generate(ServerLevel world, VaultRaid vault, MutableBlockPos pos) {
      BoundingBox box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).orElseGet(() -> {
         BlockPos min = pos.immutable();
         BlockPos max = pos.move(4096, 0, 0).immutable();
         return new BoundingBox(min.getX(), 0, min.getZ(), max.getX(), 256, max.getZ() + 4096);
      });
      vault.getProperties().create(VaultRaid.BOUNDING_BOX, box);
      int maxObjectives = vault.getAllObjectives().stream().mapToInt(VaultObjective::getMaxObjectivePlacements).max().orElse(10);

      try {
         ChunkPos chunkPos = new ChunkPos(box.minX() + box.getXSpan() / 2 >> 4, box.minZ() + box.getZSpan() / 2 >> 4);
         JigsawGenerator jigsaw = JigsawGenerator.builder(box, chunkPos.getWorldPosition().offset(0, 19, 0)).setDepth(this.depth).build();
         this.startChunk = new ChunkPos(jigsaw.getStartPos().getX() >> 4, jigsaw.getStartPos().getZ() >> 4);
         StructureStart start = ((VaultStructure.Feature)ModFeatures.VAULT_FEATURE.value())
            .generate(jigsaw, world.registryAccess(), world.getChunkSource().getGenerator(), world.getStructureManager(), 0, world.getSeed(), world);
         jigsaw.getGeneratedPieces().stream().flatMap(piece -> VaultPiece.of(piece).stream()).forEach(this.pieces::add);
         List<StructurePiece> obeliskPieces = jigsaw.getGeneratedPieces().stream().filter(this::isObjectivePiece).collect(Collectors.toList());
         Collections.shuffle(obeliskPieces);

         for (int i = maxObjectives; i < obeliskPieces.size(); i++) {
            jigsaw.getGeneratedPieces().remove(obeliskPieces.get(i));
         }

         world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY, true)
            .setStartForFeature((ConfiguredStructureFeature)ModFeatures.VAULT_FEATURE.value(), start);
         this.tick(world, vault);
         return vault.getProperties().exists(VaultRaid.START_POS) && vault.getProperties().exists(VaultRaid.START_FACING)
            ? true
            : this.findStartPosition(world, vault, chunkPos, this::getPortalPlacer);
      } catch (Exception var11) {
         var11.printStackTrace();
         return false;
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.put("FrameBlocks", this.frameBlocks.serializeNBT());
      nbt.putInt("Depth", this.depth);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.frameBlocks.deserializeNBT(nbt.getList("FrameBlocks", 8));
      this.depth = nbt.getInt("Depth");
   }
}
