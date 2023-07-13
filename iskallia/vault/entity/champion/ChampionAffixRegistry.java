package iskallia.vault.entity.champion;

import iskallia.vault.VaultMod;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;

public class ChampionAffixRegistry {
   private static final Map<String, Function<CompoundTag, IChampionAffix>> DESERIALIZERS = new HashMap<>();

   public static void register(String type, Function<CompoundTag, IChampionAffix> deserializer) {
      DESERIALIZERS.put(type, deserializer);
   }

   public static Optional<IChampionAffix> deserialize(CompoundTag tag) {
      String type = ChampionAffixBase.deserializeType(tag);
      if (DESERIALIZERS.containsKey(type)) {
         return Optional.of(DESERIALIZERS.get(type).apply(tag));
      } else {
         VaultMod.LOGGER.warn("Unknown champion affix type: {}", type);
         return Optional.empty();
      }
   }

   static {
      register("on_hit_apply_potion", OnHitApplyPotionAffix::deserialize);
      register("potion_aura", PotionAuraAffix::deserialize);
      register("leech_on_hit", LeechOnHitAffix::deserialize);
   }
}
