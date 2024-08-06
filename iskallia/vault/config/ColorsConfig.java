package iskallia.vault.config;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.network.chat.TextColor;

public class ColorsConfig extends Config {
   private static final TextColor MISSING_COLOR = TextColor.fromRgb(16711850);
   @Expose(
      deserialize = false
   )
   private final List<String> COMMENTS = List.of(
      "// - builtin values may be edited, but not removed",
      "// - add / remove custom values inside the custom object",
      "// - custom values override builtin values"
   );
   public static final String TEXT = "text";
   public static final String NAME = "name";
   public static final String COOLDOWN = "cooldown";
   public static final String MANA_COST = "manaCost";
   public static final String DISTANCE = "distance";
   public static final String DAMAGE = "damage";
   public static final String ABILITY_POWER = "ability_power";
   public static final String DAMAGE_REDUCTION = "damageReduction";
   public static final String LEVEL_HI = "levelHi";
   public static final String LEVEL_LO = "levelLo";
   public static final String DURATION = "duration";
   public static final String DELAY = "delay";
   public static final String RANGE = "range";
   public static final String CHANCE = "chance";
   public static final String HEAL = "heal";
   public static final String RADIUS = "radius";
   public static final String ABSORB = "absorb";
   public static final String MANA_PER_DAMAGE = "manaPerDamage";
   public static final String KNOCKBACK = "knockback";
   public static final String SLOWNESS = "slowness";
   public static final String CHILLED = "chilled";
   public static final String CHAINS = "chains";
   public static final String LEECH = "leech";
   public static final String ETERNALS = "eternals";
   public static final String RESISTANCE = "resistance";
   public static final String SPEED = "speed";
   public static final String PROJECTILE_DAMAGE_TAKEN = "projectileDamageTaken";
   public static final String BLOCKS = "blocks";
   public static final String UNBREAKING = "unbreaking";
   public static final String FORTUNE = "fortune";
   public static final String MAX_TARGETS = "maxTargets";
   public static final String FREEZE_DURATION = "freezeDuration";
   public static final String DAMAGE_INTERVAL = "damageInterval";
   public static final String PIERCING = "piercing";
   public static final String NUMBER_OF_BOUNCES = "numberOfBounces";
   public static final String NUMBER_OF_JAVELINS = "numberOfJavelins";
   public static final String THROW_POWER = "throwPower";
   public static final String AMPLIFIER = "amplifier";
   public static final String MANA_PER_BOLT = "additionalManaPerBolt";
   public static final String FORCE = "force";
   public static final String STORM_INTERVAL = "stormInterval";
   public static final String STACKS_USED_PER_HIT = "stacksUsedPerHit";
   public static final String MAX_STACKS = "maxStacks";
   public static final String LUCKY_HIT = "luckyHit";
   public static final String MAX_GLACIAL_PRISON = "maxGlacialPrison";
   public static final String GLACIAL_CHANCE = "glacialChance";
   @Expose
   private final ColorsConfig.Colors colors = new ColorsConfig.Colors();

   @Override
   public String getName() {
      return "colors";
   }

