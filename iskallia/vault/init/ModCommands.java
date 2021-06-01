package iskallia.vault.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import iskallia.vault.command.Command;
import iskallia.vault.command.GearDebugCommand;
import iskallia.vault.command.GiveBitsCommand;
import iskallia.vault.command.InternalCommand;
import iskallia.vault.command.RaidCommand;
import iskallia.vault.command.ReloadConfigsCommand;
import iskallia.vault.command.ResetTraderCommand;
import iskallia.vault.command.VaultLevelCommand;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.Commands.EnvironmentType;

public class ModCommands {
   public static ReloadConfigsCommand RELOAD_CONFIGS;
   public static RaidCommand RAID;
   public static VaultLevelCommand VAULT_LEVEL;
   public static InternalCommand INTERNAL;
   public static GiveBitsCommand GIVE_BITS;
   public static GearDebugCommand GEAR_DEBUG_COMMAND;
   public static ResetTraderCommand RESET_TRADER_COMMAND;

   public static void registerCommands(CommandDispatcher<CommandSource> dispatcher, EnvironmentType env) {
      RELOAD_CONFIGS = registerCommand(ReloadConfigsCommand::new, dispatcher, env);
      RAID = registerCommand(RaidCommand::new, dispatcher, env);
      VAULT_LEVEL = registerCommand(VaultLevelCommand::new, dispatcher, env);
      INTERNAL = registerCommand(InternalCommand::new, dispatcher, env);
      GIVE_BITS = registerCommand(GiveBitsCommand::new, dispatcher, env);
      GEAR_DEBUG_COMMAND = registerCommand(GearDebugCommand::new, dispatcher, env);
      RESET_TRADER_COMMAND = registerCommand(ResetTraderCommand::new, dispatcher, env);
   }

   public static <T extends Command> T registerCommand(Supplier<T> supplier, CommandDispatcher<CommandSource> dispatcher, EnvironmentType env) {
      T command = (T)supplier.get();
      if (!command.isDedicatedServerOnly() || env == EnvironmentType.DEDICATED || env == EnvironmentType.ALL) {
         LiteralArgumentBuilder<CommandSource> builder = Commands.func_197057_a(command.getName());
         builder.requires(sender -> sender.func_197034_c(command.getRequiredPermissionLevel()));
         command.build(builder);
         dispatcher.register((LiteralArgumentBuilder)Commands.func_197057_a("the_vault").then(builder));
      }

      return command;
   }
}
