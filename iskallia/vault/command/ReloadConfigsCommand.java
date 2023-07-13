package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.InvalidConfigsMessage;
import iskallia.vault.network.message.OmegaShardGlobalTradeMessage;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class ReloadConfigsCommand extends Command {
   @Override
   public String getName() {
      return "reloadcfg";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      ((LiteralArgumentBuilder)builder.then(Commands.literal("gen").executes(this::reloadGenConfigs))).executes(this::reloadConfigs);
   }

   private int reloadGenConfigs(CommandContext<CommandSourceStack> context) {
      try {
         ModConfigs.registerGen();
      } catch (Exception var3) {
         var3.printStackTrace();
         throw var3;
      }

      ((CommandSourceStack)context.getSource()).sendSuccess(new TextComponent("Gen Configs reloaded!").withStyle(ChatFormatting.GREEN), true);
      return 0;
   }

   private int reloadConfigs(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      try {
         ModConfigs.register();
      } catch (Exception var4) {
         var4.printStackTrace();
         throw var4;
      }

      if (!ModConfigs.INVALID_CONFIGS.isEmpty()) {
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ShardGlobalTradeMessage(ModConfigs.SOUL_SHARD.getTrades()));
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new OmegaShardGlobalTradeMessage(ModConfigs.OMEGA_SOUL_SHARD.getTrades()));
         ((CommandSourceStack)context.getSource()).sendSuccess(new TextComponent("Configs reloaded, with errors!").withStyle(ChatFormatting.RED), true);

         try {
            ModNetwork.CHANNEL
               .sendTo(
                  new InvalidConfigsMessage(ModConfigs.INVALID_CONFIGS),
                  ((CommandSourceStack)context.getSource()).getPlayerOrException().connection.connection,
                  NetworkDirection.PLAY_TO_CLIENT
               );
         } catch (CommandSyntaxException var3) {
            var3.printStackTrace();
            throw var3;
         }
      } else {
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ShardGlobalTradeMessage(ModConfigs.SOUL_SHARD.getTrades()));
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new OmegaShardGlobalTradeMessage(ModConfigs.OMEGA_SOUL_SHARD.getTrades()));
         ((CommandSourceStack)context.getSource()).sendSuccess(new TextComponent("Configs reloaded!").withStyle(ChatFormatting.GREEN), true);
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
