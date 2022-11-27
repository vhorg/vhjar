package iskallia.vault.core.world.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class PartialEntity {
   private Vec3 pos;
   private BlockPos blockPos;
   private PartialNBT nbt;

   protected PartialEntity(Vec3 pos, BlockPos blockPos, PartialNBT nbt) {
      this.pos = pos;
      this.blockPos = blockPos;
      this.nbt = nbt;
   }

   public static PartialEntity of(Vec3 pos, BlockPos blockPos, CompoundTag nbt) {
      return new PartialEntity(pos, blockPos, PartialNBT.of(nbt));
   }

   public Vec3 getPos() {
      return this.pos;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public PartialNBT getNBT() {
      return this.nbt;
   }

   public void setPos(Vec3 pos) {
      this.pos = pos;
   }

   public void setBlockPos(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public void setNbt(PartialNBT nbt) {
      this.nbt = nbt;
   }

   public PartialEntity fillMissing(PartialEntity entity) {
      if (this.pos == null) {
         this.pos = entity.getPos();
      }

      if (this.blockPos == null) {
         this.blockPos = entity.getBlockPos();
      }

      this.nbt.fillMissing(entity.getNBT());
      return this;
   }

   public PartialEntity copy() {
      return new PartialEntity(this.pos, this.blockPos, PartialNBT.of(this.nbt));
   }
}
