package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.CleanseConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

public class CleanseEffectConfig extends CleanseConfig {
   @Expose
   private final List<String> possibleEffects = new ArrayList<>();
   @Expose
   private final int effectAmplifier;

   public CleanseEffectConfig(int learningCost, AbilityConfig.Behavior behavior, int cooldown, int effectAmplifier, List<String> possibleEffects) {
      super(learningCost, behavior, cooldown);
      this.effectAmplifier = effectAmplifier;
      this.possibleEffects.addAll(possibleEffects);
   }

   public static CleanseEffectConfig ofEffectNames(
      int learningCost, AbilityConfig.Behavior behavior, int cooldown, int effectAmplifier, List<ResourceLocation> possibleEffects
   ) {
      return new CleanseEffectConfig(
         learningCost, behavior, cooldown, effectAmplifier, possibleEffects.stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
      );
   }

   public static CleanseEffectConfig ofEffects(
      int learningCost, AbilityConfig.Behavior behavior, int cooldown, int effectAmplifier, List<Effect> possibleEffects
   ) {
      return new CleanseEffectConfig(
         learningCost,
         behavior,
         cooldown,
         effectAmplifier,
         possibleEffects.stream().map(effect -> effect.getRegistryName().toString()).collect(Collectors.toList())
      );
   }

   public List<String> getPossibleEffects() {
      return this.possibleEffects;
   }

   public int getEffectAmplifier() {
      return this.effectAmplifier;
   }
}
