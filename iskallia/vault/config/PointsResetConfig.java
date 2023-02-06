package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModArchetypes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerArchetypeData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.PointsResetData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class PointsResetConfig extends Config {
   @Expose
   private boolean resetSkillPoints;
   @Expose
   private boolean resetKnowledgePoints;
   @Expose
   private boolean resetArchetypePoints;
   @Expose
   private List<UUID> skillPointsCurrentlyReset = new ArrayList<>();
   @Expose
   private List<UUID> archetypePointsCurrentlyReset = new ArrayList<>();
   @Expose
   private List<UUID> knowledgePointsCurrentlyReset = new ArrayList<>();

   @Override
   public String getName() {
      return "points_reset";
   }

   @Override
   protected void reset() {
      this.resetSkillPoints = false;
      this.resetKnowledgePoints = false;
      this.resetArchetypePoints = false;
   }

   public void enableResetSkillPoints() {
      this.resetSkillPoints = true;
      this.skillPointsCurrentlyReset.clear();
      PointsResetData.get().onResetSkillPoints();
      this.save();
      ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(this::resetSkillPoints);
   }

   public void enableResetKnowledgePoints() {
      this.resetKnowledgePoints = true;
      this.knowledgePointsCurrentlyReset.clear();
      PointsResetData.get().onResetKnowledgePoints();
      this.save();
      ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(this::resetKnowledgePoints);
   }

   public void enableResetArchetypePoints() {
      this.resetArchetypePoints = true;
      this.archetypePointsCurrentlyReset.clear();
      PointsResetData.get().onResetArchetypePoints();
      this.save();
      ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(this::resetArchetypePoints);
   }

   private void resetSkillPoints(ServerPlayer player) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      PlayerTalentsData playerTalentsData = PlayerTalentsData.get(server);
      PlayerAbilitiesData playerAbilitiesData = PlayerAbilitiesData.get(server);
      List<TalentNode<?>> talentNodes = new ArrayList<>(playerTalentsData.getTalents(player).getNodes());
      talentNodes.forEach(node -> playerTalentsData.remove(player, node));
      List<AbilityNode<?, ?>> abilityNodes = new ArrayList<>(playerAbilitiesData.getAbilities(player).getNodes());
      abilityNodes.forEach(node -> playerAbilitiesData.remove(player, node));
      PlayerVaultStatsData data = PlayerVaultStatsData.get(server);
      data.resetAndReturnSkillPoints(player);
      PointsResetData.get().addToSkillPoinsList(player.getUUID());
      player.sendMessage(
         new TextComponent(
               "Your Abilities and Talents were reset and all skill points were refunded. This is because of a patch that required it, or your Server Admin has decided it was necessary."
            )
            .withStyle(ChatFormatting.YELLOW),
         Util.NIL_UUID
      );
   }

   private void resetKnowledgePoints(ServerPlayer player) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      PlayerResearchesData researchesData = PlayerResearchesData.get(server.overworld());
      List<String> researchesDone = new ArrayList<>(researchesData.getResearches(player).getResearchesDone());
      researchesDone.forEach(name -> researchesData.removeResearch(player, ModConfigs.RESEARCHES.getByName(name)));
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(server);
      statsData.resetAndReturnKnowledgePoints(player);
      PointsResetData.get().addToKnowledgePoinsList(player.getUUID());
      player.sendMessage(
         new TextComponent(
               "Your Researches were reset and all knowledge points were refunded. This is because of a patch that required it, or your Server Admin has decided it was necessary."
            )
            .withStyle(ChatFormatting.AQUA),
         Util.NIL_UUID
      );
   }

   private void resetArchetypePoints(ServerPlayer player) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      PlayerArchetypeData archetypeData = PlayerArchetypeData.get(server.overworld());
      archetypeData.set(player, ModArchetypes.DEFAULT.getRegistryName());
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(server);
      statsData.resetAndReturnArchetypePoints(player);
      PointsResetData.get().addToArchetypePoinsList(player.getUUID());
      player.sendMessage(
         new TextComponent(
               "Your Archetype was reset and all archetype points were refunded. This is because of a patch that required it, or your Server Admin has decided it was necessary."
            )
            .withStyle(ChatFormatting.LIGHT_PURPLE),
         Util.NIL_UUID
      );
   }

   private void save() {
      try {
         this.writeConfig();
      } catch (IOException var2) {
         VaultMod.LOGGER.error("Unable to write config file when trying to reset player points.");
         VaultMod.LOGGER.error(var2.getMessage());
         var2.printStackTrace();
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         removeAndRefundTalent(player, ModConfigs.TALENTS.REACH);
         PointsResetConfig config = ModConfigs.PLAYER_RESETS;
         if (config.skillPointsCurrentlyReset.contains(player.getUUID())) {
            migrateSkillPointsData(player.getUUID());
            config.skillPointsCurrentlyReset.remove(player.getUUID());
         }

         if (config.knowledgePointsCurrentlyReset.contains(player.getUUID())) {
            migrateKnowledgePointsData(player.getUUID());
            config.knowledgePointsCurrentlyReset.remove(player.getUUID());
         }

         if (config.archetypePointsCurrentlyReset.contains(player.getUUID())) {
            migrateArchetypePointsData(player.getUUID());
            config.archetypePointsCurrentlyReset.remove(player.getUUID());
         }

         config.save();
         if (config.resetSkillPoints
            && !config.skillPointsCurrentlyReset.contains(player.getUUID())
            && !PointsResetData.get().hasSkillPointsReset(player.getUUID())) {
            config.resetSkillPoints(player);
         }

         if (config.resetKnowledgePoints
            && !config.knowledgePointsCurrentlyReset.contains(player.getUUID())
            && !PointsResetData.get().hasKnowledgePointsReset(player.getUUID())) {
            config.resetKnowledgePoints(player);
         }

         if (config.resetArchetypePoints
            && !config.archetypePointsCurrentlyReset.contains(player.getUUID())
            && !PointsResetData.get().hasArchetypePointsReset(player.getUUID())) {
            config.resetArchetypePoints(player);
         }
      }
   }

   public static void removeAndRefundTalent(ServerPlayer player, TalentGroup<?> talentGroup) {
      PlayerTalentsData data = PlayerTalentsData.get(player.getLevel());
      TalentTree talents = data.getTalents(player);
      if (talents.hasLearnedNode(talentGroup)) {
         Optional<TalentNode<?>> talentNode = talents.getLearnedNodes()
            .stream()
            .filter(node -> node.getGroup().getParentName().equals(talentGroup.getParentName()))
            .findFirst();
         if (!talentNode.isEmpty()) {
            TalentNode<?> talent = talentNode.get();
            int level = talent.getLevel();
            int cost = 0;

            for (int i = 1; i <= level; i++) {
               cost += talent.getGroup().cost(i);
            }

            PlayerVaultStatsData statsData = PlayerVaultStatsData.get(player.getLevel());
            statsData.refundSkillPoints(player, cost);
            data.remove(player, talent);
            player.sendMessage(
               new TranslatableComponent("commands.the_vault.talent.removed", new Object[]{talent.getName(), cost}).withStyle(ChatFormatting.YELLOW),
               player.getUUID()
            );
         }
      }
   }

   private static void migrateSkillPointsData(UUID uuid) {
      PointsResetData.get().addToSkillPoinsList(uuid);
   }

   private static void migrateKnowledgePointsData(UUID uuid) {
      PointsResetData.get().addToKnowledgePoinsList(uuid);
   }

   private static void migrateArchetypePointsData(UUID uuid) {
      PointsResetData.get().addToArchetypePoinsList(uuid);
   }
}
