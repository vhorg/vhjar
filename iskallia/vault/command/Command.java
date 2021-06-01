package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;

public abstract class Command {
   public abstract String getName();

   public abstract int getRequiredPermissionLevel();

   public abstract void build(LiteralArgumentBuilder<CommandSource> var1);

   public abstract boolean isDedicatedServerOnly();

   protected final void sendFeedback(CommandContext<CommandSource> context, String message, boolean showOps) {
      ((CommandSource)context.getSource()).func_197030_a(new StringTextComponent(message), showOps);
   }
}
