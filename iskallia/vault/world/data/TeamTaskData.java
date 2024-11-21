package iskallia.vault.world.data;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.RemoveTeamTasksMessage;
import iskallia.vault.network.message.UpdateAllTeamTasksMessage;
import iskallia.vault.network.message.UpdateTeamTasksMessage;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.counter.TargetTaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class TeamTaskData extends SavedData {
   protected static final String DATA_NAME = "the_vault_Team_Tasks";
   public static final TeamTaskData CLIENT_INSTANCE = new TeamTaskData();
   private final Map<String, TeamTaskData.TeamTasks> allTeamTasks = new HashMap<>();
   private final Map<String, String> completedTasks = new HashMap<>();
   private final List<TeamTaskData.TeamScore> teamScores = new ArrayList<>();

   public static TeamTaskData get() {
      return CLIENT_INSTANCE;
   }

   public static TeamTaskData get(MinecraftServer server) {
      return (TeamTaskData)server.overworld().getDataStorage().computeIfAbsent(TeamTaskData::create, TeamTaskData::new, "the_vault_Team_Tasks");
   }

   private static TeamTaskData create(CompoundTag tag) {
      TeamTaskData data = new TeamTaskData();
      data.load(tag);
      return data;
   }

   private void load(CompoundTag tag) {
      ListTag teamTaskListTag = tag.getList("TeamTasks", 10);
      teamTaskListTag.forEach(teamTaskTag -> {
         CompoundTag teamTasksTag = (CompoundTag)teamTaskTag;
         TeamTaskData.TeamTasks.readNbt(teamTasksTag.getCompound("Tasks")).ifPresent(teamTasks -> {
            String teamName = teamTasksTag.getString("Team");
            this.allTeamTasks.put(teamName, teamTasks);
         });
      });
      ListTag completedTasksListTag = tag.getList("CompletedTasks", 10);
      completedTasksListTag.forEach(completedTaskTag -> {
         CompoundTag completedTask = (CompoundTag)completedTaskTag;
         this.completedTasks.put(completedTask.getString("TaskId"), completedTask.getString("TeamName"));
      });
   }

   public CompoundTag save(CompoundTag pCompoundTag) {
      ListTag teamTaskListTag = new ListTag();
      this.allTeamTasks.forEach((teamName, teamTasks) -> {
         CompoundTag teamTasksTag = new CompoundTag();
         teamTasksTag.putString("Team", teamName);
         teamTasksTag.put("Tasks", teamTasks.writeNbt());
         teamTaskListTag.add(teamTasksTag);
      });
      pCompoundTag.put("TeamTasks", teamTaskListTag);
      ListTag completedTasksListTag = new ListTag();
      this.completedTasks.forEach((taskId, teamName) -> {
         CompoundTag completedTaskTag = new CompoundTag();
         completedTaskTag.putString("TaskId", taskId);
         completedTaskTag.putString("TeamName", teamName);
         completedTasksListTag.add(completedTaskTag);
      });
      pCompoundTag.put("CompletedTasks", completedTasksListTag);
      return pCompoundTag;
   }

   public Map<String, TeamTaskData.TeamTasks> getAllTeamTasks() {
      return this.allTeamTasks;
   }

   @Nullable
   public TeamTaskData.TeamTasks getTeamTasks(String teamName) {
      return this.allTeamTasks.get(teamName);
   }

   public TeamTaskData.TeamTasks removeTeam(String teamName, @Nullable ServerPlayer serverPlayer) {
      if (!this.allTeamTasks.containsKey(teamName)) {
         return null;
      } else {
         TeamTaskData.TeamTasks teamTasksRemoved = this.allTeamTasks.remove(teamName);
         teamTasksRemoved.onDetach();
         this.setDirty();
         if (serverPlayer != null) {
            this.syncRemoved(teamName, serverPlayer);
         }

         return teamTasksRemoved;
      }
   }

   public void removePlayerFromTeam(ServerPlayer serverPlayer, TeamTaskData.TeamTasks teamTasks) {
      teamTasks.removePlayer(serverPlayer.getUUID());
      this.setDirtyAndSynchronize(teamTasks, serverPlayer);
   }

   public void addTeamTasks(MinecraftServer server, @Nullable ServerPlayer serverPlayer, String teamName, TeamTaskData.TeamTasks teamTasks) {
      this.allTeamTasks.put(teamName, teamTasks);
      teamTasks.onAttach(server);
      this.setDirtyAndSynchronize(teamTasks, serverPlayer);
   }

   private void setDirtyAndSynchronize(TeamTaskData.TeamTasks teamTasks, @Nullable ServerPlayer serverPlayer) {
      this.setDirty();
      if (serverPlayer != null) {
         this.syncSingle(teamTasks, serverPlayer);
      }
   }

   public void addPlayerToExistingTeam(ServerPlayer serverPlayer, TeamTaskData.TeamTasks teamTasks) {
      teamTasks.addPlayer(serverPlayer.getUUID());
      this.setDirtyAndSynchronize(teamTasks, serverPlayer);
   }

   public void addCompletedTask(String taskId, String teamName) {
      this.completedTasks.put(taskId, teamName);
      this.setDirty();
   }

   private void syncSingle(TeamTaskData.TeamTasks teamTasks, ServerPlayer serverPlayer) {
      UpdateTeamTasksMessage message = new UpdateTeamTasksMessage(teamTasks);
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
   }

   private void syncRemoved(String teamName, ServerPlayer serverPlayer) {
      RemoveTeamTasksMessage message = new RemoveTeamTasksMessage(teamName);
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
   }

   public void syncAll(ServerPlayer serverPlayer) {
      UpdateAllTeamTasksMessage message = new UpdateAllTeamTasksMessage(this.allTeamTasks.values(), this.completedTasks);
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
   }

   public void updateAllTeamTasks(Collection<TeamTaskData.TeamTasks> teamTasks) {
      this.allTeamTasks.clear();
      teamTasks.forEach(teamTask -> this.allTeamTasks.put(teamTask.teamName, teamTask));
   }

   public void updateTeamTasks(TeamTaskData.TeamTasks teamTasks) {
      this.allTeamTasks.put(teamTasks.teamName, teamTasks);
   }

   public void updateCompletedTasks(Map<String, String> completedTasks) {
      if (!completedTasks.equals(this.completedTasks)) {
         this.completedTasks.clear();
         this.completedTasks.putAll(completedTasks);
         this.updateTeamScores();
      }
   }

   public void removeTeamTasks(String teamName) {
      this.allTeamTasks.remove(teamName);
   }

   public void removeAllTeamTasks() {
      this.completedTasks.clear();
      this.allTeamTasks.values().forEach(TeamTaskData.TeamTasks::onDetach);
      this.allTeamTasks.clear();
      this.setDirty();
   }

   public void removeTaskFromTeamTasksExceptForCompletingTeam(String taskId) {
      this.allTeamTasks.values().forEach(teamTasks -> {
         if (this.completedTasks.get(taskId).equals(teamTasks.teamName)) {
            teamTasks.getTask(taskId).ifPresent(Task::onDetach);
         } else {
            teamTasks.removeTask(taskId);
         }
      });
      this.setDirty();
   }

   public Optional<String> getCompletedTaskTeam(String taskId) {
      return Optional.ofNullable(this.completedTasks.get(taskId));
   }

   public Map<String, String> getCompletedTasks() {
      return this.completedTasks;
   }

   public boolean refreshTask(String taskId, MinecraftServer server) {
      if (!this.removeTaskFromTeamsAndCompleted(taskId)) {
         return false;
      } else {
         Task task = ModConfigs.TEAM_TASKS.getTask(taskId);
         if (task != null) {
            this.allTeamTasks.values().forEach(teamTasks -> teamTasks.addTask(task.copy(), server));
            this.setDirty();
            this.syncToAllOnlinePlayers(server);
            return true;
         } else {
            return false;
         }
      }
   }

   private void syncToAllOnlinePlayers(MinecraftServer server) {
      server.getPlayerList().getPlayers().forEach(this::syncAll);
   }

   private boolean removeTaskFromTeamsAndCompleted(String taskId) {
      Optional<Task> nextTaskId = ModConfigs.TEAM_TASKS.getNextTask(taskId);
      AtomicBoolean result = new AtomicBoolean(false);
      nextTaskId.ifPresent(task -> {
         if (this.allTeamTasks.values().stream().anyMatch(teamTasks -> teamTasks.hasTask(taskId)) && this.removeTaskFromTeamsAndCompleted(task.getId())) {
            result.set(true);
         }
      });
      this.allTeamTasks.values().forEach(teamTasks -> {
         if (teamTasks.removeTask(taskId)) {
            result.set(true);
         }
      });
      this.completedTasks.remove(taskId);
      return result.get();
   }

   public boolean setTaskProgress(String teamName, String taskId, int progress, MinecraftServer server) {
      TeamTaskData.TeamTasks teamTasks = this.allTeamTasks.get(teamName);
      return teamTasks == null ? false : teamTasks.getTask(taskId).map(task -> {
         if (this.setProgressIfProgressTask(task, teamTasks, progress, server)) {
            this.syncToAllOnlinePlayers(server);
            return true;
         } else {
            for (Task child : task.getChildren()) {
               if (this.setProgressIfProgressTask(child, teamTasks, progress, server)) {
                  this.syncToAllOnlinePlayers(server);
                  return true;
               }
            }

            return false;
         }
      }).orElse(false);
   }

   private boolean setProgressIfProgressTask(Task task, TeamTaskData.TeamTasks teamTasks, int progress, MinecraftServer server) {
      if (task instanceof ProgressConfiguredTask<?, ?> progressTask
         && progressTask.getCounter() instanceof TargetTaskCounter<?, ?> targetTaskCounter
         && targetTaskCounter.getCurrent() instanceof Integer) {
         ((TargetTaskCounter<Integer, ?>)targetTaskCounter).onSet(progress, TaskContext.of(teamTasks.taskSource, server));
         return true;
      } else {
         return false;
      }
   }

   public Stream<String> streamAssignedTaskIds() {
      return this.allTeamTasks.values().stream().flatMap(teamTasks -> teamTasks.tasks().values().stream()).map(Task::getId).distinct();
   }

   public Set<String> getTeamNames() {
      return this.allTeamTasks.keySet();
   }

   public Map<String, Number> getTaskProgress(String taskId) {
      return this.allTeamTasks
         .values()
         .stream()
         .map(teamTasks -> teamTasks.getTask(taskId).flatMap(t -> this.getTaskProgress(t).map(progress -> new SimpleEntry<>(teamTasks.teamName, progress))))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
   }

   private Optional<Number> getTaskProgress(Task task) {
      if (task instanceof ProgressConfiguredTask<?, ?> progressTask && progressTask.getCounter() != null) {
         return Optional.of(progressTask.getProgress().getCurrent());
      } else {
         for (Task child : task.getChildren()) {
            if (child instanceof ProgressConfiguredTask<?, ?> progressChildTask && progressChildTask.getCounter() != null) {
               return Optional.of(progressChildTask.getProgress().getCurrent());
            }
         }

         return Optional.empty();
      }
   }

   public void updateTeamScores() {
      Map<String, Set<String>> teamCompletedTasks = new HashMap<>();
      this.completedTasks.forEach((taskId, teamName) -> teamCompletedTasks.computeIfAbsent(teamName, t -> new HashSet<>()).add(taskId));
      this.teamScores.clear();
      teamCompletedTasks.forEach((teamName, taskIds) -> this.teamScores.add(new TeamTaskData.TeamScore(teamName, (Set<String>)taskIds, Set.of(), Map.of())));
      this.teamScores.sort(Comparator.<TeamTaskData.TeamScore, Integer>comparing(ts -> ts.completedTasks().size()).reversed());
   }

   public List<TeamTaskData.TeamScore> getTeamScores() {
      return this.teamScores;
   }

   public record TeamScore(String teamName, Set<String> completedTasks, Set<String> currentlyClaimedTasks, Map<String, Integer> bonusPoints) {
   }

   public static final class TeamTasks {
      private final String teamName;
      private final EntityTaskSource taskSource;
      private final Map<String, Task> tasks;
      private boolean tasksAttached = false;

      public TeamTasks(String teamName, EntityTaskSource taskSource, Map<String, Task> tasks) {
         this.teamName = teamName;
         this.taskSource = taskSource;
         this.tasks = tasks;
      }

      public boolean playerInTeam(UUID playerId) {
         return this.taskSource.getUuids().contains(playerId);
      }

      public Optional<Task> getTask(String taskId) {
         return Optional.ofNullable(this.tasks.get(taskId));
      }

      public void onDetach() {
         this.tasks.values().forEach(Task::onDetach);
         this.tasksAttached = false;
      }

      public void onAttach(MinecraftServer server) {
         if (!this.tasksAttached) {
            this.tasks.values().forEach(task -> task.onAttach(TaskContext.of(this.taskSource, server)));
            this.tasksAttached = true;
         }
      }

      public void removePlayer(UUID playerId) {
         this.taskSource.remove(playerId);
      }

      public void addPlayer(UUID playerId) {
         this.taskSource.add(playerId);
      }

      public int getNumberOfPlayers() {
         return this.taskSource.getUuids().size();
      }

      public boolean hasTask(String taskId) {
         return this.tasks.containsKey(taskId);
      }

      public void addTask(Task task, MinecraftServer server) {
         this.tasks.put(task.getId(), task);
         if (this.tasksAttached) {
            task.onAttach(TaskContext.of(this.taskSource, server));
         }
      }

      public static Optional<TeamTaskData.TeamTasks> readNbt(CompoundTag tag) {
         Optional<TaskSource> taskSource = Adapters.TASK_SOURCE_NBT.readNbt(tag.getCompound("TaskSource"));
         return taskSource.filter(EntityTaskSource.class::isInstance).map(source -> {
            String teamName = tag.getString("Team");
            Map<String, Task> tasks = new HashMap<>();
            ListTag taskListTag = tag.getList("Tasks", 10);
            taskListTag.forEach(taskTag -> Adapters.TASK_NBT.readNbt(taskTag).ifPresent(task -> tasks.put(task.getId(), task)));
            return new TeamTaskData.TeamTasks(teamName, (EntityTaskSource)source, tasks);
         });
      }

      public CompoundTag writeNbt() {
         CompoundTag tag = new CompoundTag();
         tag.putString("Team", this.teamName);
         Adapters.TASK_SOURCE_NBT.writeNbt((TaskSource)this.taskSource).ifPresent(taskSourceTag -> tag.put("TaskSource", taskSourceTag));
         ListTag taskListTag = new ListTag();
         this.tasks.values().forEach(task -> Adapters.TASK_NBT.writeNbt(task).ifPresent(taskListTag::add));
         tag.put("Tasks", taskListTag);
         return tag;
      }

      public void writeBytes(FriendlyByteBuf buffer) {
         ArrayBitBuffer bits = ArrayBitBuffer.empty();
         buffer.writeUtf(this.teamName);
         buffer.writeVarInt(this.tasks.size());
         this.tasks.values().forEach(task -> Adapters.TASK.writeBits(task, bits));
         Adapters.TASK_SOURCE.writeBits(this.taskSource, bits);
         buffer.writeLongArray(bits.toLongArray());
      }

      public static TeamTaskData.TeamTasks readBytes(FriendlyByteBuf buffer) {
         String teamName = buffer.readUtf();
         Map<String, Task> tasks = new HashMap<>();
         int size = buffer.readVarInt();
         BitBuffer bits = ArrayBitBuffer.backing(buffer.readLongArray(), 0);

         for (int i = 0; i < size; i++) {
            Adapters.TASK.readBits(bits).ifPresent(task -> tasks.put(task.getId(), task));
         }

         TaskSource taskSource = Adapters.TASK_SOURCE.readBits(bits).orElseThrow();
         return new TeamTaskData.TeamTasks(teamName, (EntityTaskSource)taskSource, tasks);
      }

      public String teamName() {
         return this.teamName;
      }

      public EntityTaskSource taskSource() {
         return this.taskSource;
      }

      public Map<String, Task> tasks() {
         return this.tasks;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            TeamTaskData.TeamTasks that = (TeamTaskData.TeamTasks)obj;
            return Objects.equals(this.teamName, that.teamName) && Objects.equals(this.taskSource, that.taskSource) && Objects.equals(this.tasks, that.tasks);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.teamName, this.taskSource, this.tasks);
      }

      @Override
      public String toString() {
         return "TeamTasks[teamName=" + this.teamName + ", taskSource=" + this.taskSource + ", tasks=" + this.tasks + "]";
      }

      public List<Task> getCompletedTasks() {
         return this.tasks.values().stream().filter(Task::isCompleted).toList();
      }

      public boolean removeTask(String taskId) {
         Task task = this.tasks.remove(taskId);
         if (task != null) {
            task.onDetach();
            return true;
         } else {
            return false;
         }
      }
   }
}
