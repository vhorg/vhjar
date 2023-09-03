package iskallia.vault.item.bottle;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;

public class BottleEffectManager {
   private static final Map<String, BiFunction<String, CompoundTag, BottleEffect>> REGISTRY = Map.of(
      "mana_flat",
      ManaFlatBottleEffect::deserialize,
      "mana_percent",
      ManaPercentBottleEffect::deserialize,
      "cooldown_reduction",
      CooldownReductionBottleEffect::deserialize,
      "potion",
      PotionBottleEffect::deserialize,
      "absorption",
      AbsorptionBottleEffect::deserialize,
      "cleanse",
      CleanseBottleEffect::deserialize,
      "cast_ability",
      CastAbilityBottleEffect::deserialize
   );

   private BottleEffectManager() {
   }

   public static Optional<BottleEffect> deserialize(CompoundTag tag) {
      String effectId = tag.getString("id");
      String type = tag.getString("type");
      return deserialize(type, effectId, tag);
   }

   public static Optional<BottleEffect> deserialize(String type, String effectId, CompoundTag tag) {
      return !REGISTRY.containsKey(type) ? Optional.empty() : Optional.of(REGISTRY.get(type).apply(effectId, tag));
   }
}
