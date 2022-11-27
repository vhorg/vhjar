package iskallia.vault.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class EtchingConfig extends Config {
   @Expose
   public EtchingConfig.EtchingMap ETCHINGS = new EtchingConfig.EtchingMap();

   @Override
   public String getName() {
      return "etching";
   }

   @Nullable
   public EtchingConfig.Etching getEtchingConfig(EtchingSet<?> etchingSet) {
      return this.getEtchingConfig(etchingSet.getRegistryName());
   }

   @Nullable
   private EtchingConfig.Etching getEtchingConfig(ResourceLocation key) {
      return this.ETCHINGS.get(key);
   }

   @Nullable
   public EtchingSet<?> getRandomEtchingSet() {
      WeightedList<EtchingSet<?>> list = new WeightedList<>();
      this.ETCHINGS.forEach((key, config) -> {
         EtchingSet<?> set = EtchingRegistry.getEtchingSet(key);
         if (set != null) {
            list.add(set, config.getWeight());
         }
      });
      return list.getRandom().orElse(null);
   }

   @Override
   protected void reset() {
      for (EtchingSet<?> etchingSet : EtchingRegistry.getOrderedEntries()) {
         EtchingConfig.Etching etching = new EtchingConfig.Etching(100, 10, 50, etchingSet.getRegistryName().getPath(), "Effect Text", 6084886);
         etching.etchingConfig = etchingSet.getDefaultConfig();
         this.ETCHINGS.put(etchingSet.getRegistryName(), etching);
      }
   }

   public static class Etching {
      @Expose
      private int weight;
      @Expose
      private String name;
      @Expose
      private String effectText;
      @Expose
      private int color;
      @Expose
      private int vendorMinValue;
      @Expose
      private int vendorMaxValue;
      private Object etchingConfig;

      public Etching(int weight, int minValue, int maxValue, String name, String effectText, int color) {
         this.weight = weight;
         this.vendorMinValue = minValue;
         this.vendorMaxValue = maxValue;
         this.name = name;
         this.effectText = effectText;
         this.color = color;
      }

      public int getWeight() {
         return this.weight;
      }

      public String getName() {
         return this.name;
      }

      public String getEffectText() {
         return this.effectText;
      }

      public int getColor() {
         return this.color;
      }

      public int getVendorMinValue() {
         return this.vendorMinValue;
      }

      public int getVendorMaxValue() {
         return this.vendorMaxValue;
      }

      public TextColor getComponentColor() {
         return TextColor.fromRgb(this.getColor());
      }

      public Object getConfig() {
         return this.etchingConfig;
      }
   }

   public static class EtchingMap extends LinkedHashMap<ResourceLocation, EtchingConfig.Etching> {
      public static class Serializer implements JsonSerializer<EtchingConfig.EtchingMap>, JsonDeserializer<EtchingConfig.EtchingMap> {
         public EtchingConfig.EtchingMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            EtchingConfig.EtchingMap etchingMap = new EtchingConfig.EtchingMap();
            JsonObject map = json.getAsJsonObject();

            for (String etchingKeyStr : map.keySet()) {
               ResourceLocation etchingKey = new ResourceLocation(etchingKeyStr);
               EtchingSet<?> etchingSet = EtchingRegistry.getEtchingSet(etchingKey);
               if (etchingSet != null) {
                  JsonObject etchingConfigObject = map.getAsJsonObject(etchingKeyStr);
                  EtchingConfig.Etching etchingConfig = (EtchingConfig.Etching)context.deserialize(etchingConfigObject, EtchingConfig.Etching.class);
                  etchingConfig.etchingConfig = context.deserialize(etchingConfigObject.get("config"), etchingSet.getConfigClass());
                  etchingMap.put(etchingKey, etchingConfig);
               }
            }

            return etchingMap;
         }

         public JsonElement serialize(EtchingConfig.EtchingMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject map = new JsonObject();
            src.forEach((etchingKey, etchingConfig) -> {
               EtchingSet<?> etchingSet = EtchingRegistry.getEtchingSet(etchingKey);
               if (etchingSet != null) {
                  JsonObject etchingConfigObject = context.serialize(etchingConfig).getAsJsonObject();
                  etchingConfigObject.add("config", context.serialize(etchingConfig.etchingConfig, etchingSet.getConfigClass()));
                  map.add(etchingKey.toString(), etchingConfigObject);
               }
            });
            return map;
         }
      }
   }
}
