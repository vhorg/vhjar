package iskallia.vault.world.stats;

import iskallia.vault.util.data.WeightedList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

public class RaffleStat implements INBTSerializable<CompoundTag> {
   private WeightedList<String> contributors = new WeightedList<>();
   private String winner = "";

   public RaffleStat() {
   }

   public RaffleStat(WeightedList<String> contributors, String winner) {
      this.contributors = contributors.copy();
      this.winner = winner;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag contributorsList = new ListTag();
      this.contributors.forEach(entry -> {
         CompoundTag tag = new CompoundTag();
         tag.putString("Value", entry.value);
         tag.putInt("Weight", entry.weight);
         contributorsList.add(tag);
      });
      nbt.put("Contributors", contributorsList);
      if (this.winner != null) {
         nbt.putString("Winner", this.winner);
      }

      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.contributors.clear();
      ListTag contributorsList = nbt.getList("Contributors", 9);
      contributorsList.stream().map(inbt -> (CompoundTag)inbt).forEach(tag -> this.contributors.add(tag.getString("Value"), tag.getInt("Weight")));
      this.winner = nbt.getString("Winner");
   }
}
