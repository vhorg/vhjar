package iskallia.vault.core.card;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class CardCondition extends CardProperty<CardCondition.Config> {
   public static final SerializableAdapter<CardCondition, CompoundTag, JsonObject> ADAPTER = Adapters.of(CardCondition::new, true);
   private final Map<Integer, List<CardCondition.Filter>> filters = new LinkedHashMap<>();

   public CardCondition() {
      super(new CardCondition.Config());
   }

   public CardCondition(CardCondition.Config config) {
      super(config);
   }

   @Override
   public boolean onPopulate() {
      if (!super.onPopulate()) {
         return false;
      } else {
         JavaRandom random = JavaRandom.ofNanoTime();
         this.getConfig()
            .tiers
            .forEach(
               (tier, config) -> config.getRandom(random)
                  .ifPresent(filters -> this.filters.put(tier, filters.stream().map(filter -> filter.generate(random)).toList()))
            );
         return true;
      }
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int tier) {
      for (CardCondition.Filter filter : CardEntry.getForTier(this.filters, tier).orElse(new ArrayList<>())) {
         List<Component> parts = new ArrayList<>();
         if (filter.colorFilter != null) {
            filter.colorFilter
               .stream()
               .map(CardEntry.Color::getColoredText)
               .reduce((c1, c2) -> new TextComponent("").append(c1).append(new TextComponent(" or ").withStyle(ChatFormatting.GRAY)).append(c2))
               .ifPresent(parts::add);
         }

         if (filter.neighborFilter != null) {
            filter.neighborFilter
               .stream()
               .map(type -> type.getText().withStyle(ChatFormatting.WHITE))
               .reduce((c1, c2) -> new TextComponent("").append(c1).append(new TextComponent(" or ").withStyle(ChatFormatting.GRAY)).append(c2))
               .ifPresent(parts::add);
         }

         if (filter.groupFilter != null) {
            filter.groupFilter
               .stream()
               .map(s -> new TextComponent(s).withStyle(ChatFormatting.WHITE))
               .reduce((c1, c2) -> (TextComponent)new TextComponent("").append(c1).append(new TextComponent(" or ").withStyle(ChatFormatting.GRAY)).append(c2))
               .ifPresent(parts::add);
         }

         if (filter.tierFilter != null) {
            Integer min = filter.tierFilter.stream().min(Integer::compareTo).orElse(null);
            Integer max = filter.tierFilter.stream().max(Integer::compareTo).orElse(null);
            if (min != null) {
               parts.add(new TextComponent(min.equals(max) ? "Tier " + min : "Tier " + min + "-" + max));
            }
         }

         MutableComponent text = new TextComponent(" > ").withStyle(ChatFormatting.GRAY);
         if (filter.minCount != null && filter.maxCount != null) {
            if (filter.minCount.equals(filter.maxCount)) {
               text.append(new TextComponent(filter.minCount == 1 ? "If there is exactly " : "If there are exactly ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent(filter.minCount + " "));
            } else {
               text.append(new TextComponent("If there are between ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent(filter.minCount + " "))
                  .append(new TextComponent("and "))
                  .append(new TextComponent(filter.maxCount + " "));
            }
         } else if (filter.minCount != null) {
            text.append(new TextComponent(filter.minCount == 1 ? "If there is at least " : "If there are at least ").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent(filter.minCount + " "));
         } else if (filter.maxCount != null) {
            text.append(new TextComponent(filter.maxCount == 1 ? "If there is at most " : "If there are at most ").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent(filter.maxCount + " "));
         }

         for (int i = 0; i < parts.size(); i++) {
            text.append(parts.get(i));
            if (i != parts.size() - 1) {
               text.append(new TextComponent(", ").withStyle(ChatFormatting.GRAY));
            }
         }

         if (filter.minCount != null && filter.minCount.equals(filter.maxCount) && filter.minCount == 1) {
            text.append(new TextComponent(parts.isEmpty() ? "Card" : " Card").withStyle(ChatFormatting.GRAY));
         } else {
            text.append(new TextComponent(parts.isEmpty() ? "Cards" : " Cards").withStyle(ChatFormatting.GRAY));
         }

         tooltip.add(text);
      }
   }

   public boolean test(int tier, CardPos origin, CardDeck deck) {
      for (CardCondition.Filter filter : CardEntry.getForTier(this.filters, tier).orElse(new ArrayList<>())) {
         Set<CardPos> filteredCards = new HashSet<>();
         if (filter.neighborFilter != null) {
            for (CardNeighborType cardNeighborType : filter.neighborFilter) {
               filteredCards.addAll(cardNeighborType.get(origin, deck));
            }
         } else {
            filteredCards.addAll(deck.getSlots());
         }

         if (filter.tierFilter != null) {
            filteredCards.removeIf(pos -> {
               Card card = deck.getCard(pos).orElse(null);
               return card == null || !filter.tierFilter.contains(card.getTier());
            });
         }

         Set<CardEntry> filteredEntries = new HashSet<>();
         filteredCards.forEach(pos -> deck.getCard(pos).ifPresent(card -> filteredEntries.addAll(card.getEntries())));
         if (filter.colorFilter != null) {
            filteredEntries.removeIf(entry -> filter.colorFilter.stream().noneMatch(color -> entry.getColors().contains(color)));
         }

         if (filter.groupFilter != null) {
            filteredEntries.removeIf(entry -> filter.groupFilter.stream().noneMatch(group -> entry.getGroups().contains(group)));
         }

         if (filter.minCount != null && filteredEntries.size() < filter.minCount || filter.maxCount != null && filteredEntries.size() > filter.maxCount) {
            return false;
         }
      }

      return true;
   }

   public CardCondition copy() {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      this.writeBits(buffer);
      buffer.setPosition(0);
      CardCondition copy = new CardCondition();
      copy.readBits(buffer);
      return copy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      if (this.isPopulated()) {
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.filters.size()), buffer);
         this.filters.forEach((tier, filters) -> {
            Adapters.INT_SEGMENTED_3.writeBits(tier, buffer);
            Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(filters.size()), buffer);
            filters.forEach(filter -> filter.writeBits(buffer));
         });
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      if (this.isPopulated()) {
         this.filters.clear();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < size; i++) {
            int tier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
            int filterSize = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
            List<CardCondition.Filter> filters = new ArrayList<>();

            for (int j = 0; j < filterSize; j++) {
               CardCondition.Filter filter = new CardCondition.Filter();
               filter.readBits(buffer);
               filters.add(filter);
            }

            this.filters.put(tier, filters);
         }
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.isPopulated()) {
            return (CompoundTag)nbt;
         } else {
            ListTag filtersTag = new ListTag();
            this.filters.forEach((tier, filters) -> {
               CompoundTag entry = new CompoundTag();
               Adapters.INT.writeNbt(tier).ifPresent(tag -> entry.put("tier", tag));
               ListTag valueTag = new ListTag();

               for (CardCondition.Filter filter : filters) {
                  filter.writeNbt().ifPresent(valueTag::add);
               }

               entry.put("value", valueTag);
               filtersTag.add(entry);
            });
            nbt.put("filters", filtersTag);
            return (CompoundTag)nbt;
         }
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (this.isPopulated()) {
         this.filters.clear();
         ListTag filtersTag = nbt.getList("filters", 10);

         for (int i = 0; i < filtersTag.size(); i++) {
            CompoundTag entry = filtersTag.getCompound(i);
            int tier = Adapters.INT.readNbt(entry.get("tier")).orElseThrow();
            ListTag valueTag = entry.getList("value", 10);
            List<CardCondition.Filter> filters = new ArrayList<>();

            for (int j = 0; j < valueTag.size(); j++) {
               CardCondition.Filter filter = new CardCondition.Filter();
               filter.readNbt(valueTag.getCompound(j));
               filters.add(filter);
            }

            this.filters.put(tier, filters);
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.isPopulated()) {
            return (JsonObject)json;
         } else {
            JsonArray filtersTag = new JsonArray();
            this.filters.forEach((tier, filters) -> {
               JsonObject entry = new JsonObject();
               Adapters.INT.writeJson(tier).ifPresent(tag -> entry.add("tier", tag));
               JsonArray valueTag = new JsonArray();

               for (CardCondition.Filter filter : filters) {
                  filter.writeJson().ifPresent(valueTag::add);
               }

               entry.add("value", valueTag);
               filtersTag.add(entry);
            });
            json.add("filters", filtersTag);
            return (JsonObject)json;
         }
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (this.isPopulated()) {
         this.filters.clear();
         JsonArray filtersTag = json.getAsJsonArray("filters");

         for (int i = 0; i < filtersTag.size(); i++) {
            JsonObject entry = filtersTag.get(i).getAsJsonObject();
            int tier = Adapters.INT.readJson(entry.get("tier")).orElseThrow();
            JsonArray valueTag = entry.getAsJsonArray("value");
            List<CardCondition.Filter> filters = new ArrayList<>();

            for (int j = 0; j < valueTag.size(); j++) {
               CardCondition.Filter filter = new CardCondition.Filter();
               filter.readJson(valueTag.get(j).getAsJsonObject());
               filters.add(filter);
            }

            this.filters.put(tier, filters);
         }
      }
   }

   public static class Config extends CardProperty.Config {
      private final Map<Integer, WeightedList<List<CardCondition.Filter.Config>>> tiers;

      public Config() {
         this.tiers = new LinkedHashMap<>();
      }

      public Config(Map<Integer, WeightedList<List<CardCondition.Filter.Config>>> tiers) {
         this.tiers = tiers;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.tiers.size()), buffer);
         this.tiers.forEach((tier, pool) -> {
            Adapters.INT_SEGMENTED_3.writeBits(tier, buffer);
            Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(pool.size()), buffer);
            pool.forEach((filters, weight) -> {
               Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(filters.size()), buffer);

               for (CardCondition.Filter.Config filter : filters) {
                  filter.writeBits(buffer);
               }

               Adapters.DOUBLE.writeBits(weight, buffer);
            });
         });
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.tiers.clear();
         int tiersSize = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < tiersSize; i++) {
            int tier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
            WeightedList<List<CardCondition.Filter.Config>> pool = new WeightedList<>();
            int poolSize = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for (int j = 0; j < poolSize; j++) {
               int filtersSize = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
               List<CardCondition.Filter.Config> filters = new ArrayList<>();

               for (int k = 0; k < filtersSize; k++) {
                  CardCondition.Filter.Config config = new CardCondition.Filter.Config();
                  config.readBits(buffer);
                  filters.add(config);
               }

               pool.put(filters, Adapters.DOUBLE.readBits(buffer).orElseThrow());
            }

            this.tiers.put(tier, pool);
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            ListTag tiersTag = new ListTag();
            this.tiers.forEach((tier, pool) -> {
               CompoundTag tierEntry = new CompoundTag();
               Adapters.INT.writeNbt(tier).ifPresent(tag -> tierEntry.put("tier", tag));
               ListTag poolTag = new ListTag();
               pool.forEach((filters, weight) -> {
                  CompoundTag poolEntry = new CompoundTag();
                  ListTag filtersTag = new ListTag();

                  for (CardCondition.Filter.Config filter : filters) {
                     CardCondition.Filter.Config.ADAPTER.writeNbt(filter).ifPresent(filtersTag::add);
                  }

                  poolEntry.put("filters", filtersTag);
                  Adapters.DOUBLE.writeNbt(weight).ifPresent(tag -> poolEntry.put("weight", tag));
                  poolTag.add(poolEntry);
               });
               tierEntry.put("pool", poolTag);
               tiersTag.add(tierEntry);
            });
            nbt.put("tiers", tiersTag);
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.tiers.clear();
         ListTag tiersTag = nbt.getList("tiers", 10);

         for (int i = 0; i < tiersTag.size(); i++) {
            CompoundTag tierEntry = tiersTag.getCompound(i);
            int tier = Adapters.INT.readNbt(tierEntry.get("tier")).orElseThrow();
            ListTag poolTag = tierEntry.getList("pool", 10);
            WeightedList<List<CardCondition.Filter.Config>> pool = new WeightedList<>();

            for (int j = 0; j < poolTag.size(); j++) {
               CompoundTag poolEntry = poolTag.getCompound(j);
               ListTag filtersTag = poolEntry.getList("filters", 10);
               List<CardCondition.Filter.Config> filters = new ArrayList<>();
               double weight = Adapters.DOUBLE.readNbt(poolEntry.get("weight")).orElseThrow();

               for (int k = 0; k < filtersTag.size(); k++) {
                  CardCondition.Filter.Config config = new CardCondition.Filter.Config();
                  config.readNbt(filtersTag.getCompound(k));
                  filters.add(config);
               }

               pool.put(filters, weight);
            }

            this.tiers.put(tier, pool);
         }
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            JsonArray tiersTag = new JsonArray();
            this.tiers.forEach((tier, pool) -> {
               JsonObject tierEntry = new JsonObject();
               Adapters.INT.writeJson(tier).ifPresent(tag -> tierEntry.add("tier", tag));
               JsonArray poolTag = new JsonArray();
               pool.forEach((filters, weight) -> {
                  JsonObject poolEntry = new JsonObject();
                  JsonArray filtersTag = new JsonArray();

                  for (CardCondition.Filter.Config filter : filters) {
                     CardCondition.Filter.Config.ADAPTER.writeJson(filter).ifPresent(filtersTag::add);
                  }

                  poolEntry.add("filters", filtersTag);
                  Adapters.DOUBLE.writeJson(weight).ifPresent(tag -> poolEntry.add("weight", tag));
                  poolTag.add(poolEntry);
               });
               tierEntry.add("pool", poolTag);
               tiersTag.add(tierEntry);
            });
            json.add("tiers", tiersTag);
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.tiers.clear();
         JsonArray tiersTag = json.get("tiers").getAsJsonArray();

         for (int i = 0; i < tiersTag.size(); i++) {
            JsonObject tierEntry = tiersTag.get(i).getAsJsonObject();
            int tier = Adapters.INT.readJson(tierEntry.get("tier")).orElseThrow();
            JsonArray poolTag = tierEntry.get("pool").getAsJsonArray();
            WeightedList<List<CardCondition.Filter.Config>> pool = new WeightedList<>();

            for (int j = 0; j < poolTag.size(); j++) {
               JsonObject poolEntry = poolTag.get(j).getAsJsonObject();
               JsonArray filtersTag = poolEntry.get("filters").getAsJsonArray();
               List<CardCondition.Filter.Config> filters = new ArrayList<>();
               double weight = Adapters.DOUBLE.readJson(poolEntry.get("weight")).orElseThrow();

               for (int k = 0; k < filtersTag.size(); k++) {
                  CardCondition.Filter.Config config = new CardCondition.Filter.Config();
                  config.readJson(filtersTag.get(k).getAsJsonObject());
                  filters.add(config);
               }

               pool.put(filters, weight);
            }

            this.tiers.put(tier, pool);
         }
      }
   }

   public static class Filter implements ISerializable<CompoundTag, JsonObject> {
      private Set<CardNeighborType> neighborFilter;
      private Set<Integer> tierFilter;
      private Set<CardEntry.Color> colorFilter;
      private Set<String> groupFilter;
      private Integer minCount;
      private Integer maxCount;

      public Filter() {
      }

      public Filter(
         Set<CardNeighborType> neighborFilter,
         Set<Integer> tierFilter,
         Set<CardEntry.Color> colorFilter,
         Set<String> groupFilter,
         Integer minCount,
         Integer maxCount
      ) {
         this.neighborFilter = neighborFilter;
         this.tierFilter = tierFilter;
         this.colorFilter = colorFilter;
         this.groupFilter = groupFilter;
         this.minCount = minCount;
         this.maxCount = maxCount;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Stream.of("neighborFilter", "tierFilter", "colorFilter", "groupFilter").forEach(key -> {
            Set<Object> set = this.getSet(key);
            Adapters.BOOLEAN.writeBits(set == null, buffer);
            if (set != null) {
               Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(set.size()), buffer);
               set.forEach(value -> this.getElementAdapter(key).writeBits(value, buffer));
            }
         });
         Adapters.INT.asNullable().writeBits(this.minCount, buffer);
         Adapters.INT.asNullable().writeBits(this.maxCount, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         Stream.of("neighborFilter", "tierFilter", "colorFilter", "groupFilter").forEach(key -> {
            Set<Object> set = new HashSet<>();
            if (!Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
               this.deserialize(key, null);
            } else {
               int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

               for (int j = 0; j < size; j++) {
                  set.add(this.getElementAdapter(key).readBits(buffer).orElseThrow());
               }

               this.deserialize(key, set);
            }
         });
         this.minCount = Adapters.INT.asNullable().readBits(buffer).orElse(null);
         this.maxCount = Adapters.INT.asNullable().readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Stream.of("neighborFilter", "tierFilter", "colorFilter", "groupFilter").forEach(key -> {
               Set<Object> set = this.getSet(key);
               if (set != null) {
                  ListTag setTag = new ListTag();
                  set.forEach(value -> this.getElementAdapter(key).writeNbt(value).ifPresent(setTag::add));
                  nbt.put(this.getKey(set), setTag);
               }
            });
            Adapters.INT.writeNbt(this.minCount).ifPresent(tag -> nbt.put("minCount", tag));
            Adapters.INT.writeNbt(this.maxCount).ifPresent(tag -> nbt.put("maxCount", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         Stream.of("neighborFilter", "tierFilter", "colorFilter", "groupFilter").forEach(key -> {
            if (nbt.get(key) instanceof ListTag setTag) {
               Set<Object> set = new HashSet<>();

               for (int i = 0; i < setTag.size(); i++) {
                  this.getElementAdapter(key).readNbt(setTag.get(i)).ifPresent(object -> set.add(object));
               }

               this.deserialize(key, set);
            } else {
               this.deserialize(key, null);
            }
         });
         this.minCount = Adapters.INT.readNbt(nbt.get("minCount")).orElse(null);
         this.maxCount = Adapters.INT.readNbt(nbt.get("maxCount")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            Stream.of("neighborFilter", "tierFilter", "colorFilter", "groupFilter").forEach(key -> {
               Set<Object> set = this.getSet(key);
               if (set != null) {
                  JsonArray setTag = new JsonArray();
                  set.forEach(value -> this.getElementAdapter(key).writeJson(value).ifPresent(setTag::add));
                  json.add(this.getKey(set), setTag);
               }
            });
            Adapters.INT.writeJson(this.minCount).ifPresent(tag -> json.add("minCount", tag));
            Adapters.INT.writeJson(this.maxCount).ifPresent(tag -> json.add("maxCount", tag));
            return (JsonObject)json;
         });
      }

      public void readJson(JsonObject json) {
         Stream.of("neighborFilter", "tierFilter", "colorFilter", "groupFilter").forEach(key -> {
            if (json.get(key) instanceof JsonArray setTag) {
               Set<Object> set = new HashSet<>();

               for (int i = 0; i < setTag.size(); i++) {
                  this.getElementAdapter(key).readJson(setTag.get(i)).ifPresent(object -> set.add(object));
               }

               this.deserialize(key, set);
            } else {
               this.deserialize(key, null);
            }
         });
         this.minCount = Adapters.INT.readJson(json.get("minCount")).orElse(null);
         this.maxCount = Adapters.INT.readJson(json.get("maxCount")).orElse(null);
      }

      public String getKey(Set<?> set) {
         if (set == this.neighborFilter) {
            return "neighborFilter";
         } else if (set == this.tierFilter) {
            return "tierFilter";
         } else if (set == this.colorFilter) {
            return "colorFilter";
         } else if (set == this.groupFilter) {
            return "groupFilter";
         } else {
            throw new UnsupportedOperationException();
         }
      }

      private Set getSet(String key) {
         if ("neighborFilter".equals(key)) {
            return this.neighborFilter;
         } else if ("tierFilter".equals(key)) {
            return this.tierFilter;
         } else if ("colorFilter".equals(key)) {
            return this.colorFilter;
         } else if ("groupFilter".equals(key)) {
            return this.groupFilter;
         } else {
            throw new UnsupportedOperationException();
         }
      }

      public ISimpleAdapter<Object, ? extends Tag, ? extends JsonElement> getElementAdapter(String key) {
         if ("neighborFilter".equals(key)) {
            return Adapters.ofEnum(CardNeighborType.class, EnumAdapter.Mode.NAME);
         } else if ("tierFilter".equals(key)) {
            return Adapters.INT_SEGMENTED_3;
         } else if ("colorFilter".equals(key)) {
            return Adapters.ofEnum(CardEntry.Color.class, EnumAdapter.Mode.NAME);
         } else if ("groupFilter".equals(key)) {
            return Adapters.UTF_8;
         } else {
            throw new UnsupportedOperationException();
         }
      }

      private void deserialize(String key, Set set) {
         if ("neighborFilter".equals(key)) {
            this.neighborFilter = set;
         } else if ("tierFilter".equals(key)) {
            this.tierFilter = set;
         } else if ("colorFilter".equals(key)) {
            this.colorFilter = set;
         } else if ("groupFilter".equals(key)) {
            this.groupFilter = set;
         }
      }

      public static class Config implements ISerializable<CompoundTag, JsonObject> {
         public static final SerializableAdapter<CardCondition.Filter.Config, CompoundTag, JsonObject> ADAPTER = Adapters.of(
            CardCondition.Filter.Config::new, false
         );
         private WeightedList<Set<CardNeighborType>> neighborFilter;
         private WeightedList<Set<Integer>> tierFilter;
         private WeightedList<Set<CardEntry.Color>> colorFilter;
         private WeightedList<Set<String>> groupFilter;
         private IntRoll minCount;
         private IntRoll maxCount;

         public Config() {
            this.neighborFilter = new WeightedList<>();
            this.tierFilter = new WeightedList<>();
            this.colorFilter = new WeightedList<>();
            this.groupFilter = new WeightedList<>();
         }

         public Config(
            WeightedList<Set<CardNeighborType>> neighborFilter,
            WeightedList<Set<Integer>> tierFilter,
            WeightedList<Set<CardEntry.Color>> colorFilter,
            WeightedList<Set<String>> groupFilter,
            IntRoll minCount,
            IntRoll maxCount
         ) {
            this.neighborFilter = neighborFilter;
            this.tierFilter = tierFilter;
            this.colorFilter = colorFilter;
            this.groupFilter = groupFilter;
            this.minCount = minCount;
            this.maxCount = maxCount;
         }

         public CardCondition.Filter generate(RandomSource random) {
            return new CardCondition.Filter(
               this.neighborFilter.getRandom(random).orElse(null),
               this.tierFilter.getRandom(random).orElse(null),
               this.colorFilter.getRandom(random).orElse(null),
               this.groupFilter.getRandom(random).orElse(null),
               this.minCount == null ? null : this.minCount.get(random),
               this.maxCount == null ? null : this.maxCount.get(random)
            );
         }

         @Override
         public void writeBits(BitBuffer buffer) {
            Stream.of(this.neighborFilter, this.tierFilter, this.colorFilter, this.groupFilter).forEach(list -> {
               Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(list.size()), buffer);
               list.forEach((set, weight) -> {
                  Adapters.BOOLEAN.writeBits(set == null, buffer);
                  if (set != null) {
                     Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(set.size()), buffer);
                     set.forEach(value -> this.getElementAdapter(list).writeBits(value, buffer));
                  }

                  Adapters.DOUBLE.writeBits(weight, buffer);
               });
            });
            Adapters.INT_ROLL.writeBits(this.minCount, buffer);
            Adapters.INT_ROLL.writeBits(this.maxCount, buffer);
         }

         @Override
         public void readBits(BitBuffer buffer) {
            Stream.of(this.neighborFilter, this.tierFilter, this.colorFilter, this.groupFilter).forEach(list -> {
               list.clear();
               int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

               for (int i = 0; i < size; i++) {
                  if (Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                     double weight = Adapters.DOUBLE.readBits(buffer).orElseThrow();
                     list.put(null, weight);
                  } else {
                     int setSize = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
                     Set<Object> set = new HashSet<>();

                     for (int j = 0; j < setSize; j++) {
                        set.add(this.getElementAdapter((WeightedList<?>)list).readBits(buffer).orElseThrow());
                     }

                     double weight = Adapters.DOUBLE.readBits(buffer).orElseThrow();
                     list.put(set, weight);
                  }
               }
            });
            this.minCount = Adapters.INT_ROLL.readBits(buffer).orElse(null);
            this.maxCount = Adapters.INT_ROLL.readBits(buffer).orElse(null);
         }

         @Override
         public Optional<CompoundTag> writeNbt() {
            return Optional.of(new CompoundTag()).map(nbt -> {
               Stream.of(this.neighborFilter, this.tierFilter, this.colorFilter, this.groupFilter).forEach(list -> {
                  ListTag listTag = new ListTag();
                  list.forEach((set, weight) -> {
                     CompoundTag entry = new CompoundTag();
                     if (set != null) {
                        ListTag setTag = new ListTag();
                        set.forEach(value -> this.getElementAdapter(list).writeNbt(value).ifPresent(setTag::add));
                        entry.put("value", setTag);
                     }

                     Adapters.DOUBLE.writeNbt(weight).ifPresent(tag -> entry.put("weight", tag));
                     listTag.add(entry);
                  });
                  nbt.put(this.getId((WeightedList<?>)list), listTag);
               });
               Adapters.INT_ROLL.writeNbt(this.minCount).ifPresent(tag -> nbt.put("minCount", tag));
               Adapters.INT_ROLL.writeNbt(this.maxCount).ifPresent(tag -> nbt.put("maxCount", tag));
               return (CompoundTag)nbt;
            });
         }

         public void readNbt(CompoundTag nbt) {
            Stream.of(this.neighborFilter, this.tierFilter, this.colorFilter, this.groupFilter).forEach(list -> {
               list.clear();
               ListTag listTag = nbt.getList(this.getId((WeightedList<?>)list), 10);

               for (int i = 0; i < listTag.size(); i++) {
                  CompoundTag entry = listTag.getCompound(i);
                  Set<Object> set;
                  if (entry.get("value") instanceof ListTag setTag) {
                     set = new HashSet<>();

                     for (int j = 0; j < setTag.size(); j++) {
                        this.getElementAdapter((WeightedList<?>)list).readNbt(setTag.get(j)).ifPresent(object -> set.add(object));
                     }
                  } else {
                     set = null;
                  }

                  list.put(set, Adapters.DOUBLE.readNbt(entry.get("weight")).orElseThrow());
               }
            });
            this.minCount = Adapters.INT_ROLL.readNbt(nbt.get("minCount")).orElse(null);
            this.maxCount = Adapters.INT_ROLL.readNbt(nbt.get("maxCount")).orElse(null);
         }

         @Override
         public Optional<JsonObject> writeJson() {
            return Optional.of(new JsonObject()).map(json -> {
               Stream.of(this.neighborFilter, this.tierFilter, this.colorFilter, this.groupFilter).forEach(list -> {
                  JsonArray listTag = new JsonArray();
                  list.forEach((set, weight) -> {
                     JsonObject entry = new JsonObject();
                     if (set != null) {
                        JsonArray setTag = new JsonArray();
                        set.forEach(value -> this.getElementAdapter(list).writeJson(value).ifPresent(setTag::add));
                        entry.add("value", setTag);
                     }

                     Adapters.DOUBLE.writeJson(weight).ifPresent(tag -> entry.add("weight", tag));
                     listTag.add(entry);
                  });
                  json.add(this.getId((WeightedList<?>)list), listTag);
               });
               Adapters.INT_ROLL.writeJson(this.minCount).ifPresent(tag -> json.add("minCount", tag));
               Adapters.INT_ROLL.writeJson(this.maxCount).ifPresent(tag -> json.add("maxCount", tag));
               return (JsonObject)json;
            });
         }

         public void readJson(JsonObject json) {
            Stream.of(this.neighborFilter, this.tierFilter, this.colorFilter, this.groupFilter).forEach(list -> {
               list.clear();
               if (json.get(this.getId(list)) instanceof JsonArray listTag) {
                  for (int var9 = 0; var9 < listTag.size(); var9++) {
                     JsonObject entry = listTag.get(var9).getAsJsonObject();
                     Set<Object> set;
                     if (entry.get("value") instanceof JsonArray setTag) {
                        set = new HashSet<>();

                        for (int j = 0; j < setTag.size(); j++) {
                           this.getElementAdapter((WeightedList<?>)list).readJson(setTag.get(j)).ifPresent(object -> set.add(object));
                        }
                     } else {
                        set = null;
                     }

                     list.put(set, Adapters.DOUBLE.readJson(entry.get("weight")).orElseThrow());
                  }
               }
            });
            this.minCount = Adapters.INT_ROLL.readJson(json.get("minCount")).orElse(null);
            this.maxCount = Adapters.INT_ROLL.readJson(json.get("maxCount")).orElse(null);
         }

         public String getId(WeightedList<?> list) {
            if (list == this.neighborFilter) {
               return "neighborFilter";
            } else if (list == this.tierFilter) {
               return "tierFilter";
            } else if (list == this.colorFilter) {
               return "colorFilter";
            } else if (list == this.groupFilter) {
               return "groupFilter";
            } else {
               throw new UnsupportedOperationException();
            }
         }

         public ISimpleAdapter<Object, ? extends Tag, ? extends JsonElement> getElementAdapter(WeightedList<?> list) {
            if (list == this.neighborFilter) {
               return Adapters.ofEnum(CardNeighborType.class, EnumAdapter.Mode.NAME);
            } else if (list == this.tierFilter) {
               return Adapters.INT_SEGMENTED_3;
            } else if (list == this.colorFilter) {
               return Adapters.ofEnum(CardEntry.Color.class, EnumAdapter.Mode.NAME);
            } else if (list == this.groupFilter) {
               return Adapters.UTF_8;
            } else {
               throw new UnsupportedOperationException();
            }
         }
      }
   }
}
