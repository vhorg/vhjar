package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.PlayerReputationData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;

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
      builder.then(
         Commands.literal("set_rep")
            .then(
               Commands.argument("level", IntegerArgumentType.integer())
                  .then(Commands.argument("god", EnumArgument.enumArgument(VaultGod.class)).executes(this::setRep))
            )
      );
      builder.then(Commands.literal("set_level").then(Commands.argument("level", IntegerArgumentType.integer()).executes(this::setLevel)));
      builder.then(Commands.literal("add_skill_points").then(Commands.argument("amount", IntegerArgumentType.integer()).executes(this::addSkillPoints)));
      builder.then(
         Commands.literal("add_expertise_points")
            .then(
               ((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer())
                     .then(Commands.argument("player", EntityArgument.player()).executes(this::addExpertisePoints)))
                  .executes(this::addExpertisePoints)
            )
      );
      builder.then(Commands.literal("get").then(Commands.argument("player", EntityArgument.player()).executes(this::getPlayerStats)));
   }

   private int getPlayerStats(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = EntityArgument.getPlayer(context, "player");
      PlayerVaultStatsData data = PlayerVaultStatsData.get(player.getLevel());
      PlayerVaultStats vaultStats = data.getVaultStats(player);
      int vaultLevel = vaultStats.getVaultLevel();
      int exp = vaultStats.getExp();
      int spentSkillPoints = vaultStats.getTotalSpentSkillPoints();
      int spentKnowledgePoints = vaultStats.getTotalSpentKnowledgePoints();
      int spentExpertisePoints = vaultStats.getTotalSpentExpertisePoints();
      int unspentSkillPoints = vaultStats.getUnspentSkillPoints();
      int unspentKnowledgePoints = vaultStats.getUnspentKnowledgePoints();
      int unspentExpertisePoints = vaultStats.getUnspentExpertisePoints();
      List<TextComponent> messages = new ArrayList<>();
      messages.add(new TextComponent("======================================"));
      messages.add(new TextComponent(String.format("Player Stats: %s%s", ChatFormatting.DARK_AQUA, player.getDisplayName().getString())));
      messages.add(new TextComponent(String.format("Vault Level: %s%s", ChatFormatting.YELLOW, vaultLevel)));
      messages.add(new TextComponent(String.format("Vault Exp: %s%s", ChatFormatting.YELLOW, exp)));
      messages.add(
         new TextComponent(
            String.format(
               "Skill Points: %s%s%s used / %s%s%s available",
               ChatFormatting.YELLOW,
               spentSkillPoints,
               ChatFormatting.RESET,
               ChatFormatting.YELLOW,
               unspentSkillPoints,
               ChatFormatting.RESET
            )
         )
      );
      messages.add(
         new TextComponent(
            String.format(
               "Knowledge Points: %s%s%s used / %s%s%s available",
               ChatFormatting.AQUA,
               spentKnowledgePoints,
               ChatFormatting.RESET,
               ChatFormatting.AQUA,
               unspentKnowledgePoints,
               ChatFormatting.RESET
            )
         )
      );
      messages.add(
         new TextComponent(
            String.format(
               "Expertise Points: %s%s%s used / %s%s%s available",
               ChatFormatting.LIGHT_PURPLE,
               spentExpertisePoints,
               ChatFormatting.RESET,
               ChatFormatting.LIGHT_PURPLE,
               unspentExpertisePoints,
               ChatFormatting.RESET
            )
         )
      );
      messages.add(new TextComponent("======================================"));
      messages.forEach(message -> ((CommandSourceStack)context.getSource()).sendSuccess(message, true));
      return 0;
   }

   private int setLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      int level = IntegerArgumentType.getInteger(context, "level");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerPlayer player = source.getPlayerOrException();
      PlayerVaultStatsData.get(source.getLevel()).setVaultLevel(player, level);
      player.refreshTabListName();
      return 0;
   }

   private int setRep(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      VaultGod god = (VaultGod)context.getArgument("god", VaultGod.class);
      int level = IntegerArgumentType.getInteger(context, "level");
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      PlayerReputationData.addReputation(player.getUUID(), god, level);
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

      try {
         ServerPlayer player = EntityArgument.getPlayer(context, "player");
         PlayerVaultStatsData.get(source.getLevel()).addExpertisePoints(player, amount);
      } catch (IllegalArgumentException var5) {
         PlayerVaultStatsData.get(source.getLevel()).addExpertisePoints(source.getPlayerOrException(), amount);
      }

      return 0;
   }
}
