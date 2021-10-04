package iskallia.vault.world.stats;

import iskallia.vault.util.data.WeightedList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class RaffleStat implements INBTSerializable<CompoundNBT> {
   private WeightedList<String> contributors = new WeightedList<>();
   private String winner = "";

   public RaffleStat() {
   }

   public RaffleStat(WeightedList<String> contributors, String winner) {
      this.contributors = contributors.copy();
      this.winner = winner;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT contributorsList = new ListNBT();
      this.contributors.forEach(entry -> {
         CompoundNBT tag = new CompoundNBT();
         tag.func_74778_a("Value", entry.value);
         tag.func_74768_a("Weight", entry.weight);
         contributorsList.add(tag);
      });
      nbt.func_218657_a("Contributors", contributorsList);
      if (this.winner != null) {
         nbt.func_74778_a("Winner", this.winner);
      }

      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.contributors.clear();
      ListNBT contributorsList = nbt.func_150295_c("Contributors", 9);
      contributorsList.stream().map(inbt -> (CompoundNBT)inbt).forEach(tag -> this.contributors.add(tag.func_74779_i("Value"), tag.func_74762_e("Weight")));
      this.winner = nbt.func_74779_i("Winner");
   }
}
