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

public class ClassicInfiniteLayout extends ClassicVaultLayout {
   public static final SupplierKey<GridLayout> KEY = SupplierKey.of("classic_infinite_vault", GridLayout.class).with(Version.v1_0, ClassicInfiniteLayout::new);
   public static final FieldRegistry FIELDS = ClassicVaultLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> TUNNEL_SPAN = FieldKey.of("tunnel_span", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all())
      .register(FIELDS);

   protected ClassicInfiniteLayout() {
   }

   public ClassicInfiniteLayout(int tunnelSpan) {
      this.set(TUNNEL_SPAN, Integer.valueOf(tunnelSpan));
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
      if (x == 0 && z == 0) {
         return VaultLayout.PieceType.START;
      } else if (x % unit == 0 && z % unit == 0) {
         return VaultLayout.PieceType.ROOM;
      } else if (x % unit != 0 && z % unit != 0) {
         return VaultLayout.PieceType.NONE;
      } else {
         Direction facing = vault.get(Vault.WORLD).get(WorldManager.FACING);
         int distance = Math.abs(x) + Math.abs(z);
         if (x % unit == 0) {
            return distance < unit && z * facing.getStepZ() <= 0 ? VaultLayout.PieceType.NONE : VaultLayout.PieceType.TUNNEL_Z;
         } else if (z % unit != 0) {
            throw new IllegalStateException("You have stumbled upon a number that doesn't exist");
         } else {
            return distance < unit && x * facing.getStepX() <= 0 ? VaultLayout.PieceType.NONE : VaultLayout.PieceType.TUNNEL_X;
         }
      }
   }
}
