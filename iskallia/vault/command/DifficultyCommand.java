package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class DifficultyCommand extends Command {
   private static final String COMMANDS_PREFIX = "commands.";
   private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType(
      vaultDifficulty -> new TranslatableComponent("commands.the_vault.difficulty.failure", new Object[]{vaultDifficulty})
   );

   @Override
   public String getName() {
      return "difficulty";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      for (VaultDifficulty vaultDifficulty : VaultDifficulty.values()) {
         builder.then(
            ((LiteralArgumentBuilder)Commands.literal(vaultDifficulty.getKey()).requires(s -> s.hasPermission(2)))
               .executes(ctx -> setDifficulty((CommandSourceStack)ctx.getSource(), vaultDifficulty))
         );
      }

      builder.executes(
         ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
            VaultDifficulty vaultDifficultyx = WorldSettings.get(player.getLevel()).getVaultDifficulty();
            ((CommandSourceStack)ctx.getSource())
               .sendSuccess(new TranslatableComponent("commands.the_vault.difficulty.query", new Object[]{vaultDifficultyx.getDisplayName()}), false);
            return vaultDifficultyx.getId();
         }
      );
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }

   public static int setDifficulty(CommandSourceStack source, VaultDifficulty vaultDifficulty) throws CommandSyntaxException {
      WorldSettings worldSettings = WorldSettings.get(source.getPlayerOrException().getLevel());
      if (worldSettings.getVaultDifficulty() == vaultDifficulty) {
         throw ERROR_ALREADY_DIFFICULT.create(vaultDifficulty.getKey());
      } else {
         worldSettings.setVaultDifficulty(vaultDifficulty);
         source.sendSuccess(new TranslatableComponent("commands.the_vault.difficulty.success", new Object[]{vaultDifficulty.getDisplayName()}), true);
         return 0;
      }
   }
}
