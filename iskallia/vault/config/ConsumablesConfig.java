package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.config.entry.ConsumableEffect;
import iskallia.vault.config.entry.ConsumableEntry;
import iskallia.vault.item.consumable.ConsumableType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ConsumablesConfig extends Config {
   @Expose
   private List<ConsumableEntry> itemEffects;
   @Expose
   private HashMap<String, List<String>> descriptions = new HashMap<>();

   @Override
   public String getName() {
      return "consumables";
   }

   @Override
   protected void reset() {
      final ConsumableEntry jadeAppleEntry = new ConsumableEntry("the_vault:jade_apple", true, 4.0F, ConsumableType.BASIC)
         .addEffect(new ConsumableEffect("minecraft:haste", 3, 6000).showIcon());
      final ConsumableEntry cobaltAppleEntry = new ConsumableEntry("the_vault:cobalt_apple", true, 0.0F, ConsumableType.BASIC)
         .addEffect(new ConsumableEffect("minecraft:fire_resistance", 1, 6000).showIcon())
         .addEffect(new ConsumableEffect("botania:feather_feet", 1, 6000).showIcon());
      final ConsumableEntry pixieAppleEntry = new ConsumableEntry("the_vault:pixie_apple", true, 8.0F, ConsumableType.BASIC)
         .addEffect(new ConsumableEffect("minecraft:speed", 5, 600).showIcon())
         .addEffect(new ConsumableEffect("minecraft:slow_falling", 2, 600).showIcon())
         .addEffect(new ConsumableEffect("minecraft:jump_boost", 2, 600).showIcon());
      final ConsumableEntry candyBarEntry = new ConsumableEntry("the_vault:candy_bar", false, 0.0F, ConsumableType.BASIC)
         .addEffect(new ConsumableEffect("minecraft:speed", 5, 1200).showIcon());
      final ConsumableEntry powerBarEntry = new ConsumableEntry("the_vault:power_bar", false, 0.0F, ConsumableType.BASIC)
         .addEffect(new ConsumableEffect("minecraft:strength", 4, 600).showIcon());
      final ConsumableEntry luckyAppleEntry = new ConsumableEntry("the_vault:lucky_apple", false, 0.0F, ConsumableType.POWERUP)
         .addEffect(new ConsumableEffect("minecraft:luck", 1, 2400).showIcon());
      final ConsumableEntry treasureAppleEntry = new ConsumableEntry("the_vault:treasure_apple", false, 0.0F, ConsumableType.POWERUP)
         .addEffect(new ConsumableEffect("minecraft:luck", 5, 2400).showIcon());
      final ConsumableEntry powerAppleEntry = new ConsumableEntry("the_vault:power_apple", false, 0.0F, ConsumableType.POWERUP)
         .addEffect(new ConsumableEffect("minecraft:strength", 2, 2400).showIcon());
      final ConsumableEntry ghostAppleEntry = new ConsumableEntry("the_vault:ghost_apple", false, 0.0F, ConsumableType.POWERUP)
         .addEffect(new ConsumableEffect("the_vault:parry", 15, 2400).showIcon());
      final ConsumableEntry golemAppleEntry = new ConsumableEntry("the_vault:golem_apple", false, 0.0F, ConsumableType.POWERUP)
         .addEffect(new ConsumableEffect("the_vault:resistance", 10, 2400).showIcon());
      final ConsumableEntry sweetAppleEntry = new ConsumableEntry("the_vault:sweet_apple", false, 0.0F, ConsumableType.POWERUP)
         .addEffect(new ConsumableEffect("minecraft:speed", 2, 2400).showIcon());
      final ConsumableEntry heartyAppleEntry = new ConsumableEntry("the_vault:hearty_apple", true, 2.0F, ConsumableType.POWERUP);
      this.itemEffects = new ArrayList<ConsumableEntry>() {
         {
            this.add(jadeAppleEntry);
            this.add(cobaltAppleEntry);
            this.add(pixieAppleEntry);
            this.add(candyBarEntry);
            this.add(powerBarEntry);
            this.add(luckyAppleEntry);
            this.add(treasureAppleEntry);
            this.add(powerAppleEntry);
            this.add(ghostAppleEntry);
            this.add(golemAppleEntry);
            this.add(sweetAppleEntry);
            this.add(heartyAppleEntry);
         }
      };
      this.descriptions
         .put(
            "the_vault:jade_apple",
            this.createStringList(
               new StringTextComponent(TextFormatting.GREEN + "Mine Pro!").func_150265_g(),
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Gives you " + TextFormatting.YELLOW + "2 extra hearts ").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "temporarily..").func_150265_g(),
               this.newLine(),
               new StringTextComponent(TextFormatting.RED + "Haste 3 " + TextFormatting.RESET + "for 5 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:cobalt_apple",
            this.createStringList(
               new StringTextComponent(TextFormatting.GREEN + "No Fear!").func_150265_g(),
               this.newLine(),
               new StringTextComponent(TextFormatting.RED + "Fire Resistance 1 " + TextFormatting.RESET + "and").func_150265_g(),
               new StringTextComponent(TextFormatting.RED + "negates fall damage " + TextFormatting.RESET + "for 5 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:pixie_apple",
            this.createStringList(
               new StringTextComponent(TextFormatting.GREEN + "Wannabe Flier!").func_150265_g(),
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Gives you " + TextFormatting.YELLOW + "2 extra hearts ").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "temporarily..").func_150265_g(),
               this.newLine(),
               new StringTextComponent(TextFormatting.RED + "Speed 5 " + TextFormatting.RESET + "and").func_150265_g(),
               new StringTextComponent(TextFormatting.RED + "Jump Boost 2 " + TextFormatting.RESET + "for 30 seconds.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:candy_bar",
            this.createStringList(
               new StringTextComponent(TextFormatting.GREEN + "Sugar Rush!").func_150265_g(),
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Gives you " + TextFormatting.RED + "Speed 5").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 60 seconds.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:power_bar",
            this.createStringList(
               new StringTextComponent(TextFormatting.GREEN + "Pumping Iron!").func_150265_g(),
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Gives you " + TextFormatting.RED + "Strength 4").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 30 seconds.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:lucky_apple",
            this.createStringList(
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Add " + TextFormatting.RED + "+1 Luck").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 2 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:treasure_apple",
            this.createStringList(
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Add " + TextFormatting.RED + "+5 Luck").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 2 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:power_apple",
            this.createStringList(
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Add " + TextFormatting.RED + "+2 Strength").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 2 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:ghost_apple",
            this.createStringList(
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Add " + TextFormatting.RED + "+25% Parry").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 2 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:golem_apple",
            this.createStringList(
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Add " + TextFormatting.RED + "+15 Resistance").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 2 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:sweet_apple",
            this.createStringList(
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Add " + TextFormatting.RED + "+2 Speed").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "for 2 minutes.").func_150265_g()
            )
         );
      this.descriptions
         .put(
            "the_vault:hearty_apple",
            this.createStringList(
               this.newLine(),
               new StringTextComponent(TextFormatting.RESET + "Add " + TextFormatting.RED + "1 Extra Heart").func_150265_g(),
               new StringTextComponent(TextFormatting.RESET + "temporarily...").func_150265_g()
            )
         );
   }

   private String newLine() {
      return " ";
   }

   private List<String> createStringList(String... lines) {
      return new ArrayList<>(Arrays.asList(lines));
   }

   public ConsumableEntry get(String id) {
      for (ConsumableEntry setting : this.itemEffects) {
         if (setting.getItemId().equalsIgnoreCase(id)) {
            return setting;
         }
      }

      return null;
   }

   public List<String> getDescriptionFor(String item) {
      return this.descriptions.get(item);
   }

   public float getParryChance() {
      ConsumableEntry entry = this.get("the_vault:ghost_apple");
      if (entry != null) {
         for (ConsumableEffect effect : entry.getEffects()) {
            if (effect.getEffectId().equalsIgnoreCase(Vault.id("parry").toString())) {
               return effect.getAmplifier() * 0.01F;
            }
         }
      }

      return 0.0F;
   }
}
