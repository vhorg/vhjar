package iskallia.vault.skill.ability;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.type.PlayerAbility;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class AbilityNode<T extends PlayerAbility> implements INBTSerializable<CompoundNBT> {
   private AbilityGroup<T> group;
   private int level;

   public AbilityNode(AbilityGroup<T> group, int level) {
      this.group = group;
      this.level = level;
   }

   public AbilityGroup<T> getGroup() {
      return this.group;
   }

   public int getLevel() {
      return this.level;
   }

   public T getAbility() {
      return !this.isLearned() ? null : this.group.getAbility(this.level);
   }

   public String getName() {
      return this.group.getName(this.level);
   }

   public boolean isLearned() {
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
      this.group = (AbilityGroup<T>)ModConfigs.ABILITIES.getByName(groupName);
      this.level = nbt.func_74762_e("Level");
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         AbilityNode<?> that = (AbilityNode<?>)other;
         return this.level == that.level && this.group.getParentName().equals(that.group.getParentName());
      } else {
         return false;
      }
   }

   public static <T extends PlayerAbility> AbilityNode<T> fromNBT(CompoundNBT nbt, Class<T> clazz) {
      AbilityGroup<T> group = (AbilityGroup<T>)ModConfigs.ABILITIES.getByName(nbt.func_74779_i("Name"));
      int level = nbt.func_74762_e("Level");
      return new AbilityNode<>(group, level);
   }
}
