package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.OpenVaultSnapshotMessage;
import iskallia.vault.world.data.VaultSnapshots;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class OpenVaultSnapshotCommand extends Command {
   @Override
   public String getName() {
      return "open_snapshot";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.literal("open")
            .then(Commands.argument("target", UuidArgument.uuid()).then(Commands.argument("asPlayer", UuidArgument.uuid()).executes(this::open)))
      );
      builder.then(Commands.literal("send").then(Commands.argument("target", UuidArgument.uuid()).executes(this::send)));
   }

   private int open(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      UUID target = UuidArgument.getUuid(ctx, "target");
      UUID asPlayer = UuidArgument.getUuid(ctx, "asPlayer");
      VaultSnapshot snapshot = VaultSnapshots.get(target);
      if (snapshot != null) {
         ModNetwork.CHANNEL.sendTo(new OpenVaultSnapshotMessage.S2C(snapshot, asPlayer), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }

      return 0;
   }

   private int send(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      UUID target = UuidArgument.getUuid(ctx, "target");
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      sendCommand(player, target, srv);
      return 0;
   }

   public static void sendCommand(ServerPlayer player, UUID target, MinecraftServer srv) {
      String openSnapshot = "/the_vault open_snapshot open " + target + " " + player.getUUID();
      String snapshotName = "Vault";
      MutableComponent acceptTxt = new TextComponent(snapshotName).withStyle(ChatFormatting.AQUA);
      acceptTxt.withStyle(
         style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent("Click to open")))
            .withClickEvent(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND, openSnapshot))
      );
      Component acceptMessage = new TextComponent("")
         .append(new TextComponent("[").withStyle(ChatFormatting.GREEN))
         .append(acceptTxt)
         .append(new TextComponent("]").withStyle(ChatFormatting.GREEN));
      srv.getPlayerList()
         .broadcastMessage(new TranslatableComponent("chat.type.text", new Object[]{player.getDisplayName(), acceptMessage}), ChatType.CHAT, Util.NIL_UUID);
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
