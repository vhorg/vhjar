package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerFavourData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.EnumArgument;

public class VaultGodSayCommand extends Command {
   @Override
   public String getName() {
      return "say";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.argument("sender", EnumArgument.enumArgument(PlayerFavourData.VaultGodType.class))
            .then(Commands.argument("message", MessageArgument.message()).executes(this::onSay))
      );
   }

   private int onSay(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      MinecraftServer srv = ((CommandSourceStack)ctx.getSource()).getServer();
      Component text = MessageArgument.getMessage(ctx, "message");
      PlayerFavourData.VaultGodType sender = (PlayerFavourData.VaultGodType)ctx.getArgument("sender", PlayerFavourData.VaultGodType.class);
      TextComponent senderTxt = new TextComponent("[VG] ");
      senderTxt.withStyle(ChatFormatting.DARK_PURPLE)
         .append(new TextComponent(sender.getName()).withStyle(sender.getChatColor()))
         .append(new TextComponent(": ").withStyle(ChatFormatting.WHITE));
      senderTxt.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, sender.getHoverChatComponent())));
      srv.getPlayerList().broadcastMessage(new TextComponent("").append(senderTxt).append(text), ChatType.SYSTEM, Util.NIL_UUID);
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
