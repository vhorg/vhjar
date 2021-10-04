package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class DenseDiamondRoomLayout extends DenseVaultLayout {
   public static final ResourceLocation ID = Vault.id("dense_diamond");

   public DenseDiamondRoomLayout() {
      this(11);
   }

   public DenseDiamondRoomLayout(int size) {
      super(ID, size);
   }

   @Override
   protected void generateLayoutRooms(VaultRoomLayoutGenerator.Layout layout, int size) {
      int xOffset = -size / 2;

      for (int x = 0; x <= size / 2; x++) {
         this.addRooms(layout, xOffset + x, 1 + x * 2);
      }

      for (int x = size / 2 + 1; x < size; x++) {
         int index = x - (size / 2 + 1);
         this.addRooms(layout, xOffset + x, size - (index + 1) * 2);
      }
   }

   private void addRooms(VaultRoomLayoutGenerator.Layout layout, int x, int roomsZ) {
      for (int z = -roomsZ / 2; z <= roomsZ / 2; z++) {
         if (x != -1 || z != 0) {
            layout.putRoom(new DenseVaultLayout.DensePackedRoom(new Vector3i(x, 0, z)));
         }
      }
   }
}
