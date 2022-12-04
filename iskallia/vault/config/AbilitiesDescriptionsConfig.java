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
            "Dash",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Dash"), List.of("cooldown", "manaCost", "distance"), List.of("level", "cooldown", "manaCost", "distance")
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
            "Execute",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Execute"), List.of("cooldown", "duration", "damage"), List.of("level", "cooldown", "duration", "damage")
            )
         );
      this.data
         .put(
            "Farmer",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Farmer"),
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
            "Ghost Walk",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Ghost Walk"), List.of("cooldown", "manaCost", "duration"), List.of("level", "cooldown", "manaCost", "duration")
            )
         );
      this.data
         .put(
            "Heal",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Heal"), List.of("cooldown", "manaCost", "heal"), List.of("level", "cooldown", "manaCost", "heal")
            )
         );
      this.data
         .put(
            "Heal_Effect",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Heal_Effect"), List.of("cooldown", "manaCost"), List.of("level", "cooldown", "manaCost")
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
            "Hunter",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Hunter"),
               List.of("cooldown", "manaCost", "duration", "radius"),
               List.of("level", "cooldown", "manaCost", "duration", "radius")
            )
         );
      this.data
         .put(
            "Hunter_Blocks",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Hunter_Blocks"),
               List.of("cooldown", "manaCost", "duration", "radius"),
               List.of("level", "cooldown", "manaCost", "duration", "radius")
            )
         );
      this.data
         .put(
            "Mana Shield",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Mana Shield"),
               List.of("cooldown", "manaCostPerSecond", "absorb"),
               List.of("level", "cooldown", "manaCostPerSecond", "absorb")
            )
         );
      this.data
         .put(
            "Mega Jump",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Mega Jump"),
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
               List.of("cooldown", "manaCost", "rangeVertical"),
               List.of("level", "cooldown", "manaCost", "rangeVertical")
            )
         );
      this.data
         .put(
            "Nova",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Nova"),
               List.of("cooldown", "manaCost", "radius", "damage", "knockback"),
               List.of("level", "cooldown", "manaCost", "radius", "damage", "knockback")
            )
         );
      this.data
         .put(
            "Nova_Dot",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Nova_Dot"),
               List.of("cooldown", "manaCost", "radius", "damage", "duration"),
               List.of("level", "cooldown", "manaCost", "radius", "damage", "duration")
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
            "Rampage",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Rampage"),
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
            "Summon Eternal",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Summon Eternal"),
               List.of("cooldown", "manaCost", "eternals", "duration", "ancientChance"),
               List.of("level", "cooldown", "manaCost", "eternals", "duration", "ancientChance")
            )
         );
      this.data
         .put(
            "Tank",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Tank"),
               List.of("cooldown", "manaCostPerSecond", "duration", "resistance"),
               List.of("level", "cooldown", "manaCostPerSecond", "duration", "resistance")
            )
         );
      this.data
         .put(
            "Tank_Projectile",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Tank_Projectile"),
               List.of("cooldown", "manaCostPerSecond", "reducedProjectileDamageTaken", "resistanceKnockback"),
               List.of("level", "cooldown", "manaCostPerSecond", "reducedProjectileDamageTaken", "resistanceKnockback")
            )
         );
      this.data
         .put(
            "Tank_Reflect",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Tank_Reflect"),
               List.of("cooldown", "manaCostPerSecond", "chance", "damage"),
               List.of("level", "cooldown", "manaCostPerSecond", "chance", "damage")
            )
         );
      this.data
         .put(
            "Taunt",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Taunt"),
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
            "Vein Miner",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Vein Miner"), List.of("cooldown", "blocks"), List.of("level", "cooldown", "blocks")
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
            "Stonefall",
            new AbilitiesDescriptionsConfig.DescriptionData(
               this.defaultDescription("Stonefall"), List.of("cooldown", "manaCost", "duration"), List.of("level", "cooldown", "manaCost", "duration")
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
