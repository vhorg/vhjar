package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModArchetypes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerArchetypeData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
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
      this.save();
      ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(this::resetSkillPoints);
   }

   public void enableResetKnowledgePoints() {
      this.resetKnowledgePoints = true;
      this.knowledgePointsCurrentlyReset.clear();
      this.save();
      ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(this::resetKnowledgePoints);
   }

   public void enableResetArchetypePoints() {
      this.resetArchetypePoints = true;
      this.archetypePointsCurrentlyReset.clear();
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
      this.skillPointsCurrentlyReset.add(player.getUUID());
      this.save();
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
      this.knowledgePointsCurrentlyReset.add(player.getUUID());
      this.save();
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
      this.archetypePointsCurrentlyReset.add(player.getUUID());
      this.save();
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
         PointsResetConfig config = ModConfigs.PLAYER_RESETS;
         if (config.resetSkillPoints && !config.skillPointsCurrentlyReset.contains(player.getUUID())) {
            config.resetSkillPoints(player);
         }

         if (config.resetKnowledgePoints && !config.knowledgePointsCurrentlyReset.contains(player.getUUID())) {
            config.resetKnowledgePoints(player);
         }

         if (config.resetArchetypePoints && !config.archetypePointsCurrentlyReset.contains(player.getUUID())) {
            config.resetArchetypePoints(player);
         }
      }
   }
}
