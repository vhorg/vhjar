package iskallia.vault.world.vault.modifier.registry;

import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class VaultModifierRegistry {
   private static final Map<ResourceLocation, VaultModifier<?>> VAULT_MODIFIER_MAP = new HashMap<>();

   public static void register(ResourceLocation id, VaultModifier<?> vaultModifier) {
      if (VAULT_MODIFIER_MAP.containsKey(id)) {
         throw new IllegalStateException("Attempted to register duplicate vault modifier id: " + id);
      } else {
         VAULT_MODIFIER_MAP.put(id, vaultModifier);
      }
   }

   public static void clear() {
      VAULT_MODIFIER_MAP.clear();
   }

   public static <M extends VaultModifier<?>> M get(ResourceLocation id) {
      return (M)VAULT_MODIFIER_MAP.get(id);
   }

   public static <M extends VaultModifier<?>> Optional<M> getOpt(ResourceLocation id) {
      return Optional.ofNullable(VAULT_MODIFIER_MAP.get(id)).map((Function<? super VaultModifier<?>, ? extends M>)(vaultModifier -> vaultModifier));
   }

   public static <M extends VaultModifier<?>> Optional<M> getOpt(ResourceLocation id, Class<M> modifierClass) {
      return getOpt(id);
   }

   public static <M extends VaultModifier<?>> M getOrDefault(ResourceLocation id, @Nullable M defaultModifier) {
      VaultModifier<?> vaultModifier = VAULT_MODIFIER_MAP.get(id);
      return (M)(vaultModifier == null ? defaultModifier : vaultModifier);
   }

   public static Stream<VaultModifier<?>> getAll() {
      return VAULT_MODIFIER_MAP.values().stream();
   }

   public static <M extends VaultModifier<?>> Stream<M> getAll(VaultModifierType<M, ?> vaultModifierType) {
      return getAll(vaultModifierType.modifierClass());
   }

   public static <M extends VaultModifier<?>> Stream<M> getAll(Class<M> vaultModifierClass) {
      return getAll().filter(vaultModifierClass::isInstance).map(vaultModifierClass::cast);
   }

   private VaultModifierRegistry() {
   }
}
