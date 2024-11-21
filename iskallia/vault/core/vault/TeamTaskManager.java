package iskallia.vault.core.vault;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.TeamRenderer;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.world.data.TeamTaskData;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class TeamTaskManager {
   private static final int SYNC_INTERVAL = 5;
   private static long lastSyncTime = 0L;
   private static boolean shouldCheckReclaimTasks = true;
   private static Map<Object, TeamTaskManager.TaskEventListener> taskEventListeners = new HashMap<>();

   public static void addTaskEventListener(Object parent, TeamTaskManager.TaskEventListener listener) {
      taskEventListeners.put(parent, listener);
   }

   public static void removeTaskEventListener(Object parent) {
      taskEventListeners.remove(parent);
   }

   public static void resetTasks(MinecraftServer server) {
      TeamTaskData teamTaskData = TeamTaskData.get(server);
      teamTaskData.removeAllTeamTasks();
      server.getPlayerList().getPlayers().forEach(player -> {
         Scoreboard scoreboard = server.getScoreboard();
         PlayerTeam playersTeam = scoreboard.getPlayersTeam(player.getGameProfile().getName());
         if (playersTeam != null) {
            teamTaskData.syncAll(player);
            checkAndAddPlayerInTeam(teamTaskData, player, playersTeam.getName());
         }
      });
   }

   public static boolean refreshTask(MinecraftServer server, String taskId) {
      TeamTaskData teamTaskData = TeamTaskData.get(server);
      if (teamTaskData.refreshTask(taskId, server)) {
         taskEventListeners.values().forEach(listener -> listener.onTaskRefresh(taskId));
         return true;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onPlayerJoin(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
         Scoreboard scoreboard = event.getPlayer().getServer().getScoreboard();
         PlayerTeam playersTeam = scoreboard.getPlayersTeam(event.getPlayer().getGameProfile().getName());
         if (playersTeam != null) {
            TeamTaskData teamTaskData = TeamTaskData.get(event.getPlayer().getServer());
            checkAndAddPlayerInTeam(teamTaskData, serverPlayer, playersTeam.getName());
            teamTaskData.syncAll(serverPlayer);
         }
      }
   }

   @SubscribeEvent
   public static void onServerStart(ServerStartedEvent event) {
      TeamTaskData teamTaskData = TeamTaskData.get(event.getServer());
      attachTeamTasks(event, teamTaskData);
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         if (lastSyncTime + 5L <= server.overworld().getGameTime()) {
            lastSyncTime = server.overworld().getGameTime();
            TeamTaskData teamTaskData = TeamTaskData.get(server);
            teamTaskData.setDirty();
            if (shouldCheckReclaimTasks) {
               addReclaimTasksForAllTeams(teamTaskData, teamTaskData.getCompletedTasks(), server);
               shouldCheckReclaimTasks = false;
            }

            checkTaskCompletion(teamTaskData, server);
            server.getPlayerList().getPlayers().forEach(teamTaskData::syncAll);
         }
      }
   }

   private static void checkTaskCompletion(TeamTaskData teamTaskData, MinecraftServer server) {
      Map<String, String> completedTasks = new HashMap<>();

      for (Entry<String, TeamTaskData.TeamTasks> entry : teamTaskData.getAllTeamTasks().entrySet()) {
         String teamName = entry.getKey();
         TeamTaskData.TeamTasks teamTasks = entry.getValue();
         teamTasks.getCompletedTasks().forEach(task -> {
            if (!teamTaskData.getCompletedTaskTeam(task.getId()).isPresent()) {
               teamTaskData.addCompletedTask(task.getId(), teamName);
               completedTasks.put(task.getId(), teamName);
               notifyAllPlayersOfTaskCompletion(server, teamName, task);
            }
         });
      }

      completedTasks.keySet().forEach(teamTaskData::removeTaskFromTeamTasksExceptForCompletingTeam);
      addReclaimTasksForAllTeams(teamTaskData, completedTasks, server);
   }

   private static void addReclaimTasksForAllTeams(TeamTaskData teamTaskData, Map<String, String> completedTasks, MinecraftServer server) {
      if (!completedTasks.isEmpty()) {
         teamTaskData.getAllTeamTasks().values().forEach(teamTasks -> addReclaimTasksFor(teamTasks, completedTasks, server));
         teamTaskData.setDirty();
      }
   }

   private static void notifyAllPlayersOfTaskCompletion(MinecraftServer server, String teamName, Task task) {
      Scoreboard scoreboard = server.getScoreboard();
      PlayerTeam team = scoreboard.getPlayerTeam(teamName);
      if (team != null) {
         Component completionMessage = new TextComponent("Team ")
            .withStyle(ChatFormatting.GOLD)
            .append(team.getDisplayName().copy().withStyle(team.getColor()))
            .append(" has completed task ")
            .append(new TextComponent(task.getRenderer() instanceof TeamRenderer teamRenderer ? teamRenderer.name : "").withStyle(ChatFormatting.GREEN))
            .append(
               new TextComponent(task instanceof IProgressTask progressTask ? " " + progressTask.getProgress().getCurrent().intValue() : "")
                  .withStyle(ChatFormatting.GREEN)
            );
         server.getPlayerList().getPlayers().forEach(player -> player.displayClientMessage(completionMessage, false));
      }
   }

   private static void attachTeamTasks(ServerStartedEvent event, TeamTaskData teamTaskData) {
      teamTaskData.getAllTeamTasks().values().forEach(teamTasks -> teamTasks.onAttach(event.getServer()));
   }

   @SubscribeEvent
   public static void onServerStop(ServerStoppedEvent event) {
      TeamTaskData teamTaskData = TeamTaskData.get(event.getServer());
      teamTaskData.getAllTeamTasks().values().forEach(TeamTaskData.TeamTasks::onDetach);
   }

   public static void playerAddedToTeam(String playerName, String teamName) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
         if (player != null) {
            TeamTaskData teamTaskData = TeamTaskData.get(server);
            checkAndAddPlayerInTeam(teamTaskData, player, teamName);
         }
      }
   }

   public static void playerRemovedFromTeam(PlayerTeam playerTeam, String playerName, String teamName) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
         if (player != null) {
            removePlayerFromTeam(TeamTaskData.get(server), player, teamName, playerTeam.getPlayers().isEmpty());
         }
      }
   }

   private static void checkAndAddPlayerInTeam(TeamTaskData teamTaskData, ServerPlayer serverPlayer, String teamName) {
      teamTaskData.getAllTeamTasks()
         .values()
         .stream()
         .filter(teamTasksx -> teamTasksx.playerInTeam(serverPlayer.getUUID()) && !teamTasksx.teamName().equals(teamName))
         .forEach(teamTasksx -> teamTaskData.removePlayerFromTeam(serverPlayer, teamTasksx));
      TeamTaskData.TeamTasks teamTasks = teamTaskData.getTeamTasks(teamName);
      if (teamTasks != null) {
         addReclaimTasks(teamTaskData, teamTasks, serverPlayer.getServer());
         if (teamTasks.playerInTeam(serverPlayer.getUUID())) {
            return;
         }

         teamTaskData.addPlayerToExistingTeam(serverPlayer, teamTasks);
      } else {
         addTeamTasks(teamTaskData, teamName, serverPlayer);
      }
   }

   private static void addTeamTasks(TeamTaskData teamTaskData, String teamName, ServerPlayer serverPlayer) {
      EntityTaskSource taskSource = EntityTaskSource.empty();
      taskSource.add(serverPlayer.getUUID());
      addTeamTasks(teamTaskData, teamName, serverPlayer.getServer(), serverPlayer, taskSource);
   }

   private static void addTeamTasks(
      TeamTaskData teamTaskData, String teamName, MinecraftServer server, @Nullable ServerPlayer serverPlayer, EntityTaskSource taskSource
   ) {
      TeamTaskData.TeamTasks teamTasks = new TeamTaskData.TeamTasks(
         teamName,
         taskSource,
         ModConfigs.TEAM_TASKS
            .streamNonReclaimTasks()
            .filter(task -> teamTaskData.getCompletedTaskTeam(task.getId()).isEmpty())
            .map(Task::copy)
            .collect(Collectors.toMap(Task::getId, t -> (Task)t))
      );
      addReclaimTasks(teamTaskData, teamTasks, server);
      teamTaskData.addTeamTasks(server, serverPlayer, teamName, teamTasks);
   }

   private static void addReclaimTasks(TeamTaskData teamTaskData, TeamTaskData.TeamTasks teamTasks, MinecraftServer server) {
      addReclaimTasksFor(teamTasks, teamTaskData.getCompletedTasks(), server);
   }

   private static void addReclaimTasksFor(TeamTaskData.TeamTasks teamTasks, Map<String, String> completedTasks, MinecraftServer server) {
      completedTasks.forEach((taskId, teamName) -> ModConfigs.TEAM_TASKS.getNextTask(taskId).ifPresent(task -> {
         if (!completedTasks.containsKey(task.getId()) && !teamName.equals(teamTasks.teamName()) && !teamTasks.getTask(task.getId()).isPresent()) {
            teamTasks.addTask(task.copy(), server);
         }
      }));
   }

   private static void removePlayerFromTeam(TeamTaskData teamTaskData, ServerPlayer serverPlayer, String teamName, boolean lastPlayerInTeam) {
      TeamTaskData.TeamTasks teamTasks = teamTaskData.getTeamTasks(teamName);
      if (teamTasks != null) {
         if (teamTasks.playerInTeam(serverPlayer.getUUID())) {
            if (lastPlayerInTeam) {
               teamTaskData.removeTeam(teamName, serverPlayer);
            } else {
               teamTaskData.removePlayerFromTeam(serverPlayer, teamTasks);
            }
         }
      }
   }

   public static void onConfigReload() {
      shouldCheckReclaimTasks = true;
      taskEventListeners.values().forEach(TeamTaskManager.TaskEventListener::onConfigLoad);
   }

   public interface TaskEventListener {
      void onTaskRefresh(String var1);

      void onConfigLoad();
   }
}
