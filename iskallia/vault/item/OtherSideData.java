package iskallia.vault.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public class OtherSideData implements INBTSerializable<CompoundTag> {
   private CompoundTag delegate;
   private BlockPos linkedPos;
   private ResourceKey<Level> linkedDim;

   public OtherSideData() {
   }

   public OtherSideData(ItemStack delegate) {
      if (delegate != null) {
         this.delegate = delegate.getOrCreateTag();
         this.deserializeNBT(this.delegate.getCompound("OtherSideData"));
      }
   }

   public CompoundTag getDelegate() {
      return this.delegate;
   }

   public void updateDelegate() {
      if (this.delegate != null) {
         this.delegate.put("OtherSideData", this.serializeNBT());
      }
   }

   public BlockPos getLinkedPos() {
      return this.linkedPos;
   }

   public ResourceKey<Level> getLinkedDim() {
      return this.linkedDim;
   }

   public OtherSideData setLinkedPos(BlockPos linkedPos) {
      this.linkedPos = linkedPos;
      this.updateDelegate();
      return this;
   }

   public OtherSideData setLinkedDim(ResourceKey<Level> linkedDim) {
      if (this.linkedDim != (this.linkedDim = linkedDim)) {
         this.updateDelegate();
      }

      return this;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putIntArray("LinkedPos", new int[]{this.linkedPos.getX(), this.linkedPos.getY(), this.linkedPos.getZ()});
      nbt.putString("LinkedDim", this.linkedDim.location().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      int[] arr = nbt.getIntArray("LinkedPos");
      this.linkedPos = new BlockPos(arr[0], arr[1], arr[2]);
      this.linkedDim = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("LinkedDim")));
   }
}
