package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModGearAttributes;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class MenuPlayerStatDescriptionConfig extends Config {
   private static final String NAME = "menu_player_stat_description";
   @Expose
   private LinkedHashMap<String, String> PROMINENT_STATS_DESCRIPTIONS;
   @Expose
   private TreeMap<ResourceLocation, String> MOD_GEAR_ATTRIBUTE_DESCRIPTIONS;
   @Expose
   private LinkedHashMap<String, String> VAULT_STATS_DESCRIPTIONS;

   @Override
   public String getName() {
      return "menu_player_stat_description";
   }

   public String getVaultStatDescriptionFor(@Nullable String key) {
      if (key == null) {
         return "Null key";
      } else {
         String result = this.VAULT_STATS_DESCRIPTIONS.get(key);
         return result == null ? "Missing description for [%s]".formatted(key) : result;
      }
   }

   public String getProminentStatDescriptionFor(@Nullable String key) {
      if (key == null) {
         return "Null key";
      } else {
         String result = this.PROMINENT_STATS_DESCRIPTIONS.get(key);
         return result == null ? "Missing description for [%s]".formatted(key) : result;
      }
   }

   @Nullable
   public String getModGearAttributeDescriptionFor(@Nullable ResourceLocation resourceLocation) {
      if (resourceLocation == null) {
         return "Null resource location";
      } else {
         String result = this.MOD_GEAR_ATTRIBUTE_DESCRIPTIONS.get(resourceLocation);
         return result == null ? "Missing description for [%s]".formatted(resourceLocation) : result;
      }
   }

   @Override
   protected void reset() {
      this.VAULT_STATS_DESCRIPTIONS = new LinkedHashMap<String, String>() {
         {
            this.put("treasure_rooms_opened", "The total number of treasure rooms you've opened.");
            this.put("crystals_crafted", "The total number of Vault Crystals you've crafted.");
            this.put("vaults_total", "The total number of vaults you've run.");
            this.put("vaults_completed", "The total number of vaults you've successfully completed.");
            this.put("vaults_bailed", "The total number of vaults you've survived.");
            this.put("vaults_failed", "The total number of vaults you've failed.");
            this.put("experience", "The total vault experience you've gained.");
            this.put("damage_dealt", "The total amount of damage you've dealt in vaults.");
            this.put("damage_taken", "The total amount of damage you've taken in vaults.");
            this.put("mobs_unalived", "The total number of mobs you've unalived in vaults.");
            this.put("blocks_mined", "The total number of blocks you've mined in vaults.");
            this.put("trapped_chests", "The total number of trapped chests you've opened in vaults.");
            this.put("chests_looted", "The total number of chests you've looted in vaults.");
         }
      };
      this.PROMINENT_STATS_DESCRIPTIONS = new LinkedHashMap<String, String>() {
         {
            this.put("damage", "Configure me!");
            this.put("hearts", "Configure me!");
            this.put("defense", "Configure me!");
            this.put("mana", "Configure me!");
            this.put("greed", "Configure me!");
         }
      };
      this.MOD_GEAR_ATTRIBUTE_DESCRIPTIONS = new TreeMap<ResourceLocation, String>() {
         {
            this.put(ModGearAttributes.ARMOR.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ATTACK_DAMAGE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ATTACK_SPEED.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ATTACK_SPEED_PERCENT.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ABILITY_POWER.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.REACH.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ATTACK_RANGE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.KNOCKBACK_RESISTANCE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.HEALTH.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.MANA_REGEN_ADDITIVE_PERCENTILE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.MANA_ADDITIVE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.MANA_ADDITIVE_PERCENTILE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.HEALING_EFFECTIVENESS.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.DURABILITY.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.COOLDOWN_REDUCTION.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.LEECH.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.RESISTANCE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.BLOCK.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.SOULBOUND.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.IS_FIRE_IMMUNE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.CRITICAL_HIT_TAKEN_REDUCTION.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.VANILLA_CRITICAL_HIT_CHANCE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.DURABILITY_WEAR_REDUCTION.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.FATAL_STRIKE_CHANCE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.FATAL_STRIKE_DAMAGE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.THORNS_CHANCE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.THORNS_DAMAGE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ON_HIT_CHAIN.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ON_HIT_AOE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ON_HIT_STUN.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.SWEEPING_HIT_CHANCE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ITEM_QUANTITY.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.ITEM_RARITY.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.TRAP_DISARMING.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.SOUL_QUANTITY.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.DAMAGE_INCREASE.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.DAMAGE_ILLAGERS.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.DAMAGE_SPIDERS.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.DAMAGE_UNDEAD.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.VELARA_AFFINITY.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.TENOS_AFFINITY.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.WENDARR_AFFINITY.getRegistryName(), "Configure me!");
            this.put(ModGearAttributes.IDONA_AFFINITY.getRegistryName(), "Configure me!");
         }
      };
   }
}
