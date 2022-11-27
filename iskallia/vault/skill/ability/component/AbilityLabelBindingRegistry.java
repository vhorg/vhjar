package iskallia.vault.skill.ability.component;

import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class AbilityLabelBindingRegistry {
   private static final Map<Class<?>, Map<String, IAbilityLabelBinding<?>>> CLASS_BINDING_MAP = new IdentityHashMap<>();

   public static void clear() {
      CLASS_BINDING_MAP.clear();
   }

   public static <C extends AbstractAbilityConfig> boolean containsKey(Class<C> configClass) {
      return CLASS_BINDING_MAP.containsKey(configClass);
   }

   public static <C extends AbstractAbilityConfig> void register(Class<C> configClass, String key, IAbilityLabelBinding<C> binding) {
      CLASS_BINDING_MAP.computeIfAbsent(configClass, aClass -> new HashMap<>()).put(key, binding);
   }

   public static <C extends AbstractAbilityConfig> String getBindingValue(C config, String key) {
      IAbilityLabelBinding<C> binding = (IAbilityLabelBinding<C>)CLASS_BINDING_MAP.getOrDefault(config.getClass(), Collections.emptyMap()).get(key);
      return binding == null ? "NO BINDING" : binding.get(config);
   }

   private AbilityLabelBindingRegistry() {
   }
}
