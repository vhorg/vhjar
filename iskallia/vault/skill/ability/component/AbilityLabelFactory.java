package iskallia.vault.skill.ability.component;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public final class AbilityLabelFactory {
   public static final String COOLDOWN = "cooldown";
   public static final String MANA_COST = "manaCost";
   public static final String MANA_COST_PER_SECOND = "manaCostPerSecond";
   public static final String LEVEL = "level";
   public static final String DISTANCE = "distance";
   public static final String DAMAGE = "damage";
   public static final String DURATION = "duration";
   public static final String DELAY = "delay";
   public static final String RANGE_HORIZONTAL = "rangeHorizontal";
   public static final String RANGE_VERTICAL = "rangeVertical";
   public static final String CHANCE = "chance";
   public static final String HEAL = "heal";
   public static final String RADIUS = "radius";
   public static final String ABSORB = "absorb";
   public static final String MANA_PER_DAMAGE = "manaPerDamage";
   public static final String KNOCKBACK = "knockback";
   public static final String SLOWNESS = "slowness";
   public static final String CHAINS = "chains";
   public static final String LEECH = "leech";
   public static final String ETERNALS = "eternals";
   public static final String ANCIENT_CHANCE = "ancientChance";
   public static final String RESISTANCE = "resistance";
   public static final String RESISTANCE_KNOCKBACK = "resistanceKnockback";
   public static final String REDUCED_PROJECTILE_DAMAGE_TAKEN = "reducedProjectileDamageTaken";
   public static final String BLOCKS = "blocks";
   public static final String UNBREAKING = "unbreaking";
   public static final String FORTUNE = "fortune";
   private static final Map<String, AbilityLabelFactory.IAbilityComponentFactory> FACTORY_MAP = new HashMap<String, AbilityLabelFactory.IAbilityComponentFactory>() {
      {
         this.put("cooldown", context -> AbilityLabelFactory.label("\n Cooldown: ", AbilityLabelFactory.binding(context.config(), "cooldown"), "cooldown"));
         this.put("manaCost", context -> AbilityLabelFactory.label("\n Mana Cost: ", AbilityLabelFactory.binding(context.config(), "manaCost"), "manaCost"));
         this.put(
            "manaCostPerSecond", context -> AbilityLabelFactory.label("\n Mana / Sec: ", AbilityLabelFactory.binding(context.config(), "manaCost"), "manaCost")
         );
         this.put("level", context -> {
            String levelString = AbilityLabelFactory.binding(context.config(), "level");
            return AbilityLabelFactory.label("\n Min Level: ", levelString, context.vaultLevel() < Integer.parseInt(levelString) ? "levelLo" : "levelHi");
         });
         this.put("distance", context -> AbilityLabelFactory.label("\n Distance: ", AbilityLabelFactory.binding(context.config(), "distance"), "distance"));
         this.put("damage", context -> AbilityLabelFactory.label("\n Damage: ", AbilityLabelFactory.binding(context.config(), "damage"), "damage"));
         this.put("duration", context -> AbilityLabelFactory.label("\n Duration: ", AbilityLabelFactory.binding(context.config(), "duration"), "duration"));
         this.put("delay", context -> AbilityLabelFactory.label("\n Delay: ", AbilityLabelFactory.binding(context.config(), "delay"), "delay"));
         this.put(
            "rangeHorizontal", context -> AbilityLabelFactory.label("\n XZ Range: ", AbilityLabelFactory.binding(context.config(), "rangeHorizontal"), "range")
         );
         this.put(
            "rangeVertical", context -> AbilityLabelFactory.label("\n Y Range: ", AbilityLabelFactory.binding(context.config(), "rangeVertical"), "range")
         );
         this.put("chance", context -> AbilityLabelFactory.label("\n Chance: ", AbilityLabelFactory.binding(context.config(), "chance"), "chance"));
         this.put("heal", context -> AbilityLabelFactory.label("\n Heal: ", AbilityLabelFactory.binding(context.config(), "heal"), "heal"));
         this.put("radius", context -> AbilityLabelFactory.label("\n Radius: ", AbilityLabelFactory.binding(context.config(), "radius"), "radius"));
         this.put("absorb", context -> AbilityLabelFactory.label("\n Absorb: ", AbilityLabelFactory.binding(context.config(), "absorb"), "absorb"));
         this.put(
            "manaPerDamage",
            context -> AbilityLabelFactory.label("\n Mana / Damage: ", AbilityLabelFactory.binding(context.config(), "manaPerDamage"), "manaPerDamage")
         );
         this.put("knockback", context -> AbilityLabelFactory.label("\n Knockback: ", AbilityLabelFactory.binding(context.config(), "knockback"), "knockback"));
         this.put("slowness", context -> AbilityLabelFactory.label("\n Slowness: ", AbilityLabelFactory.binding(context.config(), "slowness"), "slowness"));
         this.put("chains", context -> AbilityLabelFactory.label("\n Chains: ", AbilityLabelFactory.binding(context.config(), "chains"), "chains"));
         this.put("leech", context -> AbilityLabelFactory.label("\n Leech: ", AbilityLabelFactory.binding(context.config(), "leech"), "leech"));
         this.put("eternals", context -> AbilityLabelFactory.label("\n Eternals: ", AbilityLabelFactory.binding(context.config(), "eternals"), "eternals"));
         this.put(
            "ancientChance", context -> AbilityLabelFactory.label("\n Ancient Chance: ", AbilityLabelFactory.binding(context.config(), "chance"), "chance")
         );
         this.put(
            "resistance", context -> AbilityLabelFactory.label("\n Resistance: ", AbilityLabelFactory.binding(context.config(), "resistance"), "resistance")
         );
         this.put(
            "resistanceKnockback",
            context -> AbilityLabelFactory.label("\n Knockback Res.: ", AbilityLabelFactory.binding(context.config(), "resistance"), "resistance")
         );
         this.put(
            "reducedProjectileDamageTaken",
            context -> AbilityLabelFactory.label(
               "\n Projectile Resistance: ", AbilityLabelFactory.binding(context.config(), "projectileDamageTaken"), "projectileDamageTaken"
            )
         );
         this.put("blocks", context -> AbilityLabelFactory.label("\n Blocks: ", AbilityLabelFactory.binding(context.config(), "blocks"), "blocks"));
         this.put(
            "unbreaking", context -> AbilityLabelFactory.label("\n Unbreaking: ", AbilityLabelFactory.binding(context.config(), "unbreaking"), "unbreaking")
         );
         this.put("fortune", context -> AbilityLabelFactory.label("\n Fortune: ", AbilityLabelFactory.binding(context.config(), "fortune"), "fortune"));
      }
   };

   public static MutableComponent create(String key, AbilityLabelContext<?> context) {
      AbilityLabelFactory.IAbilityComponentFactory factory = FACTORY_MAP.get(key);
      return (MutableComponent)(factory == null ? new TextComponent("\n MISSING: " + key) : factory.create(context));
   }

   private static <C extends AbstractAbilityConfig> String binding(C config, String key) {
      return AbilityLabelBindingRegistry.getBindingValue(config, key);
   }

   private static MutableComponent label(String label, String value, String colorKey) {
      return new TextComponent(label).withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("text"))).append(text(value, colorKey));
   }

   private static MutableComponent text(String text, String colorKey) {
      return text(text, ModConfigs.COLORS.getColor(colorKey));
   }

   private static MutableComponent text(String text, TextColor color) {
      return new TextComponent(text).withStyle(Style.EMPTY.withColor(color));
   }

   private AbilityLabelFactory() {
   }

   public interface IAbilityComponentFactory {
      MutableComponent create(AbilityLabelContext<?> var1);
   }
}
