package iskallia.vault.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.gear.charm.AttributeCharm;
import iskallia.vault.gear.charm.CharmEffect;
import iskallia.vault.gear.charm.CharmEffectRegistry;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public class CharmConfig extends Config {
   @Expose
   public CharmConfig.CharmMap CHARMS = new CharmConfig.CharmMap();
   public static final List<ResourceLocation> LOC = List.of(
      VaultMod.id("particle/charm/small_idona"),
      VaultMod.id("particle/charm/large_idona"),
      VaultMod.id("particle/charm/grand_idona"),
      VaultMod.id("particle/charm/majestic_idona"),
      VaultMod.id("particle/charm/small_tenos"),
      VaultMod.id("particle/charm/large_tenos"),
      VaultMod.id("particle/charm/grand_tenos"),
      VaultMod.id("particle/charm/majestic_tenos"),
      VaultMod.id("particle/charm/small_wendarr"),
      VaultMod.id("particle/charm/large_wendarr"),
      VaultMod.id("particle/charm/grand_wendarr"),
      VaultMod.id("particle/charm/majestic_wendarr"),
      VaultMod.id("particle/charm/small_velara"),
      VaultMod.id("particle/charm/large_velara"),
      VaultMod.id("particle/charm/grand_velara"),
      VaultMod.id("particle/charm/majestic_velara")
   );

   @Override
   public String getName() {
      return "charm";
   }

   @Nullable
   public CharmConfig.Charm getCharmConfig(CharmEffect<?> charmEffect) {
      return this.getTrinketConfig(charmEffect.getRegistryName());
   }

   @Nullable
   private CharmConfig.Charm getTrinketConfig(ResourceLocation key) {
      return this.CHARMS.get(key);
   }

   @Nullable
   public CharmEffect<?> getRandomTrinketSet(CharmConfig.Size size) {
      return this.getRandomTrinketSet((charmEffect, weight) -> weight, size);
   }

   @Nullable
   public CharmEffect<?> getRandomTrinketSet(BiFunction<CharmEffect<?>, Integer, Integer> weightAdjustFn, CharmConfig.Size size) {
      WeightedList<CharmEffect<?>> list = new WeightedList<>();
      this.CHARMS.forEach((key, config) -> {
         CharmEffect<?> charmEffect = CharmEffectRegistry.getEffect(key);
         if (charmEffect instanceof AttributeCharm<?> attributeCharm && attributeCharm.getSize() == size) {
            list.add(charmEffect, weightAdjustFn.apply(charmEffect, config.getWeight()));
         }
      });
      return list.getRandom().orElse(null);
   }

   public Set<ResourceLocation> getTrinketIds() {
      return this.CHARMS.keySet();
   }

   @Override
   protected void reset() {
      this.CHARMS.clear();

      for (CharmEffect<?> effect : CharmEffectRegistry.getOrderedEntries()) {
         CharmConfig.Charm charm = new CharmConfig.Charm(
            100,
            VaultGod.VELARA,
            effect.getRegistryName().getPath(),
            "textures/particle/charm/" + effect.getRegistryName().getPath() + ".png",
            16733525,
            16733525
         );
         charm.charmConfig = effect.getDefaultConfig();
         this.CHARMS.put(effect.getRegistryName(), charm);
      }
   }

   public static class Charm {
      @Expose
      private int weight;
      @Expose
      private VaultGod god;
      @Expose
      private String name;
      @Expose
      private String particleLoc;
      @Expose
      private int color;
      @Expose
      private int majesticColor;
      @Expose
      private int minUses;
      @Expose
      private int maxUses;
      @Expose
      private int minAffinity;
      @Expose
      private int maxAffinity;
      private Object charmConfig;

      public Charm(int weight, VaultGod god, String name, String particleLoc, int color, int majesticColor) {
         this(weight, god, name, particleLoc, color, majesticColor, 6, 9, 0, 1);
      }

      public Charm(
         int weight, VaultGod god, String name, String particleLoc, int color, int majesticColor, int minUses, int maxUses, int minAffinity, int maxAffinity
      ) {
         this.weight = weight;
         this.god = god;
         this.name = name;
         this.particleLoc = particleLoc;
         this.color = color;
         this.majesticColor = majesticColor;
         this.minUses = minUses;
         this.maxUses = maxUses;
         this.minAffinity = minAffinity;
         this.maxAffinity = maxAffinity;
      }

      public int getWeight() {
         return this.weight;
      }

      public VaultGod getGod() {
         return this.god;
      }

      public String getName() {
         return this.name;
      }

      public String getParticleLoc() {
         return this.particleLoc;
      }

      public int getColor() {
         return this.color;
      }

      public int getMajesticColor() {
         return this.majesticColor;
      }

      public TextColor getComponentColor() {
         return TextColor.fromRgb(this.getColor());
      }

      public int getRandomUses() {
         return Mth.randomBetweenInclusive(Config.rand, this.minUses, this.maxUses);
      }

      public int getRandomAffinity() {
         return Mth.randomBetweenInclusive(Config.rand, this.minAffinity, this.maxAffinity);
      }

      public Object getConfig() {
         return this.charmConfig;
      }
   }

   public static class CharmMap extends LinkedHashMap<ResourceLocation, CharmConfig.Charm> {
      public static class Serializer implements JsonSerializer<CharmConfig.CharmMap>, JsonDeserializer<CharmConfig.CharmMap> {
         public CharmConfig.CharmMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CharmConfig.CharmMap trinketMap = new CharmConfig.CharmMap();
            JsonObject map = json.getAsJsonObject();

            for (String trinketKeyStr : map.keySet()) {
               ResourceLocation trinketKey = new ResourceLocation(trinketKeyStr);
               CharmEffect<?> trinketEffect = CharmEffectRegistry.getEffect(trinketKey);
               if (trinketEffect != null) {
                  JsonObject trinketConfigObject = map.getAsJsonObject(trinketKeyStr);
                  CharmConfig.Charm trinketConfig = (CharmConfig.Charm)context.deserialize(trinketConfigObject, CharmConfig.Charm.class);
                  trinketConfig.charmConfig = context.deserialize(trinketConfigObject.get("config"), trinketEffect.getConfigClass());
                  trinketMap.put(trinketKey, trinketConfig);
               }
            }

            return trinketMap;
         }

         public JsonElement serialize(CharmConfig.CharmMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject map = new JsonObject();
            src.forEach((charmKey, charmConfig) -> {
               CharmEffect<?> charmEffect = CharmEffectRegistry.getEffect(charmKey);
               if (charmEffect != null) {
                  JsonObject charmConfigObject = context.serialize(charmConfig).getAsJsonObject();
                  charmConfigObject.add("config", context.serialize(charmConfig.charmConfig, charmEffect.getConfigClass()));
                  map.add(charmKey.toString(), charmConfigObject);
               }
            });
            return map;
         }
      }
   }

   public static enum Size implements StringRepresentable {
      SMALL,
      LARGE,
      GRAND,
      MAJESTIC;

      public String getSerializedName() {
         return this.name();
      }
   }
}
