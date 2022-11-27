package iskallia.vault.world.vault.gen;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.config.VaultSizeConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.gen.FragmentedJigsawGenerator;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.layout.DiamondRoomLayout;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutRegistry;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class FragmentedVaultGenerator extends VaultGenerator {
   public static final int REGION_SIZE = 8192;
   private VaultRoomLayoutGenerator layoutGenerator;
   private VaultSizeConfig.SizeLayout layout;

   public FragmentedVaultGenerator(ResourceLocation id) {
      super(id);
   }

   public FragmentedVaultGenerator setLayout(VaultSizeConfig.SizeLayout layout) {
      this.layout = layout;
      return this;
   }

   @Nonnull
   protected VaultRoomLayoutGenerator provideLayoutGenerator(VaultSizeConfig.SizeLayout layout) {
      VaultRoomLayoutGenerator generator = VaultRoomLayoutRegistry.getLayoutGenerator(layout.getLayout());
      if (generator == null) {
         generator = new DiamondRoomLayout();
      }

      generator.setSize(layout.getSize());
      return generator;
   }

   @Override
   public boolean generate(ServerLevel world, VaultRaid vault, MutableBlockPos pos) {
      BoundingBox vaultBox = this.generateBoundingBox(vault, pos.immutable());
      pos.move(Direction.EAST, 8192);
      boolean raffle = vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false);
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      boolean generatesTreasureRooms = vault.getProperties().getBase(VaultRaid.CRYSTAL_DATA).map(CrystalData::canGenerateTreasureRooms).orElse(true);
      VaultSizeConfig.SizeLayout layout = this.layout != null ? this.layout : ModConfigs.VAULT_SIZE.getLayout(level, raffle);
      if (this.layoutGenerator == null) {
         this.layoutGenerator = vault.getAllObjectives().stream().findFirst().map(VaultObjective::getCustomLayout).orElse(this.provideLayoutGenerator(layout));
      }

      VaultRoomLayoutGenerator.Layout vaultLayout = this.layoutGenerator.generateLayout();
      this.setGuaranteedRooms(vaultLayout, vault);
      VaultRoomLevelRestrictions.addGenerationPreventions(vaultLayout, level);
      this.startChunk = new ChunkPos(new BlockPos(vaultBox.getCenter()));
      FragmentedJigsawGenerator gen = new FragmentedJigsawGenerator(
         vaultBox, this.startChunk.getWorldPosition().offset(0, 19, 0), generatesTreasureRooms, this.layoutGenerator, vaultLayout
      );
      StructureStart start = ((VaultStructure.Feature)ModFeatures.VAULT_FEATURE.value())
         .generate(gen, world.registryAccess(), world.getChunkSource().getGenerator(), world.getStructureManager(), 0, world.getSeed(), world);
      gen.getGeneratedPieces().stream().flatMap(piece -> VaultPiece.of(piece).stream()).forEach(this.pieces::add);
      this.removeRandomObjectivePieces(vault, gen, layout.getObjectiveRoomRatio());
      world.getChunk(this.startChunk.x, this.startChunk.z, ChunkStatus.EMPTY, true)
         .setStartForFeature((ConfiguredStructureFeature)ModFeatures.VAULT_FEATURE.value(), start);
      this.tick(world, vault);
      return vault.getProperties().exists(VaultRaid.START_POS) && vault.getProperties().exists(VaultRaid.START_FACING)
         ? false
         : this.findStartPosition(
            world,
            vault,
            this.startChunk,
            () -> new PortalPlacer(
               (pos1, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, facing.getAxis()),
               (pos1, random, facing) -> Blocks.BLACKSTONE.defaultBlockState()
            )
         );
   }

   private void setGuaranteedRooms(VaultRoomLayoutGenerator.Layout vaultLayout, VaultRaid vault) {
      CrystalData data = vault.getProperties().getBaseOrDefault(VaultRaid.CRYSTAL_DATA, CrystalData.EMPTY);
      Collection<VaultRoomLayoutGenerator.Room> rooms = vaultLayout.getRooms();
      List<String> roomKeys = data.getGuaranteedRoomFilters();
      if (roomKeys.size() > rooms.size()) {
         roomKeys = roomKeys.subList(0, rooms.size());
      }

      Set<Vec3i> usedRooms = new HashSet<>();
      roomKeys.forEach(roomKey -> {
         if (VaultRoomNames.getName(roomKey) != null) {
            VaultRoomLayoutGenerator.Room room;
            do {
               room = MiscUtils.getRandomEntry(vaultLayout.getRooms(), rand);
            } while (room == null || usedRooms.contains(room.getRoomPosition()));

            usedRooms.add(room.getRoomPosition());
            room.andFilter(key -> key.getPath().contains(roomKey));
         }
      });
   }

   private void removeRandomObjectivePieces(VaultRaid vault, FragmentedJigsawGenerator generator, float objectiveRatio) {
      List<StructurePiece> obeliskPieces = generator.getGeneratedPieces().stream().filter(this::isObjectivePiece).collect(Collectors.toList());
      Collections.shuffle(obeliskPieces);
      int maxObjectives = Mth.floor(obeliskPieces.size() / objectiveRatio);
      int objectiveCount = vault.getAllObjectives().stream().findFirst().map(objective -> objective.modifyObjectiveCount(maxObjectives)).orElse(maxObjectives);

      for (int i = objectiveCount; i < obeliskPieces.size(); i++) {
         generator.removePiece(obeliskPieces.get(i));
      }
   }

   private BoundingBox generateBoundingBox(VaultRaid vault, BlockPos pos) {
      BoundingBox box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).orElseGet(() -> {
         BlockPos max = pos.offset(8192, 0, 8192);
         return new BoundingBox(pos.getX(), 0, pos.getZ(), max.getX(), 256, max.getZ());
      });
      vault.getProperties().create(VaultRaid.BOUNDING_BOX, box);
      return box;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      if (this.layoutGenerator != null) {
         tag.put("Layout", VaultRoomLayoutRegistry.serialize(this.layoutGenerator));
      }

      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      if (nbt.contains("Layout", 10)) {
         VaultRoomLayoutGenerator layout = VaultRoomLayoutRegistry.deserialize(nbt.getCompound("Layout"));
         if (layout != null) {
            this.layoutGenerator = layout;
         }
      }
   }
}
