package iskallia.vault.world.gen.structure;

import iskallia.vault.VaultMod;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.VaultRoomLevelRestrictions;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VaultJigsawHelper {
   private static final Random rand = new Random();
   public static final int ROOM_WIDTH_HEIGHT = 47;
   public static final int TUNNEL_LENGTH = 48;
   private static final Predicate<ResourceLocation> TUNNEL_FILTER = key -> {
      String path = key.getPath();
      return !path.contains("treasure_rooms") && !path.contains("rooms");
   };
   private static final Predicate<ResourceLocation> ROOM_FILTER = key -> {
      String path = key.getPath();
      return !path.contains("treasure_rooms") && !path.contains("tunnels");
   };

   public static List<VaultPiece> expandVault(VaultRaid vault, ServerLevel sWorld, VaultRoom fromRoom, Direction targetDir) {
      return expandVault(vault, sWorld, fromRoom, targetDir, null);
   }

   public static List<VaultPiece> expandVault(
      VaultRaid vault, ServerLevel sWorld, VaultRoom fromRoom, Direction targetDir, @Nullable StructurePoolElement roomToGenerate
   ) {
      if (targetDir.getAxis() == Axis.Y) {
         return Collections.emptyList();
      } else {
         BlockPos side = fromRoom.getTunnelConnectorPos(targetDir);
         MutableBlockPos mutableGenPos = side.mutable();
         VaultGenerator generator = vault.getGenerator();
         int vaultLevel = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
         StructurePoolElement roomPiece = roomToGenerate == null ? getRandomVaultRoom(vaultLevel) : roomToGenerate;
         List<VaultPiece> pieces = new ArrayList<>();
         pieces.addAll(placeRandomTunnel(sWorld, mutableGenPos, targetDir));
         pieces.addAll(placeRandomRoom(sWorld, mutableGenPos, targetDir, roomPiece));
         generator.addPieces(pieces);
         return pieces;
      }
   }

   public static boolean canExpand(VaultRaid vault, VaultRoom fromRoom, Direction targetDir) {
      BlockPos roomCenter = new BlockPos(fromRoom.getCenter());
      if (!vault.getGenerator().getPiecesAt(roomCenter.relative(targetDir, 28)).isEmpty()) {
         return false;
      } else {
         BlockPos nextRoom = roomCenter.relative(targetDir, 95);
         BlockPos nextRoomEdge = nextRoom.relative(targetDir, 24);
         return vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).map(box -> !box.isInside(nextRoomEdge)).orElse(true)
            ? false
            : vault.getGenerator().getPiecesAt(nextRoom).isEmpty();
      }
   }

   private static List<VaultPiece> placeRandomRoom(ServerLevel sWorld, MutableBlockPos generationPos, Direction toCenter, StructurePoolElement roomToGenerate) {
      int jigsawGroundOffset = 22;
      BlockPos at = generationPos.immutable();
      Rotation roomRotation = Rotation.getRandom(rand);
      StructureManager tplMgr = sWorld.getStructureManager();
      BoundingBox jigsawBox = roomToGenerate.getBoundingBox(tplMgr, at, roomRotation);
      Vec3i size = jigsawBox.getLength();
      BlockPos directionOffset = new BlockPos(
         toCenter.getStepX() * size.getX() / 2, -jigsawGroundOffset + size.getY() / 2, toCenter.getStepZ() * size.getZ() / 2
      );
      BlockPos genPos = at.offset(directionOffset);
      Vec3i center = jigsawBox.getCenter();
      genPos = genPos.offset(at.subtract(center));
      List<VaultPiece> vaultPieces = JigsawPiecePlacer.newPlacer(roomToGenerate, sWorld, genPos)
         .withRotation(roomRotation)
         .andJigsawFilter(ROOM_FILTER)
         .placeJigsaw();
      generationPos.move(toCenter, 23);
      return vaultPieces;
   }

   private static List<VaultPiece> placeRandomTunnel(ServerLevel sWorld, MutableBlockPos generationPos, Direction targetDir) {
      int jigsawGroundOffset = 2;
      StructurePoolElement tunnel = getRandomVaultTunnel();
      BlockPos at = generationPos.immutable();
      Rotation tunnelRotation = getTunnelRotation(targetDir);
      Direction shift = targetDir.getClockWise();
      StructureManager tplMgr = sWorld.getStructureManager();
      BoundingBox jigsawBox = tunnel.getBoundingBox(tplMgr, at, tunnelRotation);
      Vec3i size = jigsawBox.getLength();
      BlockPos directionOffset = new BlockPos(targetDir.getStepX() * size.getX() / 2, 0, targetDir.getStepZ() * size.getZ() / 2);
      directionOffset = directionOffset.offset(
         -(shift.getStepX() * size.getX()) / 2, -size.getY() / 2 + jigsawGroundOffset, -(shift.getStepZ() * size.getZ()) / 2
      );
      BlockPos genPos = at.offset(directionOffset);
      List<VaultPiece> vaultPieces = JigsawPiecePlacer.newPlacer(tunnel, sWorld, genPos)
         .withRotation(tunnelRotation)
         .andJigsawFilter(TUNNEL_FILTER)
         .placeJigsaw();
      generationPos.move(targetDir.getStepX() * size.getX() / 2, 0, targetDir.getStepZ() * size.getZ() / 2).move(targetDir);
      return vaultPieces;
   }

   private static Rotation getTunnelRotation(Direction direction) {
      switch (direction) {
         case SOUTH:
            return Rotation.CLOCKWISE_180;
         case WEST:
            return Rotation.COUNTERCLOCKWISE_90;
         case EAST:
            return Rotation.CLOCKWISE_90;
         case NORTH:
         default:
            return Rotation.NONE;
      }
   }

   public static void preloadVaultRooms(ServerStartedEvent event) {
      new Random();
      StructureManager mgr = event.getServer().getStructureManager();
   }

   @Nonnull
   public static StructurePoolElement getRandomVaultTunnel() {
      return getRandomPiece(VaultMod.id("vault/tunnels"));
   }

   @Nonnull
   public static StructurePoolElement getRandomVaultRoom(int vaultLevel) {
      WeightedList<StructurePoolElement> rooms = getRoomList(VaultMod.id("vault/rooms"));
      return rooms.copyFiltered(piece -> VaultRoomLevelRestrictions.canGenerate(piece, vaultLevel)).getOptionalRandom(rand).orElseThrow(RuntimeException::new);
   }

   @Nonnull
   public static StructurePoolElement getArchitectRoom() {
      return getRandomPiece(VaultMod.id("architect_event/rooms"));
   }

   @Nonnull
   public static StructurePoolElement getRaidChallengeRoom() {
      return getRandomPiece(VaultMod.id("raid/rooms"));
   }

   @Nonnull
   public static WeightedList<StructurePoolElement> getVaultRoomList(int vaultLevel) {
      WeightedList<StructurePoolElement> rooms = getRoomList(VaultMod.id("vault/rooms"));
      return rooms.copyFiltered(piece -> VaultRoomLevelRestrictions.canGenerate(piece, vaultLevel));
   }

   @Nonnull
   private static WeightedList<StructurePoolElement> getRoomList(ResourceLocation key) {
      StructureTemplatePool roomPool = getPool(key);
      WeightedList<StructurePoolElement> pool = new WeightedList<>();
      roomPool.rawTemplates.forEach(weightedPiece -> pool.add((StructurePoolElement)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
      return pool;
   }

   @Nonnull
   private static StructureTemplatePool getPool(ResourceLocation key) {
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      Registry<StructureTemplatePool> jigsawRegistry = srv.registryAccess().registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      return (StructureTemplatePool)jigsawRegistry.getOptional(key).orElseThrow(RuntimeException::new);
   }

   @Nonnull
   private static StructurePoolElement getRandomPiece(ResourceLocation key) {
      return getRoomList(key).getOptionalRandom(rand).orElseThrow(RuntimeException::new);
   }
}
