package iskallia.vault.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import iskallia.vault.init.ModConfigs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ClickEvent.Action;

public class PointsResetCommand extends Command {
   private static final Map<String, CommandSourceStack> currentlyRunning = new HashMap<>();

   @Override
   public String getName() {
      return "points_reset";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.argument("type", StringArgumentType.word())
            .suggests((context, suggestionsBuilder) -> SharedSuggestionProvider.suggest(List.of("skill", "knowledge", "archetype"), suggestionsBuilder))
            .executes(this::resetPoints)
      );
   }

   private int resetPoints(CommandContext<CommandSourceStack> context) {
      String type = StringArgumentType.getString(context, "type");
      if (!currentlyRunning.containsKey(type)) {
         currentlyRunning.put(type, (CommandSourceStack)context.getSource());
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               new TextComponent(
                     "WARNING: THIS CANNOT BE UNDONE! If you are absolutely sure you want to reset "
                        + type.toUpperCase()
                        + " points, click here or run the command again.."
                  )
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withBold(true).withClickEvent(new ClickEvent(Action.RUN_COMMAND, context.getInput()))),
               true
            );
         return 0;
      } else {
         try {
            switch (type) {
               case "skill":
                  ModConfigs.PLAYER_RESETS.enableResetSkillPoints();
                  break;
               case "knowledge":
                  ModConfigs.PLAYER_RESETS.enableResetKnowledgePoints();
                  break;
               case "archetype":
                  ModConfigs.PLAYER_RESETS.enableResetArchetypePoints();
                  break;
               default:
                  ((CommandSourceStack)context.getSource()).sendFailure(new TextComponent("Invalid Points Type: " + type));
                  currentlyRunning.remove(type);
                  return 0;
            }
         } catch (Exception var5) {
            ((CommandSourceStack)context.getSource()).sendFailure(new TextComponent(var5.getMessage()));
            currentlyRunning.remove(type);
            return 0;
         }

         ((CommandSourceStack)context.getSource()).sendSuccess(new TextComponent("You have reset " + type + " points").withStyle(ChatFormatting.GREEN), true);
         currentlyRunning.remove(type);
         return 1;
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
