package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.discoverylogic.goal.OverworldSpiritExtractionGoal;
import iskallia.vault.discoverylogic.goal.VaultAttackBlockComboGoal;
import iskallia.vault.discoverylogic.goal.VaultChainAttackGoal;
import iskallia.vault.discoverylogic.goal.VaultGuardianKillGoal;
import iskallia.vault.discoverylogic.goal.VaultLeechGoal;
import iskallia.vault.discoverylogic.goal.VaultMobStunGoal;
import iskallia.vault.discoverylogic.goal.VaultSoulShardCollectionGoal;
import iskallia.vault.discoverylogic.goal.VaultThornsDamageGoal;
import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class ModModelDiscoveryGoals {
   public static Map<ResourceLocation, DiscoveryGoal> REGISTRY = new HashMap<>();
   public static VaultSoulShardCollectionGoal SOUL_SHARD_COLLECTION = registerGoal(
      VaultMod.id("soul_shard_collection"), new VaultSoulShardCollectionGoal(10000)
   );
   public static VaultMobStunGoal MOB_STUNNED = registerGoal(VaultMod.id("mob_stunned"), new VaultMobStunGoal(100));
   public static VaultChainAttackGoal MOBS_CHAINED = registerGoal(VaultMod.id("mobs_chained"), new VaultChainAttackGoal(5, 6));
   public static VaultThornsDamageGoal THORNS_DAMAGE_DEALT = registerGoal(VaultMod.id("thorns_damage_dealt"), new VaultThornsDamageGoal(1000.0F));
   public static VaultAttackBlockComboGoal ATTACK_BLOCK_COMBO = registerGoal(VaultMod.id("attack_block_combo"), new VaultAttackBlockComboGoal(10.0F));
   public static VaultGuardianKillGoal GUARDIAN_KILL_GOAL = registerGoal(VaultMod.id("guardian_kill_goal"), new VaultGuardianKillGoal(10.0F));
   public static VaultLeechGoal LEECH = registerGoal(VaultMod.id("leech"), new VaultLeechGoal(100.0F));
   public static OverworldSpiritExtractionGoal SPIRIT_EXTRACTION = registerGoal(VaultMod.id("leech"), new OverworldSpiritExtractionGoal(200.0F));

   private static <G extends DiscoveryGoal> G registerGoal(ResourceLocation id, G goal) {
      goal.setId(id);
      REGISTRY.put(id, goal);
      return goal;
   }
}
