package iskallia.vault.core.card;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class GearCardModifier<T> extends CardModifier<GearCardModifier.Config<T>> {
   private final Map<Integer, T> values = new LinkedHashMap<>();

   public GearCardModifier() {
      super(new GearCardModifier.Config<>());
   }

   public GearCardModifier(GearCardModifier.Config<T> config) {
      super(config);
   }

   public VaultGearAttribute<T> getAttribute() {
      return this.getConfig().getAttribute();
   }

   public T getValue(int tier) {
      return CardEntry.getForTier(this.values, tier).orElseThrow();
   }

   @Override
   public int getHighlightColor() {
      return this.getAttribute().getReader().getRgbColor();
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int tier) {
      CardEntry.getForTier(this.values, tier)
         .ifPresent(
            value -> {
               long window = Minecraft.getInstance().getWindow().getWindow();
               boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
               VaultGearAttributeInstance<T> instance = new VaultGearAttributeInstance<>(this.getAttribute(), (T)value);
               MutableComponent text = this.getAttribute().getReader().getDisplay(instance, VaultGearModifier.AffixType.PREFIX);
               if (shiftDown) {
                  Style txtStyle = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false).withUnderlined(false).withBold(false);
                  MutableComponent cmpRangeDescriptor = new TextComponent("");
                  List<Object> configs = this.getConfig().pool.keySet().stream().map(s -> this.getConfig().getConfig(s)).toList();
                  MutableComponent minMaxRangeCmp = ((ConfigurableAttributeGenerator<T, Object>)this.getAttribute()
                     .getGenerator())
                     .getConfigRangeDisplay(this.getAttribute().getReader(), configs.get(0), configs.get(configs.size() - 1));
                  if (minMaxRangeCmp != null) {
                     if (!cmpRangeDescriptor.getString().isBlank()) {
                        cmpRangeDescriptor.append(" ");
                     }

                     cmpRangeDescriptor.append(minMaxRangeCmp);
                     if (Screen.hasAltDown()) {
                        cmpRangeDescriptor.append(",");
                     }
                  }

                  if (Screen.hasAltDown()) {
                     if (!cmpRangeDescriptor.getString().isBlank()) {
                        cmpRangeDescriptor.append(" ");
                     }

                     MutableComponent rangeCmp = ((ConfigurableAttributeGenerator<T, Object>)this.getAttribute()
                        .getGenerator())
                        .getConfigRangeDisplay(this.getAttribute().getReader(), this.getConfig().getConfig(tier));
                     CardEntry.getTier(this.values, tier).ifPresent(t -> {
                        if (rangeCmp != null) {
                           cmpRangeDescriptor.append("T%s: ".formatted(t));
                           cmpRangeDescriptor.append(rangeCmp);
                        }
                     });
                  }

                  if (!cmpRangeDescriptor.getString().isBlank()) {
                     text.append(new TextComponent(" ").withStyle(txtStyle).append("(").append(cmpRangeDescriptor).append(")"));
                  }
               }

               tooltip.add(text);
            }
         );
   }

   @Override
   public boolean onPopulate() {
      if (!super.onPopulate()) {
         return false;
      } else {
         this.getConfig().getPool().forEach((tier, configNbt) -> {
            ConfigurableAttributeGenerator generator = this.getAttribute().getGenerator();
            Object config = this.getConfig().getConfig(tier);
            if (config != null) {
               this.values.put(tier, (T)generator.generateRandomValue(config, new Random()));
            }
         });
         return true;
      }
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getSnapshotAttributes(int tier) {
      return List.of(new VaultGearAttributeInstance<>(this.getAttribute(), this.getValue(tier)));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      if (this.isPopulated()) {
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.values.size()), buffer);
         this.values.forEach((tier, value) -> {
            Adapters.INT_SEGMENTED_3.writeBits(tier, buffer);
            this.getAttribute().getType().write(buffer, (T)value);
         });
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      if (this.isPopulated()) {
         this.values.clear();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < size; i++) {
            this.values.put(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow(), this.getAttribute().getType().read(buffer));
         }
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.isPopulated()) {
            return (CompoundTag)nbt;
         } else {
            ListTag values = new ListTag();
            this.values.forEach((tier, value) -> {
               CompoundTag entry = new CompoundTag();
               Adapters.INT.writeNbt(tier).ifPresent(tag -> entry.put("tier", tag));
               entry.put("value", this.getAttribute().getType().nbtWrite((T)value));
               values.add(entry);
            });
            nbt.put("values", values);
            return (CompoundTag)nbt;
         }
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (this.isPopulated()) {
         this.values.clear();
         ListTag pool = nbt.getList("values", 10);

         for (int i = 0; i < pool.size(); i++) {
            CompoundTag entry = pool.getCompound(i);
            this.values.put(Adapters.INT.readNbt(entry.get("tier")).orElseThrow(), this.getAttribute().getType().nbtRead(entry.get("value")));
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.isPopulated()) {
            return (JsonObject)json;
         } else {
            JsonArray values = new JsonArray();
            this.values.forEach((tier, value) -> {
               JsonObject entry = new JsonObject();
               Adapters.INT.writeJson(tier).ifPresent(tag -> entry.add("tier", tag));
               Adapters.GENERIC_NBT.writeJson(this.getAttribute().getType().nbtWrite((T)value)).ifPresent(tag -> entry.add("value", tag));
               values.add(entry);
            });
            json.add("values", values);
            return (JsonObject)json;
         }
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (this.isPopulated()) {
         this.values.clear();
         if (json.get("pool") instanceof JsonArray pool) {
            for (int i = 0; i < pool.size(); i++) {
               JsonObject entry = pool.get(i).getAsJsonObject();
               this.values
                  .put(
                     Adapters.INT.readJson(entry.get("tier")).orElseThrow(),
                     this.getAttribute().getType().nbtRead(Adapters.GENERIC_NBT.readJson(entry.get("value")).orElseThrow())
                  );
            }
         }
      }
   }

   public static class Config<T> extends CardModifier.Config {
      private VaultGearAttribute<T> attribute;
      private final Map<Integer, String> pool;

      public Config() {
         this.pool = new LinkedHashMap<>();
      }

      public Config(VaultGearAttribute<T> attribute, Map<Integer, String> pool) {
         this.attribute = attribute;
         this.pool = pool;
      }

      public VaultGearAttribute<T> getAttribute() {
         return this.attribute;
      }

      public Map<Integer, String> getPool() {
         return this.pool;
      }

      public Object getConfig(int tier) {
         return CardEntry.getForTier(this.pool, tier).map(configString -> {
            Class<?> configClass = this.getAttribute().getGenerator().getConfigurationObjectClass();
            if (configClass != null) {
               JsonElement configJson = JsonParser.parseString(configString);
               Object config = GearCardModifier.GsonContext.INSTANCE.deserialize(configJson, configClass);
               if (config instanceof ConfigurableAttributeGenerator.CustomTierConfig custom && configJson instanceof JsonObject object) {
                  custom.deserializeAdditional(object, GearCardModifier.GsonContext.INSTANCE);
               }

               return config;
            } else {
               return null;
            }
         }).orElse(null);
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.GEAR_ATTRIBUTE.writeBits((IForgeRegistryEntry)this.attribute, buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.pool.size()), buffer);
         this.pool.forEach((tier, configString) -> {
            Adapters.INT_SEGMENTED_3.writeBits(tier, buffer);
            Adapters.UTF_8.writeBits(configString, buffer);
         });
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.attribute = (VaultGearAttribute<T>)Adapters.GEAR_ATTRIBUTE.readBits(buffer).orElseThrow();
         this.pool.clear();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < size; i++) {
            this.pool.put(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow(), Adapters.UTF_8.readBits(buffer).orElseThrow());
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.GEAR_ATTRIBUTE.writeNbt((IForgeRegistryEntry)this.attribute).ifPresent(tag -> nbt.put("attribute", tag));
            ListTag pool = new ListTag();
            this.pool.forEach((tier, configString) -> {
               CompoundTag entry = new CompoundTag();
               Adapters.UTF_8.writeNbt(configString).ifPresent(tag -> entry.put("config", tag));
               Adapters.INT.writeNbt(tier).ifPresent(tag -> entry.put("tier", tag));
               pool.add(entry);
            });
            nbt.put("pool", pool);
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.attribute = (VaultGearAttribute<T>)Adapters.GEAR_ATTRIBUTE.readNbt(nbt.get("attribute")).orElseThrow();
         this.pool.clear();
         ListTag pool = nbt.getList("pool", 10);

         for (int i = 0; i < pool.size(); i++) {
            CompoundTag entry = pool.getCompound(i);
            this.pool.put(Adapters.INT.readNbt(entry.get("tier")).orElseThrow(), Adapters.UTF_8.readNbt(entry.get("config")).orElseThrow());
         }
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.GEAR_ATTRIBUTE.writeJson((IForgeRegistryEntry)this.attribute).ifPresent(tag -> json.add("attribute", tag));
            JsonArray pool = new JsonArray();
            this.pool.forEach((tier, configString) -> {
               JsonObject entry = (JsonObject)JsonParser.parseString(configString);
               Adapters.INT.writeJson(tier).ifPresent(tag -> entry.add("tier", tag));
               pool.add(entry);
            });
            json.add("pool", pool);
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.attribute = (VaultGearAttribute<T>)Adapters.GEAR_ATTRIBUTE.readJson(json.get("attribute")).orElseThrow();
         this.pool.clear();
         if (json.get("pool") instanceof JsonArray pool) {
            for (int i = 0; i < pool.size(); i++) {
               JsonObject entry = pool.get(i).getAsJsonObject();
               if (entry.has("config")) {
                  this.pool.put(Adapters.INT.readJson(entry.get("tier")).orElseThrow(), entry.get("config").toString());
               } else {
                  int tier = Adapters.INT.readJson(entry.get("tier")).orElseThrow();
                  entry.remove("tier");
                  this.pool.put(tier, entry.toString());
               }
            }
         }
      }
   }

   private static class GsonContext implements JsonSerializationContext, JsonDeserializationContext {
      public static final GearCardModifier.GsonContext INSTANCE = new GearCardModifier.GsonContext();

      public JsonElement serialize(Object src) {
         return iskallia.vault.config.Config.GSON.toJsonTree(src);
      }

      public JsonElement serialize(Object src, Type typeOfSrc) {
         return iskallia.vault.config.Config.GSON.toJsonTree(src, typeOfSrc);
      }

      public <R> R deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
         return (R)iskallia.vault.config.Config.GSON.fromJson(json, typeOfT);
      }
   }
}
