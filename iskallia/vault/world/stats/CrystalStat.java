package iskallia.vault.world.stats;

import iskallia.vault.altar.RequiredItems;
import iskallia.vault.nbt.VListNBT;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class CrystalStat implements INBTSerializable<CompoundTag> {
   public VListNBT<RequiredItems, CompoundTag> recipe = new VListNBT<>(RequiredItems::serializeNBT, RequiredItems::new);
   public long time;

   public CrystalStat() {
   }

   public CrystalStat(List<RequiredItems> recipe) {
      recipe.forEach(item -> this.recipe.add(item.copy()));
      this.time = System.currentTimeMillis();
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.put("Recipe", this.recipe.serializeNBT());
      nbt.putLong("Time", this.time);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.recipe.deserializeNBT(nbt.getList("Recipe", 10));
      this.time = nbt.getLong("Time");
   }
}
