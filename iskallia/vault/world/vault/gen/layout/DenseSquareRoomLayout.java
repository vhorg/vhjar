package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

public class DenseSquareRoomLayout extends DenseVaultLayout {
   public static final ResourceLocation ID = VaultMod.id("dense_square");

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
               layout.putRoom(new DenseVaultLayout.DensePackedRoom(new Vec3i(x, 0, z)));
            }
         }
      }
   }
}
