package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerDifficultyCommand extends Command {
   private static final String COMMANDS_PREFIX = "commands.";
   private static final Dynamic2CommandExceptionType ERROR_ALREADY_DIFFICULT = new Dynamic2CommandExceptionType(
      (playerName, vaultDifficulty) -> new TranslatableComponent("commands.the_vault.player_difficulty.failure", new Object[]{playerName, vaultDifficulty})
   );
   private static final DynamicCommandExceptionType ERROR_DIFFERENT_PLAYER = new DynamicCommandExceptionType(
      playerName -> new TranslatableComponent("commands.the_vault.player_difficulty.failure.different_player", new Object[]{playerName})
   );

   @Override
   public String getName() {
      return "local_difficulty";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      RequiredArgumentBuilder<CommandSourceStack, EntitySelector> argument = Commands.argument("player", EntityArgument.player());

      for (VaultDifficulty vaultDifficulty : VaultDifficulty.values()) {
         argument.then(
            ((LiteralArgumentBuilder)Commands.literal(vaultDifficulty.getKey())
                  .requires(s -> s.hasPermission(s.getLevel().getGameRules().getBoolean(ModGameRules.NO_OP_DIFFICULTY) ? 0 : 2)))
               .executes(ctx -> setDifficulty((CommandSourceStack)ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), vaultDifficulty))
         );
      }

      argument.executes(
         ctx -> {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            VaultDifficulty vaultDifficultyx = WorldSettings.get(((CommandSourceStack)ctx.getSource()).getLevel()).getPlayerDifficulty(player.getUUID());
            ((CommandSourceStack)ctx.getSource())
               .sendSuccess(
                  new TranslatableComponent("commands.the_vault.player_difficulty.query", new Object[]{player.getName(), vaultDifficultyx.getDisplayName()}),
                  false
               );
            return vaultDifficultyx.getId();
         }
      );
      builder.then(argument);
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }

   public static int setDifficulty(CommandSourceStack source, Player player, VaultDifficulty vaultDifficulty) throws CommandSyntaxException {
      ServerPlayer sender = source.getPlayerOrException();
      WorldSettings worldSettings = WorldSettings.get(source.getLevel());
      if (!sender.hasPermissions(4) && !sender.getUUID().equals(player.getUUID())) {
         throw ERROR_DIFFERENT_PLAYER.create(player.getName());
      } else if (worldSettings.getPlayerDifficulty(player.getUUID()) == vaultDifficulty) {
         throw ERROR_ALREADY_DIFFICULT.create(player.getName(), vaultDifficulty);
      } else {
         worldSettings.setPlayerDifficulty(player.getUUID(), vaultDifficulty);
         source.sendSuccess(
            new TranslatableComponent("commands.the_vault.player_difficulty.success", new Object[]{player.getName(), vaultDifficulty.getDisplayName()}), true
         );
         return 0;
      }
   }
}
