package iskallia.vault.skill.ability.group;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.AbilityRegistry;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.util.RomanNumber;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;

public abstract class AbilityGroup<T extends AbstractAbilityConfig, E extends AbstractAbility<T>> {
   @Expose
   protected final String name;
   @Expose
   protected final List<T> levelConfiguration = new ArrayList();
   private final BiMap<Integer, String> nameCache = HashBiMap.create();

   protected AbilityGroup(String name) {
      this.name = name;
   }

   public int getMaxLevel() {
      return this.levelConfiguration.size();
   }

   protected void addLevel(T config) {
      this.levelConfiguration.add(config);
   }

   public String getParentName() {
      return this.name;
   }

   public String getName(int level) {
      return (String)this.getNameCache().get(level);
   }

   public T getAbilityConfig(@Nullable String specialization, int level) {
      if (level < 0) {
         return this.getDefaultConfig(0);
      } else {
         level = Math.min(level, this.getMaxLevel() - 1);
         if (specialization != null) {
            T config = this.getSubConfig(specialization, level);
            if (config != null) {
               return config;
            }
         }

         return this.getDefaultConfig(level);
      }
   }

   public boolean hasSpecialization(String specialization) {
      return this.getSubConfig(specialization, 0) != null;
   }

   protected abstract T getSubConfig(String var1, int var2);

   public abstract String getSpecializationName(String var1);

   private T getDefaultConfig(int level) {
      return this.levelConfiguration.get(Mth.clamp(level, 0, this.getMaxLevel() - 1));
   }

   @Nullable
   public E getAbility(@Nullable String specialization) {
      return (E)AbilityRegistry.getAbility(specialization == null ? this.getParentName() : specialization);
   }

   public int learningCost() {
      return this.getDefaultConfig(0).getLearningCost();
   }

   public int levelUpCost(@Nullable String specialization, int toLevel) {
      return toLevel > this.getMaxLevel() ? -1 : this.getAbilityConfig(specialization, toLevel - 1).getLearningCost();
   }

   private BiMap<Integer, String> getNameCache() {
      if (this.nameCache.isEmpty()) {
         for (int i = 1; i <= this.getMaxLevel(); i++) {
            this.nameCache.put(i, this.getParentName() + " " + RomanNumber.toRoman(i));
         }
      }

      return this.nameCache;
   }

   public abstract boolean isConfigurationValid();

   protected boolean areListsEqualSize(List<?> list, List<?>... specLists) {
      for (List<?> specList : specLists) {
         if (list.size() != specList.size()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AbilityGroup<?, ?> that = (AbilityGroup<?, ?>)o;
         return Objects.equals(this.name, that.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name);
   }
}
