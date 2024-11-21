package iskallia.vault.config.card;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.card.Card;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.init.ModConfigs;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class BoosterPackConfig extends Config {
   @Expose
   private Map<String, BoosterPackConfig.BoosterPackEntry> values;

   @Override
   public String getName() {
      return "card%sbooster_packs".formatted(File.separator);
   }

   public Map<String, BoosterPackConfig.BoosterPackEntry> getValues() {
      return this.values;
   }

   public Optional<Component> getName(String id) {
      return Optional.ofNullable(this.values.get(id)).map(pack -> pack.name);
   }

   public Optional<BoosterPackConfig.BoosterPackModel> getModel(String id) {
      return Optional.ofNullable(this.values.get(id)).map(pack -> pack.model);
   }

   public Set<BoosterPackConfig.BoosterPackModel> getModels() {
      return this.values.values().stream().map(pack -> pack.model).collect(Collectors.toSet());
   }

   public List<Card> getOutcomes(String id, RandomSource random) {
      List<Card> cards = new ArrayList<>();
      BoosterPackConfig.BoosterPackEntry pack = this.values.get(id);
      if (pack == null) {
         return cards;
      } else {
         int roll = pack.roll.getRandom(random).orElse(0);

         for (int i = 0; i < roll; i++) {
            int tier = pack.tier.getRandom(random).orElse(0);
            CardEntry.Color color = pack.color == null ? null : pack.color.getRandom(random).orElse(null);
            List<CardEntry> entries = new ArrayList<>();
            List<BoosterPackConfig.CardConfig> card = pack.card.getRandom(random).orElse(null);
            if (card != null) {
               for (BoosterPackConfig.CardConfig config : card) {
                  if (!(random.nextDouble() >= config.probability)) {
                     ModConfigs.CARD_MODIFIERS.getRandom(config.modifier, random).ifPresent(e -> {
                        if (color != null) {
                           e.colors.add(color);
                        }

                        if (config.colors != null) {
                           config.colors.getRandom(random).ifPresent(colors -> e.colors.addAll(colors));
                        }

                        if (config.groups != null) {
                           e.groups.addAll(config.groups);
                        }

                        CardEntry entry = e.toEntry();
                        if (config.scaler != null) {
                           ModConfigs.CARD_SCALERS.getRandom(config.scaler, random).ifPresent(entry::setScaler);
                        }

                        if (config.condition != null) {
                           ModConfigs.CARD_CONDITIONS.getRandom(config.condition, random).ifPresent(entry::setCondition);
                        }

                        entries.add(entry);
                     });
                  }
               }

               cards.add(new Card(tier, entries));
            }
         }

         return cards;
      }
   }

   @Override
   protected void reset() {
      this.values = new LinkedHashMap<>();
      this.values
         .put(
            "the_vault:default",
            new BoosterPackConfig.BoosterPackEntry(
               new TextComponent("Default Booster Pack"),
               new BoosterPackConfig.BoosterPackModel("the_vault:booster_pack/default#inventory", "the_vault:booster_pack/default_ripped#inventory"),
               new WeightedList<Integer>().add(2, 1).add(3, 1.0).add(4, 1.0).add(5, 1.0),
               new WeightedList<Integer>().add(1, 99).add(2, 0.01),
               new WeightedList<CardEntry.Color>()
                  .add(CardEntry.Color.GREEN, 1.0)
                  .add(CardEntry.Color.BLUE, 1.0)
                  .add(CardEntry.Color.YELLOW, 1.0)
                  .add(CardEntry.Color.RED, 1.0),
               new WeightedList<List<BoosterPackConfig.CardConfig>>()
                  .add(
                     Arrays.asList(
                        new BoosterPackConfig.CardConfig("@default", null, null, null, null, 1.0),
                        new BoosterPackConfig.CardConfig("@default", null, Set.of("Foil"), "@default", "@default", 0.1)
                     ),
                     1.0
                  )
            )
         );
   }

   public static class BoosterPackEntry {
      @Expose
      private Component name;
      @Expose
      private BoosterPackConfig.BoosterPackModel model;
      @Expose
      private WeightedList<Integer> roll;
      @Expose
      private WeightedList<Integer> tier;
      @Expose
      private WeightedList<CardEntry.Color> color;
      @Expose
      private WeightedList<List<BoosterPackConfig.CardConfig>> card;

      public BoosterPackEntry(
         Component name,
         BoosterPackConfig.BoosterPackModel model,
         WeightedList<Integer> roll,
         WeightedList<Integer> tier,
         WeightedList<CardEntry.Color> color,
         WeightedList<List<BoosterPackConfig.CardConfig>> card
      ) {
         this.name = name;
         this.model = model;
         this.roll = roll;
         this.tier = tier;
         this.color = color;
         this.card = card;
      }

      public WeightedList<List<BoosterPackConfig.CardConfig>> getCard() {
         return this.card;
      }
   }

   public static class BoosterPackModel {
      @Expose
      private String unopened;
      @Expose
      private String opened;

      public BoosterPackModel(String unopened, String opened) {
         this.unopened = unopened;
         this.opened = opened;
      }

      public String getUnopened() {
         return this.unopened;
      }

      public String getOpened() {
         return this.opened;
      }
   }

   public static class CardConfig {
      @Expose
      private String modifier;
      @Expose
      private WeightedList<List<CardEntry.Color>> colors;
      @Expose
      private Set<String> groups;
      @Expose
      private String scaler;
      @Expose
      private String condition;
      @Expose
      private double probability;

      public CardConfig(String modifier, WeightedList<List<CardEntry.Color>> colors, Set<String> groups, String scaler, String condition, double probability) {
         this.modifier = modifier;
         this.colors = colors;
         this.groups = groups;
         this.scaler = scaler;
         this.condition = condition;
         this.probability = probability;
      }

      public String getModifier() {
         return this.modifier;
      }
   }
}
