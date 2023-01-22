package iskallia.vault.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;

public class SpiritCommand extends Command {
   private static final String PLAYER_IGN_ARGUMENT = "playerIGN";
   private static final String VAULT_LEVEL_ARGUMENT = "vaultLevel";

   @Override
   public String getName() {
      return "spirit";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.literal("summon")
            .then(
               Commands.argument("playerIGN", StringArgumentType.word())
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("vaultLevel", IntegerArgumentType.integer())
                           .executes(
                              ctx -> this.summonSpirit(
                                 ctx, StringArgumentType.getString(ctx, "playerIGN"), IntegerArgumentType.getInteger(ctx, "vaultLevel"), false
                              )
                           ))
                        .then(
                           Commands.argument("addCurrentPlayersItems", BoolArgumentType.bool())
                              .executes(
                                 ctx -> this.summonSpirit(
                                    ctx,
                                    StringArgumentType.getString(ctx, "playerIGN"),
                                    IntegerArgumentType.getInteger(ctx, "vaultLevel"),
                                    BoolArgumentType.getBool(ctx, "addCurrentPlayersItems")
                                 )
                              )
                        )
                  )
            )
      );
   }

   private int summonSpirit(CommandContext<CommandSourceStack> context, String playerIGN, int vaultLevel, boolean addCurrentPlayersItems) throws CommandSyntaxException {
      ServerPlayer serverPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      Entity entity = source.getEntity();
      if (entity != null) {
         ServerLevel serverLevel = source.getLevel();
         serverLevel.getServer().getProfileCache().get(playerIGN).ifPresentOrElse(gp -> {
            SpiritEntity spirit = (SpiritEntity)ModEntities.SPIRIT.spawn(serverLevel, null, null, entity.getOnPos(), MobSpawnType.MOB_SUMMONED, true, false);
            if (spirit != null) {
               spirit.setGameProfile(gp);
               spirit.setVaultLevel(vaultLevel);
               spirit.setPlayerLevel(vaultLevel);
               ServerVaults.get(serverLevel).ifPresent(vault -> {
                  Listener listener = vault.get(Vault.LISTENERS).get(serverPlayer.getUUID());
                  if (listener != null) {
                     spirit.setJoinState(listener.get(Listener.JOIN_STATE));
                  }
               });
               if (addCurrentPlayersItems) {
                  spirit.addPlayersItems(serverPlayer);
               }
            }
         }, () -> serverPlayer.sendMessage(new TextComponent("Unable to find player's IGN: " + playerIGN).withStyle(ChatFormatting.RED), Util.NIL_UUID));
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
