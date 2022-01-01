package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class TriangleRoomLayout extends ConnectedRoomGenerator {
   public static final ResourceLocation ID = Vault.id("triangle");
   private int size;

   public TriangleRoomLayout() {
      this(11);
   }

   public TriangleRoomLayout(int size) {
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
         this.connectRooms(layout, this.size + 2);
         return layout;
      }
   }

   private void calculateRooms(VaultRoomLayoutGenerator.Layout layout, int size) {
      int halfSize = size / 2;
      Direction facing = Direction.func_176731_b(rand.nextInt(4));
      Vector3i directionVec = facing.func_176730_m();
      Vector3i offset = directionVec.func_177967_a(facing, -halfSize);
      Direction edgeFacing = facing.func_176746_e();
      Vector3i corner = offset.func_177967_a(edgeFacing, -halfSize);

      for (int hItr = 0; hItr <= size; hItr++) {
         float allowedDst = (float)(size - hItr) / size;

         for (int wItr = 0; wItr <= size; wItr++) {
            float dst = (float)Math.abs(wItr - halfSize) / halfSize;
            if (!(dst > allowedDst)) {
               Vector3i roomPos = corner.func_177967_a(edgeFacing, wItr).func_177967_a(facing, hItr);
               layout.putRoom(roomPos);
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
