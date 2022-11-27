package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.util.EntityHelper;
import java.util.Random;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.command.EnumArgument;

public class MagnetCommand extends Command {
   private static final Random rand = new Random();

   @Override
   public String getName() {
      return "magnet";
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
         Commands.argument("perk", EnumArgument.enumArgument(MagnetItem.Perk.class))
            .executes(c -> this.give(c, (MagnetItem.Perk)c.getArgument("perk", MagnetItem.Perk.class)))
      );
   }

   private int give(CommandContext<CommandSourceStack> c, MagnetItem.Perk perk) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)c.getSource()).getPlayerOrException();
      ServerLevel world = player.getLevel();
      ItemStack stack = ModItems.MAGNET_ITEM.getDefaultInstance();
      MagnetItem.setPerk(stack, perk, ModConfigs.MAGNET_CONFIG.getPerkUpgrade(perk).getYield(rand));
      EntityHelper.giveItem(player, stack);
      return 0;
   }
}
