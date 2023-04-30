package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.quest.QuestDebugModeMessage;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

public class QuestCommand extends Command {
   private static final SuggestionProvider<CommandSourceStack> TRUE_FALSE = (context, builder) -> SharedSuggestionProvider.suggest(
      List.of("true", "false"), builder
   );

   @Override
   public String getName() {
      return "quests";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("progress").then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(this::addProgress)));
      builder.then(Commands.literal("debug").then(Commands.argument("enabled", StringArgumentType.word()).suggests(TRUE_FALSE).executes(this::setDebugMode)));
      builder.then(Commands.literal("reset").executes(this::reset));
   }

   private int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      QuestStatesData.get().getState(player).reset();
      return 1;
   }

   private int setDebugMode(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      boolean enabled = Boolean.parseBoolean(StringArgumentType.getString(context, "enabled"));
      ModNetwork.CHANNEL.sendTo(new QuestDebugModeMessage(enabled), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
      return 1;
   }

   private int addProgress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      QuestState state = QuestStatesData.get().getState(player);
      String inProgress = (String)state.getInProgress().stream().findFirst().orElseThrow();
      Quest quest = state.<QuestConfig>getConfig(player.getLevel()).getQuestById(inProgress);
      state.addProgress(quest, IntegerArgumentType.getInteger(context, "amount"));
      return 1;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
