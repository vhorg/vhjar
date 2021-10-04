package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class DenseSquareRoomLayout extends DenseVaultLayout {
   public static final ResourceLocation ID = Vault.id("dense_square");

   public DenseSquareRoomLayout() {
      this(11);
   }

   public DenseSquareRoomLayout(int size) {
      super(ID, size);
   }

   @Override
   protected void generateLayoutRooms(VaultRoomLayoutGenerator.Layout layout, int size) {
      int halfSize = size / 2;

      for (int x = -halfSize; x <= halfSize; x++) {
         for (int z = -halfSize; z <= halfSize; z++) {
            if (x != -1 || z != 0) {
               layout.putRoom(new DenseVaultLayout.DensePackedRoom(new Vector3i(x, 0, z)));
            }
         }
      }
   }
}
