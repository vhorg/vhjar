package iskallia.vault.skill.talent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;

public class TalentNode<T extends LegacyTalent> implements INBTSerializable<CompoundTag> {
   private TalentGroup<T> group;
   private int level;

   public TalentNode(TalentGroup<T> group, int level) {
      this.group = group;
      this.level = Mth.clamp(level, 0, group.getMaxLevel());
   }

   public TalentNode(CompoundTag nbt) {
      this.deserializeNBT(nbt);
   }

   public TalentGroup<T> getGroup() {
      return this.group;
   }

   public int getLevel() {
      return this.level;
   }

   public T getTalent() {
      return !this.isLearned() ? null : this.getGroup().getTalent(this.getLevel());
   }

   public String getName() {
      return this.getGroup().getName(this.getLevel());
   }

   public boolean isLearned() {
      return this.getLevel() > 0;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Name", this.getGroup().getParentName());
      nbt.putInt("Level", this.getLevel());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      String groupName = nbt.getString("Name");
      this.level = nbt.getInt("Level");
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         TalentNode<?> that = (TalentNode<?>)other;
         return this.level == that.level && this.group.getParentName().equals(that.group.getParentName());
      } else {
         return false;
      }
   }
}
