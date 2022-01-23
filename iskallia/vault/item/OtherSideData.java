package iskallia.vault.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class OtherSideData implements INBTSerializable<CompoundNBT> {
   private CompoundNBT delegate;
   private BlockPos linkedPos;
   private RegistryKey<World> linkedDim;

   public OtherSideData() {
   }

   public OtherSideData(ItemStack delegate) {
      if (delegate != null) {
         this.delegate = delegate.func_196082_o();
         this.deserializeNBT(this.delegate.func_74775_l("OtherSideData"));
      }
   }

   public CompoundNBT getDelegate() {
      return this.delegate;
   }

   public void updateDelegate() {
      if (this.delegate != null) {
         this.delegate.func_218657_a("OtherSideData", this.serializeNBT());
      }
   }

   public BlockPos getLinkedPos() {
      return this.linkedPos;
   }

   public RegistryKey<World> getLinkedDim() {
      return this.linkedDim;
   }

   public OtherSideData setLinkedPos(BlockPos linkedPos) {
      this.linkedPos = linkedPos;
      this.updateDelegate();
      return this;
   }

   public OtherSideData setLinkedDim(RegistryKey<World> linkedDim) {
      if (this.linkedDim != (this.linkedDim = linkedDim)) {
         this.updateDelegate();
      }

      return this;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74783_a("LinkedPos", new int[]{this.linkedPos.func_177958_n(), this.linkedPos.func_177956_o(), this.linkedPos.func_177952_p()});
      nbt.func_74778_a("LinkedDim", this.linkedDim.func_240901_a_().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      int[] arr = nbt.func_74759_k("LinkedPos");
      this.linkedPos = new BlockPos(arr[0], arr[1], arr[2]);
      this.linkedDim = RegistryKey.func_240903_a_(Registry.field_239699_ae_, new ResourceLocation(nbt.func_74779_i("LinkedDim")));
   }
}
