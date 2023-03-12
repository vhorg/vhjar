package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.ArenaRaidData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.builder.RaidCommandVaultBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class RaidCommand extends Command {
   @Override
   public String getName() {
      return "raid";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      for (RaidCommand.Type type : RaidCommand.Type.values()) {
         builder.then(
            Commands.literal(type.name().toUpperCase())
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("start")
                        .then(Commands.argument("size", IntegerArgumentType.integer(1, 200)).executes(context -> this.startRaid(context, type))))
                     .executes(context -> this.startRaid(context, type))
               )
         );
      }
   }

   private int startRaid(CommandContext<CommandSourceStack> context, RaidCommand.Type type) throws CommandSyntaxException {
      if (type == RaidCommand.Type.VAULT) {
         ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
         VaultRaid.Builder vault = RaidCommandVaultBuilder.get().initializeBuilder(player.getLevel(), player, null);
         VaultRaidData.get(((CommandSourceStack)context.getSource()).getLevel()).startVault(((CommandSourceStack)context.getSource()).getLevel(), vault);
      } else if (type == RaidCommand.Type.ARENA) {
         ArenaRaidData.get(((CommandSourceStack)context.getSource()).getLevel()).startNew(((CommandSourceStack)context.getSource()).getPlayerOrException());
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }

   public static enum Type {
      VAULT,
      FINAL_VAULT,
      ARENA;
   }
}
