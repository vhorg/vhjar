package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.server.ServerWorld;

public class DebugCommand extends Command {
   @Override
   public String getName() {
      return "debug";
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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(Commands.func_197057_a("vault_kick").then(Commands.func_197056_a("player", EntityArgument.func_197096_c()).executes(this::kickFromVault)));
   }

   private int kickFromVault(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity player = EntityArgument.func_197089_d(context, "player");
      ServerWorld world = player.func_71121_q();
      VaultRaid vault = VaultRaidData.get(world).getActiveFor(player);
      if (vault == null) {
         ((CommandSource)context.getSource()).func_197030_a(new StringTextComponent(player.func_200200_C_().getString() + " is not in a vault!"), true);
         return 0;
      } else {
         if (vault.getPlayers().size() > 1) {
            vault.getPlayer(player).ifPresent(vPlayer -> {
               VaultRaid.RUNNER_TO_SPECTATOR.execute(vault, vPlayer, world);
               VaultRaid.HIDE_OVERLAY.execute(vault, vPlayer, world);
            });
         } else {
            vault.getPlayer(player)
               .ifPresent(
                  vPlayer -> VaultRaid.REMOVE_SCAVENGER_ITEMS
                     .then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS)
                     .then(VaultRaid.EXIT_SAFELY)
                     .execute(vault, vPlayer, world)
               );
         }

         IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
         playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
         StringTextComponent suffix = new StringTextComponent(" bailed.");
         MiscUtils.broadcast(new StringTextComponent("").func_230529_a_(playerName).func_230529_a_(suffix));
         return 0;
      }
   }
}
