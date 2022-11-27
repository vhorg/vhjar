package iskallia.vault.attribute;

import iskallia.vault.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BoundingBoxAttribute extends VAttribute.Instance<BoundingBox> {
   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         nbt.put("BaseValue", NBTHelper.serializeBoundingBox(this.getBaseValue()));
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (nbt.contains("BaseValue", 11)) {
         this.setBaseValue(NBTHelper.deserializeBoundingBox(nbt.getIntArray("BaseValue")));
      }
   }
}
