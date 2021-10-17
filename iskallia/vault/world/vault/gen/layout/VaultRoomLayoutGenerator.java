package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.world.gen.structure.JigsawPatternFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

public abstract class VaultRoomLayoutGenerator implements JigsawPoolProvider {
   private final ResourceLocation id;

   protected VaultRoomLayoutGenerator(ResourceLocation id) {
      this.id = id;
   }

   public final ResourceLocation getId() {
      return this.id;
   }

   public abstract void setSize(int var1);

   public abstract VaultRoomLayoutGenerator.Layout generateLayout();

   protected CompoundNBT serialize() {
      return new CompoundNBT();
   }

   protected void deserialize(CompoundNBT tag) {
   }

   public static class Layout {
      private final Map<Vector3i, VaultRoomLayoutGenerator.Room> rooms = new HashMap<>();
      private final Set<VaultRoomLayoutGenerator.Tunnel> tunnels = new HashSet<>();

      protected void putRoom(Vector3i roomPosition) {
         this.putRoom(new VaultRoomLayoutGenerator.Room(roomPosition));
      }

      protected void putRoom(VaultRoomLayoutGenerator.Room room) {
         this.rooms.put(room.getRoomPosition(), room);
      }

      @Nullable
      public VaultRoomLayoutGenerator.Room getRoom(Vector3i v) {
         return this.rooms.get(v);
      }

      public Collection<VaultRoomLayoutGenerator.Room> getRooms() {
         return this.rooms.values();
      }

      protected void addTunnel(VaultRoomLayoutGenerator.Tunnel tunnel) {
         this.tunnels.add(tunnel);
      }

      public Collection<VaultRoomLayoutGenerator.Tunnel> getTunnels() {
         return this.tunnels;
      }
   }

   public static class Room {
      protected final Vector3i roomPosition;
      private final JigsawPatternFilter jigsawFilter = new JigsawPatternFilter();

      public Room(Vector3i roomPosition) {
         this.roomPosition = roomPosition;
      }

      public VaultRoomLayoutGenerator.Room andFilter(Predicate<ResourceLocation> roomPieceFilter) {
         this.jigsawFilter.andMatches(roomPieceFilter);
         return this;
      }

      public Vector3i getRoomPosition() {
         return this.roomPosition;
      }

      public boolean canGenerateTreasureRooms() {
         return true;
      }

      public BlockPos getRoomOffset() {
         return new BlockPos(
            this.getRoomPosition().func_177958_n() * 47 + this.getRoomPosition().func_177958_n() * 48,
            0,
            this.getRoomPosition().func_177952_p() * 47 + this.getRoomPosition().func_177952_p() * 48
         );
      }

      public BlockPos getAbsoluteOffset(Rotation vaultRotation, Rotation roomRotation) {
         return this.getRoomOffset().func_190942_a(vaultRotation).func_177971_a(new BlockPos(-23, -13, -23).func_190942_a(roomRotation));
      }

      public JigsawPiece getRandomPiece(JigsawPattern pattern, Random random) {
         return this.jigsawFilter.getRandomPiece(pattern, random);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            VaultRoomLayoutGenerator.Room room = (VaultRoomLayoutGenerator.Room)o;
            return Objects.equals(this.roomPosition, room.roomPosition);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.roomPosition);
      }
   }

   public static class Tunnel {
      private final VaultRoomLayoutGenerator.Room from;
      private final VaultRoomLayoutGenerator.Room to;
      private final JigsawPatternFilter jigsawFilter = new JigsawPatternFilter();

      public Tunnel(VaultRoomLayoutGenerator.Room from, VaultRoomLayoutGenerator.Room to) {
         this.from = from;
         this.to = to;
      }

      public Tunnel(VaultRoomLayoutGenerator.Room from, VaultRoomLayoutGenerator.Room to, Predicate<ResourceLocation> tunnelPieceFilter) {
         this(from, to);
         this.jigsawFilter.andMatches(tunnelPieceFilter);
      }

      public VaultRoomLayoutGenerator.Room getFrom() {
         return this.from;
      }

      public VaultRoomLayoutGenerator.Room getTo() {
         return this.to;
      }

      public Rotation getRandomConnectingRotation(Random random) {
         return this.getFrom().getRoomPosition().func_177958_n() - this.getTo().getRoomPosition().func_177958_n() == 0
            ? Rotation.CLOCKWISE_180
            : Rotation.CLOCKWISE_90;
      }

      public BlockPos getAbsoluteOffset(Rotation vaultRotation, Rotation tunnelRotation) {
         Vector3i from = this.getFrom().getRoomPosition();
         Vector3i to = this.getTo().getRoomPosition();
         Vector3i dir = new Vector3i(to.func_177958_n() - from.func_177958_n(), 0, to.func_177952_p() - from.func_177952_p());
         BlockPos relativeOffset = this.getFrom().getRoomOffset().func_177982_a(dir.func_177958_n() * 47, 0, dir.func_177952_p() * 47);
         if (dir.func_177958_n() < 0) {
            relativeOffset = relativeOffset.func_177982_a(-1, 0, 0);
         }

         if (dir.func_177952_p() < 0) {
            relativeOffset = relativeOffset.func_177982_a(0, 0, -1);
         }

         return relativeOffset.func_190942_a(vaultRotation).func_177971_a(new BlockPos(-5, 6, -24).func_190942_a(tunnelRotation));
      }

      public JigsawPiece getRandomPiece(JigsawPattern pattern, Random random) {
         return this.jigsawFilter.getRandomPiece(pattern, random);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            VaultRoomLayoutGenerator.Tunnel tunnel = (VaultRoomLayoutGenerator.Tunnel)o;
            return Objects.equals(this.from, tunnel.from) && Objects.equals(this.to, tunnel.to)
               || Objects.equals(this.from, tunnel.to) && Objects.equals(this.to, tunnel.from);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.from.hashCode() ^ this.to.hashCode();
      }
   }
}
