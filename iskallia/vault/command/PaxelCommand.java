package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.item.paxel.PaxelItem;
import java.util.Random;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.command.EnumArgument;

public class PaxelCommand extends Command {
   private static final Random rand = new Random();

   @Override
   public String getName() {
      return "paxel";
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
      builder.then(
         Commands.argument("perk", EnumArgument.enumArgument(PaxelItem.Perk.class))
            .executes(c -> this.give(c, (PaxelItem.Perk)c.getArgument("perk", PaxelItem.Perk.class)))
      );
   }

   private int give(CommandContext<CommandSourceStack> c, PaxelItem.Perk perk) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)c.getSource()).getPlayerOrException();
      ServerLevel world = player.getLevel();
      ItemStack i = player.getMainHandItem();
      if (i.getItem() instanceof PaxelItem) {
         PaxelItem.addPerk(i, perk);
      }

      return 0;
   }
}
