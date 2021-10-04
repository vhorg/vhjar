package iskallia.vault.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public abstract class Command {
   public abstract String getName();

   public abstract int getRequiredPermissionLevel();

   public void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> builder = Commands.func_197057_a(this.getName());
      builder.requires(sender -> sender.func_197034_c(this.getRequiredPermissionLevel()));
      this.build(builder);
      dispatcher.register((LiteralArgumentBuilder)Commands.func_197057_a("the_vault").then(builder));
   }

   public abstract void build(LiteralArgumentBuilder<CommandSource> var1);

   public abstract boolean isDedicatedServerOnly();

   protected final void sendFeedback(CommandContext<CommandSource> context, String message, boolean showOps) {
      ((CommandSource)context.getSource()).func_197030_a(new StringTextComponent(message), showOps);
   }
}
