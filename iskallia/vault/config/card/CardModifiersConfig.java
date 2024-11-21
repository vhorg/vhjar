package iskallia.vault.config.card;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.GearCardModifier;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.init.ModGearAttributes;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.network.chat.TextComponent;

public class CardModifiersConfig extends Config {
   @Expose
   private Map<String, CardEntry.Config> values;
   @Expose
   private Map<String, WeightedList<String>> pools;

   @Override
   public String getName() {
      return "card%smodifiers".formatted(File.separator);
   }

   public Collection<CardEntry.Config> getEntries() {
      return this.values.values();
   }

   public Optional<CardEntry.Config> getRandom(String id, RandomSource random) {
      if (id.startsWith("@")) {
         WeightedList<String> pool = this.pools.get(id.substring(1));
         return pool == null ? Optional.empty() : pool.getRandom(random).flatMap(s -> this.getRandom(s, random));
      } else {
         return Optional.ofNullable(this.values.get(id)).map(CardEntry.Config::copy);
      }
   }

   public Map<String, CardEntry.Config> getAll(String id) {
      if (id.startsWith("@")) {
         WeightedList<String> pool = this.pools.get(id.substring(1));
         if (pool == null) {
            Map<String, CardEntry.Config> cfg = new HashMap<>();
            cfg.put(id, null);
            return cfg;
         } else {
            Map<String, CardEntry.Config> cfg = new HashMap<>();
            pool.keySet().forEach(poolEntryId -> cfg.putAll(this.getAll(poolEntryId)));
            return cfg;
         }
      } else {
         Map<String, CardEntry.Config> cfg = new HashMap<>();
         cfg.put(id, this.values.getOrDefault(id, null));
         return cfg;
      }
   }

   @Override
   protected void reset() {
      this.values = new LinkedHashMap<>();
      this.pools = new LinkedHashMap<>();
      this.values
         .put(
            "attack_damage",
            new CardEntry.Config(
               new TextComponent("Attack Damage Card"),
               Set.of(),
               Set.of("Stat", "Offense"),
               "the_vault:card/icon/attack_damage#inventory",
               new GearCardModifier<>(new GearCardModifier.Config<>(ModGearAttributes.ATTACK_DAMAGE, new LinkedHashMap<Integer, String>() {
                  {
                     this.put(Integer.valueOf(1), "{ \"min\": 2.0, \"max\": 6.0, \"step\": 0.5 }");
                     this.put(Integer.valueOf(2), "{ \"min\": 7.0, \"max\": 12.0, \"step\": 0.5 }");
                     this.put(Integer.valueOf(3), "{ \"min\": 13.0, \"max\": 18.0, \"step\": 0.5 }");
                     this.put(Integer.valueOf(4), "{ \"min\": 19.0, \"max\": 27.0, \"step\": 0.5 }");
                     this.put(Integer.valueOf(5), "{ \"min\": 28.0, \"max\": 35.0, \"step\": 0.5 }");
                  }
               })),
               null,
               null
            )
         );
      this.values
         .put(
            "health",
            new CardEntry.Config(
               new TextComponent("Health Card"),
               Set.of(),
               Set.of("Stat", "Defense"),
               "the_vault:card/icon/health#inventory",
               new GearCardModifier<>(new GearCardModifier.Config<>(ModGearAttributes.HEALTH, new LinkedHashMap<Integer, String>() {
                  {
                     this.put(Integer.valueOf(1), "{ \"min\": 1.0, \"max\": 2.0, \"step\": 1.0 }");
                     this.put(Integer.valueOf(2), "{ \"min\": 3.0, \"max\": 4.0, \"step\": 1.0 }");
                     this.put(Integer.valueOf(3), "{ \"min\": 5.0, \"max\": 6.0, \"step\": 1.0 }");
                     this.put(Integer.valueOf(4), "{ \"min\": 7.0, \"max\": 8.0, \"step\": 1.0 }");
                     this.put(Integer.valueOf(5), "{ \"min\": 9.0, \"max\": 10.0, \"step\": 1.0 }");
                  }
               })),
               null,
               null
            )
         );
      this.pools.put("default", new WeightedList<String>().add("attack_damage", 1.0).add("health", 1.0));
   }
}
