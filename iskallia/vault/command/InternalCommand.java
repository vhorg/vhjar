package iskallia.vault.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.type.Research;
import iskallia.vault.world.data.PlayerBlackMarketData;
import iskallia.vault.world.data.PlayerResearchesData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class InternalCommand extends Command {
   @Override
   public String getName() {
      return "internal";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("reset_black_market").then(Commands.argument("player", EntityArgument.player()).executes(this::resetBlackMarket)));
      builder.then(Commands.literal("reset_black_markets").executes(this::resetBlackMarkets));
      builder.then(
         Commands.literal("remove_research")
            .then(
               Commands.argument("player", EntityArgument.player())
                  .then(Commands.argument("research", StringArgumentType.string()).executes(this::unlearnResearch))
            )
      );
   }

   private int unlearnResearch(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
      Research research = ModConfigs.RESEARCHES.getByName(StringArgumentType.getString(ctx, "research"));
      if (research == null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(new TextComponent("Unknown research"));
         return 0;
      } else {
         PlayerResearchesData.get(target.getLevel()).removeResearch(target, research);
         return 0;
      }
   }

   private int resetBlackMarket(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
      PlayerBlackMarketData.get(((CommandSourceStack)ctx.getSource()).getServer()).getBlackMarket(target).resetTrades();
      return 0;
   }

   private int resetBlackMarkets(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      PlayerBlackMarketData.get(((CommandSourceStack)ctx.getSource()).getServer()).getPlayerMap().forEach((uuid, blackMarket) -> blackMarket.resetTrades());
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
