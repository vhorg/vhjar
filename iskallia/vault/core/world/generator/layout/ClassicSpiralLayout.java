package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Rotation;

public class ClassicSpiralLayout extends ClassicInfiniteLayout {
   public static final SupplierKey<GridLayout> KEY = SupplierKey.of("classic_spiral_vault", GridLayout.class).with(Version.v1_0, ClassicSpiralLayout::new);
   public static final FieldRegistry FIELDS = ClassicInfiniteLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> HALF_LENGTH = FieldKey.of("half_length", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Rotation> ROTATION = FieldKey.of("rotation", Rotation.class)
      .with(Version.v1_0, Adapter.ofOrdinal(r -> r.ordinal() >> 1, Rotation.CLOCKWISE_90, Rotation.COUNTERCLOCKWISE_90), DISK.all())
      .register(FIELDS);

   protected ClassicSpiralLayout() {
   }

   public ClassicSpiralLayout(int tunnelSpan, Rotation rotation) {
      super(tunnelSpan);
      this.set(ROTATION, rotation);
   }

   public ClassicSpiralLayout(int tunnelSpan, int halfLength, Rotation rotation) {
      super(tunnelSpan);
      this.set(HALF_LENGTH, Integer.valueOf(halfLength));
      this.set(ROTATION, rotation);
   }

   @Override
   public SupplierKey<GridLayout> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public VaultLayout.PieceType getType(Vault vault, RegionPos region) {
      int x = region.getX();
      int z = region.getZ();
      int unit = this.get(TUNNEL_SPAN) + 1;
      VaultLayout.PieceType type = super.getType(vault, region);
      if (this.has(HALF_LENGTH)) {
         int distance = Math.max(Math.abs(region.getX()), Math.abs(region.getZ()));
         if (distance > this.get(HALF_LENGTH) * unit) {
            return VaultLayout.PieceType.NONE;
         }
      }

      if (!type.isTunnel()) {
         return type;
      } else {
         Direction facing = vault.get(Vault.WORLD).get(WorldManager.FACING);
         Direction side = this.get(ROTATION).rotate(facing);
         int ox = 2 * (x / unit) + (type == VaultLayout.PieceType.TUNNEL_Z ? 0 : (x < 0 ? -1 : 1));
         int oz = 2 * (z / unit) + (type == VaultLayout.PieceType.TUNNEL_X ? 0 : (z < 0 ? -1 : 1));
         switch (facing) {
            case EAST:
               if (side == Direction.NORTH) {
                  oz *= -1;
               }
               break;
            case WEST:
               ox *= -1;
               if (side == Direction.NORTH) {
                  oz *= -1;
               }
               break;
            case NORTH:
               int tempx = ox;
               ox = -oz;
               oz = -tempx;
               if (side == Direction.EAST) {
                  oz *= -1;
               }
               break;
            case SOUTH:
               int temp = ox;
               ox = oz;
               oz = temp;
               if (side == Direction.WEST) {
                  oz = temp * -1;
               }
         }

         VaultLayout.PieceType oType = facing.getAxis() == Axis.Z ? type.rotate(Rotation.CLOCKWISE_90) : type;
         if (oType == VaultLayout.PieceType.TUNNEL_X) {
            int min = 1 - Math.abs(oz);
            int max = oz > 0 ? oz - 1 : 1 - oz;
            if (ox < min || ox > max) {
               return VaultLayout.PieceType.NONE;
            }
         } else if (oType == VaultLayout.PieceType.TUNNEL_Z) {
            if (ox == 0) {
               return VaultLayout.PieceType.NONE;
            }

            int min = ox > 0 ? 3 - ox : ox + 1;
            int max = Math.abs(ox) - 1;
            if (oz < min || oz > max) {
               return VaultLayout.PieceType.NONE;
            }
         }

         return type;
      }
   }
}
