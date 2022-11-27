package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class TriangleRoomLayout extends ConnectedRoomGenerator {
   public static final ResourceLocation ID = VaultMod.id("triangle");
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
      Direction facing = Direction.from2DDataValue(rand.nextInt(4));
      Vec3i directionVec = facing.getNormal();
      Vec3i offset = directionVec.relative(facing, -halfSize);
      Direction edgeFacing = facing.getClockWise();
      Vec3i corner = offset.relative(edgeFacing, -halfSize);

      for (int hItr = 0; hItr <= size; hItr++) {
         float allowedDst = (float)(size - hItr) / size;

         for (int wItr = 0; wItr <= size; wItr++) {
            float dst = (float)Math.abs(wItr - halfSize) / halfSize;
            if (!(dst > allowedDst)) {
               Vec3i roomPos = corner.relative(edgeFacing, wItr).relative(facing, hItr);
               layout.putRoom(roomPos);
            }
         }
      }
   }

   @Override
   protected void deserialize(CompoundTag tag) {
      super.deserialize(tag);
      if (tag.contains("size", 3)) {
         this.size = tag.getInt("size");
      }
   }

   @Override
   protected CompoundTag serialize() {
      CompoundTag tag = super.serialize();
      tag.putInt("size", this.size);
      return tag;
   }
}
