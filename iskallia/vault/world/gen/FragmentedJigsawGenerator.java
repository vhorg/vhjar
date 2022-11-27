package iskallia.vault.world.gen;

import iskallia.vault.world.gen.structure.JigsawPieceResolver;
import iskallia.vault.world.vault.gen.layout.JigsawPoolProvider;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier.Context;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement.PieceFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.phys.AABB;

public class FragmentedJigsawGenerator implements VaultJigsawGenerator {
   public static final int START_X_Z = 21;
   public static final int ROOM_X_Z = 47;
   public static final int ROOM_Y_OFFSET = -13;
   public static final int TUNNEL_Z = 48;
   public static final int TUNNEL_X = 11;
   public static final int TUNNEL_Y_OFFSET = 6;
   private final BoundingBox structureBoundingBox;
   private final BlockPos startPos;
   private final boolean generateTreasureRooms;
   private final JigsawPoolProvider jigsawPoolProvider;
   private final VaultRoomLayoutGenerator.Layout generatedLayout;
   private List<StructurePiece> pieces = new ArrayList<>();

   public FragmentedJigsawGenerator(
      BoundingBox structureBoundingBox,
      BlockPos startPos,
      boolean generateTreasureRooms,
      JigsawPoolProvider jigsawPoolProvider,
      VaultRoomLayoutGenerator.Layout generatedLayout
   ) {
      this.structureBoundingBox = structureBoundingBox;
      this.startPos = startPos;
      this.generateTreasureRooms = generateTreasureRooms;
      this.jigsawPoolProvider = jigsawPoolProvider;
      this.generatedLayout = generatedLayout;
   }

   @Override
   public BoundingBox getStructureBox() {
      return this.structureBoundingBox;
   }

   @Override
   public BlockPos getStartPos() {
      return this.startPos;
   }

   public boolean generatesTreasureRooms() {
      return this.generateTreasureRooms;
   }

   @Override
   public int getSize() {
      return 0;
   }

   public JigsawPoolProvider getJigsawPoolProvider() {
      return this.jigsawPoolProvider;
   }

   @Override
   public List<StructurePiece> getGeneratedPieces() {
      return Collections.unmodifiableList(this.pieces);
   }

   public boolean removePiece(StructurePiece piece) {
      return this.pieces.remove(piece);
   }

   @Override
   public void generate(
      RegistryAccess registries,
      Context<JigsawConfiguration> config,
      PieceFactory pieceFactory,
      ChunkGenerator gen,
      StructureManager manager,
      List<StructurePiece> pieceList,
      Random random,
      boolean flag1,
      boolean generateOnSurface
   ) {
      BlockPos startPos = this.getStartPos();
      Registry<StructureTemplatePool> registry = registries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      Rotation startRotation = Rotation.getRandom(random);
      Rotation vaultGenerationRotation = startRotation.getRotated(Rotation.CLOCKWISE_90);
      StructureTemplatePool starts = this.getJigsawPoolProvider().getStartRoomPool(registry);
      StructurePoolElement startPiece = starts.getRandomTemplate(random);
      BoundingBox startBoundingBox = startPiece.getBoundingBox(manager, startPos, startRotation);
      PoolElementStructurePiece start = pieceFactory.create(manager, startPiece, startPos, startPiece.getGroundLevelDelta(), startRotation, startBoundingBox);
      pieceList.add(start);
      BlockPos centerRoomPos = startPos.offset(new BlockPos(10, 0, 10).rotate(startRotation))
         .relative(vaultGenerationRotation.rotate(Direction.EAST), 11)
         .relative(vaultGenerationRotation.rotate(Direction.EAST), 23);
      List<StructurePiece> synchronizedPieces = Collections.synchronizedList(pieceList);
      StructureTemplatePool rooms = this.getJigsawPoolProvider().getRoomPool(registry);
      this.generatedLayout
         .getRooms()
         .parallelStream()
         .forEach(
            room -> {
               StructurePoolElement roomPiece = room.getRandomPiece(rooms, random);
               Rotation roomRotation = Rotation.getRandom(random);
               BlockPos roomPos = centerRoomPos.offset(room.getAbsoluteOffset(vaultGenerationRotation, roomRotation));
               JigsawPieceResolver resolverx = JigsawPieceResolver.newResolver(roomPiece, roomPos)
                  .withRotation(roomRotation)
                  .andJigsawFilter(key -> !key.getPath().contains("tunnels"));
               if (room.getRoomOffset().equals(BlockPos.ZERO)) {
                  resolverx.addStructureBox(AABB.of(startBoundingBox).inflate(1.0, 3.0, 1.0));
               }

               if (!this.generatesTreasureRooms() || !room.canGenerateTreasureRooms()) {
                  resolverx.andJigsawFilter(key -> !key.getPath().contains("treasure_rooms"));
               }

               synchronizedPieces.addAll(resolverx.resolveJigsawPieces(manager, registry));
            }
         );
      StructureTemplatePool tunnels = this.getJigsawPoolProvider().getTunnelPool(registry);

      for (VaultRoomLayoutGenerator.Tunnel tunnel : this.generatedLayout.getTunnels()) {
         StructurePoolElement tunnelPiece = tunnel.getRandomPiece(tunnels, random);
         Rotation tunnelRotation = tunnel.getRandomConnectingRotation(random).getRotated(vaultGenerationRotation);
         BlockPos tunnelPos = centerRoomPos.offset(tunnel.getAbsoluteOffset(vaultGenerationRotation, tunnelRotation));
         JigsawPieceResolver resolver = JigsawPieceResolver.newResolver(tunnelPiece, tunnelPos)
            .withRotation(tunnelRotation)
            .andJigsawFilter(key -> !key.getPath().contains("rooms"));
         pieceList.addAll(resolver.resolveJigsawPieces(manager, registry));
      }

      this.pieces = pieceList;
   }
}
