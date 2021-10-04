package iskallia.vault.world.vault.gen;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModStructures;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.JigsawGenerator;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

public class ClassicGenerator extends VaultGenerator {
   public static final int REGION_SIZE = 4096;
   protected VListNBT<Block, StringNBT> frameBlocks = new VListNBT<>(
      block -> StringNBT.func_229705_a_(block.getRegistryName().toString()),
      nbt -> Registry.field_212618_g.func_241873_b(new ResourceLocation(nbt.func_150285_a_())).orElse(Blocks.field_150350_a)
   );
   protected int depth = -1;

   public ClassicGenerator(ResourceLocation id) {
      super(id);
      this.frameBlocks
         .addAll(Arrays.asList(Blocks.field_235406_np_, Blocks.field_235406_np_, Blocks.field_235410_nt_, Blocks.field_235411_nu_, Blocks.field_235412_nv_));
   }

   public PortalPlacer getPortalPlacer() {
      return new PortalPlacer(
         (pos, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.func_176223_P().func_206870_a(VaultPortalBlock.field_176550_a, facing.func_176740_k()),
         (pos, random, facing) -> this.frameBlocks.get(random.nextInt(this.frameBlocks.size())).func_176223_P()
      );
   }

   public ClassicGenerator setDepth(int depth) {
      this.depth = depth;
      return this;
   }

   @Override
   public boolean generate(ServerWorld world, VaultRaid vault, Mutable pos) {
      MutableBoundingBox box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).orElseGet(() -> {
         BlockPos min = pos.func_185334_h();
         BlockPos max = pos.func_196234_d(4096, 0, 0).func_185334_h();
         return new MutableBoundingBox(min.func_177958_n(), 0, min.func_177952_p(), max.func_177958_n(), 256, max.func_177952_p() + 4096);
      });
      vault.getProperties().create(VaultRaid.BOUNDING_BOX, box);
      int maxObjectives = vault.getAllObjectives().stream().mapToInt(VaultObjective::getMaxObjectivePlacements).max().orElse(10);

      try {
         ChunkPos chunkPos = new ChunkPos(box.field_78897_a + box.func_78883_b() / 2 >> 4, box.field_78896_c + box.func_78880_d() / 2 >> 4);
         JigsawGenerator jigsaw = JigsawGenerator.builder(box, chunkPos.func_206849_h().func_177982_a(0, 19, 0)).setDepth(this.depth).build();
         this.startChunk = new ChunkPos(jigsaw.getStartPos().func_177958_n() >> 4, jigsaw.getStartPos().func_177952_p() >> 4);
         StructureStart<?> start = ModFeatures.VAULT_FEATURE
            .generate(jigsaw, world.func_241828_r(), world.func_72863_F().field_186029_c, world.func_184163_y(), 0, world.func_72905_C());
         jigsaw.getGeneratedPieces().stream().flatMap(piece -> VaultPiece.of(piece).stream()).forEach(this.pieces::add);
         List<StructurePiece> obeliskPieces = jigsaw.getGeneratedPieces().stream().filter(this::isObjectivePiece).collect(Collectors.toList());
         Collections.shuffle(obeliskPieces);

         for (int i = maxObjectives; i < obeliskPieces.size(); i++) {
            jigsaw.getGeneratedPieces().remove(obeliskPieces.get(i));
         }

         world.func_217353_a(chunkPos.field_77276_a, chunkPos.field_77275_b, ChunkStatus.field_223226_a_, true).func_230344_a_(ModStructures.VAULT_STAR, start);
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
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_218657_a("FrameBlocks", this.frameBlocks.serializeNBT());
      nbt.func_74768_a("Depth", this.depth);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.frameBlocks.deserializeNBT(nbt.func_150295_c("FrameBlocks", 8));
      this.depth = nbt.func_74762_e("Depth");
   }
}
