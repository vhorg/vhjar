package iskallia.vault.core.card;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CardEntry implements ISerializable<CompoundTag, JsonObject> {
   private Component name;
   private Set<CardEntry.Color> colors;
   private Set<String> groups;
   @Nullable
   private String model;
   private CardModifier<?> modifier;
   private CardScaler scaler;
   private CardCondition condition;
   public static final ArrayAdapter<CardEntry.Color> COLORS = Adapters.ofArray(
      CardEntry.Color[]::new, Adapters.ofEnum(CardEntry.Color.class, EnumAdapter.Mode.NAME)
   );
   public static final ArrayAdapter<String> GROUPS = Adapters.ofArray(String[]::new, Adapters.UTF_8);

   public CardEntry() {
      this.colors = new HashSet<>();
      this.groups = new HashSet<>();
   }

   public CardEntry(
      Component name,
      Set<CardEntry.Color> colors,
      Set<String> groups,
      @Nullable String model,
      CardModifier<?> modifier,
      CardScaler scaler,
      CardCondition condition
   ) {
      this.name = name;
      this.colors = colors;
      this.groups = groups;
      this.model = model;
      this.modifier = modifier;
      this.scaler = scaler;
      this.condition = condition;
   }

   public Component getName() {
      return this.name;
   }

   public CardScaler getScaler() {
      return this.scaler;
   }

   public Set<CardEntry.Color> getColors() {
      return this.colors;
   }

   public void setColors(Set<CardEntry.Color> colors) {
      this.colors = colors;
   }

   public Set<String> getGroups() {
      return this.groups;
   }

   @Nullable
   public String getModel() {
      return this.model;
   }

   public void setModel(@Nullable String model) {
      this.model = model;
   }

   public CardModifier<?> getModifier() {
      return this.modifier;
   }

   public void setScaler(CardScaler scaler) {
      this.scaler = scaler;
   }

   public void setCondition(CardCondition condition) {
      this.condition = condition;
   }

   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int tier) {
      this.modifier.addText(tooltip, minIndex, flag, time, tier);
      if (this.scaler != null) {
         this.scaler.addText(tooltip, minIndex, flag, time, tier);
      }

      if (this.condition != null) {
         this.condition.addText(tooltip, minIndex, flag, time, tier);
      }
   }

   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected, int tier) {
      this.modifier.onInventoryTick(world, entity, slot, selected, tier);
      if (this.scaler != null) {
         this.scaler.onInventoryTick(world, entity, slot, selected, tier);
      }

      if (this.condition != null) {
         this.condition.onInventoryTick(world, entity, slot, selected, tier);
      }
   }

   public List<VaultGearAttributeInstance<?>> getSnapshotAttributes(int tier, CardPos pos, CardDeck deck) {
      List<VaultGearAttributeInstance<?>> attributes = new ArrayList<>();
      if (this.condition != null && !this.condition.test(tier, pos, deck)) {
         return attributes;
      } else {
         List<VaultGearAttributeInstance<?>> base = this.modifier.getSnapshotAttributes(tier);

         for (int i = 0; i < (this.scaler == null ? 1 : this.scaler.getFrequency(tier, pos, deck)); i++) {
            attributes.addAll(base);
         }

         return attributes;
      }
   }

   public static <T> Optional<T> getForTier(Map<Integer, T> map, int tier) {
      int nearest = Integer.MAX_VALUE;
      T result = null;

      for (Entry<Integer, T> entry : map.entrySet()) {
         if (entry.getKey() <= tier) {
            int difference = tier - entry.getKey();
            if (difference < nearest) {
               nearest = difference;
               result = entry.getValue();
            }
         }
      }

      return Optional.ofNullable(result);
   }

   public static <T> Optional<Integer> getTier(Map<Integer, T> map, int tier) {
      int nearest = Integer.MAX_VALUE;
      Integer result = null;

      for (Entry<Integer, T> entry : map.entrySet()) {
         if (entry.getKey() <= tier) {
            int difference = tier - entry.getKey();
            if (difference < nearest) {
               nearest = difference;
               result = entry.getKey();
            }
         }
      }

      return Optional.ofNullable(result);
   }

   public CardEntry copy() {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      this.writeBits(buffer);
      buffer.setPosition(0);
      CardEntry copy = new CardEntry();
      copy.readBits(buffer);
      return copy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.COMPONENT.asNullable().writeBits(this.name, buffer);
      COLORS.writeBits(this.colors.toArray(CardEntry.Color[]::new), buffer);
      GROUPS.writeBits(this.groups.toArray(String[]::new), buffer);
      Adapters.UTF_8.asNullable().writeBits(this.model, buffer);
      CardModifier.ADAPTER.writeBits(this.modifier, buffer);
      CardScaler.ADAPTER.writeBits(this.scaler, buffer);
      CardCondition.ADAPTER.writeBits(this.condition, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.name = Adapters.COMPONENT.asNullable().readBits(buffer).orElse(null);
      this.colors = Arrays.stream(COLORS.readBits(buffer).orElse(new CardEntry.Color[0])).collect(Collectors.toSet());
      this.groups = Arrays.stream(GROUPS.readBits(buffer).orElse(new String[0])).collect(Collectors.toSet());
      this.model = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
      this.modifier = CardModifier.ADAPTER.readBits(buffer).orElse(null);
      this.scaler = CardScaler.ADAPTER.readBits(buffer).orElse(null);
      this.condition = CardCondition.ADAPTER.readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         Adapters.COMPONENT.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
         COLORS.writeNbt(this.colors.toArray(CardEntry.Color[]::new)).ifPresent(tag -> nbt.put("colors", tag));
         GROUPS.writeNbt(this.groups.toArray(String[]::new)).ifPresent(tag -> nbt.put("groups", tag));
         Adapters.UTF_8.writeNbt(this.model).ifPresent(tag -> nbt.put("model", tag));
         CardModifier.ADAPTER.writeNbt(this.modifier).ifPresent(tag -> nbt.put("modifier", tag));
         CardScaler.ADAPTER.writeNbt(this.scaler).ifPresent(tag -> nbt.put("scaler", tag));
         CardCondition.ADAPTER.writeNbt(this.condition).ifPresent(tag -> nbt.put("condition", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.name = Adapters.COMPONENT.readNbt(nbt.get("name")).orElse(null);
      this.colors = Arrays.stream(COLORS.readNbt(nbt.get("colors")).orElse(new CardEntry.Color[0])).collect(Collectors.toSet());
      this.groups = Arrays.stream(GROUPS.readNbt(nbt.get("groups")).orElse(new String[0])).collect(Collectors.toSet());
      this.model = Adapters.UTF_8.readNbt(nbt.get("model")).orElse(null);
      this.modifier = CardModifier.ADAPTER.readNbt(nbt.get("modifier")).orElse(null);
      this.scaler = CardScaler.ADAPTER.readNbt((CompoundTag)nbt.get("scaler")).orElse(null);
      this.condition = CardCondition.ADAPTER.readNbt((CompoundTag)nbt.get("condition")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject()).map(json -> {
         Adapters.COMPONENT.writeJson(this.name).ifPresent(tag -> json.add("name", tag));
         COLORS.writeJson(this.colors.toArray(CardEntry.Color[]::new)).ifPresent(tag -> json.add("colors", tag));
         GROUPS.writeJson(this.groups.toArray(String[]::new)).ifPresent(tag -> json.add("groups", tag));
         Adapters.UTF_8.writeJson(this.model).ifPresent(tag -> json.add("model", tag));
         CardModifier.ADAPTER.writeJson(this.modifier).ifPresent(tag -> json.add("modifier", tag));
         CardScaler.ADAPTER.writeJson(this.scaler).ifPresent(tag -> json.add("scaler", tag));
         CardCondition.ADAPTER.writeJson(this.condition).ifPresent(tag -> json.add("condition", tag));
         return (JsonObject)json;
      });
   }

   public void readJson(JsonObject json) {
      this.name = Adapters.COMPONENT.readJson(json.get("name")).orElse(null);
      this.colors = Arrays.stream(COLORS.readJson(json.get("colors")).orElse(new CardEntry.Color[0])).collect(Collectors.toSet());
      this.groups = Arrays.stream(GROUPS.readJson(json.get("groups")).orElse(new String[0])).collect(Collectors.toSet());
      this.model = Adapters.UTF_8.readJson(json.get("model")).orElse(null);
      this.modifier = CardModifier.ADAPTER.readJson(json.get("modifier")).orElse(null);
      this.scaler = CardScaler.ADAPTER.readJson(json.getAsJsonObject("scaler")).orElse(null);
      this.condition = CardCondition.ADAPTER.readJson(json.getAsJsonObject("condition")).orElse(null);
   }

   public static enum Color {
      GREEN("Green", 5635925),
      BLUE("Blue", 5636095),
      YELLOW("Yellow", 16777045),
      RED("Red", 16733525);

      private final String name;
      private final int color;

      private Color(String name, int color) {
         this.name = name;
         this.color = color;
      }

      public Component getColoredText() {
         return new TextComponent(this.name).setStyle(Style.EMPTY.withColor(this.color));
      }
   }

   public static class Config implements ISerializable<CompoundTag, JsonObject> {
      public static final SerializableAdapter<CardEntry.Config, CompoundTag, JsonObject> ADAPTER = Adapters.of(CardEntry.Config::new, false);
      public static final ArrayAdapter<CardEntry.Color> COLORS = Adapters.ofArray(
         CardEntry.Color[]::new, Adapters.ofEnum(CardEntry.Color.class, EnumAdapter.Mode.NAME)
      );
      public static final ArrayAdapter<String> GROUPS = Adapters.ofArray(String[]::new, Adapters.UTF_8);
      public Component name;
      public Set<CardEntry.Color> colors;
      public Set<String> groups;
      @Nullable
      public String model;
      public CardModifier<?> value;
      public CardScaler scaler;
      public CardCondition condition;

      public Config() {
      }

      public Config(
         Component name,
         Set<CardEntry.Color> colors,
         Set<String> groups,
         @Nullable String model,
         CardModifier<?> value,
         CardScaler scaler,
         CardCondition condition
      ) {
         this.name = name;
         this.colors = colors;
         this.groups = groups;
         this.model = model;
         this.value = value;
         this.scaler = scaler;
         this.condition = condition;
      }

      public CardEntry toEntry() {
         return new CardEntry(this.name, this.colors, this.groups, this.model, this.value, this.scaler, this.condition).copy();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            if (this.scaler == null && this.condition == null) {
               CardModifier.ADAPTER.writeJson(this.value).ifPresent(tag -> {
                  if (tag instanceof JsonObject object) {
                     object.entrySet().forEach(entry -> json.add((String)entry.getKey(), (JsonElement)entry.getValue()));
                  }
               });
            } else {
               CardModifier.ADAPTER.writeJson(this.value).ifPresent(tag -> json.add("value", tag));
            }

            COLORS.writeJson(this.colors.toArray(CardEntry.Color[]::new)).ifPresent(tag -> json.add("colors", tag));
            GROUPS.writeJson(this.groups.toArray(String[]::new)).ifPresent(tag -> json.add("groups", tag));
            Adapters.COMPONENT.writeJson(this.name).ifPresent(tag -> json.add("name", tag));
            Adapters.UTF_8.writeJson(this.model).ifPresent(tag -> json.add("model", tag));
            CardScaler.ADAPTER.writeJson(this.scaler).ifPresent(tag -> json.add("scaler", tag));
            CardCondition.ADAPTER.writeJson(this.condition).ifPresent(tag -> json.add("condition", tag));
            return (JsonObject)json;
         });
      }

      public void readJson(JsonObject json) {
         this.name = Adapters.COMPONENT.readJson(json.getAsJsonObject("name")).orElseThrow();
         this.model = Adapters.UTF_8.readJson(json.get("model")).orElse(null);
         this.colors = Arrays.stream(COLORS.readJson(json.get("colors")).orElse(new CardEntry.Color[0])).collect(Collectors.toSet());
         this.groups = Arrays.stream(GROUPS.readJson(json.get("groups")).orElse(new String[0])).collect(Collectors.toSet());
         if (json.has("type")) {
            this.value = CardModifier.ADAPTER.readJson(json).orElseThrow();
            this.scaler = null;
            this.condition = null;
         } else {
            this.value = CardModifier.ADAPTER.readJson(json.getAsJsonObject("value")).orElseThrow();
            this.scaler = CardScaler.ADAPTER.readJson(json.getAsJsonObject("scaler")).orElseThrow();
            this.condition = CardCondition.ADAPTER.readJson(json.getAsJsonObject("condition")).orElseThrow();
         }
      }

      public CardEntry.Config copy() {
         CardEntry.Config config = new CardEntry.Config();
         this.writeJson().ifPresent(config::readJson);
         return config;
      }
   }
}
