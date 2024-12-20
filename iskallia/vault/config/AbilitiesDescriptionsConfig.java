package iskallia.vault.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;

public class AbilitiesDescriptionsConfig extends Config {
   @Expose
   private TreeMap<String, AbilitiesDescriptionsConfig.DescriptionData> data;

   @Override
   public String getName() {
      return "abilities_descriptions";
   }

   @Override
   public <T extends Config> T readConfig() {
      AbilitiesDescriptionsConfig config = super.readConfig();
      config.data
         .values()
         .stream()
         .map(descriptionData -> descriptionData.description)
         .forEach(jsonElement -> ModConfigs.COLORS.replaceColorStrings(jsonElement));
      return (T)config;
   }

   public MutableComponent getDescriptionFor(String skillName) {
      AbilitiesDescriptionsConfig.DescriptionData data = this.data.get(skillName);
      return data == null
         ? Serializer.fromJsonLenient(
            "[{text:'No description for ', color:'#192022'},{text: '" + skillName + "', color: '#fcf5c5'},{text: ', yet', color: '#192022'}]"
         )
         : Serializer.fromJson(data.description);
   }

   public List<String> getCurrent(String skillName) {
      AbilitiesDescriptionsConfig.DescriptionData data = this.data.get(skillName);
      return data == null ? Collections.emptyList() : data.current;
   }

   public List<String> getNext(String skillName) {
      AbilitiesDescriptionsConfig.DescriptionData data = this.data.get(skillName);
      return data == null ? Collections.emptyList() : data.next;
   }

