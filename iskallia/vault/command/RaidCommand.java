package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.VaultRaidData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      for (RaidCommand.Type type : RaidCommand.Type.values()) {
         builder.then(
            Commands.func_197057_a(type.name().toUpperCase()).then(Commands.func_197057_a("start").executes(context -> this.startRaid(context, type)))
         );
      }
   }

   private int startRaid(CommandContext<CommandSource> context, RaidCommand.Type type) throws CommandSyntaxException {
      if (type == RaidCommand.Type.VAULT) {
         VaultRaidData.get(((CommandSource)context.getSource()).func_197023_e())
            .startNew(((CommandSource)context.getSource()).func_197035_h(), ModItems.VAULT_CRYSTAL_OMEGA, false);
      } else if (type == RaidCommand.Type.FINAL_VAULT) {
         VaultRaidData.get(((CommandSource)context.getSource()).func_197023_e())
            .startNew(((CommandSource)context.getSource()).func_197035_h(), ModItems.VAULT_CRYSTAL_OMEGA, true);
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }

   public static enum Type {
      VAULT,
      FINAL_VAULT;
   }
}
