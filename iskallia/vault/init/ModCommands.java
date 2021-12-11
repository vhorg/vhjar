package iskallia.vault.init;

import com.mojang.brigadier.CommandDispatcher;
import iskallia.vault.Vault;
import iskallia.vault.backup.BackupListArgument;
import iskallia.vault.command.ArchitectDirectionCommands;
import iskallia.vault.command.Command;
import iskallia.vault.command.CrystalCommand;
import iskallia.vault.command.DebugCommand;
import iskallia.vault.command.EternalCommand;
import iskallia.vault.command.GearCommand;
import iskallia.vault.command.GiveLootCommand;
import iskallia.vault.command.InternalCommand;
import iskallia.vault.command.InvRestoreCommand;
import iskallia.vault.command.PartyCommand;
import iskallia.vault.command.ReloadConfigsCommand;
import iskallia.vault.command.VaultGodSayCommand;
import iskallia.vault.command.VaultLevelCommand;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands.EnvironmentType;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class ModCommands {
   public static ReloadConfigsCommand RELOAD_CONFIGS;
   public static VaultLevelCommand VAULT_LEVEL;
   public static InternalCommand INTERNAL;
   public static DebugCommand DEBUG;
   public static InvRestoreCommand INV_RESTORE;
   public static GiveLootCommand GIVE_LOOT;
   public static VaultGodSayCommand VAULTGOD_SAY;
   public static CrystalCommand CRYSTAL;
   public static EternalCommand ETERNAL;
   public static GearCommand GEAR;
   public static PartyCommand PARTY;

   public static void registerCommands(CommandDispatcher<CommandSource> dispatcher, EnvironmentType env) {
      RELOAD_CONFIGS = registerCommand(ReloadConfigsCommand::new, dispatcher, env);
      VAULT_LEVEL = registerCommand(VaultLevelCommand::new, dispatcher, env);
      INTERNAL = registerCommand(InternalCommand::new, dispatcher, env);
      DEBUG = registerCommand(DebugCommand::new, dispatcher, env);
      INV_RESTORE = registerCommand(InvRestoreCommand::new, dispatcher, env);
      GIVE_LOOT = registerCommand(GiveLootCommand::new, dispatcher, env);
      VAULTGOD_SAY = registerCommand(VaultGodSayCommand::new, dispatcher, env);
      CRYSTAL = registerCommand(CrystalCommand::new, dispatcher, env);
      ETERNAL = registerCommand(EternalCommand::new, dispatcher, env);
      GEAR = registerCommand(GearCommand::new, dispatcher, env);
      PARTY = registerCommand(PartyCommand::new, dispatcher, env);
      ArchitectDirectionCommands.register(dispatcher);
   }

   public static void registerArgumentTypes() {
      ArgumentTypes.func_218136_a(
         Vault.id("backup_list_player").toString(), BackupListArgument.Player.class, new ArgumentSerializer(BackupListArgument.Player::new)
      );
      ArgumentTypes.func_218136_a(
         Vault.id("backup_list_uuid").toString(), BackupListArgument.UUIDRef.class, new ArgumentSerializer(BackupListArgument.UUIDRef::new)
      );
   }

   public static <T extends Command> T registerCommand(Supplier<T> supplier, CommandDispatcher<CommandSource> dispatcher, EnvironmentType env) {
      T command = (T)supplier.get();
      if (!command.isDedicatedServerOnly() || env == EnvironmentType.DEDICATED || env == EnvironmentType.ALL) {
         command.registerCommand(dispatcher);
      }

      return command;
   }
}
