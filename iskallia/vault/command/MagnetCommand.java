package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.LegacyMagnetItem;
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
         Commands.argument("perk", EnumArgument.enumArgument(LegacyMagnetItem.Perk.class))
            .executes(c -> this.give(c, (LegacyMagnetItem.Perk)c.getArgument("perk", LegacyMagnetItem.Perk.class)))
      );
   }

   private int give(CommandContext<CommandSourceStack> c, LegacyMagnetItem.Perk perk) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)c.getSource()).getPlayerOrException();
      ServerLevel world = player.getLevel();
      ItemStack stack = ModItems.MAGNET.getDefaultInstance();
      LegacyMagnetItem.setPerk(stack, perk, ModConfigs.MAGNET_CONFIG.getPerkUpgrade(perk).getYield(rand));
      EntityHelper.giveItem(player, stack);
      return 0;
   }
}
