package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.discoverylogic.goal.OverworldSpiritExtractionGoal;
import iskallia.vault.discoverylogic.goal.VaultAttackBlockComboGoal;
import iskallia.vault.discoverylogic.goal.VaultBlockBreakGoal;
import iskallia.vault.discoverylogic.goal.VaultBlockPlacementGoal;
import iskallia.vault.discoverylogic.goal.VaultChainAttackGoal;
import iskallia.vault.discoverylogic.goal.VaultCompletionGoal;
import iskallia.vault.discoverylogic.goal.VaultGuardianKillGoal;
import iskallia.vault.discoverylogic.goal.VaultLeechGoal;
import iskallia.vault.discoverylogic.goal.VaultMobKillGoal;
import iskallia.vault.discoverylogic.goal.VaultMobStunGoal;
import iskallia.vault.discoverylogic.goal.VaultSoulShardCollectionGoal;
import iskallia.vault.discoverylogic.goal.VaultThornsDamageGoal;
import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import iskallia.vault.discoverylogic.goal.base.FlaggedVaultDiscoveryGoal;
import iskallia.vault.entity.entity.miner_zombie.MinerZombieEntity;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.AxeItem;
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
      new VaultThornsDamageGoal(100000.0F)
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
      VaultMod.id("spirit_extraction"), new OverworldSpiritExtractionGoal(189.0F).setReward((player, goal) -> {
         DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
         ResourceLocation modelId = ModDynamicModels.Swords.DEATHS_DOOR.getId();
         if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
            MutableComponent info = new TextComponent("You have revived a spirit for at least 189 gold!").withStyle(ChatFormatting.GOLD);
            player.sendMessage(info, Util.NIL_UUID);
            discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
         }
      })
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
   public static VaultMobKillGoal MOBS_KILLED = registerGoal(
      VaultMod.id("mobs_killed"), new VaultMobKillGoal(500).withPredicate(e -> e.getEntityLiving() instanceof Mob).setReward((player, goal) -> {
         DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
         ResourceLocation modelId = ModDynamicModels.Swords.RED_KATANA.getId();
         if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
            MutableComponent info = new TextComponent("You have killed " + (int)goal.getTargetProgress() + " mobs this Vault!").withStyle(ChatFormatting.GREEN);
            player.sendMessage(info, Util.NIL_UUID);
            discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
         }
      })
   );
   public static VaultMobKillGoal MOBS_KILLED_2 = registerGoal(
      VaultMod.id("mobs_killed_2"),
      new VaultMobKillGoal(500)
         .withPredicate(e -> e.getEntityLiving() instanceof MinerZombieEntity)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Armor.LUPICANIS.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have killed " + (int)goal.getTargetProgress() + " Miner Zombies this Vault!")
                     .withStyle(ChatFormatting.GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.LUPICANIS);
               }
            }
         )
   );
   public static VaultMobKillGoal MOBS_PROJECTILE_KILLED = registerGoal(
      VaultMod.id("mobs_projectile_killed"),
      new VaultMobKillGoal(20)
         .withPredicate(e -> e.getEntityLiving() instanceof Mob)
         .withPredicate(e -> e.getSource().isProjectile())
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.RING_BLADE.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have killed " + (int)goal.getTargetProgress() + " mobs with a projectile this Vault!")
                     .withStyle(ChatFormatting.GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static VaultBlockBreakGoal BREAK_FOOLS_GOLD = registerGoal(
      VaultMod.id("break_fools_gold"), new VaultBlockBreakGoal(ModBlocks.FOOLS_GOLD_BLOCK, 1.0F).setReward((player, goal) -> {
         DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
         ResourceLocation modelId = ModDynamicModels.Armor.PIRATE.getId();
         if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
            MutableComponent info = new TextComponent("YARHAR fiddle dee dee, this gold isn't for thee!").withStyle(ChatFormatting.GOLD);
            player.sendMessage(info, Util.NIL_UUID);
            discoversData.discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.PIRATE);
         }
      })
   );
   public static VaultMobKillGoal SLOWED_MOBS_KILLED = registerGoal(
      VaultMod.id("slowed_mobs_killed"),
      new VaultMobKillGoal(500)
         .withPredicate(e -> e.getEntityLiving() instanceof Mob)
         .withPredicate(e -> e.getEntityLiving().hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.SOUL_SWORD_BLUE.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have killed " + (int)goal.getTargetProgress() + " slowed mobs this Vault!")
                     .withStyle(ChatFormatting.GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static VaultMobKillGoal POISONED_MOBS_KILLED = registerGoal(
      VaultMod.id("poisoned_mobs_killed"),
      new VaultMobKillGoal(300)
         .withPredicate(e -> e.getEntityLiving() instanceof Mob)
         .withPredicate(e -> e.getEntityLiving().hasEffect(MobEffects.POISON))
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Swords.SOUL_SWORD_GREEN.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have killed " + (int)goal.getTargetProgress() + " poisoned mobs this Vault!")
                     .withStyle(ChatFormatting.GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
               }
            }
         )
   );
   public static VaultMobKillGoal AXE_MOBS_KILLED = registerGoal(
      VaultMod.id("axe_mobs_killed"),
      new VaultMobKillGoal(300)
         .withPredicate(e -> e.getEntityLiving() instanceof Mob)
         .withKillerPredicate(p -> p.getMainHandItem().getItem() instanceof AxeItem)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Axes.BLOOD_CHOPPER.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have killed " + (int)goal.getTargetProgress() + " mobs with an Axe this Vault!")
                     .withStyle(ChatFormatting.GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.AXE, modelId, player);
               }
            }
         )
   );
   public static VaultMobKillGoal AXE_MOBS_KILLED_2 = registerGoal(
      VaultMod.id("axe_mobs_killed_2"),
      new VaultMobKillGoal(600)
         .withPredicate(e -> e.getEntityLiving() instanceof Mob)
         .withKillerPredicate(p -> p.getMainHandItem().getItem() instanceof AxeItem)
         .setReward(
            (player, goal) -> {
               DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
               ResourceLocation modelId = ModDynamicModels.Axes.BLOOD_CLEAVER.getId();
               if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
                  MutableComponent info = new TextComponent("You have killed " + (int)goal.getTargetProgress() + " mobs with an Axe this Vault!")
                     .withStyle(ChatFormatting.GREEN);
                  player.sendMessage(info, Util.NIL_UUID);
                  discoversData.discoverModelAndBroadcast(ModItems.AXE, modelId, player);
               }
            }
         )
   );
   public static VaultCompletionGoal LV50_VAULT_COMPLETED_WITHOUT_MANA_USAGE = registerGoal(
      VaultMod.id("lv50_vault_completed_without_mana_usage"),
      new VaultCompletionGoal().withPredicate(data -> data.getVault().get(Vault.LEVEL).get() >= 50).setReward((player, goal) -> {
         DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
         ResourceLocation modelId = ModDynamicModels.Axes.EVIL_MACE.getId();
         if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
            MutableComponent info = new TextComponent("You have completed this vault without using any mana at all!").withStyle(ChatFormatting.GREEN);
            player.sendMessage(info, Util.NIL_UUID);
            discoversData.discoverModelAndBroadcast(ModItems.AXE, modelId, player);
         }
      })
   );
   public static VaultCompletionGoal LV50_VAULT_COMPLETED_WITHOUT_HIT_TAKEN = registerGoal(
      VaultMod.id("lv50_vault_completed_without_hit_taken"),
      new VaultCompletionGoal()
         .withPredicate(data -> data.getVault().get(Vault.LEVEL).get() >= 50)
         .withServerInitializer((manager, goal) -> CommonEvents.ENTITY_DAMAGE.register(manager, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
               goal.setProgress(player, FlaggedVaultDiscoveryGoal.FLAG_FAILED);
            }
         }))
         .setReward((player, goal) -> {
            DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
            ResourceLocation modelId = ModDynamicModels.Shields.CRYSTAL_HEART.getId();
            if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
               MutableComponent info = new TextComponent("You have completed this vault without taking any damage at all!").withStyle(ChatFormatting.GREEN);
               player.sendMessage(info, Util.NIL_UUID);
               discoversData.discoverModelAndBroadcast(ModItems.SHIELD, modelId, player);
            }
         })
   );
   public static VaultCompletionGoal VAULT_COMPLETED_WITHOUT_HEALING = registerGoal(
      VaultMod.id("vault_completed_without_healing"),
      new VaultCompletionGoal().withServerInitializer((manager, goal) -> CommonEvents.ENTITY_HEAL.register(manager, event -> {
         if (event.getEntity() instanceof ServerPlayer player) {
            goal.setProgress(player, FlaggedVaultDiscoveryGoal.FLAG_FAILED);
         }
      })).setReward((player, goal) -> {
         DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
         ResourceLocation modelId = ModDynamicModels.Axes.LAST_SIGHT.getId();
         if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
            MutableComponent info = new TextComponent("You have completed this vault without any healing!").withStyle(ChatFormatting.GREEN);
            player.sendMessage(info, Util.NIL_UUID);
            discoversData.discoverModelAndBroadcast(ModItems.AXE, modelId, player);
         }
      })
   );

   private static <G extends DiscoveryGoal<G>> G registerGoal(ResourceLocation id, G goal) {
      goal.setId(id);
      REGISTRY.put(id, goal);
      return goal;
   }
}
