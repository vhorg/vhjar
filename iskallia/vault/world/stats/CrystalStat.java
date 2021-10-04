package iskallia.vault.world.stats;

import iskallia.vault.altar.RequiredItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.nbt.VListNBT;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class CrystalStat implements INBTSerializable<CompoundNBT> {
   private CrystalData.Type type;
   public VListNBT<RequiredItem, CompoundNBT> recipe = new VListNBT<>(RequiredItem::serializeNBT, RequiredItem::deserializeNBT);
   public long time;

   public CrystalStat() {
   }

   public CrystalStat(List<RequiredItem> recipe, CrystalData.Type type) {
      this.type = type;
      recipe.forEach(item -> this.recipe.add(item.copy()));
      this.time = System.currentTimeMillis();
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Type", this.type.name());
      nbt.func_218657_a("Recipe", this.recipe.serializeNBT());
      nbt.func_74772_a("Time", this.time);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.type = nbt.func_150297_b("Type", 8) ? Enum.valueOf(CrystalData.Type.class, nbt.func_74779_i("Type")) : CrystalData.Type.CLASSIC;
      this.recipe.deserializeNBT(nbt.func_150295_c("Recipe", 10));
      this.time = nbt.func_74763_f("Time");
   }
}
