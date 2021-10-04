package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;

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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.executes(this::reloadConfigs);
   }

   private int reloadConfigs(CommandContext<CommandSource> context) {
      try {
         ModConfigs.register();
      } catch (Exception var3) {
         var3.printStackTrace();
         throw var3;
      }

      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ShardGlobalTradeMessage(ModConfigs.SOUL_SHARD.getShardTrades()));
      ((CommandSource)context.getSource()).func_197030_a(new StringTextComponent("Configs reloaded!").func_240699_a_(TextFormatting.GREEN), true);
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
