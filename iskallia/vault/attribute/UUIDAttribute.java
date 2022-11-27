package iskallia.vault.attribute;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

public class UUIDAttribute extends VAttribute.Instance<UUID> {
   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         nbt.putString("BaseValue", this.getBaseValue().toString());
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (nbt.contains("BaseValue", 8)) {
         this.setBaseValue(UUID.fromString(nbt.getString("BaseValue")));
      }
   }
}
