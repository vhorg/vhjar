package iskallia.vault.world.vault.gen.layout;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

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

   public static class DensePackedRoom extends VaultRoomLayoutGenerator.Room {
      public DensePackedRoom(Vec3i roomPosition) {
         super(roomPosition);
      }

      @Override
      public boolean canGenerateTreasureRooms() {
         return false;
      }

      @Override
      public BlockPos getRoomOffset() {
         return new BlockPos(this.getRoomPosition().getX() * 47, 0, this.getRoomPosition().getZ() * 47);
      }
   }
}
