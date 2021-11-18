package iskallia.vault.dump;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GearAttributeDump extends JsonDump {
   @Override
   public String fileName() {
      return "gear_attributes.json";
   }

   @Override
   public JsonObject dumpToJSON() {
      JsonObject jsonObject = new JsonObject();
      JsonObject attributes = new JsonObject();
      this.addTooltip(
         attributes,
         ModAttributes.GEAR_CRAFTED_BY,
         GearAttributeDump.PossibleValues.stringType(),
         GearAttributeDump.TooltipFragment.of("Crafted by ${value}").color(16770048)
      );
      this.addTooltip(
         attributes,
         ModAttributes.GEAR_TIER,
         GearAttributeDump.PossibleValues.enumType(1, 2),
         GearAttributeDump.TooltipFragment.of("Tier: "),
         GearAttributeDump.TooltipFragment.of("${value}").color(9556190)
      );
      this.addTooltip(
         attributes,
         ModAttributes.GEAR_RARITY,
         GearAttributeDump.PossibleValues.enumType(VaultGear.Rarity.class),
         GearAttributeDump.TooltipFragment.of("Rarity: "),
         GearAttributeDump.TooltipFragment.of("${value}").color(9556190)
      );
      this.addTooltip(
         attributes,
         ModAttributes.GEAR_SET,
         GearAttributeDump.PossibleValues.enumType(VaultGear.Set.class),
         GearAttributeDump.TooltipFragment.of("Etching: "),
         GearAttributeDump.TooltipFragment.of("${value}").color(11184810)
      );
      this.addTooltip(
         attributes,
         ModAttributes.MAX_REPAIRS,
         GearAttributeDump.PossibleValues.integerType(),
         GearAttributeDump.TooltipFragment.of("Repairs: "),
         GearAttributeDump.TooltipFragment.of("0 / ${value}").color(16777045)
      );
      this.addTooltip(
         attributes,
         ModAttributes.GEAR_MAX_LEVEL,
         GearAttributeDump.PossibleValues.integerType(),
         GearAttributeDump.TooltipFragment.of("Levels: "),
         GearAttributeDump.TooltipFragment.of("0 / ${value}").color(16777045)
      );
      JsonObject modifiers = new JsonObject();
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_ARMOR,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value} Armor").color(4766456)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_ARMOR_TOUGHNESS,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value} Armor Toughness").color(13302672)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.THORNS_CHANCE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Thorns Chance").color(7195648)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.THORNS_DAMAGE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Thorns Damage").color(3646976)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_KNOCKBACK_RESISTANCE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Knockback Resistance").color(16756751)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_ATTACK_DAMAGE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value} Attack Damage").color(13116966)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_ATTACK_SPEED,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value} Attack Speed").color(16767592)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_DURABILITY,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value} Durability").color(14668030)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_PLATING,
         GearAttributeDump.PossibleValues.integerType(),
         GearAttributeDump.TooltipFragment.of("+${value} Plating").color(14668030)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_PLATING,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value} Reach").color(8706047)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_FEATHER_FEET,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Feather Feet").color(13499899)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_MIN_VAULT_LEVEL,
         GearAttributeDump.PossibleValues.integerType(),
         GearAttributeDump.TooltipFragment.of("+${value} Min Vault Level").color(15523772)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.ADD_COOLDOWN_REDUCTION,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Cooldown Reduction").color(63668)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.EXTRA_LEECH_RATIO,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Leech").color(16716820)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.FATAL_STRIKE_CHANCE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Fatal Strike Chance").color(16523264)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.FATAL_STRIKE_DAMAGE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Fatal Strike Damage").color(12520704)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.EXTRA_HEALTH,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value} Health").color(2293541)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.EXTRA_PARRY_CHANCE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Parry").color(11534098)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.EXTRA_RESISTANCE,
         GearAttributeDump.PossibleValues.numberType(),
         GearAttributeDump.TooltipFragment.of("+${value}% Resistance").color(16702720)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.EFFECT_IMMUNITY,
         GearAttributeDump.PossibleValues.enumType("Poison", "Wither", "Hunger", "Mining Fatigue", "Slowness", "Weakness"),
         GearAttributeDump.TooltipFragment.of("+${value} Immunity").color(10801083)
      );
      this.addTooltip(
         modifiers,
         ModAttributes.EFFECT_CLOUD,
         GearAttributeDump.PossibleValues.enumType("Poison", "Wither", "Hunger", "Mining Fatigue", "Slowness", "Weakness"),
         GearAttributeDump.TooltipFragment.of("+${value} Cloud").color(15007916)
      );
      this.addTooltip(
         modifiers, ModAttributes.SOULBOUND, GearAttributeDump.PossibleValues.noneType(), GearAttributeDump.TooltipFragment.of("Soulbound").color(9856253)
      );
      jsonObject.add("attributes", attributes);
      jsonObject.add("modifiers", modifiers);
      return jsonObject;
   }

   private void addTooltip(
      JsonObject json, VAttribute<?, ?> attribute, GearAttributeDump.PossibleValues possibleValues, GearAttributeDump.TooltipFragment... fragments
   ) {
      JsonObject tooltipJson = new JsonObject();
      String attributeName = Arrays.stream(attribute.getId().func_110623_a().replaceAll("_", " ").split("\\s+"))
         .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
         .collect(Collectors.joining(" "));
      tooltipJson.addProperty("name", attributeName);
      JsonArray format = new JsonArray();

      for (GearAttributeDump.TooltipFragment fragment : fragments) {
         JsonObject fragmentJson = new JsonObject();
         fragmentJson.addProperty("text", fragment.text);
         fragmentJson.addProperty("color", fragment.color);
         if (fragment.bold) {
            fragmentJson.addProperty("bold", true);
         }

         if (fragment.italic) {
            fragmentJson.addProperty("italic", true);
         }

         if (fragment.underline) {
            fragmentJson.addProperty("underline", true);
         }

         format.add(fragmentJson);
      }

      tooltipJson.add("format", format);
      JsonObject possibleValuesJson = new JsonObject();
      possibleValuesJson.addProperty("type", possibleValues.type);
      if (possibleValues.values != null) {
         possibleValuesJson.add("values", possibleValues.valuesAsJson());
      }

      tooltipJson.add("possibleValues", possibleValuesJson);
      json.add(attribute.getId().func_110623_a(), tooltipJson);
   }

   public static class PossibleValues {
      String type;
      Object[] values;

      private static GearAttributeDump.PossibleValues type(String type) {
         GearAttributeDump.PossibleValues possibleValues = new GearAttributeDump.PossibleValues();
         possibleValues.type = type;
         return possibleValues;
      }

      private static GearAttributeDump.PossibleValues noneType() {
         return type("none");
      }

      private static GearAttributeDump.PossibleValues stringType() {
         return type("string");
      }

      private static GearAttributeDump.PossibleValues integerType() {
         return type("integer");
      }

      private static GearAttributeDump.PossibleValues numberType() {
         return type("number");
      }

      private static GearAttributeDump.PossibleValues booleanType() {
         return type("boolean");
      }

      public static <T extends Enum<?>> GearAttributeDump.PossibleValues enumType(Class<T> enumClass) {
         return enumType(enumNames(enumClass));
      }

      public static GearAttributeDump.PossibleValues enumType(Object... values) {
         GearAttributeDump.PossibleValues possibleValues = new GearAttributeDump.PossibleValues();
         possibleValues.type = "enum";
         possibleValues.values = values;
         return possibleValues;
      }

      public GearAttributeDump.PossibleValues values(Object... values) {
         this.values = values;
         return this;
      }

      public JsonArray valuesAsJson() {
         JsonArray valuesJson = new JsonArray();

         for (Object value : this.values) {
            valuesJson.add(value.toString());
         }

         return valuesJson;
      }

      private static <T extends Enum<?>> Object[] enumNames(Class<T> enumClass) {
         Enum<?>[] enumConstants = enumClass.getEnumConstants();
         List<String> names = new LinkedList<>();

         for (Enum<?> enumConstant : enumConstants) {
            String enumName = enumConstant.name();
            String normalizedName = Arrays.stream(enumName.replaceAll("_", " ").split("\\s+"))
               .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
               .collect(Collectors.joining(" "));
            names.add(normalizedName);
         }

         return names.toArray();
      }
   }

   public static class TooltipFragment {
      String text;
      int color;
      boolean bold;
      boolean italic;
      boolean underline;

      public static GearAttributeDump.TooltipFragment of(String text) {
         GearAttributeDump.TooltipFragment fragment = new GearAttributeDump.TooltipFragment();
         fragment.text = text;
         fragment.color = 16777215;
         return fragment;
      }

      public GearAttributeDump.TooltipFragment color(int color) {
         this.color = color;
         return this;
      }

      public GearAttributeDump.TooltipFragment bold() {
         this.bold = true;
         return this;
      }

      public GearAttributeDump.TooltipFragment italic() {
         this.italic = true;
         return this;
      }

      public GearAttributeDump.TooltipFragment underline() {
         this.underline = true;
         return this;
      }
   }
}
