package iskallia.vault.world.vault.influence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;

public class VaultInfluenceRegistry {
   private static final Map<ResourceLocation, Supplier<VaultInfluence>> influences = new HashMap<>();

   public static void init() {
      influences.clear();
      register(TimeInfluence.ID, TimeInfluence::new);
      register(EffectInfluence.ID, EffectInfluence::new);
      register(MobAttributeInfluence.ID, MobAttributeInfluence::new);
      register(MobsInfluence.ID, MobsInfluence::new);
      register(DamageInfluence.ID, DamageInfluence::new);
      register(DamageTakenInfluence.ID, DamageTakenInfluence::new);
      Arrays.stream(VaultAttributeInfluence.Type.values()).forEach(type -> register(VaultAttributeInfluence.newInstance(type)));
   }

   public static Optional<VaultInfluence> getInfluence(ResourceLocation key) {
      return Optional.ofNullable(influences.get(key)).map(Supplier::get);
   }

   private static void register(Supplier<VaultInfluence> defaultSupplier) {
      influences.put(defaultSupplier.get().getKey(), defaultSupplier);
   }

   private static void register(ResourceLocation key, Supplier<VaultInfluence> defaultSupplier) {
      influences.put(key, defaultSupplier);
   }
}
