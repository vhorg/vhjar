package iskallia.vault.util.calc;

import com.google.gson.annotations.SerializedName;

public enum PlayerStat {
   @SerializedName("absorption")
   ABSORPTION,
   @SerializedName("block_chance")
   BLOCK_CHANCE,
   @SerializedName("cooldown_reduction")
   COOLDOWN_REDUCTION,
   @SerializedName("fatal_strike_chance")
   FATAL_STRIKE_CHANCE,
   @SerializedName("fatal_strike_damage")
   FATAL_STRIKE_DAMAGE,
   @SerializedName("leech")
   LEECH,
   @SerializedName("resistance")
   RESISTANCE,
   @SerializedName("speed")
   SPEED,
   @SerializedName("thorns_chance")
   THORNS_CHANCE,
   @SerializedName("thorns_damage")
   THORNS_DAMAGE_FLAT,
   @SerializedName("thorns_damage_multiplier")
   THORNS_DAMAGE_MULTIPLIER,
   @SerializedName("durability_damage")
   DURABILITY_DAMAGE,
   @SerializedName("durability_wear_reduction")
   DURABILITY_WEAR_REDUCTION,
   @SerializedName("durability_wear_reduction_cap")
   DURABILITY_WEAR_REDUCTION_CAP,
   @SerializedName("on_hit_chain")
   ON_HIT_CHAIN,
   @SerializedName("item_quantity")
   ITEM_QUANTITY,
   @SerializedName("item_rarity")
   ITEM_RARITY,
   @SerializedName("rage_per_hit")
   RAGE_PER_HIT,
   @SerializedName("rage_damage")
   RAGE_DAMAGE,
   @SerializedName("healing_effectiveness")
   HEALING_EFFECTIVENESS,
   @SerializedName("velara_affinity")
   VELARA_AFFINITY,
   @SerializedName("tenos_affinity")
   TENOS_AFFINITY,
   @SerializedName("wendarr_affinity")
   WENDARR_AFFINITY,
   @SerializedName("idona_affinity")
   IDONA_AFFINITY,
   @SerializedName("copiously")
   COPIOUSLY,
   @SerializedName("lucky_hit_chance")
   LUCKY_HIT_CHANCE,
   @SerializedName("soul_chance")
   SOUL_CHANCE,
   @SerializedName("trap_disarm_chance")
   TRAP_DISARM_CHANCE,
   @SerializedName("stun_chance")
   STUN_CHANCE,
   @SerializedName("ability_power_multiplier")
   ABILITY_POWER_MULTIPLIER;
}
