package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class DiamondRoomLayout extends ConnectedRoomGenerator {
   public static final ResourceLocation ID = Vault.id("diamond");
   private int size;

   public DiamondRoomLayout() {
      this(11);
   }

   public DiamondRoomLayout(int size) {
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
         layout.putRoom(new VaultRoomLayoutGenerator.Room(new Vector3i(x, 0, z)));
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
