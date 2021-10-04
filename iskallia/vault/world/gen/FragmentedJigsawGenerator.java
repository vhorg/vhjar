package iskallia.vault.world.gen;

import iskallia.vault.world.gen.structure.JigsawPieceResolver;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FragmentedJigsawGenerator implements VaultJigsawGenerator {
   public static final int START_X_Z = 21;
   public static final int ROOM_X_Z = 47;
   public static final int ROOM_Y_OFFSET = -13;
   public static final int TUNNEL_Z = 48;
   public static final int TUNNEL_X = 11;
   public static final int TUNNEL_Y_OFFSET = 6;
   private final MutableBoundingBox structureBoundingBox;
   private final BlockPos startPos;
   private final VaultRoomLayoutGenerator layoutGenerator;
   private List<StructurePiece> pieces = new ArrayList<>();

   public FragmentedJigsawGenerator(MutableBoundingBox structureBoundingBox, BlockPos startPos, VaultRoomLayoutGenerator layoutGenerator) {
      this.structureBoundingBox = structureBoundingBox;
      this.startPos = startPos;
      this.layoutGenerator = layoutGenerator;
   }

   @Override
   public MutableBoundingBox getStructureBox() {
      return this.structureBoundingBox;
   }

   @Override
   public BlockPos getStartPos() {
      return this.startPos;
   }

   @Override
   public int getSize() {
      return 0;
   }

   public VaultRoomLayoutGenerator getLayoutGenerator() {
      return this.layoutGenerator;
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
      DynamicRegistries registries,
      VillageConfig config,
      IPieceFactory pieceFactory,
      ChunkGenerator gen,
      TemplateManager manager,
      List<StructurePiece> pieceList,
      Random random,
      boolean flag1,
      boolean generateOnSurface
   ) {
      BlockPos startPos = this.getStartPos();
      Registry<JigsawPattern> registry = registries.func_243612_b(Registry.field_243555_ax);
      Rotation startRotation = Rotation.func_222466_a(random);
      Rotation vaultGenerationRotation = startRotation.func_185830_a(Rotation.CLOCKWISE_90);
      JigsawPattern starts = this.getLayoutGenerator().getStartRoomPool(registry);
      JigsawPiece startPiece = starts.func_214944_a(random);
      MutableBoundingBox startBoundingBox = startPiece.func_214852_a(manager, startPos, startRotation);
      AbstractVillagePiece start = pieceFactory.create(manager, startPiece, startPos, startPiece.func_214850_d(), startRotation, startBoundingBox);
      pieceList.add(start);
      BlockPos centerRoomPos = startPos.func_177971_a(new BlockPos(10, 0, 10).func_190942_a(startRotation))
         .func_177967_a(vaultGenerationRotation.func_185831_a(Direction.EAST), 11)
         .func_177967_a(vaultGenerationRotation.func_185831_a(Direction.EAST), 23);
      List<StructurePiece> synchronizedPieces = Collections.synchronizedList(pieceList);
      VaultRoomLayoutGenerator.Layout layout = this.layoutGenerator.generateLayout();
      JigsawPattern rooms = this.getLayoutGenerator().getRoomPool(registry);
      layout.getRooms()
         .parallelStream()
         .forEach(
            room -> {
               JigsawPiece roomPiece = room.getRandomPiece(rooms, random);
               Rotation roomRotation = Rotation.func_222466_a(random);
               BlockPos roomPos = centerRoomPos.func_177971_a(room.getAbsoluteOffset(vaultGenerationRotation, roomRotation));
               JigsawPieceResolver resolverx = JigsawPieceResolver.newResolver(roomPiece, roomPos)
                  .withRotation(roomRotation)
                  .andJigsawFilter(key -> !key.func_110623_a().contains("tunnels"));
               if (room.getRoomOffset().equals(BlockPos.field_177992_a)) {
                  resolverx.addStructureBox(AxisAlignedBB.func_216363_a(startBoundingBox).func_72314_b(1.0, 3.0, 1.0));
               }

               if (!room.canGenerateTreasureRooms()) {
                  resolverx.andJigsawFilter(key -> !key.func_110623_a().contains("treasure_rooms"));
               }

               synchronizedPieces.addAll(resolverx.resolveJigsawPieces(manager, registry));
            }
         );
      JigsawPattern tunnels = this.getLayoutGenerator().getTunnelPool(registry);

      for (VaultRoomLayoutGenerator.Tunnel tunnel : layout.getTunnels()) {
         JigsawPiece tunnelPiece = tunnel.getRandomPiece(tunnels, random);
         Rotation tunnelRotation = tunnel.getRandomConnectingRotation(random).func_185830_a(vaultGenerationRotation);
         BlockPos tunnelPos = centerRoomPos.func_177971_a(tunnel.getAbsoluteOffset(vaultGenerationRotation, tunnelRotation));
         JigsawPieceResolver resolver = JigsawPieceResolver.newResolver(tunnelPiece, tunnelPos)
            .withRotation(tunnelRotation)
            .andJigsawFilter(key -> !key.func_110623_a().contains("rooms"));
         pieceList.addAll(resolver.resolveJigsawPieces(manager, registry));
      }

      this.pieces = pieceList;
   }
}
