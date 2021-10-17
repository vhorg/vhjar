package iskallia.vault.skill.ability;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.effect.AbilityEffect;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class AbilityNode<T extends AbilityConfig, E extends AbilityEffect<T>> implements INBTSerializable<CompoundNBT> {
   private String groupName;
   private int level = 0;
   private String specialization = null;

   public AbilityNode(String groupName, int level, @Nullable String specialization) {
      this.groupName = groupName;
      this.level = level;
      this.specialization = specialization;
   }

   private AbilityNode(CompoundNBT nbt) {
      this.deserializeNBT(nbt);
   }

   public AbilityGroup<T, E> getGroup() {
      return (AbilityGroup<T, E>)ModConfigs.ABILITIES.getAbilityGroupByName(this.groupName);
   }

   public int getLevel() {
      return this.level;
   }

   @Nullable
   public String getSpecialization() {
      return this.specialization;
   }

   public void setSpecialization(@Nullable String specialization) {
      this.specialization = specialization;
   }

   public String getName() {
      return !this.isLearned() ? this.getGroup().getName(1) : this.getGroup().getName(this.getLevel());
   }

   public String getSpecializationName() {
      String specialization = this.getSpecialization();
      return specialization == null ? this.getGroup().getParentName() : this.getGroup().getSpecializationName(specialization);
   }

   public boolean isLearned() {
      return this.getLevel() > 0;
   }

   @Nullable
   public T getAbilityConfig() {
      return !this.isLearned() ? this.getGroup().getAbilityConfig(null, -1) : this.getGroup().getAbilityConfig(this.getSpecialization(), this.getLevel() - 1);
   }

   @Nullable
   public E getAbility() {
      return this.getGroup().getAbility(this.getSpecialization());
   }

   public void onAdded(PlayerEntity player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onAdded(this.getAbilityConfig(), player);
      }
   }

   public void onRemoved(PlayerEntity player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onRemoved(this.getAbilityConfig(), player);
      }
   }

   public void onFocus(PlayerEntity player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onFocus(this.getAbilityConfig(), player);
      }
   }

   public void onBlur(PlayerEntity player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onBlur(this.getAbilityConfig(), player);
      }
   }

   public void onTick(PlayerEntity player, boolean active) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onTick(this.getAbilityConfig(), player, active);
      }
   }

   public boolean onAction(ServerPlayerEntity player, boolean active) {
      return this.isLearned() && this.getAbility() != null ? this.getAbility().onAction(this.getAbilityConfig(), player, active) : false;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Name", this.getGroup().getParentName());
      nbt.func_74768_a("Level", this.getLevel());
      if (this.getSpecialization() != null) {
         nbt.func_74778_a("Specialization", this.getSpecialization());
      }

      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.groupName = nbt.func_74779_i("Name");
      this.level = nbt.func_74762_e("Level");
      if (nbt.func_150297_b("Specialization", 8)) {
         this.specialization = nbt.func_74779_i("Specialization");
         if (this.specialization.equals("Rampage_Nocrit") || this.specialization.equals("Ghost Walk_Duration")) {
            this.specialization = null;
         }
      } else {
         this.specialization = null;
      }
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         AbilityNode<?, ?> that = (AbilityNode<?, ?>)other;
         return this.level == that.level && Objects.equals(this.getGroup(), that.getGroup());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getGroup(), this.level);
   }

   public static <T extends AbilityConfig, E extends AbilityEffect<T>> AbilityNode<T, E> fromNBT(CompoundNBT nbt) {
      return new AbilityNode<>(nbt);
   }
}
