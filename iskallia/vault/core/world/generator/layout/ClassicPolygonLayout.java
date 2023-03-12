package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.compound.IntList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;

public class ClassicPolygonLayout extends ClassicInfiniteLayout {
   public static final SupplierKey<GridLayout> KEY = SupplierKey.of("classic_polygon_vault", GridLayout.class).with(Version.v1_0, ClassicPolygonLayout::new);
   public static final FieldRegistry FIELDS = ClassicInfiniteLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<IntList> VERTICES = FieldKey.of("vertices", IntList.class)
      .with(Version.v1_0, CompoundAdapter.of(() -> IntList.createSegmented(7)), DISK.all())
      .register(FIELDS);

   protected ClassicPolygonLayout() {
   }

   public ClassicPolygonLayout(int tunnelSpan) {
      super(tunnelSpan);
   }

   public ClassicPolygonLayout(int tunnelSpan, int... vertices) {
      this(tunnelSpan);
      this.set(VERTICES, IntList.createSegmented(7));

      for (int coord : vertices) {
         this.get(VERTICES).add(Integer.valueOf(coord));
      }
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
      if (!this.containsPoint(x / unit, z / unit)) {
         return VaultLayout.PieceType.NONE;
      } else {
         VaultLayout.PieceType type = super.getType(vault, region);
         if (type == VaultLayout.PieceType.TUNNEL_X) {
            int xRoom1 = x - Math.floorMod(x, unit);
            int xRoom2 = xRoom1 + unit;
            if (!this.getType(vault, region.with(xRoom1, z)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }

            if (!this.getType(vault, region.with(xRoom2, z)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }
         } else if (type == VaultLayout.PieceType.TUNNEL_Z) {
            int zRoom1 = z - Math.floorMod(z, unit);
            int zRoom2 = zRoom1 + unit;
            if (!this.getType(vault, region.with(x, zRoom1)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }

            if (!this.getType(vault, region.with(x, zRoom2)).connectsToTunnel()) {
               return VaultLayout.PieceType.NONE;
            }
         }

         return type;
      }
   }

   public boolean containsPoint(int x, int z) {
      IntList vertices = this.get(VERTICES);
      int[] xp = new int[vertices.size() >>> 1];
      int[] zp = new int[vertices.size() >>> 1];

      for (int i = 0; i < vertices.size(); i++) {
         (i % 2 == 0 ? xp : zp)[i >> 1] = vertices.get(i);
      }

      boolean flag = false;
      int i = 0;

      for (int j = xp.length - 1; i < xp.length; j = i++) {
         if ((zp[i] <= z && z < zp[j] || zp[j] <= z && z < zp[i]) && x < (xp[j] - xp[i]) * (z - zp[i]) / (zp[j] - zp[i]) + xp[i]) {
            flag = !flag;
         }
      }

      if (flag) {
         return true;
      } else {
         for (int index = 0; index < xp.length; index++) {
            int n = (index + 1) % xp.length;
            int dxc = x - xp[index];
            int dzc = z - zp[index];
            int dxl = xp[n] - xp[index];
            int dzl = zp[n] - zp[index];
            int cross = dxc * dzl - dzc * dxl;
            if (cross == 0) {
               if (Math.abs(dxl) >= Math.abs(dzl)) {
                  if (dxl > 0 ? xp[index] <= x && x <= xp[n] : zp[index] <= x && x <= xp[index]) {
                     return true;
                  }
               } else if (dzl > 0 ? zp[index] <= z && z <= zp[n] : zp[n] <= z && z <= zp[index]) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
