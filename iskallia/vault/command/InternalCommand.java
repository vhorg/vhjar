package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.config.StreamerMultipliersConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.ItemGiftBomb;
import iskallia.vault.item.ItemRelicBoosterPack;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.EventTeamData;
import iskallia.vault.world.data.PlayerAliasData;
import iskallia.vault.world.data.PlayerBlackMarketData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.StreamData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

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
      builder.then(
         Commands.literal("received_sub")
            .then(
               Commands.argument("subscriber", StringArgumentType.word())
                  .then(
                     Commands.argument("months", IntegerArgumentType.integer(0))
                        .executes(
                           context -> this.receivedSub(
                              context, StringArgumentType.getString(context, "subscriber"), IntegerArgumentType.getInteger(context, "months"), 1
                           )
                        )
                  )
            )
      );
      builder.then(
         Commands.literal("received_sub")
            .then(
               Commands.argument("subscriber", StringArgumentType.word())
                  .then(
                     Commands.argument("months", IntegerArgumentType.integer())
                        .then(
                           Commands.argument("tier", IntegerArgumentType.integer())
                              .executes(
                                 context -> this.receivedSub(
                                    context,
                                    StringArgumentType.getString(context, "subscriber"),
                                    IntegerArgumentType.getInteger(context, "months"),
                                    IntegerArgumentType.getInteger(context, "tier")
                                 )
                              )
                        )
                  )
            )
      );
      builder.then(
         Commands.literal("received_sub_gift")
            .then(
               Commands.argument("gifter", StringArgumentType.word())
                  .then(
                     Commands.argument("amount", IntegerArgumentType.integer())
                        .then(
                           Commands.argument("tier", IntegerArgumentType.integer())
                              .executes(
                                 context -> this.receivedSubGift(
                                    context,
                                    StringArgumentType.getString(context, "gifter"),
                                    IntegerArgumentType.getInteger(context, "amount"),
                                    IntegerArgumentType.getInteger(context, "tier")
                                 )
                              )
                        )
                  )
            )
      );
      builder.then(
         Commands.literal("received_donation")
            .then(
               Commands.argument("donator", StringArgumentType.word())
                  .then(
                     Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(
                           context -> this.receivedDonation(
                              context, StringArgumentType.getString(context, "donator"), IntegerArgumentType.getInteger(context, "amount")
                           )
                        )
                  )
            )
      );
      builder.then(
         Commands.literal("received_bit_donation")
            .then(
               Commands.argument("donator", StringArgumentType.word())
                  .then(
                     Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(
                           context -> this.receivedBitDonation(
                              context, StringArgumentType.getString(context, "donator"), IntegerArgumentType.getInteger(context, "amount")
                           )
                        )
                  )
            )
      );
      builder.then(
         Commands.literal("received_trader_core_redemption")
            .then(
               Commands.argument("sender", StringArgumentType.word())
                  .executes(context -> this.receiveTraderCoreRedemption(context, StringArgumentType.getString(context, "sender")))
            )
      );
      builder.then(Commands.literal("booster_pack").executes(this::giveBoosterPack));
      builder.then(
         Commands.literal("team_modifyscore")
            .then(
               Commands.argument("team", StringArgumentType.string())
                  .then(Commands.argument("amount", IntegerArgumentType.integer()).executes(this::modifyScore))
            )
      );
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

   private int modifyScore(CommandContext<CommandSourceStack> context) {
      ServerLevel sWorld = ((CommandSourceStack)context.getSource()).getLevel();
      String team = StringArgumentType.getString(context, "team");
      int amount = IntegerArgumentType.getInteger(context, "amount");
      EventTeamData.get(sWorld).modifyScore(team, amount);
      return 0;
   }

   private int giveBoosterPack(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      if (ModConfigs.ARCHITECT_EVENT.isEnabled()) {
         MiscUtils.giveItem(player, ItemRelicBoosterPack.getArchitectBoosterPack());
      } else {
         MiscUtils.giveItem(player, new ItemStack(ModItems.RELIC_BOOSTER_PACK));
      }

      return 0;
   }

   private int receivedSub(CommandContext<CommandSourceStack> context, String name, int months, int tier) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      name = PlayerAliasData.applyAlias(player, name);
      StreamData.get(player.getLevel()).onSub(player.getServer(), player.getUUID(), name, months);
      PlayerVaultStatsData.get(player.getLevel()).addVaultExp(player, ModConfigs.STREAMER_EXP.getExpPerSub(player.getName().getString()));
      if (tier >= 3) {
         player.sendMessage(new TextComponent(name + " subscribed with Tier 3!").withStyle(ChatFormatting.GRAY), Util.NIL_UUID);
      }

      return 0;
   }

   private int receivedSubGift(CommandContext<CommandSourceStack> context, String gifter, int amount, int tier) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      String mcNick = player.getDisplayName().getString();
      gifter = PlayerAliasData.applyAlias(player, gifter);
      StreamerMultipliersConfig.StreamerMultipliers multipliers = ModConfigs.STREAMER_MULTIPLIERS.ofStreamer(mcNick);
      float multiplier = tier != 0 && tier != 1
         ? (tier == 2 ? multipliers.weightPerGiftedSubT2 : multipliers.weightPerGiftedSubT3)
         : multipliers.weightPerGiftedSubT1;
      StreamData.get(player.getLevel()).onDono(player.getServer(), player.getUUID(), gifter, (int)(amount * multiplier));
      this.handleGiftBombs(player, gifter, amount);
      return 0;
   }

   private int receivedDonation(CommandContext<CommandSourceStack> context, String donator, int amount) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      String mcNick = player.getDisplayName().getString();
      donator = PlayerAliasData.applyAlias(player, donator);
      int multiplier = ModConfigs.STREAMER_MULTIPLIERS.ofStreamer(mcNick).weightPerDonationUnit;
      StreamData.get(player.getLevel()).onDono(player.getServer(), player.getUUID(), donator, amount * multiplier);
      return 0;
   }

   private int receivedBitDonation(CommandContext<CommandSourceStack> context, String donator, int amount) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      String mcNick = player.getDisplayName().getString();
      donator = PlayerAliasData.applyAlias(player, donator);
      int multiplier = ModConfigs.STREAMER_MULTIPLIERS.ofStreamer(mcNick).weightPerHundredBits;
      StreamData.get(player.getLevel()).onDono(player.getServer(), player.getUUID(), donator, amount / 100 * multiplier);
      return 0;
   }

   private int receiveTraderCoreRedemption(CommandContext<CommandSourceStack> context, String claimer) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      claimer = PlayerAliasData.applyAlias(player, claimer);
      player.sendMessage(new TextComponent(claimer + " redeemed a Trader Core!").withStyle(ChatFormatting.GRAY), Util.NIL_UUID);
      return 0;
   }

   private void handleGiftBombs(ServerPlayer player, String gifter, int amount) {
      if (amount >= 5) {
         gifter = PlayerAliasData.applyAlias(player, gifter);
         ItemGiftBomb.Variant variant = amount <= 9
            ? ItemGiftBomb.Variant.NORMAL
            : (amount <= 19 ? ItemGiftBomb.Variant.SUPER : (amount <= 49 ? ItemGiftBomb.Variant.MEGA : ItemGiftBomb.Variant.OMEGA));
         Vec3 position = player.position();
         player.getLevel()
            .playSound(
               null,
               position.x,
               position.y,
               position.z,
               variant != ItemGiftBomb.Variant.NORMAL && variant != ItemGiftBomb.Variant.SUPER
                  ? ModSounds.MEGA_GIFT_BOMB_GAIN_SFX
                  : ModSounds.GIFT_BOMB_GAIN_SFX,
               SoundSource.PLAYERS,
               0.75F,
               1.0F
            );
         ItemStack giftBomb = ItemGiftBomb.forGift(variant, gifter, amount);
         EntityHelper.giveItem(player, giftBomb);
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
