package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

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
      builder.then(Commands.literal("reset_all").executes(this::resetAll));
   }

   private int setLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      int level = IntegerArgumentType.getInteger(context, "level");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).setVaultLevel(source.getPlayerOrException(), level);
      return 0;
   }

   private int addExp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      int exp = IntegerArgumentType.getInteger(context, "exp");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).addVaultExp(source.getPlayerOrException(), exp);
      return 0;
   }

   private int resetAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).reset(source.getPlayerOrException());
      PlayerAbilitiesData.get(source.getLevel()).resetAbilityTree(source.getPlayerOrException());
      PlayerTalentsData.get(source.getLevel()).resetTalentTree(source.getPlayerOrException());
      PlayerResearchesData.get(source.getLevel()).resetResearchTree(source.getPlayerOrException());
      return 0;
   }
}
