package iskallia.vault.attribute;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryKeyAttribute<T> extends VAttribute.Instance<ResourceKey<T>> {
   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         CompoundTag valueNBT = new CompoundTag();
         valueNBT.putString("Parent", this.getBaseValue().getRegistryName().toString());
         valueNBT.putString("Identifier", this.getBaseValue().location().toString());
         nbt.put("BaseValue", valueNBT);
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (nbt.contains("BaseValue", 10)) {
         CompoundTag valueNBT = nbt.getCompound("BaseValue");
         this.setBaseValue(
            ResourceKey.create(
               ResourceKey.createRegistryKey(new ResourceLocation(valueNBT.getString("Parent"))), new ResourceLocation(valueNBT.getString("Identifier"))
            )
         );
      }
   }
}
