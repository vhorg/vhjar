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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

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

   protected CompoundTag serialize() {
      return new CompoundTag();
   }

   protected void deserialize(CompoundTag tag) {
   }

   public static class Layout {
      private final Map<Vec3i, VaultRoomLayoutGenerator.Room> rooms = new HashMap<>();
      private final Set<VaultRoomLayoutGenerator.Tunnel> tunnels = new HashSet<>();

      protected void putRoom(Vec3i roomPosition) {
         this.putRoom(new VaultRoomLayoutGenerator.Room(roomPosition));
      }

      protected void putRoom(VaultRoomLayoutGenerator.Room room) {
         this.rooms.put(room.getRoomPosition(), room);
      }

      @Nullable
      public VaultRoomLayoutGenerator.Room getRoom(Vec3i v) {
         return this.rooms.get(v);
      }

      public Collection<VaultRoomLayoutGenerator.Room> getRooms() {
         return this.rooms.values();
      }

      protected void addTunnel(VaultRoomLayoutGenerator.Room from, VaultRoomLayoutGenerator.Room to) {
         this.addTunnel(new VaultRoomLayoutGenerator.Tunnel(from, to));
      }

      protected void addTunnel(VaultRoomLayoutGenerator.Tunnel tunnel) {
         this.tunnels.add(tunnel);
      }

      public Collection<VaultRoomLayoutGenerator.Tunnel> getTunnels() {
         return this.tunnels;
      }
   }

   public static class Room {
      protected final Vec3i roomPosition;
      private final JigsawPatternFilter jigsawFilter = new JigsawPatternFilter();

      public Room(Vec3i roomPosition) {
         this.roomPosition = roomPosition;
      }

      public VaultRoomLayoutGenerator.Room andFilter(Predicate<ResourceLocation> roomPieceFilter) {
         this.jigsawFilter.andMatches(roomPieceFilter);
         return this;
      }

      public Vec3i getRoomPosition() {
         return this.roomPosition;
      }

      public boolean canGenerateTreasureRooms() {
         return true;
      }

      public BlockPos getRoomOffset() {
         return new BlockPos(
            this.getRoomPosition().getX() * 47 + this.getRoomPosition().getX() * 48, 0, this.getRoomPosition().getZ() * 47 + this.getRoomPosition().getZ() * 48
         );
      }

      public BlockPos getAbsoluteOffset(Rotation vaultRotation, Rotation roomRotation) {
         return this.getRoomOffset().rotate(vaultRotation).offset(new BlockPos(-23, -13, -23).rotate(roomRotation));
      }

      public StructurePoolElement getRandomPiece(StructureTemplatePool pattern, Random random) {
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
         return this.getFrom().getRoomPosition().getX() - this.getTo().getRoomPosition().getX() == 0 ? Rotation.CLOCKWISE_180 : Rotation.CLOCKWISE_90;
      }

      public BlockPos getAbsoluteOffset(Rotation vaultRotation, Rotation tunnelRotation) {
         Vec3i from = this.getFrom().getRoomPosition();
         Vec3i to = this.getTo().getRoomPosition();
         Vec3i dir = new Vec3i(to.getX() - from.getX(), 0, to.getZ() - from.getZ());
         BlockPos relativeOffset = this.getFrom().getRoomOffset().offset(dir.getX() * 47, 0, dir.getZ() * 47);
         if (dir.getX() < 0) {
            relativeOffset = relativeOffset.offset(-1, 0, 0);
         }

         if (dir.getZ() < 0) {
            relativeOffset = relativeOffset.offset(0, 0, -1);
         }

         return relativeOffset.rotate(vaultRotation).offset(new BlockPos(-5, 6, -24).rotate(tunnelRotation));
      }

      public StructurePoolElement getRandomPiece(StructureTemplatePool pattern, Random random) {
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
