package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import java.util.ArrayList;
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
      if (this.styles.containsKey(key)) {
         return this.styles.get(key).getIcon();
      } else {
         return this.specializationStyleLookup.containsKey(key) ? this.specializationStyleLookup.get(key).getIcon() : null;
      }
   }

   public Map<String, AbilitiesGUIConfig.AbilityStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
      int x = 0;
      int y = 0;
      this.styles
         .put(
            "Nova",
            new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/nova"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
               {
                  this.put("Nova_Speed", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/nova_speed")));
                  this.put("Nova_Dot", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/nova_dot")));
               }
            })
         );
      x += 38;
      this.styles
         .put(
            "Vein Miner",
            new AbilitiesGUIConfig.AbilityStyle(
               x, y, VaultMod.id("gui/abilities/vein_miner"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
                  {
                     this.put("Vein Miner_Fortune", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/vein_miner_fortune")));
                     this.put("Vein Miner_Durability", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/vein_miner_durability")));
                     this.put("Vein Miner_Void", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/vein_miner_void")));
                  }
               }
            )
         );
      x += 38;
      this.styles
         .put(
            "Rampage",
            new AbilitiesGUIConfig.AbilityStyle(
               x, y, VaultMod.id("gui/abilities/rampage"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
                  {
                     this.put("Rampage_Leech", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/rampage_leech")));
                     this.put("Rampage_Chain", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/rampage_chain")));
                  }
               }
            )
         );
      x += 38;
      this.styles.put("Ghost Walk", new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/ghost_walk")));
      x += 38;
      this.styles
         .put(
            "Dash",
            new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/dash"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
               {
                  this.put("Dash_Damage", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/dash_damage")));
               }
            })
         );
      x += 38;
      this.styles
         .put(
            "Mega Jump",
            new AbilitiesGUIConfig.AbilityStyle(
               x, y, VaultMod.id("gui/abilities/mega_jump"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
                  {
                     this.put("Mega Jump_Break_Up", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/mega_jump_break_up")));
                     this.put("Mega Jump_Break_Down", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/mega_jump_break_down")));
                  }
               }
            )
         );
      x += 38;
      this.styles.put("Mana Shield", new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/mana_shield")));
      x += 38;
      this.styles.put("Execute", new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/execute")));
      x += 38;
      this.styles
         .put(
            "Heal",
            new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/heal"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
               {
                  this.put("Heal_Group", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/heal_group")));
                  this.put("Heal_Effect", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/heal_effect")));
               }
            })
         );
      x += 38;
      this.styles
         .put(
            "Tank",
            new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/tank"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
               {
                  this.put("Tank_Projectile", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/tank_projectile")));
                  this.put("Tank_Reflect", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/tank_reflect")));
               }
            })
         );
      x += 38;
      this.styles.put("Summon Eternal", new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/summon_eternal")));
      x += 38;
      this.styles
         .put(
            "Hunter",
            new AbilitiesGUIConfig.AbilityStyle(
               x, y, VaultMod.id("gui/abilities/hunter"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
                  {
                     this.put("Hunter_Blocks", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/hunter_blocks")));
                  }
               }
            )
         );
      x += 38;
      this.styles
         .put(
            "Farmer",
            new AbilitiesGUIConfig.AbilityStyle(
               x, y, VaultMod.id("gui/abilities/farmer"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
                  {
                     this.put("Farmer_Melon", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/farmer_melon")));
                     this.put("Farmer_Cactus", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/farmer_cactus")));
                     this.put("Farmer_Animal", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/farmer_animal")));
                  }
               }
            )
         );
      x += 38;
      this.styles
         .put(
            "Taunt",
            new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/taunt"), new LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle>() {
               {
                  this.put("Taunt_Repel", new AbilitiesGUIConfig.SpecializationStyle(VaultMod.id("gui/abilities/taunt_repel")));
               }
            })
         );
      x += 38;
      this.styles.put("Stonefall", new AbilitiesGUIConfig.AbilityStyle(x, y, VaultMod.id("gui/abilities/stonefall")));
   }

   public static class AbilityStyle extends AbilitiesGUIConfig.IconStyle {
      @Expose
      private final int x;
      @Expose
      private final int y;
      @Expose
      private final LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle> specializationStyles;

      public AbilityStyle(int x, int y, ResourceLocation icon) {
         this(x, y, icon, new LinkedHashMap<>());
      }

      public AbilityStyle(int x, int y, ResourceLocation icon, LinkedHashMap<String, AbilitiesGUIConfig.SpecializationStyle> specializationStyles) {
         super(icon);
         this.x = x;
         this.y = y;
         this.specializationStyles = specializationStyles;
      }

      public List<ResourceLocation> getIcons() {
         return this.specializationStyles
            .values()
            .stream()
            .map(AbilitiesGUIConfig.IconStyle::getIcon)
            .collect(Collectors.toCollection(() -> new ArrayList<>(List.of(this.getIcon()))));
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
