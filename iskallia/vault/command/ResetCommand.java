package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ResetCommand extends Command {
   @Override
   public String getName() {
      return "reset";
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
      builder.then(Commands.literal("all").executes(this::resetAll));
      builder.then(Commands.literal("level_abilities_talents_and_expertises").executes(this::resetLevelAbilitiesTalentsAndExpertises));
      builder.then(Commands.literal("level").executes(this::resetLevel));
      builder.then(Commands.literal("abilities_and_talents").executes(this::resetAbilitiesAndTalents));
      builder.then(Commands.literal("expertises").executes(this::resetExpertises));
      builder.then(Commands.literal("research").executes(this::resetResearch));
   }

   private int resetAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).resetLevelAbilitiesAndExpertise(source.getPlayerOrException());
      PlayerResearchesData.get(source.getLevel()).resetResearchTree(source.getPlayerOrException());
      PlayerVaultStatsData.get(source.getLevel()).resetKnowledge(source.getPlayerOrException());
      return 0;
   }

   private int resetLevelAbilitiesTalentsAndExpertises(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).resetLevelAbilitiesAndExpertise(source.getPlayerOrException());
      return 0;
   }

   private int resetLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).resetLevel(source.getPlayerOrException());
      return 0;
   }

   private int resetAbilitiesAndTalents(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).resetAbilities(source.getPlayerOrException());
      return 0;
   }

   private int resetExpertises(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerVaultStatsData.get(source.getLevel()).resetExpertises(source.getPlayerOrException());
      return 0;
   }

   private int resetResearch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      PlayerResearchesData.get(source.getLevel()).resetResearchTree(source.getPlayerOrException());
      PlayerVaultStatsData.get(source.getLevel()).resetKnowledge(source.getPlayerOrException());
      return 0;
   }
}
