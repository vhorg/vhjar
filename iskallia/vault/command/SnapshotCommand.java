package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.ArenaSnapshotData;
import iskallia.vault.world.data.TimestampedInventorySnapshotData;
import iskallia.vault.world.data.VaultDeathSnapshotData;
import iskallia.vault.world.data.VaultJoinSnapshotData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class SnapshotCommand extends Command {
   private static final String PLAYER_IGN_ARGUMENT = "playerIGN";
   private static final String TIMESTAMP_ARGUMENT = "timestamp";
   private static final String RESTORE_TO_PLAYER_ARGUMENT = "restoreToPlayer";

   @Override
   public String getName() {
      return "snapshot";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(
               ((LiteralArgumentBuilder)Commands.literal("arena").then(Commands.literal("create").executes(this::createSnapshot)))
                  .then(Commands.literal("restore").executes(this::restoreSnapshot))
            ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("death").then(this.timestampedSnapshotList(this::listDeathSnapshots)))
                  .then(this.timestampedSnapshotRestore(VaultDeathSnapshotData::get))
            ))
         .then(
            ((LiteralArgumentBuilder)Commands.literal("vaultJoin").then(this.timestampedSnapshotList(this::listJoinSnapshots)))
               .then(this.timestampedSnapshotRestore(VaultJoinSnapshotData::get))
         );
   }

   private LiteralArgumentBuilder<CommandSourceStack> timestampedSnapshotRestore(Function<ServerLevel, TimestampedInventorySnapshotData> getSnapshotData) {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("restore")
         .then(
            Commands.argument("playerIGN", StringArgumentType.word())
               .then(
                  ((RequiredArgumentBuilder)Commands.argument("timestamp", IntegerArgumentType.integer())
                        .executes(
                           context -> this.restoreTimestampedSnapshot(
                              getSnapshotData,
                              ((CommandSourceStack)context.getSource()).getPlayerOrException(),
                              StringArgumentType.getString(context, "playerIGN"),
                              IntegerArgumentType.getInteger(context, "timestamp"),
                              ((CommandSourceStack)context.getSource()).getPlayerOrException()
                           )
                        ))
                     .then(
                        Commands.argument("restoreToPlayer", EntityArgument.players())
                           .executes(
                              ctx -> this.restoreTimestampedSnapshot(
                                 getSnapshotData,
                                 ((CommandSourceStack)ctx.getSource()).getPlayerOrException(),
                                 StringArgumentType.getString(ctx, "playerIGN"),
                                 IntegerArgumentType.getInteger(ctx, "timestamp"),
                                 ((CommandSourceStack)ctx.getSource()).getPlayerOrException()
                              )
                           )
                     )
               )
         );
   }

   private LiteralArgumentBuilder<CommandSourceStack> timestampedSnapshotList(com.mojang.brigadier.Command<CommandSourceStack> listSnapshotsCommand) {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("list")
         .then(Commands.argument("playerIGN", StringArgumentType.word()).executes(listSnapshotsCommand));
   }

   private int restoreTimestampedSnapshot(
      Function<ServerLevel, TimestampedInventorySnapshotData> getSnapshotData,
      ServerPlayer serverPlayer,
      String playerIGN,
      int timestamp,
      Player restoreToPlayer
   ) {
      ServerLevel serverLevel = serverPlayer.getLevel();
      serverLevel.getServer().getProfileCache().get(playerIGN).ifPresentOrElse(gp -> {
         if (getSnapshotData.apply(serverLevel).restoreSnapshot(restoreToPlayer, gp.getId(), timestamp)) {
            serverPlayer.sendMessage(new TextComponent("Restored " + playerIGN + "'s snapshot"), Util.NIL_UUID);
         } else {
            serverPlayer.sendMessage(new TextComponent("Failed to restore " + playerIGN + "'s snapshot"), Util.NIL_UUID);
         }
      }, () -> serverPlayer.sendMessage(new TextComponent("Unable to find player's IGN: " + playerIGN).withStyle(ChatFormatting.RED), Util.NIL_UUID));
      return 0;
   }

   private int listDeathSnapshots(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return this.listSnapshotTimestamps(context, VaultDeathSnapshotData::get, "death");
   }

   private int listJoinSnapshots(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return this.listSnapshotTimestamps(context, VaultJoinSnapshotData::get, "vaultJoin");
   }

   private int listSnapshotTimestamps(
      CommandContext<CommandSourceStack> context, Function<ServerLevel, TimestampedInventorySnapshotData> getSnapshotData, String snapshotName
   ) throws CommandSyntaxException {
      String playerIGN = StringArgumentType.getString(context, "playerIGN");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerLevel serverLevel = source.getLevel();
      ServerPlayer player = source.getPlayerOrException();
      serverLevel.getServer()
         .getProfileCache()
         .get(playerIGN)
         .ifPresentOrElse(
            gp -> {
               List<Integer> timestamps = new ArrayList<>(getSnapshotData.apply(serverLevel).getSnapshotTimestamps(gp.getId()));
               if (timestamps.isEmpty()) {
                  player.sendMessage(new TextComponent("No snapshot exists for " + playerIGN).withStyle(ChatFormatting.RED), Util.NIL_UUID);
               } else {
                  timestamps.sort(Comparator.reverseOrder());
                  SimpleDateFormat dateFormat = new SimpleDateFormat();
                  source.sendSuccess(new TextComponent("Timestamps"), false);
                  timestamps.forEach(
                     timestamp -> {
                        MutableComponent message = new TextComponent("");
                        message.append(
                           new TextComponent(dateFormat.format(new Date(timestamp.intValue() * 1000L)) + " (" + timestamp + ")")
                              .withStyle(
                                 s -> s.withColor(ChatFormatting.GREEN)
                                    .withClickEvent(
                                       new ClickEvent(
                                          Action.SUGGEST_COMMAND, "/the_vault snapshot " + snapshotName + " restore " + playerIGN + " " + timestamp + " @p"
                                       )
                                    )
                                    .withHoverEvent(
                                       new HoverEvent(
                                          net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                          new TextComponent("Click to get restore command for this timestamp")
                                       )
                                    )
                              )
                        );
                        source.sendSuccess(message, false);
                     }
                  );
               }
            },
            () -> player.sendMessage(new TextComponent("Unable to find player's IGN: " + playerIGN).withStyle(ChatFormatting.RED), Util.NIL_UUID)
         );
      return 0;
   }

   private int restoreSnapshot(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ArenaSnapshotData.get(((CommandSourceStack)ctx.getSource()).getLevel()).restoreSnapshot(((CommandSourceStack)ctx.getSource()).getPlayerOrException());
      return 0;
   }

   private int createSnapshot(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ArenaSnapshotData.get(((CommandSourceStack)ctx.getSource()).getLevel()).createSnapshot(((CommandSourceStack)ctx.getSource()).getPlayerOrException());
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
