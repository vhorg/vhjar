package iskallia.vault.init;

import com.mojang.brigadier.CommandDispatcher;
import iskallia.vault.command.Command;
import iskallia.vault.command.HandCommand;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModClientCommands {
   public static HandCommand HAND;

   public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
      HAND = registerCommand(HandCommand::new, dispatcher);
   }

   public static <T extends Command> T registerCommand(Supplier<T> supplier, CommandDispatcher<CommandSourceStack> dispatcher) {
      T command = (T)supplier.get();
      if (!command.isDedicatedServerOnly()) {
         command.registerCommand(dispatcher);
      }

      return command;
   }
}
