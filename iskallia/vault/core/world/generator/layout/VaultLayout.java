package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.template.Template;
import java.util.Iterator;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Rotation;

public interface VaultLayout {
   Iterator<VaultLayout.LayoutEntry> expandingIterator(Vault var1, int var2);

   public record LayoutEntry(VaultLayout.PieceType type, Template template) {
   }

   public static enum PieceType {
      NONE,
      START,
      START_NORTH,
      START_SOUTH,
      START_WEST,
      START_EAST,
      ROOM,
      TUNNEL_X,
      TUNNEL_Z;

      public boolean isStart() {
         return this == START || this == START_NORTH || this == START_SOUTH || this == START_WEST || this == START_EAST;
      }

      public boolean isTunnel() {
         return this == TUNNEL_X || this == TUNNEL_Z;
      }

      public boolean connectsToTunnel() {
         return this.isStart() || this == ROOM;
      }

      public VaultLayout.PieceType rotate(Rotation rotation) {
         return switch (this) {
            case NONE -> NONE;
            case START -> START;
            case START_NORTH -> ofStart(rotation.rotate(Direction.NORTH));
            case START_SOUTH -> ofStart(rotation.rotate(Direction.SOUTH));
            case START_WEST -> ofStart(rotation.rotate(Direction.WEST));
            case START_EAST -> ofStart(rotation.rotate(Direction.EAST));
            case ROOM -> ROOM;
            case TUNNEL_X -> ofTunnel(rotation.rotate(Direction.WEST));
            case TUNNEL_Z -> ofTunnel(rotation.rotate(Direction.NORTH));
         };
      }

      public static VaultLayout.PieceType ofStart(Direction facing) {
         return switch (facing) {
            case NORTH -> START_NORTH;
            case SOUTH -> START_SOUTH;
            case WEST -> START_WEST;
            case EAST -> START_EAST;
            default -> throw new UnsupportedOperationException("Start cannot face " + facing);
         };
      }

      public static VaultLayout.PieceType ofTunnel(Direction direction) {
         return ofTunnel(direction.getAxis());
      }

      public static VaultLayout.PieceType ofTunnel(Axis axis) {
         return switch (axis) {
            case X -> TUNNEL_X;
            case Z -> TUNNEL_Z;
            default -> throw new UnsupportedOperationException("Tunnel cannot be aligned on " + axis);
         };
      }
   }
}
