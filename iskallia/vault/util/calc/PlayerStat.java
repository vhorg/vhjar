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
   @SerializedName("thorns_chance")
   THORNS_CHANCE,
   @SerializedName("thorns_damage")
   THORNS_DAMAGE,
   @SerializedName("durability_damage")
   DURABILITY_DAMAGE,
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
   HEALING_EFFECTIVENESS;
}
