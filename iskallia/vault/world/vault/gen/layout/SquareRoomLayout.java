package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class SquareRoomLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = Vault.id("square");
   private int size;

   public SquareRoomLayout() {
      this(11);
   }

   public SquareRoomLayout(int size) {
      super(ID);
      this.size = size;
   }

   @Override
   public void setSize(int size) {
      this.size = size;
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      VaultRoomLayoutGenerator.Layout layout = new VaultRoomLayoutGenerator.Layout();
      if (this.size % 2 == 0) {
         throw new IllegalArgumentException("Cannot generate vault diamond shape with even size!");
      } else {
         this.calculateRooms(layout, this.size);
         this.connectRooms(layout, this.size);
         return layout;
      }
   }

   private void calculateRooms(VaultRoomLayoutGenerator.Layout layout, int size) {
      int halfSize = size / 2;

      for (int x = -halfSize; x <= halfSize; x++) {
         for (int z = -halfSize; z <= halfSize; z++) {
            layout.putRoom(new Vector3i(x, 0, z));
         }
      }
   }

   private void connectRooms(VaultRoomLayoutGenerator.Layout layout, int size) {
      int halfSize = size / 2;

      for (int xx = -halfSize; xx <= halfSize; xx++) {
         for (int zz = -halfSize; zz <= halfSize; zz++) {
            VaultRoomLayoutGenerator.Room middle = layout.getRoom(new Vector3i(xx, 0, zz));
            if (middle != null) {
               if (xx != -1 || zz != 0) {
                  VaultRoomLayoutGenerator.Room right = layout.getRoom(new Vector3i(xx + 1, 0, zz));
                  if (right != null) {
                     layout.addTunnel(new VaultRoomLayoutGenerator.Tunnel(middle, right));
                  }
               }

               VaultRoomLayoutGenerator.Room up = layout.getRoom(new Vector3i(xx, 0, zz + 1));
               if (up != null) {
                  layout.addTunnel(new VaultRoomLayoutGenerator.Tunnel(middle, up));
               }
            }
         }
      }
   }

   @Override
   protected void deserialize(CompoundNBT tag) {
      super.deserialize(tag);
      if (tag.func_150297_b("size", 3)) {
         this.size = tag.func_74762_e("size");
      }
   }

   @Override
   protected CompoundNBT serialize() {
      CompoundNBT tag = super.serialize();
      tag.func_74768_a("size", this.size);
      return tag;
   }
}
