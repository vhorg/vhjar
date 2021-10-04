package iskallia.vault.item.paxel.enhancement;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.Color;

public class DurabilityEnhancement extends PaxelEnhancement {
   protected int extraDurability;

   @Override
   public Color getColor() {
      return Color.func_240743_a_(-5888257);
   }

   public DurabilityEnhancement(int extraDurability) {
      this.extraDurability = extraDurability;
   }

   public int getExtraDurability() {
      return this.extraDurability;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74768_a("ExtraDurability", this.extraDurability);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.extraDurability = nbt.func_74762_e("ExtraDurability");
   }
}
