package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(Commands.func_197057_a("add_exp").then(Commands.func_197056_a("exp", IntegerArgumentType.integer()).executes(this::addExp)));
      builder.then(Commands.func_197057_a("set_level").then(Commands.func_197056_a("level", IntegerArgumentType.integer()).executes(this::setLevel)));
      builder.then(Commands.func_197057_a("reset_all").executes(this::resetAll));
   }

   private int setLevel(CommandContext<CommandSource> context) throws CommandSyntaxException {
      int level = IntegerArgumentType.getInteger(context, "level");
      CommandSource source = (CommandSource)context.getSource();
      PlayerVaultStatsData.get(source.func_197023_e()).setVaultLevel(source.func_197035_h(), level);
      return 0;
   }

   private int addExp(CommandContext<CommandSource> context) throws CommandSyntaxException {
      int exp = IntegerArgumentType.getInteger(context, "exp");
      CommandSource source = (CommandSource)context.getSource();
      PlayerVaultStatsData.get(source.func_197023_e()).addVaultExp(source.func_197035_h(), exp);
      return 0;
   }

   private int resetAll(CommandContext<CommandSource> context) throws CommandSyntaxException {
      CommandSource source = (CommandSource)context.getSource();
      PlayerVaultStatsData.get(source.func_197023_e()).reset(source.func_197035_h());
      PlayerAbilitiesData.get(source.func_197023_e()).resetAbilityTree(source.func_197035_h());
      PlayerTalentsData.get(source.func_197023_e()).resetTalentTree(source.func_197035_h());
      PlayerResearchesData.get(source.func_197023_e()).resetResearchTree(source.func_197035_h());
      return 0;
   }
}
