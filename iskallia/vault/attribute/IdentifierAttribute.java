package iskallia.vault.attribute;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class IdentifierAttribute extends VAttribute.Instance<ResourceLocation> {
   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         nbt.putString("BaseValue", this.getBaseValue().toString());
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (nbt.contains("BaseValue", 8)) {
         this.setBaseValue(new ResourceLocation(nbt.getString("BaseValue")));
      }
   }
}
