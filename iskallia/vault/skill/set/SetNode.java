package iskallia.vault.skill.set;

import iskallia.vault.init.ModConfigs;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class SetNode<T extends PlayerSet> implements INBTSerializable<CompoundNBT> {
   private SetGroup<T> group;
   private int level;

   public SetNode(SetGroup<T> group, int level) {
      this.group = group;
      this.level = level;
   }

   public SetGroup<T> getGroup() {
      return this.group;
   }

   public int getLevel() {
      return this.level;
   }

   public T getSet() {
      return !this.isActive() ? null : this.getGroup().getSet(this.getLevel());
   }

   public String getName() {
      return this.getGroup().getName(this.getLevel());
   }

   public boolean isActive() {
      return this.level != 0;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Name", this.getGroup().getParentName());
      nbt.func_74768_a("Level", this.getLevel());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      String groupName = nbt.func_74779_i("Name");
      this.group = (SetGroup<T>)ModConfigs.SETS.getByName(groupName);
      this.level = nbt.func_74762_e("Level");
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         SetNode<?> that = (SetNode<?>)other;
         return this.level == that.level && this.group.getParentName().equals(that.group.getParentName());
      } else {
         return false;
      }
   }

   public static <T extends PlayerSet> SetNode<T> fromNBT(CompoundNBT nbt, Class<T> clazz) {
      SetGroup<T> group = (SetGroup<T>)ModConfigs.SETS.getByName(nbt.func_74779_i("Name"));
      int level = nbt.func_74762_e("Level");
      return new SetNode<>(group, level);
   }
}
