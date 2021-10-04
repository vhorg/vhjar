package iskallia.vault.world.gen.structure;

import iskallia.vault.Vault;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

public class VaultJigsawHelper {
   private static final Random rand = new Random();
   public static final int ROOM_WIDTH_HEIGHT = 47;
   public static final int TUNNEL_LENGTH = 48;
   private static final Predicate<ResourceLocation> TUNNEL_FILTER = key -> {
      String path = key.func_110623_a();
      return !path.contains("treasure_rooms") && !path.contains("rooms");
   };
   private static final Predicate<ResourceLocation> ROOM_FILTER = key -> {
      String path = key.func_110623_a();
      return !path.contains("treasure_rooms") && !path.contains("tunnels");
   };

   public static List<VaultPiece> expandVault(
      VaultRaid vault, ServerWorld sWorld, VaultRoom fromRoom, Direction targetDir, @Nullable JigsawPiece roomToGenerate
   ) {
      if (targetDir.func_176740_k() == Axis.Y) {
         return Collections.emptyList();
      } else {
         BlockPos side = fromRoom.getTunnelConnectorPos(targetDir);
         Mutable mutableGenPos = side.func_239590_i_();
         VaultGenerator generator = vault.getGenerator();
         JigsawPiece roomPiece = roomToGenerate == null ? getRandomRoom() : roomToGenerate;
         List<VaultPiece> pieces = new ArrayList<>();
         pieces.addAll(placeRandomTunnel(sWorld, mutableGenPos, targetDir));
         pieces.addAll(placeRandomRoom(sWorld, mutableGenPos, targetDir, roomPiece));
         generator.addPieces(pieces);
         return pieces;
      }
   }

   public static boolean canExpand(VaultRaid vault, VaultRoom fromRoom, Direction targetDir) {
      BlockPos roomCenter = new BlockPos(fromRoom.getCenter());
      if (!vault.getGenerator().getPiecesAt(roomCenter.func_177967_a(targetDir, 28)).isEmpty()) {
         return false;
      } else {
         BlockPos nextRoom = roomCenter.func_177967_a(targetDir, 95);
         BlockPos nextRoomEdge = nextRoom.func_177967_a(targetDir, 24);
         return vault.getProperties().getBase(VaultRaid.BOUNDING_BOX).map(box -> !box.func_175898_b(nextRoomEdge)).orElse(true)
            ? false
            : vault.getGenerator().getPiecesAt(nextRoom).isEmpty();
      }
   }

   private static List<VaultPiece> placeRandomRoom(ServerWorld sWorld, Mutable generationPos, Direction toCenter, JigsawPiece roomToGenerate) {
      int jigsawGroundOffset = 22;
      BlockPos at = generationPos.func_185334_h();
      Rotation roomRotation = Rotation.func_222466_a(rand);
      TemplateManager tplMgr = sWorld.func_184163_y();
      MutableBoundingBox jigsawBox = roomToGenerate.func_214852_a(tplMgr, at, roomRotation);
      Vector3i size = jigsawBox.func_175896_b();
      BlockPos directionOffset = new BlockPos(
         toCenter.func_82601_c() * size.func_177958_n() / 2, -jigsawGroundOffset + size.func_177956_o() / 2, toCenter.func_82599_e() * size.func_177952_p() / 2
      );
      BlockPos genPos = at.func_177971_a(directionOffset);
      Vector3i center = jigsawBox.func_215126_f();
      genPos = genPos.func_177971_a(at.func_177973_b(center));
      List<VaultPiece> vaultPieces = JigsawPiecePlacer.newPlacer(roomToGenerate, sWorld, genPos)
         .withRotation(roomRotation)
         .andJigsawFilter(ROOM_FILTER)
         .placeJigsaw();
      generationPos.func_189534_c(toCenter, 23);
      return vaultPieces;
   }

