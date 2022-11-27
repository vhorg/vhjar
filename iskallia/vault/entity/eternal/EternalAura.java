package iskallia.vault.entity.eternal;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class EternalAura implements INBTSerializable<CompoundTag> {
   private String auraName;

   public EternalAura(String auraName) {
      this.auraName = auraName;
   }

   public EternalAura(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public String getAuraName() {
      return this.auraName;
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putString("auraName", this.auraName);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.auraName = tag.getString("auraName");
   }
}
