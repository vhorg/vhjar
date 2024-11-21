package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import iskallia.vault.core.vault.TeamTaskManager;
import iskallia.vault.world.data.TeamTaskData;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

public class TeamTasksCommand extends Command {
   public static final SuggestionProvider<CommandSourceStack> ASSIGNED_TASKIDS = (context, builder) -> {
      TeamTaskData.get(((CommandSourceStack)context.getSource()).getServer()).streamAssignedTaskIds().sorted().forEach(builder::suggest);
      return builder.buildFuture();
   };
   private static final SuggestionProvider<CommandSourceStack> TEAM_NAMES = (context, builder) -> {
      TeamTaskData.get(((CommandSourceStack)context.getSource()).getServer()).getTeamNames().stream().sorted().forEach(builder::suggest);
      return builder.buildFuture();
   };

   @Override
   public String getName() {
      return "team_tasks";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("reset_tasks").executes(this::resetTasks));
      builder.then(
         Commands.literal("refresh_task").then(Commands.argument("taskId", StringArgumentType.string()).suggests(ASSIGNED_TASKIDS).executes(this::refreshTask))
      );
      builder.then(
         Commands.literal("set_task_progress")
            .then(
               ((RequiredArgumentBuilder)Commands.argument("teamName", StringArgumentType.string())
                     .suggests(TEAM_NAMES)
                     .then(
                        Commands.argument("taskId", StringArgumentType.string())
                           .suggests(ASSIGNED_TASKIDS)
                           .then(Commands.argument("progress", IntegerArgumentType.integer()).executes(this::setTaskProgress))
                     ))
                  .executes(this::refreshTask)
            )
      );
      builder.then(
         Commands.literal("get_task_progress")
            .then(Commands.argument("taskId", StringArgumentType.string()).suggests(ASSIGNED_TASKIDS).executes(this::getTaskProgress))
      );
   }

   private int getTaskProgress(CommandContext<CommandSourceStack> ctx) {
      MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
      String taskId = StringArgumentType.getString(ctx, "taskId");
      TeamTaskData teamTaskData = TeamTaskData.get(server);
      Map<String, Number> taskProgress = teamTaskData.getTaskProgress(taskId);
      if (!taskProgress.isEmpty()) {
         ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent("Task progress for " + taskId), false);
         taskProgress.forEach((teamName, progress) -> ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent(teamName + ": " + progress), false));
      } else {
         ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent("Task not found"), false);
      }

      return 0;
   }

   private int setTaskProgress(CommandContext<CommandSourceStack> ctx) {
      MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
      String teamName = StringArgumentType.getString(ctx, "teamName");
      String taskId = StringArgumentType.getString(ctx, "taskId");
      int progress = IntegerArgumentType.getInteger(ctx, "progress");
      TeamTaskData teamTaskData = TeamTaskData.get(server);
      if (teamTaskData.setTaskProgress(teamName, taskId, progress, server)) {
         ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent("Progress set"), false);
      } else {
         ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent("Task not found"), false);
      }

      return 0;
   }

   private int refreshTask(CommandContext<CommandSourceStack> ctx) {
      MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
      String taskId = StringArgumentType.getString(ctx, "taskId");
      if (TeamTaskManager.refreshTask(server, taskId)) {
         ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent("Task refreshed"), false);
      } else {
         ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent("Task not found"), false);
      }

      return 0;
   }

   private int resetTasks(CommandContext<CommandSourceStack> ctx) {
      TeamTaskManager.resetTasks(((CommandSourceStack)ctx.getSource()).getServer());
      ((CommandSourceStack)ctx.getSource()).sendSuccess(new TextComponent("Tasks reset for all teams"), false);
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
