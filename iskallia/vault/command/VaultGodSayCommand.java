package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerFavourData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(
         Commands.func_197056_a("sender", EnumArgument.enumArgument(PlayerFavourData.VaultGodType.class))
            .then(Commands.func_197056_a("message", MessageArgument.func_197123_a()).executes(this::onSay))
      );
   }

   private int onSay(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      MinecraftServer srv = ((CommandSource)ctx.getSource()).func_197028_i();
      ITextComponent text = MessageArgument.func_197124_a(ctx, "message");
      PlayerFavourData.VaultGodType sender = (PlayerFavourData.VaultGodType)ctx.getArgument("sender", PlayerFavourData.VaultGodType.class);
      StringTextComponent senderTxt = new StringTextComponent("[VG] ");
      senderTxt.func_240699_a_(TextFormatting.DARK_PURPLE)
         .func_230529_a_(new StringTextComponent(sender.getName()).func_240699_a_(sender.getChatColor()))
         .func_230529_a_(new StringTextComponent(": ").func_240699_a_(TextFormatting.WHITE));
      senderTxt.func_240700_a_(style -> style.func_240716_a_(new HoverEvent(Action.field_230550_a_, sender.getHoverChatComponent())));
      srv.func_184103_al().func_232641_a_(new StringTextComponent("").func_230529_a_(senderTxt).func_230529_a_(text), ChatType.SYSTEM, Util.field_240973_b_);
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
