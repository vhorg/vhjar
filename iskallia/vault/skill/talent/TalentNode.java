package iskallia.vault.skill.talent;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.type.PlayerTalent;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;

public class TalentNode<T extends PlayerTalent> implements INBTSerializable<CompoundNBT> {
   private TalentGroup<T> group;
   private int level;

   public TalentNode(TalentGroup<T> group, int level) {
      this.group = group;
      this.level = MathHelper.func_76125_a(level, 0, group.getMaxLevel());
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

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Name", this.getGroup().getParentName());
      nbt.func_74768_a("Level", this.getLevel());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      String groupName = nbt.func_74779_i("Name");
      this.group = (TalentGroup<T>)ModConfigs.TALENTS.getByName(groupName);
      this.level = nbt.func_74762_e("Level");
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

   @Nullable
   public static <T extends PlayerTalent> TalentNode<T> fromNBT(CompoundNBT nbt, Class<T> clazz) {
      TalentGroup<T> group = (TalentGroup<T>)ModConfigs.TALENTS.getTalent(nbt.func_74779_i("Name")).orElse(null);
      if (group == null) {
         return null;
      } else {
         int level = nbt.func_74762_e("Level");
         return new TalentNode<>(group, level);
      }
   }
}
