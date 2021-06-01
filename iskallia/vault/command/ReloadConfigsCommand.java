package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import iskallia.vault.init.ModConfigs;
import net.minecraft.command.CommandSource;

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
         return 0;
      } catch (Exception var3) {
         var3.printStackTrace();
         throw var3;
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
