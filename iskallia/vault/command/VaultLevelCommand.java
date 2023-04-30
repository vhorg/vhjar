package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class VaultLevelCommand extends Command {
   @Override
   public String getName() {
      return "vault_level";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("add_exp").then(Commands.argument("exp", IntegerArgumentType.integer()).executes(this::addExp)));
      builder.then(Commands.literal("set_level").then(Commands.argument("level", IntegerArgumentType.integer()).executes(this::setLevel)));
      builder.then(Commands.literal("add_skill_points").then(Commands.argument("amount", IntegerArgumentType.integer()).executes(this::addSkillPoints)));
      builder.then(Commands.literal("add_expertise_points").then(Commands.argument("amount", IntegerArgumentType.integer()).executes(this::addExpertisePoints)));
   }

   private int setLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      int level = IntegerArgumentType.getInteger(context, "level");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerPlayer player = source.getPlayerOrException();
      PlayerVaultStatsData.get(source.getLevel()).setVaultLevel(player, level);
      player.refreshTabListName();
      return 0;
   }

   private int addExp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      int exp = IntegerArgumentType.getInteger(context, "exp");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).addVaultExp(source.getPlayerOrException(), exp);
      return 0;
   }

   private int addSkillPoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      int amount = IntegerArgumentType.getInteger(context, "amount");
      PlayerVaultStatsData.get(source.getLevel()).addSkillPoints(source.getPlayerOrException(), amount);
      return 0;
   }

   private int addExpertisePoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      int amount = IntegerArgumentType.getInteger(context, "amount");
      PlayerVaultStatsData.get(source.getLevel()).addExpertisePoints(source.getPlayerOrException(), amount);
      return 0;
   }
}
