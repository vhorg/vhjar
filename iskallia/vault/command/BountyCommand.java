package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.BountyData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BountyCommand extends Command {
   @Override
   public String getName() {
      return "bounty";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("reroll").executes(this::rerollBounties));
   }

   private int rerollBounties(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      BountyData.get().resetAllBounties(((CommandSourceStack)context.getSource()).getPlayerOrException().getUUID());
      return 1;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
