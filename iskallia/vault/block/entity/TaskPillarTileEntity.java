package iskallia.vault.block.entity;

import iskallia.vault.block.ICollectionTileEntity;
import iskallia.vault.core.vault.TeamTaskManager;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.task.Task;
import iskallia.vault.world.data.TeamTaskData;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

public class TaskPillarTileEntity extends BlockEntity implements ICollectionTileEntity {
   private static final int COMPLETION_CHECK_INTERVAL = 5;
   private long lastCompletionCheckTime = 0L;
   private boolean checkedForReclaimTaskId = false;
   private int lightsTintColor = -12303292;
   @Nullable
   private String taskId = null;
   @Nullable
   private String reclaimTaskId = null;
   private boolean replacedBlocksOnCompletion = false;
   @Nullable
   private String completedByTeam = null;

   public TaskPillarTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.TASK_PILLAR_TILE_ENTITY, pWorldPosition, pBlockState);
   }

   public void onLoad() {
      super.onLoad();
      if (this.level != null && !this.level.isClientSide()) {
         TeamTaskManager.addTaskEventListener(
            this,
            new TeamTaskManager.TaskEventListener() {
               @Override
               public void onTaskRefresh(String taskId) {
                  if (this.isTaskOrAReclaimChild(taskId) && TaskPillarTileEntity.this.level instanceof ServerLevel serverLevel) {
                     if (!taskId.equals(TaskPillarTileEntity.this.taskId)) {
                        TaskPillarTileEntity.this.taskId = taskId;
                        TaskPillarTileEntity.this.checkedForReclaimTaskId = false;
                     }

                     TeamTaskData teamTaskData = TeamTaskData.get(serverLevel.getServer());
                     String newCompletedByTeam = teamTaskData.getCompletedTaskTeam(taskId).orElse(null);
                     if (TaskPillarTileEntity.this.completedByTeam != null && newCompletedByTeam == null) {
                        TaskPillarTileEntity.this.completedByTeam = null;
                        TaskPillarTileEntity.this.lightsTintColor = -12303292;
                        TaskPillarTileEntity.placeBlocksBelow(serverLevel, TaskPillarTileEntity.this.getBlockPos(), Blocks.STONE.defaultBlockState());
                        TaskPillarTileEntity.this.setChanged();
                        TaskPillarTileEntity.this.level
                           .sendBlockUpdated(
                              TaskPillarTileEntity.this.getBlockPos(), TaskPillarTileEntity.this.getBlockState(), TaskPillarTileEntity.this.getBlockState(), 3
                           );
                     } else if (TaskPillarTileEntity.this.completedByTeam != null && !TaskPillarTileEntity.this.completedByTeam.equals(newCompletedByTeam)) {
                        TaskPillarTileEntity.this.completedByTeam = newCompletedByTeam;
                        TaskPillarTileEntity.this.replacedBlocksOnCompletion = false;
                     }
                  }
               }

               private boolean isTaskOrAReclaimChild(String taskId) {
                  return TaskPillarTileEntity.this.taskId == null
                     ? false
                     : TaskPillarTileEntity.this.taskId.equals(taskId)
                        || ModConfigs.TEAM_TASKS.getNextTask(taskId).map(t -> this.isTaskOrAReclaimChild(t.getId())).orElse(false);
               }

               @Override
               public void onConfigLoad() {
                  TaskPillarTileEntity.this.checkedForReclaimTaskId = false;
               }
            }
         );
      }
   }

   public void setRemoved() {
      super.setRemoved();
      if (this.level != null && !this.level.isClientSide()) {
         TeamTaskManager.removeTaskEventListener(this);
      }
   }

   public void onChunkUnloaded() {
      super.onChunkUnloaded();
      if (this.level != null && !this.level.isClientSide()) {
         TeamTaskManager.removeTaskEventListener(this);
      }
   }

   public void setTaskId(String taskId) {
      this.taskId = taskId;
      this.setChanged();
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
   }

   public Optional<Task> getTask(ServerPlayer serverPlayer) {
      if (serverPlayer.getServer() == null) {
         return Optional.empty();
      } else {
         TeamTaskData teamTaskData = TeamTaskData.get(serverPlayer.getServer());
         return this.getTask(serverPlayer, teamTaskData, serverPlayer.getServer().getScoreboard());
      }
   }

   public Optional<Task> getTask(Player player, TeamTaskData teamTaskData, Scoreboard scoreboard) {
      PlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getGameProfile().getName());
      if (playerTeam == null) {
         return Optional.empty();
      } else {
         TeamTaskData.TeamTasks teamTasks = teamTaskData.getTeamTasks(teamTaskData.getCompletedTaskTeam(this.taskId).orElse(playerTeam.getName()));
         return teamTasks != null ? teamTasks.getTask(this.taskId) : Optional.empty();
      }
   }

   public Optional<Task> getReclaimTask(Player player, TeamTaskData teamTaskData, Scoreboard scoreboard) {
      if (this.reclaimTaskId == null) {
         return Optional.empty();
      } else {
         PlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getGameProfile().getName());
         if (playerTeam == null) {
            return Optional.empty();
         } else {
            TeamTaskData.TeamTasks teamTasks = teamTaskData.getTeamTasks(playerTeam.getName());
            return teamTasks != null ? teamTasks.getTask(this.reclaimTaskId) : Optional.empty();
         }
      }
   }

   public CompoundTag getUpdateTag() {
      CompoundTag updateTag = super.getUpdateTag();
      this.saveData(updateTag);
      return updateTag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      this.saveData(tag);
   }

   private void saveData(CompoundTag tag) {
      if (this.taskId != null) {
         tag.putString("TaskId", this.taskId);
      }

      if (this.reclaimTaskId != null) {
         tag.putString("ReclaimTaskId", this.reclaimTaskId);
      }

      if (this.completedByTeam != null) {
         tag.putString("CompletedByTeam", this.completedByTeam);
      }

      tag.putBoolean("ReplacedBlocksOnCompletion", this.replacedBlocksOnCompletion);
      tag.putInt("TintColor", this.lightsTintColor);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.taskId = tag.getString("TaskId");
      this.reclaimTaskId = tag.contains("ReclaimTaskId") ? tag.getString("ReclaimTaskId") : null;
      this.completedByTeam = tag.contains("CompletedByTeam") ? tag.getString("CompletedByTeam") : null;
      this.replacedBlocksOnCompletion = tag.getBoolean("ReplacedBlocksOnCompletion");
      this.lightsTintColor = tag.getInt("TintColor");
      if (this.level != null) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   @Override
   public boolean isForTask(String taskId) {
      return this.taskId != null && this.taskId.equals(taskId) || this.reclaimTaskId != null && this.reclaimTaskId.equals(taskId);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, TaskPillarTileEntity tile) {
      if (tile.taskId != null && tile.lastCompletionCheckTime + 5L <= level.getGameTime() && level instanceof ServerLevel serverLevel) {
         tile.lastCompletionCheckTime = level.getGameTime();
         if (tile.completedByTeam != null) {
            TeamTaskData teamTaskData = TeamTaskData.get(serverLevel.getServer());
            teamTaskData.getCompletedTaskTeam(tile.reclaimTaskId).ifPresent(teamName -> {
               tile.completedByTeam = teamName;
               tile.taskId = tile.reclaimTaskId;
               tile.reclaimTaskId = null;
               tile.checkedForReclaimTaskId = false;
               tile.replacedBlocksOnCompletion = false;
            });
         } else {
            TeamTaskData teamTaskData = TeamTaskData.get(serverLevel.getServer());
            teamTaskData.getCompletedTaskTeam(tile.taskId).ifPresent(s -> tile.completedByTeam = s);
            tile.replacedBlocksOnCompletion = false;
         }

         if (!tile.checkedForReclaimTaskId) {
            tile.checkedForReclaimTaskId = true;
            ModConfigs.TEAM_TASKS.getNextTask(tile.taskId).ifPresentOrElse(nextTask -> {
               tile.reclaimTaskId = nextTask.getId();
               tile.setChanged();
               level.sendBlockUpdated(pos, state, state, 3);
            }, () -> {
               tile.reclaimTaskId = null;
               tile.setChanged();
               level.sendBlockUpdated(pos, state, state, 3);
            });
         }

         if (!tile.replacedBlocksOnCompletion) {
            placeBlocks(level, pos, state, tile, serverLevel);
         }
      }
   }

   private static void placeBlocks(Level level, BlockPos pos, BlockState state, TaskPillarTileEntity tile, ServerLevel serverLevel) {
      if (tile.completedByTeam != null) {
         Scoreboard scoreboard = serverLevel.getScoreboard();
         PlayerTeam team = scoreboard.getPlayerTeam(tile.completedByTeam);
         if (team != null) {
            placeBlocksBelow(level, pos, getBlockFromTeamColor(team.getColor()).defaultBlockState());
            tile.lightsTintColor = team.getColor().getColor() != null ? darkenColor(team.getColor().getColor()) : -1;
            tile.replacedBlocksOnCompletion = true;
            tile.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
         }
      }
   }

   private static void placeBlocksBelow(Level level, BlockPos pos, BlockState stateToPlace) {
      BlockPos firstCorner = pos.below().relative(Axis.X, -1).relative(Axis.Z, -1);
      BlockPos secondCorner = firstCorner.relative(Axis.X, 2).relative(Axis.Z, 2);
      BlockPos.betweenClosed(firstCorner, secondCorner).forEach(p -> level.setBlock(p, stateToPlace, 3));
   }

   private static int darkenColor(Integer color) {
      float ratio = 0.8F;
      int r = Math.min((int)Math.min((color >> 16 & 0xFF) * ratio, 255.0F), 255);
      int g = Math.min((int)Math.min((color >> 8 & 0xFF) * ratio, 255.0F), 255);
      int b = Math.min((int)Math.min((color & 0xFF) * ratio, 255.0F), 255);
      return r << 16 | g << 8 | b;
   }

   private static Block getBlockFromTeamColor(ChatFormatting color) {
      return switch (color) {
         case WHITE -> Blocks.WHITE_CONCRETE;
         case BLACK -> Blocks.BLACK_CONCRETE;
         case DARK_PURPLE -> Blocks.PURPLE_CONCRETE;
         case BLUE -> Blocks.LIGHT_BLUE_TERRACOTTA;
         case DARK_BLUE -> Blocks.BLUE_CONCRETE;
         case YELLOW -> Blocks.YELLOW_CONCRETE;
         case GREEN -> Blocks.LIME_CONCRETE;
         case DARK_GRAY -> Blocks.GRAY_CONCRETE;
         case GRAY -> Blocks.LIGHT_GRAY_CONCRETE;
         case AQUA -> Blocks.LIGHT_BLUE_CONCRETE;
         case DARK_AQUA -> Blocks.CYAN_CONCRETE;
         case LIGHT_PURPLE -> Blocks.PINK_CONCRETE;
         case DARK_GREEN -> Blocks.GREEN_CONCRETE;
         case RED -> Blocks.PINK_TERRACOTTA;
         case DARK_RED -> Blocks.RED_CONCRETE;
         default -> Blocks.QUARTZ_BLOCK;
      };
   }

   public int getTintColor(int tintIndex) {
      return tintIndex == 1 ? this.lightsTintColor : -1;
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.getBlockPos(), this.getBlockPos().offset(1, 2, 1));
   }

   public String getTaskId() {
      return this.taskId;
   }

   public boolean hasReclaimTaskId() {
      return this.reclaimTaskId != null;
   }
}
