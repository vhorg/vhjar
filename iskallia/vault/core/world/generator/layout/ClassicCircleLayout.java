package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;

public class ClassicCircleLayout extends ClassicInfiniteLayout {
   public static final SupplierKey<GridLayout> KEY = SupplierKey.of("classic_circle_vault", GridLayout.class).with(Version.v1_0, ClassicCircleLayout::new);
   public static final FieldRegistry FIELDS = ClassicInfiniteLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> RADIUS = FieldKey.of("radius", Integer.class).with(Version.v1_0, Adapter.ofInt(), DISK.all()).register(FIELDS);

   protected ClassicCircleLayout() {
   }

   public ClassicCircleLayout(int tunnelSpan, int radius) {
      super(tunnelSpan);
      this.set(RADIUS, Integer.valueOf(radius));
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
      if (this.has(RADIUS)) {
         int x = region.getX();
         int z = region.getZ();
         int unit = this.get(TUNNEL_SPAN) + 1;
         double distance = Math.sqrt(x * x + z * z);
         if (distance > this.get(RADIUS) * unit) {
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
      } else {
         return super.getType(vault, region);
      }
   }
}
