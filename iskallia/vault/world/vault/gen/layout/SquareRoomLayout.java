package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SquareRoomLayout extends ConnectedRoomGenerator {
   public static final ResourceLocation ID = VaultMod.id("square");
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
            layout.putRoom(new Vec3i(x, 0, z));
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
