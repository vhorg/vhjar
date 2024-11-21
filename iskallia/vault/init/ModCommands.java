package iskallia.vault.init;

import com.mojang.brigadier.CommandDispatcher;
import iskallia.vault.VaultMod;
import iskallia.vault.backup.BackupListArgument;
import iskallia.vault.command.AbilityCommand;
import iskallia.vault.command.AliasCommand;
import iskallia.vault.command.BountyCommand;
import iskallia.vault.command.Command;
import iskallia.vault.command.CrystalCommand;
import iskallia.vault.command.DebugCommand;
import iskallia.vault.command.DifficultyCommand;
import iskallia.vault.command.EternalCommand;
import iskallia.vault.command.GearDebugCommand;
import iskallia.vault.command.GiveLootCommand;
import iskallia.vault.command.InternalCommand;
import iskallia.vault.command.MagnetCommand;
import iskallia.vault.command.ModelDebugCommand;
import iskallia.vault.command.OpenVaultSnapshotCommand;
import iskallia.vault.command.PartyCommand;
import iskallia.vault.command.PlayerDifficultyCommand;
import iskallia.vault.command.PointsResetCommand;
import iskallia.vault.command.QuestCommand;
import iskallia.vault.command.ReloadConfigsCommand;
import iskallia.vault.command.ResearchTeamCommand;
import iskallia.vault.command.ResetCommand;
import iskallia.vault.command.SnapshotCommand;
import iskallia.vault.command.SpiritCommand;
import iskallia.vault.command.TeamTasksCommand;
import iskallia.vault.command.VaultAltarCommand;
import iskallia.vault.command.VaultGodSayCommand;
import iskallia.vault.command.VaultLevelCommand;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;

public class ModCommands {
   public static ReloadConfigsCommand RELOAD_CONFIGS;
   public static VaultLevelCommand VAULT_LEVEL;
   public static InternalCommand INTERNAL;
   public static DebugCommand DEBUG;
   public static ModelDebugCommand MODEL_DEBUG;
   public static AbilityCommand ABILITY_COMMAND;
   public static GiveLootCommand GIVE_LOOT;
   public static VaultGodSayCommand VAULTGOD_SAY;
   public static AliasCommand ALIAS;
   public static CrystalCommand CRYSTAL;
   public static EternalCommand ETERNAL;
   public static GearDebugCommand GEAR_DEBUG;
   public static MagnetCommand MAGNET;
   public static ResearchTeamCommand RESEARCH_TEAM;
   public static PartyCommand PARTY;
   public static SpiritCommand SPIRIT;
   public static SnapshotCommand SNAPSHOT;
   public static BountyCommand BOUNTY;
   public static DifficultyCommand DIFFICULTY;
   public static PlayerDifficultyCommand PLAYER_DIFFICULTY;
   public static VaultAltarCommand VAULT_ALTAR;
   public static PointsResetCommand POINTS_RESET;
   public static OpenVaultSnapshotCommand OPEN_VAULT_SNAPSHOT;
   public static QuestCommand QUEST_COMMAND;
   public static ResetCommand RESET_COMMAND;
   public static TeamTasksCommand TEAM_TASKS_COMMAND;

   public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandSelection env) {
      RELOAD_CONFIGS = registerCommand(ReloadConfigsCommand::new, dispatcher, env);
      VAULT_LEVEL = registerCommand(VaultLevelCommand::new, dispatcher, env);
      INTERNAL = registerCommand(InternalCommand::new, dispatcher, env);
      DEBUG = registerCommand(DebugCommand::new, dispatcher, env);
      MODEL_DEBUG = registerCommand(ModelDebugCommand::new, dispatcher, env);
      ABILITY_COMMAND = registerCommand(AbilityCommand::new, dispatcher, env);
      GIVE_LOOT = registerCommand(GiveLootCommand::new, dispatcher, env);
      VAULTGOD_SAY = registerCommand(VaultGodSayCommand::new, dispatcher, env);
      ALIAS = registerCommand(AliasCommand::new, dispatcher, env);
      CRYSTAL = registerCommand(CrystalCommand::new, dispatcher, env);
      ETERNAL = registerCommand(EternalCommand::new, dispatcher, env);
      GEAR_DEBUG = registerCommand(GearDebugCommand::new, dispatcher, env);
      MAGNET = registerCommand(MagnetCommand::new, dispatcher, env);
      RESEARCH_TEAM = registerCommand(ResearchTeamCommand::new, dispatcher, env);
      PARTY = registerCommand(PartyCommand::new, dispatcher, env);
      SPIRIT = registerCommand(SpiritCommand::new, dispatcher, env);
      SNAPSHOT = registerCommand(SnapshotCommand::new, dispatcher, env);
      DIFFICULTY = registerCommand(DifficultyCommand::new, dispatcher, env);
      PLAYER_DIFFICULTY = registerCommand(PlayerDifficultyCommand::new, dispatcher, env);
      BOUNTY = registerCommand(BountyCommand::new, dispatcher, env);
      VAULT_ALTAR = registerCommand(VaultAltarCommand::new, dispatcher, env);
      POINTS_RESET = registerCommand(PointsResetCommand::new, dispatcher, env);
      OPEN_VAULT_SNAPSHOT = registerCommand(OpenVaultSnapshotCommand::new, dispatcher, env);
      QUEST_COMMAND = registerCommand(QuestCommand::new, dispatcher, env);
      RESET_COMMAND = registerCommand(ResetCommand::new, dispatcher, env);
      TEAM_TASKS_COMMAND = registerCommand(TeamTasksCommand::new, dispatcher, env);
   }

   public static void registerArgumentTypes() {
      ArgumentTypes.register(
         VaultMod.id("backup_list_player").toString(), BackupListArgument.Player.class, new EmptyArgumentSerializer(BackupListArgument.Player::new)
      );
      ArgumentTypes.register(
         VaultMod.id("backup_list_uuid").toString(), BackupListArgument.UUIDRef.class, new EmptyArgumentSerializer(BackupListArgument.UUIDRef::new)
      );
   }

   public static <T extends Command> T registerCommand(Supplier<T> supplier, CommandDispatcher<CommandSourceStack> dispatcher, CommandSelection env) {
      T command = (T)supplier.get();
      if (!command.isDedicatedServerOnly() || env == CommandSelection.DEDICATED || env == CommandSelection.ALL) {
         command.registerCommand(dispatcher);
      }

      return command;
   }
}
