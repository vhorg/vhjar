package iskallia.vault.skill.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import iskallia.vault.skill.ability.effect.AbilityEffect;
import javax.annotation.Nullable;
import net.minecraftforge.common.MinecraftForge;

public class AbilityRegistry {
   private static final BiMap<String, AbilityEffect<?>> abilityRegistry = HashBiMap.create();

   public static <E extends AbilityEffect<?>> E register(String key, E ability) {
      abilityRegistry.put(key, ability);
      MinecraftForge.EVENT_BUS.register(ability);
      return ability;
   }

   @Nullable
   public static AbilityEffect<?> getAbility(String key) {
      return (AbilityEffect<?>)abilityRegistry.get(key);
   }

   @Nullable
   public static String getKey(AbilityEffect<?> ability) {
      return (String)abilityRegistry.inverse().get(ability);
   }
}
