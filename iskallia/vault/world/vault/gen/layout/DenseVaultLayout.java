package iskallia.vault.world.vault.gen.layout;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

public abstract class DenseVaultLayout extends VaultRoomLayoutGenerator {
   private int size;

   protected DenseVaultLayout(ResourceLocation key, int size) {
      super(key);
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
         this.generateLayoutRooms(layout, this.size);
         return layout;
      }
   }

   protected abstract void generateLayoutRooms(VaultRoomLayoutGenerator.Layout var1, int var2);

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

   public static class DensePackedRoom extends VaultRoomLayoutGenerator.Room {
      public DensePackedRoom(Vector3i roomPosition) {
         super(roomPosition);
      }

      @Override
      public boolean canGenerateTreasureRooms() {
         return false;
      }

      @Override
      public BlockPos getRoomOffset() {
         return new BlockPos(this.getRoomPosition().func_177958_n() * 47, 0, this.getRoomPosition().func_177952_p() * 47);
      }
   }
}
