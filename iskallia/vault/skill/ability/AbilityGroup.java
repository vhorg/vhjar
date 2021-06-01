package iskallia.vault.skill.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.type.EffectAbility;
import iskallia.vault.skill.ability.type.ExecuteAbility;
import iskallia.vault.skill.ability.type.GhostWalkAbility;
import iskallia.vault.skill.ability.type.PlayerAbility;
import iskallia.vault.skill.ability.type.RampageAbility;
import iskallia.vault.skill.ability.type.TankAbility;
import iskallia.vault.util.RomanNumber;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import net.minecraft.potion.Effect;

public class AbilityGroup<T extends PlayerAbility> {
   @Expose
   private final String name;
   @Expose
   private final T[] levels;
   private BiMap<String, T> registry;

   public AbilityGroup(String name, T... levels) {
      this.name = name;
      this.levels = levels;
   }

   public int getMaxLevel() {
      return this.levels.length;
   }

   public String getParentName() {
      return this.name;
   }

   public String getName(int level) {
      return level == 0 ? this.name + " " + RomanNumber.toRoman(0) : (String)this.getRegistry().inverse().get(this.getAbility(level));
   }

   public T getAbility(int level) {
      if (level < 0) {
         return this.levels[0];
      } else {
         return level >= this.getMaxLevel() ? this.levels[this.getMaxLevel() - 1] : this.levels[level - 1];
      }
   }

   public int learningCost() {
      return this.levels[0].getCost();
   }

   public int cost(int level) {
      return level > this.getMaxLevel() ? -1 : this.levels[level - 1].getCost();
   }

   public BiMap<String, T> getRegistry() {
      if (this.registry == null) {
         this.registry = HashBiMap.create(this.getMaxLevel());
         if (this.getMaxLevel() == 1) {
            this.registry.put(this.getParentName(), this.levels[0]);
         } else if (this.getMaxLevel() > 1) {
            for (int i = 0; i < this.getMaxLevel(); i++) {
               this.registry.put(this.getParentName() + " " + RomanNumber.toRoman(i + 1), this.getAbility(i + 1));
            }
         }
      }

      return this.registry;
   }

   public static AbilityGroup<EffectAbility> ofEffect(String name, Effect effect, EffectAbility.Type type, int maxLevel, IntUnaryOperator cost) {
      EffectAbility[] abilities = IntStream.range(0, maxLevel)
         .mapToObj(i -> new EffectAbility(cost.applyAsInt(i + 1), effect, i, type))
         .toArray(EffectAbility[]::new);
      return new AbilityGroup<>(name, abilities);
   }

   public static AbilityGroup<GhostWalkAbility> ofGhostWalkEffect(String name, Effect effect, EffectAbility.Type type, int maxLevel, IntUnaryOperator cost) {
      GhostWalkAbility[] abilities = IntStream.range(0, maxLevel)
         .mapToObj(i -> new GhostWalkAbility(cost.applyAsInt(i + 1), effect, i, (i + 1) * 100, type, PlayerAbility.Behavior.RELEASE_TO_PERFORM))
         .toArray(GhostWalkAbility[]::new);
      return new AbilityGroup<>(name, abilities);
   }

   public static AbilityGroup<RampageAbility> ofRampage(String name, Effect effect, EffectAbility.Type type, int maxLevel, IntUnaryOperator cost) {
      RampageAbility[] abilities = IntStream.range(0, maxLevel)
         .mapToObj(i -> new RampageAbility(cost.applyAsInt(i + 1), effect, i, i, (i + 1) * 100, (i + 1) * 100, type, PlayerAbility.Behavior.RELEASE_TO_PERFORM))
         .toArray(RampageAbility[]::new);
      return new AbilityGroup<>(name, abilities);
   }

   public static AbilityGroup<TankAbility> ofTank(String name, Effect effect, EffectAbility.Type type, int maxLevel, IntUnaryOperator cost) {
      TankAbility[] abilities = IntStream.range(0, maxLevel)
         .mapToObj(i -> new TankAbility(cost.applyAsInt(i + 1), effect, i, (i + 1) * 100, type, PlayerAbility.Behavior.RELEASE_TO_PERFORM))
         .toArray(TankAbility[]::new);
      return new AbilityGroup<>(name, abilities);
   }

   public static AbilityGroup<ExecuteAbility> ofExecute(String name, Effect effect, EffectAbility.Type type, int maxLevel, IntUnaryOperator cost) {
      ExecuteAbility[] abilities = IntStream.range(0, maxLevel)
         .mapToObj(i -> new ExecuteAbility(cost.applyAsInt(i + 1), effect, i, type, PlayerAbility.Behavior.RELEASE_TO_PERFORM, (i + 1.0F) * 0.1F))
         .toArray(ExecuteAbility[]::new);
      return new AbilityGroup<>(name, abilities);
   }
}