   @Override
   protected void reset() {
      this.data = new TreeMap<>();
      this.data
         .put(
            "Dash_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Dash_Base"), List.of("cooldown", "manaCost", "distance"), List.of("level", "cooldown", "manaCost", "distance")
            )
         );
      this.data
         .put(
            "Dash_Damage",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Dash_Damage"),
               List.of("cooldown", "manaCost", "distance", "damage"),
               List.of("level", "cooldown", "manaCost", "distance", "damage")
            )
         );
      this.data
         .put(
            "Dash_Warp",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Dash_Warp"), List.of("cooldown", "manaCost", "force"), List.of("level", "cooldown", "manaCost", "force")
            )
         );
      this.data
         .put(
            "Farmer_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Farmer_Base"),
               List.of("cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical"),
               List.of("level", "cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical")
            )
         );
      this.data
         .put(
            "Farmer_Cactus",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Farmer_Cactus"),
               List.of("cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical"),
               List.of("level", "cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical")
            )
         );
      this.data
         .put(
            "Farmer_Melon",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Farmer_Melon"),
               List.of("cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical"),
               List.of("level", "cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical")
            )
         );
      this.data
         .put(
            "Farmer_Animal",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Farmer_Animal"),
               List.of("cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical", "chance"),
               List.of("level", "cooldown", "manaCost", "delay", "rangeHorizontal", "rangeVertical", "chance")
            )
         );
      this.data
         .put(
            "Ghost_Walk_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Ghost_Walk_Base"), List.of("cooldown", "manaCost", "duration"), List.of("level", "cooldown", "manaCost", "duration")
            )
         );
      this.data
         .put(
            "Ghost Walk_Spirit_Walk",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Ghost Walk_Spirit_Walk"),
               List.of("cooldown", "manaCost", "duration"),
               List.of("level", "cooldown", "manaCost", "duration")
            )
         );
      this.data
         .put(
            "Heal_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Heal_Base"), List.of("cooldown", "manaCost", "heal"), List.of("level", "cooldown", "manaCost", "heal")
            )
         );
      this.data
         .put(
            "Heal_Cleanse",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Heal_Cleanse"), List.of("cooldown", "manaCost"), List.of("level", "cooldown", "manaCost")
            )
         );
      this.data
         .put(
            "Heal_Group",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Heal_Group"),
               List.of("cooldown", "manaCost", "heal", "radius"),
               List.of("level", "cooldown", "manaCost", "heal", "radius")
            )
         );
      this.data
         .put(
            "Hunter_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Hunter_Base"),
               List.of("cooldown", "manaCost", "duration", "radius"),
               List.of("level", "cooldown", "manaCost", "duration", "radius")
            )
         );
      this.data
         .put(
            "Mana_Shield_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Mana_Shield_Base"),
               List.of("cooldown", "manaCostPerSecond", "absorb"),
               List.of("level", "cooldown", "manaCostPerSecond", "absorb")
            )
         );
      this.data
         .put(
            "Implode_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Implode_Base"),
               List.of("cooldown", "manaCostPerSecond", "damage"),
               List.of("level", "cooldown", "manaCostPerSecond", "damage")
            )
         );
      this.data
         .put(
            "Mana_Shield_Cast_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Mana_Shield_Cast_Base"),
               List.of("cooldown", "manaCost", "duration", "healthPoints"),
               List.of("level", "cooldown", "manaCost", "duration", "healthPoints")
            )
         );
      this.data
         .put(
            "Retribution_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Retribution_Base"),
               List.of("cooldown", "manaCost", "distance", "damage"),
               List.of("level", "cooldown", "manaCost", "distance", "damage")
            )
         );
      this.data
         .put(
            "Implode",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Implode"),
               List.of("cooldown", "manaCost", "radius", "damage"),
               List.of("level", "cooldown", "manaCost", "radius", "damage")
            )
         );
      this.data
         .put(
            "Retribution",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Retribution"),
               List.of("cooldown", "manaCost", "distance", "damage"),
               List.of("level", "cooldown", "manaCost", "distance", "damage")
            )
         );
      this.data
         .put(
            "Mega_Jump_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Mega_Jump_Base"),
               List.of("cooldown", "manaCost", "rangeVertical"),
               List.of("level", "cooldown", "manaCost", "rangeVertical")
            )
         );
      this.data
         .put(
            "Mega Jump_Break_Up",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Mega Jump_Break_Up"),
               List.of("cooldown", "manaCost", "rangeVertical"),
               List.of("level", "cooldown", "manaCost", "rangeVertical")
            )
         );
      this.data
         .put(
            "Mega Jump_Break_Down",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Mega Jump_Break_Down"),
               List.of("cooldown", "manaCost", "rangeVertical", "radius"),
               List.of("level", "cooldown", "manaCost", "rangeVertical", "radius")
            )
         );
      this.data
         .put(
            "Nova_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Nova_Base"),
               List.of("cooldown", "manaCost", "radius", "ability_power", "knockback"),
               List.of("level", "cooldown", "manaCost", "radius", "ability_power", "knockback")
            )
         );
      this.data
         .put(
            "Nova_Dot",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Nova_Dot"),
               List.of("cooldown", "manaCost", "radius", "ability_power", "duration"),
               List.of("level", "cooldown", "manaCost", "radius", "ability_power", "duration")
            )
         );
      this.data
         .put(
            "Nova_Speed",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Nova_Speed"),
               List.of("cooldown", "manaCost", "radius", "duration", "slowness"),
               List.of("level", "cooldown", "manaCost", "radius", "duration", "slowness")
            )
         );
      this.data
         .put(
            "Rampage_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Rampage_Base"),
               List.of("cooldown", "manaCostPerSecond", "damage"),
               List.of("level", "cooldown", "manaCostPerSecond", "damage")
            )
         );
      this.data
         .put(
            "Rampage_Chain",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Rampage_Chain"),
               List.of("cooldown", "manaCostPerSecond", "chains"),
               List.of("level", "cooldown", "manaCostPerSecond", "chains")
            )
         );
      this.data
         .put(
            "Rampage_Leech",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Rampage_Leech"),
               List.of("cooldown", "manaCostPerSecond", "leech"),
               List.of("level", "cooldown", "manaCostPerSecond", "leech")
            )
         );
      this.data
         .put(
            "Summon_Eternal_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Summon_Eternal_Base"),
               List.of("cooldown", "manaCost", "eternals", "duration", "ancientChance"),
               List.of("level", "cooldown", "manaCost", "eternals", "duration", "ancientChance")
            )
         );
      this.data
         .put(
            "Empower_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Empower_Base"),
               List.of("cooldown", "manaCostPerSecond", "speed"),
               List.of("level", "cooldown", "manaCostPerSecond", "speed")
            )
         );
      this.data
         .put(
            "Empower_Ice_Armour",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Empower_Ice_Armour"),
               List.of("cooldown", "manaCostPerSecond", "chilled", "duration", "additionalManaPerHit"),
               List.of("level", "cooldown", "manaCostPerSecond", "chilled", "duration", "additionalManaPerHit")
            )
         );
      this.data
         .put(
            "Empower_Slowness_Aura",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Empower_Slowness_Aura"),
               List.of("cooldown", "manaCostPerSecond", "slowness", "radius"),
               List.of("level", "cooldown", "manaCostPerSecond", "slowness", "radius")
            )
         );
      this.data
         .put(
            "Taunt_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Taunt_Base"),
               List.of("cooldown", "manaCost", "radius", "duration"),
               List.of("level", "cooldown", "manaCost", "radius", "duration")
            )
         );
      this.data
         .put(
            "Taunt_Repel",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Taunt_Repel"),
               List.of("cooldown", "manaCost", "radius", "duration", "distance"),
               List.of("level", "cooldown", "manaCost", "radius", "duration", "distance")
            )
         );
      this.data
         .put(
            "Taunt_Charm",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Taunt_Charm"),
               List.of("cooldown", "manaCost", "radius", "duration", "maxTargets", "ability_power"),
               List.of("level", "cooldown", "manaCost", "radius", "duration", "maxTargets", "ability_power")
            )
         );
      this.data
         .put(
            "Vein_Miner_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Vein_Miner_Base"), List.of("cooldown", "blocks"), List.of("level", "cooldown", "blocks")
            )
         );
      this.data
         .put(
            "Vein Miner_Durability",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Vein Miner_Durability"),
               List.of("cooldown", "blocks", "unbreaking"),
               List.of("level", "cooldown", "blocks", "unbreaking")
            )
         );
      this.data
         .put(
            "Vein Miner_Fortune",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Vein Miner_Fortune"), List.of("cooldown", "blocks", "fortune"), List.of("level", "cooldown", "blocks", "fortune")
            )
         );
      this.data
         .put(
            "Vein Miner_Void",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Vein Miner_Void"), List.of("cooldown", "blocks"), List.of("level", "cooldown", "blocks")
            )
         );
      this.data
         .put(
            "Stonefall_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Stonefall_Base"),
               List.of("cooldown", "manaCost", "duration", "damageReduction", "radius", "knockback"),
               List.of("level", "cooldown", "manaCost", "duration", "damageReduction", "radius", "knockback")
            )
         );
      this.data
         .put(
            "Stonefall_Snow",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Stonefall_Snow"),
               List.of("cooldown", "manaCost", "duration", "damageReduction", "radius", "ability_power"),
               List.of("level", "cooldown", "manaCost", "duration", "damageReduction", "radius", "ability_power")
            )
         );
      this.data
         .put(
            "Stonefall_Cold",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Stonefall_Cold"),
               List.of("cooldown", "manaCost", "duration", "damageReduction", "freezeDuration", "amplifier", "radius"),
               List.of("level", "cooldown", "manaCost", "duration", "damageReduction", "freezeDuration", "amplifier", "radius")
            )
         );
      this.data
         .put(
            "Totem_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Totem_Base"),
               List.of("cooldown", "manaCost", "duration", "radius", "heal"),
               List.of("level", "cooldown", "manaCost", "duration", "radius", "heal")
            )
         );
      this.data
         .put(
            "Totem_Mob_Damage",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Totem_Mob_Damage"),
               List.of("cooldown", "manaCost", "duration", "radius", "damage"),
               List.of("level", "cooldown", "manaCost", "duration", "radius", "damage")
            )
         );
      this.data
         .put(
            "Totem_Mana_Regen",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Totem_Mob_Damage"),
               List.of("cooldown", "manaCost", "duration", "radius", "manaRegen"),
               List.of("level", "cooldown", "manaCost", "duration", "radius", "manaRegen")
            )
         );
      this.data
         .put(
            "Totem_Player_Damage",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Totem_Mob_Damage"),
               List.of("cooldown", "manaCost", "duration", "radius", "damage"),
               List.of("level", "cooldown", "manaCost", "duration", "radius", "damage")
            )
         );
      this.data
         .put(
            "Javelin_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Javelin_Base"),
               List.of("cooldown", "manaCost", "damage", "throwPower", "knockback"),
               List.of("level", "cooldown", "manaCost", "damage", "throwPower", "knockback")
            )
         );
      this.data
         .put(
            "Javelin_Piercing",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Javelin_Piercing"),
               List.of("cooldown", "manaCost", "damage", "throwPower", "piercing"),
               List.of("level", "cooldown", "manaCost", "damage", "throwPower", "piercing")
            )
         );
      this.data
         .put(
            "Javelin_Scatter",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Javelin_Scatter"),
               List.of("cooldown", "manaCost", "damage", "throwPower", "numberOfBounces", "numberOfJavelins"),
               List.of("level", "cooldown", "manaCost", "damage", "throwPower", "numberOfBounces", "numberOfJavelins")
            )
         );
      this.data
         .put(
            "Javelin_Sight",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Javelin_Sight"),
               List.of("cooldown", "manaCost", "damage", "throwPower", "radius", "duration"),
               List.of("level", "cooldown", "manaCost", "damage", "throwPower", "radius", "duration")
            )
         );
      this.data
         .put(
            "Smite_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Smite_Base"),
               List.of("cooldown", "manaCostPerSecond", "radius", "damage", "damageInterval", "additionalManaPerBolt"),
               List.of("level", "cooldown", "manaCostPerSecond", "radius", "damage", "damageInterval", "additionalManaPerBolt")
            )
         );
      this.data
         .put(
            "Smite_Archon",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Smite_Archon"),
               List.of("cooldown", "manaCostPerSecond", "radius", "damage", "damageInterval", "additionalManaPerBolt"),
               List.of("level", "cooldown", "manaCostPerSecond", "radius", "damage", "damageInterval", "additionalManaPerBolt")
            )
         );
      this.data
         .put(
            "Smite_Thunderstorm",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Smite_Thunderstorm"),
               List.of("cooldown", "manaCostPerSecond", "radius", "damage", "damageInterval", "additionalManaPerBolt"),
               List.of("level", "cooldown", "manaCostPerSecond", "radius", "damage", "damageInterval", "additionalManaPerBolt")
            )
         );
      this.data
         .put(
            "Shell_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Shell_Base"),
               List.of("cooldown", "manaCostPerSecond", "additionalManaPerHit", "stunChance", "stunAmplifier", "stunDuration"),
               List.of("level", "cooldown", "manaCostPerSecond", "additionalManaPerHit", "stunChance", "stunAmplifier", "stunDuration")
            )
         );
      this.data
         .put(
            "Shell_Porcupine",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Shell_Porcupine"),
               List.of("cooldown", "manaCostPerSecond", "durabilityWearReduction", "damage", "additionalManaPerHit"),
               List.of("level", "cooldown", "manaCostPerSecond", "durabilityWearReduction", "damage", "additionalManaPerHit")
            )
         );
      this.data
         .put(
            "Shell_Quill",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Shell_Quill"),
               List.of("cooldown", "manaCostPerSecond", "durabilityWearReduction", "damage", "additionalManaPerHit", "quillCount"),
               List.of("level", "cooldown", "manaCostPerSecond", "durabilityWearReduction", "damage", "additionalManaPerHit", "quillCount")
            )
         );
      this.data
         .put(
            "Fireball_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Fireball_Base"),
               List.of("cooldown", "manaCost", "ability_power", "radius"),
               List.of("level", "cooldown", "manaCost", "ability_power", "radius")
            )
         );
      this.data
         .put(
            "Fireball_Volley",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Fireball_Volley"),
               List.of("cooldown", "manaCost", "ability_power", "radius"),
               List.of("level", "cooldown", "manaCost", "ability_power", "radius")
            )
         );
      this.data
         .put(
            "Fireball_Fireshot",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Fireball_Fireshot"),
               List.of("cooldown", "manaCost", "ability_power"),
               List.of("level", "cooldown", "manaCost", "ability_power")
            )
         );
      this.data
         .put(
            "Storm_Arrow_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Storm_Arrow_Base"),
               List.of("cooldown", "manaCost", "ability_power", "radius", "stormInterval"),
               List.of("level", "cooldown", "manaCost", "ability_power", "radius", "stormInterval")
            )
         );
      this.data
         .put(
            "Storm_Arrow_Blizzard",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Storm_Arrow_Blizzard"),
               List.of("cooldown", "manaCost", "ability_power", "radius", "stormInterval", "frostbiteChance", "frostbiteDuration", "slowDuration", "amplifier"),
               List.of(
                  "level",
                  "cooldown",
                  "manaCost",
                  "ability_power",
                  "radius",
                  "stormInterval",
                  "frostbiteChance",
                  "frostbiteDuration",
                  "slowDuration",
                  "amplifier"
               )
            )
         );
      this.data
         .put(
            "Battle_Cry_Base",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Battle_Cry_Base"),
               List.of("cooldown", "manaCost", "attackDamagePerStack", "radius", "stacksUsedPerHit", "maxStacks", "duration"),
               List.of("level", "cooldown", "manaCost", "attackDamagePerStack", "radius", "stacksUsedPerHit", "maxStacks", "duration")
            )
         );
      this.data
         .put(
            "Battle_Cry_Spectral_Strike",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Battle_Cry_Spectral_Strike"),
               List.of("cooldown", "manaCost", "abilityPowerPerStack", "radius", "stacksUsedPerHit", "maxStacks", "duration"),
               List.of("level", "cooldown", "manaCost", "abilityPowerPerStack", "radius", "stacksUsedPerHit", "maxStacks", "duration")
            )
         );
      this.data
         .put(
            "Battle_Cry_Lucky_Strike",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Battle_Cry_Lucky_Strike"),
               List.of("cooldown", "manaCost", "luckyHitPerStack", "radius", "stacksUsedPerHit", "maxStacks", "duration"),
               List.of("level", "cooldown", "manaCost", "luckyHitPerStack", "radius", "stacksUsedPerHit", "maxStacks", "duration")
            )
         );
   }

   private JsonElement defaultDescription(String skillName) {
      return JsonParser.parseString(
         "[{text:'Default config description for ', color:'$text'},{text: '"
            + skillName
            + "', color: '$name'},{text: ' - please configure me!', color: '$text'}]"
      );
   }

   public static class DescriptionData {
      @Expose
      private final JsonElement description;
      @Expose
      private final List<String> current;
      @Expose
      private final List<String> next;

      public DescriptionData(JsonElement description, List<String> current, List<String> next) {
         this.description = description;
         this.current = current;
         this.next = next;
      }
   }
}
