package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.discoverylogic.goal.OverworldSpiritExtractionGoal;
import iskallia.vault.discoverylogic.goal.VaultAttackBlockComboGoal;
import iskallia.vault.discoverylogic.goal.VaultBlockPlacementGoal;
import iskallia.vault.discoverylogic.goal.VaultChainAttackGoal;
import iskallia.vault.discoverylogic.goal.VaultGuardianKillGoal;
import iskallia.vault.discoverylogic.goal.VaultLeechGoal;
import iskallia.vault.discoverylogic.goal.VaultMobStunGoal;
import iskallia.vault.discoverylogic.goal.VaultSoulShardCollectionGoal;
import iskallia.vault.discoverylogic.goal.VaultThornsDamageGoal;
import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class ModModelDiscoveryGoals {
   public static Map<ResourceLocation, DiscoveryGoal<?>> REGISTRY = new HashMap<>();
   public static VaultSoulShardCollectionGoal SOUL_SHARD_COLLECTION = registerGoal(
      VaultMod.id("soul_shard_collection"),
      new VaultSoulShardCollectionGoal(10000)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.SOUL_SWORD.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have collected " + (int)goal.getTargetProgress() + "x Soul Shards this Vault!")
                     .withStyle(ChatFormatting.LIGHT_PURPLE);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static VaultMobStunGoal MOB_STUNNED = registerGoal(
      VaultMod.id("mob_stunned"),
      new VaultMobStunGoal(400)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.BASEBALL_BAT.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have stunned " + (int)goal.getTargetProgress() + "x monsters this Vault!")
                     .withStyle(ChatFormatting.GOLD);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static VaultChainAttackGoal MOBS_CHAINED = registerGoal(
      VaultMod.id("mobs_chained"),
      new VaultChainAttackGoal(5, 6)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.CHAINSWORD.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent(
                        "You have " + goal.getTargetCount() + "-chain attacked " + (int)goal.getTargetProgress() + " times this Vault!"
                     )
                     .withStyle(ChatFormatting.DARK_RED);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static VaultThornsDamageGoal THORNS_DAMAGE_DEALT = registerGoal(
      VaultMod.id("thorns_damage_dealt"),
      new VaultThornsDamageGoal(10000.0F)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Shields.NOU.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have dealt " + goal.getTargetProgress() + " thorns reflection damage this Vault!")
                     .withStyle(ChatFormatting.DARK_GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SHIELD, modelId, player);
               }
            }
         )
   );
   public static VaultAttackBlockComboGoal ATTACK_BLOCK_COMBO = registerGoal(
      VaultMod.id("attack_block_combo"),
      new VaultAttackBlockComboGoal(10.0F)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Shields.BELL.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have blocked " + (int)goal.getTargetProgress() + " attacks in a row this Vault!")
                     .withStyle(ChatFormatting.GOLD);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SHIELD, modelId, player);
               }
            }
         )
   );
   public static VaultGuardianKillGoal GUARDIAN_KILL_GOAL = registerGoal(
      VaultMod.id("guardian_kill_goal"),
      new VaultGuardianKillGoal(10.0F)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Shields.TURTLE.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent(
                        "You have unalived " + (int)goal.getTargetProgress() + " Guardians while wearing a Turtle Helmet this Vault!"
                     )
                     .withStyle(ChatFormatting.DARK_RED);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SHIELD, modelId, player);
               }
            }
         )
   );
   public static VaultLeechGoal LEECH = registerGoal(
      VaultMod.id("leech"),
      new VaultLeechGoal(100.0F)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.DARK_BLADE.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have leeched " + goal.getTargetProgress() + " health this Vault!")
                     .withStyle(ChatFormatting.DARK_PURPLE);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static OverworldSpiritExtractionGoal SPIRIT_EXTRACTION = registerGoal(
      VaultMod.id("spirit_extraction"),
      new OverworldSpiritExtractionGoal(189.0F)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.DEATHS_DOOR.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have revived a spirit " + (int)goal.getTargetProgress() + " for at least 189 gold!")
                     .withStyle(ChatFormatting.GOLD);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static VaultBlockPlacementGoal BAMBOO_PLACEMENT = registerGoal(
      VaultMod.id("bamboo_placement"),
      new VaultBlockPlacementGoal(Blocks.BAMBOO_SAPLING, 12.0F)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.BAMBOO.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have placed " + (int)goal.getTargetProgress() + " bamboos this Vault!")
                     .withStyle(ChatFormatting.GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );

   private static <G extends DiscoveryGoal<G>> G registerGoal(ResourceLocation id, G goal) {
      goal.setId(id);
      REGISTRY.put(id, goal);
      return goal;
   }
}
