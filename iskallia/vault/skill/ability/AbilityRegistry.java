package iskallia.vault.skill.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraftforge.common.MinecraftForge;

public class AbilityRegistry {
   private static final BiMap<String, AbstractAbility<?>> abilityRegistry = HashBiMap.create();

   public static <E extends AbstractAbility<?>> E register(String key, E ability) {
      abilityRegistry.put(key, ability);
      MinecraftForge.EVENT_BUS.register(ability);
      return ability;
   }

   public static Stream<String> getAbilityKeys() {
      return abilityRegistry.keySet().stream();
   }

   @Nullable
   public static AbstractAbility<?> getAbility(String key) {
      return (AbstractAbility<?>)abilityRegistry.get(key);
   }

   @Nullable
   public static String getKey(AbstractAbility<?> ability) {
      return (String)abilityRegistry.inverse().get(ability);
   }
}
