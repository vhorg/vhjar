package iskallia.vault.entity.eternal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class EternalAura implements INBTSerializable<CompoundNBT> {
   private String auraName;

   public EternalAura(String auraName) {
      this.auraName = auraName;
   }

   public EternalAura(CompoundNBT tag) {
      this.deserializeNBT(tag);
   }

   public String getAuraName() {
      return this.auraName;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT tag = new CompoundNBT();
      tag.func_74778_a("auraName", this.auraName);
      return tag;
   }

   public void deserializeNBT(CompoundNBT tag) {
      this.auraName = tag.func_74779_i("auraName");
   }
}
