package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.item.HourglassBlockItem;
import iskallia.vault.init.ModBlocks;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.command.EnumArgument;

public class SandEventCommand extends Command {
   @Override
   public String getName() {
      return "sand_event";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.literal("add_sand")
            .then(
               Commands.argument("contributor", StringArgumentType.word())
                  .then(
                     Commands.argument("amount", IntegerArgumentType.integer())
                        .then(Commands.argument("color", EnumArgument.enumArgument(ChatFormatting.class)).executes(this::onSandAdd))
                  )
            )
      );
      builder.then(
         Commands.literal("make_hourglass")
            .then(
               Commands.argument("uuid", UuidArgument.uuid()).then(Commands.argument("playerName", StringArgumentType.word()).executes(this::createHourglass))
            )
      );
   }

   private int createHourglass(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      UUID playerUUID = UuidArgument.getUuid(ctx, "uuid");
      String playerName = StringArgumentType.getString(ctx, "playerName");
      ItemStack hourglass = new ItemStack(ModBlocks.HOURGLASS);
      HourglassBlockItem.addHourglassOwner(hourglass, playerUUID, playerName);
      player.addItem(hourglass);
      return 0;
   }

   private int onSandAdd(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      String contributor = StringArgumentType.getString(ctx, "contributor");
      int amount = IntegerArgumentType.getInteger(ctx, "amount");
      ChatFormatting color = (ChatFormatting)ctx.getArgument("color", ChatFormatting.class);
      ServerLevel sWorld = player.getLevel();
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