   private static List<VaultPiece> placeRandomTunnel(ServerWorld sWorld, Mutable generationPos, Direction targetDir) {
      int jigsawGroundOffset = 2;
      JigsawPiece tunnel = getRandomTunnel();
      BlockPos at = generationPos.func_185334_h();
      Rotation tunnelRotation = getTunnelRotation(targetDir);
      Direction shift = targetDir.func_176746_e();
      TemplateManager tplMgr = sWorld.func_184163_y();
      MutableBoundingBox jigsawBox = tunnel.func_214852_a(tplMgr, at, tunnelRotation);
      Vector3i size = jigsawBox.func_175896_b();
      BlockPos directionOffset = new BlockPos(targetDir.func_82601_c() * size.func_177958_n() / 2, 0, targetDir.func_82599_e() * size.func_177952_p() / 2);
      directionOffset = directionOffset.func_177982_a(
         -(shift.func_82601_c() * size.func_177958_n()) / 2, -size.func_177956_o() / 2 + jigsawGroundOffset, -(shift.func_82599_e() * size.func_177952_p()) / 2
      );
      BlockPos genPos = at.func_177971_a(directionOffset);
      List<VaultPiece> vaultPieces = JigsawPiecePlacer.newPlacer(tunnel, sWorld, genPos)
         .withRotation(tunnelRotation)
         .andJigsawFilter(TUNNEL_FILTER)
         .placeJigsaw();
      generationPos.func_196234_d(targetDir.func_82601_c() * size.func_177958_n() / 2, 0, targetDir.func_82599_e() * size.func_177952_p() / 2)
         .func_189536_c(targetDir);
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

   public static void preloadVaultRooms(FMLServerStartedEvent event) {
      Random rand = new Random();
      TemplateManager mgr = event.getServer().func_240792_aT_();
      getRoomJigsawPool().field_214952_d.forEach(weightedPiece -> {
         JigsawPiece piece = (JigsawPiece)weightedPiece.getFirst();
         if (piece instanceof PalettedListPoolElement) {
            for (JigsawPiece listPiece : ((PalettedListPoolElement)piece).getElements()) {
               listPiece.func_214849_a(mgr, BlockPos.field_177992_a, Rotation.func_222466_a(rand), rand);
            }
         } else {
            piece.func_214849_a(mgr, BlockPos.field_177992_a, Rotation.func_222466_a(rand), rand);
         }
      });
   }

   @Nonnull
   public static JigsawPiece getRandomTunnel() {
      return getRandomPiece(Vault.id("vault/tunnels"));
   }

   @Nonnull
   public static JigsawPiece getRandomRoom() {
      return getRandomPiece(Vault.id("vault/rooms"));
   }

   @Nonnull
   public static JigsawPiece getArchitectRoom() {
      return getRandomPiece(Vault.id("architect_event/rooms"));
   }

   @Nonnull
   public static JigsawPattern getRoomJigsawPool() {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
      MutableRegistry<JigsawPattern> jigsawRegistry = srv.func_244267_aX().func_243612_b(Registry.field_243555_ax);
      return (JigsawPattern)jigsawRegistry.func_241873_b(Vault.id("vault/rooms")).orElseThrow(RuntimeException::new);
   }

   @Nonnull
   private static JigsawPiece getRandomPiece(ResourceLocation key) {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
      MutableRegistry<JigsawPattern> jigsawRegistry = srv.func_244267_aX().func_243612_b(Registry.field_243555_ax);
      JigsawPattern pattern = (JigsawPattern)jigsawRegistry.func_241873_b(key).orElseThrow(RuntimeException::new);
      WeightedList<JigsawPiece> weightedPieces = new WeightedList<>();
      pattern.field_214952_d.forEach(weightedPiece -> weightedPieces.add((JigsawPiece)weightedPiece.getFirst(), (Integer)weightedPiece.getSecond()));
      return weightedPieces.getOptionalRandom(rand).orElseThrow(RuntimeException::new);
   }
}
