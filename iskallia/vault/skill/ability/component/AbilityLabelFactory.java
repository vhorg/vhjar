package iskallia.vault.skill.ability.component;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
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
   public static final String SPEED = "speed";
   public static final String DURABILITY_WEAR_REDUCTION = "durabilityWearReduction";
   public static final String RESISTANCE_KNOCKBACK = "resistanceKnockback";
   public static final String REDUCED_PROJECTILE_DAMAGE_TAKEN = "reducedProjectileDamageTaken";
   public static final String BLOCKS = "blocks";
   public static final String UNBREAKING = "unbreaking";
   public static final String FORTUNE = "fortune";
   public static final String MANA_REGEN = "manaRegen";
   public static final String MAX_TARGETS = "maxTargets";
   public static final String DAMAGE_REDUCTION = "damageReduction";
   public static final String FREEZE_DURATION = "freezeDuration";
   public static final String DAMAGE_INCREASE = "damageIncrease";
   public static final String DAMAGE_INTERVAL = "damageInterval";
   public static final String PIERCING = "piercing";
   public static final String NUMBER_OF_BOUNCES = "numberOfBounces";
   public static final String NUMBER_OF_JAVELINS = "numberOfJavelins";
   public static final String THROW_POWER = "throwPower";
   public static final String AMPLIFIER = "amplifier";
   public static final String MANA_PER_BOLT = "additionalManaPerBolt";
   public static final String FORCE = "force";
   public static final String MANA_PER_HIT = "additionalManaPerHit";
   public static final String STUN_CHANCE = "stunChance";
   public static final String STUN_DURATION = "stunDuration";
   public static final String STUN_AMPLIFIER = "stunAmplifier";
   public static final String QUILL_COUNT = "quillCount";
   private static final Map<String, AbilityLabelFactory.IAbilityComponentFactory> FACTORY_MAP = new HashMap<String, AbilityLabelFactory.IAbilityComponentFactory>() {
      {
         this.put("cooldown", context -> AbilityLabelFactory.label("\n Cooldown: ", AbilityLabelFactory.binding(context.config(), "cooldown"), "cooldown"));
         this.put("manaCost", context -> AbilityLabelFactory.label("\n Mana Cost: ", AbilityLabelFactory.binding(context.config(), "manaCost"), "manaCost"));
         this.put(
            "manaCostPerSecond", context -> AbilityLabelFactory.label("\n Mana / Sec: ", AbilityLabelFactory.binding(context.config(), "manaCost"), "manaCost")
         );
         this.put("level", context -> {
            String levelString = AbilityLabelFactory.binding(context.config(), "level");

            try {
               return AbilityLabelFactory.label("\n Min Level: ", levelString, context.vaultLevel() < Integer.parseInt(levelString) ? "levelLo" : "levelHi");
            } catch (Exception var3) {
               var3.printStackTrace();
               return new TextComponent("ERROR");
            }
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
         this.put("speed", context -> AbilityLabelFactory.label("\n Speed: ", AbilityLabelFactory.binding(context.config(), "speed"), "speed"));
         this.put(
            "durabilityWearReduction",
            context -> AbilityLabelFactory.label("\n Unbreaking: ", AbilityLabelFactory.binding(context.config(), "durabilityWearReduction"), "unbreaking")
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
         this.put("manaRegen", context -> AbilityLabelFactory.label("\n Mana Regen: ", AbilityLabelFactory.binding(context.config(), "manaRegen"), "manaCost"));
         this.put(
            "maxTargets", context -> AbilityLabelFactory.label("\n Max Targets: ", AbilityLabelFactory.binding(context.config(), "maxTargets"), "maxTargets")
         );
         this.put(
            "damageReduction",
            context -> AbilityLabelFactory.label("\n Damage Reduction: ", AbilityLabelFactory.binding(context.config(), "damageReduction"), "damageReduction")
         );
         this.put(
            "freezeDuration",
            context -> AbilityLabelFactory.label("\n Freeze Duration: ", AbilityLabelFactory.binding(context.config(), "freezeDuration"), "freezeDuration")
         );
         this.put(
            "damageInterval",
            context -> AbilityLabelFactory.label("\n Damage Interval: ", AbilityLabelFactory.binding(context.config(), "damageInterval"), "damageInterval")
         );
         this.put(
            "damageIncrease",
            context -> AbilityLabelFactory.label("\n Damage Increased: ", AbilityLabelFactory.binding(context.config(), "damageIncrease"), "damage")
         );
         this.put("piercing", context -> AbilityLabelFactory.label("\n Piercing: ", AbilityLabelFactory.binding(context.config(), "piercing"), "piercing"));
         this.put(
            "numberOfBounces",
            context -> AbilityLabelFactory.label("\n Bounces: ", AbilityLabelFactory.binding(context.config(), "numberOfBounces"), "numberOfBounces")
         );
         this.put(
            "numberOfJavelins",
            context -> AbilityLabelFactory.label("\n Javelins: ", AbilityLabelFactory.binding(context.config(), "numberOfJavelins"), "numberOfJavelins")
         );
         this.put(
            "throwPower", context -> AbilityLabelFactory.label("\n Throw Power: ", AbilityLabelFactory.binding(context.config(), "throwPower"), "throwPower")
         );
         this.put("amplifier", context -> AbilityLabelFactory.label("\n Amplifier: ", AbilityLabelFactory.binding(context.config(), "amplifier"), "amplifier"));
         this.put(
            "additionalManaPerBolt",
            context -> AbilityLabelFactory.label("\n Mana Per Bolt: ", AbilityLabelFactory.binding(context.config(), "additionalManaPerBolt"), "manaCost")
         );
         this.put("force", context -> AbilityLabelFactory.label("\n Force: ", AbilityLabelFactory.binding(context.config(), "force"), "force"));
         this.put(
            "additionalManaPerHit",
            context -> AbilityLabelFactory.label("\n Mana Per Hit: ", AbilityLabelFactory.binding(context.config(), "additionalManaPerHit"), "manaCost")
         );
         this.put("stunChance", context -> AbilityLabelFactory.label("\n Stun Chance: ", AbilityLabelFactory.binding(context.config(), "stunChance"), "chance"));
         this.put(
            "stunDuration",
            context -> AbilityLabelFactory.label("\n Stun Duration: ", AbilityLabelFactory.binding(context.config(), "stunDuration"), "duration")
         );
         this.put(
            "stunAmplifier",
            context -> AbilityLabelFactory.label("\n Stun Amplifier: ", AbilityLabelFactory.binding(context.config(), "stunAmplifier"), "amplifier")
         );
         this.put("quillCount", context -> AbilityLabelFactory.label("\n Quill count: ", AbilityLabelFactory.binding(context.config(), "quillCount"), "chains"));
      }
   };

   public static MutableComponent create(String key, AbilityLabelContext<?> context) {
      AbilityLabelFactory.IAbilityComponentFactory factory = FACTORY_MAP.get(key);
      return (MutableComponent)(factory == null ? new TextComponent("\n MISSING: " + key) : factory.create(context));
   }

   private static <C extends Skill> String binding(C config, String key) {
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
