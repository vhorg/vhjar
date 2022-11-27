package iskallia.vault.skill.ability;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.skill.ability.group.AbilityGroup;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public class AbilityNode<T extends AbstractAbilityConfig, E extends AbstractAbility<T>> implements INBTSerializable<CompoundTag> {
   private String groupName;
   private int level = 0;
   private String specialization = null;
   private static final String TAG_NAME = "Name";
   public static final String TAG_LEVEL = "Level";
   public static final String TAG_SPECIALIZATION = "Specialization";

   public AbilityNode(String groupName, int level, @Nullable String specialization) {
      this.groupName = groupName;
      this.level = level;
      this.specialization = specialization;
   }

   private AbilityNode(CompoundTag nbt) {
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

   public KeyBehavior getKeyBehavior() {
      return this.getAbility().getKeyBehavior();
   }

   public void onAdded(Player player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onAdded(this.getAbilityConfig(), player);
      }
   }

   public void onRemoved(Player player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onRemoved(this.getAbilityConfig(), player);
      }
   }

   public void onFocus(Player player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onFocus(this.getAbilityConfig(), player);
      }
   }

   public void onBlur(Player player) {
      if (this.isLearned() && this.getAbility() != null) {
         this.getAbility().onBlur(this.getAbilityConfig(), player);
      }
   }

   public AbilityTickResult onTick(ServerPlayer player, boolean active) {
      return this.isLearned() && this.getAbility() != null ? this.getAbility().onTick(this.getAbilityConfig(), player, active) : AbilityTickResult.PASS;
   }

   public AbilityActionResult onAction(ServerPlayer player, boolean active) {
      return this.isLearned() && this.getAbility() != null ? this.getAbility().onAction(this.getAbilityConfig(), player, active) : AbilityActionResult.FAIL;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Name", this.getGroup().getParentName());
      nbt.putInt("Level", this.getLevel());
      if (this.getSpecialization() != null) {
         nbt.putString("Specialization", this.getSpecialization());
      }

      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.groupName = nbt.getString("Name");
      this.level = nbt.getInt("Level");
      if (nbt.contains("Specialization", 8)) {
         this.specialization = nbt.getString("Specialization");
      } else {
         this.specialization = null;
      }
   }

   public static <T extends AbstractAbilityConfig, E extends AbstractAbility<T>> AbilityNode<T, E> fromNBT(CompoundTag nbt) {
      return new AbilityNode<>(nbt);
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
}
