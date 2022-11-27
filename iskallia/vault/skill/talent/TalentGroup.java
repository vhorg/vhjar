package iskallia.vault.skill.talent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.skill.talent.type.AttributeTalent;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.skill.talent.type.VanillaAttributeTalent;
import iskallia.vault.util.RomanNumber;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TalentGroup<T extends Talent> {
   @Expose
   private final String name;
   @Expose
   private final T[] levels;
   private BiMap<String, T> registry;

   public TalentGroup(String name, T... levels) {
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
      return level == 0 ? this.name + " " + RomanNumber.toRoman(0) : (String)this.getRegistry().inverse().get(this.getTalent(level));
   }

   public T getTalent(int level) {
      if (level < 0) {
         return this.levels[0];
      } else {
         return level >= this.getMaxLevel() ? this.levels[this.getMaxLevel() - 1] : this.levels[level - 1];
      }
   }

   public int learningCost() {
      return this.levels[0].getLearningCost();
   }

   public int cost(int level) {
      return level > this.getMaxLevel() ? -1 : this.levels[level - 1].getLearningCost();
   }

   public BiMap<String, T> getRegistry() {
      if (this.registry == null) {
         this.registry = HashBiMap.create(this.getMaxLevel());
         if (this.getMaxLevel() == 1) {
            this.registry.put(this.getParentName(), this.levels[0]);
         } else if (this.getMaxLevel() > 1) {
            for (int i = 0; i < this.getMaxLevel(); i++) {
               this.registry.put(this.getParentName() + " " + RomanNumber.toRoman(i + 1), this.getTalent(i + 1));
            }
         }
      }

      return this.registry;
   }

   public static TalentGroup<AttributeTalent> ofGearAttribute(String name, VaultGearAttribute<?> attribute, int maxLevel, IntToDoubleFunction valueFn) {
      AttributeTalent[] talents = IntStream.range(0, maxLevel)
         .mapToObj(i -> new AttributeTalent(i + 1, attribute, valueFn.applyAsDouble(i)))
         .toArray(AttributeTalent[]::new);
      return new TalentGroup<>(name, talents);
   }

   public static TalentGroup<EffectTalent> ofEffect(String name, MobEffect effect, int maxLevel) {
      EffectTalent[] talents = IntStream.range(0, maxLevel).mapToObj(i -> new EffectTalent(i + 1, effect, i + 1)).toArray(EffectTalent[]::new);
      return new TalentGroup<>(name, talents);
   }

   public static TalentGroup<VanillaAttributeTalent> ofAttribute(
      String name, Attribute attribute, Operation operation, int maxLevel, IntToDoubleFunction amount
   ) {
      VanillaAttributeTalent[] talents = IntStream.range(0, maxLevel)
         .mapToObj(
            i -> new VanillaAttributeTalent(
               i + 1,
               attribute,
               new VanillaAttributeTalent.Modifier(attribute.getDescriptionId() + " " + RomanNumber.toRoman(i + 1), amount.applyAsDouble(i + 1), operation)
            )
         )
         .toArray(VanillaAttributeTalent[]::new);
      return new TalentGroup<>(name, talents);
   }

   public static <T extends Talent> TalentGroup<T> of(String name, int maxLevel, IntFunction<T> supplier) {
      Talent[] talents = IntStream.range(0, maxLevel).mapToObj(supplier).toArray(Talent[]::new);
      return new TalentGroup<>(name, (T[])talents);
   }
}
