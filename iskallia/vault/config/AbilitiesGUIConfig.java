package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class AbilitiesGUIConfig extends Config {
   @Expose
   private Map<String, AbilitiesGUIConfig.AbilityStyle> styles;
   private final Map<String, AbilitiesGUIConfig.SpecializationStyle> specializationStyleLookup = new HashMap<>();

   @Override
   public String getName() {
      return "abilities_gui_styles";
   }

   @Override
   public <T extends Config> T readConfig() {
      AbilitiesGUIConfig config = super.readConfig();
      config.specializationStyleLookup.clear();

      for (AbilitiesGUIConfig.AbilityStyle abilityStyle : config.styles.values()) {
         Map<String, AbilitiesGUIConfig.SpecializationStyle> specializationStyles = abilityStyle.getSpecializationStyles();
         if (specializationStyles != null) {
            config.specializationStyleLookup.putAll(specializationStyles);
         }
      }

      return (T)config;
   }

   @Nullable
   public ResourceLocation getIcon(String key) {
      return this.specializationStyleLookup.containsKey(key) ? this.specializationStyleLookup.get(key).getIcon() : null;
   }

   public Map<String, AbilitiesGUIConfig.AbilityStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
      int x = 0;
      int y = 0;
      this.styles.put("Nova", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Nova_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/nova")));
            this.put("Nova_Slow", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/nova_speed")));
            this.put("Nova_Dot", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/nova_dot")));
         }
      }));
      x += 38;
      this.styles.put("Vein_Miner", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Vein_Miner_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/vein_miner")));
            this.put("Vein_Miner_Fortune", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/vein_miner_fortune")));
            this.put("Vein_Miner_Durability", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/vein_miner_durability")));
            this.put("Vein_Miner_Void", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/vein_miner_void")));
         }
      }));
      x += 38;
      this.styles.put("Rampage", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Rampage_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/rampage")));
            this.put("Rampage_Leech", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/rampage_leech")));
            this.put("Rampage_Chain", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/rampage_chain")));
         }
      }));
      x += 38;
      this.styles.put("Ghost_Walk", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Ghost_Walk_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/ghost_walk")));
            this.put("Ghost_Walk_Spirit", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/ghost_walk_spirit_walk")));
         }
      }));
      x += 38;
      this.styles.put("Dash", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Dash_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/dash")));
            this.put("Dash_Damage", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/dash_damage")));
            this.put("Dash_Warp", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/dash_warp")));
         }
      }));
      x += 38;
      this.styles.put("Mega_Jump", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Mega_Jump_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/mega_jump")));
            this.put("Mega_Jump_Break_Up", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/mega_jump_break_up")));
            this.put("Mega_Jump_Break_Down", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/mega_jump_break_down")));
         }
      }));
      x += 38;
      this.styles.put("Mana_Shield", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Mana_Shield_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/mana_shield")));
            this.put("Mana_Shield_Retribution", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/mana_shield_retribution")));
         }
      }));
      x += 38;
      this.styles.put("Execute", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Execute_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/execute")));
         }
      }));
      x += 38;
      this.styles.put("Heal", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Heal_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/heal")));
            this.put("Heal_Group", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/heal_group")));
            this.put("Heal_Cleanse", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/heal_effect")));
         }
      }));
      x += 38;
      this.styles.put("Empower", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Empower_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/empower")));
            this.put("Empower_Ice_Armor", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/empower_ice_armour")));
         }
      }));
      x += 38;
      this.styles.put("Summon_Eternal", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Summon_Eternal_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/summon_eternal")));
         }
      }));
      x += 38;
      this.styles.put("Hunter", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Hunter_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter")));
            this.put("Hunter_Blocks", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter_blocks")));
            this.put("Hunter_Wooden", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter_wooden")));
            this.put("Hunter_Gilded", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter_gilded")));
            this.put("Hunter_Living", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter_living")));
            this.put("Hunter_Ornate", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter_ornate")));
            this.put("Hunter_Coins", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter_coins")));
         }
      }));
      x += 38;
      this.styles.put("Farmer", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Farmer_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/farmer")));
            this.put("Farmer_Melon", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/farmer_melon")));
            this.put("Farmer_Cactus", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/farmer_cactus")));
            this.put("Farmer_Animal", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/farmer_animal")));
         }
      }));
      x += 38;
      this.styles.put("Taunt", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Taunt_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/taunt")));
            this.put("Taunt_Repel", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/taunt_repel")));
            this.put("Taunt_Charm", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/taunt_charm")));
         }
      }));
      x += 38;
      this.styles.put("Stonefall", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Stonefall_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/stonefall")));
            this.put("Stonefall_Snow", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/stonefall_snow")));
            this.put("Stonefall_Cold", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/stonefall_cold")));
         }
      }));
      x += 38;
      this.styles.put("Totem", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Totem_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/totem")));
            this.put("Totem_Mob_Damage", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/totem_mob_damage")));
            this.put("Totem_Mana_Regen", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/totem_mana_regen")));
            this.put("Totem_Player_Damage", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/totem_player_damage")));
         }
      }));
      x += 38;
      this.styles.put("Javelin", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Javelin_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/javelin")));
            this.put("Javelin_Piercing", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/javelin_piercing")));
            this.put("Javelin_Scatter", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/javelin_scatter")));
            this.put("Javelin_Sight", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/javelin_sight")));
         }
      }));
      x += 38;
      this.styles.put("Smite", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Smite_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/smite")));
            this.put("Smite_Archon", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/smite_archon")));
            this.put("Smite_Thunderstorm", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/smite_thunderstorm")));
         }
      }));
      x += 38;
      this.styles.put("Shell", new AbilitiesGUIConfig.AbilityStyle(x, y, new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
         {
            this.put("Shell_Base", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/shell")));
            this.put("Shell_Porcupine", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/shell_porcupine")));
            this.put("Shell_Quill", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/shell_quill")));
         }
      }));
   }

   public static class AbilityStyle {
      @Expose
      private final int x;
      @Expose
      private final int y;
      @Expose
      private final Map<String, AbilitiesGUIConfig.SpecializationStyle> specializationStyles;

      public AbilityStyle(int x, int y, Map<String, AbilitiesGUIConfig.SpecializationStyle> specializationStyles) {
         this.x = x;
         this.y = y;
         this.specializationStyles = specializationStyles;
      }

      public List<ResourceLocation> getIcons() {
         return this.specializationStyles.values().stream().map(AbilitiesGUIConfig.IconStyle::getIcon).collect(Collectors.toList());
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public Map<String, AbilitiesGUIConfig.SpecializationStyle> getSpecializationStyles() {
         return this.specializationStyles;
      }
   }

   public static class IconStyle {
      @Expose
      private final ResourceLocation icon;

      public IconStyle(ResourceLocation icon) {
         this.icon = icon;
      }

      public ResourceLocation getIcon() {
         return this.icon;
      }
   }

   public static class SpecializationStyle extends AbilitiesGUIConfig.IconStyle {
      public SpecializationStyle(ResourceLocation icon) {
         super(icon);
      }
   }
}
