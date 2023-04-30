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
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TrinketConfig extends Config {
   @Expose
   public TrinketConfig.TrinketMap TRINKETS = new TrinketConfig.TrinketMap();

   @Override
   public String getName() {
      return "trinket";
   }

   @Nullable
   public TrinketConfig.Trinket getTrinketConfig(TrinketEffect<?> trinketEffect) {
      return this.getTrinketConfig(trinketEffect.getRegistryName());
   }

   @Nullable
   private TrinketConfig.Trinket getTrinketConfig(ResourceLocation key) {
      return this.TRINKETS.get(key);
   }

   @Nullable
   public TrinketEffect<?> getRandomTrinketSet() {
      WeightedList<TrinketEffect<?>> list = new WeightedList<>();
      this.TRINKETS.forEach((key, config) -> {
         TrinketEffect<?> trinketEffect = TrinketEffectRegistry.getEffect(key);
         if (trinketEffect != null) {
            list.add(trinketEffect, config.getWeight());
         }
      });
      return list.getRandom().orElse(null);
   }

   @Override
   protected void reset() {
      this.TRINKETS.clear();

      for (TrinketEffect<?> effect : TrinketEffectRegistry.getOrderedEntries()) {
         TrinketConfig.Trinket trinket = new TrinketConfig.Trinket(100, effect.getRegistryName().getPath(), "Trinket Text", 6084886);
         trinket.trinketConfig = effect.getDefaultConfig();
         this.TRINKETS.put(effect.getRegistryName(), trinket);
      }
   }

   public static class Trinket {
      @Expose
      private int weight;
      @Expose
      private String name;
      @Expose
      private String effectText;
      @Expose
      private int color;
      @Expose
      private int minUses;
      @Expose
      private int maxUses;
      @Expose
      private int minCraftedUses;
      @Expose
      private int maxCraftedUses;
      private Object trinketConfig;

      public Trinket(int weight, String name, String effectText, int color) {
         this(weight, name, effectText, color, 6, 9);
      }

      public Trinket(int weight, String name, String effectText, int color, int minUses, int maxUses) {
         this.weight = weight;
         this.name = name;
         this.effectText = effectText;
         this.color = color;
         this.minUses = minUses;
         this.maxUses = maxUses;
         this.minCraftedUses = minUses;
         this.maxCraftedUses = maxUses;
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

      public TextColor getComponentColor() {
         return TextColor.fromRgb(this.getColor());
      }

      public int getRandomUses() {
         return Mth.randomBetweenInclusive(Config.rand, this.minUses, this.maxUses);
      }

      public int getRandomCraftedUses() {
         return Mth.randomBetweenInclusive(Config.rand, this.minCraftedUses, this.maxCraftedUses);
      }

      public Object getConfig() {
         return this.trinketConfig;
      }
   }

   public static class TrinketMap extends LinkedHashMap<ResourceLocation, TrinketConfig.Trinket> {
      public static class Serializer implements JsonSerializer<TrinketConfig.TrinketMap>, JsonDeserializer<TrinketConfig.TrinketMap> {
         public TrinketConfig.TrinketMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            TrinketConfig.TrinketMap trinketMap = new TrinketConfig.TrinketMap();
            JsonObject map = json.getAsJsonObject();

            for (String trinketKeyStr : map.keySet()) {
               ResourceLocation trinketKey = new ResourceLocation(trinketKeyStr);
               TrinketEffect<?> trinketEffect = TrinketEffectRegistry.getEffect(trinketKey);
               if (trinketEffect != null) {
                  JsonObject trinketConfigObject = map.getAsJsonObject(trinketKeyStr);
                  TrinketConfig.Trinket trinketConfig = (TrinketConfig.Trinket)context.deserialize(trinketConfigObject, TrinketConfig.Trinket.class);
                  trinketConfig.trinketConfig = context.deserialize(trinketConfigObject.get("config"), trinketEffect.getConfigClass());
                  trinketMap.put(trinketKey, trinketConfig);
               }
            }

            return trinketMap;
         }

         public JsonElement serialize(TrinketConfig.TrinketMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject map = new JsonObject();
            src.forEach((trinketKey, trinketConfig) -> {
               TrinketEffect<?> trinketEffect = TrinketEffectRegistry.getEffect(trinketKey);
               if (trinketEffect != null) {
                  JsonObject trinketConfigObject = context.serialize(trinketConfig).getAsJsonObject();
                  trinketConfigObject.add("config", context.serialize(trinketConfig.trinketConfig, trinketEffect.getConfigClass()));
                  map.add(trinketKey.toString(), trinketConfigObject);
               }
            });
            return map;
         }
      }
   }
}
