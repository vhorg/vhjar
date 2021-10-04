package iskallia.vault.world.vault.gen;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.config.VaultSizeConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModStructures;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.gen.FragmentedJigsawGenerator;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.layout.DiamondRoomLayout;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutRegistry;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

public class FragmentedVaultGenerator extends VaultGenerator {
   public static final int REGION_SIZE = 8192;
   private VaultRoomLayoutGenerator layoutGenerator;

   public FragmentedVaultGenerator(ResourceLocation id) {
      super(id);
   }

   @Nonnull
   protected VaultRoomLayoutGenerator provideLayoutGenerator(VaultSizeConfig.SizeLayout layout) {
      VaultRoomLayoutGenerator generator = VaultRoomLayoutRegistry.generateLayout(layout.getLayout());
      if (generator == null) {
         generator = new DiamondRoomLayout();
      }

      generator.setSize(layout.getSize());
      return generator;
   }

   @Override
   public boolean generate(ServerWorld world, VaultRaid vault, Mutable pos) {
      MutableBoundingBox vaultBox = this.generateBoundingBox(vault, pos.func_185334_h());
      pos.func_189534_c(Direction.EAST, 8192);
      boolean raffle = vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false);
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      VaultSizeConfig.SizeLayout layout = ModConfigs.VAULT_SIZE.getLayout(level, raffle);
      if (this.layoutGenerator == null) {
         this.layoutGenerator = vault.getAllObjectives().stream().findFirst().map(VaultObjective::getCustomLayout).orElse(this.provideLayoutGenerator(layout));
      }

      this.startChunk = new ChunkPos(new BlockPos(vaultBox.func_215126_f()));
      FragmentedJigsawGenerator gen = new FragmentedJigsawGenerator(vaultBox, this.startChunk.func_206849_h().func_177982_a(0, 19, 0), this.layoutGenerator);
      StructureStart<?> start = ModFeatures.VAULT_FEATURE
         .generate(gen, world.func_241828_r(), world.func_72863_F().field_186029_c, world.func_184163_y(), 0, world.func_72905_C());
      gen.getGeneratedPieces().stream().flatMap(piece -> VaultPiece.of(piece).stream()).forEach(this.pieces::add);
      this.removeRandomObjectivePieces(vault, gen, layout.getObjectiveRoomRatio());
      world.func_217353_a(this.startChunk.field_77276_a, this.startChunk.field_77275_b, ChunkStatus.field_223226_a_, true)
         .func_230344_a_(ModStructures.VAULT_STAR, start);
      this.tick(world, vault);
      return vault.getProperties().exists(VaultRaid.START_POS) && vault.getProperties().exists(VaultRaid.START_FACING)
         ? false
         : this.findStartPosition(
            world,
            vault,
            this.startChunk,
            () -> new PortalPlacer(
               (pos1, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL
                  .func_176223_P()
                  .func_206870_a(VaultPortalBlock.field_176550_a, facing.func_176740_k()),
               (pos1, random, facing) -> Blocks.field_235406_np_.func_176223_P()
            )
         );
   }

   private void removeRandomObjectivePieces(VaultRaid vault, FragmentedJigsawGenerator generator, float objectiveRatio) {
      List<StructurePiece> obeliskPieces = generator.getGeneratedPieces().stream().filter(this::isObjectivePiece).collect(Collectors.toList());
      Collections.shuffle(obeliskPieces);
      int maxObjectives = MathHelper.func_76141_d(obeliskPieces.size() / objectiveRatio);
      int objectiveCount = vault.getAllObjectives().stream().findFirst().map(objective -> objective.modifyObjectiveCount(maxObjectives)).orElse(maxObjectives);
      int requiredCount = vault.getProperties().getBaseOrDefault(VaultRaid.CRYSTAL_DATA, CrystalData.EMPTY).getTargetObjectiveCount();
      if (requiredCount != -1) {
         objectiveCount = vault.getAllObjectives()
            .stream()
            .findFirst()
            .map(objective -> objective.modifyMinimumObjectiveCount(maxObjectives, requiredCount))
            .orElse(objectiveCount);
      }

      for (int i = objectiveCount; i < obeliskPieces.size(); i++) {
         generator.removePiece(obeliskPieces.get(i));
      }
   }

   private MutableBoundingBox generateBoundingBox(VaultRaid vault, BlockPos pos) {
      MutableBoundingBox box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).orElseGet(() -> {
         BlockPos max = pos.func_177982_a(8192, 0, 8192);
         return new MutableBoundingBox(pos.func_177958_n(), 0, pos.func_177952_p(), max.func_177958_n(), 256, max.func_177952_p());
      });
      vault.getProperties().create(VaultRaid.BOUNDING_BOX, box);
      return box;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      if (this.layoutGenerator != null) {
         tag.func_218657_a("Layout", VaultRoomLayoutRegistry.serialize(this.layoutGenerator));
      }

      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      if (nbt.func_150297_b("Layout", 10)) {
         VaultRoomLayoutGenerator layout = VaultRoomLayoutRegistry.deserialize(nbt.func_74775_l("Layout"));
         if (layout != null) {
            this.layoutGenerator = layout;
         }
      }
   }
}
