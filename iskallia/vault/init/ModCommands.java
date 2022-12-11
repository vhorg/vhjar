package iskallia.vault.init;

import com.mojang.brigadier.CommandDispatcher;
import iskallia.vault.VaultMod;
import iskallia.vault.backup.BackupListArgument;
import iskallia.vault.command.AbilityCommand;
import iskallia.vault.command.AliasCommand;
import iskallia.vault.command.ArchitectCommand;
import iskallia.vault.command.BountyCommand;
import iskallia.vault.command.Command;
import iskallia.vault.command.CrystalCommand;
import iskallia.vault.command.DebugCommand;
import iskallia.vault.command.DifficultyCommand;
import iskallia.vault.command.EternalCommand;
import iskallia.vault.command.EventCommand;
import iskallia.vault.command.GearDebugCommand;
import iskallia.vault.command.GiveLootCommand;
import iskallia.vault.command.GlobalTimerCommand;
import iskallia.vault.command.InternalCommand;
import iskallia.vault.command.MagnetCommand;
import iskallia.vault.command.ModelDebugCommand;
import iskallia.vault.command.PartyCommand;
import iskallia.vault.command.PaxelCommand;
import iskallia.vault.command.RaidCommand;
import iskallia.vault.command.ReloadConfigsCommand;
import iskallia.vault.command.ResearchTeamCommand;
import iskallia.vault.command.SandEventCommand;
import iskallia.vault.command.SnapshotCommand;
import iskallia.vault.command.SpiritCommand;
import iskallia.vault.command.TeamScoreCommand;
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
   public static GlobalTimerCommand GLOBAL_TIMER;
   public static RaidCommand RAID;
   public static VaultLevelCommand VAULT_LEVEL;
   public static InternalCommand INTERNAL;
   public static DebugCommand DEBUG;
   public static ModelDebugCommand MODEL_DEBUG;
   public static AbilityCommand ABILITY_COMMAND;
   public static GiveLootCommand GIVE_LOOT;
   public static VaultGodSayCommand VAULTGOD_SAY;
   public static SandEventCommand SAND_EVENT;
   public static AliasCommand ALIAS;
   public static ArchitectCommand ARCHITECT;
   public static TeamScoreCommand SCORES;
   public static CrystalCommand CRYSTAL;
   public static EternalCommand ETERNAL;
   public static EventCommand EVENT;
   public static GearDebugCommand GEAR_DEBUG;
   public static MagnetCommand MAGNET;
   public static PaxelCommand PAXEL;
   public static ResearchTeamCommand RESEARCH_TEAM;
   public static PartyCommand PARTY;
   public static SpiritCommand SPIRIT;
   public static SnapshotCommand SNAPSHOT;
   public static BountyCommand BOUNTY;
   public static DifficultyCommand DIFFICULTY;
   public static VaultAltarCommand VAULT_ALTAR;

   public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandSelection env) {
      RELOAD_CONFIGS = registerCommand(ReloadConfigsCommand::new, dispatcher, env);
      GLOBAL_TIMER = registerCommand(GlobalTimerCommand::new, dispatcher, env);
      RAID = registerCommand(RaidCommand::new, dispatcher, env);
      VAULT_LEVEL = registerCommand(VaultLevelCommand::new, dispatcher, env);
      INTERNAL = registerCommand(InternalCommand::new, dispatcher, env);
      DEBUG = registerCommand(DebugCommand::new, dispatcher, env);
      MODEL_DEBUG = registerCommand(ModelDebugCommand::new, dispatcher, env);
      ABILITY_COMMAND = registerCommand(AbilityCommand::new, dispatcher, env);
      GIVE_LOOT = registerCommand(GiveLootCommand::new, dispatcher, env);
      VAULTGOD_SAY = registerCommand(VaultGodSayCommand::new, dispatcher, env);
      SAND_EVENT = registerCommand(SandEventCommand::new, dispatcher, env);
      ALIAS = registerCommand(AliasCommand::new, dispatcher, env);
      ARCHITECT = registerCommand(ArchitectCommand::new, dispatcher, env);
      SCORES = registerCommand(TeamScoreCommand::new, dispatcher, env);
      CRYSTAL = registerCommand(CrystalCommand::new, dispatcher, env);
      ETERNAL = registerCommand(EternalCommand::new, dispatcher, env);
      EVENT = registerCommand(EventCommand::new, dispatcher, env);
      GEAR_DEBUG = registerCommand(GearDebugCommand::new, dispatcher, env);
      MAGNET = registerCommand(MagnetCommand::new, dispatcher, env);
      PAXEL = registerCommand(PaxelCommand::new, dispatcher, env);
      RESEARCH_TEAM = registerCommand(ResearchTeamCommand::new, dispatcher, env);
      PARTY = registerCommand(PartyCommand::new, dispatcher, env);
      SPIRIT = registerCommand(SpiritCommand::new, dispatcher, env);
      SNAPSHOT = registerCommand(SnapshotCommand::new, dispatcher, env);
      DIFFICULTY = registerCommand(DifficultyCommand::new, dispatcher, env);
      BOUNTY = registerCommand(BountyCommand::new, dispatcher, env);
      VAULT_ALTAR = registerCommand(VaultAltarCommand::new, dispatcher, env);
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
