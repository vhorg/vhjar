package iskallia.vault.core.card;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class Card implements ISerializable<CompoundTag, JsonObject> {
   public static final SerializableAdapter<Card, CompoundTag, JsonObject> ADAPTER = Adapters.of(Card::new, true);
   public static Set<String> TYPES = new HashSet<>(Arrays.asList("Arcane", "Temporal", "Resource", "Evolution", "Stat", "Wild"));
   private int tier;
   private List<CardEntry> entries;
   public static final ArrayAdapter<CardEntry> ENTRIES = Adapters.ofArray(CardEntry[]::new, Adapters.of(CardEntry::new, false));

   public Card() {
      this.tier = 1;
      this.entries = new ArrayList<>();
   }

   public Card(int tier, List<CardEntry> entries) {
      this.tier = tier;
      this.entries = entries;
   }

   public int getTier() {
      return this.tier;
   }

   public List<CardEntry> getEntries() {
      return this.entries;
   }

   public Component getFirstName() {
      for (CardEntry entry : this.entries) {
         if (entry.getName() != null) {
            return entry.getName();
         }
      }

      return null;
   }

   public Set<CardEntry.Color> getColors() {
      return this.entries.stream().flatMap(entry -> entry.getColors().stream()).collect(Collectors.toSet());
   }

   public CardEntry.Color getFirstColor() {
      for (CardEntry entry : this.entries) {
         if (!entry.getColors().isEmpty()) {
            return entry.getColors().iterator().next();
         }
      }

      return null;
   }

   @Nullable
   public String getFirstModel() {
      for (CardEntry entry : this.entries) {
         if (entry.getModel() != null) {
            return entry.getModel();
         }
      }

      return null;
   }

   public boolean canUpgrade() {
      for (CardEntry entry : this.entries) {
         if (this.tier < entry.getModifier().getMaxTier()) {
            return true;
         }
      }

      return false;
   }

   public void onUpgrade() {
      this.tier++;
   }

   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Tier: ").append(new TextComponent(this.tier + "").setStyle(Style.EMPTY.withColor(11583738))));
      List<CardEntry.Color> colors = new ArrayList<>(this.getColors());
      if (!colors.isEmpty()) {
         TextComponent colorText = new TextComponent("");

         for (int i = 0; i < colors.size(); i++) {
            colorText.append(colors.get(i).getColoredText());
            if (i != colors.size() - 1) {
               colorText.append(new TextComponent(", ").withStyle(ChatFormatting.GRAY));
            }
         }

         tooltip.add(new TextComponent("Color: ").append(colorText));
      }

      List<String> groups = new ArrayList<>(this.getGroups());
      List<String> types = new ArrayList<>();

      for (String type : TYPES) {
         if (groups.remove(type)) {
            types.add(type);
         }
      }

      if (!types.isEmpty()) {
         TextComponent typeText = new TextComponent("");

         for (int ix = 0; ix < types.size(); ix++) {
            typeText.append(new TextComponent(types.get(ix)).withStyle(Style.EMPTY.withColor(13421772)));
            if (ix != types.size() - 1) {
               typeText.append(new TextComponent(", ").withStyle(ChatFormatting.GRAY));
            }
         }

         tooltip.add(new TextComponent("Type: ").append(typeText));
      }

      if (!groups.isEmpty()) {
         TextComponent groupText = new TextComponent("");

         for (int ixx = 0; ixx < groups.size(); ixx++) {
            groupText.append(new TextComponent(groups.get(ixx)).withStyle(Style.EMPTY.withColor(13421772)));
            if (ixx != groups.size() - 1) {
               groupText.append(new TextComponent(", ").withStyle(ChatFormatting.GRAY));
            }
         }

         tooltip.add(new TextComponent("Groups: ").append(groupText));
      }

      for (CardEntry entry : this.entries) {
         entry.addText(tooltip, minIndex, flag, time, this.tier);
      }
   }

   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      for (CardEntry entry : this.entries) {
         entry.onInventoryTick(world, entity, slot, selected, this.tier);
      }
   }

   public boolean hasGroup(String group) {
      for (CardEntry entry : this.entries) {
         if (entry.getGroups().contains(group)) {
            return true;
         }
      }

      return false;
   }

   public List<String> getGroups() {
      return this.entries.stream().map(CardEntry::getGroups).flatMap(Collection::stream).distinct().toList();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      ENTRIES.writeBits(this.entries.toArray(CardEntry[]::new), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.tier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.entries = Arrays.stream(ENTRIES.readBits(buffer).orElse(new CardEntry[0])).collect(Collectors.toList());
      this.tier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         ENTRIES.writeNbt(this.entries.toArray(CardEntry[]::new)).ifPresent(tag -> nbt.put("entries", tag));
         Adapters.INT_SEGMENTED_3.writeNbt(Integer.valueOf(this.tier)).ifPresent(tag -> nbt.put("tier", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.entries = Arrays.stream(ENTRIES.readNbt(nbt.get("entries")).orElse(new CardEntry[0])).collect(Collectors.toList());
      this.tier = Adapters.INT_SEGMENTED_3.readNbt(nbt.get("tier")).orElse(1);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject()).map(json -> {
         ENTRIES.writeJson(this.entries.toArray(CardEntry[]::new)).ifPresent(tag -> json.add("entries", tag));
         Adapters.INT_SEGMENTED_3.writeJson(Integer.valueOf(this.tier)).ifPresent(tag -> json.add("tier", tag));
         return (JsonObject)json;
      });
   }

   public void readJson(JsonObject json) {
      this.entries = Arrays.stream(ENTRIES.readJson(json.get("entries")).orElse(new CardEntry[0])).collect(Collectors.toList());
      this.tier = Adapters.INT_SEGMENTED_3.readJson(json.get("tier")).orElse(1);
   }

   public static class Config implements ISerializable<CompoundTag, JsonObject> {
      public static final SerializableAdapter<Card.Config, CompoundTag, JsonObject> ADAPTER = Adapters.of(Card.Config::new, true);
      private final Map<WeightedList<CardEntry>, Double> modifiers = new LinkedHashMap<>();

      public Card generate(int tier, RandomSource random) {
         List<CardEntry> entries = new ArrayList<>();
         this.modifiers.forEach((pool, probability) -> {
            if (!(random.nextDouble() >= probability)) {
               pool.getRandom(random).ifPresent(entry -> {
                  entry = entry.copy();
                  CardEntry.Color[] colors = CardEntry.Color.values();
                  entry.setColors(Set.of(colors[random.nextInt(colors.length)]));
                  entries.add(entry);
               });
            }
         });
         return new Card(tier, entries);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            JsonArray modifiers = new JsonArray();
            this.modifiers.forEach((pool, probability) -> {
               JsonObject element = new JsonObject();
               JsonArray array = new JsonArray();
               pool.forEach((entry, weight) -> entry.writeJson().ifPresent(modifier -> {
                  Adapters.DOUBLE.writeJson(weight).ifPresent(tag -> modifier.add("weight", tag));
                  array.add(modifier);
               }));
               element.add("pool", array);
               Adapters.DOUBLE.writeJson(probability).ifPresent(tag -> element.add("probability", tag));
               modifiers.add(element);
            });
            json.add("modifiers", modifiers);
            return (JsonObject)json;
         });
      }

      public void readJson(JsonObject json) {
         this.modifiers.clear();

         for (JsonElement child1 : json.getAsJsonArray("modifiers")) {
            if (child1 instanceof JsonObject) {
               JsonObject element = (JsonObject)child1;
               JsonArray array = element.getAsJsonArray("pool");
               WeightedList<CardEntry> pool = new WeightedList<>();

               for (JsonElement child2 : array) {
                  if (child2 instanceof JsonObject modifier) {
                     CardEntry entry = new CardEntry();
                     entry.readJson(modifier);
                     double weight = Adapters.DOUBLE.readJson(modifier.get("weight")).orElseThrow();
                     pool.put(entry, weight);
                  }
               }

               this.modifiers.put(pool, Adapters.DOUBLE.readJson(element.get("probability")).orElse(1.0));
            }
         }
      }
   }
}