   @Override
   protected void reset() {
      this.colors.builtin.clear();
      this.colors.custom.clear();
      this.colors.builtin.put("text", TextColor.fromRgb(1646626));
      this.colors.builtin.put("name", TextColor.fromRgb(16577989));
      this.colors.builtin.put("cooldown", TextColor.fromRgb(255));
      this.colors.builtin.put("manaCost", TextColor.fromRgb(65535));
      this.colors.builtin.put("distance", TextColor.fromRgb(15918725));
      this.colors.builtin.put("ability_power", TextColor.fromRgb(16711883));
      this.colors.builtin.put("damage", TextColor.fromRgb(12727847));
      this.colors.builtin.put("damageReduction", TextColor.fromRgb(12727847));
      this.colors.builtin.put("levelHi", TextColor.fromRgb(65280));
      this.colors.builtin.put("levelLo", TextColor.fromRgb(12727847));
      this.colors.builtin.put("duration", TextColor.fromRgb(16561527));
      this.colors.builtin.put("delay", TextColor.fromRgb(9193750));
      this.colors.builtin.put("range", TextColor.fromRgb(883257));
      this.colors.builtin.put("chance", TextColor.fromRgb(10441724));
      this.colors.builtin.put("heal", TextColor.fromRgb(8254855));
      this.colors.builtin.put("radius", TextColor.fromRgb(883257));
      this.colors.builtin.put("absorb", TextColor.fromRgb(9367001));
      this.colors.builtin.put("manaPerDamage", TextColor.fromRgb(65535));
      this.colors.builtin.put("knockback", TextColor.fromRgb(10033226));
      this.colors.builtin.put("slowness", TextColor.fromRgb(2837801));
      this.colors.builtin.put("chilled", TextColor.fromRgb(5286063));
      this.colors.builtin.put("chains", TextColor.fromRgb(2837801));
      this.colors.builtin.put("leech", TextColor.fromRgb(16739950));
      this.colors.builtin.put("eternals", TextColor.fromRgb(13107175));
      this.colors.builtin.put("resistance", TextColor.fromRgb(13487988));
      this.colors.builtin.put("speed", TextColor.fromRgb(13487988));
      this.colors.builtin.put("projectileDamageTaken", TextColor.fromRgb(13611380));
      this.colors.builtin.put("blocks", TextColor.fromRgb(9723192));
      this.colors.builtin.put("unbreaking", TextColor.fromRgb(3355443));
      this.colors.builtin.put("fortune", TextColor.fromRgb(6692771));
      this.colors.builtin.put("maxTargets", TextColor.fromRgb(13435035));
      this.colors.builtin.put("freezeDuration", TextColor.fromRgb(255));
      this.colors.builtin.put("damageInterval", TextColor.fromRgb(11035173));
      this.colors.builtin.put("piercing", TextColor.fromRgb(16738816));
      this.colors.builtin.put("numberOfBounces", TextColor.fromRgb(16749492));
      this.colors.builtin.put("numberOfJavelins", TextColor.fromRgb(14942082));
      this.colors.builtin.put("throwPower", TextColor.fromRgb(16743168));
      this.colors.builtin.put("amplifier", TextColor.fromRgb(16777118));
      this.colors.builtin.put("additionalManaPerBolt", TextColor.fromRgb(65535));
      this.colors.builtin.put("force", TextColor.fromRgb(15918725));
      this.colors.builtin.put("stormInterval", TextColor.fromRgb(15910513));
      this.colors.builtin.put("maxStacks", TextColor.fromRgb(52084));
      this.colors.builtin.put("stacksUsedPerHit", TextColor.fromRgb(13748480));
      this.colors.builtin.put("luckyHit", TextColor.fromRgb(1308067));
      this.colors.builtin.put("maxGlacialPrison", TextColor.fromRgb(4703456));
      this.colors.builtin.put("glacialChance", TextColor.fromRgb(8973822));
   }

   public TextColor getColor(String key) {
      return this.colors.get(key);
   }

   public void replaceColorStrings(JsonElement element) {
      this.replaceColorStrings(element, this.colors);
   }

   private TextColor replaceColorStrings(JsonElement element, ColorsConfig.Colors colors) {
      if (element.isJsonObject()) {
         JsonObject object = element.getAsJsonObject();

         for (String key : Lists.newArrayList(object.keySet())) {
            TextColor result;
            if ((result = this.replaceColorStrings(object.get(key), colors)) != null) {
               object.remove(key);
               object.add(key, new JsonPrimitive(result.toString()));
            }
         }
      } else if (element.isJsonArray()) {
         JsonArray array = element.getAsJsonArray();

         for (int i = 0; i < array.size(); i++) {
            TextColor result;
            if ((result = this.replaceColorStrings(array.get(i), colors)) != null) {
               array.set(i, new JsonPrimitive(result.toString()));
            }
         }
      } else if (element.isJsonPrimitive()) {
         JsonPrimitive primitive = element.getAsJsonPrimitive();
         if (primitive.isString()) {
            String string = primitive.getAsString();
            if (string.startsWith("$")) {
               return colors.get(string.substring(1));
            }
         }
      }

      return null;
   }

   public static class Colors {
      @Expose
      private final TreeMap<String, TextColor> builtin = new TreeMap<>();
      @Expose
      private final TreeMap<String, TextColor> custom = new TreeMap<>();

      public TextColor get(String key) {
         return this.custom.containsKey(key) ? this.custom.get(key) : this.builtin.getOrDefault(key, ColorsConfig.MISSING_COLOR);
      }
   }
}
