package iskallia.vault.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public abstract class Command {
   public abstract String getName();

   public abstract int getRequiredPermissionLevel();

   public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(this.getName());
      builder.requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()));
      this.build(builder);
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("the_vault").then(builder));
   }

   public abstract void build(LiteralArgumentBuilder<CommandSourceStack> var1);

   public abstract boolean isDedicatedServerOnly();

   protected final void sendFeedback(CommandContext<CommandSourceStack> context, String message, boolean showOps) {
      ((CommandSourceStack)context.getSource()).sendSuccess(new TextComponent(message), showOps);
   }
}
