package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AbilityCommand extends Command {
   @Override
   public String getName() {
      return "ability";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("reset_specializations").executes(this::resetSpecializations));
   }

   private int resetSpecializations(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
