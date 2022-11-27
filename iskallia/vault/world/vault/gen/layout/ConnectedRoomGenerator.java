package iskallia.vault.world.vault.gen.layout;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

public abstract class ConnectedRoomGenerator extends VaultRoomLayoutGenerator {
   protected ConnectedRoomGenerator(ResourceLocation id) {
      super(id);
   }

   public void connectRooms(VaultRoomLayoutGenerator.Layout layout, int size) {
      int halfSize = size / 2;

      for (int xx = -halfSize; xx <= halfSize; xx++) {
         for (int zz = -halfSize; zz <= halfSize; zz++) {
            VaultRoomLayoutGenerator.Room middle = layout.getRoom(new Vec3i(xx, 0, zz));
            if (middle != null) {
               if (xx != -1 || zz != 0) {
                  VaultRoomLayoutGenerator.Room right = layout.getRoom(new Vec3i(xx + 1, 0, zz));
                  if (right != null) {
                     layout.addTunnel(middle, right);
                  }
               }

               VaultRoomLayoutGenerator.Room up = layout.getRoom(new Vec3i(xx, 0, zz + 1));
               if (up != null) {
                  layout.addTunnel(middle, up);
               }
            }
         }
      }
   }
}
