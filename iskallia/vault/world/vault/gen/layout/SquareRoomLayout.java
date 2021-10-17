package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class SquareRoomLayout extends ConnectedRoomGenerator {
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
         throw new IllegalArgumentException("Cannot generate vault square shape with even size!");
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
